<g:formRemote name="allForm" url="[controller:controllerName,action:'spaces']" update="list">
  <label class="auto" for="sid">Код:</label>
  <input type="text" class="mini" id="sid" name="sid" value="${inrequest?.sid}" />
  <label class="auto" for="company_name">Компания:</label>
  <input type="text" style="width:225px" id="company_name" name="company_name" value="${inrequest?.company_name?:''}" />
  <div id="companyname_autocomplete" class="autocomplete" style="display:none"></div>
  <label class="auto" for="arendatype_id">Тип аренды:</label>
  <g:select name="arendatype_id" value="${inrequest?.arendatype_id?:0}" from="${arendatypes}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/>
  <label class="auto" for="spacetype_id">Помещение</label>
  <g:select name="spacetype_id" value="${inrequest?.spacetype_id?:0}" from="${spacetypes}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/>
  <label class="auto" for="enddate">Окончание</label>
  <g:datepicker class="normal nopad" style="width:200px" name="enddate" value="${inrequest?.enddate?String.format('%td.%<tm.%<tY',inrequest.enddate):''}"/>
  <label class="auto" for="modstatus">Статус</label>
  <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['Активные','Архив','Закрыт досрочно']" keys="[1,0,-1]" noSelection="${['-100':'все']}"/>
  <label for="is_nds" class="auto">НДС:</label>
  <g:select class="mini" name="is_nds" value="${inrequest?.is_nds}" from="['Да','Нет']" keys="10" noSelection="${['-100':'все']}"/>
  <label class="auto" for="address">Адрес:</label>
  <input type="text" id="address" name="address" value="${inrequest?.address?:''}" />
  <label class="auto" for="debt">
    <input type="checkbox" id="debt" name="debt" value="1" <g:if test="${inrequest?.debt}">checked</g:if> />
    Задолженность
  </label>
  <label class="auto" for="is_adrsame">
    <input type="checkbox" id="is_adrsame" name="is_adrsame" value="1" <g:if test="${inrequest?.is_adrsame}">checked</g:if> />
    Совп.
  </label><br/>
  <label class="auto" for="anumber">Номер:</label>
  <input type="text" id="anumber" name="anumber" value="${inrequest?.anumber}" />
  <div class="fright">
    <g:link action="spacepayrequests" class="button">Заявки на платежи &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
    <input type="button" class="spacing reset" value="Сброс" onclick="resetarendafilter()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${iscanedit}">
    <g:link action="space" class="button">Добавить новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('company_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_autocomplete")}'
  });
  jQuery("#enddate").mask("99.99.9999",{placeholder:" "});
</script>
