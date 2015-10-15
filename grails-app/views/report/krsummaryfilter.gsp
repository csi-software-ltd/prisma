<g:form name="allForm" controller="${controllerName}" target="_blank">
  <div class="fright">
    <g:remoteLink action="krdebtcompute" class="button" onSuccess="\$('form_submit_button').click()">
      Пересчитать задолженность &nbsp;<i class="icon-angle-right icon-large"></i>
    </g:remoteLink>
    <g:actionSubmit value="PDF" class="spacing" action="krsummary"/>
    <g:actionSubmit value="XLS" class="spacing" action="krsummaryXLS"/>
    <g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'krsummary',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
</script>