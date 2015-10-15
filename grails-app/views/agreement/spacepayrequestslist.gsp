<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${searchresult.records}">
  <div id="resultList">
    <g:formRemote name="createSpacePayrequestsForm" url="[action:'createsppayrequests']" onSuccess="\$('form_submit_button').click();">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
        <g:if test="${!inrequest.modstatus}">
          <th><input type="checkbox" id="groupcheckbox" checked onclick="togglecheck()"></th>
        </g:if>
          <th>Срок оплаты</th>
          <th>Плательщик</th>
          <th>Получатель</th>
          <th>Сумма</th>
          <th>Статус</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
      <g:if test="${record.rate>0}">
        <tr align="center">
        <g:if test="${!inrequest.modstatus}">
          <td><input type="checkbox" ${record.is_nopayment||!record.paystatus?'disabled':'checked'} name="space" value="${record.id}"></td>
        </g:if>
          <td>${String.format('%td.%<tm.%<tY',new Date(today.getYear(),today.getMonth()+(today.getDate()>record.payterm?1:0),record.payterm))}</td>
          <td><g:link controller="agreement" action="space" id="${record.id}">${record.arendator_name}</g:link></td>
          <td>${record.arendodatel_name}</td>
          <td>${number(value:record.actrate)}</td>
          <td>${record.is_nopayment?'Оплата не требуется':!record.paystatus?'Невозможно оплатить':record.payreqstatus?'Оплачен':'Неоплачен'}</td>
        </tr>
      </g:if>
      </g:each>
      </tbody>
    </table>
      <input type="submit" id="createprequests_form_submit_button" style="display:none"/>
    </g:formRemote>
  </div>
  <div style="padding:10px">
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>