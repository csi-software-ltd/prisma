<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата</th>
          <th>Тип работ</th>
          <th>Срок</th>
          <th>Стоимость</th>
          <th>Комментарий</th>
          <th>Ответственный</th>
          <th>Автор</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${history}" status="i" var="record">
        <tr align="center">
          <td>${shortDate(date:record.inputdate)}</td>
          <td>${smrcats[record.smrcat_id]}</td>
          <td>${record.enddate?String.format('%td.%<tm.%<tY',record.enddate):'нет'}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td>${record.comment}</td>
          <td>${record.res_name}</td>
          <td>${record.admin_name}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>