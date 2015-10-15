<div id="ajax_wrap">
<g:if test="${puser}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr style="line-height:15px">                              
          <th>Имя</th>          
          <th>Отдел</th>          
          <th>Руководитель</th>                            
        </tr>
      </thead>
      <tbody>     
        <tr align="left">
          <td><a href="${createLink(controller:'user',action:'userdetail',id:puser.id)}" title="Пользователь">${puser.name}</a>
          <td>${Department.get(puser.department_id?:0)?.name?:''}</td>                         
          <td align="center"><i class="icon-${puser.is_leader?'ok':'minus'}" title="${puser.is_leader?'Руководитель':'Сотрудник'}"></i></td>                   
        </tr>     
      </tbody>
    </table>
  </div>  
</g:if>
<g:else>
  <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
    <tr align="center">
      <td colspan="6" class="btns">
        <a class="button" id="adduserbutton" href="javascript:void(0)" onclick="newUser()">Добавить пользователя &nbsp;<i class="icon-angle-right icon-large"></i></a>
      </td>
    </tr>
  </table>  
</g:else>
</div>

