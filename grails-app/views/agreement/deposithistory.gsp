<style type="text/css">
  .list td,.list th { font-size: 12px }
</style>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата</th>
          <th>Сумма<br/>Ставка</th>
          <th>Срок депозита</th>
          <th>Проценты</th>
          <th>Комментарий</th>
          <th>Автор</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${history}" status="i" var="record">
        <tr align="center">
          <td width="90">${shortDate(date:record.inputdate)}</td>
          <td>${number(value:record.summa)}<br/>${number(value:record.rate)}</td>
          <td>${record.enddate?String.format('%td.%<tm.%<tY',record.enddate):'бессрочный'}</td>
          <td>${record.term==1?'в конце срока':'ежемесячно'}</td>
          <td width="350">${record.comment}</td>
          <td>${record.admin_name}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>