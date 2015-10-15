<style type="text/css">
  .list td,.list th { font-size: 11px !important }
  tr.disabled > td { background:silver !important; opacity:0.4 !important }
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
          <th width="50px">Срок<br/>Дата</th>
          <th>Плательщик</th>
          <th>Тип</th>
          <th>Тип налога</th>
          <th>Сумма</th>
          <th>Назначение</th>
          <th>Статус</th>
          <th width="30px"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center">
          <td>${record.paydate?String.format('%td.%<tm.%<tY',record.paydate):''}<br/>${record.execdate?String.format('%td.%<tm.%<tY',record.execdate):''}</td>
          <td><g:if test="${record.fromcompany_id}"><g:link controller="company" action="detail" id="${record.fromcompany_id}" target="_blank">${record.fromcompany}</g:link></g:if><g:else>${record.fromcompany}</g:else></td>
          <td>
            ${record.paytype==1?'исходящий':'входящий'}
          </td>
          <td>${taxes[record.tax_id]}</td>
          <td>${number(value:record.summa)}</td>
          <td>${record.destination}</td>
          <td>${record.modstatus==0?'новый':record.modstatus==1?'в задании':record.modstatus==2?'выполнен':'подтвержден'}</td>
          <td align="center">
          <g:if test="${record.modstatus==3}">
            <g:link class="button" style="z-index:1" url="${[controller:'payment',action:'paymentdetail',id:payments[record.id]]}" title="Детали" target="_blank"><i class="icon-edit"></i></g:link>
          </g:if>
          <g:if test="${record.modstatus==0&&iscandelete}">
            <g:remoteLink class="button" style="z-index:1" url="${[controller:'payment',action:'deletebudgrequest',id:record.id]}" title="Удалить" before="if(!confirm('Вы действительно хотите удалить платеж?')) return false" onSuccess="\$('form_submit_button').click();"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.count}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>