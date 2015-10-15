<html>
  <head>
    <title>Prisma: Задания - Продление договора аренды №${spaceprolong.anumber}</title>
    <meta name="layout" content="main" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processResponse(e){
        var sErrorMsg = '';
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else {
          location.reload(true);
        }
      }
      function submitPermit(iStatus){
        $('permitstatus').value = iStatus;
        submitForm();
      }
      function submitWork(iStatus){
        $('workstatus').value = iStatus;
        submitForm();
      }
      function submitForm(){
        $('submit_button').click();
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
      .tabs a:hover,.tabs a.active,.tabs a.active i{color:#000}
    </style>
  </head>
  <body>
    <h3 class="fleft">Задание на продление договора аренды №${spaceprolong.anumber}</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку заданий</a>
    <div class="clear"></div>
    <g:formRemote name="spaceprolongDetailForm" url="${[action:'updatespaceprolong',id:spaceprolong.id]}" method="post" onSuccess="processResponse(e)">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="arendator" disabled>Арендатор:</label>
      <input type="text" id="arendator" disabled value="${arendator.name}" />
      <label for="arendodatel" disabled>Арендодатель:</label>
      <input type="text" id="arendodatel" disabled value="${arendodatel.name}" />
      <label for="spacetype_id" disabled>Тип помещения:</label>
      <g:select id="spacetype_id" name="spacetype_id" value="${spaceprolong.spacetype_id}" from="${spacetypes}" optionKey="id" optionValue="name" disabled="true"/>
      <label for="asort" disabled>Признак аренды:</label>
      <g:select id="asort" name="asort" value="${spaceprolong.asort}" from="['Аренда','Субаренда']" keys="[1,0]" disabled="true"/>
      <label for="fulladdress" disabled>Адрес:</label>
      <input type="text" class="fullline" id="fulladdress" disabled value="${spaceprolong.fulladdress}" />

      <hr class="admin" />

      <label for="sp_permitstatus" disabled>Статус разрешения</label>
      <g:select id="sp_permitstatus" name="sp_permitstatus" value="${spaceprolong.permitstatus}" from="['Нет информации','Разрешено','Отказано']" keys="[0,1,-1]" disabled="true"/>
      <label for="sp_workstatus" disabled>Статус работы</label>
      <g:select id="sp_workstatus" name="sp_workstatus" value="${spaceprolong.workstatus}" from="['Нет информации','Принято к исполнению']" keys="01" disabled="true"/>
      <label for="workuser" disabled>Ответственный:</label>
      <input type="text" id="workuser" disabled value="${workuser?.shortname}" />
      <label for="workdate" disabled>Дата работы:</label>
      <input type="text" name="workdate" disabled value="${spaceprolong.workdate?String.format('%td.%<tm.%<tY',spaceprolong.workdate):''}" />

      <hr class="admin" />

      <label for="prolongcomment">Комментарий к продлению:</label>
      <g:textArea name="prolongcomment" id="prolongcomment" value="${spaceprolong.prolongcomment}" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
        <input type="submit" id="submit_button" value="Сохранить" />
      </g:if>
      <g:if test="${iscanpermit&&spaceprolong.permitstatus<1}">
        <input type="button" class="spacing" value="Разрешить" onclick="submitPermit(1)" />
      </g:if><g:if test="${iscanpermit&&spaceprolong.permitstatus>-1&&!spaceprolong.workstatus}">
        <input type="button" class="spacing" value="Отказать" onclick="submitPermit(-1)" />
      </g:if><g:if test="${iscanpermit&&spaceprolong.permitstatus==1&&!spaceprolong.workstatus}">
        <input type="button" class="spacing" value="Снять разрешение" onclick="submitPermit(0)" />
      </g:if>
      <g:if test="${iscanwork&&spaceprolong.permitstatus==1&&!spaceprolong.workstatus}">
        <input type="button" class="spacing" value="Взять в работу" onclick="submitWork(1)" />
      </g:if><g:elseif test="${iscanwork&&spaceprolong.workstatus}">
        <input type="button" class="spacing" value="Отменить взятие" onclick="submitWork(0)" />
      </g:elseif>
      </div>
      <input type="hidden" id="permitstatus" name="permitstatus" value="${spaceprolong.permitstatus}"/>
      <input type="hidden" id="workstatus" name="workstatus" value="${spaceprolong.workstatus}"/>
    </g:formRemote>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>