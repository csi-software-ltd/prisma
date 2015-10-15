<label for="repayment_summa">Сумма к списанию:</label>
<input type="text" id="repayment_summa" name="summa" value="${number(value:repayment.summa-repayment.clientcommission)}"/>
<div class="clear"></div>
<div class="fright">
  <input type="submit" class="button" value="Списать" />
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#repaymentupdateForm').slideUp();"/>
</div>
<input type="hidden" name="id" value="${payrequest.id}"/>
<input type="hidden" name="repayment_id" value="${repayment.id}"/>
<div class="clear" style="padding-bottom:10px"></div>