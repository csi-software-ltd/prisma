<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата</th>
          <th>Номер договора</th>
          <th>Дата договора</th>
          <th>Дата окончания</th>
          <th>Тип оплаты<br/>вступ. взноса</th>
          <th>Вступительный<br/>взнос</th>
          <th>Сумма допуска</th>
          <th>Членские взносы</th>
          <th>Автор</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${history}" status="i" var="record">
        <tr align="center">
          <td>${shortDate(date:record.inputdate)}</td>
          <td>${record.anumber}</td>
          <td>${String.format('%td.%<tm.%<tY',record.adate)}</td>
          <td>${record.enddate?String.format('%td.%<tm.%<tY',record.enddate):'нет'}</td>
          <td>${record.paytype==1?'Единовременно':record.paytype==2?'График':''}</td>
          <td>${intnumber(value:record.entryfee)}</td>
          <td>${intnumber(value:record.alimit)}</td>
          <td>${intnumber(value:record.regfee)}</td>
          <td>${record.admin_name}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>