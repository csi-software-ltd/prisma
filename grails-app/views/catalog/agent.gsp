<html>
  <head>
    <title>Prisma: Справочники - <g:if test="${agent}">Агент ${agent.name}</g:if><g:else>Новый агент</g:else></title>
    <meta name="layout" content="main" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['aname'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Фио"])}</li>'; $('aname').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${agent?1:0}){
          location.reload(true);
        } else if(e.responseJSON.agent){
          location.assign('${createLink(controller:controllerName,action:'agent')}'+'/'+e.responseJSON.agent);
        } else
          returnToList();
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
          case 0: getAgreements();break;
          case 1: getPayments();break;
          case 2: getCashpayments();break;
          case 3: getFixes();break;
        }
      }
      function getAgreements(){
        if(${agent?1:0}) $('agreements_submit_button').click();
      }
      function getPayments(){
        if(${agent?1:0}) $('payments_submit_button').click();
      }
      function getCashpayments(){
        if(${agent?1:0}) $('cashpayments_submit_button').click();
      }
      function getFixes(){
        if(${agent?1:0}) $('agentfixes_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px}
    </style>
  </head>
  <body onload="init()">
    <h3 class="fleft"><g:if test="${agent}">Агент ${agent.name}</g:if><g:else>Новый агент</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку агентов</a>
    <div class="clear"></div>
    <g:formRemote name="agentDetailForm" url="${[action:'updateagent',id:agent?.id?:0]}" method="post" onSuccess="processResponse(e)">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="aname">Фио:</label>
      <input type="text" class="fullline" id="aname" name="aname" value="${agent?.name}" maxlength="250"/>
      <label for="client_id">Клиент:</label>
      <g:select name="client_id" value="${agent?.client_id}" from="${client}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}"/>
      <label for="modstatus" disabled>Статус:</label>
      <g:select name="modstatus" value="${agent?.modstatus}" from="['активный','неактивный']" keys="[1,0]" disabled="true"/>

      <hr class="admin" />
  <g:if test="${agent}">
      <label for="accrued">Начислено:</label>
      <input type="text" id="accrued" value="${number(value:accrued)}"/>
      <label for="agreed">Согласовано:</label>
      <input type="text" id="agreed" value="${number(value:agreed)}"/>
      <label for="cashpaid">Оплачено налом:</label>
      <input type="text" id="cashpaid" value="${number(value:cashpaid)}"/>
      <label for="paid">Безналом:</label>
      <input type="text" id="paid" value="${number(value:paid)}"/>
      <label for="agentfix">Прямые выплаты:</label>
      <input type="text" id="agentfix" value="${number(value:agentfix)}"/>
      <label for="balance">Баланс:</label>
      <input type="text" id="balance" value="${number(value:balance)}"/>

      <hr class="admin" />

  </g:if>
      <div class="fright" id="btns" style="padding-top:10px">
      <g:if test="${iscanedit}">
        <input class="spacing" type="submit" id="submit_button" value="Сохранить" />
      </g:if>
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      </div>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${agent}">
    <div class="tabs">
      <ul class="nav">
        <li><a href="javascript:void(0)" onclick="viewCell(0)">Договоры</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(1)">Безналичные платежи</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(2)">Наличные платежи</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(3)">Корректировки</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="agreementsForm" url="[action:'agentagreements',id:agent.id]" update="details">
      <input type="submit" class="button" id="agreements_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="paymentsForm" url="[action:'agentpayments',id:agent.id]" update="details">
      <input type="submit" class="button" id="payments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="cashpaymentsForm" url="[action:'agentcashpayments',id:agent.id]" update="details">
      <input type="submit" class="button" id="cashpayments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="agentfixesForm" url="[action:'agentfixes',id:agent.id]" update="details">
      <input type="submit" class="button" id="agentfixes_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'agents',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>