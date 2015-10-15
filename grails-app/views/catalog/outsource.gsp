<html>
  <head>
    <title>Prisma: Справочники - <g:if test="${outsource}">Аутсорсер "${outsource.name}"</g:if><g:else>Новый аутсорсер</g:else></title>
    <meta name="layout" content="main" />
    <g:javascript>
      function init(){  
        <g:if test="${outsource}">
          getCompanies();
        </g:if>
      }
      function getCompanies(){
        if(${outsource?1:0}){ 
          var iId= ${outsource?.id?:0};        
          <g:remoteFunction controller='catalog' action='outsourcecompanylist' update='details' params="'id='+iId"/>;
        }  
      }
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['name'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Название"])}</li>'; $('name').addClassName('red'); break;              
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("infolist").up('div').hide();
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${outsource?1:0}){
          location.reload(true);
        } else if(e.responseJSON.outsource_id){
          location.assign('${createLink(controller:controllerName,action:'outsource')}'+'/'+e.responseJSON.outsource_id);
        } else
          returnToList();
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px}
    </style>
  </head>
  <body onload="init()">
    <h3 class="fleft"><g:if test="${outsource}">Аутсорсер "${outsource.name}"</g:if><g:else>Новый аутсорсер</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку аутсорсеров</a>
    <div class="clear"></div>

    <div class="info-box" style="display:none;margin-top:0">
      <span class="icon icon-info-sign icon-3x"></span>
      <ul id="infolist">      
        <li>Изменения сохранены</li>
      </ul>
    </div>    
    <div class="error-box" style="display:none">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>

    <g:formRemote name="outsourceDetailForm" url="${[action:'updateoutsource',id:outsource?.id?:0]}" method="post" onSuccess="processResponse(e)">      
      <label for="name">Название:</label>
      <input type="text" class="fullline" id="name" name="name" value="${outsource?.name}" maxlength="50"/>
      <label for="modstatus">Статус:</label>
      <g:select name="modstatus" value="${outsource?.modstatus}" from="['активный','неактивный']" keys="[1,0]"/>  
      <hr class="admin" />
      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
        <input class="spacing" type="submit" id="submit_button" value="Сохранить" />
      </g:if>
      </div>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${outsource}">
    <div class="tabs">
      <ul class="nav">
        <li class="selected"><a href="javascript:void(0)">Компании</a></li>        
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>    
  </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
