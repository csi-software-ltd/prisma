<style type="text/css">
  select { width: 176px; }
</style>
<label class="auto" for="fmonth_month">Месяц:</label>
<g:datePicker class="auto" name="fmonth" precision="month" value="${flperiod?.fmonth?:new Date()}" relativeYears="[114-new Date().getYear()..10]" disabled="${flperiod?true:false}"/>
<label for="flperiod_qdays" style="margin-right: 16px;">Дней в месяце:</label>
<input type="text" class="auto" id="flperiod_qdays" name="qdays" value="${flperiod?.qdays}" disabled/>
<label for="flperiod_summa">Сумма:<br/><small>исходящих платежей</small></label>
<input type="text" id="flperiod_summa" name="summa" value="${number(value:flperiod?.summa)}" />
<label for="flperiod_compensation">Компенсация:</label>
<input type="text" id="flperiod_compensation" name="compensation" value="${number(value:flperiod?.compensation)}" />
<label for="flperiod_procent">Сумма процентов:</label>
<input type="text" id="flperiod_procent" name="procent" value="${number(value:flperiod?.procent)}" />
<label for="flperiod_body">Сумма погашения:</label>
<input type="text" id="flperiod_body" name="body" value="${number(value:flperiod?.body)}" />
<label for="flperiod_returnsumma">Сумма:<br/><small>возвратов комиссии</small></label>
<input type="text" id="flperiod_returnsumma" name="returnsumma" value="${number(value:flperiod?.returnsumma)}" />
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="flperiodadd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#flperiodAddForm').slideUp();"/>
</div>
<input type="hidden" name="flizing_id" value="${flizing.id}"/>
<input type="hidden" name="id" value="${flperiod?.id?:0}"/>
<div class="clear" style="padding-bottom:10px"></div>