<style type="text/css">
  .list td,.list th { font-size: 12px }
</style>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата</th>
          <th>Дата на</th>
          <th>Сумма</th>
          <th>Комментарий</th>
          <th>Надбавка</th>
          <th>Статус</th>
          <th>Автор</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${history}" status="i" var="record">
        <tr align="center">
          <td>${shortDate(date:record.inputdate)}</td>
          <td>${String.format('%td.%<tm.%<tY',record.reqdate)}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td>${record.comment}</td>
          <td>${number(value:record.margin)}</td>
          <td>${cashstatus[record.modstatus]}</td>
          <td>${record.admin_name}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>