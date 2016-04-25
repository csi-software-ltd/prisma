<style type="text/css">
  select { width: auto; }
</style>
<g:formRemote name="allForm" url="[controller:controllerName,action:'dccomissions']" update="list">
  <label class="auto" for="comissiondate_month">Месяц:</label>
  <g:datePicker class="auto" name="comissiondate" precision="month" value="${inrequest?.comissiondate_month&&inrequest?.comissiondate_year?new Date(inrequest.comissiondate_year-1900,inrequest.comissiondate_month-1,1):inrequest?.comissiondate_year?new Date(inrequest.comissiondate_year-1900,0,1):new Date()}" relativeYears="[114-new Date().getYear()..0]" noSelection="${['':'все']}"/>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Показать" />
    <g:link action="dccomission" class="button">Добавить комиссию &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  if(${inrequest?.comissiondate_month||!inrequest?.fromDetails?0:1}) $('comissiondate_month').selectedIndex = 0;
  if(${inrequest?.comissiondate_year||!inrequest?.fromDetails?0:1}) $('comissiondate_year').selectedIndex = 0;
  $('form_submit_button').click();
</script>