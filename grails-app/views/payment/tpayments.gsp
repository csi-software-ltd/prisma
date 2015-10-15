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
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата платежа<br/>Номер платежа</th>
          <th>Клиент</th>
          <th>Сделка</th>
          <th>Тип платежа</th>
          <th>Контрагенты</th>
          <th>Сумма</th>
          <th>Процент откупа<br/>Сумма откупа</th>
          <th>Остаток счета</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center" class="${record.modstatus<2?'yellow':''}" style="${record.client_id&&record.curclientsaldo+record.dinclientsaldo+record.computeClientdelta()<0?'color:red':''}">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}<br/>${record.id}</td>
          <td><g:if test="${record.is_bankmoney}">Банк</g:if><g:else>${record.client_name}</g:else></td>
          <td><g:if test="${record.deal_id}">${record.deal_id}</g:if><g:else>нет</g:else></td>
          <td>
          <g:if test="${record.paytype==1}"><abbr title="Исходящий"><i class="icon-signout icon-large"></i></abbr></g:if>
          <g:elseif test="${record.paytype==2}"><abbr title="Входящий"><i class="icon-signin icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==8}"><abbr title="Откуп"><i class="icon-magic icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==9}"><abbr title="Комиссия"><i class="icon-money icon-large"></i></abbr></g:elseif>
          <g:else><abbr title="Абон. плата"><i class="icon-calendar icon-large"></i></abbr></g:else>
          </td>
          <td>${record.fromcompany_name?:record.fromcompany?:'нет'}<br/>${record.tocompany_name?:record.tocompany?:'нет'}</td>
          <td>${number(value:record.summa)}</td>
          <td><g:if test="${record.client_id&&record.paytype==8}">${number(value:record.payoffperc)}<br/>${number(value:record.payoffsumma)}</g:if><g:else>нет</g:else></td>
          <td><g:if test="${record.client_id}">${number(value:record.curclientsaldo+record.dinclientsaldo+record.computeClientdelta())}</g:if><g:else>нет</g:else></td>
          <td nowrap>
            <a class="button" style="z-index:1" href="${g.createLink(controller:controllerName, action:'tpayment', id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          <g:if test="${record.modstatus==0&&record.deal_id==0&&record.paytype!=2&&record.clientcommission==0&&(record.initiator==user.id||iscanedit)}">
            &nbsp;&nbsp;<g:remoteLink class="button" style="z-index:1;" url="${[controller:'payment',action:'deletetpayment',id:record.id]}" title="Удалить" before="if(!confirm('Вы действительно хотите удалить данный платеж?')) return false" onSuccess="\$('form_submit_button').click()"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          <g:if test="${record.file_id}">
            &nbsp;&nbsp;<a class="button" style="z-index:1" href="${createLink(controller:'payment', action:'showscan', id:record.file_id, params:[code:Tools.generateModeParam(record.file_id)])}" title="Скан документа" target="_blank"><i class="icon-picture"></i></a>
          </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
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