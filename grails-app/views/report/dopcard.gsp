<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Период</th>
          <th>Сумма выплат</th>
          <th>Сумма возвратов</th>
          <th>Сумма комиссий</th>
          <th>Дельта</th>
          <th>Текущий остаток</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${reports}" var="record">
        <tr align="center">
          <td>${record.platperiod}</td>
          <td>${number(value:record.paid)}</td>
          <td>${number(value:record.returned)}</td>
          <td>${number(value:record.comission)}</td>
          <td>${number(value:record.paid-record.returned-record.comission)}</td>
          <td></td>
        </tr>
      </g:each>
        <tr align="center">
          <td>ИТОГО</td>
          <td>${number(value:total.paid)}</td>
          <td>${number(value:total.returned)}</td>
          <td>${number(value:total.comission)}</td>
          <td>${number(value:total.delta)}</td>
          <td>${number(value:dopcardsaldo)}</td>
        </tr>
      </tbody>
    </table>
  </div>
</div>