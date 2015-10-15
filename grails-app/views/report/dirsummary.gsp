<style type="text/css">
  .list td,.list th { font-size: 12px }
  abbr { vertical-align: middle; }
</style>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
      <g:if test="${allreport}">
        <tr>
          <th>№</th>
          <th>Фио директора</th>
          <th>Компания</th>
          <th>Дата вступления в должность</th>
        </tr>
      </g:if><g:else>
        <tr>
          <th>Фио директора</th>
          <th>Паспортные данные</th>
          <th>Образование</th>
          <th>Дата вступления в должность</th>
          <th>Дата завершения деятельности</th>
        </tr>
      </g:else>
      </thead>
      <tbody>
      <g:each in="${allreport}" var="record" status="i">
        <tr align="center">
          <td>${i+1}</td>
          <td>${record.shortname}</td>
          <td>${record.position_name}</td>
          <td>${String.format('%td.%<tm.%<tY',record.jobstart)}</td>
        </tr>
      </g:each>
      <g:each in="${compreport}" var="record" status="i">
        <tr align="center">
          <td>${record.shortname}</td>
          <td>${record.collectPassData()}</td>
          <td>${record.education}</td>
          <td>${String.format('%td.%<tm.%<tY',record.jobstart)}</td>
          <td>${record.jobend?String.format('%td.%<tm.%<tY',record.jobend):'нет'}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>