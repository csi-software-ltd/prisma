<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата платежа</th>
          <th>Номер платежа</th>
          <th>Сумма</th>
          <th>Назначение</th>
          <th>Доп. платеж</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${payments}" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td><g:link controller="payment" action="paymentdetail" id="${record.id}">${record.platnumber}</g:link></td>
          <td>${number(value:record.summa)}</td>
          <td>${record.destination}</td>
          <td>${record.is_dop?'Да':'Нет'}</td>
        </tr>
      </g:each>
      <g:if test="${!payments}">
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Платежей не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>