<g:form name="allForm" url="[controller:controllerName,action:'kreditcompany']" target="_blank">
  <label class="auto" for="company_id">Компания</label>
  <g:select name="company_id" from="${companies}" optionKey="id" optionValue="name"/>
  <label class="auto" for="reportdate">Дата отчета</label>
  <g:datepicker class="normal nopad" name="reportdate" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
  <label class="auto" for="stopdate">Дата начала</label>
  <g:datepicker class="normal nopad" name="stopdate" value=""/>
  <div class="fright">
    <input type="button" class="reset spacing" value="Сброс" onclick="resetKreditcompanyFilter()"/>
    <input type="submit" id="form_submit_button" value="Сформировать" />
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('list').innerHTML='';
</script>