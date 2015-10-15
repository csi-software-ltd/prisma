<html>
  <head>
    <title>${infotext?.title?:'Prisma - Проекты'}</title>
    <meta name="layout" content="main" />
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
      function init(){
        $('form_submit_button').click();
      }
    </g:javascript>
  </head>
  <body onload="init()">
    <div class="clear"></div>
    <div class="padtop filter">
      <g:formRemote name="allForm" url="[controller:'catalog',action:'projectlist']" update="list">
        <label class="auto" for="name">Название:</label>
        <input type="text" id="name" name="name" value="${inrequest?.name?:''}" style="width:400px"/>
        <div id="name_autocomplete" class="autocomplete" style="display:none"></div>
        <label class="auto" for="modstatus">Статус:</label>
        <g:select name="modstatus" value="${inrequest?.modstatus}" from="['активный','неактивный']" keys="[1,0]" noSelection="${['-1':'не выбран']}"/>
        <div class="fright">
          <input type="reset" class="spacing" value="Сброс"/>
          <input type="submit" id="form_submit_button" value="Показать"/>
        <g:if test="${iscanadd}">
          <g:link action="project" class="button">Новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
        </g:if>
        </div>
      </g:formRemote>
      <div class="clear"></div>
    </div>
    <div id="list"></div>
  </body>
</html>