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
          <th>Номер</th>
          <th>Клиент</th>
          <th>Дата сделки<br/>от&nbsp;&ndash;&nbsp;до</th>
          <th>Приход<br/>Выход</th>
          <th>Комиссия</th>
          <th>Сальдо сделки</th>
          <th>Статус</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center">
          <td>${record.id}</td>
          <td>${record.client_name}</td>
          <td>${String.format('%td.%<tm.%<tY',record.dstart)}<br/>${String.format('%td.%<tm.%<tY',record.dend)}</td>
          <td>${number(value:record.income)}<br/>${number(value:record.outlay)}</td>
          <td>${number(value:record.commission)}</td>
          <td>${number(value:record.dealsaldo)}</td>
          <td>
          <g:if test="${record.modstatus}"><abbr title="Согласована"><i class="icon-ok icon-large"></i></abbr></g:if>
          <g:else><abbr title="Новая"><i class="icon-minus icon-large"></i></abbr></g:else>
          </td>
          <td>
            <a class="button" href="${g.createLink(controller:controllerName,action:'deal',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
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
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>