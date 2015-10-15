<html>
  <head>
    <title>${infotext?.title?:'Prisma - Отделы'}</title>
    <meta name="layout" content="main" />
    <g:javascript library="prototype/autocomplete" />    
    <g:javascript>
      function init(){
        $('form_submit_button').click();
        new Autocomplete('name', {
          serviceUrl:'${resource(dir:"autocomplete",file:"department_autocomplete")}'
        });
      }
      function resetFilter(){
        $('name').value='';
      }
    </g:javascript>
  </head>
  <body onload="init()">
    <div id="filter" class="padtop filter">
      <g:formRemote name="allForm" url="[controller:'catalog', action:'departmentlist']" update="list">
        <label class="auto" for="name">Название:</label>
        <input type="text" id="name" name="name" value="${inrequest?.name?:''}" />
        <div id="name_autocomplete" class="autocomplete" style="display:none"></div>
        <div class="fright">
          <input type="button" class="spacing reset" value="Сброс" onclick="resetFilter()"/>
          <input type="submit" id="form_submit_button" value="Показать"/>
        <g:if test="${iscanadd}">
          <a class="button" href="${createLink(action:'departmentdetail')}">Новый &nbsp;<i class="icon-angle-right icon-large"></i></a>
        </g:if>
        </div>
        <div class="clear"></div>
      </g:formRemote> 
    </div>
    <div id="list"></div>
  </body>
</html>
