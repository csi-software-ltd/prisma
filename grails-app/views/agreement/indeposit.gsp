<html>
  <head>
    <title>Prisma: <g:if test="${deposit}">Договор депозита в холдинг №${deposit.id}</g:if><g:else>Новый договор депозита в холдинг</g:else></title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function closeAgr(){
        if(confirm('Вы уверены, что хотите досрочно закрыть договор депозита №${deposit?.anumber}?')) { submitForm(0) }
      }
      function clickPaginate(event){
        event.stop();
        var link = event.element();
        if(link.href == null){
          return;
        }
        new Ajax.Updater(
          { success: $('ajax_wrap') },
          link.href,
          { evalScripts: true });
      }
      function processResponse(e){
        $("saveinfo").up('div').hide();
        $("errorlist").up('div').hide();
        var sErrorMsg = '';
        ['client_id','summa','rate','comrate'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['adate','enddate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк"])}</li>'; $('client_id').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата договора"])}</li>'; $('adate').up('span').addClassName('k-error-colored'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('summa').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Ставка"])}</li>'; $('rate').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок депозита"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Процент комиссии"])}</li>'; $('comrate').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(${deposit?1:0}){
          $("saveinfo").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(e.responseJSON.deposit){
          location.assign('${createLink(controller:controllerName,action:'indeposit')}'+'/'+e.responseJSON.deposit);
        } else
          returnToList();
      }
      function processAddindepositprjoperationResponse(e){
        var sErrorMsg = '';
        ['indepositprjoperation_project_from','indepositprjoperation_project_to','indepositprjoperation_summa'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['indepositprjoperation_operationdate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Проект откуда"])}</li>'; $('indepositprjoperation_project_from').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Проект куда"])}</li>'; $('indepositprjoperation_project_to').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('indepositprjoperation_summa').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата операции"])}</li>'; $('indepositprjoperation_operationdate').up('span').addClassName('k-error-colored'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.invalid.max.message",args:["Сумма","текущая сумма депозита на проекте"])}</li>'; $('indepositprjoperation_summa').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorindepositprjoperationlist").innerHTML=sErrorMsg;
          $("errorindepositprjoperationlist").up('div').show();
        } else
          jQuery('#indepositprjoperationAddForm').slideUp(300, function() { getOperations(); });
      }
      function processAddindepositdopagrResponse(e){
        var sErrorMsg = '';
        ['indepositdopagr_nomer','indepositdopagr_summa','indepositdopagr_rate','indepositdopagr_comrate'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['indepositdopagr_dsdate','indepositdopagr_startdate','indepositdopagr_enddate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('indepositdopagr_summa').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Ставка"])}</li>'; $('indepositdopagr_rate').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата соглашения"])}</li>'; $('indepositdopagr_dsdate').up('span').addClassName('k-error-colored'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата начала"])}</li>'; $('indepositdopagr_startdate').up('span').addClassName('k-error-colored'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата окончания"])}</li>'; $('indepositdopagr_enddate').up('span').addClassName('k-error-colored'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Процент комиссии"])}</li>'; $('indepositdopagr_comrate').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorindepositdopagrlist").innerHTML=sErrorMsg;
          $("errorindepositdopagrlist").up('div').show();
        } else
          jQuery('#indepositdopagrAddForm').slideUp(300, function() { getDopAgrs(); });
      }
      function submitForm(iStatus){
        $('modstatus').value = iStatus
        $('submit_button').click();
      }
      function setProjectId(iStatus){
        $('prjoperations_project_id').value=iStatus;
        getOperations();
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
          case 0: getPayments();break;
          case 1: getCash();break;
          case 2: getOperations();break;
          case 3: getProjects();break;
          case 4: getDopAgrs();break;
          case 5: getHistory();break;
        }
      }
      function getPayments(){
        if(${deposit?1:0}) $('payrequests_submit_button').click();
      }
      function getCash(){
        if(${deposit?1:0}) $('cash_submit_button').click();
      }
      function getOperations(){
        if(${deposit?1:0}) $('prjoperations_submit_button').click();
      }
      function getProjects(){
        if(${deposit?1:0}) $('projects_submit_button').click();
      }
      function getDopAgrs(){
        if(${deposit?1:0}) $('dopagrs_submit_button').click();
      }
      function getHistory(){
        if(${deposit?1:0}) $('history_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
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
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку депозитов</a>
    <h3 class="fleft"><g:if test="${deposit}">Договор депозита в холдинг №${deposit.id} (${String.format('%td.%<tm.%<tY %<tT',deposit.inputdate)})</g:if><g:else>Новый договор депозита в холдинг</g:else></h3>
    <div class="clear"></div>
    <g:formRemote name="depositDetailForm" url="${[action:'updateindeposit',id:deposit?.id?:0]}" method="post" onSuccess="processResponse(e)">

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

    <g:if test="${deposit}">
      <label for="aclass" disabled>Класс:</label>
      <g:select name="aclass" value="${deposit.aclass}" from="['безналичный','наличный']" keys="12" disabled="true"/>
      <label for="client" disabled>Клиент:</label>
      <input type="text" id="client" disabled name="client" value="${client.name}"/>
      <label for="status" disabled>Статус:</label>
      <g:select name="status" value="${deposit.modstatus}" from="['Архив','Активный']" keys="${0..1}" disabled="true"/>
      <label for="bodydebt" disabled>Текущий депозит:</label>
      <input type="text" id="bodydebt" disabled name="bodydebt" value="${number(value:bodydebt)}"/>
      <label for="percdate" disabled>Дата процентов:</label>
      <input type="text" name="percdate" disabled value="${percentdebt.percdate?String.format('%td.%<tm.%<tY',percentdebt.percdate):''}" />
      <label for="percdebt" disabled>Сумма процентов:</label>
      <input type="text" id="percdebt" disabled name="percdebt" value="${number(value:percentdebt.percdebt)}"/>

      <hr class="admin" />

    </g:if><g:else>
      <label for="aclass">Класс:</label>
      <g:select name="aclass" value="" from="['безналичный','наличный']" keys="12"/>
      <label for="client_id">Клиент:</label>
      <g:select name="client_id" value="" from="${clients}" optionValue="name" optionKey="id" noSelection="${['0':'не выбран']}"/>

      <hr class="admin" />

    </g:else>

      <label for="anumber">Номер договора:</label>
      <input type="text" id="anumber" name="anumber" ${deposit?'disabled':''} value="${deposit?.anumber}"/>
      <label for="adate">Дата договора:</label>
      <g:datepicker class="normal nopad" name="adate" value="${deposit?.adate?String.format('%td.%<tm.%<tY',deposit.adate):''}" disabled="${deposit?'true':'false'}"/>
      <label for="atype">Тип договора:</label>
      <g:select id="atype" name="atype" value="${deposit?.atype}" from="['Бессрочный','Срочный']" keys="01" disabled="${deposit?'true':'false'}"/>
      <label for="enddate">Срок договора:</label>
      <g:datepicker class="normal nopad" name="enddate" value="${deposit?.enddate?String.format('%td.%<tm.%<tY',deposit.enddate):''}" disabled="${deposit?'true':'false'}"/>
      <label for="summa">Сумма:</label>
      <input type="text" id="summa" name="summa" ${deposit?'disabled':''} value="${number(value:deposit?.summa)}" />
      <label for="valuta_id">Валюта депозита:</label>
      <g:select id="valuta_id" name="valuta_id" from="${Valuta.findAllByModstatus(1)}" value="${deposit?.valuta_id?:857}" optionValue="name" optionKey="id"/>
      <label for="rate">Ставка в годовых:</label>
      <input type="text" id="rate" name="rate" ${deposit?'disabled':''} value="${number(value:deposit?.rate?:24.0g)}"/>
      <label for="comrate">Процент комиссии:</label>
      <input type="text" id="comrate" name="comrate" ${deposit?'disabled':''} value="${number(value:deposit?deposit.comrate:5.0g)}"/>
      <label for="startsaldo">Начальное сальдо:</label>
      <input type="text" id="startsaldo" name="startsaldo" value="${number(value:deposit?.startsaldo)}"/>
      <br/><label for="comment">Комментарий:</label>
      <g:textArea name="comment" id="comment" value="${deposit?.comment}" />
      <input type="hidden" id="modstatus" name="modstatus" value="1"/>
      <input style="display:none" type="submit" id="submit_button" value="Сохранить" />

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="button" class="reset spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
      <g:if test="${deposit?.modstatus==1}">
        <input type="button" class="spacing" value="Досрочно закрыть" onclick="closeAgr()"/>
      </g:if>
      <g:if test="${isCanRestore}">
        <input type="button" class="spacing" value="Восстановить" onclick="submitForm(1)"/>
      </g:if>
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(${deposit?deposit.modstatus:1})"/>
      </g:if>
      </div>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${deposit}">
    <div class="tabs">
      <ul class="nav">
        <li style="${deposit.aclass==2?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(0)">Безналичные платежи</a></li>
        <li style="${deposit.aclass==1?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(1)">Наличные платежи</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(2)">Операции по проектам</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(3)">Проекты</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(4)">Доп. соглашения</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(5)">История</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="paymentsForm" url="[action:'indepositpayments',id:deposit.id]" update="details">
      <input type="submit" class="button" id="payrequests_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="cashForm" url="[action:'indepositcashpayments',id:deposit.id]" update="details">
      <input type="submit" class="button" id="cash_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="operationForm" url="[action:'indepositprjoperations',id:deposit.id]" update="details">
      <input type="submit" class="button" id="prjoperations_submit_button" value="Показать" style="display:none" />
      <input type="hidden" id="prjoperations_project_id" name="project_id" value="0"/>
    </g:formRemote>
    <g:formRemote name="projectsForm" url="[action:'indepositprojects',id:deposit.id]" update="details">
      <input type="submit" class="button" id="projects_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="dopagrsForm" url="[action:'indepositdopagrs',id:deposit.id]" update="details">
      <input type="submit" class="button" id="dopagrs_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="historyForm" url="[action:'indeposithistory',id:deposit.id]" update="details">
      <input type="submit" class="button" id="history_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>