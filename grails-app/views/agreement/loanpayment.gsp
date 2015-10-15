<label for="loanpayment_summa">Сумма тела:</label>
<input type="text" class="auto" id="loanpayment_summa" name="summa" value="${number(value:loanpayment?.summa)}"/>
<label for="loanpayment_summapercrub">Сумма процентов:</label>
<input type="text" class="auto" id="loanpayment_summaperc" name="summaperc" value="${number(value:loanpayment?.summaperc)}"/>
<g:if test="${!loan.isRateable()}">
<label for="loanpayment_summarub">Сумма тела, руб:</label>
<input type="text" class="auto" id="loanpayment_summarub" name="summarub" value="${number(value:loanpayment?.summarub)}"/>
<label for="loanpayment_summapercrub">Проценты, руб:</label>
<input type="text" class="auto" id="loanpayment_summapercrub" name="summapercrub" value="${number(value:loanpayment?.summapercrub)}"/>
</g:if>
<label for="loanpayment_paydate">Дата платежа:</label>
<g:datepicker class="normal nopad" style="margin-right:108px" name="loanpayment_paydate" value="${loanpayment?String.format('%td.%<tm.%<tY',loanpayment.paydate):''}"/>
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="loanpaymentadd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#loanpaymentAddForm').slideUp();"/>
</div>
<input type="hidden" name="loan_id" value="${loan.id}"/>
<input type="hidden" name="id" value="${loanpayment?.id?:0}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#loanpayment_paydate").mask("99.99.9999",{placeholder:" "});
</script>