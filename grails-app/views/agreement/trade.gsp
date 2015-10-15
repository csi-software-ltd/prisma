<html>
  <head>
    <title>Prisma: <g:if test="${trade}">Договор поставок и услуг №${trade.anumber}</g:if><g:else>Новый договор поставок и услуг</g:else></title>
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
      function togglecatdata(iId){
        if(iId=='7') $('catdata').show();
        else $('catdata').hide();
      }
      function updatecatdata(iId){
        <g:remoteFunction controller='agreement' action='tradespacelist' params="'company_id='+iId" update="catdata" />
      }
      function newCompany(){
        window.open('${createLink(controller:'company',action:'detail')}');
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['client','supplier','anumber','tradecat_id','summa','space_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['adate','enddate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
      <g:if test="${iscanaddcompanies}">
        ['client','supplier'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('red');
        });
      </g:if>
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
            <g:if test="${iscanaddcompanies}">
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Покупатель"])}</li>'; $('client').up('span').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Покупатель"])}</li>'; $('client').up('span').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Покупателя","Покупатель"])}</li>'; $('client').up('span').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Поставщик"])}</li>'; $('supplier').up('span').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Поставщик"])}</li>'; $('supplier').up('span').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Поставщика","Поставщик"])}</li>'; $('supplier').up('span').addClassName('red'); break;
            </g:if><g:else>
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Покупатель"])}</li>'; $('client').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Покупатель"])}</li>'; $('client').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Покупателя","Покупатель"])}</li>'; $('client').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Поставщик"])}</li>'; $('supplier').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Поставщик"])}</li>'; $('supplier').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Поставщика","Поставщик"])}</li>'; $('supplier').addClassName('red'); break;
            </g:else>
              case 7: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Категория"])}</li>'; $('tradecat_id').addClassName('red'); break;
              case 8: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('summa').addClassName('red'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Номер договора"])}</li>'; $('anumber').addClassName('red'); break;
              case 10: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата договора"])}</li>'; $('adate').up('span').addClassName('k-error-colored'); break;
              case 11: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Срок действия"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 12: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Арендный договор"])}</li>'; $('space_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${trade?1:0}){
          location.reload(true);
        } else if(e.responseJSON.trade){
          location.assign('${createLink(controller:controllerName,action:'trade')}'+'/'+e.responseJSON.trade);
        } else
          location.assign('${createLink(controller:controllerName,action:'index')}');
      }
      function processaddpayrequestResponse(e){
        var sErrorMsg = '';
        ['payrequest_summa','payrequest_tobank','payrequest_expensetype_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red').removeClassName('k-error-colored');
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
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк получателя"])}</li>'; $('payrequest_tobank').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата платежа"])}</li>'; $('payrequest_paydate').up('span').addClassName('k-error-colored'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Доходы-расходы"])}</li>'; $('payrequest_expensetype_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorpayrequestlist").innerHTML=sErrorMsg;
          $("errorpayrequestlist").up('div').show();
        } else
          jQuery('#payrequestAddForm').slideUp(300, function() { getPayments(); });
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
          case 0: getPayments();break;
          case 1: getHistory();break;
        }
      }
      function getPayments(){
        if(${trade?1:0}) $('payments_submit_button').click();
      }
      function getHistory(){
        if(${trade?1:0}) $('history_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
        new Autocomplete('client', {
          serviceUrl:'${resource(dir:"autocomplete",file:"companyname_full_autocomplete")}',
          onSelect: function(value, data){
            var lsData = data.split(';');
            updatecatdata(lsData[0]);
          }
        });
        new Autocomplete('supplier', {
          serviceUrl:'${resource(dir:"autocomplete",file:"trade_supplier_autocomplete")}',
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
    <h3 class="fleft"><g:if test="${trade}">Договор поставок и услуг №${trade.anumber}</g:if><g:else>Новый договор поставок и услуг</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку договоров</a>
    <div class="clear"></div>
    <g:formRemote name="requestDetailForm" url="${[action:'updatetrade',id:trade?.id?:0]}" method="post" onSuccess="processResponse(e)">

    <g:if test="${trade?.modstatus==0}">
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

    <g:if test="${trade}">
      <label for="trade_id" disabled>Код договора:</label>
      <input type="text" id="trade_id" disabled value="${trade.id}" />
      <label for="inputdate" disabled>Дата создания:</label>
      <input type="text" name="inputdate" disabled value="${String.format('%td.%<tm.%<tY %<tT',trade.inputdate)}" />
      <label for="modstatus" disabled>Статус:</label>
      <g:select name="modstatus" value="${trade.modstatus}" from="['Архив','Активный']" keys="[0,1]" disabled="true"/><br/>
      <label for="debt" disabled>Долг арендатора:</label>
      <input type="text" id="debt" disabled value="${intnumber(value:trade.debt)}" />
      <label for="debtdate" disabled>Дата долга:</label>
      <input type="text" name="debtdate" disabled value="${trade.debtdate?String.format('%td.%<tm.%<tY',trade.debtdate):'нет'}" />
      <hr class="admin" />
    </g:if>

      <label for="tradetype">Тип договора</label>
      <g:select id="tradetype" name="tradetype" value="${trade?.tradetype}" from="['Услуги','Поставки']" keys="[0,1]"/>
      <label for="tradecat_id">Категория:</label>
      <g:select name="tradecat_id" value="${trade?.tradecat_id}" from="${tradecats}" optionValue="name" optionKey="id" noSelection="${['0':'не выбрано']}" onchange="togglecatdata(this.value)"/>
      <label for="client">Покупатель:</label>
    <g:if test="${iscanaddcompanies}">
      <span class="input-append">
        <input type="text" class="nopad normal" id="client" name="client" value="${client}"/>
        <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
      </span>
    </g:if><g:else>
      <input type="text" id="client" name="client" value="${client}"/>
    </g:else>
      <label for="supplier">Поставщик:</label>
    <g:if test="${iscanaddcompanies}">
      <span class="input-append">
        <input type="text" class="nopad normal" id="supplier" name="supplier" value="${supplier}"/>
        <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
      </span>
    </g:if><g:else>
      <input type="text" id="supplier" name="supplier" value="${supplier}"/>
    </g:else>
      <label for="tradesort">Класс договора</label>
      <g:select id="tradesort" name="tradesort" value="${trade?.tradesort}" from="['Внешний','Холдинг']" keys="[0,1]"/>
      <span id="catdata" style="${trade?.tradecat_id!=7?'display:none':''}"><label for="space_id">Арендный договор:</label>
      <g:select name="space_id" value="${trade?.space_id}" from="${spaces}" optionKey="id" noSelection="${['0':'не выбрано']}"/></span>

      <hr class="admin" />

      <label for="anumber">Номер договора:</label>
      <input class="fullline" type="text" id="anumber" name="anumber" value="${trade?.anumber}"/>
      <label for="adate">Дата договора:</label>
      <g:datepicker class="normal nopad" style="margin-right:108px" name="adate" value="${trade?.adate?String.format('%td.%<tm.%<tY',trade.adate):''}"/>
      <label for="enddate">Срок окончания:</label>
      <g:datepicker class="normal nopad" name="enddate" value="${trade?.enddate?String.format('%td.%<tm.%<tY',trade.enddate):''}"/>

      <hr class="admin" />

      <label for="summa">Сумма:</label>
      <input type="text" id="summa" name="summa" value="${intnumber(value:trade?.summa)}"/>
      <label for="paytype">Тип платежа</label>
      <g:select id="paytype" name="paytype" value="${trade?.paytype}" from="['Единовременно','Регулярные платежи']" keys="[1,0]"/>
      <label for="responsible">Ответственный:</label>
      <g:select name="responsible" value="${trade?.responsible?:session.user.id}" from="${responsiblies}" optionValue="name" optionKey="id" noSelection="${['0':'не выбрано']}"/><br/>
      <label for="description">Описание фактического назначения:</label>
      <g:textArea name="description" id="description" value="${trade?.description}" />
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" id="comment" value="${trade?.comment}" />

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
  <g:if test="${trade}">
    <div class="tabs">
      <ul class="nav">
        <li><a href="javascript:void(0)" onclick="viewCell(0)">Платежи</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(1)">История</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="paymentsForm" url="[action:'trpayments',id:trade.id]" update="details">
      <input type="submit" class="button" id="payments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="historyForm" url="[action:'tradehistory',id:trade.id]" update="details">
      <input type="submit" class="button" id="history_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>