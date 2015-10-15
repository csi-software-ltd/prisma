<html>
  <head>
    <title>${infotext?.title?:'Prisma - Отчеты - '+reportgroup.groupname}</title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
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
      function setactivelink(iStatus){
        jQuery('.tabs a[class="active"]').removeClass('active');
        $('replink'+iStatus).addClassName('active');
      }
      function getBankList(sId,sType){
        <g:remoteFunction controller='report' action='docbanklist' params="'company_id='+sId+'&type='+sType" update="banklist" />
      }
      function updatepersspan(sDepartment){
        <g:remoteFunction controller='report' action='perslist' params="'department_id='+sDepartment" update="persspan" />
      }
      function toggledate(iType){
        if(iType<2) $('datesection').show();
        else $('datesection').hide();
      }
      function getCompanyList(sId){
        <g:remoteFunction controller='report' action='bankcompanylist' params="'bank_id='+sId" update="companylist" />
      }
      function resetKreditfolioFilter(){
        $('kreditfoliodate').value = $('kreditfoliodate').defaultValue;
        $('kreditdate_start').value = '';
        $('kreditdate_end').value = '';
        $('bank_id').selectedIndex = 0;
        $('zalog_id').selectedIndex = 0;
        $('responsible').selectedIndex = 0;
        $('is_agr').checked = false;
        $('is_nolicense').checked = false;
        $('is_debt').checked = false;
        $('is_active').checked = false;
        getCompanyList('');
      }
      function resetKreditcompanyFilter(){
        $('reportdate').value = $('reportdate').defaultValue;
        $('stopdate').value = '';
        $('company_id').selectedIndex = 0;
      }
      function resetSpacefolioFilter(){
        $('spacefoliodate').value = $('spacefoliodate').defaultValue;
        $('spacedate_start').value = '';
        $('spacedate_end').value = '';
        $('company_id').selectedIndex = 0;
      }
    </g:javascript>
  </head>
  <body onload="\$('replink${reports.find{session.user.group."$it.checkfield"}?.id?:0}').click()">
    <div class="tabs padtop fright">
    <g:each in="${reports}" var="report">
    <g:if test="${session.user.group."$report.checkfield"}">
      <g:remoteLink id="replink${report.id}" before="setactivelink(${report.id})" url="${[controller:controllerName, action:report.action]}" update="filter"><i class="icon-${report.icon} icon-large"></i> ${report.name} </g:remoteLink>
    </g:if>
    </g:each>
    </div>
    <div class="clear"></div>
    <div id="filter" class="padtop filter">
    </div>
    <div id="list"></div>
  </body>
</html>