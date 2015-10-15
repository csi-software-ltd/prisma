<style type="text/css">
  select { width: auto; }
</style>
<g:formRemote name="allForm" url="[controller:controllerName,action:'dccomissions']" update="list">
  <label class="auto" for="comissiondate_month">Месяц:</label>
  <g:datePicker class="auto" name="comissiondate" precision="month" value="${new Date()}" relativeYears="[114-new Date().getYear()..0]"/>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Показать" />
    <g:link action="dccomission" class="button">Добавить комиссию &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>