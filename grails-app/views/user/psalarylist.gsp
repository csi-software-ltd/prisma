<g:formRemote name="psalaryEditForm" url="[action:'savePsalaryDetail',params:[pers_id:inrequest?.pers_id?:0]]" method="post" onSuccess="processpsalaryResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorpsalarylist">
      <li></li>
    </ul>
  </div>
  <div id="psalary"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${psalary.size()}</div>   
    <div class="clear"></div>
  </div>  
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">      
      <thead>
        <tr style="line-height:15px">                              
          <th width="50">Дата</th>          
          <th width="50">Дата<br/>модификации</th>
          <th>Фактическая зарплата</th>          
          <th width="400">Комментарий</th>
          <th>Администратор</th>
          <th width="50"></th>          
        </tr>
      </thead>      
      <tbody>
      <g:each in="${psalary}" status="i" var="record">
        <tr align="center">
          <td>${shortDateNoTime(date:record.pdate)}</td>
          <td>${record.moddate?shortDate(date:record.moddate):''}</td>
          <td>${intnumber(value:record.actsalary?:0)}</td>
          <td>${record.comment}</td>
          <td>${User.get(record.admin_id)?.name}</td>
          <td valign="middle" align="center">
          <g:if test="${user.confaccess==2||(pers_user?.perstype==2 && user.is_tehdirleader)}">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="editPsalary('${record?.id?:0}')"><i class="icon-pencil"></i></a>&nbsp;&nbsp;
            <g:remoteLink style="z-index:1" class="button" url="${[controller:'user', action:'deletepsalary', id:record.id, params:[pers_id:pers_user?.id]]}" title="Удалить" onSuccess="getPsalary()"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </td>
        </tr>
      </g:each> 
      <g:if test="${user.confaccess==2||(pers_user?.perstype==2 && user.is_tehdirleader)}">
        <tr align="center">
          <td colspan="6" style="display:${((pers_user?.perstype==2)&&!pers_user?.is_fixactsalary)?'none':'table-cell'}">
            <a class="button" id="addpsalarybutton" href="javascript:void(0)" onclick="editPsalary(0)">Назначить новый оклад &nbsp;<i class="icon-angle-right icon-large"></i></a>
          </td>
        </tr>
      </g:if>      
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${psalary.size()}</span>  
    <div class="clear"></div>    
  </div>  
</div>
<g:formRemote name="psalaryForm" url="[action:'psalary']" update="psalary" onComplete="\$('errorpsalarylist').up('div').hide();jQuery('#psalaryEditForm').slideDown();" style="display:none">
  <input type="text" id="psalary_id" name="id" value="0" style="display:none"/>
  <input type="submit" class="button" id="psalarydetail_submit_button" value="Показать"/>
</g:formRemote>
