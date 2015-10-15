<g:formRemote name="allForm" url="[controller:controllerName,action:'deposits']" update="list">
  <label class="auto" for="did">Код</label>
  <input type="text" class="mini" id="did" name="did" value="${inrequest?.did}" />
  <label class="auto" for="bankcompany_id">Банк:</label>
  <g:select name="bankcompany_id" value="${inrequest?.bankcompany_id}" from="${banks}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/>
  <div class="fright">
    <input type="button" class="spacing reset" value="Сброс" onclick="resetdepositfilter()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${iscanedit}">
    <g:link action="deposit" class="button">Добавить новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>