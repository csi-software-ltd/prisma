<label for="agentrateperiod_calcrate">Общий процент по периоду:</label>
<input type="text" class="mini" id="agentrateperiod_calcrate" name="calcrate" value="${number(value:agentrateperiod.calcrate)}"/>
<label for="agentrateperiod_calccost">Себестоимость по периоду:</label>
<input type="text" class="mini" id="agentrateperiod_calccost" name="calccost" value="${number(value:agentrateperiod.calccost)}"/>
<label class="auto" for="agentrateperiod_vrate">Курс:</label>
<input type="text" class="mini" id="agentrateperiod_vrate" name="vrate" value="${number(value:agentrateperiod.vrate)}" />
<hr class="admin" />
<ul id="agentrateslist">
<g:each in="${agentrates}" var="agentrate">
	<li style="border-bottom: 1px solid black;width:936px;margin-bottom:10px">
		<div style="width:830px;float:left">
			<label for="agentrates_agent_id_${agentrate.id}">Агент:</label>
			<g:select name="agentrates_agent_id_${agentrate.id}" value="${agentrate?.agent_id}" from="${agents}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}" disabled="true"/>
			<label class="auto" for="agentrates_rate_${agentrate.id}">Ставка:</label>
			<input type="text" class="auto" id="agentrates_rate_${agentrate.id}" name="rate_${agentrate.id}" value="${number(value:agentrate?.rate)}"/>
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
			<g:select name="agentrates_subtype_${agentrate.id}" name="subtype_${agentrate.id}" value="${agentrate?.subtype}" from="['Расчет по СС','Расчет по прибыли']" keys="01" disabled="true"/>
		</div>
	</li>
</g:each>
</ul>
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#agentrateperiodUpdateForm').slideUp();"/>
</div>
<input type="hidden" name="agentagr_id" value="${agentagr.id}"/>
<input type="hidden" name="id" value="${agentrateperiod.id}"/>
<div class="clear" style="padding-bottom:10px"></div>
<hr class="admin" />