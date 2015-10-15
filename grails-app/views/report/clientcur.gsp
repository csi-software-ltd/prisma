<style type="text/css">
  .list td,.list th { font-size: 11px !important}
  tr.yellow > td { background:lightyellow !important }
</style>
<div id="ajax_wrap">
  <div style="padding:10px">
    <h2>Сводка за период</h2>
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
          <td>Остаток клиентских средств на начало периода</td>
          <td align="center">${number(value:startclientsaldo)}</td>
        </tr>
        <tr>
          <td>Общая сумма поступлений по клиентским платежам</td>
          <td align="center">${number(value:(report.records.sum{ it.computeIncome() }?:0.0g))}</td>
        </tr>
        <tr>
          <td>Общая сумма вывода по клиентским платежам</td>
          <td align="center">${number(value:(report.records.sum{ it.computeOutlay() }?:0.0g))}</td>
        </tr>
        <tr>
          <td>Начислено к возврату комиссионных</td>
          <td align="center">${number(value:(report.records.sum{ it.subcomission }?:0.0g))}</td>
        </tr>
        <tr>
          <td>Возврат комиссионных</td>
          <td align="center">${number(value:(report.records.sum{ it.is_clientcommission?it.clientdelta:0.0g }?:0.0g))}</td>
        </tr>
        <tr>
          <td>Сумма списаний по агентским договорам</td>
          <td align="center">${number(value:(report.records.sum{ it.clientcommission }?:0.0g))}</td>
        </tr>
        <tr>
          <td>Суммарная комиссия</td>
          <td align="center">${number(value:(report.records.sum{ it.comission }?:0.0g))}</td>
        </tr>
        <tr>
          <td>Остаток клиентских средств на конец периода</td>
          <td align="center">${number(value:endclientsaldo)}</td>
        </tr>
      </tbody>
    </table>
    <div style="padding:10px">
      <h2>Детализация по подклиентам за период</h2>
    </div>
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Подклиент</th>
          <th>Сумма прихода</th>
          <th>Сумма вывода</th>
          <th>Сумма комиссий</th>
          <th>Сумма к возврату клиенту</th>
          <th>Сумма возврата клиенту</th>
          <th>Сумма к возврату посреднику</th>
          <th>Сумма возврата посреднику</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${report.records.groupBy{it.subclient_id}}" status="i" var="record">
      <g:if test="${record.key>0}">
        <tr align="center">
          <td>${clients[record.key]}</td>
          <td>${number(value:(record.value.sum{ it.computeIncome() }?:0.0g))}</td>
          <td>${number(value:(record.value.sum{ it.computeOutlay() }?:0.0g))}</td>
          <td>${number(value:(record.value.sum{ it.comission }?:0.0g))}</td>
          <td>${number(value:(record.value.sum{ it.supcomission }?:0.0g))}</td>
          <td>${number(value:(record.value.sum{ it.is_clientcommission?it.clientdelta:0.0g }?:0.0g))}</td>
          <td>${number(value:(record.value.sum{ it.midcomission }?:0.0g))}</td>
          <td>${number(value:(record.value.sum{ it.is_midcommission?it.clientdelta:0.0g }?:0.0g))}</td>
        </tr>
      </g:if>
      </g:each>
      <g:if test="${!report.records.groupBy{it.subclient_id}.find{it.key>0}}">
        <tr>
          <td colspan="8" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Платежей не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>