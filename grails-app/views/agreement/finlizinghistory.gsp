<style type="text/css">
  .list td,.list th { font-size: 12px }
</style>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата</th>
          <th>Номер<br/>Дата<br/>Окончание</th>
          <th>Сумма<br/>Ставка</th>
          <th>Описание<br/>Комментарий</th>
          <th>Автор</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${history}" status="i" var="record">
        <tr align="center">
          <td width="90">${shortDate(date:record.inputdate)}</td>
          <td>${record.anumber}<br/>${String.format('%td.%<tm.%<tY',record.adate)}<br/>${String.format('%td.%<tm.%<tY',record.enddate)}</td>
          <td>${number(value:record.summa)}<br/>${number(value:record.rate)}</td>
          <td width="400">${record.description}<br/>${record.comment}</td>
          <td>${record.admin_name}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>