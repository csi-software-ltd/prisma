<html>
  <head>
    <title>Prisma: <g:if test="${agentagr}">Агентский договор "${agentagr.name}"</g:if><g:else>Новый агентский договор</g:else></title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript>
      var newagentnumber = 0;
      var newaddbanknumber = 0;
      var iTaxoption_id = 0;
      function returnToList(){
        $("returnToListForm").submit();
      }
      function updatebank(sBankId){
        if($('bankkredits_bank_id')){
          $('bankkredits_client_id').value = 0
          $('bankkredits_bank_id').value = sBankId
          $('bankkredit_submit_button').click();
        }
      }
      function updateclient(sClient){
        if($('bankkredits_client_id')){
          $('bankkredits_client_id').value = sClient
          $('bankkredit_submit_button').click();
        }
      }
      function updateagentnumber(){
        if($('agentkredit_newagentnumber'))
          $('agentkredit_newagentnumber').value=newagentnumber;
      }
      function updateaddbanknumber(){
        $('newaddbanknumber').value=newaddbanknumber;
      }
      function getsubagenttemplate(){
        newagentnumber++;
        <g:remoteFunction controller='agreement' action='subagenttemplate' params="'agnumber='+newagentnumber" onSuccess="processAgentTemplate(e)" />
      }
      function getagenttemplate(){
        newagentnumber++;
        <g:remoteFunction controller='agreement' action='agenttemplate' params="'agnumber='+newagentnumber" onSuccess="processAgentTemplate(e)" />
      }
      function processAgentTemplate(e){
        jQuery('#agentrateslist').append(e.responseText);
      }
      function hidenode(el){
        var li = el.up('li');
        jQuery(li).fadeOut(400, function() { li.parentNode.removeChild(li); })
      }
      function getaddbanktemplate(){
        newaddbanknumber++;
        <g:remoteFunction controller='agreement' action='agentaddbanktemplate' params="'agr_id=${agentagr?.id?:0}&banknumber='+newaddbanknumber" onSuccess="processAddbankTemplate(e)" />
      }
      function processAddbankTemplate(e){
        jQuery('#addbankslist').append(e.responseText);
      }
      function setPeriodstatus(iStatus){
        $('period_status').value=iStatus;
        $('periods_submit_button').click();
      }
      function setAgPaymenttype(iStatus){
        $('agpayment_type').value=iStatus;
        $('agpayments_submit_button').click();
      }
      function setClPaymenttype(iStatus){
        $('clpayment_type').value=iStatus;
        $('clpayments_submit_button').click();
      }
      function showdetails(el,e){
        jQuery(el).next("tr").children("td").children("div").html(e.responseText);
        jQuery(el).next("tr").children("td").children("div").slideDown(400)
        jQuery(el).next("tr").siblings("tr.detail").find("td > div:visible").slideUp(400);
        jQuery(el).addClass("current");
        jQuery(el).siblings("tr").removeClass("current");
      }
      function getdetail(el,iId){
        <g:remoteFunction controller='agreement' action='agentrateplan' id="${agentagr?.id}" params="'agent_id='+iId" onSuccess="showdetails(el,e)" />
      }
      function getBankList(iClientId){
        <g:remoteFunction controller='agreement' action='agentbanklist' params="'client_id='+iClientId" update="banklist" />
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['name','client_id','bank_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Название"])}</li>'; $('name').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Клиент"])}</li>'; $('client_id').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк"])}</li>'; $('bank_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${agentagr?1:0}){
          location.reload(true);
        } else if(e.responseJSON.agentagr){
          location.assign('${createLink(controller:controllerName,action:'agent')}'+'/'+e.responseJSON.agentagr);
        } else
          location.assign('${createLink(controller:controllerName,action:'index')}');
      }
      function processAddkreditResponse(e){
        var sErrorMsg = '';
        ['agentkredit_kredit_id','bankkredit_bank_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        for(var i=1;i<=newagentnumber;i++){
          if($('agentrates_agent_id_new'+i))
            $('agentrates_agent_id_new'+i).removeClassName('red');
        }
        if(e.responseJSON.errorcode.length||e.responseJSON.newagenterrorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Кредит"])}</li>'; $('agentkredit_kredit_id').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Банк"])}</li>'; $('bankkredit_bank_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          if(e.responseJSON.newagenterrorcode.length)
            sErrorMsg+='<li>${message(code:"error.not.unique.agent.message",args:["Агент"])}</li>';
          e.responseJSON.newagenterrorcode.forEach(function(err){
              $('agentrates_agent_id_new'+err).addClassName('red');
          });
          $("errorkreditlist").innerHTML=sErrorMsg;
          $("errorkreditlist").up('div').show();
        } else
          jQuery('#agentkreditAddForm').slideUp(300, function() { $('agentkredits_submit_button').click(); });
      }
      function processcomputeoldperiodResponse(e){
        jQuery('#periodDateForm').slideUp(300, function() { getPeriods(); });
      }
      function processupdateperiodResponse(e){
        var sErrorMsg = '';
        ['agentperiod_dateend'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Дата окончания периода"])}</li>'; $('agentperiod_dateend').up('span').addClassName('k-error-colored'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorperiodlist").innerHTML=sErrorMsg;
          $("errorperiodlist").up('div').show();
        } else
          jQuery('#periodUpdateForm').slideUp(300, function() { getPeriods(); });
      }
      function processupdateaddperiodResponse(e){
        var sErrorMsg = '';
        ['agentaddperiod_period_id','agentaddperiod_calcrate'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Общий процент по периоду"])}</li>'; $('agentaddperiod_calcrate').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Период"])}</li>'; $('agentaddperiod_period_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("erroraddperiodlist").innerHTML=sErrorMsg;
          $("erroraddperiodlist").up('div').show();
        } else
          jQuery('#addperiodUpdateForm').slideUp(300, function() { getPeriods(); });
      }
      function processaddfixResponse(e){
        var sErrorMsg = '';
        ['agentfix_agent_id','agentfix_summa'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['agentfix_paydate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Агент"])}</li>'; $('agentfix_agent_id').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата проводки"])}</li>'; $('agentfix_paydate').up('span').addClassName('k-error-colored'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('agentfix_summa').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Сумма"])}</li>'; $('agentfix_summa').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorfixlist").innerHTML=sErrorMsg;
          $("errorfixlist").up('div').show();
        } else
          jQuery('#fixaddForm').slideUp(300, function() { getAgentfixes(); });
      }
      function processaddclfixResponse(e){
        var sErrorMsg = '';
        ['clientfix_summa'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['clientfix_paydate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата проводки"])}</li>'; $('clientfix_paydate').up('span').addClassName('k-error-colored'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('clientfix_summa').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Сумма"])}</li>'; $('clientfix_summa').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorclfixlist").innerHTML=sErrorMsg;
          $("errorclfixlist").up('div').show();
        } else
          jQuery('#clfixaddForm').slideUp(300, function() { getClientfixes(); });
      }
      function newCompany(){
        window.open('${createLink(controller:'company',action:'detail')}');
      }
      function openCompany(){
        var iCompany_id = $('agpayment_tocompany_id').value;
        if(iCompany_id)
          window.open('${createLink(controller:'company',action:'detail')}'+'/'+iCompany_id);
      }
      function processaddagpaymentResponse(e){
        var sErrorMsg = '';
        ['agpayment_paycat','agpayment_tobank','agpayment_summa','agpayment_comment','agpayment_agent_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['agpayment_paydate','agpayment_fromcompany','agpayment_tocompany'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата платежа"])}</li>'; $("agpayment_paydate").up('span').addClassName('k-error-colored'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Категория"])}</li>'; $("agpayment_paycat").addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Плат. компания"])}</li>'; $("agpayment_fromcompany").up('span').addClassName('k-error-colored'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Получ. компания"])}</li>'; $("agpayment_tocompany").up('span').addClassName('k-error-colored'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк получателя"])}</li>'; $("agpayment_tobank").addClassName('red'); break;
              case 10: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Агент"])}</li>'; $("agpayment_agent_id").addClassName('red'); break;
              case 11: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $("agpayment_summa").addClassName('red'); break;
              case 12: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Комментарий"])}</li>'; $("agpayment_comment").addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("erroraddagpaymentlist").innerHTML=sErrorMsg;
          $("erroraddagpaymentlist").up('div').show();
        } else {
          jQuery('#agpaymentaddForm').slideUp(300, function() { getAgentPayments(); });
        }
      }
      function getBankByCompany(){
        var iCompany_id = $('agpayment_tocompany_id').value;
        if(iCompany_id)
          <g:remoteFunction controller='agreement' action='agpaymentbanklist' params="'company_id='+iCompany_id" update="agpayment_tobank_span" onSuccess="\$('agpayment_bank_div').show()"/>;
      }
      function setSummaNds(iSumma){
        var nds = ${nds?:0};
        if(iSumma>0 && nds>0 && iTaxoption_id>0)
          $("agpayment_summands").value = (nds * iSumma / (100 + nds)).toFixed(2);
        else
          $("agpayment_summands").value = 0;
      }
      function selectAgreement(iValue){
        if(iValue==-1)
          iValue = $('agreementtype_id').value;
        <g:remoteFunction controller='agreement' action='agpaymentagrlist' params="'agreementtype_id='+iValue" update="agpayment_agreement_span" />
      }
      function togglePaycat(iVal){
        $('agpayment_paycat_agr').hide();
        $("agpayment_comment_span").hide();
        switch(iVal){
          case '1': $('agpayment_paycat_agr').show();break;
          case '4': $("agpayment_comment_span").show();break;
        }
      }
      function submitForm(iStatus){
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
          case 0: getKredits();break;
          case 1: getPeriods();break;
          case 2: getClientActs();break;
          case 3: getAgents();break;
          case 4: getAgentratePeriods();break;
          case 5: getAgentfixes();break;
          case 6: getAgentProfit();break;
          case 7: getClientPayments();break;
          case 8: getAgentPayments();break;
          case 9: getClientfixes();break;
        }
      }
      function getKredits(){
        if(${agentagr?1:0}) $('agentkredits_submit_button').click();
      }
      function getClientPayments(){
        if(${agentagr?1:0}) $('clpayments_submit_button').click();
      }
      function getPeriods(){
        if(${agentagr?1:0}) $('periods_submit_button').click();
      }
      function getAgentPayments(){
        if(${agentagr?1:0}) $('agpayments_submit_button').click();
      }
      function getAgents(){
        if(${agentagr?1:0}) $('agents_submit_button').click();
      }
      function getAgentratePeriods(){
        if(${agentagr?1:0}) $('agentrateperiods_submit_button').click();
      }
      function getClientActs(){
        if(${agentagr?1:0}) $('clientacts_submit_button').click();
      }
      function getAgentfixes(){
        if(${agentagr?1:0}) $('agentfixes_submit_button').click();
      }
      function getAgentProfit(){
        if(${agentagr?1:0}) $('agentprofit_submit_button').click();
      }
      function getClientfixes(){
        if(${agentagr?1:0}) $('clientfixes_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
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
    <h3 class="fleft"><g:if test="${agentagr}">Агентский договор "${agentagr.name}"</g:if><g:else>Новый агентский договор</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку договоров</a>
    <div class="clear"></div>
    <g:formRemote name="requestDetailForm" before="updateaddbanknumber()" url="${[action:'updateagent',id:agentagr?.id?:0]}" method="post" onSuccess="processResponse(e)">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

    <g:if test="${agentagr}">
      <label for="agentagr_id" disabled>Код договора:</label>
      <input type="text" id="agentagr_id" disabled value="${agentagr.id}" />
      <label for="inputdate" disabled>Дата создания:</label>
      <input type="text" name="inputdate" disabled value="${String.format('%td.%<tm.%<tY %<tT',agentagr.inputdate)}" />
      <label for="modstatus" disabled>Статус:</label>
      <g:select name="modstatus" value="${agentagr.modstatus}" from="['Архив','Активный']" keys="[0,1]" disabled="true"/>
      <label for="saldo" disabled>Остаток средств:</label>
      <input type="text" id="saldo" disabled value="${number(value:saldo)}" />
    <g:if test="${mainclient}">
      <label for="mainclient" disabled>Основной клиент:</label>
      <input type="text" id="mainclient" disabled value="${mainclient.name}" />
    </g:if>
      <hr class="admin" />
    </g:if>

      <label for="name">Название:</label>
      <input type="text" id="name" name="name" value="${agentagr?.name}"/>
      <label for="client_id">Клиент:</label>
      <g:select name="client_id" value="${agentagr?.client_id}" from="${clients}" optionValue="name" optionKey="id" noSelection="${['0':'не выбрано']}" onchange="getBankList(this.value)" disabled="${isHaveCredit}"/>
      <label for="bank_id">Банк:</label>
      <span id="banklist"><g:select class="fullline" name="bank_id" from="${banks}" value="${agentagr?agentagr.bank_id:''}" optionValue="name" optionKey="id" noSelection="${['':agentagr?'не выбран':'клиент не указан']}" disabled="${isHaveCredit}"/></span>
    <g:if test="${agentagr}">
      <ul id="addbankslist">
      <g:each in="${addbankslist}" var="addbank">
        <li style="width:905px;">
          <div style="width:830px;float:left;">
            <label for="addbank_id_${addbank.id}">Доп. банк:</label>
            <g:select style="width:600px" name="addbank_id_${addbank.id}" from="${banks}" value="${addbank?.bank_id}" optionValue="name" optionKey="id" noSelection="${['':'не выбран']}" disabled="true"/>
          </div>
          <div style="float:right;width:75px;height:51px;">
          <g:if test="${iscanedit&&!addbankskredits[addbank.id]}">
            <g:remoteLink class="button" url="${[controller:'agreement',action:'deleteaddbank',id:addbank.id]}" title="Удалить" after="hidenode(this)"><i class="icon-trash icon-large"></i></g:remoteLink>
          </g:if>
          </div>
        </li>
      </g:each>
      </ul>
      <input type="button" style="width:885px;margin-left:15px" class="spacing" value="Добавить банк" onclick="getaddbanktemplate()"/>
    </g:if>
      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
      <g:if test="${agentagr}">
        <a class="button" href="${createLink(controller:'agreement',action:'agentagrbalance',id:agentagr.id)}" target="_blank">
          Сверка балаланса &nbsp;<i class="icon-angle-right icon-large"></i>
        </a>
      </g:if>
      <g:if test="${agentagr&&iscanedit&&!isHaveCredit}">
        <g:remoteLink class="button" url="${[controller:'agreement',action:'deleteagentagr',id:agentagr.id]}" onSuccess="returnToList()">Удалить договор &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink>
      </g:if>
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(1)"/>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      </g:if>
      </div>
      <input type="hidden" id="newaddbanknumber" name="newaddbanknumber" value="0"/>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${agentagr}">
    <div class="tabs">
      <ul class="nav">
        <li><a href="javascript:void(0)" onclick="viewCell(0)">Договора</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(1)">Расчеты</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(2)">Сверки</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(3)">Агенты</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(4)">Агентские периоды</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(5)">Корректировки</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(6)">Доходы</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(7)">Кл. платежи</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(8)">Аг. платежи</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(9)">Списания</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="agentkreditsForm" url="[action:'agentkredits',id:agentagr.id]" update="details">
      <input type="submit" class="button" id="agentkredits_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="agentperiodsForm" url="[action:'agentperiods',id:agentagr.id]" update="details">
      <input type="submit" class="button" id="periods_submit_button" value="Показать" style="display:none" />
      <input type="hidden" id="period_status" name="modstatus" value="0"/>
    </g:formRemote>
    <g:formRemote name="agentsForm" url="[action:'agents',id:agentagr.id]" update="details">
      <input type="submit" class="button" id="agents_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="agentrateperiodsForm" url="[action:'agentrateperiods',id:agentagr.id]" update="details">
      <input type="submit" class="button" id="agentrateperiods_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="clientactsForm" url="[action:'clientacts',id:agentagr.id]" update="details">
      <input type="submit" class="button" id="clientacts_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="agentfixesForm" url="[action:'agentfixes',id:agentagr.id]" update="details">
      <input type="submit" class="button" id="agentfixes_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="agentprofitForm" url="[action:'agentprofit',id:agentagr.id]" update="details">
      <input type="submit" class="button" id="agentprofit_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="clpaymentsForm" url="[action:'agclientpayments',id:agentagr.id]" update="details">
      <input type="submit" class="button" id="clpayments_submit_button" value="Показать" style="display:none" />
      <input type="hidden" id="clpayment_type" name="type" value="0"/>
    </g:formRemote>
    <g:formRemote name="agpaymentsForm" url="[action:'agagentpayments',id:agentagr.id]" update="details">
      <input type="submit" class="button" id="agpayments_submit_button" value="Показать" style="display:none" />
      <input type="hidden" id="agpayment_type" name="type" value="0"/>
    </g:formRemote>
    <g:formRemote name="clientfixesForm" url="[action:'clientfixes',id:agentagr.id]" update="details">
      <input type="submit" class="button" id="clientfixes_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
