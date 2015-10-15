<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Фамилия</th>          
          <th>Сумма</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${debt}" status="i" var="record">
        <tr align="center">
          <td>${Pers.get(record.pers_id)?.shortname}</td>
          <td>${record.saldo}</td>
        </tr>
      </g:each>
      <g:if test="${!debt}">
        <tr>
          <td colspan="3" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Задолженностей не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
