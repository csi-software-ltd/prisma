<g:formRemote id="founderAddForm" name="founderAddForm" url="[action:'addfounder']" method="post" onSuccess="processaddfounderResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorfounderlist">
      <li></li>
    </ul>
  </div>
  <div id="founder"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div class="tabs fright">
    <a id="employeestatus1" onclick="setEmployeestatus(1)"><i class="icon-list icon-large"></i> Активные </a>
    <a id="employeestatus0" onclick="setEmployeestatus(0)"><i class="icon-list icon-large"></i> Архив </a>
    <a id="employeestatus4" onclick="setEmployeestatus(4)"><i class="icon-list icon-large"></i> Вакансии </a>
    <a id="employeestatus3" class="active" onclick="setEmployeestatus(3)"><i class="icon-list icon-large"></i> Учредители </a>
    <a id="employeestatus2" onclick="setEmployeestatus(2)"><i class="icon-list icon-large"></i> История </a>
  </div>
  <div class="clear"></div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Фамилия</th>
          <th>Доля в процентах</th>
          <th>Доля в тыс.руб</th>
          <th>Начало</th>
          <th>Окончание</th>
          <th>Комментарий</th>
          <th>Действия</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${founders}" status="i" var="record">
      <g:if test="${record.modstatus}">
        <tr align="center">
          <td>${record.shortname?:record.company_name}</td>
          <td>${record.share}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td>${String.format('%td.%<tm.%<tY',record.startdate)}</td>
          <td>Нет</td>
          <td>${record.comment}</td>
          <td valign="middle">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('founder_id').value=${record.id};$('founder_submit_button').click();"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:if>
      </g:each>
      <g:if test="${founders.find{it.modstatus==0}}">
        <tr>
          <td colspan="7" class="btns" style="text-align:center">
            Архив
          </td>
        </tr>
      </g:if>
      <g:each in="${founders}" status="i" var="record">
      <g:if test="${!record.modstatus}">
        <tr align="center">
          <td>${record.shortname}</td>
          <td>${record.share}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td>${String.format('%td.%<tm.%<tY',record.startdate)}</td>
          <td>${String.format('%td.%<tm.%<tY',record.enddate)}</td>
          <td>${record.comment}</td>
          <td valign="middle">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('founder_id').value=${record.id};$('founder_submit_button').click();"><i class="icon-pencil"></i></a>&nbsp;&nbsp;
            <g:remoteLink class="button" style="z-index:1" before="if(!confirm('Вы действительно хотите удалить учредителя?')) return false" url="${[action:'deletefounder',id:record.id, params:[company_id:company.id]]}" title="Удалить" onSuccess="getPersonal()"><i class="icon-trash"></i></g:remoteLink>
          </td>
        </tr>
      </g:if>
      </g:each>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="7" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('founder_id').value=0;$('founder_submit_button').click();">
              Добавить учредителя &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="founderForm" url="[action:'founder', params:[company_id:company.id]]" update="founder" onComplete="\$('errorfounderlist').up('div').hide();jQuery('#founderAddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="founder_submit_button" value="Показать"/>
  <input type="hidden" id="founder_id" name="id" value="0"/>
</g:formRemote>
