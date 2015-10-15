<html>
  <head>
    <title>Prisma: <g:if test="${department}">Редактировать отдел № ${department.id}</g:if><g:else>Добавление нового отдела</g:else></title>
    <meta name="layout" content="main" />    
    <g:javascript>                  
      function init(){  
        <g:if test="${flash?.departmentedit_success}">
          $("infolist").up('div').show();
        </g:if>
        <g:if test="${department}">
          getExpensetype();
        </g:if>        
      }
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processDepartmentResponse(e){
        $('name').removeClassName('red');
        if (e.responseJSON.error){
          var sErrorMsg='';
          if (e.responseJSON.errorcode){            
             switch (e.responseJSON.errorcode) {                                                               
              case 1: sErrorMsg='<li>Введите название отдела</li>'; $('name').addClassName('red'); break;
            }
            $("infolist").up('div').hide();
            $("errorlist").innerHTML=sErrorMsg;
            $("errorlist").up('div').show();
          }  
        }else{          
          location.assign('${createLink(controller:'catalog',action:'departmentdetail')}'+'/'+e.responseJSON.department_id);          
        }                      
      }                
      function proccesDepartmentExpensetype(e){
        if (!e.responseJSON.error){
          $("infolist").up('div').show();
          getExpensetype();
        }  
        else{
          $("infolist").up('div').hide();
          $("errorlist").innerHTML='Ошибка сохранения типов расходов!';
          $("errorlist").up('div').show();
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
          case 0: getExpensetype();break; 
          case 1: getUsers();break;
          case 2: getSubs();break;
        }
      }
      function getExpensetype(){
        if(${department?1:0}){    
          var idepartmentId= ${department?.id?:0};       
          <g:remoteFunction controller='catalog' action='departmentexpensetype' update='details' params="'id='+idepartmentId"/>;
        }  
      }
      function getUsers(){
        if(${department?1:0}){    
          var idepartmentId= ${department?.id?:0};       
          <g:remoteFunction controller='catalog' action='departmentuserlist' update='details' params="'id='+idepartmentId"/>;
        }  
      } 
      function getSubs(){
        if(${department?1:0}){
          var idepartmentId= ${department?.id?:0};
          <g:remoteFunction controller='catalog' action='departmentsublist' update='details' params="'id='+idepartmentId"/>;
        }
      }
      function toggleUl(sFid){
        if(jQuery("#"+sFid+"_div").is(':hidden')){
          $(sFid+"_label").removeClassName('icon-collapse').addClassName('icon-collapse-top');
          jQuery("#"+sFid+"_div").slideDown();
        } else{
          $(sFid+"_label").removeClassName('icon-collapse-top').addClassName('icon-collapse');
          jQuery('#'+sFid+"_div").slideUp();
        }
      }
      function toggleUl2(sFid){
        if(jQuery("#"+sFid+"_div2").is(':hidden')){
          $(sFid+"_label2").removeClassName('icon-collapse').addClassName('icon-collapse-top');
          jQuery("#"+sFid+"_div2").slideDown();
        } else{
          $(sFid+"_label2").removeClassName('icon-collapse-top').addClassName('icon-collapse');
          jQuery('#'+sFid+"_div2").slideUp();
        }
      }
      function toggleDep(){
        if(jQuery("#is_dep").is(':checked')){          
          jQuery('#parent,label[for="parent"]').attr('disabled','disabled');       
        } else {          
          jQuery('#parent,label[for="parent"]').removeAttr('disabled');         
        }
      }        
    </g:javascript>    
  </head>
  <body onload="init();">
    <h3 class="fleft"><g:if test="${department}">Редактировать отдел № ${department.id}</g:if><g:else>Добавление нового отдела</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку отделов</a>
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
    <g:formRemote url="${[controller:'catalog',action:'saveDepartmentDetail',id:department?.id?:0]}" onSuccess="processDepartmentResponse(e)" method="post" name="createdepartmentForm">           
      <label for="name">Название:</label>
      <input type="text" name="name" id="name" style="width:350px" value="${department?.name?:''}"/>
      <label for="shortname">Краткое название:</label>
      <input type="text" name="shortname" id="shortname" value="${department?.shortname?:''}"/>      
      <label class="auto" for="is_dep">
        <input type="checkbox" id="is_dep" name="is_dep" value="1" onclick="toggleDep()" <g:if test="${department?.is_dep}">checked</g:if> />
        Департамент
      </label>
      <label class="auto" for="parent" <g:if test="${department?.is_dep}">disabled</g:if>>Входит в:</label>      
      <g:select name="parent" value="${department?.parent?:0}" from="${Department.findAllByIs_dep(1)}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}" disabled="${department?.is_dep?'true':'false'}"/>
      <label class="auto" for="is_cashextstaff">
        <input type="checkbox" id="is_cashextstaff" name="is_cashextstaff" value="1" <g:if test="${department?.is_cashextstaff}">checked</g:if> />
        Подотчетные из всего холдинга
      </label><br />
      <label for="project_id">Проект:</label>      
      <g:select name="project_id" value="${department?.project_id?:0}" from="${projects}" optionKey="id" optionValue="name" />
      <hr class="admin" />
      <div class="fright">                   
        <input type="reset" class="spacing" value="Отмена" onclick="returnToList();" />
        <input type="submit" class="button" value="Сохранить" />        
      </div>                 
    </g:formRemote>     
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:'catalog',action:'departments',params:[fromDetails:1]]}">
    </g:form>    
    <div class="clear"></div>    
    <g:if test="${department}">
      <div class="tabs">
        <ul class="nav">
          <li class="selected"><a href="javascript:void(0)" onclick="viewCell(0)">Типы расходов</a></li>
          <li><a href="javascript:void(0)" onclick="viewCell(1)">Сотрудники</a></li>
        <g:if test="${department?.is_dep}">
          <li><a href="javascript:void(0)" onclick="viewCell(2)">Подотделы</a></li>
        </g:if>
        </ul>
        <div class="tab-content">
          <div class="inner">
            <div id="details"></div>
          </div>
        </div>
      </div>
    </g:if>  
  </body>
</html>
