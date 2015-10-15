<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата платежа</th>
          <th>Номер платежа</th>
          <th>Агентский договор</th>
          <th>Тип</th>
          <th>Сумма</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${cashpayments}" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.operationdate)}</td>
          <td><g:link style="z-index:1" controller="cash" action="maincashrecord" id="${record.id}">${record.id}</g:link></td>
          <td><g:link style="z-index:1" controller="agreement" action="agent" id="${record.agentagr_id}">${agrs[record.agentagr_id]}</g:link></td>
          <td>${record.type==1?'выдача':'возврат'}</td>
          <td>${intnumber(value:record.summa)}</td>
        </tr>
      </g:each>
      <g:if test="${!cashpayments}">
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Платежей не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>