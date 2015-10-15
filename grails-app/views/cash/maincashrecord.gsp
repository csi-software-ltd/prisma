<html>
  <head>
    <title>Prisma: Главная касса - <g:if test="${cashrecord}">Операция №${cashrecord.id}</g:if><g:else>Новая операция</g:else></title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript>
      var nontaggedclasses = <g:rawHtml>${nontaggedclasses}</g:rawHtml>;
      var expcar_ids = ${expcar_ids};
      function returnToList(){
        $("returnToListForm").submit();
      }
      function showCars(iExpId){
        if(expcar_ids.indexOf(parseInt(iExpId))>-1) $('carsection').show();
        else $('carsection').hide();
      }
      function toggleagent(sClass){
        if (sClass=='3'){
          $("addcontainer").show();
          $("contragent").hide();
          $("indeposit").hide();
          $("loan").hide();
          $("parking").hide();
          $("agent").show();
        } else if (sClass=='18'||sClass=='19'){
          $("addcontainer").show();
          $("contragent").hide();
          $("agent").hide();
          $("loan").hide();
          $("parking").hide();
          $("indeposit").show();
        } else if (sClass=='7'){
          $("addcontainer").show();
          $("contragent").hide();
          $("indeposit").hide();
          $("agent").hide();
          $("parking").hide();
          $("loan").show();
        } else if (sClass=='12'){
          $("addcontainer").show();
          $("contragent").hide();
          $("indeposit").hide();
          $("agent").hide();
          $("loan").hide();
          $("parking").show();
        } else {
          if($('maincashtype').value==2||sClass=='8'||sClass=='9'||sClass=='17') $("addcontainer").hide();
          else $("addcontainer").show();
          $("agent").hide();
          $("indeposit").hide();
          $("loan").hide();
          $("parking").hide();
          $("contragent").show();
        }
        updatepers();
        toggletagsection();
      }
      function getcashclasses(sType){
        <g:remoteFunction controller='cash' action='maincashclasslist' params="'type='+sType" update="cashclasssection" onComplete="toggleaddcontainer(e,sType)"/>
      }
      function toggletagsection(){
        $('expensetype_name').value = ''
        var cashclass = $('maincashclass').value;
        var ntclass = nontaggedclasses.find(function(el){ return el.id==cashclass });
        if (ntclass)
          $('expensetype_id').value = ntclass.exp_id;
        else
          $('expensetype_id').value = '0';
      }
      function toggleaddcontainer(e,sType){
        if (sType!='2') $('agentrate').show();
        else $('agentrate').hide();
        if (sType!='2') {
          $("agent").hide();
          $("contragent").show();
          $("addcontainer").show();
        } else $("addcontainer").hide();
        updatepers();
        toggletagsection();
      }
      function updatepers(){
        var depId = $('department_id').value;
        <g:remoteFunction controller='cash' action='maincashperslist' params="'department_id='+depId" update="pers" onSuccess="togglepers()"/>
      }
      function togglepers(){
        if (($('department_id').value=='0'&&$('maincashclass').value!=4)||$('maincashclass').value==16) $("pers").show();
        else $("pers").hide();
      }
      function getagents(sAgr){
        if ($('maincashtype').value!=2)
        <g:remoteFunction controller='cash' action='maincashagentlist' params="'agentagr='+sAgr" update="agentrate" onSuccess="\$('agentrate').show()"/>
      }
      function init(){
        new Autocomplete('expensetype_name', {
          serviceUrl:'${resource(dir:"autocomplete",file:"expensetype_autocomplete")}',
          width: 704,
          onSelect: function(value, data){
            $('expensetype_id').value = data;
            showCars(data);
          }
        });
        jQuery("#operationdate").mask("99.99.9999",{placeholder:" "});
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
      .tabs a:hover,.tabs a.active,.tabs a.active i{color:#000}
      select[id*="platperiod"] { width: 125px; }
    </style>
  </head>
  <body onload="init()">
    <h3 class="fleft"><g:if test="${cashrecord}">Операция №${cashrecord.id} от ${String.format('%td.%<tm.%<tY',cashrecord.inputdate)}</g:if><g:else>Новая операция</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К главной кассе</a>
    <div class="clear"></div>
    <g:form name="newcashreportForm" url="${[action:'updatemaincashrecord',id:cashrecord?.id?:0]}" method="post" enctype="multipart/form-data" target="upload_target">

    <g:if test="${cashrecord&&cashrecord.id != Cash.getLastId()}">
      <div class="info-box" style="margin-top:0">
        <span class="icon icon-info-sign icon-3x"></span>
        <ul id="infolist">
          <li>Изменения по операции возможны только в секции тегирования. Для изменения назначения операции введите обратную данной операции и затем правильный корректирующий вариант операции.</li>
        </ul>
      </div>
    </g:if>
      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="summa">Сумма:</label>
      <input type="text" id="summa" name="summa" value="${intnumber(value:cashrecord?.summa)}"/>
      <label for="valuta_id">Валюта:</label>
      <g:select id="valuta_id" name="valuta_id" from="${Valuta.findAllByModstatus(1)}" value="${cashrecord?.valuta_id?:857}" optionValue="name" optionKey="id"/>
      <label for="maincashtype">Тип:</label>
      <g:select name="maincashtype" value="${cashrecord?.type}" from="${['Выдача','Получение','Возврат','Финансирование','Начисление']}" keys="12345" onchange="getcashclasses(this.value)"/>
      <span id="cashclasssection"><label for="maincashclass">Класс:</label>
      <g:select name="maincashclass" value="${cashrecord?.cashclass}" from="${cashclasses}" noSelection="${['0':'не выбрано']}" optionKey="id" optionValue="name" onchange="toggleagent(this.value)"/></span>
      <span id="addcontainer" style="${(cashrecord?.type==2&&!(cashrecord?.cashclass in [3,18,19]))||(cashrecord?.cashclass in [8,9])?'display:none':''}">
        <span id="contragent" style="${cashrecord?.cashclass in [3,7]?'display:none':''}">
          <label for="department_id">Отдел:</label>
          <g:select id="department_id" name="department_id" from="${departments}" value="${cashrecord?.department_id}" optionValue="name" optionKey="id" noSelection="${['0':'Вне отдела']}" onchange="updatepers()"/>
          <span id="pers" style="${cashrecord?.pers_id==0&&cashrecord?.department_id>0?'display:none':''}">
            <label for="pers_id">Подотчетное лицо:</label>
            <g:select id="pers_id" name="pers_id" from="${executors}" value="${cashrecord?.pers_id}" optionValue="name" optionKey="id" noSelection="${['0':'не выбран']}"/></span>
        </span>
        <span id="agent" style="${cashrecord?.cashclass!=3?'display:none':''}">
          <label for="agentagr_id">Агентский договор:</label>
          <g:select id="agentagr_id" name="agentagr_id" from="${agentagrs}" value="${cashrecord?.agentagr_id}" optionValue="name" optionKey="id" noSelection="${['0':'не выбран']}" onchange="getagents(this.value)"/>
          <span id="agentrate" style="${cashrecord?.type==2?'display:none':''}"><label for="agent_id">Агент:</label>
          <g:select name="agent_id" from="${agents}" value="${cashrecord?.agent_id}" optionKey="agent_id" optionValue="agent_name" noSelection="${['0':'не выбран']}"/></span>
        </span>
        <span id="indeposit" style="${!(cashrecord?.cashclass in [18,19])?'display:none':''}">
          <label for="indeposit_id">Договор депозита:</label>
          <g:select id="indeposit_id" name="indeposit_id" from="${indeposits}" value="${cashrecord?.indeposit_id}" optionKey="id" noSelection="${['0':'не выбран']}"/>
        </span>
        <span id="loan" style="${cashrecord?.cashclass!=7?'display:none':''}">
          <label for="loaner_id">Заёмщик:</label>
          <g:select id="loaner_id" name="loaner_id" from="${loanusers}" value="${cashrecord?.pers_id}" optionValue="name" optionKey="id" noSelection="${['0':'не выбран']}"/></span>
        <span id="parking" style="${cashrecord?.cashclass!=12?'display:none':''}">
          <label for="parkinger_id">Сотрудник:</label>
          <g:select id="parkinger_id" name="parkinger_id" from="${parkingusers}" value="${cashrecord?.pers_id}" optionValue="name" optionKey="id" noSelection="${['0':'не выбран']}"/></span>
      </span><br/>
      <label for="operationdate">Дата операции:</label>
      <g:datepicker style="margin-right:108px" name="operationdate" value="${String.format('%td.%<tm.%<tY',cashrecord?.operationdate?:new Date())}"/>
      <label for="platperiod_month">Месяц:</label>
      <g:datePicker name="platperiod" precision="month" value="${cashrecord?.platperiod?:new Date()}" relativeYears="[114-new Date().getYear()..0]"/>
      <label for="file">Скан:</label>
      <input type="file" id="file" name="file" style="width:256px"/><br/>

      <hr class="admin" />

    <g:if test="${cashrecord}">
      <label for="admin">Автор:</label>
      <input type="text" id="admin" value="${admin}" disabled/>
      <label for="tagadmin">Тегировал:</label>
      <input type="text" id="tagadmin" value="${tagadmin}" disabled/>
    </g:if>

      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" id="comment" value="${cashrecord?.comment}" />
      <label for="project_id">Проект:</label>
      <g:select name="project_id" value="${cashrecord?.project_id?:defproject_id}" from="${project}" optionKey="id" optionValue="name" />
      <span id="carsection" style="${!(cashrecord?.expensetype_id in expcar_ids)?'display:none':''}"><label for="car_id">Машина:</label>
      <g:select name="car_id" value="${cashrecord?.car_id}" from="${cars}" optionKey="id" optionValue="name" noSelection="${['0':'не выбрана']}" /></span>
      <br/><label for="expensetype_id">Доходы-расходы:</label>
      <g:select name="expensetype_id" class="fullline" value="${cashrecord?.expensetype_id?:0}" from="${expensetype}" optionKey="id" noSelection="${['0':'не выбран']}" onchange="showCars(this.value)"/>
      <label for="expensetype_name">Доходы-расходы:</label>
      <input type="text" class="fullline" id="expensetype_name" value=""/>

      <hr class="admin">

      <div class="fright" id="btns" style="padding-top:10px">
      <g:if test="${cashrecord?.receipt}">
        <a class="button" href="${createLink(controller:'cash',action:'showscan',id:cashrecord.receipt,params:[code:Tools.generateModeParam(cashrecord.receipt)])}" target="_blank">
          Просмотреть скан-подтверждение &nbsp;<i class="icon-angle-right icon-large"></i>
        </a>
      </g:if>
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanadd||(iscanedit&&cashrecord)}">
        <input type="submit" class="spacing" value="Сохранить"/>
      </g:if>
      </div>
    </g:form>
    <iframe id="upload_target" name="upload_target" style="display:none"></iframe>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
