<div id="ajax_wrap">
  <div style="padding:5px 10px">&nbsp;</div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Начислено на выплату</th>
          <th>Выплачено</th>
          <th>Не оплачено</th>
        </tr>
      </thead>
      <tbody>
      <g:if test="${report}">
        <tr align="center">
          <td>${number(value:summary.accrued)}</td>
          <td>${number(value:summary.paid)}</td>
          <td>${number(value:summary.accrued-summary.paid)}</td>
        </tr>
      </g:if>
      <g:if test="${!report}">
        <tr>
          <td colspan="3" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Платежей не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
