<html>
  <head>
    <title>Prisma: Справочники - <g:if test="${enqtype}">Тип справки "${enqtype.name}"</g:if><g:else>Новый тип справки</g:else></title>
    <meta name="layout" content="main" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function toggleLongterm(sType){
        if (sType=='2') $('longtermsection').show();
        else $('longtermsection').hide();
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['tname','type','term','longterm'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Название"])}</li>'; $('tname').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип справки"])}</li>'; $('type').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок"])}</li>'; $('term').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Срок"])}</li>'; $('term').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок для иногородних"])}</li>'; $('longterm').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Срок для иногородних"])}</li>'; $('longterm').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${enqtype?1:0}){
          location.reload(true);
        } else if(e.responseJSON.enqtype){
          location.assign('${createLink(controller:controllerName,action:'enqtype')}'+'/'+e.responseJSON.enqtype);
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
    <h3 class="fleft"><g:if test="${enqtype}">Тип справки "${enqtype.name}"</g:if><g:else>Новый тип справки</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку типов</a>
    <div class="clear"></div>
    <g:formRemote name="enqtypeDetailForm" url="${[action:'updateenqtype',id:enqtype?.id?:0]}" method="post" onSuccess="processResponse(e)">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="tname">Название:</label>
      <input type="text" class="fullline" id="tname" name="tname" value="${enqtype?.name}" maxlength="50"/>
      <label for="type">Тип справки:</label>
      <g:select name="type" value="${enqtype?.type}" from="['В налоговую','В банк']" keys="12" noSelection="${['0':'не указан']}" onchange="toggleLongterm(this.value)"/><br/>
      <label for="term">Срок:</label>
      <input type="text" id="term" name="term" value="${enqtype?.term}"/>
      <span id="longtermsection" style="${enqtype?.type!=2?'display:none':''}"><label for="longterm">Для иногородних:</label>
      <input type="text" id="longterm" name="longterm" value="${enqtype?.longterm}"/></span>

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