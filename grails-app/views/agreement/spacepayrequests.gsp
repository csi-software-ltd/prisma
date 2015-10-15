<html>
  <head>
    <title>Prisma: Заявки на платежи по аренде</title>
    <meta name="layout" content="main" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
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
      function togglecheck(){
        if(document.getElementById('groupcheckbox').checked)
          jQuery('#createSpacePayrequestsForm :checkbox:not(:checked):not(:disabled)').each(function(){ this.checked=true; });
        else
          jQuery('#createSpacePayrequestsForm :checkbox:checked').each(function(){ this.checked=false; });
      }
      function init(){
        $('form_submit_button').click();
        new Autocomplete('client_name', {
          serviceUrl:'${resource(dir:"autocomplete",file:"companyname_autocomplete")}'
        });
      }
    </g:javascript>
  </head>
  <body onload="init()">
    <h3 class="fleft">Заявки на платежи по аренде</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку аренд</a>
    <div class="clear"></div>
    <div id="filter" class="padtop filter">
      <g:formRemote name="requestListForm" url="${[action:'spacepayrequestslist']}" method="post" update="list">
        <label class="auto" for="client_name">Плательщик:</label>
        <input type="text" id="client_name" name="client_name" value="" />
        <label class="auto" for="modstatus">Статус:</label>
        <g:select class="mini" name="modstatus" from="['Неоплачен','Оплачен']" keys="01"/>
        <div class="fright">
        <g:if test="${iscanedit}">
          <input type="button" class="spacing" value="В оплату" onclick="$('createprequests_form_submit_button').click();"/>
        </g:if>
          <input type="reset" class="spacing" value="Сброс"/>
          <input type="submit" id="form_submit_button" value="Показать" />
        </div>
        <div class="clear"></div>
      </g:formRemote>
    </div>
    <div id="list"></div>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>