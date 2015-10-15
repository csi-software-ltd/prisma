<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${departments.size()}</div>    
    <div class="clear"></div>
  </div>
<g:if test="${departments}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>          
          <th>Название</th>
          <th>Руководитель</th>
          <th>Входит в</th>
          <th>Кол-во сотрудников</th>
          <th>Департамент</th>
          <th width="30"></th>          
        </tr>
      </thead>
      <tbody>
      <g:each in="${departments}" status="i" var="record">
        <tr align="left">         
          <td>${record.name}</td>
          <td>
            <g:each in="${User.findAllByIs_leaderAndDepartment_id(1,record.id,[sort:'name',order:'asc'])}" var="item" status="j"><g:if test="${j}"><br/></g:if>${item.name}
            </g:each>          
          </td>
          <td>${Department.get(record.parent?:0)?.name?:''}</td>
          <td align="center">${users_count[i]}</td> 
          <td align="center"><i class="icon-${record.is_dep?'ok':'minus'}" title="${record.is_dep?'Да':'Нет'}"></i></td>          
          <td align="center">
            <g:if test="${iscanedit}"><a class="button" href="${createLink(action:'departmentdetail',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a></g:if>
          </td>          
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${departments.size()}</span>
    <div class="clear"></div>
  </div>
</g:if>
</div>
