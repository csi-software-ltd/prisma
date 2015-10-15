<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата</th>
          <th>Договор</th>
          <th>Дата договора<br/>Срок</th>
          <th>Категория</th>
          <th>Сумма</th>
          <th>Тип оплаты</th>
          <th>Комментарий</th>
          <th>Ответственный</th>
          <th>Автор</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${history}" status="i" var="record">
        <tr align="center">
          <td>${shortDate(date:record.inputdate)}</td>
          <td>${record.anumber}</td>
          <td>${String.format('%td.%<tm.%<tY',record.adate)}<g:if test="${record.enddate}"><br/>${String.format('%td.%<tm.%<tY',record.enddate)}</g:if></td>
          <td>${tradecats[record.tradecat_id]}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td>${record.paytype?'Единовременно':'Регулярные платежи'}</td>
          <td>${record.comment}</td>
          <td>${record.responsible_name}</td>
          <td>${record.admin_name}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>