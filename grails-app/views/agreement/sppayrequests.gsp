<g:formRemote id="payrequestAddForm" name="payrequestAddForm" url="[action:'addsppayrequest']" method="post" onSuccess="processaddpayrequestResponse(e)" style="display:none">
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
          <th>Сумма<br/>НДС</th>
          <th>Назначение</th>
          <th>Доп. платеж</th>
          <th>Статус</th>
          <th width="15"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${payrequests}" var="record">
        <tr align="center">
          <td width="70">${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td>${number(value:record.summa)}<br/>${record.is_nds?'с НДС':'без НДС'}</td>
          <td>${record.destination}</td>
          <td width="70">${record.is_dop?'Да':'Нет'}</td>
          <td width="100">${record.modstatus==1?'В работе':record.modstatus==2?'Выполнен':record.modstatus==3?'Подтвержден':'Необработанный'}</td>
          <td>
            <g:link controller="payment" action="payrequestdetail" id="${record.id}" style="z-index:1" class="button" target="_blank"><i class="icon-pencil"></i></g:link>
          </td>
        </tr>
      </g:each>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" id="addpayrequestbutton" href="javascript:void(0)" onclick="$('payrequest_id').value=0;$('payrequest_submit_button').click();">
              Добавить заявку &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if><g:elseif test="${!payrequests}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Заявок не найдено</a>
          </td>
        </tr>
      </g:elseif>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="payrequestForm" url="[action:'sppayrequest', params:[agr_id:space.id]]" update="payrequest" onComplete="\$('errorpayrequestlist').up('div').hide();jQuery('#payrequestAddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="payrequest_submit_button" value="Показать"/>
  <input type="hidden" id="payrequest_id" name="id" value="0"/>
</g:formRemote>