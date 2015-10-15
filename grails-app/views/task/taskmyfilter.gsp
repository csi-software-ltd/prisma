<g:formRemote name="taskForm" url="[controller:'task',action:'taskmylist']" update="[success:'list']">
  <div class="fright">
    <g:link action="taskdetail" class="button">Новое &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
    <input type="submit" style="display:none" id="form_submit_button" value="Показать"/>
  </div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>