<g:form name="buhreportsForm" url="[controller:'salary',action:'buhreportXLS']" target="_blank">
  <label class="auto" for="repdate">Дата:</label>
  <g:select class="mini" name="repdate" value="${String.format('%td.%<tm.%<tY',new Date(curreport.year-1900,curreport.month-1,1))}" from="${buhreports}" optionKey="keyvalue" optionValue="disvalue" onchange="\$('form_submit_button').click();updatebuttons(this.value);"/>
  <label class="auto" for="company_name">Компания:</label>
  <input type="text" id="company_name" name="company_name" value="${inrequest?.company_name}"/>
  <label class="auto" for="pers_name">ФИО</label>
  <input type="text" id="pers_name" name="pers_name" value="${inrequest?.pers_name}"/>
  <label class="auto" for="perstype">Тип:</label>
  <g:select class="auto" name="perstype" value="${inrequest?.perstype}" from="${['Сотрудник','Директор','Специалист']}" keys="123" noSelection="${['-100':'все']}"/>
  <label class="auto" for="compstatus">Статус комп.:</label>
  <g:select class="auto" name="compstatus" value="${inrequest?.compstatus}" from="${['Не распознана','Привязано','Начислено']}" keys="012" noSelection="${['-100':'все']}"/>
  <label class="auto" for="perstatus">Статус сотр.:</label>
  <g:select class="auto" name="perstatus" value="${inrequest?.perstatus}" from="${['Не распознан','Привязано','Начислено']}" keys="012" noSelection="${['-100':'все']}"/>
  <label class="auto" for="is_tax">
    <input type="checkbox" id="is_tax" name="is_tax" value="1" <g:if test="${inrequest?.is_tax}">checked</g:if> />
    Налоги
  </label>
  <div class="fright">
		<span id="addbuttons">
		<g:if test="${curreport?.modstatus==0&&iscanincert}">
			<g:remoteLink class="button" url="${[controller:'salary',action:'deletebuhreport',id:curreport.id]}" onSuccess="\$('sallink2').click()">Удалить</g:remoteLink>
			<g:remoteLink class="button" url="${[controller:'salary',action:'linkbuhreport',id:curreport.id]}" onSuccess="\$('form_submit_button').click();updatebuttons('${String.format('%td.%<tm.%<tY',new Date(curreport.year-1900,curreport.month-1,1))}');">Привязать</g:remoteLink>
		<g:if test="${islinked}">
			<g:remoteLink class="button" url="${[controller:'salary',action:'accruebuhreport',id:curreport.id]}" onSuccess="\$('form_submit_button').click();updatebuttons('${String.format('%td.%<tm.%<tY',new Date(curreport.year-1900,curreport.month-1,1))}');">Подвердить</g:remoteLink>
		</g:if>
		</g:if><g:if test="${curreport?.modstatus==1&&iscanincert}">
		<g:if test="${iscanpay}">
      <g:remoteLink class="button" url="${[controller:'salary',action:'payofficial',id:curreport.id]}" onSuccess="\$('form_submit_button').click();updatebuttons('${String.format('%td.%<tm.%<tY',new Date(curreport.year-1900,curreport.month-1,1))}');">В оплату</g:remoteLink>
		</g:if>
			<g:remoteLink class="button" url="${[controller:'salary',action:'disaccruebuhreport',id:curreport.id]}" onSuccess="\$('form_submit_button').click();updatebuttons('${String.format('%td.%<tm.%<tY',new Date(curreport.year-1900,curreport.month-1,1))}');">Снять начисление</g:remoteLink>
		</g:if>
		</span>
  <g:if test="${iscanincert}">
    <g:remoteLink class="button" url="${[controller:'salary',action:'computebuhreport']}" onSuccess="processIncertbuhResponse(e)">Новая ведомость</g:remoteLink>
    <g:link action="addbuhreport" class="button">Закачать</g:link>
  </g:if>
    <input type="button" class="spacing" value="XLS" onclick="$('buhreportsForm').submit()"/>
    <input type="button" class="reset spacing" value="Сброс" onclick="resetbuhfilter()"/>
    <input style="display:none" type="submit" class="spacing" value="Показать" onclick="$('form_submit_button').click();return false"/>
    <g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'buhreports']" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('company_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"buhcompanyname_autocomplete")}'
  });
  new Autocomplete('pers_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"buhpersname_autocomplete")}'
  });
</script>