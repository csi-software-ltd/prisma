<html>
  <head>
    <title>${infotext?.title?:'Prisma - Зарплаты'}</title>
    <meta name="layout" content="main" />
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
        $('sallink'+iStatus).addClassName('active');
        if (iStatus=='10') $('filter').hide();
        else $('filter').show();
      }
      function updatebuttons(sDate){
        $('addbuttons').innerHTML = ''
        <g:remoteFunction url="${[controller:controllerName,action:'getaddbuttons']}" params="'date='+sDate" update="addbuttons"/>
      }
      function updateoffbuttons(sDate){
        $('addoffbuttons').innerHTML = ''
        <g:remoteFunction url="${[controller:controllerName,action:'getaddoffbuttons']}" params="'date='+sDate" update="addoffbuttons"/>
      }
      function updateInn(sId,sInn){
        <g:remoteFunction url="${[controller:controllerName,action:'updatebuhinn']}" params="'salcomp_id='+sId+'&salinn='+sInn" onSuccess="\$('form_submit_button').click();"/>
      }
      function updateSnils(sId,sSnils){
        <g:remoteFunction url="${[controller:controllerName,action:'updatebuhsnils']}" params="'salcomp_id='+sId+'&salsnils='+sSnils" onSuccess="processUpdateSnils(e,sId)"/>
      }
      function updateCardmain(sId,sSalary){
        <g:remoteFunction url="${[controller:controllerName,action:'updateoffcardmain']}" params="'salcomp_id='+sId+'&salmain='+sSalary.replace(/&nbsp;/g,'').replace(/ /g,'')" onSuccess="\$('form_submit_button').click();"/>
      }
      function processUpdateSnils(e,sId){
        if(!e.responseJSON.error){
          switch (e.responseJSON.perstatus) {
            case 0: $('stat'+sId).innerText='Не распознан'; $('stat'+sId).up('tr').addClassName('yellow'); break;
            case 1: $('stat'+sId).innerText='Привязано'; $('stat'+sId).up('tr').removeClassName('yellow'); break;
          }
        }
      }
      function keyintercept(event){
        if(event.keyCode==13||event.keyCode==18) { event.stop(); event.target.blur(); }
      }
      function deleteAvans(lId){
        if(confirm('Вы действительно хотите удалить авансовую ведомость?')) {
          <g:remoteFunction controller='salary' action='deleteavansreport' params="'id='+lId" onSuccess="\$('form_submit_button').click();" />
        }
      }
      function processIncertbuhResponse(e){
        if(!e.responseJSON.error){
          $('sallink2').click();
        } else {
          alert("${message(code:"error.not.unique.message",args:["Бухгалтерская ведомость","датой"])}");
        }
      }
      function resetbuhfilter(){
        $('company_name').value = '';
        $('pers_name').value = '';
        $('repdate').selectedIndex = 0;
        $('perstype').selectedIndex = 0;
        $('compstatus').selectedIndex = 0;
        $('perstatus').selectedIndex = 0;
        $('is_tax').checked = false;
      }
    </g:javascript>
  </head>
  <body onload="\$('sallink${inrequest?.salsection?:saltypes.find{session.user.group."$it.checkfield"}?.id?:0}').click()">
    <div class="tabs padtop fright">
    <g:each in="${saltypes}" var="type">
    <g:if test="${session.user.group."$type.checkfield"}">
      <g:remoteLink id="sallink${type.id}" before="setactivelink(${type.id})" url="${[controller:controllerName,action:type.action]}" update="filter"><i class="icon-${type.icon} icon-large"></i> ${type.shortname} </g:remoteLink>
    </g:if>
    </g:each>
    <g:if test="${user.is_leader}">
      <g:remoteLink id="sallink10" before="setactivelink(10)" url="${[controller:controllerName,action:'personal']}" update="list"><i class="icon-group icon-large"></i> Мои сотрудники </g:remoteLink>
    </g:if>
    </div>
    <div class="clear"></div>
    <div id="filter" class="padtop filter">
    </div>
    <div id="list"></div>
  </body>
</html>