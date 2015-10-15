<g:select style="${!short_version?'width:628px':''}" name="agreement_id" from="${agr}" optionKey="id" noSelection="${['0':'не выбран']}"/>
<g:if test="${!short_version}">
  <span class="add-on" onclick="newAgr()"><abbr title="Добавить договор"><i class="icon-plus"></i></abbr></span> 
  <span class="add-on" onclick="selectAgreement(-1)"><abbr title="Обновить договор"><i class="icon-refresh"></i></abbr></span>
</g:if>