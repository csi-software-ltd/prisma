<g:form name="allForm" controller="${controllerName}" target="_blank">
  <label class="auto" for="reportdate">Дата отчета</label>
  <g:datepicker class="normal nopad" name="reportdate" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
  <label class="auto" for="cname">Компания</label>
  <input type="text" id="cname" name="cname" value="" />
  <div class="fright">
    <g:actionSubmit value="PDF" class="spacing" action="dirsummary"/>
    <g:actionSubmit value="XLS" class="spacing" action="dirsummaryXLS"/>
    <input type="reset" class="spacing" value="Сброс"/>
    <g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'dirsummary',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  new Autocomplete('cname', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_int_autocomplete")}'
  });
  $('form_submit_button').click();
</script>