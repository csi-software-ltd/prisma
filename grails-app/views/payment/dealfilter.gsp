<g:formRemote name="allForm" url="[controller:controllerName,action:'deals']" update="list">
  <label class="auto" for="client_id">Клиент:</label>
  <g:select name="client_id" value="${inrequest?.client_id}" from="${clients}" optionKey="id" optionValue="name" noSelection="${['0':'все']}"/>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>