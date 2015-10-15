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
          <th>Дата модификации</th>
          <th>Текст вопроса</th>
          <th>Тип вопроса</th>
          <th>Статус ответа</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center" style="${record.modstatus&&!record.is_readanswer?'color:red':''}">
          <td>${record.id}<br/>${String.format('%td.%<tm.%<tY',record.adate)}</td>
          <td width="450" align="left"><g:rawHtml>${record.qtext}</g:rawHtml></td>
          <td>${ftypes[record.feedbacktype_id]}</td>
          <td>
            <a href="${g.createLink(action:'question',id:record.id)}" target="_blank"><g:if test="${!record.modstatus}">Ожидает ответа</g:if><g:else>Просмотреть ответ</g:else></a>
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