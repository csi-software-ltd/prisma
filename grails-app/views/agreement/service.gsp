<html>
  <head>
    <title>Prisma: <g:if test="${service}">Договор услуг №${service.anumber}</g:if><g:else>Новый договор услуг</g:else></title>
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
      function closeAgr(){
        if(confirm('Вы уверены, что хотите досрочно закрыть договор услуг №${service?.anumber}?')) { submitForm(0) }
      }
      function deleteAgr(){
        if(confirm('Вы уверены, что хотите удалить договор услуг №${service?.anumber}?')) { submitForm(-1) }
      }
      function newCompany(){
        window.open('${createLink(controller:'company',action:'detail')}');
      }
      function geteBankList(){
        var companyname = $('ecompany').value
        <g:remoteFunction controller='agreement' action='servicebanklist' params="'companyname='+companyname" update="ebanklist" />
      }
      function getzBankList(){
        var companyname = $('zcompany').value
        <g:remoteFunction controller='agreement' action='servicebanklist' params="'companyname='+companyname+'&type=1'" update="zbanklist" />
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['atype','asort','anumber','summa','prolongterm','payterm'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['adate','enddate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        ['zcompany','ecompany'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Заказчик"])}</li>'; $('zcompany').up('span').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Заказчик"])}</li>'; $('zcompany').up('span').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Заказчика","Заказчик"])}</li>'; $('zcompany').up('span').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Исполнитель"])}</li>'; $('ecompany').up('span').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Исполнитель"])}</li>'; $('ecompany').up('span').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Исполнителя","Исполнитель"])}</li>'; $('ecompany').up('span').addClassName('red'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип договора"])}</li>'; $('atype').addClassName('red'); break;
              case 10: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Признак договора"])}</li>'; $('asort').addClassName('red'); break;
              case 11: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Номер договора"])}</li>'; $('anumber').addClassName('red'); break;
              case 12: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Стоимость"])}</li>'; $('summa').addClassName('red'); break;
              case 13: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Стоимость"])}</li>'; $('summa').addClassName('red'); break;
              case 14: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата договора"])}</li>'; $('adate').up('span').addClassName('k-error-colored'); break;
              case 15: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок действия"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 16: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Срок действия"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 17: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Число платежа"])}</li>'; $('payterm').addClassName('red'); break;
              case 18: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок пролонгации"])}</li>'; $('prolongterm').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${service?1:0}){
          location.reload(true);
        } else if(e.responseJSON.service){
          location.assign('${createLink(controller:controllerName,action:'service')}'+'/'+e.responseJSON.service);
        } else
          returnToList();
      }
      function processaddpayrequestResponse(e){
        var sErrorMsg = '';
        ['payrequest_summa','payrequest_tobank','payrequest_expensetype_id'].forEach(function(ids){
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
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('payrequest_summa').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Сумма"])}</li>'; $('payrequest_summa').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк получателя"])}</li>'; $('payrequest_tobank').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата платежа"])}</li>'; $('payrequest_paydate').up('span').addClassName('k-error-colored'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Доходы-расходы"])}</li>'; $('payrequest_expensetype_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorpayrequestlist").innerHTML=sErrorMsg;
          $("errorpayrequestlist").up('div').show();
        } else
          jQuery('#payrequestAddForm').slideUp(300, function() { getPayrequests(); });
      }
      function processAddservicedopagrResponse(e){
        var sErrorMsg = '';
        ['servicedopagr_nomer','servicedopagr_summa'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['servicedopagr_dsdate','servicedopagr_startdate','servicedopagr_enddate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Номер соглашения"])}</li>'; $('servicedopagr_nomer').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('servicedopagr_summa').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата соглашения"])}</li>'; $('servicedopagr_dsdate').up('span').addClassName('k-error-colored'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата начала"])}</li>'; $('servicedopagr_startdate').up('span').addClassName('k-error-colored'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата окончания"])}</li>'; $('servicedopagr_enddate').up('span').addClassName('k-error-colored'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorservicedopagrlist").innerHTML=sErrorMsg;
          $("errorservicedopagrlist").up('div').show();
        } else
          jQuery('#servicedopagrAddForm').slideUp(300, function() { getDopAgrs(); });
      }
      function processAddservicecalculationResponse(e){
        var sErrorMsg = '';
        ['servcalc_summa'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['servcalc_maindate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Сумма платежа"])}</li>'; $('servcalc_summa').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Период"])}</li>'; $('servcalc_maindate').up('span').addClassName('k-error-colored'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.not.unique2.message",args:["Начисление","периодом"])}</li>'; $('servcalc_maindate').up('span').addClassName('k-error-colored'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorservicecalculationlist").innerHTML=sErrorMsg;
          $("errorservicecalculationlist").up('div').show();
        } else
          jQuery('#servicecalculationAddForm').slideUp(300, function() { getCalcs(); });
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
          case 1: getDopAgrs();break;
          case 2: getCalcs();break;
          case 3: getHistory();break;
        }
      }
      function getPayrequests(){
        if(${service?1:0}) $('payrequests_submit_button').click();
      }
      function getDopAgrs(){
        if(${service?1:0}) $('dopagrs_submit_button').click();
      }
      function getCalcs(){
        if(${service?1:0}) $('calcs_submit_button').click();
      }
      function getHistory(){
        if(${service?1:0}) $('history_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
        new Autocomplete('zcompany', {
          serviceUrl:'${resource(dir:"autocomplete",file:"space_arendodatel_autocomplete")}',
          onSelect: function(value, data){
            $('zcompany').focus();
          }
        });
        new Autocomplete('ecompany', {
          serviceUrl:'${resource(dir:"autocomplete",file:"space_arendodatel_autocomplete")}',
          onSelect: function(value, data){
            $('ecompany').focus();
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
      .k-ff { overflow: inherit !important;}
    </style>
  </head>
  <body onload="init()">
    <h3 class="fleft"><g:if test="${service}">Договор услуг №${service.anumber} (${service.id} | ${String.format('%td.%<tm.%<tY %<tT',service.inputdate)})</g:if><g:else>Новый договор услуг</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку услуг</a>
    <div class="clear"></div>
    <g:formRemote name="requestDetailForm" url="${[action:'updateservice',id:service?.id?:0]}" method="post" onSuccess="processResponse(e)">

    <g:if test="${service?.modstatus==0}">
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

    <g:if test="${service}">
      <label for="inputdate" disabled>Дата создания:</label>
      <input type="text" name="inputdate" disabled value="${String.format('%td.%<tm.%<tY %<tT',service.inputdate)}" />
      <label for="status" disabled>Статус:</label>
      <g:select name="status" value="${service.modstatus}" from="['Архив','Активный']" keys="[0,1]" disabled="true"/>
      <hr class="admin" />
    </g:if>

      <label for="zcompany">Заказчик:</label>
      <span class="input-append">
        <input type="text" class="nopad normal" ${service?'disabled':''} id="zcompany" name="zcompany" value="${zcompany?.name}" onblur="getzBankList();"/>
        <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
      </span>
      <div id="zcompanyname_autocomplete" class="autocomplete" style="display:none"></div>
      <label for="ecompany">Исполнитель:</label>
      <span class="input-append">
        <input type="text" class="nopad normal" ${service?'disabled':''} id="ecompany" name="ecompany" value="${ecompany?.name}" onblur="geteBankList();"/>
        <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
      </span>
      <div id="ecompanyname_autocomplete" class="autocomplete" style="display:none"></div>
      <label for="zbank_id">Банк заказчика:</label>
      <span id="zbanklist"><g:select class="fullline" name="zbank_id" value="${service?.zbank_id}" from="${zbanks}" optionValue="name" optionKey="id" noSelection="${['':zcompany?'не выбран':'заказчик не указан']}"/></span>
      <label for="ebank_id">Банк исполнителя:</label>
      <span id="ebanklist"><g:select class="fullline" name="ebank_id" value="${service?.ebank_id}" from="${ebanks}" optionValue="name" optionKey="id" noSelection="${['':ecompany?'не выбран':'исполнитель не указан']}"/></span>
      <label for="atype">Тип договора:</label>
      <g:select name="atype" value="${service?.atype}" from="${stypes}" optionKey="id" optionValue="name" noSelection="${['0':'не указан']}"/>
      <label for="asort">Признак договора:</label>
      <g:select name="asort" value="${service?.asort}" from="['Внешний','Внутренний','Для внешних']" keys="123" noSelection="${['0':'не указан']}"/>
      <hr class="admin" />

      <label for="anumber">Номер договора:</label>
      <input type="text" id="anumber" name="anumber" value="${service?.anumber}"/>
      <label for="summa">Стоимость:</label>
      <input type="text" id="summa" name="summa" ${service?'disabled':''} value="${intnumber(value:service?.summa)}"/>
      <label for="adate">Дата договора:</label>
      <g:datepicker class="normal nopad" style="margin-right:108px" name="adate" value="${service?.adate?String.format('%td.%<tm.%<tY',service.adate):''}"/>
      <label for="enddate">Срок действия:</label>
      <g:datepicker class="normal nopad" name="enddate" value="${service?.enddate?String.format('%td.%<tm.%<tY',service.enddate):''}" disabled="${service?'true':'false'}"/><br/>
      <label for="prolongcondition">Пролонгация</label>
      <g:select name="prolongcondition" value="${service?.prolongcondition}" from="['нет', 'по доп соглашению', 'автоматически']" keys="012"/>
      <label for="prolongterm">Срок пролонгации<br/><small>в месяцах</small></label>
      <input type="text" id="prolongterm" name="prolongterm" value="${service?.prolongcondition!=2?'нет':service?.prolongterm}"/>

      <hr class="admin" />

      <label for="paycondition">Порядок оплаты</label>
      <g:select name="paycondition" value="${service?.paycondition}" from="['ежемесячная оплата','ежеквартальная оплата', 'без оплаты']" keys="120"/>
      <label for="payterm">Число платежа:</label>
      <input type="text" id="payterm" name="payterm" value="${service?.payterm}"/>
      <label for="paytermcondition">Условия оплаты</label>
      <g:select name="paytermcondition" value="${service?.paytermcondition}" from="['за прошлый месяц','за текущий месяц','за следующий месяц']" keys="${1..3}"/>
      <label class="auto" for="is_nds">
        <input type="checkbox" id="is_nds" name="is_nds" value="1" <g:if test="${service?.is_nds!=0}">checked</g:if> />
        С НДС
      </label>
      <br/><label for="project_id">Проект:</label>
      <g:select name="project_id" value="${service?.project_id}" from="${projects}" optionValue="name" optionKey="id" noSelection="${['0':'не выбрано']}"/>
      <label for="responsible">Ответственный:</label>
      <g:select name="responsible" value="${service?.responsible?:session.user.id}" from="${responsiblies}" optionValue="pers_name" optionKey="id" noSelection="${['0':'не выбрано']}"/>
      <label for="description">Описание договора:</label>
      <g:textArea name="description" id="description" value="${service?.description}" />
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" id="comment" value="${service?.comment}" />

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
      <g:if test="${service?.modstatus==1}">
        <input type="button" class="spacing" value="Досрочно закрыть" onclick="closeAgr()"/>
      </g:if>
      <g:if test="${isCanRestore}">
        <input type="button" class="spacing" value="Восстановить" onclick="submitForm(1)"/>
      </g:if>
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(${service?service.modstatus:1})"/>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      </g:if>
      </div>
      <input type="hidden" id="modstatus" name="modstatus" value="1"/>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${service}">
    <div class="tabs">
      <ul class="nav">
        <li><a href="javascript:void(0)" onclick="viewCell(0)">Платежи</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(1)">Доп. соглашения</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(2)">Начисления</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(3)">История</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="payrequestsForm" url="[action:'srpayrequests',id:service.id]" update="details">
      <input type="submit" class="button" id="payrequests_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="dopagrsForm" url="[action:'servicedopagrs',id:service.id]" update="details">
      <input type="submit" class="button" id="dopagrs_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="calcsForm" url="[action:'servicecalculations',id:service.id]" update="details">
      <input type="submit" class="button" id="calcs_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="historyForm" url="[action:'servicehistory',id:service.id]" update="details">
      <input type="submit" class="button" id="history_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>