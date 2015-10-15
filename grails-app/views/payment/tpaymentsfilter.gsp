<g:formRemote name="allForm" url="[controller:controllerName,action:'tpayments']" update="list">
  <label class="auto" for="company_name">Компания:</label>
  <input type="text" id="company_name" name="company_name" value="${inrequest?.company_name?:''}" />
  <div id="companyname_ext_autocomplete" class="autocomplete" style="display:none"></div>
  <label class="auto" for="paydate_start">Период платежей от</label>
  <g:datepicker class="normal nopad" name="paydate_start" value="${inrequest?.paydate_start?String.format('%td.%<tm.%<tY',inrequest.paydate_start):''}"/>
  <label class="auto" for="paydate_end">до</label>
  <g:datepicker class="normal nopad" name="paydate_end" value="${inrequest?.paydate_end?String.format('%td.%<tm.%<tY',inrequest.paydate_end):''}"/>
  <label class="auto" for="modstatus">Статус исполнения</label>
  <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['Заявки','Выполнены']" keys="12" noSelection="${['0':'все']}"/>
  <div class="fright">
    <input type="button" class="reset spacing" value="Сброс" onclick="resettfilter()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${iscanadd}">
    <g:link action="newtpayment" class="button">Добавить новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('company_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_autocomplete")}'
  });
</script>