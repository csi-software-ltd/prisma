<label for="agentpayment_summa">Сумма к выплате:</label>
<input type="text" id="agentpayment_summa" name="summa" value="${number(value:agentpayment.summa-agentpayment.agentcommission)}"/>
<div class="clear"></div>
<div class="fright">
  <input type="submit" class="button" value="Списать" />
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#agentpaymentupdateForm').slideUp();"/>
</div>
<input type="hidden" name="id" value="${payrequest.id}"/>
<input type="hidden" name="agentpayment_id" value="${agentpayment.id}"/>
<div class="clear" style="padding-bottom:10px"></div>