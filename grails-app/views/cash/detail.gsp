<html>
  <head>
    <title>Prisma: Касса - <g:if test="${zakaz}">Заявка №${zakaz.id}</g:if><g:else>Новая заявка</g:else></title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function changeBasetype(iType){
        $('basetype1').hide();
        $('basetype2').hide();
        $('basetype'+iType).show();
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['summa','purpose','department_id','executor','comment'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['todate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('summa').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Цель заявки"])}</li>'; $('purpose').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Отдел"])}</li>'; $('department_id').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Подотчетное лицо"])}</li>'; $('executor').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Комментарий"])}</li>'; $('comment').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["На дату"])}</li>'; $('todate').up('span').addClassName('k-error-colored'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${zakaz?1:0}){
          location.reload(true);
        } else if(e.responseJSON.zakaz){
          location.assign('${createLink(controller:controllerName,action:'detail')}'+'/'+e.responseJSON.zakaz);
        } else
          location.assign('${createLink(controller:controllerName,action:'index')}');
      }
      function submitForm(iStatus){
        $('modstatus').value = iStatus
        $('submit_button').click();
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
          case 0: getEvents();break;
        }
      }
      function getEvents(){
        if(${zakaz?1:0}) $('events_submit_button').click();
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
      .tabs a:hover,.tabs a.active,.tabs a.active i{color:#000}
    </style>
  </head>
  <body onload="getEvents()">
    <h3 class="fleft"><g:if test="${zakaz}">Заявка №${zakaz.id}</g:if><g:else>Новая заявка</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку заявок</a>
    <div class="clear"></div>
    <g:formRemote name="requestDetailForm" url="${[action:'update',id:zakaz?.id?:0]}" method="post" onSuccess="processResponse(e)">

    <g:if test="${session.user.cashaccess==3&&zakaz?.cashrequest_id}">
      <div class="info-box" style="margin-top:0">
        <span class="icon icon-info-sign icon-3x"></span>
        <ul id="infolist">
          <li>Внимание! Заявка находится в запросе на пополнение кассы № <g:link controller="cash" action="cashrequest" id="${zakaz?.cashrequest_id}">${zakaz.cashrequest_id}</g:link></li>
        </ul>
      </div>
    </g:if>

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

    <g:if test="${zakaz}">
      <label for="zakaz_id" disabled>Код заявки:</label>
      <input type="text" id="zakaz_id" disabled value="${zakaz.id}" />
      <label for="inputdate" disabled>Дата создания:</label>
      <input type="text" name="inputdate" disabled value="${String.format('%td.%<tm.%<tY %<tT',zakaz.inputdate)}" />
      <label for="zakaz_modstatus" disabled>Статус заявки:</label>
      <input type="text" id="zakaz_modstatus" disabled value="${cashstatus[zakaz.modstatus]}" />
      <label for="moddate" disabled>Дата изменения:</label>
      <input type="text" name="moddate" disabled value="${String.format('%td.%<tm.%<tY %<tT',zakaz.moddate)}" />
      <label for="department" disabled>Отдел:</label>
      <input type="text" id="department" disabled value="${department?:'вне отдела'}" />
      <label for="initiator" disabled>Инициатор:</label>
      <input type="text" id="initiator" disabled value="${initiator}" />
      <hr class="admin" />
    </g:if>

      <label for="summa">Сумма:</label>
      <input type="text" id="summa" name="summa" ${zakaz?.modstatus>2&&zakaz?.modstatus!=5?'disabled':''} value="${intnumber(value:zakaz?.summa)}"/>
      <label for="valuta_id">Валюта:</label>
      <g:select id="valuta_id" name="valuta_id" from="${Valuta.findAllByModstatus(1)}" value="${zakaz?.valuta_id?:857}" optionValue="name" optionKey="id" disabled="${zakaz?.modstatus>1}"/>
      <label for="purpose">Цель заявки:</label>
      <input class="fullline" type="text" id="purpose" name="purpose" value="${zakaz?.purpose}"/>
      <label for="todate">На дату:</label>
      <g:datepicker class="normal nopad" name="todate" value="${String.format('%td.%<tm.%<tY',zakaz?.todate?:Tools.getNextWorkedDate(new Date()))}"/><br/>
    <g:if test="${!zakaz && session.user.cashaccess==3}">
      <label for="basetype">Выдавать:</label>
      <g:select id="basetype" name="basetype" from="['На отдел','На подотчетное лицо']" keys="${1..2}" onchange="changeBasetype(this.value)"/>
      <span id="basetype1">
        <label for="department_id">Отдел:</label>
        <g:select id="department_id" name="department_id" from="${departments}" value="" optionValue="name" optionKey="id" noSelection="${['0':'не выбран']}"/>
      </span>
      <span id="basetype2" style="display:none">
        <label for="executor">Подотчетное лицо:</label>
        <g:select id="executor" name="executor" from="${executors}" value="" optionValue="name" optionKey="id" noSelection="${['0':'не выбрано']}"/>
      </span>
    </g:if>
    <g:if test="${zakaz}">
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" id="comment" value="${zakaz?.comment}" />
    </g:if>

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
      <g:if test="${session.user.cashaccess==3&&zakaz?.modstatus==1&&!zakaz?.cashrequest_id}">
        <g:remoteLink class="button" url="${[controller:'cash', action:'completezakaz', id:zakaz.id]}" before="if(!confirm('Вы действительно хотите оплатить заявку без пополнения?')) return false" onSuccess="location.reload(true)">Оплатить &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink>
      </g:if>
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(0)"/>
      <g:if test="${session.user.cashaccess==3&&zakaz?.modstatus==1&&!zakaz?.cashrequest_id}">
        <input type="button" class="spacing" value="Отказать" onclick="submitForm(5)"/>
      </g:if>
      <g:if test="${session.user.id==zakaz?.initiator&&zakaz?.modstatus==5}">
        <input type="button" class="spacing" value="Подать повторно" onclick="submitForm(1)"/>
      </g:if>
      <g:if test="${session.user.id==zakaz?.initiator&&zakaz?.modstatus==1&&!zakaz?.cashrequest_id}">
        <input type="button" class="spacing" value="Удалить" onclick="submitForm(-1)"/>
      </g:if>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      </div>
      <input type="hidden" id="modstatus" name="modstatus" value="0"/>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${zakaz}">
    <div class="tabs">
      <ul class="nav">
        <li class="selected"><a href="javascript:void(0)" onclick="viewCell(0)">События</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="eventsForm" url="[action:'events',id:zakaz.id]" update="details">
      <input type="submit" class="button" id="events_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>