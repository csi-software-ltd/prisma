<g:formRemote name="allForm" url="[controller:'catalog', action:'zalogtypelist']" update="list">
  <label class="auto" for="zalogtype_name">Название:</label>
  <input type="text" id="zalogtype_name" name="name" value="${inrequest?.name?:''}" />
  <div class="fright">
    <input type="button" class="spacing reset" value="Сброс" onclick="resetZalogtypeFilter()"/>
    <input type="submit" id="form_submit_button" value="Показать"/>
  <g:if test="${iscanadd}">
    <g:link action="zalogtype" class="button">Новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>