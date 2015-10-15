  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Период сверки</th>
          <th>Дата сверки</th>
          <th>Сумма доходов по<br/>периоду сверки</th>
          <th>Сумма доходов по<br/>пред. периодам</th>
          <th>Статус</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${acts}" status="i" var="record">
        <tr align="center">
          <td>${String.format('%tB %<tY',new Date(record.value[0].year-1900,record.value[0].month-1,1))}</td>
          <td>${String.format('%td.%<tm.%<tY',record.value[0].inputdate)}</td>
          <td>${number(value:record.value[0].profit)}</td>
          <td>${number(value:record.value[0].profitprev)}</td>
          <td>${record.value[0].modstatus?'согласован':'новый'}</td>
          <td valign="middle">
            <a class="button" href="${g.createLink(action:'printprofitact',id:agentagr.id,params:[act_id:record.value[0].id])}" title="Распечатать" target="_blank"><i class="icon-print"></i></a>
          </td>
        </tr>
      </g:each>
      <g:if test="${!acts}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Актов не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>