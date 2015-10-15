<style type="text/css">
  .list td,.list th { font-size: 11px !important}
  tr.yellow > td { background:lightyellow !important }
</style>
<div id="ajax_wrap">
  <div style="padding:10px">
    <h2>Сводка за месяц</h2>
  </div>
  <div id="resultList">
    <table class="list" width="50%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Наименование поступления</th>
          <th>Сумма, руб.</th>
        </tr>
      </thead>
      <tbody>
        <tr>          
          <td>Сумма поступлений по клиентским платежам</td>
          <td align="center">${number(value:statistic.income)}</td>          
        </tr>
        <tr>          
          <td>Количество входящих платежей</td>
          <td align="center">${intnumber(value:statistic.incomecount)}</td>          
        </tr>
        <tr>          
          <td>Сумма вывода по клиентским платежам</td>
          <td align="center">${number(value:statistic.outlay)}</td>          
        </tr>
        <tr>          
          <td>Количество исходящих платежей</td>
          <td align="center">${intnumber(value:statistic.outlaycount)}</td>          
        </tr>
        <tr>          
          <td>Суммарная комиссия</td>
          <td align="center">${number(value:statistic.comission)}</td>          
        </tr>
        <tr>
          <td>Сумма к возврату комиссионных</td>
          <td align="center">${number(value:statistic.supcomission)}</td>          
        </tr>
        <tr>          
          <td>Сумма возврата комиссионных</td>
          <td align="center">${number(value:statistic.retcomission)}</td>          
        </tr>
        <tr>
          <td>Сумма к возврату посреднику</td>
          <td align="center">${number(value:statistic.midcomission)}</td>          
        </tr>
        <tr>          
          <td>Сумма возврата посреднику</td>
          <td align="center">${number(value:statistic.retmidcomission)}</td>          
        </tr>
        <tr>
          <td>Сумма списаний</td>
          <td align="center">${number(value:statistic.repayment)}</td>          
        </tr>
        <tr>          
          <td>Остаток клиентских средств на начало месяца</td>
          <td align="center">${number(value:statistic.startclientsaldo)}</td>          
        </tr>
        <tr>         
          <td>Остаток клиентских средств на конец месяца</td>
          <td align="center">${number(value:statistic.endclientsaldo)}</td>          
        </tr>
      </tbody>
    </table>
    <div style="padding:10px">
      <h2>Детализация по клиентам за месяц</h2>
    </div>
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Клиент</th>
          <th>Сумма прихода</th>
          <th>Сумма вывода</th>
          <th>Сумма к возврату комиссии</th>
          <th>Сумма возврата комиссии</th>
          <th>Сумма к возврату посреднику</th>
          <th>Сумма возврата посреднику</th>
          <th>Сумма списаний</th>
          <th>Сумма комиссий</th>
          <th>Остаток счета</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${report}" status="i" var="record">
        <tr align="center" style="${record.curclientsaldo+record.dinclientsaldo<0?'color:red':''}">
          <td>${record.client_name}</td>
          <td>${number(value:record.income?:0.0g)}</td>
          <td>${number(value:record.outlay?:0.0g)}</td>
          <td>${number(value:record.clsupcomission?:0.0g)}</td>
          <td>${number(value:record.clretcomission?:0.0g)}</td>
          <td>${number(value:record.clmidcomission?:0.0g)}</td>
          <td>${number(value:record.clretmidcomission?:0.0g)}</td>
          <td>${number(value:record.clrepayment?:0.0g)}</td>
          <td>${number(value:record.clcomission?:0.0g)}</td>
          <td>${number(value:record.curclientsaldo+record.dinclientsaldo)}</td>
        </tr>
      </g:each>
      <g:if test="${!report}">
        <tr>
          <td colspan="10" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Платежей не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
