<g:form name="allForm" controller="${controllerName}" target="_blank">
  <label class="auto" for="type">Тип:</label>
  <g:select class="mini" name="type" from="['по отделам','по подотчетным лицам','по заемным средствам','задолженность по штрафам']" keys="0123" onchange="toggledate(this.value)"/>
  <span id="datesection"><label class="auto" for="reportdate">Дата отчета:</label>
  <g:datepicker class="normal nopad" name="reportdate" value="${String.format('%td.%<tm.%<tY',new Date())}"/></span>
  <div class="fright">
    <g:actionSubmit value="PDF" class="spacing" action="cashdep"/>
    <g:actionSubmit value="XLS" class="spacing" action="cashdepXLS"/>
    <input type="reset" class="spacing" value="Сброс"/>
		<g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'cashdep',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
</script>