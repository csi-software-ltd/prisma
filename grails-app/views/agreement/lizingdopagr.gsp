<label for="lizingdopagr_nomer">Номер соглашения:</label>
<input type="text" id="lizingdopagr_nomer" name="nomer" value="${lizingdopagr?.nomer}"/>
<label for="lizingdopagr_dsdate">Дата соглашения:</label>
<g:datepicker class="normal nopad" name="lizingdopagr_dsdate" value="${lizingdopagr?String.format('%td.%<tm.%<tY',lizingdopagr.dsdate):''}"/><br/>
<label for="lizingdopagr_startdate">Дата начала:</label>
<g:datepicker class="normal nopad" style="margin-right:108px" name="lizingdopagr_startdate" value="${lizingdopagr?String.format('%td.%<tm.%<tY',lizingdopagr.startdate):''}"/>
<label for="lizingdopagr_enddate">Дата окончания:</label>
<g:datepicker class="normal nopad" name="lizingdopagr_enddate" value="${lizingdopagr?String.format('%td.%<tm.%<tY',lizingdopagr.enddate):''}"/>
<label for="lizingdopagr_summa">Сумма:</label>
<input type="text" id="lizingdopagr_summa" name="summa" value="${number(value:lizingdopagr?.summa)}"/>
<br/><label for="lizingdopagr_comment">Комментарий:</label>
<g:textArea id="lizingdopagr_comment" name="comment" value="${lizingdopagr?.comment}" />
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="lizingdopagradd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#lizingdopagrAddForm').slideUp();"/>
</div>
<input type="hidden" name="lizing_id" value="${lizing.id}"/>
<input type="hidden" name="id" value="${lizingdopagr?.id?:0}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#lizingdopagr_dsdate").mask("99.99.9999",{placeholder:" "});
  jQuery("#lizingdopagr_startdate").mask("99.99.9999",{placeholder:" "});
  jQuery("#lizingdopagr_enddate").mask("99.99.9999",{placeholder:" "});
</script>