<label for="bank_id">Банк:</label>
<g:select class="fullline" name="bank_id" from="${banks}" value="${selectedBank}" optionValue="name" optionKey="id" noSelection="${['':'не выбран']}" onchange="getCurBankList(this.value)"/>
<g:if test="${valutas.find{it!=857}}">
<br/><label for="accounttype">Тип запроса:</label>
<g:select name="accounttype" from="['по расчетному счету','по всем счетам','по валютным счетам']" keys="012" onchange="toggleValuta(this.value)"/>
<span id="valutadetail" style="display:none"><label for="valuta_id">Валюта:</label>
<g:select name="valuta_id" from="${valutas}" optionValue="${{Valuta.get(it).name}}" noSelection="${['0':'не выбрана']}"/></span>
</g:if>