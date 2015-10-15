<label for="finlizingdopagr_flpoluchatel">Лизингополучатель</label>
<input type="text" class="fullline" id="finlizingdopagr_flpoluchatel" name="flpoluchatel" value="${flpoluchatel}"/>
<label for="finlizingdopagr_nomer">Номер соглашения:</label>
<input type="text" id="finlizingdopagr_nomer" name="nomer" value="${finlizingdopagr?.nomer}"/>
<label for="finlizingdopagr_dsdate">Дата соглашения:</label>
<g:datepicker class="normal nopad" name="finlizingdopagr_dsdate" value="${finlizingdopagr?String.format('%td.%<tm.%<tY',finlizingdopagr.dsdate):''}"/><br/>
<label for="finlizingdopagr_startdate">Дата начала:</label>
<g:datepicker class="normal nopad" style="margin-right:108px" name="finlizingdopagr_startdate" value="${finlizingdopagr?String.format('%td.%<tm.%<tY',finlizingdopagr.startdate):''}"/>
<label for="finlizingdopagr_enddate">Дата окончания:</label>
<g:datepicker class="normal nopad" name="finlizingdopagr_enddate" value="${finlizingdopagr?String.format('%td.%<tm.%<tY',finlizingdopagr.enddate):''}"/>
<label for="finlizingdopagr_summa">Сумма:</label>
<input type="text" id="finlizingdopagr_summa" name="summa" value="${number(value:finlizingdopagr?.summa)}"/>
<label for="finlizingdopagr_rate">Ставка:</label>
<input type="text" id="finlizingdopagr_rate" name="rate" value="${number(value:finlizingdopagr?.rate)}"/>
<br/><label for="finlizingdopagr_comment">Комментарий:</label>
<g:textArea id="finlizingdopagr_comment" name="comment" value="${finlizingdopagr?.comment}" />
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="finlizingdopagradd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#finlizingdopagrAddForm').slideUp();"/>
</div>
<input type="hidden" name="flizing_id" value="${flizing.id}"/>
<input type="hidden" name="id" value="${finlizingdopagr?.id?:0}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#finlizingdopagr_dsdate").mask("99.99.9999",{placeholder:" "});
  jQuery("#finlizingdopagr_startdate").mask("99.99.9999",{placeholder:" "});
  jQuery("#finlizingdopagr_enddate").mask("99.99.9999",{placeholder:" "});
  new Autocomplete('finlizingdopagr_flpoluchatel', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_int_autocomplete")}'
  });
</script>