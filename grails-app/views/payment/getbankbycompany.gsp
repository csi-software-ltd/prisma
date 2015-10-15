<g:if test="${!type}">
	<g:select class="nopad normal" name="tobank" value="${bank.size()==1?bank[0].id:''}" from="${bank}" optionKey="id" optionValue="name" noSelection="${['':'не выбран']}" style="width:630px"/> 
	<g:if test="${!cut_version}">
	  <span class="add-on" onclick="openCompany()"><abbr title="Добавить банк получателя"><i class="icon-plus"></i></abbr></span> 
	  <span class="add-on" onclick="getBankByCompany()"><abbr title="Обновить банк получателя"><i class="icon-refresh"></i></abbr></span>
	</g:if>
</g:if><g:else>
	<g:select class="nopad normal" name="frombank" value="${bank.size()==1?bank[0].id:''}" from="${bank}" optionKey="id" optionValue="name" noSelection="${['':'не выбран']}" style="width:630px"/> 
	<g:if test="${!cut_version}">
	  <span class="add-on" onclick="openCompany('from')"><abbr title="Добавить банк плательщика"><i class="icon-plus"></i></abbr></span> 
	  <span class="add-on" onclick="getBankByCompany('from')"><abbr title="Обновить банк плательщика"><i class="icon-refresh"></i></abbr></span>
	</g:if>
</g:else>