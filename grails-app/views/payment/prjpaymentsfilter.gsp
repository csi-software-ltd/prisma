<g:formRemote name="prjpaymentsForm" url="[controller:'payment',action:'prjpayments']" update="list">
  <label for="project_id">Проект:</label>
  <g:select name="project_id" value="${(inrequest?.project_id==null)?1:inrequest?.project_id}" from="${projects}" optionKey="id" optionValue="name"/>
  <div class="fright">
    <input type="button" class="reset spacing" value="Сброс" onclick="resetPrjFilter()"/>
    <input type="submit" id="form_submit_button" value="Показать"/>
  </div>
</g:formRemote>
<div class="clear"></div>
<script type="text/javascript">
  $('form_submit_button').click();
</script>
