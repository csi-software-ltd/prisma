<g:form name="allForm" controller="${controllerName}" target="_blank">
  <label class="auto" for="repdate">Дата:</label>
  <g:select class="mini" name="repdate" from="${reports}" optionKey="keyvalue" optionValue="disvalue"/>
  <label class="auto" for="bank_name">Банк:</label>
  <input type="text" style="width:630px" id="bank_name" name="bank_name" />
  <label class="auto" for="company_name">Компания:</label>
  <input type="text" id="company_name" name="company_name" />
  <div class="fright">
    <g:actionSubmit value="PDF" class="spacing" action="dcpayments"/>
    <g:actionSubmit value="XLS" class="spacing" action="dcpaymentsXLS"/>
    <input type="reset" class="spacing" value="Сброс"/>
    <g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'dcpayments',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('bank_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"bankname_autocomplete")}'
  });
  new Autocomplete('company_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"buhcompanyname_autocomplete")}'
  });
</script>