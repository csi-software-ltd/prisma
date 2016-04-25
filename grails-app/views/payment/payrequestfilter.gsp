<div class="clear"></div>    
<div class="info-box" style="display:none;margin-top:0">
  <span class="icon icon-info-sign icon-3x"></span>
  <ul id="infolist">
   Задание создано  
  </ul>
</div>
<div class="error-box" style="display:none">
  <span class="icon icon-warning-sign icon-3x"></span>
  <ul id="errorlist">
    <li></li>
  </ul>
</div>
<style type="text/css">
  select { width: auto; }
</style>
<g:formRemote name="payrequestForm" url="[controller:'payment',action:'payrequestlist']" update="list" onSuccess="scrollPayrequestToAnchor()">
  <label for="companyname">Компания:</label>
  <input type="text" id="companyname" name="companyname" value="${inrequest?.companyname}"/>
  <div id="fromcompany_autocomplete" class="autocomplete" style="display:none"></div>
  <label for="paydate">Дата платежа:</label>
  <g:datepicker class="normal nopad" name="paydate" value="${inrequest?.paydate?String.format('%td.%<tm.%<tY',inrequest.paydate):''}"/>
  <label class="auto" for="modstatus">Статус:</label>
  <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['отклонен', 'новый', 'в задании', 'выполнен', 'подтвержден']" keys="${-1..3}" noSelection="${['-100':'все']}"/>
  <label class="auto" for="paytype">Тип платежа</label>
  <g:select class="mini" name="paytype" value="${inrequest?.paytype}" from="${Paytype.list()}" optionKey="id" optionValue="name" noSelection="${['-100':'все']}"/>
  <label class="auto" for="platperiod_month">Период</label>
  <g:datePicker class="auto" name="platperiod" precision="month" value="${inrequest?.platperiod_month&&inrequest?.platperiod_year?new Date(inrequest.platperiod_year-1900,inrequest.platperiod_month-1,1):inrequest?.platperiod_year?new Date(inrequest.platperiod_year-1900,0,1):new Date()}" relativeYears="[114-new Date().getYear()..0]" noSelection="${['':'все']}"/>
  <label class="auto" for="is_noclient">
    <input type="checkbox" id="is_noclient" name="is_noclient" value="1" <g:if test="${inrequest?.is_noclient!=0}">checked</g:if> />
    Без клиентских
  </label>
  <label class="auto" for="is_noinner">
    <input type="checkbox" id="is_noinner" name="is_noinner" value="1" <g:if test="${inrequest?.is_noinner!=0}">checked</g:if> />
    Без внутренних
  </label>
  <label class="auto" for="instatus">Получение</label>
  <g:select class="mini" name="instatus" value="${inrequest?.instatus}" from="['отклонен', 'новый', 'в полете', 'получен', 'подтвержден']" keys="${-1..3}" noSelection="${['-100':'все']}"/>
  <label class="auto" for="pid">Код</label>
  <input type="text" class="mini" id="pid" name="pid" value="${inrequest?.pid?:''}"/>
  <label class="auto" for="project_id">Проект</label>
  <g:select class="mini" name="project_id" value="${inrequest?.project_id?:0}" from="${Project.list()}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/>
  <label class="auto" style="margin-right:0" for="is_notag">
    <input type="checkbox" id="is_notag" name="is_notag" value="1" <g:if test="${inrequest?.is_notag}">checked</g:if> />
    Не тегирован
  </label>
<g:if test="${iscanconfirm}">
  <label for="is_payconfirm" class="auto" style="margin-right:0">
    <input type="checkbox" id="is_payconfirm" name="is_payconfirm" value="1" <g:if test="${inrequest?.is_payconfirm}">checked</g:if> />
    Без выписки
  </label>
</g:if>
  <div class="fright">
    <input type="button" class="reset spacing" value="Сброс" onclick="resetPayrequestFilter()"/>
    <input type="button" id="form_submit_button_logic" value="Показать" onclick="submitPayrequestLogic()"/>
    <g:if test="${iscanedit}">
      <g:link action="newpayrequest" class="button">Новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
    </g:if>
    <input type="submit" id="form_submit_button" value="Показать" style="display:none"/>
  </div>
</g:formRemote>
<g:if test="${iscantask}">
  <div class="clear"></div>
  <g:formRemote name="taskForm" url="[controller:'payment',action:'createTaskpay']" onSuccess="processPayrequestTask(e)">
  <hr class="admin" style="width:70px;float:left;margin-left:15px"/><a style="text-decoration:none" href="javascript:void(0)">&nbsp;&nbsp;Создать&nbsp;задания&nbsp;</a><hr class="admin" style="width:730px;float:right"/>
  <label for="term">Срок исполнения:</label>
  <g:datepicker class="normal nopad" name="term" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
  <input type="hidden" id="payrequest_ids" name="payrequest_ids" value="0"/>
  <input type="hidden" id="creating_strategy" name="creating_strategy" value="0"/>
  <div class="fright">
    <input type="button" value="В оплату"  onclick="submitTaskLogic(0)"/>
    <input type="submit" id="form_task_submit_button" value="Показать" style="display:none"/>
  </div>
  </g:formRemote>
</g:if>
<div class="clear"></div>
<script type="text/javascript">
  new Autocomplete('companyname', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_autocomplete")}'
  });
  jQuery("#paydate").mask("99.99.9999",{placeholder:" "});
  if(${inrequest?.platperiod_month||!inrequest?.fromDetails?0:1}) $('platperiod_month').selectedIndex = 0;
  if(${inrequest?.platperiod_year||!inrequest?.fromDetails?0:1}) $('platperiod_year').selectedIndex = 0;
  $('form_submit_button').click();
</script>
