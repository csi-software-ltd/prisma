<g:formRemote name="taskForm" url="[controller:'task',action:'taskalllist']" update="[success:'list']">
  <label for="taskstaus">Статус:</label>
  <g:select name="taskstatus" value="${inrequest?.taskstatus}" from="${Taskstatus.list()}" optionKey="id" optionValue="name" noSelection="${['-1':'все']}"/>
  <label for="department_id">Отдел:</label>
  <g:select name="department_id" value="${inrequest?.department_id}" from="${Department.list([sort:'name',order:'asc'])}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}" onchange="getExecutor(this.value)"/><br/>
  <label for="executor">Адресат:</label>
  <span id="executor_span"><g:select name="executor" value="${inrequest?.executor}" from="${executor}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}"/></span>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс" onclick="resetdata()"/>
    <input type="submit" id="form_submit_button" value="Показать"/>
  </div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>