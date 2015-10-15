<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Название банка</th>
          <th>Номер</th>
          <th>Действует до<br>месяц/год</th>
          <th>Основная</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${accounts}" status="i" var="record">
        <tr align="left" style="font-weight:${record.is_main?700:400}">
          <td>${Bank.get(record.bank_id)?.name}</td>
          <td>карта: ${record.nomer}<br/>лиц. счет: ${g.account(value:record.paccount)}</td>
          <td align="center">${record.validmonth}/${record.validyear}</td>
          <td align="center"><i class="icon-${record.is_main?'ok':'minus'}" title="${record.is_main?'да':'нет'}"></i></td>
        </tr>
      </g:each>
      <g:if test="${!accounts}">
        <tr>
          <td colspan="4" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Счетов не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>