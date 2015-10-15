<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th width="150">Дата</th>
          <th width="150">Дата<br/>модификации</th>
          <th>Фактическая зарплата</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${psalary}" status="i" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.pdate)}</td>
          <td>${String.format('%td.%<tm.%<tY',record.moddate?:record.pdate)}</td>
          <td>${intnumber(value:record.actsalary)}</td>
        </tr>
      </g:each>
      <g:if test="${!psalary}">
        <tr>
          <td colspan="3" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Назначений окладов не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>