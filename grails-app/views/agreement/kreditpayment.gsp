<label for="kreditpayment_summa">Сумма погашения:</label>
<input type="text" class="auto" id="kreditpayment_summa" name="summa" value="${number(value:kreditpayment?.summa)}"/>
<label for="kreditpayment_summapercrub">Сумма процентов:</label>
<input type="text" class="auto" id="kreditpayment_summaperc" name="summaperc" value="${number(value:kreditpayment?.summaperc)}"/>
<g:if test="${!kredit.isRateable()}">
<label for="kreditpayment_summarub">Сумма погашения, руб:</label>
<input type="text" class="auto" id="kreditpayment_summarub" name="summarub" value="${number(value:kreditpayment?.summarub)}"/>
<label for="kreditpayment_summapercrub">Проценты, руб:</label>
<input type="text" class="auto" id="kreditpayment_summapercrub" name="summapercrub" value="${number(value:kreditpayment?.summapercrub)}"/>
</g:if>
<label for="kreditpayment_paydate">Дата платежа:</label>
<g:datepicker class="normal nopad" style="margin-right:108px" name="kreditpayment_paydate" value="${kreditpayment?String.format('%td.%<tm.%<tY',kreditpayment.paydate):''}"/>
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="kreditpaymentadd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#kreditpaymentAddForm').slideUp();"/>
</div>
<input type="hidden" name="kredit_id" value="${kredit.id}"/>
<input type="hidden" name="id" value="${kreditpayment?.id?:0}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#kreditpayment_paydate").mask("99.99.9999",{placeholder:" "});
</script>