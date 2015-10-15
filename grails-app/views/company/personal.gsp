<g:formRemote id="employeeAddForm" name="employeeAddForm" url="[action:'addemployee']" method="post" onSuccess="processaddemployeeResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="erroremployeelist">
      <li></li>
    </ul>
  </div>
  <div id="employee"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div class="tabs fright">
    <a id="employeestatus1" <g:if test="${modstatus==1}">class="active"</g:if> onclick="setEmployeestatus(1)"><i class="icon-list icon-large"></i> Активные </a>
    <a id="employeestatus0" <g:if test="${modstatus==0}">class="active"</g:if> onclick="setEmployeestatus(0)"><i class="icon-list icon-large"></i> Архив </a>
    <a id="employeestatus4" onclick="setEmployeestatus(4)"><i class="icon-list icon-large"></i> Вакансии </a>
    <a id="employeestatus3" onclick="setEmployeestatus(3)"><i class="icon-list icon-large"></i> Учредители </a>
    <a id="employeestatus2" onclick="setEmployeestatus(2)"><i class="icon-list icon-large"></i> История </a>
  </div>
  <div class="clear"></div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Фамилия</th>
          <th>Должность</th>
          <th>Тип</th>
        <g:if test="${session.user.confaccess>0}">
          <th>Офиц. оклад</th>
        </g:if>
          <g:if test="${modstatus==1}"><th>Дата назначения</th></g:if><g:else><th>Дата увольнения</th></g:else>
          <th>Действия</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${personal}" status="i" var="record">
        <tr align="center">
          <td>${record.shortname}</td>
          <td>${record.position_name}</td>
          <td>${positions[record.position_id]}<g:if test="${record.position_id==1&&modstatus==1}"><br/><small>до ${String.format('%td.%<tm.%<tY',record.gd_valid)}</small></g:if></td>
        <g:if test="${session.user.confaccess>0}">
          <td>${intnumber(value:record.salary)}</td>
        </g:if>
        <g:if test="${modstatus==1}">
          <td>${String.format('%td.%<tm.%<tY',record.jobstart)}</td>
        </g:if><g:else>
          <td>${String.format('%td.%<tm.%<tY',record.jobend)}</td>
        </g:else>
          <td valign="middle">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('employee_id').value=${record.id};$('employee_submit_button').click();"><i class="icon-pencil"></i></a>
          <g:if test="${record.modstatus==0&&iscanedit}">
            &nbsp;&nbsp;<g:remoteLink style="z-index:1" class="button" url="${[controller:'company', action:'deleteemployee', id:record.id, params:[company_id:company.id]]}" title="Удалить" onSuccess="setEmployeestatus(0)"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </td>
        </tr>
      </g:each>
      <g:if test="${modstatus==1&&iscanedit}">
        <tr>
          <td colspan="7" class="btns" style="text-align:center">
            <a class="button" id="addemployeebutton" href="javascript:void(0)" onclick="$('employee_id').value=0;$('employee_submit_button').click();">
              Добавить сотрудника &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      <g:if test="${modstatus==1}">
        <tr>
          <td colspan="7" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('stafflistForm').submit();">
              Штатное расписание &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="employeeForm" url="[action:'employee', params:[company_id:company.id]]" update="employee" onComplete="\$('erroremployeelist').up('div').hide();jQuery('#employeeAddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="employee_submit_button" value="Показать"/>
  <input type="hidden" id="employee_id" name="id" value="0"/>
</g:formRemote>
<g:form name="stafflistForm" url="${[controller:'company',action:'stafflist']}" method="post" target="_blank">
  <input type="hidden" id="company_id" name="company_id" value="${company.id}"/>
</g:form>