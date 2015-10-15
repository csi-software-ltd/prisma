<label for="bankkredit_client">Заемщик:</label>
<g:select name="bankkredit_client" value="${client?.id}" from="${clients}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}" onchange="updateclient(this.value)"/>
<label for="agentkredit_kredit_id">Договор:</label>
<g:select name="agentkredit_kredit_id" value="" from="${kredits}" optionKey="id" noSelection="${['0':'все']}"/><br/>
<label for="bankkredit_rate">Общий процент:</label>
<input type="text" class="auto" id="bankkredit_rate" name="rate" value=""/>
<label for="bankkredit_cost">Себестоимость:</label>
<input type="text" class="auto" id="bankkredit_cost" name="cost" value=""/>
<label for="bankkredit_payterm">Условие расчета:</label>
<g:select id="bankkredit_payterm" name="payterm" value="" from="['по дням','по месяцам']" keys="01"/>
<label for="bankkredit_calcperiod">Период расчета:</label>
<g:select id="bankkredit_calcperiod" name="calcperiod" value="" from="['за месяц вперед','за прошлый месяц','за три месяца вперед','за текущий месяц']" keys="0123"/>
<hr class="admin" />
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="addbankkredit_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#agentkreditAddForm').slideUp();"/>
</div>
<input type="hidden" name="agentagr_id" value="${agentagr.id}"/>
<input type="hidden" name="is_bankkredit" value="1"/>
<div class="clear" style="padding-bottom:10px"></div>