<g:formRemote id="projectAddForm" name="projectAddForm" url="[action:'addtoproject']" method="post" onSuccess="processaddprojectResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorprojectlist">
      <li></li>
    </ul>
  </div>
  <div id="project"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>№</th>
          <th>Название</th>
          <th>Действие</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${projects}" status="i" var="record">
        <tr align="center">
          <td>${record.id}</td>
          <td>${record.name}</td>
          <td><g:remoteLink class="button" url="${[controller:'company',action:'removefromproject',id:record.id,params:[company_id:company.id]]}" title="Удалить" onSuccess="getProjects()"><i class="icon-trash"></i></g:remoteLink></td>
        </tr>
      </g:each>
        <tr>
          <td colspan="3" class="btns" style="text-align:center">
          <g:if test="${iscanaddproject}">
            <a class="button" id="addprojectbutton" href="javascript:void(0)" onclick="$('project_submit_button').click();">
              Добавить проект &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </g:if>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="projectForm" url="[action:'project', id:company.id]" update="project" onComplete="\$('errorprojectlist').up('div').hide();jQuery('#projectAddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="project_submit_button" value="Показать"/>
</g:formRemote>
