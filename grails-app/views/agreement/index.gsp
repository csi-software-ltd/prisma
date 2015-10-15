<html>
  <head>
    <title>${infotext?.title?:'Prisma - Договора'}</title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript library="prototype/autocomplete" />
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
        $('agrlink'+iStatus).addClassName('active');
      }
      function resetloanfilter(){
        $('client_name').value="";
        $('lender_name').value="";
        $('loantype').selectedIndex=0;
        $('modstatus').selectedIndex=0;
      }
      function resetsmrfilter(){
        $('client_name').value="";
        $('supplier_name').value="";
        $('smrcat_id').selectedIndex=0;
        $('smrsort').selectedIndex=0;
        $('modstatus').selectedIndex=0;
      }
      function resetservicefilter(){
        $('sid').value="";
        $('zcompany_name').value="";
        $('ecompany_name').value="";
        $('dateend').value="";
        $('atype').selectedIndex=0;
        $('asort').selectedIndex=0;
        $('modstatus').selectedIndex=0;
      }
      function resetarendafilter(){
        $('sid').value="";
        $('address').value="";
        $('company_name').value="";
        $('enddate').value="";
        $('anumber').value="";
        $('arendatype_id').selectedIndex=0;
        $('spacetype_id').selectedIndex=0;
        $('is_nds').selectedIndex=0;
        $('modstatus').selectedIndex=0;
        $('debt').checked=false;
        $('is_adrsame').checked=false;
      }
      function resetindepositfilter(){
        $('indid').value="";
        $('client_id').selectedIndex=0;
        $('aclass').selectedIndex=0;
        $('modstatus').selectedIndex=0;
      }
      function resetdepositfilter(){
        $('did').value="";
        $('bankcompany_id').selectedIndex=0;
      }
      function resetlicensefilter(){
        $('company_id').value="";
        $('industry_id').selectedIndex=0;
      }
      function resetflfilter(){
        $('flid').value="";
        $('fl_company_name').value="";
        $('modstatus').selectedIndex=0;
      }
      function resetkreditfilter(){
        $('kid').value="";
        $('inn').value="";
        $('company_name').value="";
        $('bankname').value="";
        $('responsible').selectedIndex=0;
        $('zalogstatus').selectedIndex=0;
        $('valuta_id').selectedIndex=0;
        $('modstatus').selectedIndex=0;
        if ($('is_real')) $('is_real').checked=false;
        if ($('is_tech')) $('is_tech').checked=false;
        if ($('is_realtech')) $('is_realtech').checked=false;
        $('is_nocheck').checked=false;
        $('cessionstatus').checked=false;
      }
      function resetlizingfilter(){
        $('lid').value="";
        $('company_name').value="";
        $('responsible').selectedIndex=0;
        $('lizsort').selectedIndex=0;
        $('modstatus').selectedIndex=0;
        $('project_id').selectedIndex=0;
        $('car_id').selectedIndex=0;
        $('cessionstatus').checked=false;
      }
      function resetagentfilter(){
        $('anumber').value="";
        $('bankname').value="";
        $('modstatus').selectedIndex=0;
        $('client_id').selectedIndex=0;
      }
      function resetcessionfilter(){
        $('inn').value="";
        $('company_name').value="";
        $('agr_id').value="";
        $('bank_id').selectedIndex=0;
        $('valuta_id').selectedIndex=0;
        $('changetype').selectedIndex=0;
        $('modstatus').selectedIndex=0;
      }
      function resettradefilter(){
        $('inn').value="";
        $('company_name').value="";
        $('tradetype').selectedIndex=0;
        $('tradesort').selectedIndex=0;
        $('responsible').selectedIndex=0;
        $('modstatus').selectedIndex=0;
        $('debt').checked=false;
      }
    </g:javascript>
  </head>
  <body onload="\$('agrlink${inrequest?.agrobject?:agrtypes.find{session.user.group."$it.checkfield"}?.id?:0}').click()">
    <div class="tabs padtop fright">
    <g:each in="${agrtypes}" var="type">
    <g:if test="${session.user.group."$type.checkfield"}">
      <g:remoteLink id="agrlink${type.id}" before="setactivelink(${type.id})" url="${[controller:controllerName,action:type.action]}" update="filter"><i class="icon-${type.icon} icon-large"></i> ${type.name} </g:remoteLink>
    </g:if>
    </g:each>
    </div>
    <div class="clear"></div>
    <div id="filter" class="padtop filter">
    </div>
    <div id="list"></div>
  </body>
</html>