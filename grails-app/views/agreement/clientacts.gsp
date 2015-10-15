<style type="text/css">
  tr.yellow > td { background:lightyellow !important }
  tr.green > td { background:lightcyan !important }
</style>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Период сверки</th>
          <th>Дата сверки</th>
          <th>Сумма комиссии по<br/>периоду сверки</th>
          <th>Сумма корректировки<br/>расчетов предыдущих периодов</th>
          <th>Задол-женность по<br/>пред. периодам</th>
          <th>Сумма клиентских платежей за месяц сверки</th>
          <th>Сумма корр. агентов за месяц сверки</th>
          <th>Сумма к погашению<br/>за период сверки</th>
          <th>Сумма к списанию<br/>за период сверки</th>
          <th>Статус</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${acts}" status="i" var="record">
        <tr align="center" class="${record.isnotpaid()?'yellow':'green'}">
          <td width="100">${String.format('%tB %<tY',new Date(record.year-1900,record.month-1,1))}</td>
          <td>${String.format('%td.%<tm.%<tY',record.inputdate)}</td>
          <td>${number(value:record.summa)}</td>
          <td>${number(value:record.summafix)}</td>
          <td>${number(value:record.summaprev-record.paid-record.agentfix)}</td>
          <td>${number(value:paysummas[record.id].clientpay_summa)}</td>
          <td>${number(value:paysummas[record.id].agentfix_summa)}</td>
          <td>${number(value:record.summa+record.summafix-record.actpaid)}</td>
          <td>${number(value:record.summa+record.summafix-record.actfixes)}</td>
          <td>${record.modstatus?'согласован':'новый'}</td>
          <td valign="middle" width="120">
          <g:if test="${record.isnotfixes()&&iscanedit}">
            <g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'genclientfix',id:agentagr.id,params:[act_id:record.id]]}" title="Сформировать списание" onSuccess="getClientActs()"><i class="icon-cog"></i></g:remoteLink>&nbsp;&nbsp;
          </g:if>
          <g:if test="${record.modstatus==0&&iscanedit}">
            <g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'deleteclientact',id:agentagr.id,params:[act_id:record.id]]}" title="Удалить" onSuccess="getClientActs()"><i class="icon-trash"></i></g:remoteLink>&nbsp;&nbsp;
            <g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'agreeclientact',id:agentagr.id,params:[act_id:record.id,status:1]]}" title="Согласовать" onSuccess="getClientActs()"><i class="icon-chevron-sign-right"></i></g:remoteLink>&nbsp;&nbsp;
          </g:if><g:elseif test="${record.modstatus==1&&iscanedit}">
            <g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'agreeclientact',id:agentagr.id,params:[act_id:record.id,status:0]]}" title="Отменить согласование" onSuccess="getClientActs()"><i class="icon-repeat"></i></g:remoteLink>&nbsp;&nbsp;
          </g:elseif>
            <a class="button" href="${g.createLink(action:'printclientact',id:agentagr.id,params:[act_id:record.id])}" title="Распечатать счет" target="_blank"><i class="icon-print"></i></a>&nbsp;&nbsp;
            <a class="button" href="${g.createLink(action:'printdetailedclientact',id:agentagr.id,params:[act_id:record.id])}" title="Распечатать детали" target="_blank"><i class="icon-list"></i></a>
          </td>
        </tr>
      </g:each>
      <g:if test="${!acts}">
        <tr>
          <td colspan="11" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Актов не найдено</a>
          </td>
        </tr>
      </g:if><g:else>
        <tr>
          <td colspan="11" class="btns" style="text-align:center">
            <g:link class="button" url="${[controller:controllerName,action:'clientactsXLS',id:agentagr.id]}" target="_blank">
              Экспорт в Excell &nbsp;<i class="icon-angle-right icon-large"></i>
            </g:link>
          </td>
        </tr>
      </g:else>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="11" class="btns" style="text-align:center">
            <g:remoteLink class="button" url="${[controller:controllerName,action:'computeclientact',id:agentagr.id]}" onSuccess="getClientActs()">
              Сформировать новые акты &nbsp;<i class="icon-angle-right icon-large"></i>
            </g:remoteLink>
          </td>
        </tr>
        <tr>
          <td colspan="11" class="btns" style="text-align:center">
            <g:remoteLink class="button" url="${[controller:controllerName,action:'computeclientactpaid',id:agentagr.id]}" onSuccess="getClientActs()">
              Перерасчет задолженности по периоду &nbsp;<i class="icon-angle-right icon-large"></i>
            </g:remoteLink>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
