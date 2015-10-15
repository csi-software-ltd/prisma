<style type="text/css">
  select { width: auto; }
</style>
<g:form name="allForm" controller="${controllerName}" target="_blank">
  <label class="auto" for="reportdate_month">Месяц:</label>
  <g:datePicker class="auto" name="reportdate" precision="month" value="${new Date()}" relativeYears="[114-new Date().getYear()..0]"/>
  <div class="fright">
    <g:actionSubmit value="PDF" class="spacing" action="clientcomission"/>
    <input type="reset" class="spacing" value="Сброс"/>
		<g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'clientcomission',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
</script>