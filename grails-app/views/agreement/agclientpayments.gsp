<div id="ajax_wrap">
  <div class="tabs fright">
    <a id="paymenttype0" <g:if test="${type==0}">class="active"</g:if> onclick="setClPaymenttype(0)"><i class="icon-list icon-large"></i> Безналичные </a>
    <a id="paymenttype1" <g:if test="${type==1}">class="active"</g:if> onclick="setClPaymenttype(1)"><i class="icon-list icon-large"></i> Наличные </a>
  </div>
  <div class="clear"></div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата платежа</th>
        <g:if test="${type==0}">
          <th>Плательщик</th>
          <th>Получатель</th>
          <th>Статус</th>
        </g:if><g:else>
          <th>Номер платежа</th>
          <th>Тип</th>
        </g:else>
          <th>Сумма</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${payments}" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td>${record.fromcompany}</td>
          <td>${record.tocompany}</td>
          <td>${record.modstatus==0?'необработанный':record.modstatus==1?'в задании':record.modstatus==2?'выполнен':'подтвержден'}</td>
          <td>${number(value:record.clientcommission)}</td>
        </tr>
      </g:each>
      <g:each in="${cashpayments}" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.operationdate)}</td>
          <td><g:link style="z-index:1" controller="cash" action="maincashrecord" id="${record.id}">${record.id}</g:link></td>
          <td>${'получение'}</td>
          <td>${intnumber(value:record.summa)}</td>
        </tr>
      </g:each>
      <g:if test="${!payments&&!cashpayments}">
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