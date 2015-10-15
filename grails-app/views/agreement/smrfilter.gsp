<g:formRemote name="allForm" url="[controller:controllerName,action:'smrs']" update="list">
  <label class="auto" for="client_name">Заказчик:</label>
  <input type="text" id="client_name" style="width:210px" name="client_name" value="${inrequest?.client_name?:''}" />
  <div id="zcompanyname_autocomplete" class="autocomplete" style="display:none"></div>
  <label class="auto" for="supplier_name">Исполнитель:</label>
  <input type="text" id="supplier_name" style="width:210px" name="supplier_name" value="${inrequest?.supplier_name?:''}" />
  <div id="ecompanyname_autocomplete" class="autocomplete" style="display:none"></div>
  <label class="auto" for="modstatus">Статус</label>
  <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['Активные','Архив']" keys="10"/>
  <label class="auto" for="smrcat_id">Тип работ:</label>
  <g:select class="mini" name="smrcat_id" value="${inrequest?.smrcat_id}" from="${smrcats}" optionKey="id" optionValue="name" noSelection="${['0':'все']}"/>
  <label class="auto" for="smrsort">Признак договора:</label>
  <g:select class="mini" name="smrsort" value="${inrequest?.smrsort}" from="['Внешний подряд','Внутренний подряд','Внешний заказчик']" keys="123" noSelection="${['-100':'все']}"/>
  <div class="fright">
    <input type="button" class="spacing reset" value="Сброс" onclick="resetsmrfilter()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${iscanedit}">
    <g:link action="smr" class="button">Добавить новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('client_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_autocomplete")}'
  });
  new Autocomplete('supplier_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_autocomplete")}'
  });
</script>