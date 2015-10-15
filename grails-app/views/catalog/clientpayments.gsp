<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th width="60">Дата</th>
          <th>№ платежа</th>
          <th>Сумма платежа<br/>НДС</th>
          <th>Плательщик<br/>ИНН</th>
          <th>Получатель<br/>ИНН</th>
          <th>Назначение</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${payments.records}" status="i" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td><g:link style="z-index:1" controller="payment" action="paymentdetail" id="${record.id}" target="_blank">${record.platnumber}</g:link></td>
	        <td>${number(value:record.summa)}<br/>${number(value:record.summands)}</td>
          <td>${record.fromcompany}<br/>${record.frominn}</td>
          <td>${record.tocompany}<br/>${record.toinn}</td>
          <td>${record.destination}</td>
	      </tr>
      </g:each>
      <g:if test="${!payments.records}">
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