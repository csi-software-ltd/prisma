<div id="ajax_wrap">
  <div style="padding:5px 10px">&nbsp;</div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Клиент</th>
          <th>Банк</th>
          <th>Сумма за год</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${report.groupBy{it.agentagr_id}}" var="record">
        <tr align="center">
          <td>${agrs[record.key].client_name}</td>
          <td>${agrs[record.key].bankname}</td>
          <td>${number(value:record.value.groupBy{new Date(it.year-1900,it.month-1,1)}.collect{ it.value[0] }.sum{ it.profit })}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>
