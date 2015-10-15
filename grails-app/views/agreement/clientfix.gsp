<label for="clientfix_summa">Сумма:</label>
<input type="text" id="clientfix_summa" name="summa" value="${number(value:clientfix?.summa)}"/>
<label for="clientfix_paydate">Дата проводки:</label>
<g:datepicker class="normal nopad" name="clientfix_paydate" value="${clientfix?.paydate?String.format('%td.%<tm.%<tY',clientfix.paydate):''}"/>
<label for="clientfix_destination">Назначение:</label>
<g:textArea name="destination" id="clientfix_destination" value="${clientfix?.destination}" />
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#clfixaddForm').slideUp();"/>
</div>
<input type="hidden" name="agentagr_id" value="${agentagr.id}"/>
<input type="hidden" name="id" value="${clientfix?.id}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#clientfix_paydate").mask("99.99.9999",{placeholder:" "});
</script>