<g:formRemote name="allForm" url="[controller:controllerName,action:'indeposits']" update="list">
  <label class="auto" for="indid">Код</label>
  <input type="text" class="mini" id="indid" name="indid" value="${inrequest?.indid}" />
  <label class="auto" for="client_id">Клиент:</label>
  <g:select name="client_id" value="${inrequest?.client_id}" from="${clients}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/>
  <label class="auto" for="modstatus">Статус:</label>
  <g:select name="modstatus" value="${inrequest?.modstatus}" from="['активный','архив']" keys="10" noSelection="${['-100':'все']}"/>
  <label class="auto" for="aclass">Класс:</label>
  <g:select name="aclass" class="mini" value="${inrequest?.aclass}" from="['безналичный','наличный']" keys="12" noSelection="${['0':'все']}"/>
  <div class="fright">
    <input type="button" class="spacing reset" value="Сброс" onclick="resetindepositfilter()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${iscanedit}">
    <g:link action="indeposit" class="button">Добавить новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>