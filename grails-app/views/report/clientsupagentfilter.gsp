<g:form name="allForm" controller="${controllerName}" target="_blank">
  <label class="auto" for="reportdate">Дата от:</label>
  <g:datepicker class="normal nopad" name="reportdate" value="${String.format('%td.%<tm.%<tY',new Date(new Date().getYear(),new Date().getMonth(),1))}"/>
  <div class="fright">
    <g:actionSubmit value="PDF" class="spacing" action="clientsupagent"/>
    <g:actionSubmit value="XLS" class="spacing" action="clientsupagentXLS"/>
    <input type="reset" class="spacing" value="Сброс"/>
		<g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'clientsupagent',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
</script>