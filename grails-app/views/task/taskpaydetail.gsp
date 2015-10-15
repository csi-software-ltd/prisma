<html>
  <head>
    <title>Prisma: Задание на плановые платежи № ${taskpay.id}</title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript>
      var isReload = false;
      function init(){
        viewCell(0);
        jQuery("#term").mask("99.99.9999",{placeholder:" "});
        if (${taskpay.is_t?1:0}) {
          new Autocomplete('company', {
            serviceUrl:'${resource(dir:"autocomplete",file:"space_arendator_autocomplete")}'
          });
        }
      }
      function returnToList(){
        $("returnToListForm").submit();
      }
      function getBankListByCompany(){
        var iCompany_id = $('tocompany_id').value;
        if(iCompany_id)
          <g:remoteFunction controller='task' action='payrequestbanklist' params="'company_id='+iCompany_id" update="tobank_span"/>;
      }
      function togglecheck(){
        if(document.getElementById('groupcheckbox').checked)
          jQuery('#addrequestTaskForm :checkbox:not(:checked):not(:disabled)').each(function(){ this.checked=true; });
        else
          jQuery('#addrequestTaskForm :checkbox:checked').each(function(){ this.checked=false; });
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
          case 0: getPayrequest();break;
          case 1: getTaskAddPayrequests();break;
          case 2: getPaytransfers();break;
        }
      }
      function getPayrequest(){
        $('payrequests_submit_button').click();
      }
      function getTaskAddPayrequests(){
        $('taskaddprequests_submit_button').click();
      }
      function getPaytransfers(){
        $('paytransfers_submit_button').click();
      }
      function getBankaccount(iValue){
        <g:remoteFunction controller='task' action='bankaccount' params="'id='+iValue+'&taskpay_id=${taskpay.id}'" update="bankaccount_div" />
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['term','executor','comment','company'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>Не заполнено обязательное поле "Срок исполнения"</li>'; $("term").addClassName('red'); break;
              case 2: sErrorMsg+='<li>Не заполнено обязательное поле "Ответственный"</li>'; $("executor").addClassName('red'); break;
              case 3: sErrorMsg+='<li>Не выбрано значение обязательного поля "Счета компании"</li>'; break;
              case 4: sErrorMsg+='<li>Не заполнено обязательное поле "Комментарий исполнителя"</li>'; $("comment").addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Компания"])}</li>'; $('company').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Компания"])}</li>'; $('company').addClassName('red'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Компанию","Компания"])}</li>'; $('company').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(!isReload){
          returnToList();
        } else location.reload(true);
      }
      function processPayrequestModstatus(e){
        if(e.responseJSON.needReload==1) location.reload(true);
        else getPayrequest();
      }
      function setTaskpaystatus(iId){
        $('taskpaystatus').value = iId;
        $('taskpayDetailFormSubmit').click();
      }
      function setIsAccept(iId){
        if (iId==0) isReload = true;
        $('is_accept').value = iId;
        $('taskpayDetailFormSubmit').click();
      }
      function pdf(){
        if(${taskpay.taskpaystatus!=0}) $("pdfForm").submit();
      }
      function closesplitform(){
        jQuery('#splitrequestForm').slideUp(300, function() {
          ['splitrequest_summa'].forEach(function(ids){
            if($(ids))
              $(ids).removeClassName('red');
          });
          $("errorsplitrequestlist").innerHTML='';
          $("errorsplitrequestlist").up('div').hide();
        });
      }
      function processsplitrequestResponse(e){
        var sErrorMsg = '';
        ['splitrequest_summa'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $("splitrequest_summa").addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Сумма"])}</li>'; $("splitrequest_summa").addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorsplitrequestlist").innerHTML=sErrorMsg;
          $("errorsplitrequestlist").up('div').show();
        } else {
          jQuery('#splitrequestForm').slideUp(300, function() { getPayrequest(); });
        }
      }
      function processaddpaytransferResponse(e){
        var sErrorMsg = '';
        ['paytransfer_frombank','paytransfer_summa','paytransfer_fromcompany','paytransfer_tobank','paytransfer_tocompany'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['paytransfer_paydate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата платежа"])}</li>'; $("paytransfer_paydate").up('span').addClassName('k-error-colored'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Плат. компания"])}</li>'; $("paytransfer_fromcompany").addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк плательщика"])}</li>'; $("paytransfer_frombank").addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $("paytransfer_summa").addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Получ. компания"])}</li>'; $("paytransfer_tocompany").addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк получателя"])}</li>'; $("paytransfer_tobank").addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorpaytransferlist").innerHTML=sErrorMsg;
          $("errorpaytransferlist").up('div').show();
        } else {
          jQuery('#paytransferForm').slideUp(300, function() { getPaytransfers(); });
        }
      }
      function processTaskpayrequestResponse(e){
        var sErrorMsg = '';
        ['payrequest_summa','payrequest_destination'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['tocompany','tobank'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Получ. компания"])}</li>'; $("tocompany").up('span').addClassName('k-error-colored'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк получателя"])}</li>'; $("tobank").up('span').addClassName('k-error-colored'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Банк получателя"])}</li>'; $("tobank").up('span').addClassName('k-error-colored'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $("payrequest_summa").addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Назначение платежа"])}</li>'; $("payrequest_destination").addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorpayrequestlist").innerHTML=sErrorMsg;
          $("errorpayrequestlist").up('div').show();
        } else {
          jQuery('#payrequestForm').slideUp(300, function() { location.reload(true); });
        }
      }
      function getBankByCompany(sId){
        var iCompany_id = $('paytransfer_fromcompany_id').value;
        if (sId=="tobank") iCompany_id = $('paytransfer_tocompany_id').value;
        if(iCompany_id)
          <g:remoteFunction controller='task' action='paytransferbanklist' params="'company_id='+iCompany_id+'&fieldId='+sId" onComplete="\$('paytransfer_'+sId+'_span').innerHTML=e.responseText"/>;
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      label.long{width:370px}
      input.normal{width:202px}      
      input.mini{width:60px!important}
    </style>
  </head>
  <body onload="init();">
    <h3 class="fleft">Задание на плановые платежи № ${taskpay.id}<g:if test="${taskpay.moddate}">&nbsp;(${String.format('%td.%<tm.%<tY %<tT',taskpay.moddate)})</g:if></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку заданий</a>
    <div class="clear"></div>

    <div class="error-box" style="display:none">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>

    <g:formRemote name="taskpayDetailForm" url="${[action:'savetaskpaydetail',id:taskpay.id]}" method="post" onSuccess="processResponse(e)">
      <label for="term" <g:if test="${taskpay.is_accept}">disabled</g:if>>Срок исполнения:</label>
      <g:datepicker class="normal nopad" style="margin-right:108px" name="term" value="${taskpay.term?String.format('%td.%<tm.%<tY',taskpay.term):''}" disabled="${taskpay.is_accept?'true':'false'}"/>
      <label for="executor">Ответственный:</label>
      <g:select name="executor" value="${taskpay.executor?:!iscanaccept?user.id:0}" from="${executor}" optionValue="pers_name" optionKey="id" noSelection="${['0':'не выбран']}"/>
      <label for="acceptdate" disabled>Дата акцепта:</label>
      <input type="text" id="acceptdate" name="acceptdate" value="${taskpay.acceptdate?String.format('%td.%<tm.%<tY %<tT',taskpay.acceptdate):'не акцептовано'}" disabled />
      <label for="moddate" disabled>Дата исполнения:</label>
      <input type="text" id="moddate" name="moddate" value="${taskpay.moddate?String.format('%td.%<tm.%<tY %<tT',taskpay.moddate):'нет'}" disabled />
    <g:if test="${!taskpay.is_t||taskpay.is_accept||taskpay.taskpaystatus==-1}">
      <label for="company" disabled>Компания:</label>
      <input type="text" id="company" value="${Company.get(taskpay.company_id)?.name}" disabled />
    </g:if><g:else>
      <label for="company">Компания:</label>
      <input type="text" id="company" name="company" value="${Company.get(taskpay.company_id)?.name}"/>
    </g:else>
      <label for="status" disabled>Статус:</label>
      <g:select name="status" value="${taskpay?.taskpaystatus}" from="${taskpaystatus}" optionKey="id" optionValue="name" disabled="true"/>
      <label for="summa" disabled>Сумма:</label>
      <input type="text" id="summa" name="summa" value="${number(value:taskpay.summa)}" disabled />
      <label class="auto" for="is_urgent" <g:if test="${!iscanaccept}">disabled</g:if>>
        <input type="checkbox" id="is_urgent" name="is_urgent" value="1" <g:if test="${taskpay.is_urgent}">checked</g:if> <g:if test="${!iscanaccept}">disabled</g:if>/>
        Срочное задание
      </label>
    <g:if test="${taskpay.company_id&&!taskpay.assertCompany()}">
      <hr class="admin">
      <label <g:if test="${curbankaccount && taskpay.is_accept}">disabled</g:if>>Счета компании:</label>
      <div class="fright" id="bankaccount_radio_div" style="width:765px;">
      <g:if test="${curbankaccount && (taskpay.is_accept || taskpay.taskpaystatus==-1)}">
        <input type="text" id="bank" value="${bank?.name}" disabled /> &nbsp;&nbsp;
        <input type="text" id="bankaccount_account" name="bankaccount_account" value="${g.account(value:curbankaccount.schet)}" disabled />
      </g:if><g:else>
      <g:each in="${bankaccount}" var="item" status="i">
        <label for="bankaccount_id${item.id}">
          <g:radio style="margin-bottom:15px" name="bankaccount_id" id="bankaccount_id${item.id}" value="${item.id}" checked="${taskpay.bankaccount_id==item.id?true:false}" onchange="getBankaccount(this.value)"/>
          <input type="text" value="${Bank.get(item.bank_id)?.name}" readonly onclick="$('bankaccount_id${item.id}').click()"/>
          <input type="text" value="${g.account(value:item.schet)}" readonly onclick="$('bankaccount_id${item.id}').click()"/>
        </label>
      </g:each>
      </g:else>
      </div>
      <div class="clear"></div>
      <div id="bankaccount_div">
        <g:if test="${bank}">            
          <label for="actsaldo" disabled>Факт. остаток:</label>
          <input type="text" id="actsaldo" disabled value="${intnumber(value:curbankaccount.actsaldo)}"/>  
          <label for="actsaldodate" disabled>Дата факт остатка:</label>
          <input type="text" id="actsaldodate" disabled value="${curbankaccount.actsaldodate?String.format('%td.%<tm.%<tY',curbankaccount.actsaldodate):''}" />
          <label for="accsaldo" disabled>Резерв по счету:</label>
          <input type="text" id="accsaldo" disabled value="${number(value:accsaldo)}"/>
          <label for="compsaldo" disabled>По компании:</label>
          <input type="text" id="compsaldo" disabled value="${number(value:compsaldo)}"/>
          <label for="cursaldo" disabled>Текущий остаток:</label>
          <input type="text" id="cursaldo" disabled value="${number(value:cursaldo)}"/>
          <label for="totalsaldo" disabled>Итоговый остаток:</label>
          <input type="text" id="totalsaldo" disabled value="${number(value:totalsaldo)}" <g:if test="${totalsaldo<0}">class="red"</g:if>/>
        </g:if>
      </div>
    </g:if>
      <div class="clear"></div>

      <hr class="admin">

      <label for="description" <g:if test="${!iscancreate}">disabled</g:if>>Описание:</label>
      <input type="text" class="fullline" id="description" name="description" value="${taskpay.description}" ${!iscancreate?'disabled':''}/>
      <label for="plan" <g:if test="${!iscanaccept}">disabled</g:if>>План исполнения:</label>
      <label class="auto" for="payway" <g:if test="${!iscanexec}">disabled</g:if>>
        <input type="checkbox" id="payway" name="payway" value="1" <g:if test="${taskpay.payway}">checked</g:if> <g:if test="${!iscanexec}">disabled</g:if> />
        Ручное поручение
      </label>
      <g:textArea name="plan" value="${taskpay.plan}" disabled="${!iscanaccept?'true':'false'}"/>
      <label for="comment" <g:if test="${!iscanexec}">disabled</g:if>>Комментарий:</label>
      <input type="text" class="fullline" id="comment" name="comment" value="${taskpay.comment}" ${!iscanexec?'disabled':''}/>

      <hr class="admin">

      <div class="fright" id="btns">
      <g:if test="${taskpay.taskpaystatus!=0}">
        <input type="button" class="spacing" value="Печать" onclick="pdf()"/>
      </g:if>
      <g:if test="${iscandelete}">
        <g:remoteLink class="button" url="${[controller:'task',action:'deletetaskpay',id:taskpay.id]}" onSuccess="returnToList()">Удалить &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink>
      </g:if>
      <g:if test="${taskpay.executor==user.id&&taskpay.taskpaystatus in [1]}">
        <input type="button" value="Отложить" onclick="setTaskpaystatus(5);"/>
      </g:if>
      <g:if test="${iscanaccept&&!taskpay.is_accept}">
        <input type="button" value="В оплату" onclick="setIsAccept(1);"/>
      </g:if><g:elseif test="${iscanaccept&&taskpay.is_accept&&taskpay.taskpaystatus in [0,1,5]}">
        <input type="button" value="Вернуть из оплаты" onclick="setIsAccept(0);"/>
      </g:elseif>
      <g:if test="${iscanexec&&taskpay.taskpaystatus in [-1,0]&&taskpay.is_accept}">
        <input type="button" value="Назначить" onclick="setTaskpaystatus(1);"/>
      </g:if><g:elseif test="${iscanexec&&taskpay.taskpaystatus==1}">
        <input type="button" value="Отменить назначение" onclick="setTaskpaystatus(0);"/>
      </g:elseif>
        <input type="submit" id="taskpayDetailFormSubmit" value="Сохранить"/>
      </div>
      <input type="hidden" id="taskpaystatus" name="taskpaystatus" value="${taskpay.taskpaystatus}" />
      <input type="hidden" id="is_accept" name="is_accept" value="${taskpay.is_accept}" />
    </g:formRemote>

    <div class="clear"></div>
    <div class="tabs">
      <ul class="nav">
        <li class="selected"><a href="javascript:void(0)" onclick="viewCell(0)">Платежи</a></li>
        <li style="${taskpay.taskpaystatus||!iscancreate?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(1)">Новые платежи</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(2)">Внутренние проводки</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <div class="clear"></div>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку заданий</a>
    <g:formRemote name="payrequestsForm" url="[action:'payrequesttasklist',id:taskpay.id]" update="details">
      <input type="submit" class="button" id="payrequests_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="taskaddprequestsForm" url="[action:'taskaddprequests',id:taskpay.id]" update="details">
      <input type="submit" class="button" id="taskaddprequests_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="paytransfersForm" url="[action:'paytransfers',id:taskpay.id]" update="details">
      <input type="submit" class="button" id="paytransfers_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:form id="pdfForm" name="pdfForm" controller="task" action="report" id="${taskpay.id}">
    </g:form>
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:'task',action:'index',params:[fromDetails:1]]}">
    </g:form>    
  </body>
</html>
