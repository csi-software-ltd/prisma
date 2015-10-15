﻿<html>
  <head>
    <title>Prisma: Справочники - <g:if test="${zalogtype}">Вид залога "${zalogtype.name}"</g:if><g:else>Новый вид залога</g:else></title>
    <meta name="layout" content="main" />
    <g:javascript>
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
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${zalogtype?1:0}){
          location.reload(true);
        } else if(e.responseJSON.zalogtype_id){
          location.assign('${createLink(controller:controllerName,action:'zalogtype')}'+'/'+e.responseJSON.zalogtype_id);
        } else
          returnToList();
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px}
    </style>
  </head>
  <body>
    <h3 class="fleft"><g:if test="${zalogtype}">Вид залога "${zalogtype.name}"</g:if><g:else>Новый вид залога</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку </a>
    <div class="clear"></div>

    <div class="error-box" style="display:none">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>

    <g:formRemote name="zalogtypeDetailForm" url="${[action:'updatezalogtype',id:zalogtype?.id?:0]}" method="post" onSuccess="processResponse(e)">
      <label for="name">Название:</label>
      <input type="text" class="fullline" id="name" name="name" value="${zalogtype?.name}" maxlength="50"/>

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
        <input class="spacing" type="submit" id="submit_button" value="Сохранить" />
      </g:if>
      </div>
    </g:formRemote>
    <div class="clear"></div>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>