<style type="text/css">
  select { width: auto; }
</style>
<g:formRemote name="prjpaymentsForm" url="[controller:'payment',action:'prjpayments']" update="list">
  <label for="project_id">Проект:</label>
  <g:select name="project_id" value="${(inrequest?.project_id==null)?1:inrequest?.project_id}" from="${projects}" optionKey="id" optionValue="name"/>
  <label class="auto" for="platperiod_month">Период</label>
  <g:datePicker class="auto" name="platperiod" precision="month" relativeYears="[114-new Date().getYear()..0]" noSelection="${['':'все']}"/>
  <div class="fright">
    <input type="button" class="reset spacing" value="Сброс" onclick="resetPrjFilter()"/>
    <input type="submit" id="form_submit_button" value="Показать"/>
  </div>
</g:formRemote>
<div class="clear"></div>
<script type="text/javascript">
	$('platperiod_month').selectedIndex = 0;
  $('form_submit_button').click();
</script>
