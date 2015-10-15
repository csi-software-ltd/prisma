<html>
  <head>
    <title>Prisma: <g:if test="${group}">Редактировать группу № ${group.id}</g:if><g:else>Добавление новой группы</g:else></title>
    <meta name="layout" content="main" />    
    <g:javascript>                  
      function init(){  
        <g:if test="${flash?.groupedit_success}">
          $("infolist").up('div').show();
        </g:if>
        <g:if test="${group}">
          getRights();
        </g:if>        
      }
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processGroupResponse(e){
        $("name").removeClassName('red');
        if (e.responseJSON.error){
          var sErrorMsg='';
          if (e.responseJSON.errorcode){            
             switch (e.responseJSON.errorcode) {                                               
              case 1: sErrorMsg='<li>Группа не создана!</li>'; break;
              case 2: sErrorMsg='<li>Tакая группа уже существует</li>'; break;              
              case 3: sErrorMsg='<li>Введите имя группы</li>'; $("name").addClassName('red'); break;
            }
            $("infolist").up('div').hide();
            $("errorlist").innerHTML=sErrorMsg;
            $("errorlist").up('div').show();
          }  
        }else{
          location.assign('${createLink(controller:'user',action:'groupdetail')}'+'/'+e.responseJSON.group_id);
        }                      
      }
      function proccesGroupRights(e){
        if (!e.responseJSON.error){
          $("infolist").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
          getRights();
        }  
        else{
          $("infolist").up('div').hide();
          $("errorlist").innerHTML='Ошибка сохранения прав!';
          $("errorlist").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        }        
      }
      function viewCell(iNum){                  
        var tabs = jQuery('.nav').find('li');
        for(var i=0; i<tabs.length; i++){
          if(i==iNum)
            tabs[i].addClassName('selected');
          else
            tabs[i].removeClassName('selected');
        }

        switch(iNum){
          case 0: getRights();break;
          case 1: getUsers();break;         
        }
      }
      function getRights(){
        if(${group?1:0}){    
          var iGroupId= ${group?.id?:0};       
          <g:remoteFunction controller='user' action='grouprights' update='details' params="'id='+iGroupId"/>;
        }  
      } 
      function getUsers(){
        if(${group?1:0}){ 
          var iGroupId= ${group?.id?:0};        
          <g:remoteFunction controller='user' action='groupuserlist' update='details' params="'usergroup_id='+iGroupId"/>;
        }  
      }
      function toggleUl(sFid){      
        if(jQuery("#"+sFid+"_ul").is(':hidden'))          
          jQuery('#'+sFid+"_ul").slideDown();
        else{
          jQuery("#"+sFid+"_ul input[type=checkbox]:checked").each(function() {
            jQuery(this).prop('checked', false);
          });         
          jQuery('#'+sFid+"_ul").slideUp();

          jQuery("#"+sFid+"_ul ul").each(function() {
            jQuery(this).hide();
          });          
        }
      }      
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      label.long{width:370px}
      input.normal{width:202px}      
      input.mini{width:60px!important}            
    </style>
  </head>
  <body onload="init();">
    <h3 class="fleft"><g:if test="${group}">Редактировать группу № ${group.id}</g:if><g:else>Добавление новой группы</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку групп</a>
    <div class="clear"></div>
    
    <div class="info-box" style="display:none;margin-top:0">
      <span class="icon icon-info-sign icon-3x"></span>
      <ul id="infolist">      
        <li>Изменения сохранены</li>
      </ul>
    </div>    
    <div class="error-box" style="display:none">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>
    <g:formRemote url="${[controller:'user',action:'saveGroupDetail',id:group?.id?:0]}" onSuccess="processGroupResponse(e)" method="POST" name="createGroupForm">           
      <label for="name">Имя:</label>
      <input type="text" name="name" id="name" class="fullline" value="${group?.name?:''}"/>                                              
      <label for="description">Описание:</label>
      <input type="text" name="description" id="description" class="fullline"  value="${group?.description?:''}"/>
      <label for="department_id">Отдел:</label>
      <g:select name="department_id" value="${group?.department_id?:0}" from="${department}" optionKey="id" optionValue="name" noSelection="${['0':'не указан']}"/>
      <label for="visualgroup_id">Видимость:</label>
      <g:select name="visualgroup_id" value="${group?.visualgroup_id}" from="${Visualgroup.list()}" optionKey="id" optionValue="name" noSelection="${['0':'все компании']}"/>
      <hr class="admin">
      <div class="fright">
        <input type="reset" class="spacing" value="Отмена" onclick="returnToList();"/>
        <g:if test="${user?.group?.is_usergroupedit}">      
          <input type="submit" class="button" value="Сохранить"/>
        </g:if>          
      </div>
    </g:formRemote>     
    <div class="clear"></div>    
    <g:if test="${group}">
      <div class="tabs">
        <ul class="nav">
          <li class="selected"><a href="javascript:void(0)" onclick="viewCell(0)">Права</a></li>                
          <li><a href="javascript:void(0)" onclick="viewCell(1)">Пользователи</a></li>                
        </ul>
        <div class="tab-content">
          <div class="inner">
            <div id="details"></div>
          </div>
        </div>
      </div>
    </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:'user',action:'groupuser',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
