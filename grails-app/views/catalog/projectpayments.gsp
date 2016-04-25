<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата платежа<br/>Номер платежа</th>
          <th>Тип платежа</th>
          <th>Категория</th>
          <th>Контрагенты</th>
          <th>Сумма</th>
          <th>Статус</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${payments.records}" status="i" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}<br/><g:link controller="payment" action="payrequestdetail" id="${record.id}" target="_blank">${record.id}</g:link></td>
          <td>
          <g:if test="${record.paytype==1}"><abbr title="Исходящий"><i class="icon-signout icon-large"></i></abbr></g:if>
          <g:elseif test="${record.paytype==3}"><abbr title="Внутренний"><i class="icon-refresh icon-large"></i></abbr></g:elseif>
          <g:else><abbr title="Входящий"><i class="icon-signin icon-large"></i></abbr></g:else>
          </td>
          <td>${record.paycat==1?'договорной':record.paycat==2?'бюджетный':record.paycat==3?'персональный':record.paycat==4?'прочий':record.paycat==5?'банковский':'счета'}</td>
          <td>${record.fromcompany_name?:record.fromcompany}<br/>${record.tocompany_name?:record.tocompany}</td>
          <td>${number(value:record.summa)}</td>
          <td>${record.modstatus==1?'в задании':record.modstatus==2?'выполнен':record.modstatus==3?'подтвержден':'необработанный'}</td>
        </tr>
      </g:each>
      <g:if test="${!payments}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Платежей не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>