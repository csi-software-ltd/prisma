<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${compositions.size()}</div>    
    <div class="clear"></div>
  </div>
<g:if test="${compositions}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>          
          <th>Название</th>
          <th>Тип</th>
          <th width="50"></th>          
        </tr>
      </thead>
      <tbody>
      <g:each in="${compositions}" status="i" var="record">
        <tr align="left">         
          <td>${record.name}</td>
          <td>${Position.get(record.position_id).name}</td>
          <td align="center">          
          <g:if test="${iscanedit && !is_compers[i]}">
            <a class="button" onclick="showCompositionWindow(${record?.id?:0})" title="Редактировать"><i class="icon-pencil"></i></a> &nbsp;
            <g:remoteLink class="button" url="${[controller:'catalog',action:'removecomposition',id:record.id]}" title="Удалить" onSuccess="\$('form_submit_button').click();" before="if(!confirm('Подтвердите удаление!')) return false"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </td>          
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${compositions.size()}</span>   
  </div>
</g:if>
</div>
