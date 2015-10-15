<html>
  <head>
    <title>${infotext?.title?:'Prisma - Касса'}</title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript>
      function clickPaginate(event){
        event.stop();
        var link = event.element();
        if(link.href == null){
          return;
        }
        new Ajax.Updater(
          { success: $('ajax_wrap') },
          link.href,
          { evalScripts: true });
      }
      function updatepersspan(sDepartment){
        <g:remoteFunction controller='cash' action='perslist' params="'department_id='+sDepartment" update="persspan" />
      }
      function repeatzakaz(sId){
        if(confirm('Отправить на повторное согласование?')) {
          <g:remoteFunction controller='cash' action='repeatzakaz' params="'id='+sId" onSuccess="\$('cashlink4').click()" />
        }
      }
      function getcashclasses(sType){
        <g:remoteFunction controller='cash' action='maincashclasslist' params="'type='+sType" update="cashclasssection"/>
      }
      function getpodrazdel(sRazdel){
        <g:remoteFunction controller='cash' action='exppodrazdellist' params="'razdel='+sRazdel" update="exppodrazdelsection"/>
        getexptypes('0');
      }
      function getexptypes(sPodrazdel){
        var razdel = $('exprazdel_id').value;
        <g:remoteFunction controller='cash' action='exptypelist' params="'razdel='+razdel+'&podrazdel='+sPodrazdel" update="exptypesection"/>
      }
      function toggleagent(sClass){
        return false;
      }
      function resetmaincashfilter(){
        $('opdatestart').value = '';
        $('opdateend').value = '';
        $('mcid').value = '';
        $('comment').value = '';
        $('exprazdel_id').selectedIndex = 0;
        $('department_id').selectedIndex = 0;
        $('maincashtype').selectedIndex = 0;
        getcashclasses(-100);
        getpodrazdel('0');
      }
      function resetreportfilter(){
        $('repdate').value = '';
        $('executor_name').value = '';
        $('exprazdel_id').selectedIndex = 0;
        $('repstatus').selectedIndex = 0;
        $('project_id').selectedIndex = 0;
        getpodrazdel('0');
      }
      function setactivelink(iStatus){
        jQuery('.tabs a[class="active"]').removeClass('active');
        $('cashlink'+iStatus).addClassName('active');
      }
    </g:javascript>
  </head>
  <body onload="\$('cashlink${inrequest?.cashsection?:session.user.cashaccess in [3,5,7]?4:session.user.cashaccess==2?5:6}').click();">
      <div class="tabs padtop fright">
      <g:if test="${!(session.user.cashaccess in [2,3,5,7])}">
        <g:remoteLink id="cashlink6" before="setactivelink(6)" url="${[controller:controllerName,action:'myoperationfilter']}" update="filter"><i class="icon-list icon-large"></i> Мои операции </g:remoteLink>
      </g:if>
      <g:if test="${session.user.cashaccess!=7}">
        <g:remoteLink id="cashlink3" before="setactivelink(3)" url="${[controller:controllerName,action:'reportfilter']}" update="filter"><i class="icon-list icon-large"></i> Отчеты </g:remoteLink>
      </g:if>
      <g:if test="${session.user.cashaccess in [1,2,3,6,7]}">
        <g:remoteLink id="cashlink1" before="setactivelink(1)" url="${[controller:controllerName,action:'zakazfilter']}" update="filter"><i class="icon-list icon-large"></i> Заявки </g:remoteLink>
      </g:if>
      <g:if test="${session.user.cashaccess in 3..5}">
        <g:remoteLink id="cashlink2" before="setactivelink(2)" url="${[controller:controllerName,action:'requestfilter']}" update="filter"><i class="icon-list icon-large"></i> Запросы на пополнение кассы </g:remoteLink>
      </g:if>
      <g:if test="${session.user.cashaccess in [3,5,6,7]}">
        <g:remoteLink id="cashlink4" before="setactivelink(4)" url="${[controller:controllerName,action:'maincashfilter']}" update="filter"><i class="icon-list icon-large"></i> Главная касса </g:remoteLink>
      </g:if>
      <g:if test="${session.user.cashaccess in [2,3,5,6]}">
        <g:remoteLink id="cashlink5" before="setactivelink(5)" url="${[controller:controllerName,action:'depcashfilter', params:inrequest]}" update="filter"><i class="icon-list icon-large"></i> Касса отдела </g:remoteLink>
      </g:if>
      </div>
      <div class="clear"></div>
      <div id="filter" class="padtop filter">
      </div>
    <div id="list"></div>
  </body>
</html>