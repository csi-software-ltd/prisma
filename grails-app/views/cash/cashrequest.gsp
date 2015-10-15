<html>
  <head>
    <title>Prisma: Касса - Запрос на пополнение №${cashrequest.id}</title>
    <meta name="layout" content="main" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function topayments(){
        $("moveToPaymentsForm").submit();
      }
      function removefromrequest(iId){
        if(confirm('Подтверждаете удаление заявки?'))
          <g:remoteFunction controller='cash' action='removefromrequest' id="${cashrequest.id}" params="'cashzakaz_id='+iId" onSuccess="location.reload(true)" />
      }
      function addtorequest(iId){
        <g:remoteFunction controller='cash' action='addtorequest' id="${cashrequest.id}" params="'cashzakaz_id='+iId" onSuccess="location.reload(true)" />
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['summa','margin'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('summa').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Надбавка"])}</li>'; $('margin').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else {
          location.reload(true);
        }
      }
      function submitForm(iStatus){
        if(iStatus==4&&!confirm('Перевести в работу?')) return false
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
          case 0: getZakazes();break;
          case 1: getNewZakazes();break;
          case 2: getHistory();break;
        }
      }
      function getZakazes(){
        if (${session.user.cashaccess!=4}) $('zakazes_submit_button').click();
      }
      function getNewZakazes(){
        if (${session.user.cashaccess==3}) $('newzakazes_submit_button').click();
      }
      function getHistory(){
        if (${session.user.cashaccess!=4}) $('history_submit_button').click();
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
      .tabs a:hover,.tabs a.active,.tabs a.active i{color:#000}
    </style>
  </head>
  <body onload="getZakazes()">
    <h3 class="fleft">Запрос на пополнение №${cashrequest.id}<g:if test="${session.user.cashaccess==4&&cashrequest.modstatus == 5&&payrequest}">&nbsp;(Платеж № <g:link controller="payment" action="tpayment" id="${payrequest?.id}">${payrequest?.id}</g:link>)</g:if></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку запросов</a>
    <div class="clear"></div>
    <g:formRemote name="cashrequestDetailForm" url="${[action:'updatecashrequest',id:cashrequest.id]}" method="post" onSuccess="processResponse(e)">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="cashrequest_modstatus" disabled>Статус заявки:</label>
      <input type="text" id="cashrequest_modstatus" disabled value="${cashstatus[cashrequest.modstatus]}" />
      <label for="initiator" disabled>Инициатор:</label>
      <input type="text" id="initiator" disabled value="${initiator}" />
      <label for="inputdate" disabled>Дата создания:</label>
      <input type="text" name="inputdate" disabled value="${String.format('%td.%<tm.%<tY %<tT',cashrequest.inputdate)}" />
      <label for="reqdate" disabled>На дату:</label>
      <input type="text" name="reqdate" disabled value="${String.format('%td.%<tm.%<tY',cashrequest.reqdate)}" />
      <hr class="admin" />

      <label for="summa">Сумма запроса:</label>
      <input type="text" id="summa" name="summa" value="${intnumber(value:cashrequest.summa)}" ${!(session.user.cashaccess in [3,5])||cashrequest.modstatus>3?'readonly':''}/>
      <label for="margin">Надбавка:</label>
      <input type="text" class="auto" id="margin" name="margin" value="${number(value:cashrequest.margin)}" ${session.user.cashaccess!=4||cashrequest.modstatus>4?'readonly':''}/>
    <g:if test="session.user.cashaccess==4">
      <label for="summa">Сумма с надбавкой</label>
      <input type="text" id="summa" name="summa" value="${number(value:cashrequest.summa / (1 - cashrequest.margin / 100))}" disabled/>
      <label for="summa">Расход</label>
      <input type="text" id="summa" name="summa" value="${number(value:cashrequest.summa * cashrequest.margin / (100 - cashrequest.margin))}" disabled/>
    </g:if>
    <g:if test="${session.user.cashaccess in [3,5]}">
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" id="comment" value="${cashrequest.comment}" />
    </g:if>

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(${cashrequest.modstatus})"/>
      <g:if test="${session.user.cashaccess==3&&cashrequest.modstatus == 1}">
        <input type="button" class="spacing" value="На согласование" onclick="submitForm(2)"/>
      </g:if>
      <g:if test="${session.user.cashaccess==5&&cashrequest.modstatus in [2,3]}">
        <input type="button" class="spacing" value="Отказать" onclick="submitForm(6)"/>
      </g:if>
      <g:if test="${session.user.cashaccess==5&&cashrequest.modstatus in [2,6]}">
        <input type="button" class="spacing" value="Подтвердить" onclick="submitForm(3)"/>
      </g:if>
      <g:if test="${session.user.cashaccess == 3&&cashrequest.modstatus in 1..3}">
        <input type="button" class="spacing" value="В работу" onclick="submitForm(4)"/>
      </g:if>
      <g:if test="${session.user.cashaccess == 3&&cashrequest.modstatus == 4}">
        <input type="button" class="spacing" value="Вернуть из работы" onclick="submitForm(1)"/>
      </g:if>
      <g:if test="${session.user.cashaccess==4&&cashrequest.modstatus == 4&&cashrequest.margin>0}">
        <input type="button" class="spacing" value="Исполнить" onclick="submitForm(5)"/>
      </g:if>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      </div>
      <input type="hidden" id="modstatus" name="modstatus" value="1"/>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${cashrequest&&session.user.cashaccess!=4}">
    <div class="tabs">
      <ul class="nav">
        <li class="selected"><a href="javascript:void(0)" onclick="viewCell(0)">Заявки</a></li>
        <li style="${session.user.cashaccess!=3?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(1)">Новые заявки</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(2)">История</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="zakazForm" url="[action:'cashrequestzakaz',id:cashrequest.id]" update="details">
      <input type="submit" class="button" id="zakazes_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="newzakazForm" url="[action:'cashrequestnewzakaz',id:cashrequest.id]" update="details">
      <input type="submit" class="button" id="newzakazes_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="historyForm" url="[action:'cashrequesthistory',id:cashrequest.id]" update="details">
      <input type="submit" class="button" id="history_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>