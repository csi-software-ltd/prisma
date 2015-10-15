<style type="text/css">
  tr.yellow > td { background:lightyellow !important }
</style>
<div id="ajax_wrap">
  <div style="padding:10px">
    <h2>Отчет по клиенту ${client?.name} за ${String.format('%tB %<tY',reportdate)}</h2>
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
          <td>Сумма поступлений клиентских средств</td>
          <td align="center">${number(value:statistic.income)}</td>          
        </tr>
        <tr>          
          <td>Количество входящих платежей</td>
          <td align="center">${intnumber(value:statistic.incomecount)}</td>          
        </tr>
        <tr>          
          <td>Сумма вывода клиентских средств</td>
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
          <td>Остаток клиентских средств на начало месяца</td>
          <td align="center">${number(value:statistic.startclientsaldo)}</td>          
        </tr>
        <tr>          
          <td>Остаток клиентских средств на конец месяца</td>
          <td align="center">${number(value:statistic.endclientsaldo)}</td>          
        </tr>
      </tbody>
    </table>
  </div>
</div>
