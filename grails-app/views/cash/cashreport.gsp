<html>
  <head>
    <title>Prisma: Касса - Отчет №${cashreport.id}</title>
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
      function submitForm(iStatus){
        $('modstatus').value = iStatus
        $('submit_button').click();
      }
      function showCars(iExpId){
        if(expcar_ids.indexOf(parseInt(iExpId))>-1) $('carsection').show();
        else $('carsection').hide();
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
          case 0: getHistory();break;
        }
      }
      function getHistory(){
        $('history_submit_button').click();
      }
      function init(){
        getHistory();
        new Autocomplete('expensetype_name', {
          serviceUrl:'${resource(dir:"autocomplete",file:"expensetype_autocomplete")}',
          width: 704,
          params: {user_id:${initiator?.id?:0}},
          onSelect: function(value, data){
            if (${is_canchangeexpensetype}){
              $('expensetype_id').value = data;
              showCars(data);
            }
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
    <h3 class="fleft">Отчет №${cashreport.id}</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку отчетов</a>
    <div class="clear"></div>
    <g:form name="newcashreportForm" url="${[action:'updatecashreport',id:cashreport.id]}" method="post" enctype="multipart/form-data" target="upload_target">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="executor" disabled>Подотчетное лицо:</label>
      <input type="text" id="executor" disabled name="executor" value="${executor?.name}"/>
      <label for="cashstatus" disabled>Статус:</label>
      <input type="text" id="cashstatus" disabled name="cashstatus" value="${cashstatus?.name}"/>
      <label for="department_id" disabled>Отдел:</label>
      <input type="text" id="department_id" disabled name="department_id" value="${department?.name}"/>
    <g:if test="${cashreport.confirmdate}">
      <label for="confirmdate" disabled>Подтверждено:</label>
      <input type="text" id="confirmdate" name="confirmdate" readonly value="${String.format('%td.%<tm.%<tY',cashreport.confirmdate)}"/>
    </g:if>
      <hr class="admin" />

      <label for="summa">Сумма:</label>
      <input type="text" id="summa" name="summa" ${!is_canchange?'readonly':''} value="${intnumber(value:cashreport.summa)}"/>
      <label for="repdate">Дата отчета:</label>
    <g:if test="${is_canchange}">
      <g:datepicker class="normal nopad" name="repdate" value="${String.format('%td.%<tm.%<tY',cashreport.repdate)}"/>
    </g:if><g:else>
      <input type="text" id="repdate" name="repdate" readonly value="${String.format('%td.%<tm.%<tY',cashreport.repdate)}"/>
    </g:else>
      <label for="description">Описание расхода:</label>
      <input type="text" class="fullline" id="description" name="description" value="${cashreport.description}" ${!is_canchange?'readonly':''}/>

      <label for="project_id">Проект:</label>
      <g:select name="project_id" value="${cashreport.project_id}" from="${project}" optionKey="id" optionValue="name" />
      <span id="carsection" style="${!(cashreport.expensetype_id in expcar_ids)?'display:none':''}"><label for="car_id">Машина:</label>
      <g:select name="car_id" value="${cashreport.car_id}" from="${cars}" optionKey="id" optionValue="name" noSelection="${['0':'не выбрана']}" disabled="${!is_canchangeexpensetype}"/></span>
      <br/><label for="expensetype_id">Доходы-расходы:</label>
      <g:select name="expensetype_id" class="fullline" value="${cashreport.expensetype_id}" from="${expensetypes}" optionKey="id" noSelection="${['0':'не выбран']}" onchange="showCars(this.value)" disabled="${!is_canchangeexpensetype}"/>
      <label for="expensetype_name">Доходы-расходы:</label>
      <input type="text" class="fullline" id="expensetype_name" value=""/>
    <g:if test="${is_canchangescan}">
      <label for="file">Новый скан:</label>
      <input type="file" id="file" name="file" style="width:256px"/>
    </g:if>
      <br/><label for="comment_dep">Комментарий:<br/><small>кассира отдела</small></label>
      <input type="text" class="fullline" id="comment_dep" name="comment_dep" value="${cashreport.comment_dep}" ${session.user.cashaccess!=2?'disabled':''}/>
      <label for="comment">Комментарий:<br/><small>главного кассира</small></label>
      <input type="text" class="fullline" id="comment" name="comment" value="${cashreport.comment}" ${session.user.cashaccess!=3?'disabled':''}/>

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <a class="button" href="${createLink(controller:'cash',action:'showscan',id:cashreport.file_id,params:[code:Tools.generateModeParam(cashreport.file_id)])}" target="_blank">
          Просмотреть скан-подтверждение &nbsp;<i class="icon-angle-right icon-large"></i>
        </a>
      <g:if test="${is_candelete}">
        <g:remoteLink class="button" before="if(!confirm('Вы действительно хотите удалить отчет?')) return false" url="${[controller:'cash',action:'deletereport',id:cashreport.id]}" onSuccess="returnToList()">Удалить &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink>
      </g:if>
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${is_canchange||is_canchangeexpensetype||is_canchangescan}">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(0)"/>
      </g:if>
      <g:if test="${session.user.cashaccess==2&&cashreport.modstatus in 0..1}">
        <input type="button" class="spacing" value="Отказать" onclick="submitForm(-1)"/>
      </g:if>
      <g:if test="${session.user.cashaccess==3&&(cashreport.modstatus in 0..1||(cashreport.modstatus==2&&cashreport.isThisMonth()))}">
        <input type="button" class="spacing" value="Отказать" onclick="submitForm(-2)"/>
      </g:if>
      <g:if test="${session.user.cashaccess==3&&cashreport.modstatus in 0..1}">
        <input type="button" class="spacing" value="Подтвердить" onclick="submitForm(2)"/>
      </g:if>
      <g:if test="${session.user.cashaccess==2&&cashreport.modstatus==0}">
        <input type="button" class="spacing" value="Подтвердить" onclick="submitForm(1)"/>
      </g:if>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      </div>
      <input type="hidden" id="modstatus" name="modstatus" value="0"/>
    </g:form>
    <div class="clear"></div>
  <g:if test="${cashreport}">
    <div class="tabs">
      <ul class="nav">
        <li class="selected"><a href="javascript:void(0)" onclick="viewCell(0)">История</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="historyForm" url="[action:'cashreporthistory',id:cashreport.id]" update="details">
      <input type="submit" class="button" id="history_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <iframe id="upload_target" name="upload_target" style="display:none"></iframe>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
