<html>
  <head>
    <title>Prisma: <g:if test="${space}">Арендный договор №${space.anumber}</g:if><g:else>Новый арендный договор</g:else></title>
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
        if(confirm('Вы уверены, что хотите удалить арендный договор №${space?.anumber}?')) { submitForm(-1) }
      }
      function newCompany(){
        window.open('${createLink(controller:'company',action:'detail')}');
      }
      function toggleProlongData(sCond){
        $('prolongtermsection').hide();
        $('monthnotificationsection').hide();
        if (sCond=='2') $('prolongtermsection').show();
        else if (sCond=='3') $('monthnotificationsection').show();
      }
      function toggleContcol(sType){
        if(sType!='5') $('contcolsection').hide();
        else $('contcolsection').show();
      }
      function toggleTerarea(sAdd){
        if ($('is_territory').checked) $('terareasection').show();
        else $('terareasection').hide();
      }
      function toggleRatedop(sAdd){
        if(sAdd=='0') $('ratedopsection').hide();
        else $('ratedopsection').show();
      }
      function toggleDopagrRatedop(sAdd){
        if(sAdd=='0') $('dopagrdopsection').hide();
        else $('dopagrdopsection').show();
      }
      function toggleSubwritten(iValue){
        if(iValue=='0'){
          $('is_subwritten').up('label').hide();
        } else {
          $('is_subwritten').up('label').show();
        }
      }
      function toggleMainagr(sAsort){
        if(sAsort=='0'){
          $('mainagr').show();
          $('nosubrenting').hide();
          $('is_subwritten').up('label').hide();
        } else {
          $('mainagr').hide();
          $('nosubrenting').show();
          toggleSubwritten($('is_nosubrenting').value);
        }
      }
      function getMainagr(){
        var arendodatel = $('arendodatel').value
        <g:remoteFunction controller='agreement' action='spacemainagrlist' params="'arendodatel='+arendodatel" update="mainagr" />
      }
      function getBankList(){
        var arendodatel = $('arendodatel').value
        <g:remoteFunction controller='agreement' action='spacebanklist' params="'arendodatel='+arendodatel" update="banklist" />
      }
      function copyAddress(sText){
        if(sText!='не выбран') $('fulladdress').value = sText.split(' | ')[1];
        else $('fulladdress').value = '';
        copyadr($('fulladdress').value);
      }
      function copyadr(sAdr){
        $("shortaddress").value = sAdr;
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['arendator','arendodatel','spacetype_id','fulladdress','anumber','payterm','mainagr_id','contcol','prolongterm','comment','monthnotification'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['adate','enddate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
      <g:if test="${iscanaddcompanies}">
        ['arendodatel'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('red');
        });
      </g:if>
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Арендатор"])}</li>'; $('arendator').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Арендатор"])}</li>'; $('arendator').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Арендатора","Арендатор"])}</li>'; $('arendator').addClassName('red'); break;
            <g:if test="${iscanaddcompanies}">
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Арендодатель"])}</li>'; $('arendodatel').up('span').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Арендодатель"])}</li>'; $('arendodatel').up('span').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Арендодателя","Арендодатель"])}</li>'; $('arendodatel').up('span').addClassName('red'); break;
            </g:if><g:else>
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Арендодатель"])}</li>'; $('arendodatel').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Арендодатель"])}</li>'; $('arendodatel').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Арендодателя","Арендодатель"])}</li>'; $('arendodatel').addClassName('red'); break;
            </g:else>
              case 7: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Основной договор"])}</li>'; $('mainagr_id').addClassName('red'); break;
              case 8: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Кол-во контейнеров"])}</li>'; $('contcol').addClassName('red'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Срок пролонгации"])}</li>'; $('prolongterm').addClassName('red'); break;
              case 10: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип помещений"])}</li>'; $('spacetype_id').addClassName('red'); break;
              case 11: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Комментарий"])}</li>'; $('comment').addClassName('red'); break;
              case 12: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Адрес"])}</li>'; $('fulladdress').addClassName('red'); break;
              case 13: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Номер договора"])}</li>'; $('anumber').addClassName('red'); break;
              case 14: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата договора"])}</li>'; $('adate').up('span').addClassName('k-error-colored'); break;
              case 15: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок действия"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 16: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Срок действия"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 17: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата ежемесячного платежа"])}</li>'; $('payterm').addClassName('red'); break;
              case 18: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Срок уведомления"])}</li>'; $('monthnotification').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(${space?1:0}){
          location.reload(true);
        } else if(e.responseJSON.space){
          location.assign('${createLink(controller:controllerName,action:'space')}'+'/'+e.responseJSON.space);
        } else
          location.assign('${createLink(controller:controllerName,action:'index')}');
      }
      function processaddpayrequestResponse(e){
        var sErrorMsg = '';
        ['payrequest_summa'].forEach(function(ids){
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
      function processAddspacedopagrResponse(e){
        var sErrorMsg = '';
        ['spacedopagr_anumber','spacedopagr_rate','spacedopagr_ratedop','spacedopagr_payterm'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['spacedopagr_adate','spacedopagr_startdate','spacedopagr_enddate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Номер соглашения"])}</li>'; $('spacedopagr_anumber').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата соглашения"])}</li>'; $('spacedopagr_adate').up('span').addClassName('k-error-colored'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата начала"])}</li>'; $('spacedopagr_startdate').up('span').addClassName('k-error-colored'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата окончания"])}</li>'; $('spacedopagr_enddate').up('span').addClassName('k-error-colored'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма платежа"])}</li>'; $('spacedopagr_rate').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Доп. платеж"])}</li>'; $('spacedopagr_ratedop').addClassName('red'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата платежа"])}</li>'; $('spacedopagr_payterm').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorspacedopagrlist").innerHTML=sErrorMsg;
          $("errorspacedopagrlist").up('div').show();
        } else
          jQuery('#spacedopagrAddForm').slideUp(300, function() { getDopAgrs(); });
      }
      function processAddspacebankcheckResponse(e){
        var sErrorMsg = '';
        ['spacebankcheck_bankname','spacebankcheck_checktype_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['spacebankcheck_checkdate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк"])}</li>'; $('spacebankcheck_bankname').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Банк"])}</li>'; $('spacebankcheck_bankname').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.multiple.bikchoice.message",args:["Банк","Банк"])}</li>'; $('spacebankcheck_bankname').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип проверки"])}</li>'; $('spacebankcheck_checktype_id').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата проверки"])}</li>'; $('spacebankcheck_checkdate').up('span').addClassName('k-error-colored'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorspacebankchecklist").innerHTML=sErrorMsg;
          $("errorspacebankchecklist").up('div').show();
        } else
          jQuery('#spacebankcheckAddForm').slideUp(300, function() { getBankchecks(); });
      }
      function processAddspacecalculationResponse(e){
        var sErrorMsg = '';
        ['spacecalc_summa'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['spacecalc_maindate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Сумма платежа"])}</li>'; $('spacecalc_summa').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Период"])}</li>'; $('spacecalc_maindate').up('span').addClassName('k-error-colored'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.not.unique2.message",args:["Начисление","периодом"])}</li>'; $('spacecalc_maindate').up('span').addClassName('k-error-colored'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorspacecalculationlist").innerHTML=sErrorMsg;
          $("errorspacecalculationlist").up('div').show();
        } else
          jQuery('#spacecalculationAddForm').slideUp(300, function() { getCalcs(); });
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
          case 0: getPayrequests();break;
          case 1: getPayments();break;
          case 2: getDopAgrs();break;
          case 3: getBankchecks();break;
          case 4: getServAgrs();break;
          case 5: getServPayments();break;
          case 6: getCalcs();break;
          case 7: getHistory();break;
        }
      }
      function getPayments(){
        if(${space?1:0}) $('payments_submit_button').click();
      }
      function getPayrequests(){
        if(${space?1:0}) $('payrequests_submit_button').click();
      }
      function getDopAgrs(){
        if(${space?1:0}) $('dopagrs_submit_button').click();
      }
      function getBankchecks(){
        if(${space?1:0}) $('bankchecks_submit_button').click();
      }
      function getServAgrs(){
        if(${space?1:0}) $('servagrs_submit_button').click();
      }
      function getServPayments(){
        if(${space?1:0}) $('servpayments_submit_button').click();
      }
      function getCalcs(){
        if(${space?1:0}) $('calcs_submit_button').click();
      }
      function getHistory(){
        if(${space?1:0}) $('history_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
        new Autocomplete('arendator', {
          serviceUrl:'${resource(dir:"autocomplete",file:"space_arendator_autocomplete")}',
          onSelect: function(value, data){
            var lsData=data.split(';');
            $('arendator_address').value=lsData[0];
          }
        });
        new Autocomplete('arendodatel', {
          serviceUrl:'${resource(dir:"autocomplete",file:"space_arendodatel_autocomplete")}',
          onSelect: function(value, data){
            $('arendodatel').focus();
          }
        });
        jQuery("#adate").mask("99.99.9999",{placeholder:" "});
        jQuery("#enddate").mask("99.99.9999",{placeholder:" "});
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
    <h3 class="fleft"><g:if test="${space}">Арендный договор №${space.anumber} (${space.id} | ${String.format('%td.%<tm.%<tY %<tT',space.inputdate)})</g:if><g:else>Новый арендный договор</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку аренд</a>
    <div class="clear"></div>
    <g:formRemote name="requestDetailForm" url="${[action:'updatespace',id:space?.id?:0]}" method="post" onSuccess="processResponse(e)">

    <g:if test="${space?.modstatus==0}">
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

    <g:if test="${space}">
      <label for="status" disabled>Статус:</label>
      <g:select name="status" value="${space.modstatus}" from="['Архив','Активный','Закрыт досрочно']" keys="[0,1,-1]" disabled="true"/>
      <label for="paystatus" disabled>Платеж</label>
      <g:select name="paystatus" value="${space.paystatus}" from="['Невозможен','Возможен']" keys="[0,1]" disabled="true"/>
      <label for="debt" disabled>Долг арендатора:</label>
      <input type="text" id="debt" disabled value="${number(value:debt.maindebt)}" />
      <label for="addpayment_debt" disabled>По доп услугам:</label>
      <input type="text" id="addpayment_debt" disabled value="${number(value:debt.dopdebt)}" />
      <hr class="admin" />
    </g:if>

      <label for="arendator"><g:if test="${space}"><g:link controller="company" action="detail" id="${space.arendator}" target="_blank">Арендатор:</g:link></g:if><g:else>Арендатор:</g:else></label>
      <input type="text" id="arendator" ${space?'disabled':''} name="arendator" value="${arendator?.name}"/>
      <div id="space_arendator_autocomplete" class="autocomplete" style="display:none"></div>
      <label for="arendodatel"><g:if test="${space}"><g:link controller="company" action="detail" id="${space.arendodatel}" target="_blank">Арендодатель:</g:link></g:if><g:else>Арендодатель:</g:else></label>
    <g:if test="${iscanaddcompanies}">
      <span class="input-append">
        <input type="text" class="nopad normal" ${space?'disabled':''} id="arendodatel" name="arendodatel" value="${arendodatel?.name}" onblur="getBankList();getMainagr();"/>
        <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
      </span>
    </g:if><g:else>
      <input type="text" id="arendodatel" ${space?'disabled':''} name="arendodatel" value="${arendodatel?.name}" onblur="getBankList();getMainagr();"/>
    </g:else>
      <div id="space_arendodatel_autocomplete" class="autocomplete" style="display:none"></div>
      <label for="asort">Класс аренды</label>
      <g:select name="asort" value="${inrequest?.company_id?(inrequest?.asort?1:0):space?.asort}" from="['Аренда','Субаренда']" keys="[1,0]" onchange="toggleMainagr(this.value)"/>
      <span id="mainagr" style="${(inrequest?.company_id?(inrequest?.asort?1:0):space?.asort)!=0?'display:none':''}"><label for="mainagr_id">Основной договор:</label>
      <g:select name="mainagr_id" value="${space?.mainagr_id?:0}" from="${agrs}" optionKey="id" noSelection="${['0':space?'не выбран':'арендодатель не указан']}" onchange="copyAddress(this.options[this.selectedIndex].text)"/></span>
      <span id="nosubrenting" style="${(inrequest?.company_id?(inrequest?.asort?1:0):space?.asort)==0?'display:none':''}">
        <label for="is_nosubrenting">Места субаренды:</label>
        <g:select name="is_nosubrenting" value="${space?.subspaceqty}" from="${1..25}" noSelection="${['0':'Без права субаренды']}" onchange="toggleSubwritten(this.value)"/>
      </span>

      <hr class="admin" />

      <label for="anumber">Номер договора:</label>
      <input type="text" id="anumber" name="anumber" value="${space?.anumber}"/>
      <label class="auto" for="is_subwritten" style="${space?.subspaceqty>0?'':'display:none'}">
        <input type="checkbox" id="is_subwritten" name="is_subwritten" value="1" <g:if test="${space?.is_subwritten}">checked</g:if> />
        Субаренда с письменного разрешения
      </label>
      <br/><label for="adate">Дата договора:</label>
      <g:datepicker class="normal nopad" name="adate" value="${space?.adate?String.format('%td.%<tm.%<tY',space.adate):''}"/>
      <label for="enddate">Срок действия:</label>
      <g:datepicker class="normal nopad" name="enddate" value="${space?.enddate?String.format('%td.%<tm.%<tY',space.enddate):''}"/>
      <label class="auto" for="is_nopayment">
        <input type="checkbox" id="is_nopayment" name="is_nopayment" value="1" <g:if test="${space?.is_nopayment}">checked</g:if> />
        Без оплаты
      </label><br/>
      <label for="prolongcondition">Пролонгация</label>
      <g:select name="prolongcondition" value="${space?.prolongcondition}" from="['без пролонгации','по доп соглашению','автоматически','с уведомлением']" keys="0123" onchange="toggleProlongData(this.value)"/>
      <span id="prolongtermsection" style="${space?.prolongcondition!=2?'display:none':''}"><label for="prolongterm">Срок пролонгации<br/><small>в месяцах</small></label>
      <input type="text" id="prolongterm" name="prolongterm" value="${space?.prolongterm?:11}"/></span>
      <span id="monthnotificationsection" style="${space?.prolongcondition!=3?'display:none':''}"><label for="monthnotification">Срок уведомления<br/><small>в месяцах</small></label>
      <input type="text" id="monthnotification" name="monthnotification" value="${space?.monthnotification?:1}"/></span>

      <hr class="admin" />

      <label for="fulladdress">Адрес:</label>
      <input class="fullline" type="text" id="fulladdress" name="fulladdress" value="${space?.fulladdress}" onblur="copyadr(this.value)"/>
      <label for="shortaddress">Короткий адрес:</label>
      <input class="fullline" type="text" id="shortaddress" name="shortaddress" value="${space?.shortaddress}"/>
      <label for="arendator_address">Адрес арендатора:</label>
      <input style="width:585px" type="text" id="arendator_address" name="arendator_address" value="${arendator?.legaladr}" disabled/>
      <label class="auto" for="is_adrsame">
        <input type="checkbox" id="is_adrsame" name="is_adrsame" value="2" <g:if test="${space?.is_adrsame}">checked</g:if> />
        Совп.
      </label>
      <label for="area">Площадь:</label>
      <input type="text" id="area" name="area" value="${space?.area}"/>
      <label class="auto" for="is_territory">
        <input type="checkbox" id="is_territory" name="is_territory" value="1" <g:if test="${space?.is_territory}">checked</g:if> onclick="toggleTerarea()"/>
        С территорией
      </label><br/>
      <label for="ratemeter">Цена за метр:</label>
      <input type="text" id="ratemeter" name="ratemeter" value="${number(value:space?.ratemeter)}"/>
      <span id="terareasection" style="${!space?.is_territory?'display:none':''}"><label for="terarea">Пл-дь территории:</label>
      <input type="text" id="terarea" name="terarea" value="${space?.terarea}"/></span>
      <br/><label for="rate">Арендная плата:</label>
      <input type="text" id="rate" name="rate" value="${number(value:space?.rate)}"/>
      <label for="actrate">Факт. плата:</label>
      <input type="text" id="actrate" name="actrate" value="${number(value:space?.actrate)}"/>
      <label for="is_addpayment">Доп. услуги:</label>
      <g:select name="is_addpayment" value="${space?.is_addpayment}" from="['Нет','Да']" keys="[0,1]" onchange="toggleRatedop(this.value)"/>
      <span id="ratedopsection" style="${space?.is_addpayment!=1?'display:none':''}"><label for="ratedop">Плата за д. услуги:</label>
      <input type="text" id="ratedop" name="ratedop" value="${number(value:space?.ratedop)}"/></span><br/>
      <label for="spacetype_id">Тип помещений:</label>
      <g:select name="spacetype_id" value="${space?.spacetype_id?:0}" from="${spacetypes}" optionValue="name" optionKey="id" noSelection="${['0':'не выбрано']}" onchange="toggleContcol(this.value)"/>
      <span id="contcolsection" style="${space?.spacetype_id!=5?'display:none':''}"><label for="contcol">Кол-во конт.:</label>
      <input type="text" id="contcol" name="contcol" value="${space?.contcol}"/></span><br/>
      <label for="paycondition">Условие срока</label>
      <g:select name="paycondition" value="${space?.paycondition}" from="['оплатить не позднее','оплатить до']" keys="12"/>
      <label for="payterm">Дата:<br/><small>ежемесячного платежа</small></label>
      <input type="text" id="payterm" name="payterm" value="${space?.payterm}"/>
      <label for="paytermcondition">Условия оплаты</label>
      <g:select name="paytermcondition" value="${space?.paytermcondition}" from="['за прошлый месяц','за текущий месяц','за следующий месяц']" keys="${1..3}"/>
    <g:if test="${iscantag}">
      <label class="auto" for="is_noexpense">
        <input type="checkbox" id="is_noexpense" name="is_noexpense" value="1" <g:if test="${space?.is_noexpense}">checked</g:if> />
        Без доходов-расходов
      </label>
    </g:if>
      <br/><label for="project_id">Проект:</label>
      <g:select name="project_id" value="${space?.project_id}" from="${projects}" optionValue="name" optionKey="id" noSelection="${['0':'не выбрано']}"/>
      <label for="responsible">Ответственный:</label>
      <g:select name="responsible" value="${space?.responsible?:session.user.id}" from="${users}" optionValue="pers_name" optionKey="id" noSelection="${['0':'не выбрано']}"/><br/>
      <label for="bank_id">Банк:</label>
      <span id="banklist"><g:select class="auto" name="bank_id" value="${space?.bank_id?:''}" from="${banks}" optionValue="name" optionKey="id" noSelection="${['':arendodatel?'не выбран':'арендодатель не указан']}"/></span><br/>
      <label for="description">Описание предмета договора:</label>
      <g:textArea name="description" id="description" value="${space?.description}" />
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" id="comment" value="${space?.comment}" />

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
        <input type="button" class="spacing" value="${space?.modstatus==-1?'Восстановить':'Сохранить'}" onclick="submitForm(1)"/>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      </g:if>
      <g:if test="${space?.modstatus>-1&&isCanDelete}">
        <input type="button" class="spacing reset" value="Закрыть досрочно" onclick="deleteAgr()" />
      </g:if>
      </div>
      <input type="hidden" id="modstatus" name="modstatus" value="1"/>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${space}">
    <div class="tabs">
      <ul class="nav">
        <li><a href="javascript:void(0)" onclick="viewCell(0)">Платежи</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(1)">Платежи по выписке</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(2)">Доп. соглашения</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(3)">Банковские проверки</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(4)">Договора услуг</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(5)">Платежи услуг</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(6)">Начисления</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(7)">История</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="paymentsForm" url="[action:'sppayments',id:space.id]" update="details">
      <input type="submit" class="button" id="payments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="payrequestsForm" url="[action:'sppayrequests',id:space.id]" update="details">
      <input type="submit" class="button" id="payrequests_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="dopagrsForm" url="[action:'spacedopagrs',id:space.id]" update="details">
      <input type="submit" class="button" id="dopagrs_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="bankchecksForm" url="[action:'spacebankchecks',id:space.id]" update="details">
      <input type="submit" class="button" id="bankchecks_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="dopagrsForm" url="[action:'spaceservagrs',id:space.id]" update="details">
      <input type="submit" class="button" id="servagrs_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="dopagrsForm" url="[action:'spaceservpayments',id:space.id]" update="details">
      <input type="submit" class="button" id="servpayments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="calcsForm" url="[action:'spacecalculations',id:space.id]" update="details">
      <input type="submit" class="button" id="calcs_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="historyForm" url="[action:'spacehistory',id:space.id]" update="details">
      <input type="submit" class="button" id="history_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>