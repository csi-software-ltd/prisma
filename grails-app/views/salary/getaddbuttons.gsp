<g:if test="${curreport?.modstatus==0&&iscanincert}">
  <g:remoteLink class="button" url="${[controller:'salary',action:'deletebuhreport',id:curreport.id]}" onSuccess="location.reload(true)">Удалить</g:remoteLink>
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