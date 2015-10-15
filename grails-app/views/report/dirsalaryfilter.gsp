<g:form name="allForm" controller="${controllerName}" target="_blank">
  <label class="auto" for="pers_id">Физ. лицо:</label>
  <g:select name="pers_id" from="${Pers.findAllByPerstype(2,[sort:'shortname',order:'asc'])}" optionValue="shortname" optionKey="id" noSelection="${['0':'все']}"/>
  <div class="fright">
    <g:remoteLink action="dirsalarycompute" class="button" onSuccess="\$('form_submit_button').click()">
      Пересчитать оклады &nbsp;<i class="icon-angle-right icon-large"></i>
    </g:remoteLink>
    <input type="reset" class="spacing" value="Сброс"/>
    <g:actionSubmit value="PDF" class="spacing" action="dirsalary"/>
    <g:actionSubmit value="XLS" class="spacing" action="dirsalaryXLS"/>
    <g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'dirsalary',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
</script>