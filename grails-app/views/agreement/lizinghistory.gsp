<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата</th>
          <th>Договор</th>
          <th>Дата договора</th>
          <th>Срок</th>
          <th>Начальный<br/>взнос</th>
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
          <td>${String.format('%td.%<tm.%<tY',record.adate)}</td>
          <td>${String.format('%td.%<tm.%<tY',record.enddate)}</td>
          <td>${number(value:record.initialfee)}</td>
          <td>${record.comment}</td>
          <td>${record.responsible_name}</td>
          <td>${record.admin_name}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>