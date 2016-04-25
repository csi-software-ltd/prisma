<html>
  <head>
    <title>Prisma: <g:if test="${cession}">Договор уступки №${cession.anumber}</g:if><g:else>Новый договор уступки</g:else></title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function getcBankList(){
        var companyname = $('cessionary').value
        <g:remoteFunction controller='agreement' action='cessbanklist' params="'companyname='+companyname" update="cbanklist" />
      }
      function getLizingData(sId){
        <g:remoteFunction controller='agreement' action='lizingdata' params="'lizing_id='+sId" onSuccess="processLizingResponse(e)" />
      }
      function getKreditData(sId){
        <g:remoteFunction controller='agreement' action='kreditdata' params="'kredit_id='+sId" onSuccess="processKreditResponse(e)" />
      }
      function toggleAgrSection(iId){
        if (iId=='1') jQuery('#lizingagrsection').slideUp(300, function() { jQuery('#kreditagrsection').slideDown(300) });
        else jQuery('#kreditagrsection').slideUp(300, function() { jQuery('#lizingagrsection').slideDown(300) });
      }
      function processKreditResponse(e){
        if(!e.responseJSON.error){
          $('cedent').value = e.responseJSON.cedent;
          $('debtor').value = e.responseJSON.debtor;
          $('client_id').value = e.responseJSON.client_id!=0 ? e.responseJSON.client_id : -1;
        } else {
          $('cedent').value = "";
          $('debtor').value = "";
          $('client_id').selectedIndex=0;
        }
      }
      function processLizingResponse(e){
        if(!e.responseJSON.error){
          $('lizcedent').value = e.responseJSON.cedent;
          $('lizdebtor').value = e.responseJSON.debtor;
        } else {
          $('lizcedent').value = "";
          $('lizdebtor').value = "";
        }
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['cessionary','kredit_id','cessiontype','anumber','dopagrcomment','cbank_id','lizing_id'].forEach(function(ids){
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
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Цессионарий"])}</li>'; $('cessionary').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Цессионарий"])}</li>'; $('cessionary').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Цессионария","Цессионарий"])}</li>'; $('cessionary').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Кредитный договор"])}</li>'; $('kredit_id').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Кредитный договор"])}</li>'; $('kredit_id').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип договора"])}</li>'; $('cessiontype').addClassName('red'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Номер договора"])}</li>'; $('anumber').addClassName('red'); break;
              case 8: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата договора"])}</li>'; $('adate').up('span').addClassName('k-error-colored'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок действия"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 10: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Срок действия"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 11: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Доп. комментарий"])}</li>'; $('dopagrcomment').addClassName('red'); break;
              case 12: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Цессионарий"])}</li>'; $('cessionary').addClassName('red'); break;
              case 13: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк цессионария"])}</li>'; $('cbank_id').addClassName('red'); break;
              case 14: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Лизинговый договор"])}</li>'; $('lizing_id').addClassName('red'); break;
              case 15: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Лизинговый договор"])}</li>'; $('lizing_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${cession?1:0}){
          location.reload(true);
        } else if(e.responseJSON.cession){
          location.assign('${createLink(controller:controllerName,action:'cession')}'+'/'+e.responseJSON.cession);
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
          case 1: getPayments();break;
          case 2: getHistory();break;
        }
      }
      function getPlanpayment(){
        if(${cession?1:0}) $('planpayments_submit_button').click();
      }
      function getPayments(){
        if(${cession?1:0}) $('payments_submit_button').click();
      }
      function getHistory(){
        if(${cession?1:0}) $('history_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
        new Autocomplete('cessionary', {
          serviceUrl:'${resource(dir:"autocomplete",file:"space_arendodatel_autocomplete")}',
          onSelect: function(value, data){
            $('cessionary').focus();
          }
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
  <body onload="init()">
    <h3 class="fleft"><g:if test="${cession}">Договор уступки №${cession.anumber} (${cession.id} | ${String.format('%td.%<tm.%<tY %<tT',cession.inputdate)})</g:if><g:else>Новый договор уступки</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку цессий</a>
    <div class="clear"></div>
    <g:formRemote name="requestDetailForm" url="${[action:'updatecession',id:cession?.id?:0]}" method="post" onSuccess="processResponse(e)">

    <g:if test="${cession?.modstatus==0}">
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

    <g:if test="${cession}">
      <label for="modstatus" disabled>Статус:</label>
      <g:select name="modstatus" value="${cession.modstatus}" from="['Архив','Активный']" keys="[0,1]" disabled="true"/>
      <label for="changetype" disabled>Класс договора:</label>
      <g:select name="changetype" value="${cession.changetype}" from="['С внешней','На внешнюю','Внутренняя смена']" keys="123" disabled="true"/>

    </g:if>
      <label for="cessionvariant">Вариант договора:</label>
      <g:select name="cessionvariant" value="${cession?.cessionvariant}" from="['Кредит','Лизинг']" keys="12" disabled="${cession?'true':'false'}" onchange="toggleAgrSection(this.value)"/>
      <label for="cessiontype">Тип договора:</label>
      <g:select name="cessiontype" value="${cession?.cessiontype}" from="['Цессия','Перевод долга']" keys="12" noSelection="${['0':'не выбрано']}"/>

      <hr class="admin" />

      <div id="kreditagrsection" style="${cession?.cessionvariant==2?'display:none':''}"><label for="kredit_id" style="margin-right:13px">Кредитный договор</label>
      <g:select name="kredit_id" class="fullline" value="${cession?.agr_id}" from="${kredits}" optionKey="id" noSelection="${['0':'не выбран']}" onchange="getKreditData(this.value)"/>
      <label for="cedent">Цедент:</label>
      <input type="text" class="fullline" id="cedent" disabled value="${cedent}"/>
      <label for="debtor">Должник:</label>
      <input type="text" class="fullline" id="debtor" disabled name="debtor" value="${debtor}"/>
      <label for="client_id">Клиент:</label>
      <g:select name="client_id" value="${cession?.client_id}" from="${clients}" optionValue="name" optionKey="id" noSelection="${['-1':'не выбрано']}" disabled="${cession?true:false}"/>

      <hr class="admin" /></div>
      <div id="lizingagrsection" style="${cession?.cessionvariant!=2?'display:none':''}"><label for="lizing_id" style="margin-right:5px">Лизинговый договор</label>
      <g:select name="lizing_id" class="fullline" value="${cession?.agr_id}" from="${lizings}" optionKey="id" noSelection="${['0':'не выбран']}" onchange="getLizingData(this.value)"/>
      <label for="lizcedent">Цедент:</label>
      <input type="text" class="fullline" id="lizcedent" disabled value="${cedent}"/>
      <label for="lizdebtor">Должник:</label>
      <input type="text" class="fullline" id="lizdebtor" disabled value="${debtor}"/>

      <hr class="admin" /></div>

      <label for="cessionary">Цессионарий:</label>
      <input type="text" class="fullline" id="cessionary" name="cessionary" value="${cessionary}" onblur="getcBankList();"/>
      <div id="space_arendodatel_autocomplete" class="autocomplete" style="display:none"></div>
      <label for="cbank_id">Банк цессионария:</label>
      <span id="cbanklist"><g:select class="fullline" name="cbank_id" value="${cession?.cbank_id}" from="${cbanks}" optionValue="name" optionKey="id" noSelection="${['':cessionary?'не выбран':'цессионарий не указан']}"/></span>
    <g:if test="${cession?.cessionvariant==1}">
      <label for="zalogstatus" disabled>Тип залога:</label>
      <g:select name="zalogstatus" value="${cession.zalogstatus}" from="['Нет','Есть']" keys="${1..2}" disabled="true"/>
      <label for="paytype" disabled>Тип платежа:</label>
      <g:select name="paytype" value="${cession.paytype}" from="['Кредит','Кредитная линия','Овердрафт']" keys="${1..3}" disabled="true"/>
    </g:if>

      <hr class="admin" />

      <label for="anumber">Номер договора:</label>
      <input class="fullline" type="text" id="anumber" name="anumber" value="${cession?.anumber}"/>
      <label for="adate">Дата договора:</label>
      <g:datepicker class="normal nopad" style="margin-right:108px" name="adate" value="${cession?.adate?String.format('%td.%<tm.%<tY',cession.adate):''}"/>
      <label for="enddate">Срок действия:</label>
      <g:datepicker class="normal nopad" name="enddate" value="${cession?.enddate?String.format('%td.%<tm.%<tY',cession.enddate):''}"/>
    <g:if test="${cession}">
      <label for="dopagrcomment">Доп. комментарий (при смене срока действия):</label>
      <g:textArea name="dopagrcomment" id="dopagrcomment" value="" />
    </g:if>
      <hr class="admin" />

      <label for="summa">Сумма:</label>
      <input type="text" id="summa" name="summa" disabled value="${number(value:cession?.summa)}"/>
      <label for="valuta_id">Валюта кредита:</label>
      <g:select id="valuta_id" name="valuta_id" from="${Valuta.findAllByModstatus(1)}" value="${cession?.valuta_id?:857}" optionValue="name" optionKey="id"/>
      <label for="maindebt">Основной долг:</label>
      <input type="text" id="maindebt" name="maindebt" value="${number(value:cession?.maindebt)}"/>
      <label for="procdebt">Сумма процентов:</label>
      <input type="text" id="procdebt" name="procdebt" value="${number(value:cession?.procdebt)}"/>
      <label for="procdebtperiod">Период процентов:</label>
      <input type="text" id="procdebtperiod" name="procdebtperiod" value="${cession?.procdebtperiod}"/>
      <label class="auto" for="is_debtfull">
        <input type="checkbox" id="is_debtfull" name="is_debtfull" value="1" <g:if test="${cession?.is_debtfull}">checked</g:if> />
        Передается полностью
      </label><br/>
      <label for="responsible">Ответственный:</label>
      <g:select name="responsible" value="${cession?.responsible?:session.user.id}" from="${users}" optionValue="name" optionKey="id" noSelection="${['0':'не выбрано']}"/>
    <g:if test="${session.user.group.is_rep_dirsalary}">
      <label class="auto" for="is_dirsalary">
        <input type="checkbox" id="is_dirsalary" name="is_dirsalary" value="1" <g:if test="${cession?.is_dirsalary}">checked</g:if> />
        Учитывать в зарплатах директоров
      </label>
    </g:if>
      <br/>
      <label for="description">Описание:</label>
      <g:textArea name="description" id="description" value="${cession?.description}" />
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" id="comment" value="${cession?.comment}" />

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(1)"/>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      </g:if>
      </div>

    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${cession}">
    <div class="tabs">
      <ul class="nav">
        <li><a href="javascript:void(0)" onclick="viewCell(0)">График платежей</a></li>
        <li style="${!iscanpay?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(1)">Платежи</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(2)">История</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="planpaymentsForm" url="[action:'cessionline',id:cession.id]" update="details">
      <input type="submit" class="button" id="planpayments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="paymentsForm" url="[action:'cespayments',id:cession.id]" update="details">
      <input type="submit" class="button" id="payments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="historyForm" url="[action:'cessionhistory',id:cession.id]" update="details">
      <input type="submit" class="button" id="history_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>