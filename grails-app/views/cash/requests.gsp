<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${requests.count}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${requests.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${requests.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>На дату</th>
          <th>Сумма</th>
          <th>Статус</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${requests.records}" status="i" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.reqdate)}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td>${cashstatus[record.modstatus]}</td>
          <td nowrap>
            <a class="button" href="${g.createLink(controller:'cash',action:'cashrequest',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          <g:if test="${session.user.cashaccess==3&&record.modstatus in [1,2,6]}">
            &nbsp;&nbsp;<g:remoteLink class="button" style="z-index:1;" url="${[controller:'cash',action:'deletecashrequest',id:record.id]}" title="Удалить" onSuccess="\$('form_submit_button').click()"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${requests.count}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${requests.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>