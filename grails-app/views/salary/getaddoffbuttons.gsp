<g:if test="${curreport?.modstatus==0&&iscanedit}">
  <g:remoteLink class="button" url="${[controller:'salary',action:'createpayrequests',id:curreport.id]}" onSuccess="\$('form_submit_button').click();updateoffbuttons('${String.format('%td.%<tm.%<tY',new Date(curreport.year-1900,curreport.month-1,1))}');">В оплату</g:remoteLink>
  <g:remoteLink class="button" url="${[controller:'salary',action:'recomputeofficial',id:curreport.id]}" onSuccess="\$('form_submit_button').click();">Пересчитать</g:remoteLink>
</g:if><g:if test="${curreport?.modstatus==1&&iscanedit}">
  <g:remoteLink class="button" url="${[controller:'salary',action:'closebuhreport',id:curreport.id]}" onSuccess="\$('form_submit_button').click();updateoffbuttons('${String.format('%td.%<tm.%<tY',new Date(curreport.year-1900,curreport.month-1,1))}');">Закрыть</g:remoteLink>
</g:if>