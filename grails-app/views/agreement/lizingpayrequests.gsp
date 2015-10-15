<html>
  <head>
    <title>Prisma: Заявки на платежи по лизингу</title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
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
          jQuery('#createLizingPayrequestsForm :checkbox:not(:checked)').each(function(){ this.checked=true; });
        else
          jQuery('#createLizingPayrequestsForm :checkbox:checked').each(function(){ this.checked=false; });
      }
    </g:javascript>
  </head>
  <body onload="\$('form_submit_button').click();">
    <h3 class="fleft">Заявки на платежи по лизингу</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку лизингов</a>
    <div class="clear"></div>
    <div id="filter" class="padtop filter">
      <g:formRemote name="requestListForm" url="${[action:'lizingpayrequestslist']}" method="post" update="list">
        <label for="startdate">Дата платежа с:</label>
        <g:datepicker class="normal nopad" name="startdate" value=""/>
        <label class="auto" for="enddate">по:</label>
        <g:datepicker class="normal nopad" name="enddate" value="${String.format('%td.%<tm.%<tY',new Date()+5)}"/>
        <label class="auto" for="client_name">Плательщик:</label>
        <input type="text" id="client_name" name="client_name" value="" />
        <label for="modstatus">Статус:</label>
        <g:select name="modstatus" from="['Неоплачен','В оплате','Оплачен']" keys="012"/>
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