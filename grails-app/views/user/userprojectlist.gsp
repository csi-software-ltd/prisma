<g:formRemote id="projectAddForm" name="projectAddForm" url="[action:'adduserproject',id:useredit.id]" method="post" onSuccess="processaddprojectResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorprojectlist">
      <li></li>
    </ul>
  </div>
  <div id="userproject"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${userprojects.size()}</div>
    <div class="clear"></div>
  </div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>      
        <tr>                              
          <th>Название</th>          
          <th width="30"></th>                                     
        </tr>
      </thead>
      <tbody>
        <tr align="center">
          <td colspan="2">
            <a class="button" id="addprojectbutton" href="javascript:void(0)" onclick="$('project_id').value=0;$('project_submit_button').click();">
              Добавить проект &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      <g:each in="${userprojects}" status="i" var="record">
        <tr align="center">
          <td>${record.name}</td>
          <td>
            <g:remoteLink class="button" style="z-index:1" url="${[controller:'user',action:'removeuserproject',id:record.id,params:[user_id:useredit.id]]}" title="Удалить" onSuccess="getUserProject()"><i class="icon-trash"></i></g:remoteLink>
          </td>                                   
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="projectForm" url="[action:'userproject',params:[user_id:useredit.id]]" update="userproject" onComplete="\$('errorprojectlist').up('div').hide();jQuery('#projectAddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="project_submit_button" value="Показать" /> 
  <input type="hidden" id="project_id" name="id" value="0" />  
</g:formRemote>
