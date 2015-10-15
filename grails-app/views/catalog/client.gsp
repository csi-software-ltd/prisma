<html>
  <head>
    <title>Prisma: <g:if test="${client}">Клиент № ${client.id}</g:if><g:else>Добавление нового клиента</g:else></title>
    <meta name="layout" content="main" />
    <g:javascript>
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
      }
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processResponse(e){
        $('detail_name').removeClassName('red');
        var sErrorMsg='';
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>Не заполнено обязательное поле "Название"</li>'; $('detail_name').addClassName('red');break;
              case 2: sErrorMsg+='<li>${message(code:"error.not.unique2.message",args:["Клиент","названием"])}</li>'; $('detail_name').addClassName('red');break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${client?1:0}){
          location.reload(true);
        } else if(e.responseJSON.client_id){
          location.assign('${createLink(controller:'catalog',action:'client')}'+'/'+e.responseJSON.client_id);
        } else
          location.assign('${createLink(controller:'catalog',action:'clients')}');
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
          case 0: getPayment();break;
          case 1: getKreditAgr();break;
          case 2: getAgentAgr();break;
          case 3: getSubclients();break;
        }
      }
      function getPayment(){
        if(${client?1:0}) $('payment_submit_button').click();
      } 
      function getKreditAgr(){
        if(${client?1:0}) $('kreditagr_submit_button').click();
      }
      function getAgentAgr(){
        if(${client?1:0}) $('agentagr_submit_button').click();
      }
      function getSubclients(){
        if(${client?1:0}) $('subclients_submit_button').click();
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
    <h3 class="fleft"><g:if test="${client}">Клиент № ${client.id}</g:if><g:else>Добавление нового клиента</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку клиентов</a>
    <div class="clear"></div>

    <div class="error-box" style="display:none">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>

    <g:formRemote url="${[controller:'catalog',action:'updateclient',id:client?.id?:0]}" onSuccess="processResponse(e)" method="POST" name="clientForm">
      <label for="detail_name">Название:</label>
      <input type="text" name="name" id="detail_name" value="${client?.name?:''}"/>
    <g:if test="${client?.is_t}">
      <label for="fee">Абон. плата:</label>
      <input type="text" name="fee" id="fee" value="${number(value:client?.fee)}" />
    </g:if><g:else>
      <label for="parent">Основной клиент:</label>
      <g:select name="parent" value="${client?.parent}" from="${Client.findAllByIdNotEqualAndIs_super(client?.id?:0,1)}" optionKey="id" optionValue="name" noSelection="${['0':'нет']}"/>
    </g:else>
      <label for="modstatus" disabled>Статус:</label>
      <g:select name="modstatus" value="${client?.modstatus}" from="['активный','неактивный']" keys="[1,0]" disabled="true"/>
    <g:if test="${client}">
      <label for="dinsaldo" disabled>Текущий остаток:</label>
      <input type="text" name="dinsaldo" id="dinsaldo" value="${number(value:curclientsaldo+dinclientsaldo)}" disabled />
      <label for="saldo">Начальный остаток</label>
      <input type="text" name="saldo" id="saldo" value="${number(value:client.saldo)}" />
      <label for="addsaldo">Сальдо клиента:</label>
      <input type="text" name="addsaldo" id="addsaldo" value="${number(value:client.addsaldo)}" />
      <label for="midsaldo">Сальдо посредника</label>
      <input type="text" name="midsaldo" id="midsaldo" value="${number(value:client.midsaldo)}" />
    </g:if>
    <g:if test="${client?.parent>0}">
      <label class="auto" for="is_clientcomm">
        <input type="checkbox" id="is_clientcomm" name="is_clientcomm" value="1" <g:if test="${client.is_clientcomm}">checked</g:if> />
        Комиссия клиента
      </label>
      <label class="auto" for="is_middleman">
        <input type="checkbox" id="is_middleman" name="is_middleman" value="1" <g:if test="${client.is_middleman}">checked</g:if> />
        Комиссия посредника
      </label>
    </g:if>
      <div class="clear"></div>
      <div class="fright">
      <g:if test="${iscanedit}">
        <input type="submit" class="button" value="Сохранить"/>
      </g:if>
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      </div>
    </g:formRemote>
    <div class="clear"></div>
    <g:if test="${client}">
      <div class="tabs">
        <ul class="nav">
          <li><a href="javascript:void(0)" onclick="viewCell(0)">Платежи</a></li>
          <li><a href="javascript:void(0)" onclick="viewCell(1)">Кредитные договоры</a></li>
          <li style="${!isagent?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(2)">Агентские договоры</a></li>
          <li style="${!client.is_super?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(3)">Подклиенты</a></li>
        </ul>
        <div class="tab-content">
          <div class="inner">
            <div id="details"></div>
          </div>
        </div>
      </div>
      <g:formRemote name="paymentform" url="[action:'clientpayments',id:client.id]" update="[success:'details']">
        <input type="submit" class="button" id="payment_submit_button" value="Показать" style="display:none" />
      </g:formRemote>
      <g:formRemote name="kreditagrform" url="[action:'clientkredits',id:client.id]" update="[success:'details']">
        <input type="submit" class="button" id="kreditagr_submit_button" value="Показать" style="display:none" />
      </g:formRemote>
      <g:formRemote name="agentagrform" url="[action:'clientagentagrs',id:client.id]" update="[success:'details']">
        <input type="submit" class="button" id="agentagr_submit_button" value="Показать" style="display:none" />
      </g:formRemote>
      <g:formRemote name="subclientsform" url="[action:'subclients',id:client.id]" update="[success:'details']">
        <input type="submit" class="button" id="subclients_submit_button" value="Показать" style="display:none" />
      </g:formRemote>
    </g:if>
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:'catalog', action:'clients',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>