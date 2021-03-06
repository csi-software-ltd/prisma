﻿<style type="text/css">
  table.list thead th, table.list tbody th, table.list thead td, table.list tbody td { font-size: 11px }
  tr.yellow > td { background:lightyellow !important }
</style>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult.count}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${searchresult.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Срок платежа</th>
          <th>Дата акцепта</th>
          <th>Дата исполнения</th>
          <th>Компания</th>
          <th>Банк</th>
          <th>Тип платежа</th>
          <th>Контрагент</th>
          <th>Назначение</th>
          <th>Сумма</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="left" class="${record.is_urgent?'yellow':''}">
          <td align="center">${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td align="center">${String.format('%td.%<tm.%<tY',record.acceptdate)}</td>
          <td align="center">${record.execdate?String.format('%td.%<tm.%<tY %<tH:%<tM',record.execdate):'нет'}</td>
          <td>${record.fromcompany}</td>
          <td>${record.bank_name}</td>
          <td>${record.paytype==1?'исходящий':'внутренний'}</td>
          <td>${record.tocompany}</td>
          <td><g:shortString length="50" text="${record.destination}"/></td>
          <td>${number(value:record.summa)}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.count}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>