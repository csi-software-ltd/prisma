<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${zalogtypes.size()}</div>
    <div class="clear"></div>
  </div>
<g:if test="${zalogtypes}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th width="30">Код</th>
          <th>Название</th>
          <th width="30"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${zalogtypes}" status="i" var="record">
        <tr align="center">
          <td>${record.id}</td>
          <td align="left">${record.name}</td>
          <td>
            <a class="button" href="${createLink(controller:controllerName,action:'zalogtype',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</g:if>
</div>