<html>
  <head>
    <title>${infotext?.title?:'Prisma - Статьи расходов'}</title>
    <meta name="layout" content="main" />    
    <g:javascript>
      function init(){
        $('form_submit_button').click();        
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
      function resetFilter(){
        $('expensetype1_id').selectedIndex=0;
        $('expensetype2_id').selectedIndex=0;
        $('modstatus').selectedIndex=0;
        $('name').value='';
      }
      function getExpensetypes2(iId){
        <g:remoteFunction controller='catalog' action='expensetype2list' update='expensetype2_id' params="'id='+iId"/>;
      }
    </g:javascript>
  </head>
  <body onload="init()">
    <div id="filter" class="padtop filter">
      <g:formRemote name="allForm" url="[controller:'catalog', action:'expensetypeslist']" update="list">
        <label for="expensetype1_id">Раздел:</label>
        <g:select name="expensetype1_id" value="${inrequest?.expensetype1_id?:0}" from="${Expensetype1.list(sort:'name')}" optionKey="id" optionValue="name" noSelection="${['0':'не указан']}" onchange="getExpensetypes2(this.value)"/>
        <label for="expensetype2_id">Подраздел:</label>
        <g:select name="expensetype2_id" value="${inrequest?.expensetype2_id?:0}" from="${Expensetype2.list(sort:'name')}" optionKey="id" optionValue="name" noSelection="${['0':'не указан']}"/><br />
        <label for="name">Название:</label>
        <input type="text" id="name" name="name" value="${inrequest?.name?:''}" />
        <label class="auto" for="modstatus">Статус</label>
        <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['Активные','Архив']" keys="10" noSelection="${['-100':'все']}"/>
        <div class="fright">
          <input type="button" class="reset spacing" value="Сброс" onclick="resetFilter()"/>
          <input type="submit" id="form_submit_button" value="Показать"/>
        <g:if test="${iscanadd}">
          <g:link action="expensetype" class="button">Новая &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
        </g:if>
        </div>
        <div class="clear"></div>
      </g:formRemote>
    </div>
    <div id="list"></div>
  </body>
</html>
