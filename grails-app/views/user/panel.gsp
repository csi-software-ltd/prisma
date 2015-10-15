<html>
  <head>
    <title>Prisma приложение</title>
    <meta name="layout" content="main" />
    <g:javascript>
      function viewCell(iNum){
        var tabs = jQuery('.nav').find('li');
        for(var i=0; i<tabs.length; i++){
          if(i==iNum)
            tabs[i].addClassName('selected');
          else
            tabs[i].removeClassName('selected');
        }

        switch(iNum){
          case 0: getTasks();break;
          case 1: getSaldo();break;
          case 2: getKredits();break;
          case 3: getPersonal();break;
          case 4: getDebt();break;
          case 5: getSalary();break;
          case 6: getAccounts();break;
        }
      }
      function getTasks(){
        $('tasks_submit_button').click();
      }
      function getSaldo(){
        $('saldo_submit_button').click();
      }
      function getKredits(){
        $('kredits_submit_button').click();
      }
      function getPersonal(){
        $('personal_submit_button').click();
      }
      function getDebt(){
        $('debt_submit_button').click();
      }
      function getSalary(){
        $('salary_submit_button').click();
      }
      function getAccounts(){
        $('accounts_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
      }
    </g:javascript>
  </head>  
  <body onload="init()">  
    <div class="grid_6">
      <h3>Курсы валют</h3>
      <label class="auto">Сегодня:</label>
      <input type="text" readonly value="${g.formatDate(date:new Date(),format:'dd.MM.yyyy HH:mm')}" style="width:auto" />
      <table class="list">
        <tbody> 
        <g:each in="${rates_current}" var="item" status="i">        
          <tr>
            <th width="30" <g:if test="${i==0}">class="round"</g:if>><i class="icon-${item.code.toLowerCase()} icon-light"></i></th>
            <td <g:if test="${i==0}">class="round"</g:if>><input type="text" class="nopad" readonly value="${number(value:item.vrate,fdigs:4)}" /></td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>  
    <div class="grid_6">      
    <g:if test="${rates_next}">   
      <h3>&nbsp;</h3>
      <label class="auto">Завтра:</label>
      <input type="text" readonly value="${formatDate(date:new Date()+1,format:'dd.MM.yyyy HH:mm')}" style="width:auto" />
      <table class="list">
        <tbody> 
        <g:each in="${rates_next}" var="item" status="i">        
          <tr>
            <th width="30" <g:if test="${i==0}">class="round"</g:if>><i class="icon-${item.code.toLowerCase()} icon-light"></i></th>
            <td <g:if test="${i==0}">class="round"</g:if>><input type="text" class="nopad" readonly value="${number(value:item.vrate,fdigs:4)}" /></td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </g:if>  
    </div>
    <div class="clear" style="padding-bottom:10px"></div>
    <div class="tabs">
      <ul class="nav">
        <li><a href="javascript:void(0)" onclick="viewCell(0)">Задания</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(1)">Остатки средств</a></li>
        <li style="${!session.user.group.is_kreditinfo?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(2)">Новые кредиты</a></li>
        <li style="${!user.is_leader?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(3)">Мои сотрудники</a></li>
        <li style="${session.user.cashaccess!=2?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(4)">Подотчет</a></li>        
        <li><a href="javascript:void(0)" onclick="viewCell(5)">История назначений</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(6)">Персональные счета</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="tasksForm" url="[action:'tasks']" update="details">
      <input type="submit" class="button" id="tasks_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="saldoForm" url="[action:'saldo']" update="details">
      <input type="submit" class="button" id="saldo_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="kreditsForm" url="[action:'kredits']" update="details">
      <input type="submit" class="button" id="kredits_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="personalForm" url="[action:'personal']" update="details">
      <input type="submit" class="button" id="personal_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="debtForm" url="[action:'debt']" update="details">
      <input type="submit" class="button" id="debt_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="salaryForm" url="[action:'salary']" update="details">
      <input type="submit" class="button" id="salary_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="accountsForm" url="[action:'accounts']" update="details">
      <input type="submit" class="button" id="accounts_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </body>
</html>
