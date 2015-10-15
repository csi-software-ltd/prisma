<table class="list" style="width:100%">
  <thead>
    <tr>
      <th>Клиент<br/>Банк</th>
      <th>Договор<br/>Дата</th>
      <th>Сумма</th>
    </tr>
  </thead>
  <tbody>
  <g:each in="${newtechkredits}" status="i" var="record">
    <tr align="center">
      <td>${record.client_name}<br/>${record.bank_name}</td>
      <td><g:link style="z-index:1" controller="agreement" action="kredit" id="${record.id}" target="_blank">${record.anumber}</g:link><br/>${String.format('%td.%<tm.%<tY',record.adate)}</td>
      <td nowrap>${intnumber(value:record.summa)}<i class="icon-${valutas[record.valuta_id]}"></i></td>
    </tr>
  </g:each>
  </tbody>
</table>
