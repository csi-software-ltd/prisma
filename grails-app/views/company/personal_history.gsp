<div id="ajax_wrap">
  <div class="tabs fright">
    <a id="employeestatus1" onclick="setEmployeestatus(1)"><i class="icon-list icon-large"></i> Активные </a>
    <a id="employeestatus0" onclick="setEmployeestatus(0)"><i class="icon-list icon-large"></i> Архив </a>
    <a id="employeestatus4" onclick="setEmployeestatus(4)"><i class="icon-list icon-large"></i> Вакансии </a>
    <a id="employeestatus3" onclick="setEmployeestatus(3)"><i class="icon-list icon-large"></i> Учредители </a>
    <a id="employeestatus2" class="active" onclick="setEmployeestatus(2)"><i class="icon-list icon-large"></i> История </a>
  </div>
  <div class="clear"></div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата</th>
          <th>Фамилия</th>
          <th>Начало работы</th>
          <th>Конец работы</th>
          <th>Тип</th>
          <th>Должность</th>
          <th>Срок полномочий</th>
        <g:if test="${session.user.confaccess>0}">
          <th>Зарплата</th>
        </g:if>
          <th>Комментарий</th>
          <th>Автор</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${history}" status="i" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY %<tT',record.inputdate)}</td>
          <td>${record.shortname}</td>
          <td>${String.format('%td.%<tm.%<tY',record.jobstart)}</td>
          <td>${record.jobend?String.format('%td.%<tm.%<tY',record.jobend):'нет'}</td>
          <td>${positions[record.position_id]}</td>
          <td>${record.position_name}</td>
          <td>${record.gd_valid?String.format('%td.%<tm.%<tY',record.gd_valid):'нет'}</td>
        <g:if test="${session.user.confaccess>0}">
          <td>${intnumber(value:record.salary)}</td>
        </g:if>
          <td>${record.comment}</td>
          <td>${record.admin_name}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>