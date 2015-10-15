<html>
  <head>
    <title>Prisma: Платежи - Фактический платеж № ${payrequest.id}</title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript>
      var expcar_ids = ${expcar_ids};
      function init(){
        if (${tag?1:0}) jQuery('html,body').animate({scrollTop: jQuery('#expensetype_id').offset().top},'slow');
        if ($('indate')) jQuery("#indate").mask("99.99.9999",{placeholder:" "});
        new Autocomplete('expensetype_name', {
          serviceUrl:'${resource(dir:"autocomplete",file:"expensetype_autocomplete")}',
          onSelect: function(value, data){
            $('expensetype_id').value = data;
            showCars(data);
          }
        });
      }
      function returnToList(){
        $("returnToListForm").submit();
      }
      function showCars(iExpId){
        if(expcar_ids.indexOf(parseInt(iExpId))>-1) $('carsection').show();
        else $('carsection').hide();
      }
      function openCompany(id){        
        if(id>0)
          window.open('${createLink(controller:'company',action:'detail')}'+'/'+id);          
      }
      function openPers(id){        
        if(id>0)
          window.open('${createLink(controller:'user',action:'persdetail')}'+'/'+id);          
      }                                                     
      function openTaskpay(){
        var iId=${payrequest?.taskpay_id?:0};
        if(iId>0)
          window.open('${createLink(controller:'task',action:'taskpaydetail')}'+'/'+iId);
      }
      function selectAgreement(iValue,iType){
        if(iValue==-1)
          iValue = $('agreementtype_id').value;
        var iCompanyId = ${payrequest.paytype!=2?payrequest.fromcompany_id:payrequest.tocompany_id}
        var iContrCompanyId = ${payrequest.paytype!=2?payrequest.tocompany_id:payrequest.fromcompany_id}
        <g:remoteFunction controller='payment' action='agreement' params="'agreementtype_id='+iValue+'&company_id='+iCompanyId+'&ctrcompany_id='+iContrCompanyId+'&short=1'" update="agreement_span" />
        if (iValue=='3'){
          $('is_dop').up('label').show();
          $('is_fine').up('label').show();
          $('is_com').up('label').hide();
          $('is_dopmain').up('label').hide();
        } else if (iValue=='11'){
          $('is_dop').up('label').show();
          $('is_fine').up('label').hide();
          $('is_com').up('label').hide();
          $('is_dopmain').up('label').hide();
        } else if (iValue=='12'){
          $('is_dop').up('label').hide();
          $('is_fine').up('label').hide();
          $('is_dopmain').up('label').hide();
          $('is_com').up('label').show();
        } else if (iValue=='2'){
          $('is_dop').up('label').hide();
          $('is_fine').up('label').hide();
          $('is_com').up('label').hide();
          $('is_dopmain').up('label').show();
        } else {
          $('is_dop').up('label').hide();
          $('is_fine').up('label').hide();
          $('is_com').up('label').hide();
          $('is_dopmain').up('label').hide();
        }
      }
      function getSubclientsList(sClId){
        <g:remoteFunction controller='payment' action='subclientslist' params="'client_id='+sClId" update="subclientslist"/>
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
      .tabs a:hover,.tabs a.active,.tabs a.active i{color:#000}
    </style>    
  </head>
  <body onload="init();">
    <h3 class="fleft">Фактический платеж № ${payrequest.id}</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку платежей</a>
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
    
      <label for="paydate" disabled>Дата платежа:</label>
      <input type="text" id="paydate" value="${String.format('%td.%<tm.%<tY',payrequest.paydate)}" disabled />			
			<label for="inputdate" disabled>Дата ввода:</label>
      <input type="text" id="inputdate" value="${String.format('%td.%<tm.%<tY',payrequest.inputdate)}" disabled />
      <label for="execdate" disabled>Дата исполнения:</label>
      <input type="text" id="execdate" value="${payrequest.execdate?String.format('%td.%<tm.%<tY',payrequest.execdate):''}" disabled />
      <label for="indatemain" disabled>Дата получения:</label>
      <input type="text" id="indatemain" value="${payrequest.indate?String.format('%td.%<tm.%<tY',payrequest.indate):''}" disabled />
      <label for="modstatus" disabled>Статус:</label>
      <g:select name="modstatus" value="${payrequest?.modstatus?:0}" from="['отклонен', 'новый','в задании','выполнен','подтвержден']" keys="${-1..3}" disabled="true" />
      <label for="instatus" disabled>Статус получения:</label>
      <g:select name="instatus" value="${payrequest?.instatus?:0}" from="['отклонен', 'новый', 'в полете', 'получен', 'подтвержден']" keys="${-1..3}" disabled="true" />
      <label for="paytype" disabled>Тип:</label>
      <g:select name="paytype" value="${payrequest?.paytype?:0}" from="['исходящий','входящий','внутренний','списание', 'пополнение','агентские','абон. плата','откуп','комиссия','связанный входящий','внешний']" keys="${1..11}" disabled="true"/>
      <label for="taskpay_id" disabled>№ задания:<g:if test="${payrequest?.taskpay_id?:0}"><a href="javascript:void(0)" onclick="openTaskpay()"><i class="icon-pencil"></i></a></g:if></label>
      <input type="text" id="taskpay_id" value="${payrequest.taskpay_id?:''}" readonly />      

      <g:form name="paymentrequestDetailForm" url="${[action:'savepayrequestdetail',id:payrequest?.id?:0]}" method="post" enctype="multipart/form-data" target="upload_target">
  			<label for="summa" ${payrequest.modstatus!=0?'disabled':''}>Сумма:</label>
        <input type="text" id="summa" name="summa" value="${number(value:payrequest.summa)}" ${payrequest.modstatus!=0?'disabled':''}/>
        <label for="is_nds" class="auto" disabled>
          <input type="checkbox" id="is_nds" value="1" readonly <g:if test="${payrequest?.is_nds}">checked</g:if> />
          Наличие НДС
        </label><br />
        <label for="client" disabled>Клиент:</label>
        <input type="text" id="client" disabled value="${client?.name?:'нет'}"/>
        <label for="agent" disabled>Агент:</label>
        <input type="text" id="agent" disabled value="${agent?.name?:'нет'}"/>
        <label for="initiator" disabled>Инициатор:</label>
        <input type="text" id="initiator" value="${(payrequest.initiator==0)?'Система':(User.get(payrequest.initiator?:0)?.name?:'')}" readonly />

  			<hr class="admin">

        <label for="fromcompany" disabled>Плат. компания:</label>
        <input type="text" id="fromcompany" name="fromcompany" value="${payrequest?.fromcompany}" readonly/>
        <label for="frominn" disabled>ИНН плательщика:</label>
        <input type="text" id="frominn" name="frominn" value="${payrequest?.frominn}" readonly />
        <label for="oktmo" disabled>ОКТМО плат.:</label>
        <input type="text" id="oktmo" name="oktmo" value="${payrequest?.oktmo}" readonly />
        <br/><label for="frombank" disabled>Банк:</label>
        <input type="text" id="frombank" name="frombank" value="${Bank.get(Bankaccount.get(payrequest?.bankaccount_id?:0)?.bank_id?:'')?.name?:''}" readonly class="fullline"/>

        <hr class="admin">

        <label for="tocompany" disabled>Получ. компания:</label>
        <input type="text" id="tocompany" name="tocompany" value="${payrequest?.tocompany}" readonly/>
        <label for="toinn" disabled>ИНН получателя:</label>
        <input type="text" id="toinn" name="toinn" value="${payrequest?.toinn}" readonly />
        <label for="tobank" disabled>Банк получателя:</label>
        <input type="text" id="tobank" name="tobank" value="${payrequest?.tobank}" readonly />
        <label for="tobankbik" disabled>БИК банка получ.:</label>
        <input type="text" id="tobankbik" name="tobankbik" value="${payrequest?.tobankbik}" readonly />
        <label for="tocorraccount" disabled>Кор. счет банка<br/>получателя:</label>
        <input type="text" id="tocorraccount" name="tocorraccount" value="${payrequest?.tocorraccount}" readonly />
        <label for="toaccount" disabled>Расч. счет банка<br/>получателя:</label>
        <input type="text" id="toaccount" name="toaccount" value="${payrequest?.toaccount}" readonly />

        <hr class="admin">
        <label for="destination">Назначение платежа:</label>
        <g:textArea id="destination" name="destination" value="${payrequest?.destination?:''}" />

        <hr class="admin">

        <label for="paycat" disabled>Категория:</label>
        <g:select name="paycat" value="${payrequest?.paycat}" from="['договорной','бюджетный','персональный','прочий', 'банковский', 'счета']" keys="${1..6}" noSelection="${['0':'не выбран']}" disabled="true"/>
        <label class="auto" for="is_bankmoney">
          <input type="checkbox" id="is_bankmoney" name="is_bankmoney" value="1" <g:if test="${payrequest.is_bankmoney}">checked</g:if> />
          СС банка
        </label>
        <label class="auto" for="is_persdop" style="${payrequest.paycat!=Payment.PAY_CAT_PERS?'display:none':''}">
          <input type="checkbox" id="is_persdop" name="is_persdop" value="1" <g:if test="${payrequest?.is_dop}">checked</g:if> disabled/>
          Доп. платеж
        </label>
        <label class="auto" for="is_com" style="${payrequest.paycat!=Payment.PAY_CAT_AGR||!(payrequest.agreementtype_id in [12])?'display:none':''}">
          <input type="checkbox" id="is_com" name="is_com" value="1" <g:if test="${payrequest.is_dop}">checked</g:if>/>
          Возврат комиссии
        </label>
        <label class="auto" for="is_dopmain" style="${payrequest.paycat!=Payment.PAY_CAT_AGR||!(payrequest.agreementtype_id in [2])?'display:none':''}">
          <input type="checkbox" id="is_dopmain" name="is_dopmain" value="1" <g:if test="${payrequest?.is_dop}">checked</g:if>/>
          Доп. платежи
        </label>
        <label class="auto" for="is_dop" style="${payrequest.paycat!=Payment.PAY_CAT_AGR||!(payrequest.agreementtype_id in [3,11])?'display:none':''}">
          <input type="checkbox" id="is_dop" name="is_dop" value="1" <g:if test="${payrequest.is_dop}">checked</g:if>/>
          Погашение процентов
        </label>
        <label class="auto" for="is_fine" style="${payrequest.paycat!=Payment.PAY_CAT_AGR||payrequest.agreementtype_id!=3?'display:none':''}">
          <input type="checkbox" id="is_fine" name="is_fine" value="1" <g:if test="${payrequest?.is_fine}">checked</g:if>/>
          Пеня
        </label><br/>
      <g:if test="${payrequest.paycat==Payment.PAY_CAT_AGR}">
        <label for="agreementtype_id">Тип договора:</label>
        <g:select name="agreementtype_id" value="${payrequest.agreementtype_id}" from="${agrtypes}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}" onchange="selectAgreement(this.value)"/>
        <label for="agreement_id">№ договора:</label>
        <span id="agreement_span">
          <g:select name="agreement_id" value="${payrequest?.agreement_id}" from="${agr}" optionKey="id" optionValue="${{it.instanceOf(Agentagr)?(it?.name):(it?.anumber +' от '+String.format('%td.%<tm.%<tY',it.adate))}}" noSelection="${['0':'не выбран']}"/>
        </span>
      </g:if><g:elseif test="${payrequest.paycat==Payment.PAY_CAT_BUDG}">
        <label for="tax_id">Тип налога:</label>
        <g:select id="tax_id" class="fullline" name="tax_id" value="${payrequest.tax_id}" from="${tax}" optionKey="id" optionValue="name" noSelection="${[0:'не выбран']}" />
      </g:elseif><g:elseif test="${payrequest.paycat==Payment.PAY_CAT_PERS}">
        <label for="pers_id" disabled>Работник:<a href="javascript:void(0)" onclick="openPers($('pers_id').value)"><i class="icon-pencil"></i></a></label>
        <g:select name="pers_id" value="${payrequest.pers_id}" from="${pers}" optionKey="id" optionValue="shortname" noSelection="${['0':'не выбран']}" disabled="true"/>
        <label for="perstype" disabled>Должность в комп.:</label>
        <g:select name="perstype" value="${Pers.get(payrequest.pers_id?:0)?.perstype}" from="['сотрудник','директор','специалист']" keys="${1..3}" noSelection="${['0':'не выбран']}" disabled="true"/>
      </g:elseif><g:else>
        <label for="comment">Комментарий администратора:</label>
        <g:textArea name="comment" value="${payrequest?.comment}"/>
      </g:else>

        <hr class="admin">

      <g:if test="${payrequest.paytype<=3&&iscantag}">
        <label for="client_id">Клиент:</label>
        <g:select name="client_id" from="${clients}" value="${payrequest?.client_id}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}" onchange="getSubclientsList(this.value)"/>
        <label for="subclient_id">Подклиент:</label>
        <span id="subclientslist"><g:select name="subclient_id" from="${subclients}" value="${payrequest?.subclient_id}" optionKey="id" optionValue="name" noSelection="${['0':'нет']}"/></span>
        <label for="project_id">Проект:</label>
        <g:select name="project_id" value="${payrequest?.project_id?:defproject_id}" from="${project}" optionKey="id" optionValue="name" />
        <span id="carsection" style="${!(payrequest?.expensetype_id in expcar_ids)?'display:none':''}"><label for="car_id">Машина:</label>
        <g:select name="car_id" value="${payrequest?.car_id}" from="${cars}" optionKey="id" optionValue="name" noSelection="${['0':'не выбрана']}" /></span>
        <br/><label for="expensetype_id">Доходы-расходы:</label>
        <g:select name="expensetype_id" class="fullline" value="${payrequest?.expensetype_id?:0}" from="${expensetype}" optionKey="id" noSelection="${['0':'не выбран']}" onchange="showCars(this.value)"/>
        <label for="expensetype_name">Доходы-расходы:</label>
        <input type="text" class="fullline" id="expensetype_name" value=""/>
        <label for="tagcomment">Комментарий:</label>
        <input type="text" id="tagcomment" name="tagcomment" value="${payrequest?.tagcomment?:''}" class="fullline"/>
        <hr class="admin">
      </g:if>
      <g:if test="${payrequest?.modstatus==2}">
        <label for="file">Новый скан:</label>
        <input type="file" id="file" name="file" style="width:256px"/>
      </g:if>
        <div class="fright" id="btns">
        <g:if test="${payrequest?.file_id?:0}">
          <a class="button" href="${createLink(controller:'payment',action:'showscan',id:payrequest?.file_id,params:[code:Tools.generateModeParam(payrequest?.file_id)])}" target="_blank">Скан платежа</a>
        </g:if>
        <g:if test="${payrequest.instatus==2}">
          <g:remoteLink class="button" url="${[controller:'payment',action:'cancelreceiveprincome',id:payrequest.id]}" onSuccess="location.reload(true)">Отменить получение</g:remoteLink>
        </g:if>
        <g:if test="${iscansetrefill}">
          <g:remoteLink class="button" url="${[controller:'payment',action:'payrequestsetrefill',id:payrequest.id]}" onSuccess="location.reload(true)">Выполнить за счет клиента</g:remoteLink>
        </g:if>
        <g:if test="${iscandecline}">
          <g:remoteLink class="button" url="${[controller:'payment',action:'payrequestdecline',id:payrequest.id]}" onSuccess="location.reload(true)">Отклонить</g:remoteLink>
        </g:if>
        <g:if test="${iscanrestore}">
          <g:remoteLink class="button" url="${[controller:'payment',action:'payrequestrestore',id:payrequest.id]}" onSuccess="location.reload(true)">Восстановить</g:remoteLink>
        </g:if>
        <g:if test="${payrequest.paytype==5&&!payrequest.client_id}">
          <g:remoteLink class="button" url="${[controller:'payment',action:'cancellrefill',id:payrequest.id]}" onSuccess="location.reload(true)">Выполнить за счет СС</g:remoteLink>
        </g:if>
        <g:if test="${payrequest.modstatus==0&&payrequest.taskpay_id==0&&payrequest.paytype in [1,3]}">
          <input type="button" class="button" value="В оплату" onclick="$('is_task').value=1;$('submit_button').click();"/>
        </g:if>
          <input type="submit" id="submit_button" value="Сохранить">
        </div>
        <input type="hidden" id="is_task" name="is_task" value="0"/>
      </g:form>
      <div class="clear"></div>

      <hr class="admin">

    <g:if test="${payrequest.instatus==1}">
      <g:formRemote name="inconfirmForm" url="${[controller:'payment',action:'receiveprincome',id:payrequest.id]}" onSuccess="location.reload(true)">
        <label for="indate">Дата получения:</label>
        <g:datepicker class="normal nopad" style="margin-right:108px" name="indate" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
        <div class="fright" id="btns">
          <input type="submit" class="button" value="Подтвердить получение"/>
        </div>
      </g:formRemote>
    </g:if>
    <iframe id="upload_target" name="upload_target" style="display:none"></iframe>
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:'payment',action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
