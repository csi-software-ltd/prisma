<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата платежа</th>
          <th>Сумма</th>
          <th>Агентский договор</th>
          <th>Плательщик</th>
          <th>Получатель</th>
          <th>Статус</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${payments}" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td>${number(value:record.summa)}</td>
          <td><g:link style="z-index:1" controller="agreement" action="agent" id="${record.agentagr_id}">${agrs[record.agentagr_id]}</g:link></td>
          <td>${record.fromcompany}</td>
          <td>${record.tocompany}</td>
          <td>${record.modstatus==0?'необработанный':record.modstatus==1?'в задании':record.modstatus==2?'выполнен':'подтвержден'}</td>
        </tr>
      </g:each>
      <g:if test="${!payments}">
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