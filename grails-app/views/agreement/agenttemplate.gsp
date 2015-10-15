	<li style="border-bottom: 1px solid black;width:936px;margin-bottom:10px">
		<div style="width:830px;float:left">
			<label for="agentrates_agent_id_new${agnumber}">Агент:</label>
			<g:select name="agentrates_agent_id_new${agnumber}" value="" from="${agents}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}"/>
			<label class="auto" for="agentrates_rate_new${agnumber}">Ставка:</label>
			<input type="text" class="auto" id="agentrates_rate_new${agnumber}" name="rate_new${agnumber}" value=""/>
      <div class="fright" style="padding-right:15px"><label class="auto" for="agentrates_is_display_new${agnumber}">
        <input type="checkbox" id="agentrates_is_display_new${agnumber}" name="is_display_new${agnumber}" value="1" />
        Выводить в счет
      </label></div>
		</div>
		<div style="float:right;width:76px;height:76px;padding-top:30px">
		<g:if test="${iscanedit}">
			<g:remoteLink class="button" url="${[controller:'agreement',action:'deleteagentrate']}" title="Удалить" after="hidenode(this)"><i class="icon-trash icon-large"></i></g:remoteLink>
		</g:if>
		</div>
	</li>