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
    <g:formRemote name="createKreditPayrequestsForm" url="[action:'createkrpayrequests']" onSuccess="\$('form_submit_button').click();">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
        <g:if test="${!inrequest.modstatus}">
          <th><input type="checkbox" id="groupcheckbox" checked onclick="togglecheck()"></th>
        </g:if>
          <th>Дата платежа</th>
          <th>Плательщик</th>
          <th>Банк</th>
          <th>Тип кредита</th>
          <th>Сумма</th>
          <th>Назначение</th>
          <th>Статус</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
      <g:if test="${record.summarub>0&&(inrequest.modstatus?:0)==record.paidstatus}">
        <tr align="center">
        <g:if test="${!inrequest.modstatus}">
          <td><input type="checkbox" checked name="body" value="${record.id}"></td>
        </g:if>
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td><g:link controller="agreement" action="kredit" id="${record.kredit_id}">${record.client_name}</g:link></td>
          <td>${record.bank_name}</td>
          <td>${record.is_real?'Реал':record.is_tech?'Техн':'Реалтех'}</td>
          <td>${number(value:record.summarub)}</td>
          <td>Оплата тела кредита по договору ${record.anumber} от ${String.format('%td.%<tm.%<tY',record.adate)}</td>
          <td>${record.paidstatus==1?'В оплате':record.paidstatus==2?'Оплачен':'Неоплачен'}</td>
        </tr>
      </g:if>
      <g:if test="${record.summapercrub>0&&(inrequest.modstatus?:0)==record.percpaidstatus}">
        <tr align="center">
        <g:if test="${!inrequest.modstatus}">
          <td><input type="checkbox" checked name="perc" value="${record.id}"></td>
        </g:if>
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td><g:link controller="agreement" action="kredit" id="${record.kredit_id}">${record.client_name}</g:link></td>
          <td>${record.bank_name}</td>
          <td>${record.is_real?'Реал':record.is_tech?'Техн':'Реалтех'}</td>
          <td>${number(value:record.summapercrub)}</td>
          <td>Оплата процентов по договору ${record.anumber} от ${String.format('%td.%<tm.%<tY',record.adate)}</td>
          <td>${record.percpaidstatus==1?'В оплате':record.percpaidstatus==2?'Оплачен':'Неоплачен'}</td>
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