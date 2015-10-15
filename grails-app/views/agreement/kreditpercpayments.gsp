<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата платежа</th>
          <th>Сумма</th>
          <th>Назначение</th>
          <th>Статус</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${payrequests}" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td>${number(value:record.summa)}</td>
          <td>${record.destination}</td>
          <td>${record.modstatus==1?'В работе':record.modstatus==2?'Выполнен':record.modstatus==3?'Подтвержден':'Необработанный'}</td>
        </tr>
      </g:each>
      <g:if test="${!payrequests}">
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
