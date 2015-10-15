<g:formRemote name="allForm" url="[controller:controllerName,action:'cars']" update="list">
  <div class="fright">
    <input type="submit" style="display:none" id="form_submit_button" value="Показать" />
    <g:link action="car" class="button">Новая машина &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>