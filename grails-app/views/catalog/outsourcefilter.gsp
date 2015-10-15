<div class="error-box" style="margin:0 0 15px 15px;display:none">
  <span class="icon icon-warning-sign icon-3x"></span>
  <ul id="errorlist">
    <li></li>
  </ul>
</div>
<g:formRemote name="allForm" url="[controller:'catalog', action:'outsourcelist']" update="list">
  <label class="auto" for="outsource_name">Название:</label>
  <input type="text" id="outsource_name" name="name" value="${inrequest?.name?:''}" />
  <div id="name_autocomplete" class="autocomplete" style="display:none"></div>
  <label class="auto" for="modstatus">Статус:</label>
  <g:select name="modstatus" value="${inrequest?.modstatus}" from="['активный','неактивный']" keys="[1,0]" noSelection="${['-1':'все']}"/>  
  <div class="fright">
    <input type="button" class="spacing reset" value="Сброс" onclick="resetOutsourceFilter()"/>
    <input type="submit" id="form_submit_button" value="Показать"/>
  <g:if test="${iscanadd}">
    <g:link action="outsource" class="button">Новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
   new Autocomplete('outsource_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"outsource_autocomplete")}'
  });
</script>
