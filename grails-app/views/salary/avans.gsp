<html>
  <head>
    <title>Prisma: Зарплата - Авансовая ведомость за ${String.format('%tB %<tY',new Date(avans.year-1900,avans.month-1,1))}</title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['repdate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата предполагаемой выплаты"])}</li>'; $('repdate').up('span').addClassName('k-error-colored'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.denial.message",args:["закрыть ведомость"])}</li>'; break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else {
          location.reload(true);
        }
      }
      function processprepaymentupdateResponse(e){
        var sErrorMsg = '';
        ['prepayment_prepayment'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Сумма аванса"])}</li>'; $('prepayment_prepayment').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorprepaymentlist").innerHTML=sErrorMsg;
          $("errorprepaymentlist").up('div').show();
        } else
          jQuery('#prepaymentupdateForm').slideUp(300, function() { getReport(); getSumma(); });
      }
      function submitForm(iStatus){
        $('modstatus').value = iStatus
        $('submit_button').click();
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
        $('avansreport_submit_button').click();
      }
      function getSumma(){
        <g:remoteFunction controller='salary' action='recievesumma' id="${avans.id}" onSuccess="\$('summa').value=e.responseText;"/>
      }
      function payprepayment(sId,sType){
        <g:remoteFunction controller='salary' action='payprepayment' id="${avans.id}" params="'sal_id='+sId+'&type='+sType" onSuccess="getReport();"/>
      }
      function printreport(){
        window.open('${createLink(action:'printreport',id:avans.id)}');
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
      .tabs a:hover,.tabs a.active,.tabs a.active i{color:#000}
    </style>
  </head>
  <body onload="getReport()">
    <h3 class="fleft">Авансовая ведомость за ${String.format('%tB %<tY',new Date(avans.year-1900,avans.month-1,1))}</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку авансовых ведомостей</a>
    <div class="clear"></div>
    <g:formRemote name="cashrequestDetailForm" url="${[action:'updateavans',id:avans.id]}" method="post" onSuccess="processResponse(e)">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="summa" disabled>Сумма:</label>
      <input type="text" id="summa" name="summa" value="${number(value:avans.summa)}" disabled/>
      <label for="avans_modstatus" disabled>Статус:</label>
      <input type="text" id="avans_modstatus" disabled value="${avans.modstatus==1?'К выплате':avans.modstatus==2?'Закрыта':'Новая'}" />
      <label for="repdate">Дата предполагаемой выплаты:</label>
      <g:datepicker class="normal nopad" name="repdate" value="${String.format('%td.%<tm.%<tY',avans.repdate)}"/>

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
      <g:if test="${avans.file}">
        <a class="button" href="${createLink(controller:'salary', action:'showscan',id:avans.file,params:[code:Tools.generateModeParam(avans.file)])}" target="_blank">
          Просмотреть скан ведомости &nbsp;<i class="icon-angle-right icon-large"></i>
        </a>
      </g:if>
        <input type="button" class="spacing" value="Печать" onclick="printreport()" />
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
      <g:if test="${avans.modstatus<2}">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(0)"/>
      </g:if><g:if test="${avans.modstatus==0}">
        <input type="button" class="spacing" value="В оплату" onclick="submitForm(1)"/>
      </g:if><g:if test="${iscanclose&&iscanedit}">
        <input type="button" class="spacing" value="Закрыть" onclick="submitForm(2)"/>
      </g:if>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      </g:if>
      </div>
      <input type="hidden" id="modstatus" name="modstatus" value="0"/>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${iscanedit}">
    <g:form class="fright" style="margin-top:-125px;padding-right:60px" name="salreportscanForm" url="${[action:'addsalreportscan',id:avans.id]}" method="post" enctype="multipart/form-data" target="upload_target">
      <label for="file">Новый скан:</label>
      <input type="file" id="file" name="file" style="width:240px" onchange="$('salreportscanForm').submit()"/>
    </g:form>
  </g:if>
  <g:if test="${avans}">
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
    <g:formRemote name="avansreportForm" url="[action:'avansreport',id:avans.id]" update="details">
      <input type="submit" class="button" id="avansreport_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <iframe id="upload_target" name="upload_target" style="display:none"></iframe>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
