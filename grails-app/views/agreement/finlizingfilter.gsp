<g:formRemote name="allForm" url="[controller:controllerName,action:'finlizings']" update="list">
  <label class="auto" for="flid">Код</label>
  <input type="text" class="mini" id="flid" name="flid" value="${inrequest?.flid?:''}" />
  <label class="auto" for="fl_company_name">Лизингополучатель</label>
  <input type="text" id="fl_company_name" name="company_name" value="${inrequest?.company_name?:''}" />
  <label class="auto" for="modstatus">Статус</label>
  <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['Активные','Архив']" keys="[1,0]"/>
  <div class="fright">
    <input type="button" class="spacing reset" value="Сброс" onclick="resetflfilter()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${iscanedit}">
    <g:link action="finlizing" class="button">Добавить новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  new Autocomplete('fl_company_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_int_autocomplete")}'
  });
  $('form_submit_button').click();
</script>
