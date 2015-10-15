<g:formRemote id="agentpaymentupdateForm" name="agentpaymentupdateForm" url="[action:'payagentpayment']" method="post" onSuccess="processpayagentpaymentResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="erroragentpaymentlist">
      <li></li>
    </ul>
  </div>
  <div id="agentpayment"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Сумма</th>
          <th>Дата запроса</th>
          <th>Период списания</th>
          <th>Дата списания</th>
          <th>Сумма произведенных выплат</th>
          <th>Сумма к выплате</th>
          <th width="100">Статус</th>
          <th width="60"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${agentpayments}" status="i" var="record">
        <tr align="center">
          <td>${number(value:record.summa)}</td>
          <td>${String.format('%td.%<tm.%<tY',record.inputdate)}</td>
          <td>${record.platperiod}</td>
          <td>${record.execdate?String.format('%td.%<tm.%<tY',record.execdate):'нет'}</td>
          <td>${number(value:record.agentcommission)}</td>
          <td>${number(value:record.summa-record.agentcommission)}</td>
          <td>${record.modstatus==2?'исполнен':record.modstatus==1?'в работе':'запрос'}</td>
          <td valign="middle">
          <g:if test="${iscanedit}">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Изменить сумму списания" onclick="$('agentpayment_id').value=${record.id};$('agentpayment_submit_button').click();"><i class="icon-pencil"></i></a>
          <g:if test="${record.modstatus!=2}">
            &nbsp;&nbsp;<g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'payagentpayment',id:payrequest.id,params:[agentpayment_id:record.id]]}" title="Списать" onSuccess="location.reload(true)"><i class="icon-money"></i></g:remoteLink>
          </g:if>
          </g:if>
          </td>
        </tr>
      </g:each>
      <g:if test="${!agentpayments}">
        <tr>
          <td colspan="8" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Заявок не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="agentpaymentForm" url="[action:'agentpayment', params:[payrequest_id:payrequest.id]]" update="agentpayment" onComplete="\$('erroragentpaymentlist').up('div').hide();jQuery('#agentpaymentupdateForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="agentpayment_submit_button" value="Показать"/>
  <input type="hidden" id="agentpayment_id" name="id" value="0"/>
</g:formRemote>