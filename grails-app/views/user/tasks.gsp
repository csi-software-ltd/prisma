<label class="auto">На сегодня:</label><br/><br/>
<table class="list">
  <tbody>
  <g:each in="${Tasktype.list()}" var="item" status="i">
  <g:if test="${task?."${item?.id}"?.size()}">
    <tr>
      <td>${item?.name}:</td>
      <td><g:if test="${task?."${item?.id}"?.size()==1}"><g:link controller="task" action="taskdetail" id="${task?."${item?.id}"[0]?.id?:0}" target="_blank">${task?."${item?.id}"?.size()}</g:link></g:if><g:else><g:link controller="task" action="index" target="_blank">${task?."${item?.id}"?.size()}</g:link></g:else></td>
    </tr>
  </g:if>
  </g:each>
  <g:if test="${!task_exists}">
    <tr><td colspan="2">Заданий нет</td></tr>
  </g:if>
  </tbody>
</table>
<label class="auto">На предыдущие дни:</label><br/><br/>
<table class="list">
  <tbody>
  <g:each in="${Tasktype.list()}" var="item" status="i">
  <g:if test="${task_old?."${item?.id}"?.size()}">
    <tr>
      <td>${item?.name}:</td>
      <td><g:if test="${task_old?."${item?.id}"?.size()==1}"><g:link controller="task" action="taskdetail" id="${task_old?."${item?.id}"[0]?.id?:0}" target="_blank">${task_old?."${item?.id}"?.size()}</g:link></g:if><g:else><g:link controller="task" action="index" target="_blank">${task_old?."${item?.id}"?.size()}</g:link></g:else></td>
    </tr>
  </g:if>
  </g:each>
  <g:if test="${!task_old_exists}">
    <tr><td colspan="2">Заданий нет</td></tr>
  </g:if>
  </tbody>
</table>