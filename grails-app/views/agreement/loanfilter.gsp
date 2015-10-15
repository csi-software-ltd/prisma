<g:formRemote name="allForm" url="[controller:controllerName,action:'loans']" update="list">
  <label class="auto" for="lender_name">Займодавец:</label>
  <input type="text" id="lender_name" style="width:210px" name="lender_name" value="${inrequest?.lender_name?:''}" />
  <div id="ecompanyname_autocomplete" class="autocomplete" style="display:none"></div>
  <label class="auto" for="client_name">Заемщик:</label>
  <input type="text" id="client_name" style="width:210px" name="client_name" value="${inrequest?.client_name?:''}" />
  <div id="zcompanyname_autocomplete" class="autocomplete" style="display:none"></div>
  <label class="auto" for="modstatus">Статус</label>
  <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['Активные','Архив']" keys="10"/>
  <label class="auto" for="loantype">Тип займа:</label>
  <g:select name="loantype" value="${inrequest?.loantype}" from="['Заем у внешней','Выдача внешней','Внутренний займ','Займ учредителя','Займ работнику']" keys="12345" noSelection="${['-100':'все']}"/>
  <div class="fright">
    <input type="button" class="spacing reset" value="Сброс" onclick="resetloanfilter()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${iscanedit}">
    <g:link action="loan" class="button">Добавить новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('client_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"loanclient_autocomplete")}'
  });
  new Autocomplete('lender_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"loanlender_autocomplete")}'
  });
</script>