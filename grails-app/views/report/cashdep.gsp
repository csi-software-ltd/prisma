<style type="text/css">
  .list td,.list th { font-size: 11px !important}
</style>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="70%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th><g:if test="${reporttype}">ФИО</g:if><g:else>Наименование отдела</g:else></th>
        <g:if test="${depreport}">
          <th>Сальдо отдела</th>
          <th>Сальдо сотрудников</th>
        </g:if><g:elseif test="${userreport}">
          <th>Тип</th>
        </g:elseif>
          <th>Итого</th>
        <g:if test="${depreport&&session.user.cashaccess==3}">
          <th></th>
        </g:if><g:elseif test="${userreport&&session.user.cashaccess==3}">
          <th></th>
        </g:elseif>
        </tr>
      </thead>
      <tbody>
      <g:each in="${depreport}" var="record">
        <tr>
          <td>${departments[record.id]}</td>
          <td align="center">${number(value:record.saldo?:0)}</td>
          <td align="center">${number(value:record.depusersaldo?:0)}</td>
          <td align="center">${number(value:(record.saldo?:0)+(record.depusersaldo?:0))}</td>
          <td align="center">
            <a class="button" href="${g.createLink(controller:'cash', action:'index', params:[cashsection:5, department_id:record.id])}" title="Подробнее" target="_blank"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      <g:each in="${userreport}" var="record">
        <tr>
          <td>${record.pers_name}</td>
          <td align="center">${record.cashaccess==1?'подотчетное лицо':'кассир холдинга'}</td>
          <td align="center">${number(value:record.depusersaldo)}</td>
          <td align="center">
            <a class="button" href="${g.createLink(controller:'cash', action:'index', params:[cashsection:5, department_id:record.department_id, pers_id:record.pers_id])}" title="Подробнее" target="_blank"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      <g:each in="${loanreport}" var="record">
        <tr>
          <td>${record.pers_name}</td>
          <td align="center">${number(value:record.loansaldo)}</td>
        </tr>
      </g:each>
      <g:each in="${penaltyreport}" var="record">
        <tr>
          <td>${record.pers_name}</td>
          <td align="center">${number(value:record.penalty)}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>