<g:form name="allForm" url="[controller:controllerName,action:'spacefolio']" target="_blank">
  <label class="auto" for="company_id">Арендатор:</label>
  <g:select id="company_id" name="company_id" from="${companies}" optionKey="id" optionValue="name"/>
  <label class="auto" for="spacefoliodate">Дата портфеля:</label>
  <g:datepicker class="normal nopad" name="spacefoliodate" value="${String.format('%td.%<tm.%<tY',new Date())}"/><br/>
  <label class="auto" for="spacedate_start">Период с:</label>
  <g:datepicker class="normal nopad" name="spacedate_start"/>
  <label class="auto" for="spacedate_end">по:</label>
  <g:datepicker class="normal nopad" name="spacedate_end"/>
  <div class="fright">
    <input type="button" class="reset spacing" value="Сброс" onclick="resetSpacefolioFilter()"/>
    <input type="submit" id="form_submit_button" value="Сформировать" />
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('list').innerHTML='';
</script>