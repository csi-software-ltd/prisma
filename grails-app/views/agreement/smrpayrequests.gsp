<g:formRemote id="payrequestAddForm" name="payrequestAddForm" url="[action:'addsmrpayrequest']" method="post" onSuccess="processaddpayrequestResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorpayrequestlist">
      <li></li>
    </ul>
  </div>
  <div id="payrequest"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата платежа</th>
          <th>Сумма</th>
          <th>Тип</th>
          <th>Назначение</th>
          <th width="100">Статус</th>
          <th width="45"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${payrequests}" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td>${number(value:record.summa)}</td>
          <td>
          <g:if test="${record.paytype==1}"><abbr title="Исходящий"><i class="icon-signout icon-large"></i></abbr></g:if>
          <g:elseif test="${record.paytype==2}"><abbr title="Входящий"><i class="icon-signin icon-large"></i></abbr></g:elseif>
          <g:else><abbr title="Внутренний"><i class="icon-refresh icon-large"></i></abbr></g:else>
          </td>
          <td>${record.destination}</td>
          <td>${record.modstatus==1?'В работе':record.modstatus==2?'Выполнен':record.modstatus==3?'Подтвержден':'Необработанный'}</td>
          <td valign="middle">
            <g:link controller="payment" action="payrequestdetail" id="${record.id}" style="z-index:1" class="button" target="_blank"><i class="icon-pencil"></i></g:link>
          <g:if test="${iscanedit&&record.modstatus==0}">
            &nbsp;&nbsp;<g:remoteLink class="button" style="z-index:1" title="Удалить" url="${[controller:controllerName, action:'deletesmrpayrequest', id:smr.id, params:[payrequest_id:record.id]]}" before="if(!confirm('Вы действительно хотите удалить заявку?')) return false" onSuccess="getPayrequests()"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </td>
        </tr>
      </g:each>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" id="addpayrequestbutton" href="javascript:void(0)" onclick="$('payrequest_id').value=0;$('payrequest_submit_button').click();">
              Добавить заявку на платеж &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if><g:elseif test="${!payrequests}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Платежей не найдено</a>
          </td>
        </tr>
      </g:elseif>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="payrequestForm" url="[action:'smrpayrequest', params:[agr_id:smr.id]]" update="payrequest" onComplete="\$('errorpayrequestlist').up('div').hide();jQuery('#payrequestAddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="payrequest_submit_button" value="Показать"/>
  <input type="hidden" id="payrequest_id" name="id" value="0"/>
</g:formRemote>
