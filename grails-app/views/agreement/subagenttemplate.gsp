	<li style="border-bottom: 1px solid black;width:936px;margin-bottom:10px">
		<div style="width:830px;float:left">
			<label for="agentrates_agent_id_new${agnumber}">Подагент:</label>
			<g:select name="agentrates_agent_id_new${agnumber}" value="" from="${agents}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}"/>
			<label class="auto" for="agentrates_rate_new${agnumber}">Ставка:</label>
			<input type="text" class="auto" id="agentrates_rate_new${agnumber}" name="rate_new${agnumber}" value=""/>
			<label for="agentrates_subtype_new${agnumber}">Тип процентов:</label>
			<g:select name="agentrates_subtype_new${agnumber}" name="subtype_new${agnumber}" value="" from="['Расчет по СС','Расчет по прибыли']" keys="01"/>
			<input type="hidden" id="agentrates_is_sub_new${agnumber}" name="is_sub_new${agnumber}" value="1"/>
		</div>
		<div style="float:right;width:76px;height:76px;padding-top:30px">
		<g:if test="${iscanedit}">
			<g:remoteLink class="button" url="${[controller:'agreement',action:'deleteagentrate']}" title="Удалить" after="hidenode(this)"><i class="icon-trash icon-large"></i></g:remoteLink>
		</g:if>
		</div>
	</li>