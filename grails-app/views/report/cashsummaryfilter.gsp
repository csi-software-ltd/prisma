<style type="text/css">
  select { width: auto; }
</style>
<g:form name="allForm" url="[controller:controllerName,action:'cashsummary']" target="_blank">
  <label class="auto" for="reportdate_month">Месяц:</label>
  <g:datePicker class="auto" name="reportdate" precision="month" value="${new Date()}" relativeYears="[114-new Date().getYear()..0]"/>
  <label class="auto" for="department_id">Отдел:</label>
  <g:select name="department_id" from="${Department.list()}" optionKey="id" optionValue="name" noSelection="${['0':'все']}"/>
  <label class="auto" for="is_needreports">
    <input type="checkbox" id="is_needreports" name="is_needreports" value="1" />
    Выгружать с отчетами
  </label>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Сформировать" />
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('list').innerHTML='';
</script>