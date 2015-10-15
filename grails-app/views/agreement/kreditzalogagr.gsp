<label for="kreditzalogagr_pledger">Залогодатель</label>
<input type="text" class="fullline" id="kreditzalogagr_pledger" name="pledger" value="${kreditzalogagr?.pledger?:client}"/>
<label for="kreditzalogagr_zalogagr">Залоговый договор</label>
<input type="text" id="kreditzalogagr_zalogagr" name="zalogagr" value="${kreditzalogagr?.zalogagr}"/>
<label for="kreditzalogagr_zalogtype_id">Тип залога:</label>
<g:select id="kreditzalogagr_zalogtype_id" name="zalogtype_id" value="${kreditzalogagr?.zalogtype_id}" from="${Zalogtype.list()}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}"/>
<label for="kreditzalogagr_zalogstart">Дата залога:</label>
<g:datepicker class="normal nopad" style="margin-right:24px" name="kreditzalogagr_zalogstart" value="${kreditzalogagr?.zalogstart?String.format('%td.%<tm.%<tY',kreditzalogagr.zalogstart):''}"/>
<label for="kreditzalogagr_zalogend">Срок залога:</label>
<g:datepicker class="normal nopad" style="margin-right:24px" name="kreditzalogagr_zalogend" value="${kreditzalogagr?.zalogend?String.format('%td.%<tm.%<tY',kreditzalogagr.zalogend):''}"/>
<label class="auto" for="kreditzalogagr_is_zalogagr">
  <input type="checkbox" id="kreditzalogagr_is_zalogagr" name="is_zalogagr" value="1" <g:if test="${kreditzalogagr?.is_zalogagr}">checked</g:if> />
  Договор
</label>
<label for="kreditzalogagr_marketcost">Рыноч. стоимость:</label>
<input type="text" id="kreditzalogagr_marketcost" name="marketcost" value="${number(value:kreditzalogagr?.marketcost)}"/>
<label for="kreditzalogagr_zalogcost">Залог. стоимость:</label>
<input type="text" id="kreditzalogagr_zalogcost" name="zalogcost" value="${number(value:kreditzalogagr?.zalogcost)}"/>
<label for="kreditzalogagr_strakhnumber">Страх. договор:</label>
<input type="text" id="kreditzalogagr_strakhnumber" name="strakhnumber" value="${kreditzalogagr?.strakhnumber}"/>
<label for="kreditzalogagr_strakhsumma">Страховая сумма:</label>
<input type="text" id="kreditzalogagr_strakhsumma" name="strakhsumma" value="${intnumber(value:kreditzalogagr?.strakhsumma)}"/>
<label for="kreditzalogagr_strakhdate">Дата договора:</label>
<g:datepicker class="normal nopad" style="margin-right:108px" name="kreditzalogagr_strakhdate" value="${kreditzalogagr?.strakhdate?String.format('%td.%<tm.%<tY',kreditzalogagr.strakhdate):''}"/>
<label for="kreditzalogagr_strakhvalidity">Срок действия:</label>
<g:datepicker class="normal nopad" name="kreditzalogagr_strakhvalidity" value="${kreditzalogagr?.strakhvalidity?String.format('%td.%<tm.%<tY',kreditzalogagr.strakhvalidity):''}"/>
<label for="kreditzalogagr_space1">Место хранения:</label>
<g:select id="kreditzalogagr_space1" name="space1" value="${kreditzalogagr?.space1}" from="${wh1}" optionValue="fulladdress" optionKey="id" noSelection="${['0':'нет']}" onchange="togglespace(this.value)"/>
<span id="kreditzalogagr_spanspace" style="${!kreditzalogagr?.space1?'display:none':''}"><label for="kreditzalogagr_space2">Место хранения:</label>
<g:select id="kreditzalogagr_space2" name="space2" value="${kreditzalogagr?.space2}" from="${wh2}" optionValue="fulladdress" optionKey="id" noSelection="${['0':'нет']}"/></span>
<br/><label for="kreditzalogagr_zalogprim">Примечание к залоговому договору:</label>
<g:textArea id="kreditzalogagr_zalogprim" name="zalogprim" value="${kreditzalogagr?.zalogprim}" />
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
<g:if test="${kreditzalogagr?.id}">
  <g:remoteLink class="button" url="${[action:'adddopzalog',id:kreditzalogagr.id]}" onSuccess="jQuery('#kreditzalogagrAddForm').slideUp(300, function() { getZalogAgrs(); });">
    Новое доп. соглашение &nbsp;<i class="icon-angle-right icon-large"></i>
  </g:remoteLink>
</g:if>
  <input type="submit" id="kreditzalogagradd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#kreditzalogagrAddForm').slideUp();"/>
</div>
<input type="hidden" name="kredit_id" value="${kredit.id}"/>
<input type="hidden" name="id" value="${kreditzalogagr?.id?:0}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#kreditzalogagr_zalogstart").mask("99.99.9999",{placeholder:" "});
  jQuery("#kreditzalogagr_zalogend").mask("99.99.9999",{placeholder:" "});
  jQuery("#kreditzalogagr_strakhdate").mask("99.99.9999",{placeholder:" "});
  jQuery("#kreditzalogagr_strakhvalidity").mask("99.99.9999",{placeholder:" "});
</script>