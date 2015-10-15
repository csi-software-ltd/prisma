<html>
  <head>
    <title>Prisma: <g:if test="${deposit}">Договор депозита №${deposit.id}</g:if><g:else>Новый договор депозита</g:else></title>
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
      function processResponse(e){
        $("saveinfo").up('div').hide();
        $("errorlist").up('div').hide();
        var sErrorMsg = '';
        ['bank','summa','rate'].forEach(function(ids){
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
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк"])}</li>'; $('bank').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата заключения"])}</li>'; $('adate').up('span').addClassName('k-error-colored'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('summa').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Ставка"])}</li>'; $('rate').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок депозита"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
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
          location.assign('${createLink(controller:controllerName,action:'deposit')}'+'/'+e.responseJSON.deposit);
        } else
          returnToList();
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
          case 0: getBody();break;
          case 1: getPerc();break;
          case 2: getHistory();break;
        }
      }
      function getBody(){
        if(${deposit?1:0}) $('body_submit_button').click();
      }
      function getPerc(){
        if(${deposit?1:0}) $('perc_submit_button').click();
      }
      function getHistory(){
        if(${deposit?1:0}) $('history_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
        jQuery("#adate").mask("99.99.9999",{placeholder:" "});
        jQuery("#enddate").mask("99.99.9999",{placeholder:" "});
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
    <h3 class="fleft"><g:if test="${deposit}">Договор депозита №${deposit.id} (${String.format('%td.%<tm.%<tY %<tT',deposit.inputdate)})</g:if><g:else>Новый договор депозита</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку депозитов</a>
    <div class="clear"></div>
    <g:formRemote name="depositDetailForm" url="${[action:'updatedeposit',id:deposit?.id?:0]}" method="post" onSuccess="processResponse(e)">

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
      <label for="status" disabled>Статус:</label>
      <g:select name="status" value="${deposit.modstatus}" from="['К удалению','Архив','Активный']" keys="${-1..1}" disabled="true"/>
      <label for="cursumma" disabled>Текущий депозит:</label>
      <input type="text" id="cursumma" disabled name="cursumma" value="${number(value:cursumma)}"/>

      <hr class="admin" />

    </g:if>

      <label for="anumber">Номер договора:</label>
      <input type="text" id="anumber" name="anumber" value="${deposit?.anumber}"/>
      <label for="adate">Дата заключения:</label>
      <g:datepicker class="normal nopad" name="adate" value="${deposit?.adate?String.format('%td.%<tm.%<tY',deposit.adate):''}" />
      <label for="bank">Банк:</label>
      <g:select class="fullline" id="bank" name="bank" from="${bankcompanies}" value="${deposit?.bank}" optionValue="legalname" optionKey="id" noSelection="${['0':'не выбрано']}" disabled="${deposit?true:false}"/>
      <label for="dtype">Тип договора:</label>
      <g:select id="dtype" name="dtype" value="${deposit?.dtype}" from="['Бессрочный','Срочный']" keys="01"/>
      <label for="enddate">Срок депозита:</label>
      <g:datepicker class="normal nopad" name="enddate" value="${deposit?.enddate?String.format('%td.%<tm.%<tY',deposit.enddate):''}"/>
      <label for="summa">Сумма:</label>
      <input type="text" id="summa" name="summa" value="${number(value:deposit?.summa)}" />
      <label for="valuta_id">Валюта депозита:</label>
      <g:select id="valuta_id" name="valuta_id" from="${Valuta.findAllByModstatus(1)}" value="${deposit?.valuta_id?:857}" optionValue="name" optionKey="id"/>
      <label for="rate">Ставка в годовых:</label>
      <input type="text" id="rate" name="rate" value="${number(value:deposit?.rate)}"/>
      <label for="term">Выплата процентов</label>
      <g:select id="term" name="term" value="${deposit?.term}" from="['в конце срока','ежемесячно']" keys="12"/>
      <label for="startsumma">Начальное сальдо:<br/><small>по телу</small></label>
      <input type="text" id="startsumma" name="startsumma" value="${number(value:deposit?.startsumma)}"/>
      <label for="startprocent">Начальное сальдо:<br/><small>по процентам</small></label>
      <input type="text" id="startprocent" name="startprocent" value="${number(value:deposit?.startprocent)}"/>
      <label for="startsaldodate">Дата сальдо:</label>
      <g:datepicker class="normal nopad" name="startsaldodate" value="${deposit?.startsaldodate?String.format('%td.%<tm.%<tY',deposit.startsaldodate):''}"/><br/>
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" id="comment" value="${deposit?.comment}" />
      <input type="hidden" id="modstatus" name="modstatus" value="1"/>
      <input style="display:none" type="submit" id="submit_button" value="Сохранить" />

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="button" class="reset spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(${deposit?deposit.modstatus:1})"/>
      </g:if>
      </div>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${deposit}">
    <div class="tabs">
      <ul class="nav">
        <li><a href="javascript:void(0)" onclick="viewCell(0)">Платежи по вкладу</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(1)">Платежи по процентам</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(2)">История</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="balanceForm" url="[action:'depositbodypayments',id:deposit.id]" update="details">
      <input type="submit" class="button" id="body_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="balanceForm" url="[action:'depositpercpayments',id:deposit.id]" update="details">
      <input type="submit" class="button" id="perc_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="historyForm" url="[action:'deposithistory',id:deposit.id]" update="details">
      <input type="submit" class="button" id="history_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>