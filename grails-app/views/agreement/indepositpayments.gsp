<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата платежа</th>
          <th>Сумма</th>
          <th>Тип платежа</th>
          <th>Сумма по телу</th>
          <th>Сумма по процентам</th>
          <th>Платеж по выписке</th>
          <th>Остаток тела</th>
          <th>Статус</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${payrequests}" var="record">
        <%bodydebt += record.paytype==1?-record.depbody:record.depbody%>
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td>${number(value:record.summa)}</td>
          <td>${record.paytype==1?'расход':'приход'}</td>
          <td>${number(value:record.depbody)}</td>
          <td>${number(value:record.depprc)}</td>
          <td>
          <g:if test="${payments[record.id]}">
            <g:link style="z-index:1" controller="payment" action="paymentdetail" id="${payments[record.id]}" target="_blank">${payments[record.id]}</g:link>
          </g:if><g:else>нет</g:else>
          </td>
          <td>${number(value:bodydebt)}</td>
          <td>${record.modstatus==1?'В работе':record.modstatus==2?'Выполнен':record.modstatus==3?'Подтвержден':'Необработанный'}</td>
        </tr>
      </g:each>
      <g:if test="${!payrequests}">
        <tr>
          <td colspan="8" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Платежей не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>