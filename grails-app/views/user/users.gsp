<html>
  <head>
    <title>${infotext?.title?:''}</title>
    <meta name="layout" content="main" />
    <g:javascript library="prototype/autocomplete" />    
    <g:javascript>
      function init(){
        searchUsers();
        new Autocomplete('login', {
          serviceUrl:'${resource(dir:"autocomplete",file:"login_autocomplete")}'
        });
        new Autocomplete('pers', {
          serviceUrl:'${resource(dir:"autocomplete",file:"persname_autocomplete")}'
        });
        
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
      function searchUsers(){       
        $('form_submit_button').click();
      }
      function resetdata(){        
        $('user_id').value='';        
        $('login').value='';
        $('pers').value='';        
        $('modstatus').selectedIndex = 0;
        $('department_id').selectedIndex = 0;
        jQuery('#is_block').prop("checked",false);             
      }
      function loginAsUser(iId){
        <g:remoteFunction controller='user' action='loginAsUser' onSuccess='processLoginAsUserResponse(e)' params="'id='+iId" />
      }
      function processLoginAsUserResponse(e){
        location.assign('${(context.is_dev)?context.serverURL:"/"}');
      }
    </g:javascript>      
  </head>
  <body onload="init()">
    <g:formRemote name="allForm" url="[controller:'user',action:'userlist']" update="[success:'list']">
      <div class="padtop filter">
        <label class="auto" for="user_id">Код:</label>
        <input type="text" id="user_id" name="user_id" value="${inrequest?.user_id?:''}" style="width:100px;"/>
        <label class="auto" for="login">Логин:</label>
        <input type="text" id="login" name="login" value="${inrequest?.login?:''}" />
        <div id="login_autocomplete" class="autocomplete" style="display:none"></div>
        <label class="auto" for="modstatus">Статус:</label>
        <g:select class="auto" name="modstatus" value="${inrequest?.modstatus}" keys="${1..0}" from="${['активный','неактивный']}" noSelection="${['-1':'все']}"/>      
        <label class="auto" for="is_block">
          <input type="checkbox" id="is_block" name="is_block" value="1" <g:if test="${inrequest?.is_block}">checked</g:if> />
          Блокирован
        </label>
        <!--<label class="auto" for="usergroup_id">Группа:</label>
        <g:select name="usergroup_id" value="${inrequest?.usergroup_id?:0}" from="${usergroup}" optionKey="id" optionValue="name" noSelection="${['0':'все']}"/>-->
        <label class="auto" for="department_id">Отдел:</label>
        <g:select class="mini" name="department_id" value="${inrequest?.department_id}" from="${departments}" optionKey="id" optionValue="name" noSelection="${['-100':'все']}"/>        
        <label class="auto" for="pers">Физ. лицо:</label>
        <input type="text" id="pers" name="pers" value="${inrequest?.pers?:''}" />          
        <div id="pers_autocomplete" class="autocomplete" style="display:none"></div>
        <div class="fright">
          <input type="button" class="reset spacing" value="Сброс" onclick="resetdata()"/>
          <input type="submit" id="form_submit_button" value="Показать" />
          <g:if test="${user?.group?.is_userinsert}"><g:link action="userdetail" class="button">Новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link></g:if>
        </div>
        <div class="clear"></div>
      </div>        
    </g:formRemote>    
    <div id="list"></div>
  </body>
</html>
