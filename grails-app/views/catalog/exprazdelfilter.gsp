<g:formRemote name="allForm" url="[controller:'catalog', action:'exprazdellist']" update="list">
  <label class="auto" for="exprazdel_name">Название:</label>
  <input type="text" id="exprazdel_name" name="name" value="${inrequest?.name?:''}" />
  <div id="name_autocomplete" class="autocomplete" style="display:none"></div>
  <div class="fright">
    <input type="button" class="spacing reset" value="Сброс" onclick="resetExprazdelFilter()"/>
    <input type="submit" id="form_submit_button" value="Показать"/>
  <g:if test="${iscanadd}">
    <g:link action="exprazdel" class="button">Новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"expensetype1_autocomplete")}'
  });
</script>
