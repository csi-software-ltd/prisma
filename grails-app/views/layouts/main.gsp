<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ru" lang="ru">
  <head>
    <title><g:layoutTitle default="Prisma" /></title>
    <meta http-equiv="content-language" content="ru" />
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />      
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />               
    <meta name="copyright" content="Prisma" />    
    <meta name="resource-type" content="document" />
    <meta name="document-state" content="dynamic" />
    <meta name="revisit" content="1" />
    <meta name="viewport" content="width=1000,maximum-scale=1.0" />     
    <meta name="robots" content="noindex,nofollow" />
    <!--<meta name="cmsmagazine" content="55af4ed6d7e3fafc627c933de458fa04" />-->
    <link rel="shortcut icon" href="${resource(file:'favicon.ico',absolute:true)}" type="image/x-icon" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'reset.css')}" type="text/css" />    
    <link rel="stylesheet" href="${resource(dir:'css',file:'grid.css')}" type="text/css" />  
    <link rel="stylesheet" href="${resource(dir:'css',file:'superfish.css')}" type="text/css" />      
    <link rel="stylesheet" href="${resource(dir:'css',file:'style.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'font-awesome.min.css')}" type="text/css" />    
    <g:layoutHead />
    <g:javascript library="jquery-1.10.1.min" />
    <g:javascript library="superfish.min" />
    <g:javascript library="application" />
    <g:javascript library="prototype/prototype" />
    <!--[if lt IE 7]>
  		<div class='aligncenter'><a href="http://www.microsoft.com/windows/internet-explorer/default.aspx?ocid=ie6_countdown_bannercode"><img src="http://storage.ie6countdown.com/assets/100/images/banners/warning_bar_0000_us.jpg" border="0"></a></div>  
    <![endif]-->
    <!--[if lt IE 9]>    
      <g:javascript library="html5" />
      <link rel="stylesheet" href="${resource(dir:'css',file:'ie.css')}" type="text/css" />   		  		
    <![endif]-->    
    <r:layoutResources/>
  </head>
  <body onload="${pageProperty(name:'body.onload')}">    
    <header>
      <div class="main">
        <a class="logo ${user?'mini':''}" title="Prisma — главная страница" href="${createLink(controller:'user',action:'panel')}">Prisma</a>
        <g:if test="${!user}"><h1 align="center" class="fleft">Prisma приложение</h1></g:if>
        <div class="user inline fright">
        <g:if test="${user}">
          <span class="icon-lock icon-light"></span> <span class="user-login" id="user">${user?.name?:''}</span>
          <a class="icon-question-sign icon-large icon-light" title="Вопрос администратору" href="${notice.question_count==1?createLink(controller:'feedback',action:'question',id:notice.question_id):createLink(controller:'feedback',action:'index',params:[feedsection:(session.user.group.is_superuser?1:0)])}">
          <g:if test="${notice.question_count}">
            <div class="new">${notice.question_count}</div>
          </g:if>
          </a>
        <g:if test="${session.user.group.is_clientpaynew}">
          <g:link class="user-login" controller="payment" action="newclientpayment">Новый платеж&nbsp;<i class="icon-angle-right icon-large"></i></g:link>
        </g:if>
        <g:if test="${session.user.cashaccess in 2..3}">
          <span class="user-login" style="margin-left:30px">Касса: ${intnumber(value:notice.cashsaldo)}<i class="icon-rub icon-light"></i></span>
        </g:if>
        <g:if test="${session.user.accesslevel==1}">
        <g:if test="${context.systemstatus==1}">
          <g:remoteLink class="icon-unlock icon-1x icon-light" before="if(!confirm('Вы уверены, что хотите заблокировать систему?')) return false" url="${[controller:'user',action:'updatesystemstatus',params:[status:0]]}" title="Заблокировать систему" onSuccess="location.reload(true)"></g:remoteLink>
        </g:if><g:else>
          <g:remoteLink class="icon-lock icon-1x icon-light" before="if(!confirm('Разблокировать систему?')) return false" url="${[controller:'user',action:'updatesystemstatus',params:[status:1]]}" title="Разблокировать систему" onSuccess="location.reload(true)"></g:remoteLink>
        </g:else>
        </g:if>
          <a class="icon-signout icon-1x icon-light" title="Выход" href="${g.createLink(controller:'user',action:'logout')}"> </a>
        </g:if><g:else>&nbsp;</g:else>
        </div>
        <div class="clear"></div>
      </div>
    </header>
  <g:if test="${user}">
    <nav>
      <div class="main">
        <ul class="sf-menu">
        <g:each in="${session.user.menu}" var="item">
        <g:if test="${item.is_main}">
          <li class="${controllerName==item.controller?'current':''}">
            <g:link controller="${item.controller}" action="${item.action}">${item?.name}</g:link>
            <ul style="display:none">
            <g:if test="${item.controller=='company'}">
              <li><g:link controller="${item.controller}" action="${item.action}">Холдинг</g:link></li>
              <li><g:link controller="${item.controller}" action="${item.action}" params="[is_holding:2]">Внешние</g:link></li>
              <li><g:link controller="${item.controller}" action="${item.action}" params="[is_inactive:1]">Архив</g:link></li>
            </g:if><g:elseif test="${item.controller=='cash'}">
            <g:if test="${!(session.user.cashaccess in [2,3,5,7])}">
              <li><g:link controller="${item.controller}" action="${item.action}">Мои операции</g:link></li>
            </g:if>
            <g:if test="${session.user.cashaccess!=7}">
              <li><g:link controller="${item.controller}" action="${item.action}" params="[cashsection:3]">Отчеты</g:link></li>
            </g:if>
            <g:if test="${session.user.cashaccess in [1,2,3,6,7]}">
              <li><g:link controller="${item.controller}" action="${item.action}" params="[cashsection:1]">Заявки</g:link></li>
            </g:if>
            <g:if test="${session.user.cashaccess in [3,4,5]}">
              <li><g:link controller="${item.controller}" action="${item.action}" params="[cashsection:2]">Пополнение кассы</g:link></li>
            </g:if>
            <g:if test="${session.user.cashaccess in [3,5,6,7]}">
              <li><g:link controller="${item.controller}" action="${item.action}" params="[cashsection:4]">Главная касса</g:link></li>
            </g:if>
            <g:if test="${session.user.cashaccess in [2,3,5,6]}">
              <li><g:link controller="${item.controller}" action="${item.action}" params="[cashsection:5]">Касса отдела</g:link></li>
            </g:if>
            </g:elseif><g:elseif test="${item.controller=='agreement'}">
              <g:each in="${Agreementtype.list()}" var="type">
              <g:if test="${session.user.group."$type.checkfield"}">
                <li><g:link controller="${item.controller}" action="${item.action}" params="[agrobject:type.id]">${type.name}</g:link></li>
              </g:if>
              </g:each>
            </g:elseif><g:elseif test="${item.controller=='task'}">
              <li><g:link controller="${item.controller}" action="${item.action}">Задания к исполнению</g:link></li>
              <g:if test="${session.user.group.is_taskmy}">
                <li><g:link controller="${item.controller}" action="${item.action}" params="[taskobject:1]">Мои задания</g:link></li>
              </g:if>
              <g:if test="${session.user.group.is_taskall}">
                <li><g:link controller="${item.controller}" action="${item.action}" params="[taskobject:2]">Все задания</g:link></li>
              </g:if>
              <g:if test="${session.user.group.is_taskpay}">
                <li><g:link controller="${item.controller}" action="${item.action}" params="[taskobject:3]">Плановые платежи</g:link></li>
              </g:if>
              <g:if test="${session.user.group.is_enquiry}">
                <li><g:link controller="${item.controller}" action="${item.action}" params="[taskobject:4]">Справки</g:link></li>
              </g:if>
              <g:if test="${session.user.group.is_arenda}">
                <li><g:link controller="${item.controller}" action="${item.action}" params="[taskobject:5]">Продление аренды</g:link></li>
              </g:if>
            </g:elseif><g:elseif test="${item.controller=='salary'}">
              <g:each in="${Salarytype.findAllByShortnameNotEqual('')}" var="type">
              <g:if test="${session.user.group."$type.checkfield"}">
                <li><g:link controller="${item.controller}" action="${item.action}" params="[salsection:type.id]">${type.shortname}</g:link></li>
              </g:if>
              </g:each>
              <g:if test="${session.user.is_leader}">
                <li><g:link controller="${item.controller}" action="${item.action}" params="[salsection:10]">Мои сотрудники</g:link></li>
              </g:if>
            </g:elseif><g:elseif test="${item.controller=='report'}">
              <g:each in="${Reportgroup.list()}" var="group">
              <g:if test="${session.user.group."$group.checkfield"}">
                <li><g:link controller="${item.controller}" action="${group.action}">${group.groupname}</g:link></li>
              </g:if>
              </g:each>
            </g:elseif>
            <g:elseif test="${item.controller=='payment'}">
              <g:if test="${session?.user?.group?.is_payplan}">
                <li><g:link controller="${item.controller}" action="${item.action}" params="[paymentobject:1]">Фактические платежи</g:link></li>
              </g:if>
              <g:if test="${session.user.group.is_clientpayment}">
                <li><g:link controller="${item.controller}" action="${item.action}" params="[paymentobject:3]">Клиентские платежи</g:link></li>
              </g:if>
              <g:if test="${session.user.group.is_payordering}">
                <li><g:link controller="${item.controller}" action="${item.action}" params="[paymentobject:7]">Платежи по выписке</g:link></li>
              </g:if>
              <g:if test="${session.user.group.is_clientpayment}">
                <li><g:link controller="${item.controller}" action="${item.action}" params="[paymentobject:4]">Сделки</g:link></li>
                <li><g:link controller="${item.controller}" action="${item.action}" params="[paymentobject:12]">Средства банка</g:link></li>
              </g:if>
              <g:if test="${session.user.group.is_paysaldo}">
                <li><g:link controller="${item.controller}" action="${item.action}" params="[paymentobject:2]">Сверка остатков</g:link></li>
              </g:if>
              <g:if test="${session.user.group.is_paynalog}">
                <li><g:link controller="${item.controller}" action="${item.action}" params="[paymentobject:5]">Бюджетные платежи</g:link></li>
              </g:if>
              <g:if test="${session.user.group.is_payt}">
                <li><g:link controller="${item.controller}" action="${item.action}" params="[paymentobject:6]">Платежи отдела Т</g:link></li>
              </g:if>
              <g:if test="${session.user.group.is_payproject}">
                <li><g:link controller="${item.controller}" action="${item.action}" params="[paymentobject:8]">Проектные платежи</g:link></li>
              </g:if>
              <g:if test="${session.user.group.is_dopcardpayment}">
                <li><g:link controller="${item.controller}" action="${item.action}" params="[paymentobject:9]">Доп. карты. Выплаты</g:link></li>
                <li><g:link controller="${item.controller}" action="${item.action}" params="[paymentobject:10]">Доп. карты. Поступления</g:link></li>
                <li><g:link controller="${item.controller}" action="${item.action}" params="[paymentobject:11]">Доп. карты. Комиссии</g:link></li>
              </g:if>
            </g:elseif>
            </ul>
          </li>
        </g:if>
        </g:each>
          <li class="${(session.user?.addit?:[])?.contains(action_id)?'current':''}">
            <a href="javascript:void(0)">${(session.user?.addit?:[])?.contains(action_id)?session.user?.menu.find{it.id==action_id}?.name:'Доп. меню'}</a>
            <ul style="display:none">
            <g:each in="${session.user?.menu}" var="item">
            <g:if test="${!item.is_main}">
              <li><g:link controller="user" action="menu" params="[menu:item.id]">${item.name}</g:link></li>
            </g:if>
            </g:each>
            </ul>
          </li>
        </ul>
      </div>
    </nav>
    <div class="clear"></div>
  </g:if>
    <section id="content">
      <div class="container_12">
        <g:if test="${session.attention_message!='' && session.attention_message!=null}"> 
          <div class="info-box">
            <span class="icon icon-info-sign icon-3x"></span>
            ${session.attention_message}
          </div>    
        </g:if>
        <g:layoutBody />
        <div class="clear"></div>
      </div>
    </section>
  <g:if test="${user}"> 
    <div class="notifier-panel">      
    <g:if test="${session.user.menu.find{it.id==8}}">
      <g:if test="${session.user.cashaccess in 3..5}">
        <a class="icon-money icon-light" title="Касса" href="${notice.cash_count==1?createLink(controller:'cash',action:'cashrequest',id:notice.cash_id):createLink(controller:'cash',action:'index',params:[cashsection:2])}">
        <g:if test="${notice.cash_count}">
          <div class="new">${notice.cash_count}</div>
        </g:if>
        </a>
      <g:if test="${session.user.cashaccess==3}">
        <a class="icon-bell-alt icon-light" title="Подотчеты" href="${notice.cashreport_count==1?createLink(controller:'cash',action:'cashreport',id:notice.cashreport_id):createLink(controller:'cash',action:'index',params:[cashsection:3])}">
        <g:if test="${notice.cashreport_count}">
          <div class="new">${notice.cashreport_count}</div>
        </g:if>
        </a>
        <a class="icon-tags icon-light" title="Новые заявки" href="${notice.cashzakaz_count==1?createLink(controller:'cash',action:'detail',id:notice.cashzakaz_id):createLink(controller:'cash',action:'index',params:[cashsection:1])}">
        <g:if test="${notice.cashzakaz_count}">
          <div class="new">${notice.cashzakaz_count}</div>
        </g:if>
        </a>
      </g:if>
    </g:if><g:elseif test="${session.user.cashaccess<3}">
      <g:if test="${session.user.cashaccess>0}">
        <a class="icon-money icon-light" title="Касса" href="${notice.cashzakaz_count==1?createLink(controller:'cash',action:'detail',id:notice.cashzakaz_id):createLink(controller:'cash',action:'index',params:[cashsection:1])}">
        <g:if test="${notice.cashzakaz_count}">
          <div class="new">${notice.cashzakaz_count}</div>
        </g:if>
        </a>
      </g:if>
        <a class="icon-bell-alt icon-light" title="Подотчеты" href="${notice.cashreport_count==1?createLink(controller:'cash',action:'cashreport',id:notice.cashreport_id):createLink(controller:'cash',action:'index',params:[cashsection:3])}">
        <g:if test="${notice.cashreport_count}">
          <div class="new">${notice.cashreport_count}</div>
        </g:if>
        </a>
      </g:elseif>
    </g:if>
      <g:if test="${session.user.group.is_task}">
        <a class="icon-flag icon-large icon-light" title="Задания" href="${notice.task.size()==1?createLink(controller:'task',action:'taskdetail',id:notice.task[0].id):createLink(controller:'task',action:'index')}">
        <g:if test="${notice.task.size()>0}">
          <div class="new">${notice.task.size()}</div>
        </g:if>
        </a>
      </g:if>
      <g:if test="${session.user.group.is_salaryapprove}">
        <a class="icon-table icon-light" title="Зарплатные ведомости" href="${notice.salaryreport_count==1?createLink(controller:'salary',action:'cashreport',id:notice.salaryreport_id):createLink(controller:'salary',action:'index',params:[salsection:5])}">
        <g:if test="${notice.salaryreport_count}">
          <div class="new">${notice.salaryreport_count}</div>
        </g:if>
        </a>
      </g:if>
      <g:if test="${session.user.group.is_salarynaledit}">
        <a class="icon-table icon-light" title="Отклоненные ведомости" href="${notice.cashsalarydecreport_count==1?createLink(controller:'salary',action:'cashreport',id:notice.cashsalarydecreport_id):notice.salarydecreport_count==1?createLink(controller:'salary',action:'avans',id:notice.salarydecreport_id):notice.salarydecreport_count>0?createLink(controller:'salary',action:'index',params:[salsection:1]):createLink(controller:'salary',action:'index',params:[salsection:5])}">
        <g:if test="${notice.salarydecreport_count+notice.cashsalarydecreport_count>0}">
          <div class="new">${notice.salarydecreport_count+notice.cashsalarydecreport_id}</div>
        </g:if>
        </a>
      </g:if>
      <g:if test="${session.user.group.is_payaccept}">
        <a class="icon-tasks icon-light" title="Неутвержденные задания" href="${notice.taskpay_count==1?createLink(controller:'task',action:'taskpaydetail',id:notice.taskpay_id):createLink(controller:'task',action:'index',params:[taskobject:3,is_accept:0])}">
        <g:if test="${notice.taskpay_count}">
          <div class="new">${notice.taskpay_count}</div>
        </g:if>
        </a>
      </g:if>
      <g:if test="${session.user.group.is_payplanexec}">
        <a class="icon-cogs icon-light" title="Задания на плановые платежи" href="${notice.taskpaynotassign_count==1?createLink(controller:'task',action:'taskpaydetail',id:notice.taskpaynotassign_id):createLink(controller:'task',action:'index',params:[taskobject:3])}">
        <g:if test="${notice.taskpaynotassign_count}">
          <div class="new">${notice.taskpaynotassign_count}</div>
        </g:if>
        </a>
      </g:if>
      <g:if test="${session.user.group.is_prolongwork}">
        <a class="icon-hospital icon-light" title="Задания на продление аренды" href="${notice.spaceprolong_count==1?createLink(controller:'task',action:'spaceprolong',id:notice.spaceprolong_id):createLink(controller:'task',action:'index',params:[taskobject:5])}">
        <g:if test="${notice.spaceprolong_count}">
          <div class="new">${notice.spaceprolong_count}</div>
        </g:if>
        </a>
      </g:if>
    </div>
  </g:if>
    <r:layoutResources/>
  <g:if test="${user}">
    <script type="text/javascript">
      jQuery(window).scroll(function(){
        if(jQuery(this).scrollTop()>44)
          jQuery("nav").css({position:'fixed',top:0,width:'100%'});
        else if(jQuery(this).scrollTop()==0)
          jQuery("nav").css('position','relative');        
      });
      jQuery(document).ready(function(){
        jQuery('ul.sf-menu').superfish({
          hoverClass:'sfHover',
          pathClass:'active',
          delay:300,
          animation:{height:'show'},
          speed:'def',
          cssArrows:false,
          autoArrows:false,
          dropShadows:1
        });
      });
    </script>
  </g:if>
  </body>
</html>
