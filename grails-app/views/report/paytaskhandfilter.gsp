<g:form name="allForm" controller="${controllerName}" target="_blank">
  <label class="auto" for="reportstart">Срок от:</label>
  <g:datepicker class="normal nopad" name="reportstart" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
  <label class="auto" for="reportend">Срок до:</label>
  <g:datepicker class="normal nopad" name="reportend" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
  <label class="auto" for="company_id">Компания:</label>
  <g:select name="company_id" from="${companies}" optionValue="name" optionKey="id" noSelection="${['0':'все']}" />
  <label class="auto" for="execdatestart">Исполнение от:</label>
  <g:datepicker class="normal nopad" name="execdatestart" value=""/>
  <label class="auto" for="execdateend">до:</label>
  <g:datepicker class="normal nopad" name="execdateend" value=""/>
  <label class="auto" for="bank_id">Банк:</label>
  <g:select name="bank_id" from="${banks}" optionValue="bankname" optionKey="bank_id" noSelection="${['':'все']}" />
  <br/><label class="auto" for="modstatus">Статус</label>
  <g:select class="mini" name="modstatus" value="" from="['Исполнено','Не исполнено']" keys="10" noSelection="${['-100':'все']}"/>
  <div class="fright">
    <g:actionSubmit value="PDF" class="spacing" action="paytaskhand"/>
    <g:actionSubmit value="XLS" class="spacing" action="paytaskhandXLS"/>
    <input type="reset" class="spacing" value="Сброс"/>
		<g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'paytaskhand',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
</script>