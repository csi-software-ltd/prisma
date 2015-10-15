<label for="agentfix_agent_id">Агент:</label>
<g:select id="agentfix_agent_id" name="agent_id" from="${agents}" value="${agentfix?.agent_id}" optionValue="name" optionKey="id" noSelection="${['0':'не выбран']}"/>
<label for="agentfix_summa">Сумма:</label>
<input type="text" id="agentfix_summa" name="summa" value="${number(value:agentfix?.summa)}"/>
<label for="agentfix_paydate">Дата проводки:</label>
<g:datepicker class="normal nopad" name="agentfix_paydate" value="${agentfix?.paydate?String.format('%td.%<tm.%<tY',agentfix.paydate):''}"/>
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#fixaddForm').slideUp();"/>
</div>
<input type="hidden" name="agentagr_id" value="${agentagr.id}"/>
<input type="hidden" name="id" value="${agentfix?.id}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#agentfix_paydate").mask("99.99.9999",{placeholder:" "});
</script>