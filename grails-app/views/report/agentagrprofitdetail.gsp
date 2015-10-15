<div id="ajax_wrap">
  <div style="padding:5px 10px">&nbsp;</div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Клиент</th>
          <th>Банк</th>
          <th>Заемщик</th>
          <th>Договор</th>
          <th>Сумма за период</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${report.groupBy{it.agentkredit_id}}" var="record">
        <tr align="center">
          <td>${agrs[record.value[0].agentagr_id].client_name}</td>
          <td>${agrs[record.value[0].agentagr_id].bankname}</td>
          <td>${record.value[0].clientname}</td>
          <td>${record.value[0].anumber} от ${String.format('%td.%<tm.%<tY',record.value[0].adate)}</td>
          <td>${number(value:record.value.sum{ it.recieveProfit() }?:0.0g)}</td>
        </tr>
      </g:each>
      <g:if test="${!report}">
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Данных не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
