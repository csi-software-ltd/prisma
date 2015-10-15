<g:formRemote name="allForm" url="[controller:controllerName,action:'trades']" update="list">
  <label class="auto" for="inn">ИНН:</label>
  <input type="text" class="mini" id="inn" name="inn" value="${inrequest?.inn?:''}" />
  <label class="auto" for="company_name">Компания:</label>
  <input type="text" id="company_name" name="company_name" value="${inrequest?.company_name?:''}" />
  <div id="companyname_autocomplete" class="autocomplete" style="display:none"></div>
  <label class="auto" for="tradetype">Тип:</label>
  <g:select name="tradetype" value="${inrequest?.tradetype}" from="['Услуги','Поставки']" keys="[0,1]" noSelection="${['-100':'все']}"/>
  <label for="tradesort">Признак договора:</label>
  <g:select class="mini" name="tradesort" value="${inrequest?.tradesort}" from="['Внешние','Холдинг']" keys="[0,1]" noSelection="${['-100':'все']}"/>
  <label class="auto" for="responsible">Ответственный:</label>
  <g:select name="responsible" value="${inrequest?.responsible?:0}" from="${users}" optionValue="value" optionKey="key" noSelection="${['0':'все']}"/>
  <label class="auto" for="debt">
    <input type="checkbox" id="debt" name="debt" value="1" <g:if test="${inrequest?.debt}">checked</g:if> />
    Задолженность
  </label>
  <label class="auto" for="modstatus">Статус:</label>
  <g:select name="modstatus" value="${inrequest?.modstatus}" from="['Активные','Архив']" keys="[1,0]"/>
  <div class="fright">
    <input type="button" class="spacing reset" value="Сброс" onclick="resettradefilter()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${iscanedit}">
    <g:link action="trade" class="button">Добавить новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('company_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_autocomplete")}'
  });
</script>