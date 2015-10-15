<label for="payrequest_summa">Сумма:</label>
<input type="text" class="auto" id="payrequest_summa" name="summa" value="${number(value:payrequest?.summa)}"/>
<label for="payrequest_summands">Сумма НДС:</label>
<input type="text" class="auto" id="payrequest_summands" name="summands" value="${number(value:payrequest?.summands)}"/>
<label for="payrequest_paydate">Дата платежа:</label>
<g:datepicker class="normal nopad" style="margin-right:108px" name="payrequest_paydate" value="${String.format('%td.%<tm.%<tY',payrequest?.paydate?:new Date())}"/><br/>
<label for="payrequest_destination">Назначение:</label>
<g:textArea id="payrequest_destination" name="destination" value="${payrequest?.destination}" />
<div class="clear"></div>
<div class="fright">
  <input type="submit" id="payrequestadd_submit_button" class="button" value="Сохранить" />
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#payrequestAddForm').slideUp();"/>
</div>
<input type="hidden" name="agr_id" value="${agr.id}"/>
<input type="hidden" name="id" value="${payrequest?.id?:0}"/>
<div class="clear" style="padding-bottom:10px"></div>