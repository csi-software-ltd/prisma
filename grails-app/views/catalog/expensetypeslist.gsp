<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${expensetypes.count}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${expensetypes.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${expensetypes.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Раздел</th>
          <th>Подраздел</th>
          <th>Статья</th>
          <th width="50">Статус</th>
          <th width="30"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${expensetypes.records}" status="i" var="record">
        <tr align="left">
          <td>${record.razdel}</td>
          <td>${record.podrazdel}</td>
          <td>${record.name}</td>
          <td align="center">
          <g:if test="${record.modstatus}"><abbr title="Активный"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Архив"><i class="icon-ban-circle"></i></abbr></g:else>
          </td>
          <td align="center">
            <a class="button" href="${createLink(controller:controllerName,action:'expensetype',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${expensetypes.count}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${expensetypes.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
