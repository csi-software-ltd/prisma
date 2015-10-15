<html>
  <head>
    <title>Prisma: <g:if test="${flizing}">Договор фин. лизинга №${flizing.anumber}</g:if><g:else>Новый договор фин. лизинга</g:else></title>
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
      function closeAgr(){
        if(confirm('Вы уверены, что хотите досрочно закрыть договор фин. лизинга №${flizing?.anumber}?')) { submitForm(0) }
      }
      function processResponse(e){
        $("saveinfo").up('div').hide();
        $("errorlist").up('div').hide();
        var sErrorMsg = '';
        ['anumber','summa','rate','fldatel','flpoluchatel','flbank'].forEach(function(ids){
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
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Лизингодатель"])}</li>'; $('fldatel').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Лизингодатель"])}</li>'; $('fldatel').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Лизингодателя","Лизингодатель"])}</li>'; $('fldatel').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Лизингополучатель"])}</li>'; $('flpoluchatel').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Лизингополучатель"])}</li>'; $('flpoluchatel').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Лизингополучателя","Лизингополучатель"])}</li>'; $('flpoluchatel').addClassName('red'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк"])}</li>'; $('flbank').addClassName('red'); break;
              case 8: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Банк"])}</li>'; $('flbank').addClassName('red'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Банк","Банк"])}</li>'; $('flbank').addClassName('red'); break;
              case 10: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата заключения"])}</li>'; $('adate').up('span').addClassName('k-error-colored'); break;
              case 11: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок договора"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 12: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('summa').addClassName('red'); break;
              case 13: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Ставка"])}</li>'; $('rate').addClassName('red'); break;
              case 14: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Номер договора"])}</li>'; $('anumber').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(${flizing?1:0}){
          $("saveinfo").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(e.responseJSON.flizing){
          location.assign('${createLink(controller:controllerName,action:'finlizing')}'+'/'+e.responseJSON.flizing);
        } else
          returnToList();
      }
      function processAddFlperiodResponse(e){
        var sErrorMsg = '';
        ['fmonth_month','fmonth_year'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Месяц"])}</li>'; $('fmonth_month').addClassName('red'); $('fmonth_year').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.not.unique2.message",args:["Период","Месяцем"])}</li>'; $('fmonth_month').addClassName('red'); $('fmonth_year').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorflperiodlist").innerHTML=sErrorMsg;
          $("errorflperiodlist").up('div').show();
        } else
          jQuery('#flperiodAddForm').slideUp(300, function() { getBalance(); });
      }
      function processAddfinlizingdopagrResponse(e){
        var sErrorMsg = '';
        ['finlizingdopagr_nomer','finlizingdopagr_summa','finlizingdopagr_rate','finlizingdopagr_flpoluchatel'].forEach(function(ids){
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
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Ставка"])}</li>'; $('finlizingdopagr_rate').addClassName('red'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Лизингополучатель"])}</li>'; $('finlizingdopagr_flpoluchatel').addClassName('red'); break;
              case 8: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Лизингополучатель"])}</li>'; $('finlizingdopagr_flpoluchatel').addClassName('red'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Лизингополучателя","Лизингополучатель"])}</li>'; $('finlizingdopagr_flpoluchatel').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorfinlizingdopagrlist").innerHTML=sErrorMsg;
          $("errorfinlizingdopagrlist").up('div').show();
        } else
          jQuery('#finlizingdopagrAddForm').slideUp(300, function() { getDopAgrs(); });
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
          case 0: getPayments();break;
          case 1: getBalance();break;
          case 2: getDopAgrs();break;
          case 3: getHistory();break;
        }
      }
      function getPayments(){
        if(${flizing?1:0}) $('payments_submit_button').click();
      }
      function getBalance(){
        if(${flizing?1:0}) $('balance_submit_button').click();
      }
      function getDopAgrs(){
        if(${flizing?1:0}) $('dopagrs_submit_button').click();
      }
      function getHistory(){
        if(${flizing?1:0}) $('history_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
        if (${flizing?0:1}) {
          new Autocomplete('fldatel', {
            serviceUrl:'${resource(dir:"autocomplete",file:"companyname_ext_autocomplete")}'
          });
          new Autocomplete('flpoluchatel', {
            serviceUrl:'${resource(dir:"autocomplete",file:"companyname_int_autocomplete")}'
          });
          new Autocomplete('flbank', {
            serviceUrl:'${resource(dir:"autocomplete",file:"companyname_bank_autocomplete")}'
          });
        }
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
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку лизингов</a>
    <h3 class="fleft"><g:if test="${flizing}">Договор долгосрочного финансового лизинга №${flizing.anumber} (${flizing.id} | ${String.format('%td.%<tm.%<tY',flizing.inputdate)})</g:if><g:else>Новый договор фин. лизинга</g:else></h3>
    <div class="clear"></div>
    <g:formRemote name="flizingDetailForm" url="${[action:'updatefinlizing',id:flizing?.id?:0]}" method="post" onSuccess="processResponse(e)">

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

    <g:if test="${flizing}">
      <label for="status" disabled>Статус:</label>
      <g:select name="status" value="${flizing.modstatus}" from="['К удалению','Архив','Активный']" keys="${-1..1}" disabled="true"/><br/>
      <label for="bodydebt" disabled>Текущий остаток:</label>
      <input type="text" id="bodydebt" disabled name="bodydebt" value="${number(value:summary.bodydebt)}"/>
      <label for="balance" disabled>Баланс:</label>
      <input type="text" id="balance" disabled name="balance" value="${number(value:summary.balance)}"/>

      <hr class="admin" />

    </g:if>

      <label for="fldatel">Лизингодатель</label>
      <input type="text" class="fullline" id="fldatel" name="fldatel" ${flizing?'disabled':''} value="${fldatel}"/>
      <label for="flpoluchatel">Лизингополучатель</label>
      <input type="text" class="fullline" id="flpoluchatel" name="flpoluchatel" ${flizing?'disabled':''} value="${flpoluchatel}"/>
      <label for="flbank">Банк</label>
      <input type="text" class="fullline" id="flbank" name="flbank" ${flizing?'disabled':''} value="${flbank}"/>

      <hr class="admin" />

      <label for="anumber">Номер договора:</label>
      <input type="text" class="fullline" id="anumber" name="anumber" value="${flizing?.anumber}"/>
      <label for="adate">Дата заключения:</label>
      <g:datepicker class="normal nopad" style="margin-right:108px" style="margin-right:108px" name="adate" value="${flizing?.adate?String.format('%td.%<tm.%<tY',flizing.adate):''}" />
      <label for="enddate">Срок договора:</label>
      <g:datepicker class="normal nopad" style="margin-right:108px" name="enddate" value="${flizing?.enddate?String.format('%td.%<tm.%<tY',flizing.enddate):''}" disabled="${flizing?'true':'false'}"/>
      <label for="summa">Сумма:</label>
      <input type="text" id="summa" name="summa" ${flizing?'disabled':''} value="${number(value:flizing?.summa)}" />
      <label for="rate">Ставка:</label>
      <input type="text" id="rate" name="rate" ${flizing?'disabled':''} value="${number(value:flizing?.rate)}"/>
      <label for="description">Описание:</label>
      <g:textArea name="description" id="description" value="${flizing?.description}" />
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" id="comment" value="${flizing?.comment}" />
      <label for="responsible">Ответственный:</label>
      <g:select name="responsible" value="${flizing?.responsible?:session.user.id}" from="${responsiblies}" optionValue="pers_name" optionKey="id" noSelection="${['0':'не выбрано']}"/>

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="button" class="reset spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
      <g:if test="${flizing?.modstatus==1}">
        <input type="button" class="spacing" value="Досрочно закрыть" onclick="closeAgr()"/>
      </g:if>
      <g:if test="${isCanRestore}">
        <input type="button" class="spacing" value="Восстановить" onclick="submitForm(1)"/>
      </g:if>
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(${flizing?flizing.modstatus:1})"/>
      </g:if>
      </div>
      <input type="hidden" id="modstatus" name="modstatus" value="1"/>
      <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${flizing}">
    <div class="tabs">
      <ul class="nav">
        <li><a href="javascript:void(0)" onclick="viewCell(0)">Платежи</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(1)">Расчет</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(2)">Доп. соглашения</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(3)">История</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="balanceForm" url="[action:'finlizingpayments',id:flizing.id]" update="details">
      <input type="submit" class="button" id="payments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="balanceForm" url="[action:'finlizingbalance',id:flizing.id]" update="details">
      <input type="submit" class="button" id="balance_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="dopagrsForm" url="[action:'finlizingdopagrs',id:flizing.id]" update="details">
      <input type="submit" class="button" id="dopagrs_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="historyForm" url="[action:'finlizinghistory',id:flizing.id]" update="details">
      <input type="submit" class="button" id="history_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>