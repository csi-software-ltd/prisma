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
          <th width="70px">Дата</th>
          <th width="70px">Срок исполнения</th>      
          <th>Постановщик</th>
          <th>Адресат</th>
          <th>Статус</th>
          <th>Тип задачи</th>
          <th width="300">Описание</th>
          <th width="30"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="left">
          <td>${String.format('%td.%<tm.%<tY',record.inputdate)}</td>
          <td>${record.term?String.format('%td.%<tm.%<tY',record.term):'нет'}</td>
          <td>${record.i_name?:'система'}</td>
          <td>${record.e_name?:departments[record.department_id]}</td>
          <td>${taskstatuses[record.taskstatus]}</td>
          <td>${tasktypes[record.tasktype_id]}</td>
          <td><g:shortString length="100" text="${record.description}"/></td>
          <td align="center">
            <a class="button" href="${g.createLink(controller:'task',action:'taskdetail',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
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