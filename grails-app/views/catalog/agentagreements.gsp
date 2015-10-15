<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="936" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Договор</th>
          <th>Клиент</th>
          <th>Банк</th>
          <th>Сумма начисленная</th>
          <th>Сумма корректировок</th>
          <th>Сумма выплат</th>
          <th>К оплате агенту</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${agreements}" status="i" var="record">
        <tr align="center">
          <td><g:link style="z-index:1" controller="agreement" action="agent" id="${record.agentagr_id}">${agrs[record.agentagr_id]}</g:link></td>
          <td>${record.client_name}</td>
          <td>${record.bank_name}</td>
          <td>${number(value:record.summa+record.summafix+record.summaprev)}</td>
          <td>${number(value:record.agentfix)}</td>
          <td>${number(value:record.paid)}</td>
          <td>${number(value:record.summa+record.summafix+record.summaprev-record.actpaidsum)}</td>
        </tr>
      </g:each>
      <g:if test="${!agreements}">
        <tr>
          <td colspan="7" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Договоров не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>