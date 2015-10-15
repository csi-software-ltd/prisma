<g:formRemote id="lizdoppaymentaddForm" name="lizdoppaymentaddForm" url="[action:'addlizdoppayment']" method="post" onSuccess="processaddlizdoppaymentResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="erroraddlizdoppaymentlist">
      <li></li>
    </ul>
  </div>
  <div id="lizdoppayment"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата платежа</th>
          <th>Номер платежа</th>
          <th>Сумма</th>
          <th>Назначение</th>
          <th width="15"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${payments}" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td><g:link controller="payment" action="payrequestdetail" id="${record.id}">${record.id}</g:link></td>
          <td>${number(value:record.summa)}</td>
          <td>${record.destination}</td>
          <td>
            <g:link controller="payment" action="payrequestdetail" id="${record.id}" style="z-index:1" class="button" target="_blank"><i class="icon-pencil"></i></g:link>
          </td>
        </tr>
      </g:each>
      <g:if test="${!payments}">
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Платежей не найдено</a>
          </td>
        </tr>
      </g:if>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('lizdoppayment_submit_button').click();">
              Добавить новый платеж &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="lzdopForm" url="[action:'lizdoppayment', params:[agr_id:lizing.id]]" update="lizdoppayment" onComplete="\$('erroraddlizdoppaymentlist').up('div').hide();jQuery('#lizdoppaymentaddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="lizdoppayment_submit_button" value="Показать"/>
</g:formRemote>