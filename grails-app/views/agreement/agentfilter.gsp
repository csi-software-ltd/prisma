<g:formRemote name="allForm" url="[controller:controllerName,action:'agentagrs']" update="list">
  <label for="client_id" class="auto">Клиент:</label>
  <g:select name="client_id" value="${inrequest?.client_id}" from="${Client.list()}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/>
  <label class="auto" for="anumber">Кредитный договор (номер):</label>
  <input type="text" id="anumber" name="anumber" value="${inrequest?.anumber?:''}" />
  <div id="kreditnumber_autocomplete" class="autocomplete" style="display:none"></div>
  <label for="bankname" style="min-width:65px">Банк:</label>
  <input type="text" class="fullline" id="bankname" name="bankname" value="${inrequest?.bankname?:''}" />
  <br/><label class="auto" for="modstatus">Статус:</label>
  <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['Активные','Архив']" keys="[1,0]" />
  <div class="fright">
    <input type="button" class="spacing reset" value="Сброс" onclick="resetagentfilter()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${iscanedit}">
    <g:link action="agent" class="button">Добавить новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('bankname', {
    serviceUrl:'${resource(dir:"autocomplete",file:"bankname_autocomplete")}'
  });
  new Autocomplete('anumber', {
    serviceUrl:'${resource(dir:"autocomplete",file:"kreditnumber_autocomplete")}'
  });
</script>
