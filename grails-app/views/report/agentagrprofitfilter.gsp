<style type="text/css">
  select { width: auto; }
</style>
<g:form name="allForm" controller="${controllerName}" target="_blank">
  <label class="auto" for="type">Тип:</label>
  <g:select class="auto" name="type" from="['по месяцам','по кварталам']" keys="01"/>
  <label class="auto" for="report_year">Год:</label>
  <g:datePicker class="auto" name="report" precision="year" value="${new Date()}" relativeYears="[114-new Date().getYear()..1]"/>
  <div class="fright">
    <g:actionSubmit value="PDF" class="spacing" action="agentagrprofit"/>
    <g:actionSubmit value="XLS" class="spacing" action="agentagrprofitXLS"/>
    <input type="reset" class="spacing" value="Сброс"/>
		<g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'agentagrprofit',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
</script>