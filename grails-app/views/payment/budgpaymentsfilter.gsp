<style type="text/css">
  select { width: auto; }
</style>
<g:formRemote name="payrequestForm" url="[controller:'payment',action:'budgpayments']" update="list">
  <label for="companyname">Компания:</label>
  <input type="text" id="companyname" name="companyname" value="${inrequest?.companyname}"/>
  <div id="fromcompany_autocomplete" class="autocomplete" style="display:none"></div>
  <label for="paydate">Дата платежа:</label>
  <g:datepicker class="normal nopad" name="paydate" value="${inrequest?.paydate?String.format('%td.%<tm.%<tY',inrequest.paydate):''}"/>
  <label class="auto" for="modstatus">Статус:</label>
  <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['новый', 'в задании', 'выполнен', 'подтвержден']" keys="${0..3}" noSelection="${['-100':'все']}"/>
  <label class="auto" for="platperiod_month">Период:</label>
  <g:datePicker class="auto" name="platperiod" precision="month" value="${inrequest?.platperiod?:new Date()}" relativeYears="[114-new Date().getYear()..0]"/>
  <div class="fright">
    <input type="button" class="reset spacing" value="Сброс" onclick="resetbudgfilter()"/>
    <input type="submit" id="form_submit_button" value="Показать"/>
    <g:if test="${iscanadd}">
      <g:link action="newbudgrequest" class="button">Новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
    </g:if>
  </div>
</g:formRemote>
<div class="clear"></div>
<script type="text/javascript">
  new Autocomplete('companyname', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_autocomplete")}'
  });
  jQuery("#paydate").mask("99.99.9999",{placeholder:" "});
  $('form_submit_button').click();
</script>