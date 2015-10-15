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
    <g:formRemote name="createLizingPayrequestsForm" url="[action:'createlzpayrequests']" onSuccess="\$('form_submit_button').click();">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
        <g:if test="${!inrequest.modstatus}">
          <th><input type="checkbox" id="groupcheckbox" checked onclick="togglecheck()"></th>
        </g:if>
          <th>Дата платежа</th>
          <th>Плательщик</th>
          <th>Получатель</th>
          <th>Сумма</th>
          <th>Назначение</th>
          <th>Статус</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
      <g:if test="${record.summa>0}">
        <tr align="center">
        <g:if test="${!inrequest.modstatus}">
          <td><input type="checkbox" checked name="lplanpayment" value="${record.id}"></td>
        </g:if>
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td><g:link controller="agreement" action="lizing" id="${record.lizing_id}">${record.arendator_name}</g:link></td>
          <td>${record.arendodatel_name}</td>
          <td>${number(value:record.summa)}</td>
          <td>Оплата лизинга по договору ${record.anumber} от ${String.format('%td.%<tm.%<tY',record.adate)}</td>
          <td>${record.modstatus==1?'В оплате':record.modstatus==2?'Оплачен':'Неоплачен'}</td>
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