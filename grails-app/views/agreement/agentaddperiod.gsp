<label for="agentaddperiod_period_id">Период:</label>
<g:select class="fullline" id="agentaddperiod_period_id" name="period_id" from="${periods}" optionKey="id" noSelection="${['0':'не выбран']}"/>
<label for="agentaddperiod_calcrate">Общий процент по периоду:</label>
<input type="text" id="agentaddperiod_calcrate" name="calcrate" value="" />
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#addperiodUpdateForm').slideUp();"/>
</div>
<input type="hidden" name="agentagr_id" value="${agentagr.id}"/>
<div class="clear" style="padding-bottom:10px"></div>
<hr class="admin" />