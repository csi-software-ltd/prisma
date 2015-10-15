<label for="agentkredit_client">Заемщик:</label>
<g:select name="agentkredit_client" value="${client?.id}" from="${[Company.get(kredits.first().client)]}" optionKey="id" optionValue="name" disabled="true"/>
<label for="agentkredit_kredit_id">Кредит:</label>
<g:select name="agentkredit_kredit_id" value="${agentkredit?.kredit_id}" from="${kredits}" optionKey="id" optionValue="anumber" noSelection="${['0':'не выбран']}" disabled="${agentkredit?true:false}"/>
<g:if test="${agentkredit}">
<label for="agentkredit_bank">Банк:</label>
<input type="text" class="fullline" id="agentkredit_bank" name="bank" value="${bank}" disabled/>
</g:if>
<label for="agentkredit_payterm">Условие расчета:</label>
<g:select id="agentkredit_payterm" name="payterm" value="${agentkredit?.payterm}" from="['по дням','по месяцам']" keys="[0,1]"/>
<label for="agentkredit_calcperiod">Период расчета:</label>
<g:select id="agentkredit_calcperiod" name="calcperiod" value="${agentkredit?.calcperiod}" from="['за месяц вперед','за прошлый месяц','за три месяца вперед','за текущий месяц']" keys="0123"/>
<label for="agentkredit_rate">Общий процент:</label>
<input type="text" class="auto" id="agentkredit_rate" name="rate" value="${number(value:agentkredit?.rate)}"/>
<label for="agentkredit_cost">Себестоимость:</label>
<input type="text" class="auto" id="agentkredit_cost" name="cost" value="${number(value:agentkredit?.cost)}"/>
<hr class="admin" />
<ul id="agentrateslist">
<g:each in="${agentrates}" var="agentrate">
	<li style="border-bottom: 1px solid black;width:936px;margin-bottom:10px">
		<div style="width:830px;float:left">
			<label for="agentrates_agent_id_${agentrate.id}">Агент:</label>
			<g:select name="agentrates_agent_id_${agentrate.id}" value="${agentrate?.agent_id}" from="${agents}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}" disabled="true"/>
			<label class="auto" for="agentrates_rate_${agentrate.id}">Ставка:</label>
			<input type="text" class="auto" id="agentrates_rate_${agentrate.id}" name="rate_${agentrate.id}" value="${number(value:agentrate?.rate)}"/><br/>
      <div class="fright" style="padding-right:15px"><label class="auto" for="agentrates_is_display_${agentrate.id}">
        <input type="checkbox" id="agentrates_is_display_${agentrate.id}" name="is_display_${agentrate.id}" value="1" <g:if test="${agentrate?.is_display}">checked</g:if> />
        Выводить в счет
      </label></div>
		</div>
		<div style="float:right;width:76px;height:76px;padding-top:30px">
		<g:if test="${iscanedit&&!agentacts[agentrate.id]}">
			<g:remoteLink class="button" url="${[controller:'agreement',action:'deleteagentrate',id:agentrate.id]}" title="Удалить" after="hidenode(this)"><i class="icon-trash icon-large"></i></g:remoteLink>
		</g:if>
		</div>
	</li>
</g:each>
<g:each in="${subagentrates}" var="agentrate">
	<li style="border-bottom: 1px solid black;width:936px;margin-bottom:10px">
		<div style="width:830px;float:left">
			<label for="agentrates_agent_id_${agentrate.id}">Подагент:</label>
			<g:select name="agentrates_agent_id_${agentrate.id}" value="${agentrate?.agent_id}" from="${agents}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}" disabled="true"/>
			<label class="auto" for="agentrates_rate_${agentrate.id}">Ставка:</label>
			<input type="text" class="auto" id="agentrates_rate_${agentrate.id}" name="rate_${agentrate.id}" value="${number(value:agentrate?.rate)}"/>
			<label for="agentrates_subtype_${agentrate.id}">Тип процентов:</label>
			<g:select name="agentrates_subtype_${agentrate.id}" name="subtype_${agentrate.id}" value="${agentrate?.subtype}" from="['Расчет по СС','Расчет по прибыли']" keys="01"/>
		</div>
		<div style="float:right;width:76px;height:76px;padding-top:30px">
		<g:if test="${iscanedit&&!subagentacts[agentrate.id]}">
			<g:remoteLink class="button" url="${[controller:'agreement',action:'deleteagentrate',id:agentrate.id]}" title="Удалить" after="hidenode(this)"><i class="icon-trash icon-large"></i></g:remoteLink>
		</g:if>
		</div>
	</li>
</g:each>
</ul>
<div class="fright">
<g:if test="${iscanedit&&!isHavePeriods}">
	<g:link controller="catalog" action="agent" class="button" target="_blank"><i class="icon-angle-left icon-large"></i>&nbsp;Новый агент</g:link>
<g:if test="${!isHaveSubAgents}">
  <input type="button" class="button" value="Добавить подагента" onclick="getsubagenttemplate()"/>
</g:if>
  <input type="button" class="button" value="Добавить агента" onclick="getagenttemplate()"/>
  <input type="submit" id="addagentkredit_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="newagentnumber=0;jQuery('#agentkreditAddForm').slideUp();"/>
</div>
<input type="hidden" name="agentagr_id" value="${agentagr.id}"/>
<input type="hidden" name="id" value="${agentkredit?.id?:0}"/>
<input type="hidden" id="agentkredit_newagentnumber" name="newagentnumber" value="0"/>
<div class="clear" style="padding-bottom:10px"></div>