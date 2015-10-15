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
          <th>Дата</th>
          <th>Автор</th>
          <th>Текст вопроса</th>
          <th>Тип вопроса</th>
          <th>Статус</th>
          <th>Статус просмотра</th>
          <th width="30"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center" style="${!record.modstatus?'color:red':''}">
          <td>${record.id}<br/>${String.format('%td.%<tm.%<tY %<tT',record.adate)}</td>
          <td nowrap>${record.user_name}</td>
          <td width="450" align="left"><g:rawHtml><g:shortString text="${record.qtext}" length="250"/></g:rawHtml></td>
          <td>${ftypes[record.feedbacktype_id]}</td>
          <td>
            ${record.modstatus==1?'ответ':record.modstatus==2?'FaQ':'вопрос'}
          </td>
          <td>
            <g:if test="${record.modstatus&&record.is_readanswer}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
            <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
            <a class="button" href="${createLink(action:'question',id:record.id)}" title="Детали"><i class="icon-pencil"></i></a>
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
