<g:if test="${!spacecalc}">
	<label for="spacecalc_maindate">Период:</label>
	<g:datepicker class="normal nopad" name="spacecalc_maindate" depth="year" value=""/><br/>
</g:if>
<label for="spacecalc_summa">Сумма платежа:</label>
<input type="text" id="spacecalc_summa" name="summa" value="${number(value:spacecalc?.summa)}"/>
<label class="auto" for="spacecalc_is_dop" <g:if test="${spacecalc}">disabled</g:if> >
  <input type="checkbox" id="spacecalc_is_dop" name="is_dop" value="1" <g:if test="${spacecalc?.is_dop}">checked</g:if> <g:if test="${spacecalc}">disabled</g:if> />
  За доп. услуги
</label>
<br/><label for="spacecalc_schet">Номер счета:</label>
<input type="text" id="spacecalc_schet" name="schet" value="${spacecalc?.schet}"/>
<label for="spacecalc_schetdate">Дата счета:</label>
<g:datepicker class="normal nopad" name="spacecalc_schetdate" value="${spacecalc?.schetdate?String.format('%td.%<tm.%<tY',spacecalc.schetdate):''}"/>
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="spacecalcadd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#spacecalculationAddForm').slideUp();"/>
</div>
<input type="hidden" name="space_id" value="${space.id}"/>
<input type="hidden" name="id" value="${spacecalc?.id?:0}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#spacecalc_schetdate").mask("99.99.9999",{placeholder:" "});
</script>