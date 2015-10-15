<g:formRemote name="allForm" url="[controller:controllerName,action:'licenses']" update="list">
  <label class="auto" for="company_id">Код лицензиата:</label>
  <input type="text" class="mini" id="company_id" name="company_id" value="${inrequest?.company_id?:''}" />
  <label class="auto" for="industry_id">Направление:</label>
  <g:select class="auto" name="industry_id" value="${inrequest?.industry_id?:0}" from="${industries}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/>
  <div class="clear"></div>
  <div class="fleft">
    <g:link action="licensepayrequests" class="button">
      Заявки на платежи &nbsp;<i class="icon-angle-right icon-large"></i>
    </g:link>
  </div>
  <div class="fright">
    <input type="button" class="spacing reset" value="Сброс" onclick="resetlicensefilter()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${iscanedit}">
    <g:link action="license" class="button">Добавить новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>
