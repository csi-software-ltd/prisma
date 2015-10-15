<style type="text/css">
  .list td,.list th { font-size: 12px }
  abbr { vertical-align: middle; }
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
          <th rowspan="2">Отдел</th>
          <th rowspan="2">ФИО</th>
          <th rowspan="2">Факт. оклад</th>
          <th rowspan="2">Аванс</th>
          <th rowspan="2">Официальный б/н</th>
          <th rowspan="2">Сумма к нал. выплате</th>
          <th colspan="3">Статусы оплаты</th>
        </tr>
        <tr>
          <th>Аванс</th>
          <th>Б/Н</th>
          <th>Итог</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" var="record">
        <tr align="center">
          <td>${record.d_name}</td>
          <td>${record.p_shortname}</td>
          <td>${number(value:record.actsalary)}</td>
          <td>${intnumber(value:record.prepayment)}</td>
          <td>${number(value:record.offsalary)}</td>
          <td>${intnumber(value:record.cash)}</td>
          <td>
          <g:if test="${record.prepaystatus==2}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:elseif test="${record.prepaystatus==1}"><abbr title="Начислено"><i class="icon-hourglass-half"></i></abbr></g:elseif>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
          <g:if test="${record.offstatus==2}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:elseif test="${record.offstatus==1}"><abbr title="Начислено"><i class="icon-hourglass-half"></i></abbr></g:elseif>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
          <g:if test="${record.cashstatus==2}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:elseif test="${record.cashstatus==1}"><abbr title="Начислено"><i class="icon-hourglass-half"></i></abbr></g:elseif>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
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
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>