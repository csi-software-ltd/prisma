<html>
  <head>
    <title>Prisma: <g:if test="${task}">Редактирование задания № ${task.id}</g:if><g:else>Добавление нового задания</g:else></title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function toggleDisabled(){
        if(document.getElementById('is_remap').checked)
          $('executor').disabled = false;
        else
          $('executor').disabled = true;
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['tasktype_id','term','department_id','executor','description'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>Не выбрано значение обязательного поля "Тип"</li>'; $("tasktype_id").addClassName('red'); break;
              case 2: sErrorMsg+='<li>Не заполнено обязательное поле "Срок исполнения"</li>'; $("term").addClassName('red'); break;
              case 3: sErrorMsg+='<li>Некорректные данные в поле "Срок исполнения"</li>'; $("term").addClassName('red'); break;
              case 4: sErrorMsg+='<li>Не выбрано значение обязательного поля "Отдел исполнения"</li>'; $("department_id").addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Адресат"])}</li>'; $("executor").addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Адресат"])}</li>'; $("executor").addClassName('red'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Описание"])}</li>'; $("description").addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("infolist").up('div').hide();
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${task?1:0}){
          $("errorlist").up('div').hide();
          $("infolist").up('div').show();
          jQuery('html,body').animate({scrollTop: 0},'slow');
        } else if(e.responseJSON.task_id){
          location.assign('${createLink(controller:'task',action:'taskdetail')}'+'/'+e.responseJSON.task_id);
        }
      }
      function getExecutor(department_id){
        <g:remoteFunction controller='task' action='executor' params="'department_id='+department_id" update="executor_span" />;
      }
      function viewCell(iNum){
        var tabs = jQuery('.nav').find('li');
        for(var i=0; i<tabs.length; i++){
          if(i==iNum)
            tabs[i].addClassName('selected');
          else
            tabs[i].removeClassName('selected');
        }

        switch(iNum){
          case 0: getTaskevent();break;
        }
      }
      function getTaskevent(){
        if(${task?1:0}) $('taskevent_submit_button').click();
      }
      function submitForm(iStatus){
        $('modstatus').value = iStatus
        $('submit_button').click();
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px}
      input.mini{width:60px!important}
    </style>
  </head>
  <body onload="getTaskevent()">
    <h3 class="fleft"><g:if test="${task}">Задание № ${task.id}</g:if><g:else>Добавление нового задания</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку заданий</a>
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
    <g:if test="${task}">
      <label for="inputdate" disabled>Дата заведения:</label>
      <input type="text" id="inputdate" readonly value="${task?.inputdate?String.format('%td.%<tm.%<tY',task?.inputdate):''}" />
      <label for="taskstatus" disabled>Статус:</label>
      <g:select name="taskstatus" value="${task.taskstatus}" from="${taskstatus}" optionKey="id" optionValue="name" disabled="true"/>
      <hr class="admin">
    </g:if>
    <g:formRemote name="taskDetailForm" url="${[action:'saveTaskDetail',id:task?.id?:0]}" method="post" onSuccess="processResponse(e)">
      <label for="tasktype_id">Тип:</label>
      <g:select name="tasktype_id" value="${task?.tasktype_id?:default_tasktype}" from="${tasktype}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}" disabled="${task?true:false}"/>         
      <g:if test="${!task}">
        <label for="term">Срок исполнения:</label>
        <g:datepicker class="normal nopad" style="width:200px" name="term" value="${task?.term?String.format('%td.%<tm.%<tY',task.term):''}"/> 
      </g:if><g:else>
        <label for="term">Срок исполнения:</label>
        <input type="text" id="term" disabled value="${task?.term?String.format('%td.%<tm.%<tY',task.term):''}"/>
      </g:else>
      <br/><label for="department_id">Отдел:</label>
      <g:select name="department_id" value="${task?.department_id}" from="${department}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}" onchange="getExecutor(this.value)" disabled="${task?true:false}"/>      
      <label for="executor">Адресат:</label>
      <span id="executor_span"><g:select name="executor" value="${task?.executor}" from="${executor}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}" disabled="${task?true:false}"/></span><br/>
      <label for="description">Описание:</label>
    <g:if test="${task}">
      <label for="is_remap" class="auto">
        <input type="checkbox" id="is_remap" name="is_remap" value="1" onclick="toggleDisabled()"/>
        Перенаправить
      </label>
    </g:if>
      <g:textArea rows="7" name="description" value="${task?.description?:description}" />
      <input type="hidden" name="link" value="${task?.link?:plink?:0}"/>
      <input type="hidden" id="modstatus" name="taskstatus" value="1"/>
      <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
    </g:formRemote>
    <hr class="admin">
    <div class="fright" id="btns">
    <g:if test="${task?.tasktype_id in [8,10,12]}">
      <g:link class="button" url="${[controller:'company', action:'detail', id:task.company_id]}" target="_blank">Компания &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
    </g:if><g:elseif test="${task?.tasktype_id==2}">
      <g:link class="button" url="${[controller:'agreement', action:'space', id:task.link]}" target="_blank">Договор &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
    </g:elseif>
      <input type="button" class="reset spacing" value="Отменить" onclick="returnToList()" />
    <g:if test="${!task}">
      <input type="button" value="Сохранить" onclick="submitForm(1)"/>
    </g:if><g:if test="${is_initiator}">
      <input type="button" value="Сохранить" onclick="submitForm(${task.taskstatus?:1})"/>
      <input type="button" value="Снять" onclick="submitForm(0)"/>
    </g:if><g:if test="${is_executor}">
      <g:if test="${!is_initiator}"><input type="button" value="Сохранить" onclick="submitForm(${task.taskstatus?:1})"/></g:if>
      <g:if test="${task.initiator==0}">
        <input type="button" value="OK, прочтено" onclick="submitForm(2)"/>
      </g:if><g:else>
        <input type="button" value="В работу" onclick="submitForm(4)"/>
        <input type="button" value="Выполнить" onclick="submitForm(6)"/>
        <input type="button" value="Не выполнить" onclick="submitForm(5)"/>
      </g:else>
    </g:if>
    </div>
    <div class="clear"></div>
  <g:if test="${task}">
    <div class="tabs">
      <ul class="nav">
        <li class="selected"><a href="javascript:void(0)" onclick="viewCell(0)">История изменений</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="taskEventForm" url="[action:'taskeventlist',id:task?.id?:0]" update="[success:'details']">
      <input type="submit" class="button" id="taskevent_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:'task',action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>