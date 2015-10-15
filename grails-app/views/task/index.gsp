<html>
  <head>
    <title>${infotext?.title?:'Prisma - Задания'}</title>
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
      function setactivelink(iStatus){
        jQuery('.tabs a[class="active"]').removeClass('active');
        $('tasklink'+iStatus).addClassName('active');
        if([2,3,4,5].indexOf(iStatus)>-1)
          $("filter").addClassName('filter');
        else  
          $("filter").removeClassName('filter');
      }
      function resetdata(){
        $('tid').value = '';
        $('company').value = '';
        $('termdate').value = '';
        $('taskpaystatus').selectedIndex = 0;
        $('is_accept').selectedIndex = 0;
        $('paygroup').selectedIndex = 0;
        if($('executor')) $('executor').value = '';
      }
      function resetEnquiryForm(){
        $('company_name').value = '';
        $('inputdate_start').value = '';
        $('inputdate_end').value = '';
        $('termdate').value = '';
        $('ondate').value = '';
        $('bank_id').selectedIndex = 0;
        $('taxinspection_id').selectedIndex = 0;
        $('whereto').selectedIndex = 0;
        $('modstatus').selectedIndex = 0;
      }
      function resetSpaceprolongForm(){
        $('permitstatus').selectedIndex = 0;
      }
      function getExecutor(department_id){      
        <g:remoteFunction controller='task' action='executor' params="'department_id='+department_id+'&all=1'" update="executor_span" />;
      }
    </g:javascript>
  </head>
  <body onload="\$('tasklink${inrequest?.taskobject?:0}').click();">
    <div class="tabs padtop fright">
      <g:remoteLink id="tasklink0" before="setactivelink(0)" url="${[controller:controllerName,action:'taskfilter']}" update="filter"><i class="icon-list icon-large"></i> Задания к исполнению </g:remoteLink>
      <g:if test="${ismy}"><g:remoteLink id="tasklink1" before="setactivelink(1)" url="${[controller:controllerName, action:'taskmyfilter']}" update="filter"><i class="icon-list icon-large"></i> Мои задания </g:remoteLink></g:if>
      <g:if test="${isall}"><g:remoteLink id="tasklink2" before="setactivelink(2)" url="${[controller:controllerName, action:'taskallfilter']}" update="filter"><i class="icon-list icon-large"></i> Все задания </g:remoteLink></g:if>
      <g:if test="${session?.user?.group?.is_taskpay}"><g:remoteLink id="tasklink3" before="setactivelink(3)" url="${[controller:controllerName,action:'taskpayfilter']}" update="filter"><i class="icon-list icon-large"></i> Задания на плановые платежи </g:remoteLink></g:if>
      <g:if test="${isenquiry}"><g:remoteLink id="tasklink4" before="setactivelink(4)" url="${[controller:controllerName,action:'enquiryfilter']}" update="filter"><i class="icon-list icon-large"></i> Справки </g:remoteLink></g:if>
      <g:if test="${isspprolong}"><g:remoteLink id="tasklink5" before="setactivelink(5)" url="${[controller:controllerName, action:'spaceprolongfilter']}" update="filter"><i class="icon-list icon-large"></i> Продление аренды </g:remoteLink></g:if>
    </div>
    <div class="clear"></div>
    <div id="filter" class="padtop ${(inrequest?.taskobject in 2..4)?'filter':''}">
    </div>
    <div id="list"></div>
  </body>
</html>