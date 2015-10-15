<table class="list" style="width:100%">
  <thead>
    <tr>
      <th>Моя задолженность</th>
      <th>Мои штрафы</th>
    <g:if test="${user.is_loan}">
      <th>Моя задолженность<br/>по заемным средствам</th>
    </g:if><g:if test="${session.user.cashaccess==2}">  
      <th>Остаток кассы</th>
      <th>Задолженности сотрудников</th>
    </g:if><g:if test="${session.user.cashaccess==3 || session.user.cashaccess==5}">
      <th>Главная касса</th>
      <th>Хранение</th>
    </g:if><g:if test="${session.user.group.is_payt}">
      <th>Отдел Т</th>
      <th>Остаток доп. карт</th>
    </g:if>
    </tr>
  </thead>
  <tbody>
    <tr align="center">
      <td>${intnumber(value:user.saldo)}</td>
      <td style="${user.penalty>0?'color:red':''}">${intnumber(value:user.penalty)}</td>
    <g:if test="${user.is_loan}">
      <td>${intnumber(value:user.loansaldo)}</td>
    </g:if><g:if test="${session.user.cashaccess==2}">
      <td>${intnumber(value:Department.get(user.department_id)?.cashsaldo?:0)}</td>
      <td>${intnumber(value:User.findAllByDepartment_idAndModstatus(user.department_id,1).sum{it.saldo}?:0)}</td>
    </g:if><g:if test="${session.user.cashaccess==3 || session.user.cashaccess==5}">  
      <td>${intnumber(value:Holding.get(1)?.cashsaldo?:0)}</td>
      <td>${intnumber(value:Holding.findByName('storesaldo')?.cashsaldo?:0)}</td>
    </g:if><g:if test="${session.user.group.is_payt}">
      <td>${number(value:paytsaldo)}</td>
      <td>${number(value:dopcardsaldo)}</td>
    </g:if>
    </tr>
  </tbody>
</table>
