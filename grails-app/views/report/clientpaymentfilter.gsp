<g:form name="allForm" controller="${controllerName}" target="_blank">
  <label class="auto" for="reportstart">Дата от:</label>
  <g:datepicker class="normal nopad" name="reportstart" value="${String.format('%td.%<tm.%<tY',Tools.getPreviousWorkedDate(2))}"/>
  <label class="auto" for="reportend">Дата до:</label>
  <g:datepicker class="normal nopad" name="reportend" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
  <label for="client_id">Клиент:</label>
  <g:select name="client_id" from="${Client.list()}" optionKey="id" optionValue="name" noSelection="${['-1':'все']}"/>
  <div class="fright">
    <g:actionSubmit value="PDF" class="spacing" action="clientpayment"/>
    <g:actionSubmit value="XLS" class="spacing" action="clientpaymentXLS"/>
    <input type="reset" class="spacing" value="Сброс"/>
		<g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'clientpayment',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
</script>