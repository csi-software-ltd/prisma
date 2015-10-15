<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult?.count?:0}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult?.count?:0}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${searchresult?.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>        
          <th>Название</th>
          <th>Описание</th>
          <th>Статус</th> 
          <th>Дата начала</th>          
          <th>Дата завершения</th>
          <th width="30"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center">         
          <td align="left">${record.name}</td>
          <td>${record.description}</td>
          <td><i class="icon-${record.modstatus?'ok':'minus'}" title="${record.modstatus?'активный':'неактивный'}"></i></td>
          <td>${record?.startdate?String.format('%td.%<tm.%<tY',record.startdate):''}</td>
          <td>${record?.enddate?String.format('%td.%<tm.%<tY',record.enddate):''}</td>
          <td>
            <a class="button" href="${createLink(controller:'catalog',action:'project',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          </td>          
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult?.count?:0}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count?:0}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
