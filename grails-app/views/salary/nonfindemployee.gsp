<div id="ajax_wrap">
<g:if test="${compers}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Компания</th>
          <th>ФИО</th>
          <th>Должность</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${compers}" var="record">
        <tr align="center">
          <td><g:link controller="company" action="detail" id="${record.company_id}">${record.admin_name}</g:link></td>
          <td>${record.shortname}</td>
          <td>${positions[record.position_id]}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</g:if>
</div>