<g:formRemote id="planpaymentAddForm" name="planpaymentAddForm" url="[action:'addlicplanpayment']" method="post" onSuccess="processAddplanpaymentResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorplanpaymentlist">
      <li></li>
    </ul>
  </div>
  <div id="licplanpayment"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата платежа</th>
          <th>Сумма</th>
          <th>Статус</th>
          <th>Действие</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${planpayments}" status="i" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td>${record.modstatus==1?'в оплате':record.modstatus==2?'оплаченный':'планируемый'}</td>
          <td>
          <g:if test="${record.modstatus==0&&iscanedit}">
            <g:remoteLink class="button" url="${[controller:controllerName,action:'deletelicplanpayment',id:record.id,params:[license_id:license.id]]}" title="Удалить" onSuccess="getPlanpayment()"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </td>
        </tr>
      </g:each>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="4" class="btns" style="text-align:center">
            <a class="button" id="addplanpaymentbutton" href="javascript:void(0)" onclick="$('planpayment_submit_button').click();">
              Добавить платеж &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="planpaymentForm" url="[action:'licplanpayment', params:[license_id:license.id]]" update="licplanpayment" onComplete="\$('errorplanpaymentlist').up('div').hide();jQuery('#planpaymentAddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="planpayment_submit_button" value="Показать"/>
</g:formRemote>
