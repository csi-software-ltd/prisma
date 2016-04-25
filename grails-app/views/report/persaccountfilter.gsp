<g:form name="allForm" controller="${controllerName}" target="_blank">
  <label class="auto" for="pers_name">Физ. лицо:</label>
  <input type="text" id="pers_name" name="pers_name" value=""/>
  <label class="auto" for="bankname">Банк:</label>
  <input type="text" id="bankname" style="width:470px" name="bankname" value=""/>
  <label class="auto" for="paccount">Лиц. счет:</label>
  <input type="text" id="paccount" name="paccount" value=""/>
  <label class="auto" for="is_main">Тип карты:</label>
  <g:select class="mini" name="is_main" value="" from="['Основная','Дополнительная']" keys="[1,0]" noSelection="${['-100':'все']}"/>
  <label class="auto" for="modstatus">Статус карты:</label>
  <g:select class="mini" name="modstatus" value="" from="['Активная','Неактивная']" keys="[1,0]" noSelection="${['-100':'все']}"/>
  <label class="auto" for="sort">Сортировка:</label>
  <g:select class="mini" name="sort" value="" from="['По ФИО','По банкам']" keys="[1,0]"/>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <g:actionSubmit value="PDF" class="spacing" action="persaccount"/>
    <g:actionSubmit value="XLS" class="spacing" action="persaccountXLS"/>
    <g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'persaccount',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  new Autocomplete('pers_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"persname_autocomplete")}'
  });
  new Autocomplete('bankname', {
    serviceUrl:'${resource(dir:"autocomplete",file:"banknamebik_autocomplete")}'
  });
  $('form_submit_button').click();
</script>