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
          <th>Название</th>
          <th width="200px">Основной клиент</th>
          <th width="50px">Дата заведения</th>
          <th width="30px">Статус</th>
          <th width="50px"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center">
          <td align="left">${record.name}</td>
          <td>${record.parent?mainclients[record.parent]:'нет'}</td>
          <td>${String.format('%td.%<tm.%<tY',record.inputdate)}</td>
          <td><i class="icon-${record.modstatus?'ok':'minus'}" title="${record.modstatus?'активный':'неактивный'}"></i></td>
          <td>
            <a class="button" href="${g.createLink(controller:'catalog',action:'client',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>&nbsp;&nbsp;
            <g:if test="${record?.modstatus}">
              <g:remoteLink class="button" url="${[controller:'catalog',action:'setClientStatus',id:record.id,params:[modstatus:0]]}" title="Деактивировать" onSuccess="\$('form_submit_button').click();" before="if(!confirm('Подтвердите деактивацию!')) return false"><i class="icon-trash"></i></g:remoteLink>
            </g:if><g:else>
              <g:remoteLink class="button" url="${[controller:'catalog',action:'setClientStatus',id:record.id,params:[modstatus:1]]}" title="Активировать" onSuccess="\$('form_submit_button').click();"><i class="icon-ok"></i></g:remoteLink>
            </g:else>
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