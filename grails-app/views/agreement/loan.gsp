<html>
  <head>
    <title>Prisma: <g:if test="${loan}">Договор займа №${loan.anumber}</g:if><g:else>Новый договор займа</g:else></title>
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
      function confirmgenerate(){
        if(confirm('Сформировать платежи по займу? При этом будут удалены все существующие платежи')) { 
          <g:remoteFunction class="button" url="${[controller:controllerName,action:'generateloanpayments',id:loan?.id]}" onSuccess="getPayrequests()"/>
        }
      }
      function confirmUpload(){
        if(confirm('Закачать платежи по займу? При этом будут удалены все существующие платежи')) { 
          $('uploadpaymentsForm').submit();
        } else $('cancelluploadbutton').click();
      }
      function toggleclientspan(iValue){
        if (iValue=='1') { $('clientcompanyspan').hide(); $('clientpersspan').show(); }
        else { $('clientpersspan').hide(); $('clientcompanyspan').show(); }
      }
      function togglelenderspan(iValue){
        if (iValue=='1') { $('lendercompanyspan').hide(); $('lenderpersspan').show(); }
        else { $('lenderpersspan').hide(); $('lendercompanyspan').show(); }
      }
      function deleteAgr(){
        if(confirm('Вы уверены, что хотите удалить договор займа №${loan?.anumber}?')) { submitForm(-1) }
      }
      function newCompany(){
        window.open('${createLink(controller:'company',action:'detail')}');
      }
      function copyadate(sValue){
        if(!startdate.val()) startdate.val(sValue)
      }
      function togglemonthnumber(sValue){
        if(sValue=='2') $('monthnumber').up('span').show();
        else $('monthnumber').up('span').hide();
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['lenderpers','clientpers','clienttype','anumber','summa','rate','payterm'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['adate','enddate','startdate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        ['clientcompany','lendercompany'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Займодавец"])}</li>'; $('lendercompany').up('span').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Займодавец"])}</li>'; $('lendercompany').up('span').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Займодавца","Займодавец"])}</li>'; $('lendercompany').up('span').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Заемщик"])}</li>'; $('clientcompany').up('span').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Заемщик"])}</li>'; $('clientcompany').up('span').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Заемщика","Заемщик"])}</li>'; $('clientcompany').up('span').addClassName('red'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Займодавец"])}</li>'; $('lenderpers').addClassName('red'); break;
              case 8: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Займодавец"])}</li>'; $('lenderpers').addClassName('red'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Заемщик"])}</li>'; $('clientpers').addClassName('red'); break;
              case 10: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Заемщик"])}</li>'; $('clientpers').addClassName('red'); break;
              case 11: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Тип заемщика"])}</li>'; $('clienttype').addClassName('red'); break;
              case 12: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Номер договора"])}</li>'; $('anumber').addClassName('red'); break;
              case 13: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата договора"])}</li>'; $('adate').up('span').addClassName('k-error-colored'); break;
              case 14: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('summa').addClassName('red'); break;
              case 15: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Ставка"])}</li>'; $('rate').addClassName('red'); break;
              case 16: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата выдачи"])}</li>'; $('startdate').up('span').addClassName('k-error-colored'); break;
              case 17: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок займа"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 18: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Срок займа"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 19: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата ежемесячного платежа"])}</li>'; $('payterm').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${loan?1:0}){
          location.reload(true);
        } else if(e.responseJSON.loan){
          location.assign('${createLink(controller:controllerName,action:'loan')}'+'/'+e.responseJSON.loan);
        } else
          returnToList();
      }
      function processAddplanpaymentResponse(e){
        var sErrorMsg = '';
        ['planpayment_summa','planpayment_summarub'].forEach(function(ids){
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
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма платежа в рублях"])}</li>'; $('planpayment_summarub').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorplanpaymentlist").innerHTML=sErrorMsg;
          $("errorplanpaymentlist").up('div').show();
        } else
          jQuery('#planpaymentAddForm').slideUp(300, function() { getPlanpayment(); });
      }
      function processAddloanpaymentResponse(e){
        var sErrorMsg = '';
        ['loanpayment_summa','loanpayment_summaperc','loanpayment_summarub','loanpayment_summapercrub'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['loanpayment_paydate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Сумма тела, Сумма процентов"])}</li>'; $('loanpayment_summa').addClassName('red'); $('loanpayment_summaperc').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата платежа"])}</li>'; $('loanpayment_paydate').up('span').addClassName('k-error-colored'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Сумма тела в рублях, Сумма процентов в рублях"])}</li>'; $('loanpayment_summarub').addClassName('red'); $('loanpayment_summapercrub').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorloanpaymentlist").innerHTML=sErrorMsg;
          $("errorloanpaymentlist").up('div').show();
        } else
          jQuery('#loanpaymentAddForm').slideUp(300, function() { getPayrequests(); });
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
          case 2: getOutgoingPayments();break;
          case 3: getIncomingPayments();break;
          case 4: getHistory();break;
        }
      }
      function getPayrequests(){
        if(${loan?1:0}) $('payments_submit_button').click();
      }
      function getHistory(){
        if(${loan?1:0}) $('history_submit_button').click();
      }
      function getPlanpayment(){
        if(${loan?1:0}) $('planpayments_submit_button').click();
      }
      function getIncomingPayments(){
        if(${loan?1:0}) $('lninpayrequests_submit_button').click();
      }
      function getOutgoingPayments(){
        if(${loan?1:0}) $('lnoutpayrequests_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
      <g:if test="${!loan}">
        new Autocomplete('clientcompany', {
          serviceUrl:'${resource(dir:"autocomplete",file:"space_arendodatel_autocomplete")}'
        });
        new Autocomplete('clientpers', {
          serviceUrl:'${resource(dir:"autocomplete",file:"persname_autocomplete")}',
          width:252
        });
        new Autocomplete('lendercompany', {
          serviceUrl:'${resource(dir:"autocomplete",file:"space_arendodatel_autocomplete")}'
        });
        new Autocomplete('lenderpers', {
          serviceUrl:'${resource(dir:"autocomplete",file:"compholdername_autocomplete")}',
          width:252
        });
      </g:if>
        jQuery("#adate").mask("99.99.9999",{placeholder:" "});
        jQuery("#startdate").mask("99.99.9999",{placeholder:" "});
        jQuery("#enddate").mask("99.99.9999",{placeholder:" "});
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
      .tabs a:hover,.tabs a.active,.tabs a.active i{color:#000}
      .k-ff { overflow: inherit !important;}
    </style>
  </head>
  <body onload="init()">
    <h3 class="fleft"><g:if test="${loan}">Договор займа №${loan.anumber}</g:if><g:else>Новый договор займа</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку займов</a>
    <div class="clear"></div>
    <g:formRemote name="requestDetailForm" url="${[action:'updateloan',id:loan?.id?:0]}" method="post" onSuccess="processResponse(e)">

    <g:if test="${loan?.modstatus==0}">
      <div class="info-box" style="margin-top:0">
        <span class="icon icon-info-sign icon-3x"></span>
        <ul id="infolist">
          <li>Внимание! Срок действия договора истек.</li>
        </ul>
      </div>
    </g:if>

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

    <g:if test="${loan}">
      <label for="inputdate" disabled>Дата создания:</label>
      <input type="text" name="inputdate" disabled value="${String.format('%td.%<tm.%<tY %<tT',loan.inputdate)}" />
      <label for="status" disabled>Статус:</label>
      <g:select name="status" value="${loan.modstatus}" from="['Архив','Активный','Удален']" keys="[0,1,-1]" disabled="true"/>
      <label for="loantype" disabled>Тип займа:</label>
      <g:select name="loantype" value="${loan.loantype}" from="['Заем у внешней','Выдача внешней','Внутренний займ','Займ учредителя','Займ работнику']" keys="12345" disabled="true"/>
      <label for="debt" disabled>Задолженность:</label>
      <input type="text" id="debt" disabled value="${intnumber(value:0)}" />
      <label for="lender" disabled>Займодавец:</label>
      <input type="text" id="lender" name="lender" value="${lender}" disabled/>
      <label for="client" disabled>Заемщик:</label>
      <input type="text" id="client" name="client" value="${client}" disabled/>
      <hr class="admin" />
    </g:if>
    <g:if test="${!loan}">
      <label for="lendertype">Тип займодавца:</label>
      <g:select name="lendertype" value="0" from="['Юр. лицо','Физ. лицо']" keys="01" onchange="togglelenderspan(this.value)"/>
      <label for="lendercompany">Займодавец:</label>
      <span id="lendercompanyspan"><span class="input-append">
        <input type="text" class="nopad normal" id="lendercompany" name="lendercompany" value=""/>
        <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
      </span>
      <div id="lendercompanyname_autocomplete" class="autocomplete" style="display:none"></div></span>
      <span id="lenderpersspan" style="display:none"><input type="text" id="lenderpers" name="lenderpers" value=""/>
      <div id="space_arendator_autocomplete" class="autocomplete" style="display:none"></div></span>
      <label for="clienttype">Тип заемщика:</label>
      <g:select name="clienttype" value="0" from="['Юр. лицо','Физ. лицо']" keys="01" onchange="toggleclientspan(this.value)"/>
      <label for="clientcompany">Заемщик:</label>
      <span id="clientcompanyspan"><span class="input-append">
        <input type="text" class="nopad normal" id="clientcompany" name="clientcompany" value=""/>
        <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
      </span>
      <div id="clientcompanyname_autocomplete" class="autocomplete" style="display:none"></div></span>
      <span id="clientpersspan" style="display:none"><input type="text" id="clientpers" name="clientpers" value=""/>
      <div id="space_arendator_autocomplete" class="autocomplete" style="display:none"></div></span>

      <hr class="admin" />
    </g:if>
      <label for="anumber">Номер договора:</label>
      <input type="text" id="anumber" name="anumber" value="${loan?.anumber}"/>
      <label for="adate">Дата договора:</label>
      <g:datepicker class="normal nopad" name="adate" value="${loan?.adate?String.format('%td.%<tm.%<tY',loan.adate):''}" onchange="copyadate(adate.val())" />
      <label for="startdate">Дата выдачи:</label>
      <g:datepicker class="normal nopad" style="margin-right:108px" name="startdate" value="${loan?.startdate?String.format('%td.%<tm.%<tY',loan.startdate):''}"/>
      <label for="enddate">Срок займа:</label>
      <g:datepicker class="normal nopad" style="margin-right:108px" name="enddate" value="${loan?.enddate?String.format('%td.%<tm.%<tY',loan.enddate):''}"/>
      <label for="payterm">Дата:<br/><small>ежемесячного платежа</small></label>
      <input type="text" id="payterm" name="payterm" value="${loan?.payterm}"/>
      <label for="loanclass">Класс займа:</label>
      <g:select name="loanclass" value="${loan?.loanclass}" from="['Кредит','Кредитная линия']" keys="12"/>
      <label for="repaymenttype_id">Погашение тела</label>
      <g:select name="repaymenttype_id" value="${loan?.repaymenttype_id}" from="${Repaymenttype.list()}" optionValue="name" optionKey="id" onchange="togglemonthnumber(this.value)"/>
      <span style="${loan?.repaymenttype_id!=2?'display:none':''}"><label for="monthnumber">Номер месяца:</label>
      <input type="text" id="monthnumber" name="monthnumber" value="${loan?.monthnumber}"/></span>

      <hr class="admin" />

      <label for="summa">Сумма:</label>
      <input type="text" id="summa" name="summa" value="${intnumber(value:loan?.summa)}"/>
      <label for="rate">Ставка:</label>
      <input type="text" id="rate" name="rate" value="${loan?.rate}"/>
      <label for="valuta_id">Валюта кредита:</label>
      <g:select id="valuta_id" name="valuta_id" from="${Valuta.findAllByModstatus(1)}" value="${loan?.valuta_id?:857}" optionValue="name" optionKey="id"/>
      <label class="auto" for="is_cbcalc">
        <input type="checkbox" id="is_cbcalc" name="is_cbcalc" value="1" <g:if test="${loan?.is_cbcalc}">checked</g:if> />
        Перерасчет по ЦБ
      </label>

      <hr class="admin" />

      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" id="comment" value="${loan?.comment}" />

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(1)"/>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      </g:if>
      <g:if test="${loan?.modstatus>-1&&isCanDelete}">
        <input type="button" class="spacing reset" value="К удалению" onclick="deleteAgr()" />
      </g:if>
      </div>
      <input type="hidden" id="modstatus" name="modstatus" value="1"/>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${loan}">
    <div class="tabs">
      <ul class="nav">
        <li style="${loan.loanclass!=2?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(0)">График платежей</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(1)">Погашение</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(2)">Платежи погашения</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(3)">Платежи выдачи</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(4)">История</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="paymentsForm" url="[action:'loanpayments',id:loan.id]" update="details">
      <input type="submit" class="button" id="payments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="planpaymentsForm" url="[action:'loanline',id:loan.id]" update="details">
      <input type="submit" class="button" id="planpayments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="lnoutpayrequestsForm" url="[action:'lnoutpayrequests',id:loan.id]" update="details">
      <input type="submit" class="button" id="lnoutpayrequests_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="lninpayrequestsForm" url="[action:'lninpayrequests',id:loan.id]" update="details">
      <input type="submit" class="button" id="lninpayrequests_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="historyForm" url="[action:'loanhistory',id:loan.id]" update="details">
      <input type="submit" class="button" id="history_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>