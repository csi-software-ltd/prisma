<html>
  <head>
    <title>Prisma: Платежи - Сделка №${deal.id}</title>
    <meta name="layout" content="main" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function togglecheck(){
        if(document.getElementById('groupcheckbox').checked)
          jQuery('#addrequestDealForm :checkbox:not(:checked):not(:disabled)').each(function(){ this.checked=true; });
        else
          jQuery('#addrequestDealForm :checkbox:checked').each(function(){ this.checked=false; });
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
          case 0: getDealPayrequests();break;
          case 1: getDealAddPayrequests();break;
        }
      }
      function getDealPayrequests(){
        if(${deal?1:0}) $('dealprequests_submit_button').click();
      }
      function getDealAddPayrequests(){
        if(${deal?1:0}) $('dealaddprequests_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px}
      .tabs a:hover,.tabs a.active,.tabs a.active i{color:#000}
    </style>
  </head>
  <body onload="init()">
    <h3 class="fleft">Сделка №${deal.id}</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку сделок</a>
    <div class="clear"></div>
    <form>

      <label for="client_id" disabled>Клиент:</label>
      <g:select name="client_id" value="${deal.client_id}" from="${Client.list()}" optionKey="id" optionValue="name" disabled="true"/>
      <label for="modstatus" disabled>Статус:</label>
      <input type="text" id="modstatus" disabled name="modstatus" value="${deal.modstatus?'согласована':'новая'}"/>
      <label for="dstart" disabled>Дата начала:</label>
      <input type="text" id="dstart" disabled name="dstart" value="${String.format('%td.%<tm.%<tY',deal.dstart)}"/>
      <label for="dend" disabled>Дата окончания:</label>
      <input type="text" id="dend" disabled name="dend" value="${String.format('%td.%<tm.%<tY',deal.dend)}"/>
      <label for="income" disabled>Приход:</label>
      <input type="text" id="income" name="income" disabled value="${number(value:deal.income)}"/>
      <label for="outlay" disabled>Выход:</label>
      <input type="text" id="outlay" name="outlay" disabled value="${number(value:deal.outlay)}"/>
      <label for="commission" disabled>Комиссия:</label>
      <input type="text" id="commission" name="commission" disabled value="${number(value:deal.commission)}"/>
      <label for="dealsaldo" disabled>Сальдо:</label>
      <input type="text" id="dealsaldo" name="dealsaldo" disabled value="${number(value:deal.dealsaldo)}"/>
      <label for="subcommission" disabled>К возврату:</label>
      <input type="text" id="subcommission" name="subcommission" disabled value="${number(value:deal.subcommission)}"/>
      <label for="retcommission" disabled>Возврат:</label>
      <input type="text" id="retcommission" name="retcommission" disabled value="${number(value:deal.retcommission)}"/>
      <label for="repayment" disabled>Учет списания:</label>
      <input type="text" id="repayment" name="repayment" disabled value="${number(value:deal.repayment)}"/>

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <a class="button" href="${createLink(controller:'payment',action:'printdeal',id:deal.id)}" target="_blank">
          Отчет по сделке &nbsp;<i class="icon-angle-right icon-large"></i>
        </a>
      <g:if test="${iscanedit}">
      <g:if test="${!ishavepayments}">
        <g:remoteLink class="button" style="z-index:1" url="${[controller:'payment',action:'deletedeal',id:deal.id]}" onSuccess="returnToList()">Удалить сделку &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink>
      </g:if><g:elseif test="${!deal.modstatus}">
        <g:remoteLink class="button" style="z-index:1" url="${[controller:'payment',action:'confirmdeal',id:deal.id]}" onSuccess="location.reload(true)">Согласовать сделку &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink>
      </g:elseif><g:elseif test="${deal.modstatus==1}">
        <g:remoteLink class="button" style="z-index:1" url="${[controller:'payment',action:'cancelldeal',id:deal.id]}" onSuccess="location.reload(true)">Отменить согласование &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink>
      </g:elseif>
      </g:if>
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      </div>
    </form>
    <div class="clear"></div>
    <div class="tabs">
      <ul class="nav">
        <li><a href="javascript:void(0)" onclick="viewCell(0)">Платежи сделки</a></li>
        <li style="${deal.modstatus?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(1)">Новые платежи</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="dealprequestsForm" url="[action:'dealprequests',id:deal.id]" update="details">
      <input type="submit" class="button" id="dealprequests_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="dealaddprequestsForm" url="[action:'dealaddprequests',id:deal.id]" update="details">
      <input type="submit" class="button" id="dealaddprequests_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
