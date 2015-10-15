<g:form name="allForm" controller="${controllerName}" target="_blank">
  <label class="auto" for="reportstart">Дата от:</label>
  <g:datepicker class="normal nopad" name="reportstart" value="${String.format('%td.%<tm.%<tY',Tools.getPreviousWorkedDate(2))}"/>
  <label class="auto" for="reportend">Дата до:</label>
  <g:datepicker class="normal nopad" name="reportend" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
  <label class="auto" for="client_id">Клиент:</label>
  <g:select name="client_id" from="${Client.findAllByParent(0)}" optionKey="id" optionValue="name"/>
  <div class="fright">
    <g:actionSubmit value="PDF" class="spacing" action="clientcur"/>
    <input type="reset" class="spacing" value="Сброс"/>
		<g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'clientcur',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
</script>
