<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата</th>
          <th>Выдача</th>
          <th>Гашение</th>
          <th>Использованный овер</th>
          <th>Остаток с овером</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${payments}" status="i" var="record">
        <%balance+=(record.paytype==2?record.summa:-record.summa)%>
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td><g:if test="${record.paytype==2}">${number(value:record.summa)}</g:if></td>
          <td><g:if test="${record.paytype==1}">${number(value:record.summa)}</g:if></td>
          <td>${number(value:balance)}</td>
          <td>${number(value:kredsumma-balance)}</td>
        </tr>
      </g:each>
      <g:if test="${!payments}">
        <tr>
          <td colspan="4" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Платежей не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>