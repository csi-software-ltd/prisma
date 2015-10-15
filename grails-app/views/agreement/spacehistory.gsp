<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата</th>
          <th>Срок</th>
          <th>Тип помещений</th>
          <th>Площадь</th>
          <th>Цена<br/>Плата<br/>Доп услуги</th>
          <th>Срок<br/>оплаты</th>
          <th>Комментарий</th>
          <th>Ответственный</th>
          <th>Автор</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${history}" status="i" var="record">
        <tr align="center">
          <td>${shortDate(date:record.inputdate)}</td>
          <td>${record.enddate?String.format('%td.%<tm.%<tY',record.enddate):'нет'}</td>
          <td>${spacetypes[record.spacetype_id]}</td>
          <td>${number(value:record.area)}</td>
          <td>
            ${number(value:record.ratemeter)}<br/>${number(value:record.rate)}<br/>
          <g:if test="${record.is_addpayment}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>${record.payterm}</td>
          <td>${record.comment}</td>
          <td>${record.responsible_name}</td>
          <td>${record.admin_name}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>