<html>
  <head>
    <title>${infotext?.title?:'Prisma - Группы пользователей'}</title>
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
      function resetform(){
        $('gname').value = '';
        $('department_id').selectedIndex = 0;
      }
      function init(){
        new Autocomplete('gname', {
          serviceUrl:'${resource(dir:"autocomplete",file:"usergroup_autocomplete")}'
        })
        $('form_submit_button').click();
      }
    </g:javascript>
  </head>
  <body onload="init()">
    <div class="clear"></div>
    <div class="padtop filter">
      <g:formRemote name="allForm" url="[controller:'user',action:'grouplist']" update="list">
        <label class="auto" for="gname">Название:</label>
        <input type="text" id="gname" name="name" value="${inrequest?.name?:''}"/>
        <div id="name_autocomplete" class="autocomplete" style="display:none"></div>
        <label class="auto" for="department_id">Отдел:</label>
        <g:select class="mini" name="department_id" value="${inrequest?.department_id}" from="${departments}" optionKey="id" optionValue="name" noSelection="${['-100':'все']}"/>
        <div class="fright">
          <input type="button" class="reset spacing" value="Сброс" onclick="resetform()"/>
          <input type="submit" id="form_submit_button" value="Показать"/>
        <g:if test="${session.user.group.is_usergroupinsert}">
          <g:link action="groupdetail" class="button">Новая &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
        </g:if>
        </div>
      </g:formRemote>
      <div class="clear"></div>
    </div>
    <div id="list"></div>
  </body>
</html>