<label for="spacedopagr_anumber">Номер соглашения:</label>
<input type="text" class="auto" id="spacedopagr_anumber" name="anumber" value="${spacedopagr?.anumber}"/>
<label for="spacedopagr_adate">Дата соглашения:</label>
<g:datepicker class="normal nopad" name="spacedopagr_adate" value="${String.format('%td.%<tm.%<tY',spacedopagr?.adate?:new Date())}"/>
<label for="spacedopagr_startdate">Дата начала:</label>
<g:datepicker class="normal nopad" name="spacedopagr_startdate" value="${String.format('%td.%<tm.%<tY',spacedopagr?.startdate?:new Date())}"/>
<label for="spacedopagr_enddate">Дата окончания:</label>
<g:datepicker class="normal nopad" name="spacedopagr_enddate" value="${spacedopagr?String.format('%td.%<tm.%<tY',spacedopagr.enddate):''}"/>
<label class="auto" for="spacedopagr_is_changeprice">
  <input type="checkbox" id="spacedopagr_is_changeprice" name="is_changeprice" value="1" <g:if test="${spacedopagr?.is_changeprice}">checked</g:if> />
  Стоимость изменена
</label>
<label for="spacedopagr_ratemeter">Цена за метр:</label>
<input type="text" class="auto" id="spacedopagr_ratemeter" name="ratemeter" value="${number(value:spacedopagr?.ratemeter)}"/>
<label for="spacedopagr_rate">Сумма платежа:</label>
<input type="text" class="auto" id="spacedopagr_rate" name="rate" value="${number(value:spacedopagr?.rate)}"/>
<label for="spacedopagr_is_addpayment">Доп. услуги:</label>
<g:select id="spacedopagr_is_addpayment" name="is_addpayment" value="${spacedopagr?.is_addpayment}" from="['Нет','Да']" keys="01" onchange="toggleDopagrRatedop(this.value)"/>
<span id="dopagrdopsection" style="${spacedopagr?.is_addpayment!=1?'display:none':''}"><label for="spacedopagr_ratedop">Доп. платеж:</label>
<input type="text" class="auto" id="spacedopagr_ratedop" name="ratedop" value="${number(value:spacedopagr?.ratedop)}"/></span>
<label for="spacedopagr_payterm">Дата платежа:</label>
<input type="text" class="auto" id="spacedopagr_payterm" name="payterm" value="${spacedopagr?.payterm}"/>
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="spacedopagradd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#spacedopagrAddForm').slideUp();"/>
</div>
<input type="hidden" name="space_id" value="${space.id}"/>
<input type="hidden" name="id" value="${spacedopagr?.id?:0}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#spacedopagr_dsdate").mask("99.99.9999",{placeholder:" "});
  jQuery("#spacedopagr_startdate").mask("99.99.9999",{placeholder:" "});
  jQuery("#spacedopagr_enddate").mask("99.99.9999",{placeholder:" "});
</script>