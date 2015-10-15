<g:form name="allForm" url="[controller:controllerName,action:'spacesummary']" target="_blank">
  <label class="auto" for="arendator_name">Арендатор:</label>
  <input type="text" id="arendator_name" name="arendator_name" value=""/>
  <label class="auto" for="arendodatel_name">Арендодатель:</label>
  <input type="text" id="arendodatel_name" name="arendodatel_name" value=""/>
  <label class="auto" for="is_adrsame">
    <input type="checkbox" id="is_adrsame" name="is_adrsame" value="1" <g:if test="${inrequest?.is_adrsame}">checked</g:if> />
    Совп.
  </label><br/>
  <label class="auto" for="address">Адрес:</label>
  <input type="text" id="address" name="address" value="" />
  <label class="auto" for="spacetype_id">Тип помещений:</label>
  <g:select class="mini" name="spacetype_id" value="" from="${Spacetype.list()}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/>
  <label class="auto" for="arendatype_id">Тип аренды:</label>
  <g:select class="mini" name="arendatype_id" value="" from="${Arendatype.list()}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/>
  <label class="auto" for="subrenting">Субаренда:</label>
  <g:select class="mini" name="subrenting" value="" from="['Разрешена','С письменного разрешения','Без права субаренды']" keys="123" noSelection="${['0':'все']}"/>
  <label class="auto" for="payterm_from">День оплаты с:</label>
  <g:select class="mini" name="payterm_from" value="" from="${1..31}" noSelection="${['0':'не задан']}"/>
  <label class="auto" for="payterm_to">День оплаты по:</label>
  <g:select class="mini" name="payterm_to" value="" from="${1..31}" noSelection="${['0':'не задан']}"/>
  <label class="auto" for="enddate_from">Окончание с:</label>
  <g:datepicker class="normal nopad" name="enddate_from" value=""/>
  <label class="auto" for="enddate_to">Окончание по:</label>
  <g:datepicker class="normal nopad" name="enddate_to" value=""/>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Сформировать" />
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  new Autocomplete('arendator_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_autocomplete")}'
  });
  new Autocomplete('arendodatel_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_autocomplete")}'
  });
  jQuery("#enddate_from").mask("99.99.9999",{placeholder:" "});
  jQuery("#enddate_to").mask("99.99.9999",{placeholder:" "});
</script>