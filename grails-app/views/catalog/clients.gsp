<html>
  <head>
    <title>${infotext?.title?:'Prisma - Клиенты'}</title>
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
        $('parent').selectedIndex = 0;
        $('modstatus').selectedIndex = 0;
      }
      function init(){
        new Autocomplete('name', {
          serviceUrl:'${resource(dir:"autocomplete",file:"clientname_autocomplete")}'
        });
        $('form_submit_button').click();
      }
    </g:javascript>
  </head>
  <body onload="init()">
    <div class="clear"></div>
    <div class="padtop filter">
      <g:formRemote name="allForm" url="[controller:'catalog',action:'clientlist']" update="list">
        <label class="auto" for="name">Название:</label>
        <input type="text" id="name" name="name" value="${inrequest?.name}"/>
        <label class="auto" for="parent">Осн. клиент:</label>
        <g:select class="mini" name="parent" value="${inrequest?.parent}" from="${Client.findAllByIs_super(1)}" optionKey="id" optionValue="name" noSelection="${['0':'все']}"/>
        <label class="auto" for="modstatus">Статус:</label>
        <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['активный','неактивный']" keys="[1,0]" noSelection="${['-1':'все']}"/>
        <div class="fright">
          <input type="button" class="reset spacing" value="Сброс" onclick="resetForm()"/>
          <input type="submit" id="form_submit_button" value="Показать"/>
        <g:if test="${iscanadd}">
          <g:link action="client" class="button">Новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
        </g:if>
        </div>
      </g:formRemote>
      <div class="clear"></div>
    </div>
    <div id="list"></div>
  </body>
</html>