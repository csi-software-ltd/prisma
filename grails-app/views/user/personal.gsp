<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Отдел</th>
          <th>ФИО</th>
          <th>Оклад</th>
          <th>Группа</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${personal}" status="i" var="record">
        <tr align="center">
          <td>${record.dep_name}</td>
          <td>${record.pers_name}</td>
          <td>${intnumber(value:record.actsalary)}</td>
          <td>${record.group_name}</td>
        </tr>
      </g:each>
      <g:if test="${!personal}">
        <tr>
          <td colspan="4" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Сотрудников не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>