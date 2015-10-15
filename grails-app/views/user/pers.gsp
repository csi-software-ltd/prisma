<html>
  <head>
    <title>${infotext?.title?:''}</title>
    <meta name="layout" content="main" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript>
      function init(){
        jQuery('#snils').mask("999-999-999 99"); 
        new Autocomplete('shortname', {
          serviceUrl:'${resource(dir:"autocomplete",file:"persname_autocomplete")}'
        });
        
        searchPers();
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
      function searchPers(){       
        $('form_submit_button').click();
      }
      function resetdata(){        
        $('user_id').defaultValue='';
        $('shortname').defaultValue='';
        $('snils').defaultValue='';              
        $('perstype').selectedIndex=0;
        jQuery('#is_sys_user').prop("checked",false);                       
      }                        
    </g:javascript>      
  </head>
  <body onload="init()">
    <g:formRemote name="allForm" url="[controller:'user',action:'perslist']" update="[success:'list']">
      <div class="padtop filter">
        <label class="auto" for="user_id">Код</label>
        <input type="text" class="mini" id="user_id" name="user_id" value="${inrequest?.user_id?:''}"/>
        <label class="auto" for="shortname">Фамилия</label>
        <input type="text" id="shortname" name="shortname" value="${inrequest?.shortname?:''}" />
        <label class="auto" for="inn">Учетный номер</label>
        <input type="text" id="snils" name="snils" value="${inrequest?.snils?:''}" />
        <label class="auto" for="perstype">Тип</label>
        <g:select class="mini" name="perstype" value="${inrequest?.perstype?:0}" from="['не выбран','сотрудник','директор','специалист']" keys="[0,1,2,3]"/>   
        <label for="is_sys_user">
          <input type="checkbox" id="is_sys_user" name="is_sys_user" value="1" <g:if test="${inrequest?.is_sys_user}">checked</g:if> />
          Пользователь системы
        </label>
        <div class="fright">
          <input type="reset" class="spacing" value="Сброс" onclick="resetdata()"/>
          <input type="submit" id="form_submit_button" value="Показать" />
        <g:if test="${session.user?.group?.is_persinsert}">
          <g:link action="persdetail" class="button">Новое &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
        </g:if>
        </div>
        <div class="clear"></div>
      </div>        
    </g:formRemote>    
    <div id="list"></div>
  </body>
</html>
