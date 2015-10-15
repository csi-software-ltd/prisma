<g:formRemote name="allForm" url="[controller:controllerName,action:'spaceprolongs']" update="list">
  <label for="permitstatus">Статус разрешения:</label>
  <g:select class="mini" name="permitstatus" value="${inrequest?.permitstatus}" from="['Нет информации','Разрешено','Отказано']" keys="[0,1,-1]" noSelection="${['-100':'Актуальные']}"/>
  <div class="fright">
    <input type="button" class="reset spacing" value="Сброс" onclick="resetSpaceprolongForm()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>