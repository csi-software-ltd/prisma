<g:form name="allForm" url="[controller:controllerName,action:'subspace']" target="_blank">
  <label class="auto" for="company_id">Арендатор:</label>
  <g:select id="company_id" name="company_id" from="${companies}" optionKey="id" optionValue="name" noSelection="${['0':'все']}"/>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Сформировать" />
  </div>
  <div class="clear"></div>
</g:form>