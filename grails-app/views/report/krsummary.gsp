<style type="text/css">
  .list td,.list th { font-size: 12px }
  abbr { vertical-align: middle; }
</style>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>№</th>
          <th>Наименование банка</th>
          <th>Сумма задолженности по кредитам</th>
          <th>Доля в общей задолженности</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${report}" var="record" status="i">
        <tr align="center">
          <td>${i+1}</td>
          <td>${record.bname}</td>
          <td>${number(value:record.debt)}</td>
          <td>${number(value:record.debt/totaldebt*100)}%</td>
        </tr>
      </g:each>
        <tr align="center">
          <td></td>
          <td>Всего</td>
          <td>${number(value:totaldebt)}</td>
          <td>100%</td>
        </tr>
      </tbody>
    </table>
  </div>
</div>