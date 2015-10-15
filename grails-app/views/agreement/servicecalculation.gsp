<label for="servcalc_summa">Сумма платежа:</label>
<input type="text" id="servcalc_summa" name="summa" value="${number(value:servcalc.summa)}"/>
<br/><label for="servcalc_schet">Номер счета:</label>
<input type="text" id="servcalc_schet" name="schet" value="${servcalc.schet}"/>
<label for="servcalc_schetdate">Дата счета:</label>
<g:datepicker class="normal nopad" name="servcalc_schetdate" value="${servcalc.schetdate?String.format('%td.%<tm.%<tY',servcalc.schetdate):''}"/>
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="servcalcadd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#servicecalculationAddForm').slideUp();"/>
</div>
<input type="hidden" name="service_id" value="${service.id}"/>
<input type="hidden" name="id" value="${servcalc?.id?:0}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#servcalc_schetdate").mask("99.99.9999",{placeholder:" "});
</script>