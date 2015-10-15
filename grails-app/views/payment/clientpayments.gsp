<style type="text/css">
  .list td,.list th { font-size: 11px !important}
  tr.yellow > td { background:lightyellow !important }
</style>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult.count}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${searchresult.records}">
  <div id="resultList">
    <g:formRemote name="createDealForm" url="[action:'createdeal']" onSuccess="\$('form_submit_button').click();">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
        <g:if test="${iscanedit}">
          <th><input type="checkbox" id="groupcheckbox" onclick="togglecheck()"></th>
        </g:if>
          <th>Дата платежа<br/>Номер платежа</th>
          <th>Клиент<br/>Подклиент</th>
          <th>Сделка</th>
          <th>Тип платежа</th>
          <th>Контрагенты</th>
          <th>Сумма</th>
          <th>Процент<br/>Тип процента</th>
          <th>Процент клиента<br/>посредника</th>
          <th>Комиссия</th>
          <th>Возврат клиента<br/>посредника</th>
          <th>Остаток счета</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center" class="${record.modstatus<2?'yellow':''}" style="${record.client_id&&record.curclientsaldo+record.dinclientsaldo+record.computeClientdelta()<0?'color:red':''}">
        <g:if test="${iscanedit}">
          <td><input type="checkbox" name="payrequestids" value="${record.id}" ${record.deal_id>0||record.client_id==0||!(record.paytype in [1,2,7,8,10,11])||record.modstatus<2||record.subclient_id!=0?'disabled':''}></td>
        </g:if>
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}<br/>${record.id}<g:if test="${record.sfactura}">&nbsp;<abbr title="Счет-фактура" style="color:#157DFB"><i class="icon-paper-clip icon-1x"></i></abbr></g:if></td>
          <td><g:if test="${record.is_bankmoney}">Банк</g:if><g:else>${record.client_name}<br/>${record.subclient_name}</g:else></td>
          <td><g:if test="${record.deal_id}"><g:link controller="payment" action="deal" id="${record.deal_id}" target="_blank">${record.deal_id}</g:link></g:if><g:else>нет</g:else></td>
          <td>
          <g:if test="${record.is_clientcommission}"><abbr title="Возврат комиссии"><i class="icon-gift icon-large"></i></abbr></g:if>
          <g:elseif test="${record.is_midcommission}"><abbr title="Возврат посреднику"><i class="icon-link icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==1}"><abbr title="Исходящий"><i class="icon-signout icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==2}"><abbr title="Входящий"><i class="icon-signin icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==3}"><abbr title="Внутренний"><i class="icon-refresh icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==4}"><abbr title="Списание"><i class="icon-trash icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==8}"><abbr title="Откуп"><i class="icon-magic icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==9}"><abbr title="Комиссия"><i class="icon-money icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==7}"><abbr title="Абон. плата"><i class="icon-calendar icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==10}"><abbr title="Связанный входящий"><i class="icon-exchange icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==11}"><abbr title="Внешний"><i class="icon-external-link-square icon-large"></i></abbr></g:elseif>
          <g:else><abbr title="Пополнение"><i class="icon-certificate icon-large"></i></abbr></g:else>
          </td>
          <td><g:if test="${record.paytype==4}">Списание по агентскому договору</g:if><g:else>${record.fromcompany_name?:record.fromcompany?:'нет'}<br/>${record.tocompany_name?:record.tocompany?:'нет'}</g:else></td>
          <td>${number(value:record.summa)}<g:if test="${record.clientcommission>0}"><br/>${number(value:record.clientcommission)}</g:if><g:if test="${record.agentcommission>0}"><br/>${number(value:record.agentcommission)}</g:if></td>
          <td><g:if test="${record.client_id}">${number(value:record.compercent)}<br/>${record.percenttype?'деление':'умножение'}</g:if><g:else>нет</g:else></td>
          <td><g:if test="${record.subclient_id}">${number(value:record.supcompercent)}<br/>${number(value:record.midpercent)}</g:if><g:else>нет</g:else></td>
          <td>${number(value:record.comission)}</td>
          <td><g:if test="${record.subclient_id}">${number(value:record.supcomission)}<br/>${number(value:record.midcomission)}</g:if><g:else>нет</g:else></td>
          <td><g:if test="${record.client_id}">${number(value:record.curclientsaldo+record.dinclientsaldo+record.computeClientdelta())}</g:if><g:else>нет</g:else></td>
          <td width="50">
            <a class="button" style="z-index:1" href="${g.createLink(controller:controllerName, action:'clientpayment', id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          <g:if test="${((record.modstatus==0&&record.paytype==1&&record.related_id==0)||(record.modstatus==2&&record.paytype==2)||record.paytype in [10,11])&&record.deal_id==0&&record.clientcommission==0&&(record.initiator==user.id||iscandelete)}">
            &nbsp;&nbsp;<g:remoteLink class="button" style="z-index:1;" url="${[controller:'payment',action:'deleteclientpayment',id:record.id]}" title="Удалить" before="if(!confirm('Вы действительно хотите удалить данный платеж?')) return false" onSuccess="\$('form_submit_button').click()"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          <g:if test="${record.file_id}">
            &nbsp;&nbsp;<a class="button" style="z-index:1" href="${createLink(controller:'payment', action:'showscan', id:record.file_id, params:[code:Tools.generateModeParam(record.file_id)])}" title="Скан документа" target="_blank"><i class="icon-picture"></i></a>
          </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
      <input type="submit" id="createprequests_form_submit_button" style="display:none"/>
    </g:formRemote>
  </div>
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult.count}</div>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>