<label for="planpayment_summa">Сумма платежа:</label>
<input type="text" class="auto" id="planpayment_summa" name="summa" value=""/>
<label for="planpayment_paydate">Дата платежа:</label>
<g:datepicker class="normal nopad" name="planpayment_paydate" value=""/>
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="planpaymentadd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#planpaymentAddForm').slideUp();"/>
</div>
<input type="hidden" name="license_id" value="${license.id}"/>
<div class="clear" style="padding-bottom:10px"></div>