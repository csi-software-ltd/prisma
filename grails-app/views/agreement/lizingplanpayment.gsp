<label for="planpayment_summa">Сумма платежа:</label>
<input type="text" class="auto" id="planpayment_summa" name="summa" value="${number(value:lizingplanpayment?.summa)}"/>
<label for="planpayment_paydate">Дата платежа:</label>
<g:datepicker class="normal nopad" name="planpayment_paydate" value="${lizingplanpayment?String.format('%td.%<tm.%<tY',lizingplanpayment.paydate):''}"/>
<label for="planpayment_modstatus">Статус:</label>
<g:select id="planpayment_modstatus" name="modstatus" value="${lizingplanpayment?.modstatus}" from="['Планируемый','В оплате','Оплачен']" keys="012"/>
<label class="auto" for="planpayment_is_insurance">
  <input type="checkbox" id="planpayment_is_insurance" name="is_insurance" value="1" <g:if test="${lizingplanpayment?.is_insurance}">checked</g:if> />
  Страховка
</label>
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="planpaymentadd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#planpaymentAddForm').slideUp();"/>
</div>
<input type="hidden" name="lizing_id" value="${lizing.id}"/>
<input type="hidden" name="id" value="${lizingplanpayment?.id?:0}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#planpayment_paydate").mask("99.99.9999",{placeholder:" "});
</script>