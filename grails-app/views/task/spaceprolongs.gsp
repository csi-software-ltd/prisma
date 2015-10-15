<style type="text/css">
  tr.green > td { background:lightgreen !important }
</style>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult.count}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${inrequest}" 
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
          <th rowspan="2">Код</th>
          <th rowspan="2">Срок договора<br/>Номер</th>
          <th rowspan="2">Арендатор</th>
          <th rowspan="2">Арендодатель</th>
          <th rowspan="2">Тип<br/>Признак</th>
          <th rowspan="2">Адрес</th>
          <th colspan="2">Статус</th>
          <th rowspan="2" width="30"></th>
        </tr>
        <tr>
          <th>разрешения</th>
          <th>работы</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center" style="${record.enddate<today?'color:red':''}">
          <td><g:link controller="agreement" action="space" id="${record.id}" target="_blank">${record.id}</g:link></td>
          <td>${String.format('%td.%<tm.%<tY',record.enddate)}<br/>${record.anumber} от ${String.format('%td.%<tm.%<tY',record.adate)}</td>
          <td>${record.arendator_name}</td>
          <td>${record.arendodatel_name}</td>
          <td>${spacetypes[record.spacetype_id]}<br/>${record.asort?'аренда':'субаренда'}</td>
          <td>${record.shortaddress}</td>
          <td>
          <g:if test="${record.permitstatus==0}"><abbr title="Нет информации"><i class="icon-minus icon-large"></i></abbr></g:if>
          <g:elseif test="${record.permitstatus==1}"><abbr title="Разрешено"><i class="icon-ok icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.permitstatus==-1}"><abbr title="Отказано"><i class="icon-ban-circle icon-large"></i></abbr></g:elseif>
          </td>
          <td>
          <g:if test="${record.workstatus==0}"><abbr title="Нет информации"><i class="icon-minus icon-large"></i></abbr></g:if>
          <g:else><abbr title="Принято к исполнению"><i class="icon-ok icon-large"></i></abbr></g:else>
          </td>
          <td>
            <a class="button" href="${g.createLink(controller:controllerName,action:'spaceprolong',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.count}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>