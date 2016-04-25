<html>
  <head>
    <title>Prisma: <g:if test="${license}">Лицензионный договор №${license.anumber}</g:if><g:else>Новый лицензионный договор</g:else></title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function deleteAgr(){
        if(confirm('Вы уверены, что хотите удалить лицензионный договор №${license?.anumber}?')) { submitForm(-1) }
      }
      function newCompany(){
        window.open('${createLink(controller:'company',action:'detail')}');
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['company_id','sro','industry_id','anumber'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['adate','enddate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
      <g:if test="${iscanaddcompanies}">
        ['sro'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('red');
        });
      </g:if>
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Лицензиат"])}</li>'; $('company_id').addClassName('red'); break;
            <g:if test="${iscanaddcompanies}">
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Лицензиар"])}</li>'; $('sro').up('span').addClassName('red'); break;
              case 8: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Лицензиар"])}</li>'; $('sro').up('span').addClassName('red'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Лицензиара","Лицензиар"])}</li>'; $('sro').up('span').addClassName('red'); break;
            </g:if><g:else>
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Лицензиар"])}</li>'; $('sro').addClassName('red'); break;
              case 8: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Лицензиар"])}</li>'; $('sro').addClassName('red'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Лицензиара","Лицензиар"])}</li>'; $('sro').addClassName('red'); break;
            </g:else>
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Направление"])}</li>'; $('industry_id').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Номер договора"])}</li>'; $('anumber').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата договора"])}</li>'; $('adate').up('span').addClassName('k-error-colored'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата окончания"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Дата окончания"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${license?1:0}){
          location.reload(true);
        } else if(e.responseJSON.license){
          location.assign('${createLink(controller:controllerName,action:'license')}'+'/'+e.responseJSON.license);
        } else
          location.assign('${createLink(controller:controllerName,action:'index')}');
      }
      function processAddplanpaymentResponse(e){
        var sErrorMsg = '';
        ['planpayment_summa'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['planpayment_paydate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма платежа"])}</li>'; $('planpayment_summa').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата платежа"])}</li>'; $('planpayment_paydate').up('span').addClassName('k-error-colored'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorplanpaymentlist").innerHTML=sErrorMsg;
          $("errorplanpaymentlist").up('div').show();
        } else
          jQuery('#planpaymentAddForm').slideUp(300, function() {$('planpayments_submit_button').click();});
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
          case 0: getPlanpayment();break;
          case 1: getPayrequests();break;
          case 2: getPayments();break;
          case 3: getHistory();break;
        }
      }
      function getPlanpayment(){
        if(${license?1:0}) $('planpayments_submit_button').click();
      }
      function getPayrequests(){
        if(${license?1:0}) $('payrequests_submit_button').click();
      }
      function getPayments(){
        if(${license?1:0}) $('payments_submit_button').click();
      }
      function getHistory(){
        if(${license?1:0}) $('history_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
        jQuery("#adate").mask("99.99.9999",{placeholder:" "});
        jQuery("#enddate").mask("99.99.9999",{placeholder:" "});
        new Autocomplete('sro', {
          serviceUrl:'${resource(dir:"autocomplete",file:"space_owner_autocomplete")}'
        });
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
      .tabs a:hover,.tabs a.active,.tabs a.active i{color:#000}
    </style>
  </head>
  <body onload="init();">
    <h3 class="fleft"><g:if test="${license}">Лицензионный договор №${license.anumber}</g:if><g:else>Новый лицензионный договор</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку лицензий</a>
    <div class="clear"></div>
    <g:formRemote name="requestDetailForm" url="${[action:'updatelicense',id:license?.id?:0]}" method="post" onSuccess="processResponse(e)">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="company_id">Лицензиат:</label>
      <g:select name="company_id" value="${license?license.company_id:inrequest.company_id?:0}" from="${companies}" optionValue="name" optionKey="id"  noSelection="${['0':'не выбран']}" disabled="${license?'true':'false'}"/>
      <label for="sro">Лицензиар:</label>
    <g:if test="${iscanaddcompanies}">
      <span class="input-append">
        <input type="text" class="nopad normal" ${license?'disabled':''} id="sro" name="sro" value="${sro?.name}"/>
        <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
      </span>
    </g:if><g:else>
      <input type="text" id="sro" ${license?'disabled':''} name="sro" value="${sro?.name}"/>
    </g:else>
      <label for="anumber">Номер договора:</label>
      <input type="text" id="anumber" name="anumber" value="${license?.anumber}"/>
      <label for="industry_id">Направление:</label>
      <g:select name="industry_id" value="${license?.industry_id?:0}" from="${industries}" optionValue="name" optionKey="id" noSelection="${['0':'не выбрано']}"/>
      <label for="adate">Дата договора:</label>
      <g:datepicker class="normal nopad" style="margin-right:108px" name="adate" value="${license?.adate?String.format('%td.%<tm.%<tY',license.adate):''}"/>
      <label for="enddate">Дата окончания:</label>
      <g:datepicker class="normal nopad" name="enddate" value="${license?.enddate?String.format('%td.%<tm.%<tY',license.enddate):''}"/>
      <label for="entryfee">Вступит. взнос:</label>
      <input type="text" id="entryfee" name="entryfee" value="${intnumber(value:license?.entryfee)}"/>
      <label for="paytype">Тип взноса:</label>
      <g:select name="paytype" value="${license?.paytype}" from="['Единовременный','График платежей']" keys="12"/>
      <label for="regfee">Членские взносы:</label>
      <input type="text" id="regfee" name="regfee" value="${intnumber(value:license?.regfee)}"/>
      <label for="regfeeterm">Периодичность:</label>
      <g:select name="regfeeterm" value="${license?.regfeeterm}" from="['Ежемесячно','Ежеквартально']" keys="12"/>
      <label for="strakhfee">Страховой взнос:</label>
      <input type="text" id="strakhfee" name="strakhfee" value="${intnumber(value:license?.strakhfee)}"/>

      <hr class="admin" />

      <label for="alimit">Сумма допуска:</label>
      <input type="text" id="alimit" name="alimit" value="${intnumber(value:license?.alimit)}"/>
      <label for="paidfee">Оплаченный лимит:</label>
      <input type="text" id="paidfee" disabled value="${intnumber(value:license?.paidfee)}"/>

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
      <g:if test="${license?.file}">
        <a class="button" href="${createLink(controller:'agreement', action:'showscan',id:license.file,params:[code:Tools.generateModeParam(license.file)])}" target="_blank">Просмотреть скан лицензии</a>
      </g:if>
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(1)"/>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      </g:if>
      <g:if test="${license?.modstatus>-1&&isCanDelete}">
        <input type="button" class="spacing reset" value="К удалению" onclick="deleteAgr()" />
      </g:if>
      </div>
      <input type="hidden" id="modstatus" name="modstatus" value="1"/>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${license&&iscanedit}">
    <g:form class="fright" style="margin-top:-200px;padding-right:60px" name="licensescanForm" url="${[action:'addlicensescan',id:license.id]}" method="post" enctype="multipart/form-data" target="upload_target">
      <label for="file">Новый скан:</label>
      <input type="file" id="file" name="file" style="width:256px" onchange="$('licensescanForm').submit()"/>
    </g:form>
  </g:if>
  <g:if test="${license}">
    <div class="tabs">
      <ul class="nav">
        <li><a href="javascript:void(0)" onclick="viewCell(0)">График платежей</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(1)">Платежи</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(2)">Платежи по выписке</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(3)">История</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="planpaymentsForm" url="[action:'licplanpayments',id:license.id]" update="details">
      <input type="submit" class="button" id="planpayments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="payrequestsForm" url="[action:'licpayrequests',id:license.id]" update="details">
      <input type="submit" class="button" id="payrequests_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="paymentsForm" url="[action:'licpayments',id:license.id]" update="details">
      <input type="submit" class="button" id="payments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="historyForm" url="[action:'licensehistory',id:license.id]" update="details">
      <input type="submit" class="button" id="history_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <iframe id="upload_target" name="upload_target" style="display:none"></iframe>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>