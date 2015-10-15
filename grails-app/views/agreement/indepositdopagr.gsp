<label for="indepositdopagr_nomer">Номер соглашения:</label>
<input type="text" id="indepositdopagr_nomer" name="nomer" value="${indepositdopagr?.nomer}"/>
<label for="indepositdopagr_dsdate">Дата соглашения:</label>
<g:datepicker class="normal nopad" name="indepositdopagr_dsdate" value="${indepositdopagr?String.format('%td.%<tm.%<tY',indepositdopagr.dsdate):''}"/><br/>
<label for="indepositdopagr_atype">Тип договора:</label>
<g:select id="indepositdopagr_atype" name="atype" value="${indepositdopagr?.atype}" from="['Бессрочный','Срочный']" keys="01"/><br/>
<label for="indepositdopagr_startdate">Дата начала:</label>
<g:datepicker class="normal nopad" style="margin-right:108px" name="indepositdopagr_startdate" value="${indepositdopagr?String.format('%td.%<tm.%<tY',indepositdopagr.startdate):''}"/>
<label for="indepositdopagr_enddate">Дата окончания:</label>
<g:datepicker class="normal nopad" name="indepositdopagr_enddate" value="${indepositdopagr?.enddate?String.format('%td.%<tm.%<tY',indepositdopagr.enddate):''}"/>
<label for="indepositdopagr_summa">Сумма:</label>
<input type="text" id="indepositdopagr_summa" name="summa" value="${number(value:indepositdopagr?.summa)}"/>
<label for="indepositdopagr_rate">Ставка:</label>
<input type="text" id="indepositdopagr_rate" name="rate" value="${number(value:indepositdopagr?.rate?:24.0g)}"/>
<label for="indepositdopagr_comrate">Процент комиссии:</label>
<input type="text" id="indepositdopagr_comrate" name="comrate" value="${number(value:indepositdopagr?.comrate?:5.0g)}"/>
<br/><label for="indepositdopagr_comment">Комментарий:</label>
<g:textArea id="indepositdopagr_comment" name="comment" value="${indepositdopagr?.comment}" />
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="indepositdopagradd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#indepositdopagrAddForm').slideUp();"/>
</div>
<input type="hidden" name="deposit_id" value="${deposit.id}"/>
<input type="hidden" name="id" value="${indepositdopagr?.id?:0}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#indepositdopagr_dsdate").mask("99.99.9999",{placeholder:" "});
  jQuery("#indepositdopagr_startdate").mask("99.99.9999",{placeholder:" "});
  jQuery("#indepositdopagr_enddate").mask("99.99.9999",{placeholder:" "});
</script>