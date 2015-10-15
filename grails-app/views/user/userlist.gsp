<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${users.count}</div>
    <div class="fright">
      <g:paginate controller="user" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${users.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>  
<g:if test="${users.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>                              
          <th>Код</th>
          <th>Имя</th>
          <th>Логин</th>
          <th>Физ.лицо</th>
          <th>Группа</th>
          <th>Отдел</th>
          <th>Статус</th>
          <th>Блок</th>
          <th>Время</th>
          <th width="50"></th>          
        </tr>
      </thead>
      <tbody>
      <g:each in="${users.records}" status="i" var="record">
        <tr align="left">
          <td align="center">${record.id?:0}</td>
          <td>${record.name}</td>          
          <td>${record.login}</td>           
          <td><g:link controller="user" action="persdetail" id="${record.pers_id}">${Pers.get(record.pers_id?:0)?.shortname?:''}</g:link></td>
          <td>${Usergroup.get(record.usergroup_id?:0)?.name?:''}</td>          
          <td>${Department.get(record.department_id?:0)?.name?:''}</td>
          <td align="center"><i class="icon-${record.modstatus?'ok':'minus'}" title="${record.modstatus?'активный':'неактивный'}"></i></td>
          <td align="center"><g:if test="${record.is_block==1}"><abbr title="заблокирован"><i class="icon-ok"></i></abbr></g:if></td>
          <td>${record.lastdate?shortDate(date:record.lastdate):'не входил'}</td>
          <td align="center"><a id="edit${i}" class="button" href="${createLink(action:'userdetail',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a> &nbsp;
            <g:if test="${user?.group?.is_usergroupenter}"><a class="button" onclick="loginAsUser(${record.id})" title="Войти под именем"><i class="icon-signin"></i></a></g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${users.count}</span>
    <span class="fright">
      <g:paginate controller="user" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${users.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>  
</g:if>
</div>
