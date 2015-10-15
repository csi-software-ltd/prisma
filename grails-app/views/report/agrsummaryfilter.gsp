<g:form name="allForm" url="[controller:controllerName,action:'agrsummary']" target="_blank">
  <label class="auto" for="reportdate_start">Период отчета с</label>
  <g:datepicker class="normal nopad" name="reportdate_start" value=""/>
  <label class="auto" for="reportdate_end">по</label>
  <g:datepicker class="normal nopad" name="reportdate_end" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
  <label class="auto" for="reporttype">Вариант отчета</label>
  <g:select name="reporttype" value="" from="['по заказчику', 'по исполнителю']" keys="01"/>
  <label class="auto" for="agr_cname">Компания</label>
  <input type="text" id="agr_cname" name="cname" value="" />
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Сформировать" />
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('list').innerHTML='';
  new Autocomplete('agr_cname', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_int_autocomplete")}'
  });
  jQuery("#reportdate_start").mask("99.99.9999",{placeholder:" "});
  jQuery("#reportdate_end").mask("99.99.9999",{placeholder:" "});
</script>