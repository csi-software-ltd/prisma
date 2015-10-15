<g:form name="allForm" controller="${controllerName}" target="_blank">
  <div class="fright">
    <g:actionSubmit value="PDF" class="spacing" action="dopcard"/>
		<g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'dopcard',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
</script>