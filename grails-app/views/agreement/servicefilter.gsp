<g:formRemote name="allForm" url="[controller:controllerName,action:'services']" update="list">
  <label class="auto" for="sid">Код</label>
  <input type="text" class="mini" id="sid" name="sid" value="${inrequest?.sid?:''}" />
  <label class="auto" for="zcompany_name">Заказчик:</label>
  <input type="text" id="zcompany_name" name="zcompany_name" value="${inrequest?.zcompany_name?:''}" />
  <div id="zcompanyname_autocomplete" class="autocomplete" style="display:none"></div>
  <label class="auto" for="ecompany_name">Исполнитель:</label>
  <input type="text" id="ecompany_name" name="ecompany_name" value="${inrequest?.ecompany_name?:''}" />
  <div id="ecompanyname_autocomplete" class="autocomplete" style="display:none"></div>
  <label for="atype">Тип договора:</label>
  <g:select class="mini" name="atype" value="${inrequest?.atype}" from="${stypes}" optionKey="id" optionValue="name" noSelection="${['-100':'все']}"/>
  <label for="asort">Признак договора:</label>
  <g:select class="mini" name="asort" value="${inrequest?.asort}" from="['Внешние','Внутренние','Для внешних']" keys="123" noSelection="${['-100':'все']}"/>
  <label class="auto" for="dateend">Окончание</label>
  <g:datepicker class="normal nopad" name="dateend" value="${inrequest?.dateend?String.format('%td.%<tm.%<tY',inrequest.dateend):''}"/>
  <label class="auto" for="modstatus">Статус</label>
  <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['Активные','Архив']" keys="10"/>
  <div class="fright">
    <input type="button" class="spacing reset" value="Сброс" onclick="resetservicefilter()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${iscanedit}">
    <g:link action="service" class="button">Добавить новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('zcompany_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_autocomplete")}'
  });
  new Autocomplete('ecompany_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_autocomplete")}'
  });
  jQuery("#dateend").mask("99.99.9999",{placeholder:" "});
</script>