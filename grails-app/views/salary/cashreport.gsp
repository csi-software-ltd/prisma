<html>
  <head>
    <title>Prisma: Зарплата - Наличная ведомость ${!cashreport.department_id?'директоров ':''}за ${String.format('%tB %<tY',new Date(cashreport.year-1900,cashreport.month-1,1))}</title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function computeOverloadSumma(iSumma){
        if (parseInt(parseFloat($('cashpayment_overloadhour').value)*2)>0)
          $('cashpayment_overloadsumma').value = Math.round(parseInt(parseFloat($('cashpayment_overloadhour').value)*2)*iSumma/${monthworkdays})
        else $('cashpayment_overloadsumma').value = 0;
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['commentdep'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['repdate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата предполагаемой выплаты"])}</li>'; $('repdate').up('span').addClassName('k-error-colored'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.denial.message",args:["закрыть ведомость"])}</li>'; break;
              case 3: sErrorMsg+='<li>${message(code:"error.denial.message",args:["передать в оплату"])}</li>'; break;
              case 4: sErrorMsg+='<li>${message(code:"error.denial.message",args:["подтвердить"])}</li>'; break;
              case 5: sErrorMsg+='<li>${message(code:"error.denial.message",args:["отказать"])}</li>'; break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Комментарий кассира отдела"])}</li>'; $('commentdep').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else {
          location.reload(true);
        }
      }
      function processcashpaymentupdateResponse(e){
        var sErrorMsg = '';
        ['cashpayment_actsalary','cashpayment_bonus','cashpayment_shtraf','cashpayment_overloadhour','cashpayment_overloadsumma','cashpayment_holiday','cashpayment_precashpayment','cashpayment_prevfix'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Факт. оклад"])}</li>'; $('cashpayment_actsalary').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Бонус"])}</li>'; $('cashpayment_bonus').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Штраф"])}</li>'; $('cashpayment_shtraf').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Переработка(час)"])}</li>'; $('cashpayment_overloadhour').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Переработка(руб)"])}</li>'; $('cashpayment_overloadsumma').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Отпускные"])}</li>'; $('cashpayment_holiday').addClassName('red'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Оплата до срока"])}</li>'; $('cashpayment_precashpayment').addClassName('red'); break;
              case 8: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Корректировка"])}</li>'; $('cashpayment_prevfix').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorcashpaymentlist").innerHTML=sErrorMsg;
          $("errorcashpaymentlist").up('div').show();
        } else
          jQuery('#cashpaymentupdateForm').slideUp(300, function() { getReport(); getSumma(); });
      }
      function submitForm(iStatus){
        $('modstatus').value = iStatus
        $('submit_button').click();
      }
      function setConfirm(iStatus){
        $('is_confirm').value = iStatus
      }
      function viewCell(iNum){
        var tabs = jQuery('.nav').find('li');
        for(var i=0; i<tabs.length; i++){
          if(i==iNum)
            tabs[i].addClassName('selected');
          else
            tabs[i].removeClassName('selected');
        }

        switch(iNum){
          case 0: getReport();break;
        }
      }
      function getReport(){
        $('cashsalary_submit_button').click();
      }
      function getSumma(){
        <g:remoteFunction controller='salary' action='recievesumma' id="${cashreport.id}" onSuccess="\$('summa').value=e.responseText;"/>
      }
      function paycashpayment(sId,sType){
        <g:remoteFunction controller='salary' action='paycashpayment' id="${cashreport.id}" params="'sal_id='+sId+'&type='+sType" onSuccess="getReport();"/>
      }
      function printreport(){
        window.open('${createLink(action:'printreport',id:cashreport.id)}');
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
      .tabs a:hover,.tabs a.active,.tabs a.active i{color:#000}
    </style>
  </head>
  <body onload="getReport();">
    <h3 class="fleft">Наличная ведомость ${!cashreport.department_id?'директоров ':''}за ${String.format('%tB %<tY',new Date(cashreport.year-1900,cashreport.month-1,1))}</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку наличных ведомостей</a>
    <div class="clear"></div>
    <g:formRemote name="cashrequestDetailForm" url="${[action:'updatecashreport',id:cashreport.id]}" method="post" onSuccess="processResponse(e)">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="summa" disabled>Сумма:</label>
      <input type="text" id="summa" name="summa" value="${number(value:cashreport.summa)}" disabled/>
      <label for="cashreport_modstatus" disabled>Статус:</label>
      <input type="text" id="cashreport_modstatus" disabled value="${cashreport.modstatus==1?'К выплате':cashreport.modstatus==2?'Закрыта':'Новая'}" />
    <g:if test="${cashreport.department_id}">
      <label for="department" disabled>Отдел:</label>
      <input type="text" class="fullline" id="department" name="department" value="${Department.get(cashreport.department_id)?.name}" disabled/>
    </g:if>
      <label for="repdate">Дата предполагаемой выплаты:</label>
      <g:datepicker class="normal nopad" name="repdate" value="${String.format('%td.%<tm.%<tY',cashreport.repdate)}"/><br/>
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" id="comment" disabled="${!iscanapprove?'true':'false'}" value="${cashreport.comment}" />
      <label for="commentdep">Комментарий кассира отдела:</label>
      <g:textArea name="commentdep" id="commentdep" disabled="${iscanapprove?'true':'false'}" value="${cashreport.commentdep}" />

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
      <g:if test="${cashreport.file}">
        <a class="button" href="${createLink(controller:'salary', action:'showscan',id:cashreport.file,params:[code:Tools.generateModeParam(cashreport.file)])}" target="_blank">
          Просмотреть скан ведомости &nbsp;<i class="icon-angle-right icon-large"></i>
        </a>
      </g:if>
        <input type="button" class="spacing" value="Печать" onclick="printreport()" />
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit||iscanapprove}">
      <g:if test="${cashreport.modstatus<2}">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(0)"/>
      </g:if><g:if test="${cashreport.modstatus==0&&cashreport.is_confirm==1&&iscanedit}">
        <input type="button" class="spacing" value="В оплату" onclick="submitForm(1)"/>
      </g:if><g:if test="${cashreport.modstatus==0&&cashreport.is_confirm in [-2,0]}">
        <input type="button" class="spacing" value="На согласование" onclick="setConfirm(-1);submitForm(0)"/>
      </g:if><g:if test="${cashreport.modstatus in [0,2]&&cashreport.is_confirm==-1&&iscanapprove}">
        <input type="button" class="spacing" value="Подтвердить" onclick="setConfirm(1);submitForm(0)"/>
      </g:if><g:if test="${cashreport.modstatus in [0,2]&&cashreport.is_confirm==-1&&iscanapprove}">
        <input type="button" class="spacing" value="Отказать" onclick="setConfirm(-2);submitForm(0)"/>
      </g:if><g:if test="${iscanclose}">
        <input type="button" class="spacing" value="Закрыть" onclick="submitForm(2)"/>
      </g:if>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      </g:if>
      </div>
      <input type="hidden" id="modstatus" name="modstatus" value="0"/>
      <input type="hidden" id="is_confirm" name="is_confirm" value="${cashreport.is_confirm}"/>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${iscanedit}">
    <g:form class="fright" style="margin-top:-348px;padding-right:60px" name="salreportscanForm" url="${[action:'addsalreportscan',id:cashreport.id]}" method="post" enctype="multipart/form-data" target="upload_target">
      <label for="file">Новый скан:</label>
      <input type="file" id="file" name="file" style="width:240px" onchange="$('salreportscanForm').submit()"/>
    </g:form>
  </g:if>
  <g:if test="${cashreport}">
    <div class="tabs">
      <ul class="nav">
        <li class="selected"><a href="javascript:void(0)" onclick="viewCell(0)">Ведомость</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="cashsalaryForm" url="[action:'cashsalary',id:cashreport.id]" update="details">
      <input type="submit" class="button" id="cashsalary_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <iframe id="upload_target" name="upload_target" style="display:none"></iframe>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
