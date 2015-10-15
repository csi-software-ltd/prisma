<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата события</th>
          <th>Тип события</th>
          <th>Сумма заказа</th>
          <th>Комментарий отдела</th>
          <th>Комментарий главного кассира</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${events}" status="i" var="record">
        <tr align="center">
          <td>${shortDate(date:record.inputdate)}</td>
          <td>${eventtypes[record.casheventtype_id]}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td>${record.comment_dep}</td>
          <td>${record.comment}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>