<html>
  <head>
    <title>${infotext?.title?:'Prisma - Справочники'}</title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />    
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
        $('cataloglink'+iStatus).addClassName('active');
      }
      function hideMessage(sVar){
        if ($("infolist"+sVar)) $("infolist"+sVar).up('div').hide();
        $("errorlist"+sVar).innerHTML='';
        $("errorlist"+sVar).up('div').hide();
      }
      function showSpaceWindow(iId){       
        hideMessage('Space');        
        <g:remoteFunction controller='catalog' action='spacedetail' update='createspace' params="'id='+iId"/>
        $("link").hide();
        $('createspace').show();        
      }
      function hideSpaceWindow(){
        hideMessage('Space');
        $("link").show();
        $('createspace').hide();             
      }
      function processSpaceResponse(e){
        $('name').removeClassName('red');
        if (e.responseJSON.error){
          var sErrorMsg='';
          if (e.responseJSON.errorcode){
            e.responseJSON.errorcode.forEach(function(err){
              switch (err) {                                                                                                          
                case 1: sErrorMsg='<li>Введите название типа помещений</li>'; $('name').addClassName('red'); break;
              }
            });  
            $("infolistSpace").up('div').hide();
            $("errorlistSpace").innerHTML=sErrorMsg;
            $("errorlistSpace").up('div').show();
          }  
        }else{
          location.assign('${createLink(controller:'catalog',action:'index')}'+'/?fromDetails=1');          
        }                      
      }
      function resetBankFilter(){
        $("bank_id").value='';
        $("bankname").value='';
        jQuery('#is_my').prop("checked",false);       
        jQuery('#is_license').prop("checked",true);              
      }
      function showHolidayWindow(iId){       
        hideMessage('Holiday');
        $("allForm").hide();        
        <g:remoteFunction controller='catalog' action='holidaydetail' update='createHoliday' params="'id='+iId"/>
        $('createHoliday').show();        
      }
      function hideHolidayWindow(){
        hideMessage('Holiday');        
        $("allForm").show();
        $('createHoliday').hide();             
      }
      function processHolidayResponse(e){      
        $('hdate').removeClassName('red');
       
        if (e.responseJSON.error){        
          var sErrorMsg='';         
          if(e.responseJSON.errorcode.length){          
            e.responseJSON.errorcode.forEach(function(err){
              switch (err) {                                                               
                case 1: sErrorMsg+='<li>Не заполнено обязательное поле "Дата"</li>'; $('hdate').addClassName('red'); break;  
                case 2: sErrorMsg+='<li>Нельзя назначить праздник на выходной день</li>'; $('hdate').addClassName('red'); break;  
                case 3: sErrorMsg+='<li>Нельзя назначить рабочий день на будни</li>'; $('hdate').addClassName('red'); break;                 
              }
            });                          
            $("infolistHoliday").up('div').hide();
            $("errorlistHoliday").innerHTML=sErrorMsg;
            $("errorlistHoliday").up('div').show();
          }  
        }else{
          location.assign('${createLink(controller:'catalog',action:'index')}'+'/?fromDetails=1');          
        }                      
      }
      function showAgreementtypeWindow(iId){       
        hideMessage('Agreementtype');
        $("allForm").hide();        
        <g:remoteFunction controller='catalog' action='agreementtypedetail' update='createAgreementtype' params="'id='+iId"/>
        $('createAgreementtype').show();        
      }      
      function hideAgreementtypeWindow(){
        hideMessage('Agreementtype');        
        $("allForm").show();
        $('createAgreementtype').hide();             
      }
      function processAgreementtypeResponse(e){      
        $('name').removeClassName('red');
       
        if (e.responseJSON.error){        
          var sErrorMsg='';         
          if(e.responseJSON.errorcode.length){          
            e.responseJSON.errorcode.forEach(function(err){
              switch (err) {                                                               
                case 2: sErrorMsg+='<li>Не заполнено обязательное поле "Название"</li>'; $('name').addClassName('red'); break;                             
              }
            });                          
            $("infolistAgreementtype").up('div').hide();
            $("errorlistAgreementtype").innerHTML=sErrorMsg;
            $("errorlistAgreementtype").up('div').show();
          }  
        }else{
          location.assign('${createLink(controller:'catalog',action:'index')}'+'/?fromDetails=1');          
        }                      
      }                  
      function showCompositionWindow(iId){       
        hideMessage('Composition');
        $("link").hide();    
        <g:remoteFunction controller='catalog' action='compositiondetail' update='createcomposition' params="'id='+iId"/>
        $('createcomposition').show();        
      }      
      function hideCompositionWindow(){
        hideMessage('Composition');        
        $("link").show();
        $('createcomposition').hide();             
      }
      function processCompositionResponse(e){
        var sErrorMsg = '';
        ['name','position_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {                                                              
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Название"])}</li>'; $('name').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип"])}</li>'; $('position_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlistComposition").innerHTML=sErrorMsg;
          $("errorlistComposition").up('div').show();       
        }else{
          location.assign('${createLink(controller:'catalog',action:'index')}'+'/?fromDetails=1');
        }
      }
      function resetCompositionFilter(){
        $('composition_name').value='';
        $('position_id').selectedIndex=0;
      }
      function resetExprazdelFilter(){
        $('exprazdel_name').value='';
      }
      function resetOutsourceFilter(){
        $('outsource_name').value='';
        $('modstatus').selectedIndex=0;
      }
      function resetZalogtypeFilter(){
        $('zalogtype_name').value='';
      }
      function resetCgroupFilter(){
        $('cgroup_name').value='';
      }
      function resetVisgroupFilter(){
        $('visgroup_name').value='';
      }
    </g:javascript>
  </head>
  <body onload="\$('cataloglink${inrequest?.catobject?:0}').click();">
    <div class="tabs padtop fright">
      <g:remoteLink id="cataloglink0" before="setactivelink(0)" url="${[controller:controllerName,action:'bankfilter']}" update="filter"><i class="icon-list icon-large"></i> Банки </g:remoteLink>
      <g:remoteLink id="cataloglink1" before="setactivelink(1)" url="${[controller:controllerName,action:'taxinspectionfilter']}" update="filter"><i class="icon-list icon-large"></i> Налоговые </g:remoteLink>
      <g:remoteLink id="cataloglink2" before="setactivelink(2)" url="${[controller:controllerName,action:'okvedfilter']}" update="filter"><i class="icon-list icon-large"></i> ОКВЭД </g:remoteLink>
      <g:remoteLink id="cataloglink3" before="setactivelink(3)" url="${[controller:controllerName,action:'oktmofilter']}" update="filter"><i class="icon-list icon-large"></i> ОКТМО </g:remoteLink>
      <g:remoteLink id="cataloglink4" before="setactivelink(4)" url="${[controller:controllerName,action:'kbkfilter']}" update="filter"><i class="icon-list icon-large"></i> КБК </g:remoteLink>
      <g:remoteLink id="cataloglink5" before="setactivelink(5)" url="${[controller:controllerName, action:'exprazdelfilter']}" update="filter"><i class="icon-list icon-large"></i> Разделы расходов </g:remoteLink>
      <g:remoteLink id="cataloglink6" before="setactivelink(6)" url="${[controller:controllerName, action:'outsourcefilter']}" update="filter"><i class="icon-list icon-large"></i> Аутсорсеры </g:remoteLink>
      <g:remoteLink id="cataloglink8" before="setactivelink(8)" url="${[controller:controllerName,action:'spacefilter']}" update="filter"><i class="icon-list icon-large"></i> Помещения </g:remoteLink>
      <g:remoteLink id="cataloglink9" before="setactivelink(9)" url="${[controller:controllerName,action:'holidayfilter']}" update="filter"><i class="icon-list icon-large"></i> Праздники </g:remoteLink>
      <g:remoteLink id="cataloglink10" before="setactivelink(10)" url="${[controller:controllerName,action:'agreementtypefilter']}" update="filter"><i class="icon-list icon-large"></i> Договоры </g:remoteLink>
      <g:remoteLink id="cataloglink11" before="setactivelink(11)" url="${[controller:controllerName,action:'compositionfilter']}" update="filter"><i class="icon-list icon-large"></i> Должности </g:remoteLink>
      <g:remoteLink id="cataloglink7" before="setactivelink(7)" url="${[controller:controllerName, action:'zalogtypefilter']}" update="filter"><i class="icon-list icon-large"></i> Виды залога </g:remoteLink>
    <g:if test="${isenquiry}">
      <g:remoteLink id="cataloglink13" before="setactivelink(13)" url="${[controller:controllerName,action:'enqfilter']}" update="filter"><i class="icon-list icon-large"></i> Справки </g:remoteLink>
    </g:if>
    <g:if test="${iscgroup}">
      <g:remoteLink id="cataloglink12" before="setactivelink(12)" url="${[controller:controllerName,action:'cgroupfilter']}" update="filter"><i class="icon-list icon-large"></i> Группы компаний </g:remoteLink>
    </g:if>
    <g:if test="${isttype}">
      <g:remoteLink id="cataloglink14" before="setactivelink(14)" url="${[controller:controllerName,action:'tasktypefilter']}" update="filter"><i class="icon-list icon-large"></i> Типы задач </g:remoteLink>
    </g:if>
    <g:if test="${iscar}">
      <g:remoteLink id="cataloglink15" before="setactivelink(15)" url="${[controller:controllerName,action:'carfilter']}" update="filter"><i class="icon-list icon-large"></i> Машины </g:remoteLink>
    </g:if>
    <g:if test="${isvgroup}">
      <g:remoteLink id="cataloglink16" before="setactivelink(16)" url="${[controller:controllerName,action:'visgroupfilter']}" update="filter"><i class="icon-list icon-large"></i> Группы видимости </g:remoteLink>
    </g:if>
    </div>
    <div class="clear"></div>
    <div id="filter" class="padtop filter">
    </div>
    <div id="list"></div>
  </body>
</html>
