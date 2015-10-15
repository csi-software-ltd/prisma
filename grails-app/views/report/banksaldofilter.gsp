<g:form name="allForm" controller="${controllerName}" target="_blank">
  <label class="auto" for="b_company">Компания:</label>
  <input type="text" id="b_company" name="company" value=""/>
  <label class="auto" for="b_bankname">Банк:</label>
  <input type="text" style="width:470px" id="b_bankname" name="bankname" value=""/>
  <label class="auto" for="valuta_id">Валюта:</label>
  <g:select class="auto" id="valuta_id" name="valuta_id" from="${Valuta.findAllByModstatus(1)}" value="857" optionValue="name" optionKey="id"/>
  <label class="auto" for="sort">Группировка:</label>
  <g:select class="auto" id="sort" name="sort" from="['По компании','По банку']" keys="01"/>
  <div class="fright">
    <g:actionSubmit value="PDF" class="spacing" action="banksaldo"/>
    <g:actionSubmit value="XLS" class="spacing" action="banksaldoXLS"/>
    <input type="reset" class="spacing" value="Сброс"/>
		<g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'banksaldo',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('b_company', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_int_autocomplete")}'
  });
  new Autocomplete('b_bankname', {
    serviceUrl:'${resource(dir:"autocomplete",file:"banknameholding_autocomplete")}'
  });
</script>