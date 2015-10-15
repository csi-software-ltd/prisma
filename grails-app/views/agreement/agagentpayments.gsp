<g:formRemote id="agpaymentaddForm" name="agpaymentaddForm" url="[action:'addagpayment']" method="post" onSuccess="processaddagpaymentResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="erroraddagpaymentlist">
      <li></li>
    </ul>
  </div>
  <div id="agpayment"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div class="tabs fright">
    <a id="paymenttype0" <g:if test="${type==0}">class="active"</g:if> onclick="setAgPaymenttype(0)"><i class="icon-list icon-large"></i> Безналичные </a>
    <a id="paymenttype1" <g:if test="${type==1}">class="active"</g:if> onclick="setAgPaymenttype(1)"><i class="icon-list icon-large"></i> Наличные </a>
  </div>
  <div class="clear"></div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата платежа</th>
        <g:if test="${type==0}">
          <th>Агент</th>
          <th>Плательщик</th>
          <th>Получатель</th>
          <th>Статус</th>
        </g:if><g:else>
          <th>Номер платежа</th>
          <th>Тип</th>
          <th>Получатель</th>
        </g:else>
          <th>Сумма</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${payments}" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td>${agents[record.agent_id]}</td>
          <td>${record.fromcompany}</td>
          <td>${record.tocompany}</td>
          <td>${record.modstatus==0?'необработанный':record.modstatus==1?'в задании':record.modstatus==2?'выполнен':'подтвержден'}</td>
          <td>${number(value:record.summa)}</td>
        </tr>
      </g:each>
      <g:each in="${cashpayments}" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.operationdate)}</td>
          <td><g:link style="z-index:1" controller="cash" action="maincashrecord" id="${record.id}">${record.id}</g:link></td>
          <td>${record.type==1?'выдача':'возврат'}</td>
          <td>${agents[record.agent_id]}</td>
          <td>${intnumber(value:record.summa)}</td>
        </tr>
      </g:each>
      <g:if test="${!payments&&!cashpayments}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Платежей не найдено</a>
          </td>
        </tr>
      </g:if>
      <g:if test="${type==0&&iscanedit}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('agpayment_submit_button').click();">
              Добавить новый платеж &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="fixForm" url="[action:'agpayment', params:[agentagr_id:agentagr.id]]" update="agpayment" onComplete="\$('erroraddagpaymentlist').up('div').hide();jQuery('#agpaymentaddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="agpayment_submit_button" value="Показать"/>
</g:formRemote>
