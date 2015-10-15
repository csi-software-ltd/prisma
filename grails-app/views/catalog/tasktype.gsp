<html>
  <head>
    <title>Prisma: Справочники - <g:if test="${tasktype}">Тип задания "${tasktype.name}"</g:if><g:else>Новый тип задания</g:else></title>
    <meta name="layout" content="main" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['tname'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Название"])}</li>'; $('tname').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${tasktype?1:0}){
          location.reload(true);
        } else if(e.responseJSON.tasktype){
          location.assign('${createLink(controller:controllerName,action:'tasktype')}'+'/'+e.responseJSON.tasktype);
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
    <h3 class="fleft"><g:if test="${tasktype}">Тип задания "${tasktype.name}"</g:if><g:else>Новый тип задания</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку типов</a>
    <div class="clear"></div>
    <g:formRemote name="tasktypeDetailForm" url="${[action:'updatetasktype',id:tasktype?.id?:0]}" method="post" onSuccess="processResponse(e)">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="tname">Название:</label>
      <input type="text" class="fullline" id="tname" name="tname" value="${tasktype?.name}" maxlength="50"/>

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
        <input class="spacing" type="submit" id="submit_button" value="Сохранить" />
      </div>
    </g:formRemote>
    <div class="clear"></div>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>