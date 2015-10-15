<html>
  <head>
    <title>Prisma: <g:if test="${kredit}">Кредитный договор №${kredit.anumber}</g:if><g:else>Новый кредитный договор</g:else></title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript>
      var notsaved = true;
      function returnToList(){
        if (isFormModified('kreditDetailForm')){
          if(notsaved&&confirm('Сохранить изменения?')) { submitForm(${kredit?kredit.modstatus:1}) }
          else $("returnToListForm").submit();
        } else
          $("returnToListForm").submit();
      }
      function isInputModified(input) {
        switch(input.type) {
          case 'select-one':
            var select_modified = false;
            var first_selected = false;
            for (var i = 0; i < input.options.length; i++) {
              var c_opt = input.options[i];
              if (c_opt.defaultSelected != c_opt.selected) {
                select_modified = true;
                if (c_opt.selected == true && i == 0) {
                  first_selected = true;
                } else if (i > 0 && c_opt.defaultSelected) {
                  first_selected = false
                }
              }
            }
            return select_modified && !first_selected;
          case 'radio': case 'checkbox':
            return !(input.defaultChecked == input.checked);
          default:
            return !(input.defaultValue == input.value);
        }
      }
      
      function isFormModified(form_id) {
        var form = document.getElementById(form_id);
        for (var i = 0; i < form.elements.length; i++) {
          if (isInputModified(form.elements[i])) {
            return true;
          }
        }
        return false;
      }
      function deleteAgr(){
        if(confirm('Вы уверены, что хотите удалить кредитный договор №${kredit?.anumber}?')) { submitForm(-1) }
      }
      function confirmgenerate(){
        if(confirm('Сформировать платежи по кредиту? При этом будут удалены все существующие платежи')) { 
          <g:remoteFunction class="button" url="${[controller:controllerName,action:'generatekreditpayments',id:kredit?.id]}" onSuccess="getPayments()"/>
        }
      }
      function confirmUpload(){
        if(confirm('Закачать платежи по кредиту? При этом будут удалены все существующие платежи')) { 
          $('uploadpaymentsForm').submit();
        } else $('cancelluploadbutton').click();
      }
      function copysum(sValue){
        var el = $('agentsum');
        if(el){
          if(!el.value) el.value = sValue;
        }
      }
      function copyadate(sValue){
        if(!startdate.val()) startdate.val(sValue)
      }
      function toggleCBcalc(sValue){
        if(sValue=='857') $('is_cbcalc').up('label').hide();
        else $('is_cbcalc').up('label').show();
      }
      function togglerepsection(sValue){
        if(sValue=='3') $('repsection').hide();
        else $('repsection').show();
        if (sValue=='4') {
          $('nontranshsection').hide();
          $('transhsection').show();
        } else {
          $('transhsection').hide();
          $('nontranshsection').show();
        }
      }
      function togglemonthnumber(sValue){
        if(sValue=='2') $('monthnumber').up('span').show();
        else $('monthnumber').up('span').hide();
      }
      function togglespace(sId){
        if (sId=='0') $('kreditzalogagr_spanspace').hide();
        else $('kreditzalogagr_spanspace').show();
        <g:remoteFunction controller='agreement' action='kreditzalogspacelist' id="${kredit?.client?:0}" params="'space='+sId" update="kreditzalogagr_spanspace" />
      }
      function computePercents(){
        var sDate = percentdate.val();
        var sStartdate = startpercentdate.val();
        <g:remoteFunction controller='agreement' action='kreditpercents' id="${kredit?.id?:0}" params="'percdate='+sDate+'&startdate='+sStartdate" onSuccess="\$('percents').value = e.responseJSON.percents;" />
      }
      function getBankList(){
        setTimeout(function() {
          var arendodatel = $('client').value
          <g:remoteFunction controller='agreement' action='spacebanklist' params="'arendodatel='+arendodatel" update="banklist" />
        }, 300)
      }
      function processReasonagr(e,sType){
        if(e.responseJSON){
          $('reasonagr'+sType+'_enddate').value=e.responseJSON.enddate;
        } else {
          $('reasonagr'+sType+'_enddate').value='';
        }
      }
      function processResponse(e){
        $("saveinfo").up('div').hide();
        $("errorlist").up('div').hide();
        var sErrorMsg = '';
        ['client','bank_name','anumber','summa','rate','payterm','kredtype','kredittransh','comment','sschet','percschet','comschet'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['adate','startdate','enddate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Заемщик"])}</li>'; $('client').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Заемщик"])}</li>'; $('client').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Заемщика","Заемщик"])}</li>'; $('client').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк-кредитор"])}</li>'; $('bank_name').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Номер договора"])}</li>'; $('anumber').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата заключения"])}</li>'; $('adate').up('span').addClassName('k-error-colored'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('summa').addClassName('red'); break;
              case 8: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Ставка"])}</li>'; $('rate').addClassName('red'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Комментарий"])}</li>'; $('comment').addClassName('red'); break;
              case 10: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок кредита"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 11: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Банк-кредитор"])}</li>'; $('bank_name').addClassName('red'); break;
              case 12: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Срок кредита"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 13: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Ссудный счет"])}</li>'; $('sschet').addClassName('red'); break;
              case 14: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Вид кредита"])}</li>'; $('kredtype').addClassName('red'); break;
              case 15: sErrorMsg+='<li>${message(code:"error.multiple.bikchoice.message",args:["Банк","Банк-кредитор"])}</li>'; $('bank_name').addClassName('red'); break;
              case 16: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата выдачи"])}</li>'; $('startdate').up('span').addClassName('k-error-colored'); break;
              case 17: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата ежемесячного платежа"])}</li>'; $('payterm').addClassName('red'); break;
              case 18: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок транша"])}</li>'; $('kredittransh').addClassName('red'); break;
              case 19: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Счет по процентам"])}</li>'; $('percschet').addClassName('red'); break;
              case 20: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Счет по комиссиям"])}</li>'; $('comschet').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(${kredit?1:0}){
          $("debt").value = e.responseJSON.debt;
          $("saveinfo").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(e.responseJSON.kredit){
          location.assign('${createLink(controller:controllerName,action:'kredit')}'+'/'+e.responseJSON.kredit);
        } else
          location.assign('${createLink(controller:controllerName,action:'index')}');
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
          jQuery('#planpaymentAddForm').slideUp(300, function() {$('planpayments_submit_button').click();});
      }
      function processAddkreditpaymentResponse(e){
        var sErrorMsg = '';
        ['kreditpayment_summa','kreditpayment_summaperc','kreditpayment_summarub','kreditpayment_summapercrub'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['kreditpayment_paydate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Сумма погашения, Сумма процентов"])}</li>'; $('kreditpayment_summa').addClassName('red'); $('kreditpayment_summaperc').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата платежа"])}</li>'; $('kreditpayment_paydate').up('span').addClassName('k-error-colored'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Сумма погашения в рублях, Сумма процентов в рублях"])}</li>'; $('kreditpayment_summarub').addClassName('red'); $('kreditpayment_summapercrub').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorkreditpaymentlist").innerHTML=sErrorMsg;
          $("errorkreditpaymentlist").up('div').show();
        } else
          jQuery('#kreditpaymentAddForm').slideUp(300, function() {$('payments_submit_button').click();});
      }
      function processAddkreditdopagrResponse(e){
        var sErrorMsg = '';
        ['kreditdopagr_nomer','kreditdopagr_summa','kreditdopagr_rate'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['kreditdopagr_dsdate','kreditdopagr_startdate','kreditdopagr_enddate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Номер соглашения"])}</li>'; $('kreditdopagr_nomer').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('kreditdopagr_summa').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Ставка"])}</li>'; $('kreditdopagr_rate').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата соглашения"])}</li>'; $('kreditdopagr_dsdate').up('span').addClassName('k-error-colored'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата начала"])}</li>'; $('kreditdopagr_startdate').up('span').addClassName('k-error-colored'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата окончания"])}</li>'; $('kreditdopagr_enddate').up('span').addClassName('k-error-colored'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Дата окончания"])}</li>'; $('kreditdopagr_enddate').up('span').addClassName('k-error-colored'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorkreditdopagrlist").innerHTML=sErrorMsg;
          $("errorkreditdopagrlist").up('div').show();
        } else
          jQuery('#kreditdopagrAddForm').slideUp(300, function() { getDopAgrs(); });
      }
      function processAddkreditzalogagrResponse(e){
        var sErrorMsg = '';
        ['kreditzalogagr_pledger','kreditzalogagr_zalogtype_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['kreditzalogagr_zalogstart','kreditzalogagr_zalogend'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Залогодатель"])}</li>'; $('kreditzalogagr_pledger').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип залога"])}</li>'; $('kreditzalogagr_zalogtype_id').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата залога"])}</li>'; $('kreditzalogagr_zalogstart').up('span').addClassName('k-error-colored'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок залога"])}</li>'; $('kreditzalogagr_zalogend').up('span').addClassName('k-error-colored'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorkreditzalogagrlist").innerHTML=sErrorMsg;
          $("errorkreditzalogagrlist").up('div').show();
        } else
          jQuery('#kreditzalogagrAddForm').slideUp(300, function() { getZalogAgrs(); });
      }
      function processaddpayrequestResponse(e){
        var sErrorMsg = '';
        ['payrequest_summa','payrequest_expensetype_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['payrequest_paydate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        $('payrequest_is_task').value=0;
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Сумма"])}</li>'; $('payrequest_summa').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата платежа"])}</li>'; $('payrequest_paydate').up('span').addClassName('k-error-colored'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Доходы-расходы"])}</li>'; $('payrequest_expensetype_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorpayrequestlist").innerHTML=sErrorMsg;
          $("errorpayrequestlist").up('div').show();
        } else
          jQuery('#payrequestAddForm').slideUp(300, function() { getPayrequests(); });
      }
      function setcheck(iCheck){
        $('is_check').value = iCheck
      }
      function restoreAgr(){
        if(confirm('Восстановить кредитный договор №${kredit?.anumber}?')) { submitForm(1) }
      }
      function closeAgr(){
        if(confirm('Вы уверены, что хотите закрыть кредитный договор №${kredit?.anumber}?')) { submitForm(0) }
      }
      function submitForm(iStatus){
        $('modstatus').value = iStatus
        notsaved = false;
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
          case 0: getBalance();break;
          case 1: getPlanpayment();break;
          case 2: getPayments();break;
          case 3: getPayrequests();break;
          case 4: getRealPayments();break;
          case 5: getPercOverdraft();break;
          case 6: getDopAgrs();break;
          case 7: getZalogAgrs();break;
          case 8: getHistory();break;
        }
      }
      function getPercOverdraft(){
        if(${kredit?1:0}) $('percoverdraft_submit_button').click();
      }
      function getBalance(){
        if(${kredit?1:0}) $('balance_submit_button').click();
      }
      function getPlanpayment(){
        if(${kredit?1:0}) $('planpayments_submit_button').click();
      }
      function getPayments(){
        if(${kredit?1:0}) $('payments_submit_button').click();
      }
      function getHistory(){
        if(${kredit?1:0}) $('history_submit_button').click();
      }
      function getRealPayments(){
        if(${kredit?1:0}) $('krinpayrequests_submit_button').click();
      }
      function getPayrequests(){
        if(${kredit?1:0}) $('kroutpayrequests_submit_button').click();
      }
      function getDopAgrs(){
        if(${kredit?1:0}) $('dopagrs_submit_button').click();
      }
      function getZalogAgrs(){
        if(${kredit?1:0}) $('zalogagrs_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
        new Autocomplete('client', {
          serviceUrl:'${resource(dir:"autocomplete",file:"space_arendodatel_autocomplete")}',
          onSelect: function(value, data){
            $('client').focus();
          }
        });
        new Autocomplete('bank_name', {
          serviceUrl:'${resource(dir:"autocomplete",file:"banknamebik_autocomplete")}'
        });
        jQuery("#adate").mask("99.99.9999",{placeholder:" "});
        jQuery("#startdate").mask("99.99.9999",{placeholder:" "});
        jQuery("#enddate").mask("99.99.9999",{placeholder:" "});
        jQuery("#startsaldodate").mask("99.99.9999",{placeholder:" "});
        jQuery("#percentdate").mask("99.99.9999",{placeholder:" "});
        jQuery("#startpercentdate").mask("99.99.9999",{placeholder:" "});
        jQuery("#sschet").mask("?*****.***.*.*******.****",{placeholder:" "});
        jQuery("#percschet").mask("?*****.***.*.*******.****",{placeholder:" "});
        jQuery("#comschet").mask("?*****.***.*.*******.****",{placeholder:" "});
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
      .tabs a:hover,.tabs a.active,.tabs a.active i{color:#000}
    </style>
  </head>
  <body onload="init()">
    <h3 class="fleft"><g:if test="${kredit}">Кредитный договор №${kredit.anumber} (${kredit.id} | ${String.format('%td.%<tm.%<tY %<tT',kredit.inputdate)})</g:if><g:else>Новый кредитный договор</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку кредитов</a>
    <div class="clear"></div>
    <g:formRemote name="kreditDetailForm" url="${[action:'updatekredit',id:kredit?.id?:0]}" method="post" onSuccess="processResponse(e)">

    <g:if test="${kredit?.modstatus==0}">
      <div class="info-box" style="margin-top:0">
        <span class="icon icon-info-sign icon-3x"></span>
        <ul id="infolist">
          <li>Внимание! Договор закрыт.</li>
        </ul>
      </div>
    </g:if>

      <div class="info-box" style="display:none;margin-top:0">
        <span class="icon icon-info-sign icon-3x"></span>
        <ul id="saveinfo">
          <li>Изменения сохранены!</li>
        </ul>
      </div>
      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

    <g:if test="${kredit}">
      <label for="debt" disabled>${kredit.kredtype==3?'Остаток с овером':'Задолженность'}:</label>
      <input type="text" id="debt" disabled value="${number(value:debt)}" />
      <label for="status" disabled>Статус:</label>
      <g:select name="status" value="${kredit.modstatus}" from="['К удалению','Архив','Активный']" keys="${-1..1}" disabled="true"/>
      <hr class="admin" style="width:70px;float:left"/><a style="text-decoration:none" href="javascript:void(0)">&nbsp;&nbsp;Текущее&nbsp;состояние&nbsp;</a><hr class="admin" style="width:730px;float:right"/>
    </g:if>

      <label for="client">Заемщик:</label>
      <input type="text" id="client" ${kredit?'disabled':''} name="client" value="${client}"/>
      <div id="space_arendator_autocomplete" class="autocomplete" style="display:none"></div>
      <label for="valuta_id">Валюта кредита:</label>
      <g:select id="valuta_id" name="valuta_id" from="${Valuta.findAllByModstatus(1)}" value="${kredit?.valuta_id?:857}" optionValue="name" optionKey="id" onchange="toggleCBcalc(this.value)"/>
      <label for="bank_name">Банк-кредитор:</label>
      <input type="text" style="width:585px;${bank?.is_license==0?'color:#ff0000;':''}" id="bank_name" name="bank" value="${bank}" ${kredit?'disabled':''}/>
      <div id="bank_autocomplete" class="autocomplete" style="display:none"></div>
      <label class="auto" for="zalogstatus">
        <input type="checkbox" id="zalogstatus" name="zalogstatus" value="2" <g:if test="${kredit?.zalogstatus==2}">checked</g:if> disabled/>
        Залог
      </label>
      <label for="summa">Сумма:</label>
      <input type="text" id="summa" ${kredit?'disabled':''} name="summa" value="${number(value:kredit?.summa)}" onchange="copysum(this.value)" />
      <label for="rate">Ставка в годовых:</label>
      <input type="text" id="rate" ${kredit?'disabled':''} name="rate" value="${kredit?.rate}"/>
      <label for="kredtype">Вид кредита:</label>
      <g:select name="kredtype" value="${kredit?.kredtype}" from="['Кредит','Кредитная линия','Овердрафт','Линия с лимитом задолженности']" keys="${1..4}" onchange="togglerepsection(this.value)"/>
      <label for="kreditsort">Тип договора:</label>
      <g:select id="kreditsort" name="kreditsort" value="${kredit?.is_tech?1:kredit?.is_realtech?2:0}" from="['Реальный договор','Технический договор','Реалтех']" keys="012"/>
      <label for="anumber">Номер договора:</label>
      <input type="text" id="anumber" ${kredit?'disabled':''} name="anumber" value="${kredit?.anumber}"/>
      <label for="adate">Дата заключения:</label>
      <g:datepicker class="normal nopad" name="adate" value="${kredit?.adate?String.format('%td.%<tm.%<tY',kredit.adate):''}" disabled="${kredit?'true':'false'}" onchange="copyadate(adate.val())" />
      <label for="startdate">Дата выдачи:</label>
      <g:datepicker class="normal nopad" style="margin-right:108px" name="startdate" value="${kredit?.startdate?String.format('%td.%<tm.%<tY',kredit.startdate):''}" disabled="${kredit?'true':'false'}"/>
      <label for="enddate">Срок кредита:</label>
      <g:datepicker class="normal nopad" style="margin-right:108px" name="enddate" value="${kredit?.enddate?String.format('%td.%<tm.%<tY',kredit.enddate):''}" disabled="${kredit?'true':'false'}"/>
      <div id="repsection" style="${kredit?.kredtype==3?'display:none':''}">
        <label for="payterm">Дата:<br/><small>ежемесячного платежа</small></label>
        <input type="text" id="payterm" name="payterm" value="${kredit?.payterm}"/>
        <span id="transhsection" style="${kredit?.kredtype!=4?'display:none':''}"><label for="kredittransh">Срок транша:</label>
        <input type="text" id="kredittransh" name="kredittransh" value="${kredit?.kredittransh}"/></span>
        <br/><span id="nontranshsection" style="${kredit?.kredtype==4?'display:none':''}"><label for="repaymenttype_id">Погашение кредита</label>
        <g:select name="repaymenttype_id" value="${kredit?.repaymenttype_id}" from="${Repaymenttype.list()}" optionValue="name" optionKey="id" onchange="togglemonthnumber(this.value)"/>
        <span style="${kredit?.repaymenttype_id!=2?'display:none':''}"><label for="monthnumber">Номер месяца:</label>
        <input type="text" id="monthnumber" name="monthnumber" value="${kredit?.monthnumber}"/></span></span>
      </div>
      <label for="is_agr">Наличие договора:</label>
      <g:select name="is_agr" value="${kredit?.is_agr}" from="['Нет','Есть']" keys="01"/>
      <label class="auto" for="is_cbcalc" style="${(kredit?.valuta_id?:857)==857?'display:none':''}">
        <input type="checkbox" id="is_cbcalc" name="is_cbcalc" value="1" <g:if test="${kredit?.is_cbcalc}">checked</g:if> />
        Перерасчет по ЦБ
      </label>
      <label class="auto" for="cessionstatus">
        <input type="checkbox" id="cessionstatus" name="cessionstatus" value="1" <g:if test="${kredit?.cessionstatus}">checked</g:if> disabled />
        Уступка
      </label>
    <g:if test="${kredit?.cessionstatus}">
      <br/><label for="creditor">Новый заемщик:</label>
      <input type="text" class="fullline" id="creditor" name="creditor" value="${Company.get(kredit?.creditor)?.name}" disabled/>
      <label for="cbank_id">Новый Банк:</label>
      <input type="text" class="fullline" id="cbank_id" name="cbank_id" value="${Bank.get(kredit?.cbank_id)?.name}" disabled/>
    </g:if>
      <br/><label for="aim">Цель кредита:</label>
      <input type="text" class="fullline" id="aim" name="aim" value="${kredit?.aim}"/>

      <hr class="admin" />

      <label for="responsible">Ответственный:</label>
      <g:select name="responsible" value="${kredit?.responsible?:session.user.id}" from="${users}" optionValue="pers_name" optionKey="id" noSelection="${['0':'не выбрано']}"/>
    <g:if test="${iscanclient}">
      <label for="client_id">Клиент:</label>
      <g:select name="client_id" value="${kredit?.client_id}" from="${clients}" optionValue="name" optionKey="id" noSelection="${['-1':'не выбрано']}" disabled="${ishaveAgentagr}"/>
      <label for="agentsum">Сумма агентских:</label>
      <input type="text" id="agentsum" name="agentsum" ${ishaveAgentagr?'disabled':''} value="${number(value:kredit?.agentsum)}"/>
    </g:if>
      <br/><label for="startsumma">Начальное сальдо:</label>
      <input type="text" id="startsumma" name="startsumma" value="${number(value:kredit?.startsumma)}"/>
      <label for="startsaldodate">Дата сальдо:</label>
      <g:datepicker class="normal nopad" name="startsaldodate" value="${kredit?.startsaldodate?String.format('%td.%<tm.%<tY',kredit.startsaldodate):''}"/>
      <label for="sschet">Ссудный счет</label>
      <input type="text" id="sschet" name="sschet" value="${account(value:kredit?.sschet)}"/>
      <label for="percschet">Счет по процентам</label>
      <input type="text" id="percschet" name="percschet" value="${account(value:kredit?.percschet)}"/>
      <label for="comschet">Счет по комиссиям</label>
      <input type="text" id="comschet" name="comschet" value="${account(value:kredit?.comschet)}"/>
      <label for="project_id">Проект:</label>
      <g:select name="project_id" value="${kredit?.project_id}" from="${projects}" optionValue="name" optionKey="id" noSelection="${['0':'не выбрано']}"/>
      <br/><label for="comment">Комментарий:</label>
      <g:textArea name="comment" id="comment" value="${kredit?.comment}" />
      <input type="hidden" id="modstatus" name="modstatus" value="1"/>
      <input type="hidden" id="is_check" name="is_check" value="${kredit?.is_check?:0}"/>
      <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
    </g:formRemote>
    <hr class="admin" />

    <label for="percentdate">Дата процентов:</label>
    <g:datepicker class="normal nopad" style="margin-right:108px" name="percentdate" value="${String.format('%td.%<tm.%<tY',new Date())}" onchange="computePercents()"/>
    <label for="startpercentdate">Начальная дата:</label>
    <g:datepicker class="normal nopad" style="margin-right:108px" name="startpercentdate" value="" onchange="computePercents()"/><br/>
    <label for="percents">Сумма процентов:</label>
    <input type="text" id="percents" value="${number(value:percents)}" disabled/>

    <hr class="admin" />

    <div class="fright" id="btns" style="padding-top:10px">
      <input type="button" class="reset spacing" value="Отменить" onclick="returnToList()" />
    <g:if test="${iscanedit}">
      <input type="button" class="spacing" value="Сохранить с проверкой" onclick="setcheck(1);submitForm(${kredit?kredit.modstatus:1})"/>
      <input type="button" class="spacing" value="Сохранить без проверки" onclick="setcheck(0);submitForm(${kredit?kredit.modstatus:1})"/>
    <g:if test="${isCanClose}">
      <input type="button" class="spacing" value="Закрыть" onclick="closeAgr()" />
    </g:if>
    <g:if test="${isCanEarlyclose}">
      <input type="button" class="spacing" value="Досрочно закрыть" onclick="closeAgr()" />
    </g:if>
    <g:if test="${isCanRestore}">
      <input type="button" class="spacing" value="Восстановить" onclick="restoreAgr()" />
    </g:if>
    <g:if test="${kredit?.modstatus>-1&&isCanDelete}">
      <input type="button" class="spacing reset" value="К удалению" onclick="deleteAgr()" />
    </g:if>
    </g:if>
    </div>
    <div class="clear"></div>
  <g:if test="${kredit}">
    <div class="tabs">
      <ul class="nav">
        <li style="${kredit.kredtype!=3?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(0)">Баланс</a></li>
        <li style="${!(kredit.kredtype in [2,4])?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(1)">График выдачи</a></li>
        <li style="${kredit.kredtype==3?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(2)">График погашения</a></li>
        <li style="${!iscanpay||kredit.kredtype==3?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(3)">Платежи погашения</a></li>
        <li style="${!iscanpay||kredit.kredtype==3?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(4)">Платежи выдачи</a></li>
        <li style="${!iscanpay||kredit.kredtype!=3?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(5)">Платежи погашения процентов</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(6)">Доп. соглашения</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(7)">Обеспечение</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(8)">История</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="balanceForm" url="[action:'kreditbalance',id:kredit.id]" update="details">
      <input type="submit" class="button" id="balance_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="planpaymentsForm" url="[action:'kreditline',id:kredit.id]" update="details">
      <input type="submit" class="button" id="planpayments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="paymentsForm" url="[action:'kreditpayments',id:kredit.id]" update="details">
      <input type="submit" class="button" id="payments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="payrequestsForm" url="[action:'kroutpayrequests',id:kredit.id]" update="details">
      <input type="submit" class="button" id="kroutpayrequests_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="krpaymentsForm" url="[action:'krinpayrequests',id:kredit.id]" update="details">
      <input type="submit" class="button" id="krinpayrequests_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="percoverdraftForm" url="[action:'kreditpercpayments',id:kredit.id]" update="details">
      <input type="submit" class="button" id="percoverdraft_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="dopagrsForm" url="[action:'kreditdopagrs',id:kredit.id]" update="details">
      <input type="submit" class="button" id="dopagrs_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="zalogagrsForm" url="[action:'kreditzalogagrs',id:kredit.id]" update="details">
      <input type="submit" class="button" id="zalogagrs_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="historyForm" url="[action:'kredithistory',id:kredit.id]" update="details">
      <input type="submit" class="button" id="history_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
