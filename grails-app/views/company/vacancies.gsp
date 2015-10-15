<g:formRemote id="vacancyAddForm" name="vacancyAddForm" url="[action:'addvacancy']" method="post" onSuccess="processaddvacancyResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorvacancylist">
      <li></li>
    </ul>
  </div>
  <div id="vacancy"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div class="tabs fright">
    <a id="employeestatus1" onclick="setEmployeestatus(1)"><i class="icon-list icon-large"></i> Активные </a>
    <a id="employeestatus0" onclick="setEmployeestatus(0)"><i class="icon-list icon-large"></i> Архив </a>
    <a id="employeestatus4" class="active" onclick="setEmployeestatus(4)"><i class="icon-list icon-large"></i> Вакансии </a>
    <a id="employeestatus3" onclick="setEmployeestatus(3)"><i class="icon-list icon-large"></i> Учредители </a>
    <a id="employeestatus2" onclick="setEmployeestatus(2)"><i class="icon-list icon-large"></i> История </a>
  </div>
  <div class="clear"></div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Должность</th>
          <th>Кол-во</th>
          <th>Зарплата</th>
          <th>Действия</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${vacancies}" status="i" var="record">
        <tr align="center">
          <td width="500">${record.position_name}</td>
          <td>${record.numbers}</td>
          <td>${intnumber(value:record.salary)}</td>
          <td width="50">
          <g:if test="${iscanedit}">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('vacancy_id').value=${record.id};$('vacancy_submit_button').click();"><i class="icon-pencil"></i></a>
            &nbsp;&nbsp;<g:remoteLink class="button" style="z-index:1" url="${[controller:'company', action:'deletecompvacancy', id:record.id, params:[company_id:company.id]]}" title="Удалить" onSuccess="setEmployeestatus(4)"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </td>
        </tr>
      </g:each>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="4" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('vacancy_id').value=0;$('vacancy_submit_button').click();">
              Добавить вакансию &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="vacancyForm" url="[action:'vacancy', params:[company_id:company.id]]" update="vacancy" onComplete="\$('errorvacancylist').up('div').hide();jQuery('#vacancyAddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="vacancy_submit_button" value="Показать"/>
  <input type="hidden" id="vacancy_id" name="id" value="0"/>
</g:formRemote>
