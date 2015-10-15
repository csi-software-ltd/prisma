<g:form name="allForm" controller="${controllerName}" target="_blank">
  <label class="auto" for="reportstart">Дата от:</label>
  <g:datepicker class="normal nopad" name="reportstart" value="${String.format('%td.%<tm.%<tY',Tools.getPreviousWorkedDate(2))}"/>
  <label class="auto" for="reportend">Дата до:</label>
  <g:datepicker class="normal nopad" name="reportend" value="${String.format('%td.%<tm.%<tY',Tools.getPreviousWorkedDate())}"/>
  <label class="auto" for="company">Компания:</label>
  <input type="text" id="company" name="company" value=""/>
  <label class="auto" for="bankname">Банк:</label>
  <input type="text" style="width:550px" id="bankname" name="bankname" value=""/>
  <label class="auto" for="valuta_id">Валюта:</label>
  <g:select class="auto" id="valuta_id" name="valuta_id" from="${Valuta.findAllByModstatus(1)}" value="857" optionValue="name" optionKey="id"/>
  <label class="auto" for="sort">Группировка:</label>
  <g:select class="auto" id="sort" name="sort" from="['По компании','По банку']" keys="01"/>
  <div class="fright">
    <g:actionSubmit value="PDF" class="spacing" action="saldo"/>
    <input type="reset" class="spacing" value="Сброс"/>
		<g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'saldo',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('company', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_int_autocomplete")}'
  });
  new Autocomplete('bankname', {
    serviceUrl:'${resource(dir:"autocomplete",file:"banknameholding_autocomplete")}'
  });
</script>