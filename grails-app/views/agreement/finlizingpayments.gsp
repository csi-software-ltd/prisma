<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата платежа</th>
          <th>Тип платежа</th>
          <th>Сумма</th>
          <th width="300">Назначение</th>
          <th width="70">Возврат комиссии</th>
          <th>Статус</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${payrequests}" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td>${record.paytype==1?'исходящий':'входящий'}</td>
          <td>${number(value:record.summa)}</td>
          <td width="300">${record.destination}</td>
          <td width="70">${record.is_dop==1?'Да':'Нет'}</td>
          <td>${record.modstatus==1?'В работе':record.modstatus==2?'Выполнен':record.modstatus==3?'Подтвержден':'Необработанный'}</td>
        </tr>
      </g:each>
      <g:if test="${!payrequests}">
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