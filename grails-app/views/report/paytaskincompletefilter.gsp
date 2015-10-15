<g:form name="allForm" controller="${controllerName}" target="_blank">
  <label class="auto" for="reportstart">Дата от:</label>
  <g:datepicker class="normal nopad" name="reportstart" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
  <label class="auto" for="reportend">Дата до:</label>
  <g:datepicker class="normal nopad" name="reportend" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
  <label class="auto" for="company_id">Компания:</label>
  <g:select name="company_id" from="${companies}" optionValue="name" optionKey="id" noSelection="${['0':'все']}" />
  <label class="auto" for="bank_id">Банк:</label>
  <g:select name="bank_id" from="${banks}" optionValue="bankname" optionKey="bank_id" noSelection="${['':'все']}" />
  <div class="fright">
    <g:actionSubmit value="PDF" class="spacing" action="paytaskincomplete"/>
    <g:actionSubmit value="XLS" class="spacing" action="paytaskincompleteXLS"/>
    <input type="reset" class="spacing" value="Сброс"/>
		<g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'paytaskincomplete',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
</script>