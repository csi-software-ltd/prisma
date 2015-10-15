<html>
  <head>
    <title>Prisma: Заявки на платежи по кредитам</title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
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
          jQuery('#createKreditPayrequestsForm :checkbox:not(:checked)').each(function(){ this.checked=true; });
        else
          jQuery('#createKreditPayrequestsForm :checkbox:checked').each(function(){ this.checked=false; });
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
    <h3 class="fleft">Заявки на платежи по кредитам</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку кредитов</a>
    <div class="clear"></div>
    <div id="filter" class="padtop filter">
      <g:formRemote name="requestListForm" url="${[action:'kreditpayrequestslist']}" method="post" update="list">
        <label for="startdate">Дата платежа с:</label>
        <g:datepicker class="normal nopad" name="startdate" value=""/>
        <label class="auto" for="enddate">по:</label>
        <g:datepicker class="normal nopad" name="enddate" value="${String.format('%td.%<tm.%<tY',new Date()+5)}"/>
        <label class="auto" for="client_name">Плательщик:</label>
        <input type="text" id="client_name" name="client_name" value="" />
        <div id="companyname_autocomplete" class="autocomplete" style="display:none"></div>
        <label class="auto" for="modstatus">Статус:</label>
        <g:select class="mini" name="modstatus" from="['Неоплачен','В оплате','Оплачен']" keys="012"/>
        <label class="auto" for="kredsort">Тип кредита:</label>
        <g:select class="mini" name="kredsort" from="['Все','Реал','Техн','Реалтех']" keys="0123"/>
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