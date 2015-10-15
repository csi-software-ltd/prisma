<g:formRemote name="allForm" url="[controller:controllerName,action:'lizings']" update="list">
  <label class="auto" for="lid">Код</label>
  <input type="text" class="mini" id="lid" name="lid" value="${inrequest?.lid?:''}" />
  <label class="auto" for="company_name">Компания</label>
  <input type="text" id="company_name" name="company_name" value="${inrequest?.company_name?:''}" style="width:215px"/>
  <label class="auto" for="modstatus">Статус</label>
  <g:select class="auto" name="modstatus" value="${inrequest?.modstatus}" from="['Активные','Архив']" keys="[1,0]"/>
  <label class="auto" for="project_id">Проект</label>
  <g:select class="mini" name="project_id" value="${inrequest?.project_id}" from="${projects}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/>
  <label for="lizsort">Признак:</label>
  <g:select class="mini" name="lizsort" value="${inrequest?.lizsort}" from="['Сублизинг','Лизинг']" keys="[0,1]" noSelection="${['-100':'все']}"/>
  <label class="auto" for="responsible">Ответственный:</label>
  <g:select class="mini" name="responsible" value="${inrequest?.responsible?:0}" from="${users}" optionValue="value" optionKey="key" noSelection="${['0':'все']}"/>
  <label class="auto" for="car_id">Машины:</label>
  <g:select class="mini" name="car_id" value="${inrequest?.car_id}" from="${cars}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/>
  <label class="auto" for="cessionstatus">
    <input type="checkbox" id="cessionstatus" name="cessionstatus" value="1" <g:if test="${inrequest?.cessionstatus}">checked</g:if> />
    Уступка
  </label>
  <div class="fleft">
    <g:link action="lizingpayrequests" class="button">Заявки на платежи &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </div>
  <div class="fright">
    <input type="button" class="spacing reset" value="Сброс" onclick="resetlizingfilter()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${iscanedit}">
    <g:link action="lizing" class="button">Добавить новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
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
