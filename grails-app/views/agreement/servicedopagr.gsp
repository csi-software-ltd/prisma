<label for="servicedopagr_nomer">Номер соглашения:</label>
<input type="text" id="servicedopagr_nomer" name="nomer" value="${servicedopagr?.nomer}"/>
<label for="servicedopagr_dsdate">Дата соглашения:</label>
<g:datepicker class="normal nopad" name="servicedopagr_dsdate" value="${servicedopagr?String.format('%td.%<tm.%<tY',servicedopagr.dsdate):''}"/><br/>
<label for="servicedopagr_startdate">Дата начала:</label>
<g:datepicker class="normal nopad" style="margin-right:108px" name="servicedopagr_startdate" value="${servicedopagr?String.format('%td.%<tm.%<tY',servicedopagr.startdate):''}"/>
<label for="servicedopagr_enddate">Дата окончания:</label>
<g:datepicker class="normal nopad" name="servicedopagr_enddate" value="${servicedopagr?String.format('%td.%<tm.%<tY',servicedopagr.enddate):''}"/>
<label for="servicedopagr_summa">Сумма:</label>
<input type="text" id="servicedopagr_summa" name="summa" value="${intnumber(value:servicedopagr?.summa)}"/>
<br/><label for="servicedopagr_comment">Комментарий:</label>
<g:textArea id="servicedopagr_comment" name="comment" value="${servicedopagr?.comment}" />
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="servicedopagradd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#servicedopagrAddForm').slideUp();"/>
</div>
<input type="hidden" name="service_id" value="${service.id}"/>
<input type="hidden" name="id" value="${servicedopagr?.id?:0}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#servicedopagr_dsdate").mask("99.99.9999",{placeholder:" "});
  jQuery("#servicedopagr_startdate").mask("99.99.9999",{placeholder:" "});
  jQuery("#servicedopagr_enddate").mask("99.99.9999",{placeholder:" "});
</script>