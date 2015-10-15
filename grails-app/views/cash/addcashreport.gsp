<html>
  <head>
    <title>Prisma: Касса - Новый отчет</title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript>
      var expcar_ids = ${expcar_ids};
      function returnToList(){
        $("returnToListForm").submit();
      }
      function showCars(iExpId){
        if(expcar_ids.indexOf(parseInt(iExpId))>-1) $('carsection').show();
        else $('carsection').hide();
      }
      function toggledopsection(sType){
        if(sType!='0'){
          if (${session.user.cashaccess==3}) $('depsection').show();
          $('execsection').hide();
        } else {
          $('execsection').show();
          $('depsection').hide();
        }
      }
      function init(){
        new Autocomplete('expensetype_name', {
          serviceUrl:'${resource(dir:"autocomplete",file:"expensetype_autocomplete")}',
          width: 704,
          params: {user_id:${session.user.id}},
          onSelect: function(value, data){
            $('expensetype_id').value = data;
            showCars(data);
          }
        });
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
      .tabs a:hover,.tabs a.active,.tabs a.active i{color:#000}
    </style>
  </head>
  <body onload="init()">
    <h3 class="fleft">Новый отчет</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку отчетов</a>
    <div class="clear"></div>
    <g:form name="newcashreportForm" url="${[action:'incertcashreport']}" method="post" enctype="multipart/form-data" target="upload_target">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="summa">Сумма:</label>
      <input type="text" id="summa" name="summa" value=""/>
      <label for="repdate">Дата отчета:</label>
      <g:datepicker class="normal nopad" name="repdate" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
      <label for="description">Описание расхода:</label>
      <input type="text" class="fullline" id="description" name="description" value=""/>
      <label for="project_id">Проект:</label>
      <g:select name="project_id" value="${defproject_id}" from="${project}" optionKey="id" optionValue="name" />
      <span id="carsection" style="display:none"><label for="car_id">Машина:</label>
      <g:select name="car_id" from="${cars}" optionKey="id" optionValue="name" noSelection="${['0':'не выбрана']}"/></span>
      <br/><label for="expensetype_id">Доходы-расходы:</label>
      <g:select name="expensetype_id" class="fullline" from="${expensetypes}" optionKey="id" noSelection="${['0':'не выбран']}" onchange="showCars(this.value)" />
      <label for="expensetype_name">Доходы-расходы:</label>
      <input type="text" class="fullline" id="expensetype_name" value=""/>
    <g:if test="${session.user.cashaccess in 2..3}">
      <label for="type">Отчитываться:</label>
      <g:select name="type" from="['За отдел','За сотрудника']" keys="10" onchange="toggledopsection(this.value)"/>
      <span id="depsection" style="${session.user.cashaccess==2?'display:none':''}"><label for="department_id">Отдел:</label>
      <g:select name="department_id" value="${user.department_id}" from="${departments}" optionKey="id" optionValue="name"/></span>
      <span id="execsection" style="display:none"><label for="executor">Исполнитель:</label>
      <g:select name="executor" value="${user.id}" from="${perslist}" optionKey="id" optionValue="name"/></span>
    </g:if>
      <label for="file">Скан:</label>
      <input type="file" id="file" name="file" style="width:256px"/>

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
        <input type="submit" class="spacing" value="Сохранить"/>
      </div>
    </g:form>
    <iframe id="upload_target" name="upload_target" style="display:none"></iframe>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>