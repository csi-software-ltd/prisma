<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата платежа</th>
          <th>Сумма</th>
          <th>Тип платежа</th>
          <th>Сумма по телу</th>
          <th>Сумма по процентам</th>
          <th>Остаток тела</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${cashpayments}" var="record">
        <%bodydebt += record.cashclass==19?0:record.type==1?-record.summa:record.summa%>
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.operationdate)}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td>${record.type==1?'расход':'приход'}</td>
          <td>${intnumber(value:record.cashclass==18?record.summa:0)}</td>
          <td>${intnumber(value:record.cashclass==19?record.summa:0)}</td>
          <td>${number(value:bodydebt)}</td>
        </tr>
      </g:each>
      <g:if test="${!cashpayments}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Платежей не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>