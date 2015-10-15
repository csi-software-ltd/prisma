<html>
  <head>
    <title>Prisma: Касса отдела - <g:if test="${cashrecord}">Операция №${cashrecord.id}</g:if><g:else>Новая операция</g:else></title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function togglecontragent(sType){
        if (sType!='2') $("contragent").show();
        else $("contragent").hide();
      }
      function togglepers(sType){
        if (sType=='0') $("pers").show();
        else $("pers").hide();
      }
      function updatepersspan(sDepartment){
        <g:remoteFunction controller='cash' action='userperslist' params="'department_id='+sDepartment" update="persspan" />
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
    <h3 class="fleft"><g:if test="${cashrecord}">Операция №${cashrecord.id} от ${String.format('%td.%<tm.%<tY',cashrecord.inputdate)}</g:if><g:else>Новая операция</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К кассе отдела</a>
    <div class="clear"></div>
    <g:form name="newcashreportForm" url="${[action:'updatedepcashrecord']}" method="post" enctype="multipart/form-data" target="upload_target">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="summa">Сумма:</label>
      <input type="text" id="summa" name="summa" value="${intnumber(value:cashrecord?.summa)}"/>
      <label for="valuta_id">Валюта:</label>
      <g:select id="valuta_id" name="valuta_id" from="${Valuta.findAllByModstatus(1)}" value="${cashrecord?.valuta_id?:857}" optionValue="name" optionKey="id" disabled="true"/>
      <label for="depcashtype">Тип:</label>
      <g:select name="depcashtype" value="${cashrecord?.type}" from="${['Выдача','Возврат']+(cashrecord?['Возврат в главную кассу','Получение','Начисление','Отчет']:[])}" keys="134259" disabled="${cashrecord?true:false}"/>
      <label for="depcashclass">Класс:</label>
      <g:select name="depcashclass" value="${cashrecord?.cashclass}" from="${['Зарплата','Подотчет']+(cashrecord?['Заем','Расход','Штраф']:[])}" keys="12345" noSelection="${['0':'не выбрано']}" disabled="${cashrecord?true:false}"/>
      <label for="department_id">Отдел:</label>
      <g:select id="department_id" name="department_id" from="${departments}" value="${!cashrecord?user.department_id:cashrecord.department_id?:Department.findByIs_tehdir(1).id}" optionValue="name" optionKey="id" disabled="${cashrecord?true:false}" onchange="updatepersspan(this.value)"/>
      <span id="persspan"><label for="pers_id">Сотрудник:</label>
      <g:select name="pers_id" value="${cashrecord?.pers_id}" from="${perslist}" optionValue="name" optionKey="id" noSelection="${['0':'не выбран']}" disabled="${cashrecord?true:false}"/></span>
      <label for="operationdate">Дата операции:</label>
      <g:datepicker style="margin-right:108px" name="operationdate" value="${String.format('%td.%<tm.%<tY',cashrecord?.operationdate?:new Date())}"/>
      <label for="file">Скан:</label>
      <input type="file" id="file" name="file" style="width:256px"/><br/>
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" id="comment" value="${cashrecord?.comment}" />

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
      <g:if test="${cashrecord?.receipt}">
        <a class="button" href="${createLink(controller:'cash',action:'showscan',id:cashrecord.receipt,params:[code:Tools.generateModeParam(cashrecord.receipt)])}" target="_blank">Просмотреть скан-подтверждение &nbsp;<i class="icon-angle-right icon-large"></i></a>
      </g:if>
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
        <input type="submit" class="spacing" value="Сохранить"/>
      </g:if>
      </div>
    </g:form>
    <iframe id="upload_target" name="upload_target" style="display:none"></iframe>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
