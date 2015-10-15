<g:formRemote name="allForm" url="[controller:'catalog', action:'visgrouplist']" update="list">
  <label class="auto" for="visgroup_name">Название:</label>
  <input type="text" id="visgroup_name" name="name" value="${inrequest?.name?:''}" />
  <div class="fright">
    <input type="button" class="spacing reset" value="Сброс" onclick="resetVisgroupFilter()"/>
    <input type="submit" id="form_submit_button" value="Показать"/>
    <g:link action="visualgroup" class="button">Новая группа &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>