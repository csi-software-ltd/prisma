<html>
  <head>
    <title>Prisma приложение: <g:if test="${useredit}">Редактирование пользователя № ${useredit.id}</g:if><g:else>Добавление нового пользователя</g:else></title>
    <meta name="layout" content="main" />
    <g:javascript library="prototype/autocomplete" />     
    <g:javascript>      
      var redirect=0;
      
      function init(){  
        <g:if test="${flash?.useredit_success}">
          $("infolist").up('div').show();
        </g:if> 
        <g:if test="${flash?.login}">              
          $('printForm').submit();     
        </g:if>
        <g:if test="${useredit}">
          getUserlog();
        </g:if>  
      }
      function returnToList(){
        $("returnToListForm").submit();
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
      function saveAndPrint(){
        if(!$('password').value.length){
          var sErrorMsg='<li>Не заполнено обязательное поле "Пароль"</li>'; $("password").addClassName('red');
          $("infolist").up('div').hide();
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
          $("changePassword").show();
          <g:if test="${useredit}">
            $("showPasswButton").hide();
          </g:if> 
        }else{
          $("password").removeClassName('red');
          $('print_login').value=1;        
          $("submit_button").click();
        }                                
      }
      function processResponse(e){        
        var sErrorMsg = '';
          ['name','login','email','pers','department_id','is_leader',
          'usergroup_id','tel','password','confirm_password'         
          ].forEach(function(ids){
            $(ids).removeClassName('red');
          });
        if(e.responseJSON.error){
          if(e.responseJSON.errorcode.length){          
            e.responseJSON.errorcode.forEach(function(err){
              switch (err) {                                                  
                case 1: sErrorMsg+='<li>Не заполнено обязательное поле "Имя"</li>'; $("name").addClassName('red'); break;
                case 2: sErrorMsg+='<li>Не заполнено обязательное поле "Логин"</li>'; $("login").addClassName('red'); break;
                case 3: sErrorMsg+='<li>Некорректный "Логин". Логин не менее ${loginlength} знаков из латинских букв или латинских букв и цифр</li>'; $("login").addClassName('red'); break;
                case 4: sErrorMsg+='<li>Такой "Логин" уже занят</li>'; $("login").addClassName('red'); break;
                case 5: sErrorMsg+='<li>Некорректный "Email"</li>'; $("email").addClassName('red'); break;
                case 6: sErrorMsg+='<li>Не выбранно значение обязательного поля "Физическое лицо"</li>'; $("pers").addClassName('red'); break;
                case 7: sErrorMsg+='<li>Не выбранно значение обязательного поля "Группа"</li>'; $("usergroup_id").addClassName('red'); break;
                case 8: sErrorMsg+='<li>Некорректный "Телефон"</li>'; $("tel").addClassName('red'); break;
                case 9: sErrorMsg+='<li>Не заполнено обязательное поле "Пароль"</li>'; $("password").addClassName('red'); break;                
                case 10: sErrorMsg+='<li>Некорректный пароль. Пароль не менее ${passwordlength} знаков из больших и маленьких латинских букв и цифр</li>'; $("password").addClassName('red'); $("confirm_password").addClassName('red'); break;             
                case 11: sErrorMsg+='<li>Пароли не совпадают</li>'; $("password").addClassName('red'); $("confirm_password").addClassName('red');break;
                case 12: sErrorMsg+='<li>Директор не может входить в отдел холдинга</li>'; $('is_leader').addClassName('red'); $('department_id').addClassName('red'); break;
              }
            });
          }
          $("infolist").up('div').hide();
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();   
          $('print_login').value=0;          
        } else {          
          if(redirect)
            returnToList();
          else{  
            <g:if test="${useredit}">
              location.reload(true);
            </g:if>
            <g:else>
              location.assign('${createLink(controller:'user',action:'userdetail')}'+'/'+e.responseJSON.user_id);
            </g:else>                                                                 
          }
        }        
      }                 
      function getUserlog(){
        if(${useredit?1:0}) $('userlog_submit_button').click();
      }          
      function setModstatus(){
        $('set_user_modstatus_submit_button').click();
      }
      function newPassw(){                    
        <g:remoteFunction action='generateUserPassword' onSuccess="processUserPassword(e)" />;        
      }
      function processUserPassword(e){
        if(e.responseJSON.password){
          $("password").value=e.responseJSON.password;
          $("confirm_password").value=e.responseJSON.password;
          $("changePassword").show();
          $("showPasswButton").hide();
        }
      }  
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
      function toggleBlock(){
        var iNum=$("is_block").value;
        if(iNum=="0"){
          $("blockIcon").removeClassName("icon-unlock");
          $("blockIcon").addClassName("icon-lock");
          $("is_block").value="1";
          $("is_block_text").value="блокирован";      
          $("is_block_text").up('span').addClassName("red");          
        }else{                    
          $("blockIcon").removeClassName("icon-lock");
          $("blockIcon").addClassName("icon-unlock");
          $("is_block").value="0";
          $("is_block_text").value="не блокирован";
          $("is_block_text").up('span').removeClassName("red");
        }
      }
      function toggleLeader(iValue){
        $("is_leader").selectedIndex=0;
        if(iValue>0){
          $("is_leader_label").removeAttribute("disabled");
          $("is_leader").removeAttribute("disabled");
        }else{                         
          $("is_leader_label").setAttribute("disabled","true");
          $("is_leader").setAttribute("disabled","true");
        }
      }
      function toggleDepartment_id(iValue){
        <g:if test="${!useredit}">
          if(iValue>0 && iValue==1){
            $("department_id_label").removeAttribute("disabled");
            $("department_id").removeAttribute("disabled");            
          }else{
            $("department_id").selectedIndex=0;
            $("department_id_label").setAttribute("disabled","true");
            $("department_id").setAttribute("disabled","true");
          }
        </g:if>
      }
      function toggleIs_leader(iValue){
        <g:if test="${!useredit}">
          if(iValue>0){
            $("is_leader_label").removeAttribute("disabled");
            $("is_leader").removeAttribute("disabled");
          }else{
            $("is_leader").selectedIndex=0;
            $("is_leader_label").setAttribute("disabled","true");
            $("is_leader").setAttribute("disabled","true");
          }          
        </g:if>
      }
      new Autocomplete('pers', {
        serviceUrl:'${resource(dir:"autocomplete",file:"persname_nouser_autocomplete")}',
          onSelect: function(value, data){
            var lsData = data.split(';');          
            $('pers_id').value = lsData[0];            
            toggleIs_leader(lsData[0]);    
            toggleDepartment_id(lsData[1]);            
        }   
      });
      function newPers(){
        window.open('${createLink(controller:'user',action:'persdetail')}');
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
          case 0: getUserlog();break;
          case 1: getUserProject();break;
          case 2: getExpensetypes();break;
        }
      }
      function getUserProject(){
        if(${useredit?1:0}) $('projects_submit_button').click();
      }
      function getExpensetypes(){
        if(${useredit?1:0}) $('expensetypes_submit_button').click();
      }
      function processaddprojectResponse(e){
        var sErrorMsg = '';
        ['project_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Проект"])}</li>'; $('project_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorprojectlist").innerHTML=sErrorMsg;
          $("errorprojectlist").up('div').show();
        } else
          jQuery('#projectAddForm').slideUp(300, function() {$('projects_submit_button').click();});
      }
      
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px}      
      input.mini{width:60px!important}
    </style>
  </head>
  <body onload="init();">
    <h3 class="fleft"><g:if test="${useredit}">Пользователь № ${useredit.id}</g:if><g:else>Добавление нового пользователя</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку пользователей</a>
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
    <g:if test="${useredit}">
      <label for="useredit_id" disabled>Id:</label>
      <input type="text" id="useredit_id" disabled value="${useredit?.id}" />
      <label for="inputdate" disabled>Дата заведения:</label>
      <input type="text" id="inputdate" disabled value="${useredit?.inputdate?String.format('%td.%<tm.%<tY',useredit?.inputdate):''}" /><br/>
      <label for="modstatus" disabled>Статус:</label>
      <input type="text" id="modstatus" disabled value="${useredit?.modstatus?'активный':'неактивный'}" />      
      <label for="lastdate" disabled>Посл. посещение:</label>
      <input type="text" id="lastdate" disabled value="${useredit?.lastdate?String.format('%td.%<tm.%<tY %<tT',useredit?.lastdate):''}" />
      <label for="saldo" disabled>Остаток подотчет.<br/> средств:</label>
      <input type="text" id="saldo" disabled value="${useredit?.saldo?:0}" />
      <label for="loansaldo" disabled>Остаток заёмных<br/> средств:</label>
      <input type="text" id="loansaldo" disabled value="${useredit?.loansaldo?:0}" />
      <hr class="admin">
    </g:if>
    <g:formRemote name="userDetailForm" url="[action:'saveUserDetail',id:useredit?.id?:0]" method="post" onSuccess="processResponse(e)">          
      <label for="name">Имя:</label>
      <input type="text" id="name" name="name" value="${useredit?.name?:''}"/>
      <label for="login">Логин:</label>
      <input type="text" id="login" name="login" value="${useredit?.login?:''}" /><br/>
      <label for="pers" <g:if test="${useredit}">disabled</g:if>>Физическое лицо:</label>
      <span class="input-append">
        <input type="text" class="nopad normal" id="pers" value="${Pers.get(useredit?.pers_id?:0)?.shortname?:(Pers.get(inrequest?.pers_id?:0)?.shortname?:'')}" <g:if test="${useredit}">disabled="true"</g:if>/>          
        <span class="add-on" onclick="newPers()"><abbr title="Добавить физическое лицо"><i class="icon-plus"></i></abbr></span>
      </span>
      <input type="hidden" id="pers_id" name="pers_id" value="${useredit?.pers_id?:(inrequest?.pers_id?:0)}"/>
      <label for="is_block_text">Блокировка:</label>
      <span class="input-append ${useredit?.is_block?'red':''}">
        <input type="text" class="nopad normal" id="is_block_text" readonly value="${useredit?.is_block==0?'не блокирован':'блокирован'}" />
        <span class="add-on" onclick="toggleBlock()"><i id="blockIcon" class="icon-${useredit?.is_block?'lock':'unlock'}"></i></span>
      </span>
      <input type="hidden" id="is_block" name="is_block" value="${useredit?.is_block?:0}"/>
      <label for="email">Email:</label>
      <input type="text" id="email" name="email" value="${useredit?.email?:''}"/>
      <label for="tel">Телефон:</label>
      <input type="text" id="tel" name="tel" value="${useredit?.tel?:''}"/>

      <label for="usergroup_id">Группа:</label>
      <g:select name="usergroup_id" value="${useredit?.usergroup_id?:0}" from="${usergroup}" optionKey="id" optionValue="name" noSelection="${['0':'не указан']}" disabled="${!iscaninsert}"/>                 
      <label id="department_id_label" for="department_id" <g:if test="${!iscaninsert&&(useredit?.pers_id?:0)==0&&(inrequest?.pers_id?:0)==0}">disabled</g:if>>Отдел:</label>
      <g:select name="department_id" disabled="${!iscaninsert||((useredit?.pers_id?:0)==0&&(inrequest?.pers_id?:0)==0)?'true':'false'}" value="${useredit?.department_id?:0}" from="${department}" optionKey="id" optionValue="name" noSelection="${['0':'не указан']}" onchange="toggleLeader(this.value)"/>       
      <label id="is_leader_label" <g:if test="${(useredit?.pers_id?:0)==0}">disabled</g:if> for="is_leader">Начисляет зарп.:</label>
      <g:select name="is_leader" value="${useredit?.is_leader?:0}" keys="${0..1}" from="${['нет','да']}" disabled="${iscaninsert&&(useredit?.department_id?:0)?'false':'true'}"/>
      <label for="is_remote">Удаленный доступ:</label>
      <g:select name="is_remote" value="${useredit?.is_remote?:0}" keys="${1..0}" from="${['включен','не включен']}" disabled="${!iscaninsert}"/>
      <label for="cashaccess">Доступ к кассе:</label>
      <g:select name="cashaccess" value="${useredit?.cashaccess?:0}" from="${cashaccess}" optionKey="id" optionValue="name" noSelection="${['0':'не указан']}" disabled="${!iscaninsert}"/>
      <label for="confaccess"> Доступ к конф.<br/>информации:</label>
      <g:select name="confaccess" value="${useredit?.confaccess?:0}" keys="${0..2}" from="${['нет доступа','видимость','редактирование']}" disabled="${!iscaninsert}"/>     
      <label for="is_loan">Заём:</label>
      <g:select name="is_loan" value="${useredit?.is_loan?:0}" from="${['возможен','не возможен']}" keys="10"/>
      <label for="is_parking">Паркинг:</label>
      <g:select name="is_parking" value="${useredit?.is_parking?:0}" from="${['возможен','не возможен']}" keys="10"/>
      <label class="auto" for="is_tehdirleader">
        <input type="checkbox" id="is_tehdirleader" name="is_tehdirleader" value="1" <g:if test="${useredit?.is_tehdirleader}">checked</g:if> <g:if test="${!iscaninsert}">disabled</g:if> />
        Директор директоров
      </label>
    <g:if test="${useredit}">
      <div id="btns" align="right">
        <a class="button" id="showPasswButton" onclick="this.hide();$('changePassword').show()">Изменить пароль &nbsp;<i class="icon-angle-right icon-large"></i></a> 
      </div>
    </g:if> 
      <div id="changePassword" <g:if test="${useredit}">style="display:none"</g:if>>      
        <label for="password">Пароль:</label>
        <input type="text" id="password" name="password" />
        <label for="confirm_pass">Подтверждение:</label>
        <input type="text" id="confirm_password" name="confirm_pass" />          
      </div>  
      <hr class="admin" />
      <div id="btns" class="fright">              
      <g:if test="${user?.group?.is_useredit}">
        <g:if test="${useredit}">
        <a class="button" id="activate" onclick="setModstatus()">${useredit?.modstatus?'Деактивировать':'Активировать'} &nbsp;<i class="icon-angle-right icon-large"></i></a>
        </g:if>  
        <a class="button spacing" id="generatepass" onclick="newPassw()">Генерация пароля &nbsp;<i class="icon-angle-right icon-large"></i></a>
        <!--<input type="button" id="printpass" value="Печать" onclick="saveAndPrint()"/>-->      
        <input type="submit" class="spacing" id="submit_button" value="Сохранить"/>
        <input type="submit" class="spacing" id="submit_button_exit" value="Сохранить и выйти" onclick="redirect=1"/>
      </g:if>  
        <input type="reset" value="Отменить" onclick="returnToList()" />
      </div>
      <input type="hidden" id="print_login" name="print_login" value="0"/>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${useredit}">
    <div class="tabs">
        <ul class="nav">
          <li class="selected"><a href="javascript:void(0)" onclick="viewCell(0)">История заходов</a></li>                
          <li><a href="javascript:void(0)" onclick="viewCell(1)">Проекты</a></li>                
          <li><a href="javascript:void(0)" onclick="viewCell(2)">Типы расходов</a></li>                
        </ul>
        <div class="tab-content">
          <div class="inner">
            <div id="details"></div>
          </div>
        </div>
      </div>
    </div> 
    <g:formRemote name="compersForm" url="[action:'userloglist',id:useredit.id]" update="[success:'details']">
      <input type="submit" class="button" id="userlog_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="projectForm" url="[action:'userprojectlist',id:useredit.id]" update="[success:'details']">
      <input type="submit" class="button" id="projects_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="expensetypesForm" url="[action:'userexpensetypes',id:useredit.id]" update="[success:'details']">
      <input type="submit" class="button" id="expensetypes_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:formRemote name="modstatusForm" url="[action:'set_user_modstatus',id:useredit?.id?:0]" onSuccess="location.reload(true)">
      <input type="hidden" name="modstatus" value="${useredit?.modstatus}"/>
      <input type="submit" class="button" id="set_user_modstatus_submit_button" value="Показать" style="display:none" />
    </g:formRemote>    
    <g:form id="returnToListForm" name="returnToListForm" url="${[action:'users',params:[fromEdit:1]]}">
    </g:form>
    <g:form id="printForm" name="printForm" url="${[action:'printLogin',params:[login:flash?.login?:'']]}" target="_blank" method="post">
    </g:form>
  </body>
</html>
