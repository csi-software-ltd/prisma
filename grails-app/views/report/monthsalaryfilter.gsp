<g:form name="allForm" controller="${controllerName}" target="_blank">
  <label class="auto" for="repdate">Дата:</label>
  <g:select class="mini" name="repdate" value="" from="${repdates}" optionKey="keyvalue" optionValue="disvalue"/>
  <label class="auto" for="department_id">Отдел:</label>
  <g:select name="department_id" value="" from="${Department.list()}" optionValue="name" optionKey="id" noSelection="${['-100':'все']}"/>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <g:actionSubmit value="PDF" class="spacing" action="monthsalary"/>
    <g:actionSubmit value="XLS" class="spacing" action="monthsalaryXLS"/>
    <g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'monthsalary',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
</script>