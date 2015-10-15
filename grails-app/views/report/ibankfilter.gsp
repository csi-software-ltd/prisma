<g:form name="allForm" controller="${controllerName}" target="_blank">
  <label class="auto" for="ibank_bankname">Банк:</label>
  <input type="text" style="width:470px" id="ibank_bankname" name="bankname" value=""/>
  <div class="fright">
    <g:actionSubmit value="PDF" class="spacing" action="ibank"/>
    <g:actionSubmit value="XLS" class="spacing" action="ibankXLS"/>
    <input type="reset" class="spacing" value="Сброс"/>
		<g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'ibank',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('ibank_bankname', {
    serviceUrl:'${resource(dir:"autocomplete",file:"banknameholding_autocomplete")}'
  });
</script>