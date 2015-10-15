<div id="ajax_wrap">
  <div id="resultList">
    <g:form name="addrequestTaskForm" controller="task">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th><input type="checkbox" id="groupcheckbox" onclick="togglecheck()"></th>
          <th>Срок</th>
          <th>Сумма</th>
          <th>Категория</th>
          <th>Получатель</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${payrequests}" status="i" var="record">
        <tr align="center">
          <td><input type="checkbox" name="payrequestids" value="${record.id}"></td>
          <td>${record.paydate?String.format('%td.%<tm.%<tY',record.paydate):''}</td>
          <td>${number(value:record.summa)}</td>
          <td>${record.paycat==1?'договорной':record.paycat==2?'бюджетный':record.paycat==3?'персональный':record.paycat==4?'прочий':record.paycat==5?'банковский':'счета'}</td>
          <td>
          <g:if test="${record.paycat in [1,4]}">${record.tocompany}</g:if>
          <g:elseif test="${record.paycat==2}">${taxes[record.tax_id]}</g:elseif>
          <g:else>${Pers.get(record.pers_id)?.shortname}</g:else>
          </td>
        </tr>
      </g:each>
      <g:if test="${!payrequests}">
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Новых платежей не найдено</a>
          </td>
        </tr>
      </g:if><g:elseif test="${taskpay.taskpaystatus==0}">
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('addrequestTask_form_submit_button').click()">Добавить в задание</a>
          </td>
        </tr>
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('newrequestTask_form_submit_button').click()">Добавить в новое задание</a>
          </td>
        </tr>
      </g:elseif>
      </tbody>
    </table>
      <span style="display:none"><g:submitToRemote id="addrequestTask_form_submit_button" url="[action:'extendtask',id:taskpay.id]" onSuccess="location.reload(true)"/>
      <g:submitToRemote id="newrequestTask_form_submit_button" url="[action:'movetonewtask',id:taskpay.id]" onSuccess="getTaskAddPayrequests()"/></span>
    </g:form>
  </div>
</div>