<g:formRemote name="allForm" url="[controller:'catalog', action:'cgrouplist']" update="list">
  <label class="auto" for="cgroup_name">Название:</label>
  <input type="text" id="cgroup_name" name="name" value="${inrequest?.name?:''}" />
  <div class="fright">
    <input type="button" class="spacing reset" value="Сброс" onclick="resetCgroupFilter()"/>
    <input type="submit" id="form_submit_button" value="Показать"/>
    <g:link action="cgroup" class="button">Новая группа &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>