<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Проект</th>
          <th>Текущая сумма депозита</th>
          <th>Текущий остаток процентов</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${projects}" status="i" var="record">
        <tr align="center">
          <td width="350">${record.name}</td>
          <td>${number(value:record.computeIndepositSaldo(deposit.id))}</td>
          <td>${number(value:record.computeIndepositProjectPercent(deposit.id))}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>