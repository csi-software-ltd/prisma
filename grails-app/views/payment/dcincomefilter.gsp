<style type="text/css">
  select { width: auto; }
</style>
<g:formRemote name="allForm" url="[controller:controllerName,action:'dcincome']" update="list">
  <label class="auto" for="incomedate_month">Месяц:</label>
  <g:datePicker class="auto" name="incomedate" precision="month" value="${new Date()}" relativeYears="[114-new Date().getYear()..0]"/>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>