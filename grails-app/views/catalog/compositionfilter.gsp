<div class="clear"></div>
<div class="error-box" style="display:none">
  <span class="icon icon-warning-sign icon-3x"></span>
  <ul id="errorlistComposition">
    <li></li>
  </ul>
</div>
<g:formRemote name="allForm" url="[controller:'catalog',action:'compositionlist']" update="list">
  <label class="auto" for="composition_name">Название:</label>
  <input type="text" id="composition_name" name="name" value="${inrequest?.name?:''}" />
  <div id="name_autocomplete" class="autocomplete" style="display:none"></div>
  <label class="auto" for="position_id">Тип:</label>
  <g:select name="position_id" value="${inrequest?.position_id?:0}" from="${Position.list()}" optionKey="id" optionValue="name" noSelection="${['0':'все']}"/>      
  <div class="fright" id="link">
    <input type="button" class="spacing reset" value="Сброс" onclick="resetCompositionFilter()"/>
    <input type="submit" id="form_submit_button" value="Показать"/>
  <g:if test="${iscanadd}">
    <a class="button" onclick="showCompositionWindow()">Новая &nbsp;<i class="icon-angle-right icon-large"></i></a>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<div id="createcomposition" class="tabs" style="display:none">
</div>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('composition_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"composition_autocomplete")}'
  });
</script>
