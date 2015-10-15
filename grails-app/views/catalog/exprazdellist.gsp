<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${expensetypes.size()}</div>
    <div class="clear"></div>
  </div>
<g:if test="${expensetypes}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th width="30">Код</th>
          <th>Название</th>
          <th width="50"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${expensetypes}" status="i" var="record">
        <tr align="center">
          <td>${record.id}</td>
          <td align="left">${record.name}</td>
          <td align="center">
            <a class="button" href="${createLink(controller:controllerName,action:'exprazdel',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          <g:if test="${exp2counts[record.id]==0}">
            &nbsp;<g:remoteLink class="button" before="if(!confirm('Удалить раздел?')) return false" url="${[controller:controllerName,action:'removeexprazdel',id:record.id]}" title="Удалить" onSuccess="\$('form_submit_button').click();"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</g:if>
</div>
