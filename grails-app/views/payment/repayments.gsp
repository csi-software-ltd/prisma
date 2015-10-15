<g:formRemote id="repaymentupdateForm" name="repaymentupdateForm" url="[action:'payrepayment']" method="post" onSuccess="processpayrepaymentResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorrepaymentlist">
      <li></li>
    </ul>
  </div>
  <div id="repayment"></div>
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
          <th>Сумма произведенных списаний</th>
          <th>Сумма к списанию</th>
          <th width="100">Статус</th>
          <th width="60"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${repayments}" status="i" var="record">
        <tr align="center">
          <td>${number(value:record.summa)}</td>
          <td>${String.format('%td.%<tm.%<tY',record.inputdate)}</td>
          <td>${record.platperiod}</td>
          <td>${record.execdate?String.format('%td.%<tm.%<tY',record.execdate):'нет'}</td>
          <td>${number(value:record.clientcommission)}</td>
          <td>${number(value:record.summa-record.clientcommission)}</td>
          <td>${record.modstatus==2?'исполнен':record.modstatus==1?'в работе':'запрос'}</td>
          <td valign="middle">
          <g:if test="${iscanedit}">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Изменить сумму списания" onclick="$('repayment_id').value=${record.id};$('repayment_submit_button').click();"><i class="icon-pencil"></i></a>
          <g:if test="${record.modstatus!=2}">
            &nbsp;&nbsp;<g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'payrepayment',id:payrequest.id,params:[repayment_id:record.id]]}" title="Списать" onSuccess="location.reload(true)"><i class="icon-money"></i></g:remoteLink>
          </g:if>
          </g:if>
          </td>
        </tr>
      </g:each>
      <g:if test="${!repayments}">
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
<g:formRemote name="repaymentForm" url="[action:'repayment', params:[payrequest_id:payrequest.id]]" update="repayment" onComplete="\$('errorrepaymentlist').up('div').hide();jQuery('#repaymentupdateForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="repayment_submit_button" value="Показать"/>
  <input type="hidden" id="repayment_id" name="id" value="0"/>
</g:formRemote>