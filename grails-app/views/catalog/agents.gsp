<html>
  <head>
    <title>${infotext?.title?:'Prisma - Агенты'}</title>
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
      function resetForm(){
        $('name').value = '';
        $('modstatus').selectedIndex = 0;
      }
      function init(){
        new Autocomplete('name', {
          serviceUrl:'${resource(dir:"autocomplete",file:"agentname_autocomplete")}'
        })
        $('form_submit_button').click();
      }
    </g:javascript>
  </head>
  <body onload="init()">
    <div class="clear"></div>
    <div class="padtop filter">
      <g:formRemote name="allForm" url="[controller:'catalog',action:'agentlist']" update="list">
        <label class="auto" for="name">Фио:</label>
        <input type="text" id="name" name="name" value="${inrequest?.name?:''}"/>
        <label class="auto" for="modstatus">Статус:</label>
        <g:select class="mini" name="modstatus" value="${inrequest?.modstatus?:-1}" from="['активный','неактивный']" keys="[1,0]" noSelection="${['-1':'не выбран']}"/>
        <div class="fright">
          <input type="button" class="reset spacing" value="Сброс" onclick="resetForm()"/>
          <input type="submit" id="form_submit_button" value="Показать"/>
        <g:if test="${iscanadd}">
          <g:link action="agent" class="button">Новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
        </g:if>
        </div>
      </g:formRemote>
      <div class="clear"></div>
    </div>
    <div id="list"></div>
  </body>
</html>