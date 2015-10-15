<html>
  <head>
    <title>Prisma: <g:if test="${project}">Редактирование проекта ${project.name}</g:if><g:else>Добавление нового проекта</g:else></title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript>                  
      function returnToList(){
        $("returnToListForm").submit();
      }            
      function processResponse(e){
        var sErrorMsg = '';
        ['name'].forEach(function(ids){
          $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>Не заполнено обязательное поле "Название"</li>'; $("name").addClassName('red');break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${project?1:0}){
          location.reload(true);
        } else if(e.responseJSON.project_id){
          location.assign('${createLink(controller:'catalog',action:'project')}'+'/'+e.responseJSON.project_id);
        } else
          location.assign('${createLink(controller:'catalog',action:'projects')}');
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
          case 0: getPayments();break;
        }
      }
      function getPayments(){
        if(${project?1:0}) $('payments_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      label.long{width:370px}
      input.normal{width:202px}
      input.mini{width:60px!important}
    </style>
  </head>
  <body onload="init()">
    <h3 class="fleft"><g:if test="${project}">Проект "${project.name}"</g:if><g:else>Добавление нового проекта</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку проектов</a>
    <div class="clear"></div>

    <div class="error-box" style="display:none">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>

    <g:formRemote name="projectDetailForm" url="${[action:'updateproject',id:project?.id?:0]}" method="post" onSuccess="processResponse(e)">
      <label for="name">Название:</label>
      <input type="text" class="fullline" id="name" name="name" value="${project?.name}" />
      <label for="enddate">Дата начала:</label>
      <g:datepicker class="normal nopad" name="startdate" value="${project?.startdate?String.format('%td.%<tm.%<tY',project.startdate):''}"/>
      <label for="enddate">Дата завершения:</label>
      <g:datepicker class="normal nopad" name="enddate" value="${project?.enddate?String.format('%td.%<tm.%<tY',project.enddate):''}"/><br/>
      <g:if test="${project}">
        <label for="modstatus" disabled>Статус:</label>
        <input type="text" id="modstatus" value="${project?.modstatus?'активный':'не активный'}" disabled />
        <label for="loansaldo" disabled>Заемные ср-ва:</label>
        <input type="text" id="loansaldo" disabled value="${intnumber(value:project.loansaldo)}"/>
        <label for="income" disabled>Кредит:</label>
        <input type="text" id="income" disabled value="${number(value:income)}"/>
        <label for="outlay" disabled>Дебет:</label>
        <input type="text" id="outlay" disabled value="${number(value:outlay)}"/>
      </g:if>
      <label for="description">Описание:</label>
      <g:textArea name="description" value="${project?.description}" />
      <hr class="admin">
      <div class="fright" id="btns">
      <g:if test="${iscanedit}">
        <input type="submit" id="submit_button" value="Сохранить"/>
      </g:if>
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      </div>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${project}">
    <div class="tabs">
      <ul class="nav">
        <li class="selected"><a href="javascript:void(0)" onclick="viewCell(0)">Платежи</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="paymentsForm" url="[action:'projectpayments',id:project.id]" update="details">
      <input type="submit" class="button" id="payments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:'catalog',action:'projects',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>