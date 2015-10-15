<g:formRemote name="allForm" url="[controller:controllerName,action:'enqtypes']" update="list">
  <label for="type">Тип справки:</label>
  <g:select class="mini" name="type" value="${inrequest?.type}" from="['В налоговую','В банк']" keys="12" noSelection="${['-100':'все']}"/>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Показать" />
    <g:link action="enqtype" class="button">Новый тип справки &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>