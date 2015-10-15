<g:form name="myoperationForm" url="[controller:'cash']" target="_blank">
  <label class="auto" for="opdatestart">Дата опер. с:</label>
  <g:datepicker class="normal nopad" name="opdatestart" value=""/>
  <label class="auto" for="opdateend">по:</label>
  <g:datepicker class="normal nopad" name="opdateend" value=""/>
  <div class="fright">
    <g:actionSubmit value="PDF" style="display:none" class="spacing" action="myoperations" onclick="\$('form_submit_button').click();return false"/>
    <g:actionSubmit value="XLS" class="spacing" action="myoperationsXLS"/>
    <input type="reset" class="spacing" value="Сброс"/>
    <g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'myoperations']" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  jQuery("#opdatestart").mask("99.99.9999",{placeholder:" "});
  jQuery("#opdateend").mask("99.99.9999",{placeholder:" "});
  $('form_submit_button').click();
</script>