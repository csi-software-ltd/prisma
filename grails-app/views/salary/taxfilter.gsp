<g:formRemote name="allForm" url="[controller:controllerName,action:'taxreports']" update="list">
  <label class="auto" for="company_name">Компания:</label>
  <input type="text" id="company_name" name="company_name" />
  <div id="companyname_autocomplete" class="autocomplete" style="display:none"></div>
  <label class="auto" for="tax_id">Тип налога:</label>
  <g:select class="mini" name="tax_id" from="${taxes}" optionKey="id" optionValue="shortname" noSelection="${['-100':'все']}"/>
  <label class="auto" for="paystatus">Статус:</label>
  <g:select class="mini" name="paystatus" from="${['Не распознана','Новая','В оплате','Оплачена']}" keys="${[-1,0,1,2]}" noSelection="${['-100':'все']}"/>
  <div class="fright">
  <g:if test="${iscanincert}">
    <g:remoteLink class="button" url="${[controller:'salary',action:'linktaxreport']}" onSuccess="\$('form_submit_button').click()">Привязать &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink>
    <g:remoteLink class="button" url="${[controller:'salary',action:'payalltaxreport']}" onSuccess="\$('form_submit_button').click()">В оплату &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink>
  </g:if>
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${iscanincert}">
    <g:link action="addtaxreport" class="button">Новая ведомость &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('company_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_int_autocomplete")}'
  });
</script>