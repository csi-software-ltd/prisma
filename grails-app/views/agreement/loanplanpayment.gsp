<label for="planpayment_summa">Сумма платежа:</label>
<input type="text" class="auto" id="planpayment_summa" name="summa" value=""/>
<label for="planpayment_paydate">Дата платежа:</label>
<g:datepicker class="normal nopad" name="planpayment_paydate" min="${String.format('%td.%<tm.%<tY',loan.startdate)}" max="${String.format('%td.%<tm.%<tY',loan.enddate)}" value=""/>
<g:if test="${!loan.isRateable()}">
<label for="planpayment_summarub">Сумма, руб:</label>
<input type="text" class="auto" id="planpayment_summarub" name="summarub" value=""/>
</g:if>
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="planpaymentadd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#planpaymentAddForm').slideUp();"/>
</div>
<input type="hidden" name="loan_id" value="${loan.id}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#planpayment_paydate").mask("99.99.9999",{placeholder:" "});
</script>