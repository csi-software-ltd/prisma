<g:formRemote name="taskForm" url="[controller:'task',action:'tasklist']" update="[success:'list']">
  <div class="fright">
    <input type="submit" style="display:none" id="form_submit_button" value="Показать"/>
  </div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>