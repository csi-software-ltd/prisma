<label for="kreditdopagr_nomer">Номер соглашения:</label>
<input type="text" class="auto" id="kreditdopagr_nomer" name="nomer" value="${kreditdopagr?.nomer}"/>
<label for="kreditdopagr_dsdate">Дата соглашения:</label>
<g:datepicker class="normal nopad" name="kreditdopagr_dsdate" value="${kreditdopagr?String.format('%td.%<tm.%<tY',kreditdopagr.dsdate):''}"/><br/>
<label for="kreditdopagr_startdate">Дата начала:</label>
<g:datepicker class="normal nopad" style="margin-right:24px" name="kreditdopagr_startdate" value="${kreditdopagr?String.format('%td.%<tm.%<tY',kreditdopagr.startdate):''}"/>
<label for="kreditdopagr_enddate">Дата окончания:</label>
<g:datepicker class="normal nopad" style="margin-right:24px" name="kreditdopagr_enddate" value="${kreditdopagr?String.format('%td.%<tm.%<tY',kreditdopagr.enddate):''}"/>
<label class="auto" for="kreditdopagr_is_prolong">
  <input type="checkbox" id="kreditdopagr_is_prolong" name="is_prolong" value="1" <g:if test="${kreditdopagr?.is_prolong}">checked</g:if> />
  Пролонгация
</label>
<label for="kreditdopagr_summa">Сумма:</label>
<input type="text" class="auto" id="kreditdopagr_summa" name="summa" value="${intnumber(value:kreditdopagr?.summa)}"/>
<label for="kreditdopagr_rate">Ставка:</label>
<input type="text" class="auto" id="kreditdopagr_rate" name="rate" value="${number(value:kreditdopagr?.rate)}"/>
<label for="kreditdopagr_comment">Комментарий:</label>
<g:textArea id="kreditdopagr_comment" name="comment" value="${kreditdopagr?.comment}" />
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="kreditdopagradd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#kreditdopagrAddForm').slideUp();"/>
</div>
<input type="hidden" name="kredit_id" value="${kredit.id}"/>
<input type="hidden" name="id" value="${kreditdopagr?.id?:0}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#kreditdopagr_dsdate").mask("99.99.9999",{placeholder:" "});
  jQuery("#kreditdopagr_startdate").mask("99.99.9999",{placeholder:" "});
  jQuery("#kreditdopagr_enddate").mask("99.99.9999",{placeholder:" "});
</script>