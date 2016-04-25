<html>
  <head>
    <title>Prisma: <g:if test="${lizing}">Лизинговый договор №${lizing.anumber}</g:if><g:else>Новый лизинговый договор</g:else></title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function newCompany(){
        window.open('${createLink(controller:'company',action:'detail')}');
      }
      function toggleMainagr(sAsort){
        if(sAsort=='0') $('mainagr').show();
        else $('mainagr').hide();
      }
      function getMainagr(){
        var arendodatel = $('arendodatel').value
        <g:remoteFunction controller='agreement' action='lizmainagrlist' params="'arendodatel='+arendodatel" update="mainagr" />
      }
      function getBankByCompany(){
        var iCompany_id = $('lizdoppayment_tocompany_id').value;
        if(iCompany_id)
          <g:remoteFunction controller='agreement' action='lizdoppaymentbanklist' params="'company_id='+iCompany_id" update="lizdoppayment_tobank_span" onSuccess="\$('lizdoppayment_bank_div').show()"/>;
      }
      function confirmUpload(){
        if(confirm('Закачать платежи по договору? При этом будут удалены все существующие платежи')) { 
          $('uploadpaymentsForm').submit();
        } else $('cancelluploadbutton').click();
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['arendator','arendodatel','anumber','summa'].forEach(function(ids){
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
              case 10: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Номер договора"])}</li>'; $('anumber').addClassName('red'); break;
              case 11: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата договора"])}</li>'; $('adate').up('span').addClassName('k-error-colored'); break;
              case 12: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок окончания"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 13: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Срок окончания"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 14: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('summa').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${lizing?1:0}){
          location.reload(true);
        } else if(e.responseJSON.lizing){
          location.assign('${createLink(controller:controllerName,action:'lizing')}'+'/'+e.responseJSON.lizing);
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
              case 3: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Сумма платежа"])}</li>'; $('planpayment_summa').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorplanpaymentlist").innerHTML=sErrorMsg;
          $("errorplanpaymentlist").up('div').show();
        } else
          jQuery('#planpaymentAddForm').slideUp(300, function() {$('planpayments_submit_button').click();});
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
          jQuery('#payrequestAddForm').slideUp(300, function() { getPayrequests(); });
      }
      function processAddlizingdopagrResponse(e){
        var sErrorMsg = '';
        ['finlizingdopagr_nomer','finlizingdopagr_summa'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['finlizingdopagr_dsdate','finlizingdopagr_startdate','finlizingdopagr_enddate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Номер соглашения"])}</li>'; $('finlizingdopagr_nomer').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('finlizingdopagr_summa').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата соглашения"])}</li>'; $('finlizingdopagr_dsdate').up('span').addClassName('k-error-colored'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата начала"])}</li>'; $('finlizingdopagr_startdate').up('span').addClassName('k-error-colored'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата окончания"])}</li>'; $('finlizingdopagr_enddate').up('span').addClassName('k-error-colored'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlizingdopagrlist").innerHTML=sErrorMsg;
          $("errorlizingdopagrlist").up('div').show();
        } else
          jQuery('#lizingdopagrAddForm').slideUp(300, function() { getDopAgrs(); });
      }
      function processaddlizdoppaymentResponse(e){
        var sErrorMsg = '';
        ['lizdoppayment_tobank','lizdoppayment_summa','payrequest_expensetype_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['lizdoppayment_paydate','lizdoppayment_fromcompany','lizdoppayment_tocompany'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        $('lizdoppayment_is_task').value=0;
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $("lizdoppayment_summa").addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата платежа"])}</li>'; $("lizdoppayment_paydate").up('span').addClassName('k-error-colored'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Плат. компания"])}</li>'; $("lizdoppayment_fromcompany").up('span').addClassName('k-error-colored'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Получ. компания"])}</li>'; $("lizdoppayment_tocompany").up('span').addClassName('k-error-colored'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк получателя"])}</li>'; $("lizdoppayment_tobank").addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Доходы-расходы"])}</li>'; $('payrequest_expensetype_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("erroraddlizdoppaymentlist").innerHTML=sErrorMsg;
          $("erroraddlizdoppaymentlist").up('div').show();
        } else {
          jQuery('#lizdoppaymentaddForm').slideUp(300, function() { getDopPayments(); });
        }
      }
      function submitForm(iStatus){
        $('modstatus').value = iStatus
        $('submit_button').click();
      }
      function closeAgr(){
        if(confirm('Вы уверены, что хотите досрочно закрыть лизинговый договор №${lizing?.anumber}?')) { submitForm(0) }
      }
      function restoreAgr(){
        if(confirm('Восстановить лизинговый договор №${lizing?.anumber}?')) { submitForm(1) }
      }
      function deleteAgr(){
        if(confirm('Вы уверены, что хотите удалить лизинговый договор №${lizing?.anumber}?')) { submitForm(-1) }
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
          case 3: getDopPayments();break;
          case 4: getDopAgrs();break;
          case 5: getHistory();break;
        }
      }
      function getPlanpayment(){
        if(${lizing?1:0}) $('planpayments_submit_button').click();
      }
      function getPayments(){
        if(${lizing?1:0}) $('payments_submit_button').click();
      }
      function getDopPayments(){
        if(${lizing?1:0}) $('doppayments_submit_button').click();
      }
      function getDopAgrs(){
        if(${lizing?1:0}) $('dopagrs_submit_button').click();
      }
      function getHistory(){
        if(${lizing?1:0}) $('history_submit_button').click();
      }
      function getPayrequests(){
        if(${lizing?1:0}) $('payrequests_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
        new Autocomplete('arendator', {
          serviceUrl:'${resource(dir:"autocomplete",file:"space_arendator_autocomplete")}'
        });
        new Autocomplete('arendodatel', {
          serviceUrl:'${resource(dir:"autocomplete",file:"space_arendodatel_autocomplete")}',
          onSelect: function(value, data){
            $('arendodatel').focus();
          }
        });
        jQuery("#startsaldodate").mask("99.99.9999",{placeholder:" "});
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
    <h3 class="fleft"><g:if test="${lizing}">Лизинговый договор №${lizing.anumber} (${lizing.id} | ${String.format('%td.%<tm.%<tY %<tT',lizing.inputdate)})</g:if><g:else>Новый лизинговый договор</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку лизингов</a>
    <div class="clear"></div>
    <g:formRemote name="requestDetailForm" url="${[action:'updatelizing',id:lizing?.id?:0]}" method="post" onSuccess="processResponse(e)">

    <g:if test="${lizing?.modstatus==0}">
      <div class="info-box" style="margin-top:0">
        <span class="icon icon-info-sign icon-3x"></span>
        <ul id="infolist">
          <li>Внимание! Договор закрыт.</li>
        </ul>
      </div>
    </g:if>

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

    <g:if test="${lizing}">
      <label for="status" disabled>Статус:</label>
      <g:select id="status" name="status" value="${lizing.modstatus}" from="['Архив','Активный']" keys="[0,1]" disabled="true"/>
      <label for="restfee" disabled>Остаточный платеж</label>
      <input type="text" id="restfee" disabled value="${number(value:restfee)}" />
      <hr class="admin" />
    </g:if>

      <label for="arendator" style="margin-right:8px">Лизингополучатель:</label>
      <input type="text" id="arendator" name="arendator" value="${arendator}"/>
      <div id="space_arendator_autocomplete" class="autocomplete" style="display:none"></div>
      <label for="arendodatel">Лизингодатель:</label>
    <g:if test="${iscanaddcompanies}">
      <span class="input-append">
        <input type="text" class="nopad normal" id="arendodatel" name="arendodatel" value="${arendodatel}" onblur="getMainagr();"/>
        <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
      </span>
    </g:if><g:else>
      <input type="text" id="arendodatel" name="arendodatel" value="${arendodatel}" onblur="getMainagr();"/>
    </g:else>
      <div id="space_arendodatel_autocomplete" class="autocomplete" style="display:none"></div>
      <label for="lizsort">Класс лизинга</label>
      <g:select id="lizsort" name="lizsort" value="${lizing?.lizsort}" from="['Лизинг','Сублизинг']" keys="[1,0]" onchange="toggleMainagr(this.value)"/>
      <span id="mainagr" style="${lizing?.lizsort!=0?'display:none':''}"><label for="mainagr_id">Основной договор:</label>
      <g:select name="mainagr_id" value="${lizing?.mainagr_id?:0}" from="${agrs}" optionValue="anumber" optionKey="id" noSelection="${['0':lizing?'не выбран':'лизингодатель не указан']}"/></span>

      <hr class="admin" />

      <label for="anumber">Номер договора:</label>
      <input class="fullline" type="text" id="anumber" name="anumber" value="${lizing?.anumber}"/>
      <label for="adate">Дата договора:</label>
      <g:datepicker class="normal nopad" name="adate" value="${lizing?.adate?String.format('%td.%<tm.%<tY',lizing.adate):''}"/>
      <label for="enddate">Срок окончания:</label>
      <g:datepicker class="normal nopad" name="enddate" value="${lizing?.enddate?String.format('%td.%<tm.%<tY',lizing.enddate):''}" disabled="${lizing?'true':'false'}"/>
      <label class="auto" for="cessionstatus">
        <input type="checkbox" id="cessionstatus" name="cessionstatus" value="1" <g:if test="${lizing?.cessionstatus}">checked</g:if> disabled />
        Уступка
      </label>
    <g:if test="${lizing?.cessionstatus}">
      <br/><label for="creditor">Новый заемщик:</label>
      <input type="text" class="fullline" id="creditor" name="creditor" value="${Company.get(lizing?.creditor)?.name}" disabled/>
      <label for="cbank_id">Новый Банк:</label>
      <input type="text" class="fullline" id="cbank_id" name="cbank_id" value="${Bank.get(lizing?.cbank_id)?.name}" disabled/>
    </g:if>

      <hr class="admin" />

      <label for="summa">Сумма:</label>
      <input type="text" id="summa" name="summa" ${lizing?'disabled':''} value="${number(value:lizing?.summa)}"/>
      <label for="initialfee">Начальный платеж</label>
      <input type="text" id="initialfee" name="initialfee" value="${number(value:lizing?.initialfee)}"/>
      <label for="project_id">Проект:</label>
      <g:select name="project_id" value="${lizing?.project_id}" from="${projects}" optionValue="name" optionKey="id" noSelection="${['0':'не выбрано']}"/>
      <label for="car_id">Машина:</label>
      <g:select name="car_id" value="${lizing?.car_id}" from="${cars}" optionValue="name" optionKey="id" noSelection="${['0':'не выбрано']}"/>
      <label for="startsaldo">Начальное сальдо:</label>
      <input type="text" id="startsaldo" name="startsaldo" value="${number(value:lizing?.startsaldo)}"/>
      <label for="startsaldodate">Дата сальдо:</label>
      <g:datepicker class="normal nopad" name="startsaldodate" value="${lizing?.startsaldodate?String.format('%td.%<tm.%<tY',lizing.startsaldodate):''}"/><br/>
      <label for="responsible">Ответственный:</label>
      <g:select name="responsible" value="${lizing?.responsible?:session.user.id}" from="${users}" optionValue="name" optionKey="id" noSelection="${['0':'не выбрано']}"/>
    <g:if test="${session.user.group.is_rep_dirsalary}">
      <label class="auto" for="is_dirsalary">
        <input type="checkbox" id="is_dirsalary" name="is_dirsalary" value="1" <g:if test="${lizing?.is_dirsalary}">checked</g:if> />
        Учитывать в зарплатах директоров
      </label>
    </g:if>
      <br/><label for="description">Описание предмета лизинга:</label>
      <g:textArea name="description" id="description" value="${lizing?.description}" />
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" id="comment" value="${lizing?.comment}" />

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
      <g:if test="${lizing?.modstatus==1}">
        <input type="button" class="spacing" value="Досрочно закрыть" onclick="closeAgr()"/>
      </g:if>
      <g:if test="${isCanRestore}">
        <input type="button" class="spacing" value="Восстановить" onclick="restoreAgr()" />
      </g:if>
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(${lizing?lizing.modstatus:1})"/>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      <g:if test="${lizing?.modstatus>-1&&iscandelete}">
        <input type="button" class="spacing reset" value="К удалению" onclick="deleteAgr()" />
      </g:if>
      </g:if>
      </div>
      <input type="hidden" id="modstatus" name="modstatus" value="1"/>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${lizing}">
    <div class="tabs">
      <ul class="nav">
        <li><a href="javascript:void(0)" onclick="viewCell(0)">График платежей</a></li>
        <li style="${!iscanpay?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(1)">Платежи</a></li>
        <li style="${!iscanpay?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(2)">Платежи по выписке</a></li>
        <li style="${!iscanpay?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(3)">Побочные платежи</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(4)">Доп. соглашения</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(5)">История</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="planpaymentsForm" url="[action:'lizingplanpayments',id:lizing.id]" update="details">
      <input type="submit" class="button" id="planpayments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="payrequestsForm" url="[action:'lzpayrequests',id:lizing.id]" update="details">
      <input type="submit" class="button" id="payrequests_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="paymentsForm" url="[action:'lizpayments',id:lizing.id]" update="details">
      <input type="submit" class="button" id="payments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="paymentsForm" url="[action:'lizdoppayments',id:lizing.id]" update="details">
      <input type="submit" class="button" id="doppayments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="dopagrsForm" url="[action:'lizingdopagrs',id:lizing.id]" update="details">
      <input type="submit" class="button" id="dopagrs_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="historyForm" url="[action:'lizinghistory',id:lizing.id]" update="details">
      <input type="submit" class="button" id="history_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>