<table class="list" width="920" cellpadding="0" cellspacing="0" border="0">
  <tbody>
  <g:each in="${acts}" status="i" var="record">
    <tr class="detailed" align="center">
      <td>${String.format('%tB %<tY',new Date(record.year-1900,record.month-1,1))}</td>
      <td>${String.format('%td.%<tm.%<tY',record.inputdate)}</td>
      <td>${number(value:record.summa+record.summafix)}</td>
      <td>${number(value:paysummas[record.id].agentfix_summa)}</td>
      <td>${number(value:paysummas[record.id].agentpay_summa)}</td>
      <td>${number(value:record.summa+record.summafix-record.actpaid)}</td>
      <td width="80">${record.modstatus?'согласован':'новый'}</td>
      <td valign="middle" width="70">
      <g:if test="${record.modstatus==0&&iscanedit}">
        <g:remoteLink class="button" style="z-index:1" url="${[controller:'agreement',action:'agreeagentact',id:agentagr.id,params:[act_id:record.id,status:1]]}" title="Согласовать" onSuccess="jQuery('tr.current').click();"><i class="icon-chevron-sign-right"></i></g:remoteLink>&nbsp;&nbsp;&nbsp;
        <g:remoteLink class="button" style="z-index:1" url="${[controller:'agreement',action:'deleteagentact',id:agentagr.id,params:[act_id:record.id]]}" title="Удалить" onSuccess="jQuery('tr.current').click();"><i class="icon-trash"></i></g:remoteLink>&nbsp;&nbsp;&nbsp;
      </g:if><g:elseif test="${record.modstatus==1&&iscanedit}">
        <g:remoteLink class="button" style="z-index:1" url="${[controller:'agreement',action:'agreeagentact',id:agentagr.id,params:[act_id:record.id,status:0]]}" title="Отменить согласование" onSuccess="jQuery('tr.current').click();"><i class="icon-repeat"></i></g:remoteLink>&nbsp;&nbsp;&nbsp;
      </g:elseif>
        <a class="button" href="${g.createLink(action:'printagentact',id:agentagr.id,params:[act_id:record.id])}" title="Распечатать" target="_blank"><i class="icon-print"></i></a>
      </td>
    </tr>
  </g:each>
  <g:if test="${!acts}">
    <tr>
      <td colspan="8" class="btns" style="text-align:center">
        <a class="button" href="javascript:void(0)">Расчетов не найдено</a>
      </td>
    </tr>
  </g:if>
  </tbody>
</table>