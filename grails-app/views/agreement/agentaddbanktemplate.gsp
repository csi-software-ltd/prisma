	<li style="width:905px;">
		<div style="width:830px;float:left;">
      <label for="addbank_id_new${banknumber}">Доп. банк:</label>
      <g:select style="width:600px" name="addbank_id_new${banknumber}" from="${banks}" value="" optionValue="name" optionKey="id" noSelection="${['':'не выбран']}"/>
		</div>
		<div style="float:right;width:75px;height:51px;">
		<g:if test="${iscanedit}">
			<g:remoteLink class="button" url="${[controller:'agreement',action:'deleteaddbank']}" title="Удалить" after="hidenode(this)"><i class="icon-trash icon-large"></i></g:remoteLink>
		</g:if>
		</div>
	</li>