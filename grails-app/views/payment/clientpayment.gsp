<html>
  <head>
    <title>Prisma: Платежи - Платеж №${payrequest.id}</title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript>
      var supclients = ${supclients};
      var midclients = ${midclients};
      function returnToList(){
        $("returnToListForm").submit();
      }
      function submitForm(){
        $('submit_button').click();
      }
      function computeComission(){
        var summa = parseFloat(${payrequest.summa.toDouble()}).toFixed(2);
        var clientcomission = 0
        if ($('clientcommission')) clientcomission = parseFloat($('clientcommission').value.replace(",",".").replace("\u00A0","")).toFixed(2);
        var comtype = $('percenttype').value;
        var percent = parseFloat($('compercent').value.replace(",",".")).toFixed(2);
        if(comtype=='1') $('comission').value = ((summa  - clientcomission) * percent / ( 100 - percent )).toFixed(2);
        else $('comission').value = ((summa  - clientcomission) * percent / 100).toFixed(2);
        computeSubComission();
      }
      function computeMidComission(){
        var summa = parseFloat(${payrequest.summa.toDouble()}).toFixed(2);
        var clientcomission = 0
        if ($('clientcommission')) clientcomission = parseFloat($('clientcommission').value.replace(",",".").replace("\u00A0","")).toFixed(2);
        var comtype = $('percenttype').value;
        var percent = parseFloat($('midpercent').value.replace(",",".")).toFixed(2);
        if(comtype=='1') $('midcomission').value = ((summa  - clientcomission) * percent / ( 100 - percent )).toFixed(2);
        else $('midcomission').value = ((summa  - clientcomission) * percent / 100).toFixed(2);
      }
      function computeSupComission(){
        var summa = parseFloat(${payrequest.summa.toDouble()}).toFixed(2);
        var clientcomission = 0
        if ($('clientcommission')) clientcomission = parseFloat($('clientcommission').value.replace(",",".").replace("\u00A0","")).toFixed(2);
        var comtype = $('percenttype').value;
        var percent = parseFloat($('supcompercent').value.replace(",",".")).toFixed(2);
        if(comtype=='1') $('supcomission').value = ((summa  - clientcomission) * percent / ( 100 - percent )).toFixed(2);
        else $('supcomission').value = ((summa  - clientcomission) * percent / 100).toFixed(2);
      }
      function togglesubcomsection(sValue){
        if ($('supcomsection')){
          if(supclients.indexOf(parseInt(sValue))>-1) $('supcomsection').show();
          else $('supcomsection').hide();
        }
        if ($('midcomsection')){
          if(midclients.indexOf(parseInt(sValue))>-1) $('midcomsection').show();
          else $('midcomsection').hide();
        }
      }
      function getSubclientsList(sClId){
        if (${payrequest.paytype>3&&payrequest.paytype!=10&&payrequest.paytype!=11?1:0}) return false;
        togglesubcomsection(0);
        <g:remoteFunction controller='payment' action='subclientslist' params="'client_id='+sClId" update="subclientslist"/>
      }
      function processpayrepaymentResponse(e){
        var sErrorMsg = '';
        ['repayment_summa'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Сумма к списанию"])}</li>'; $('repayment_summa').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorrepaymentlist").innerHTML=sErrorMsg;
          $("errorrepaymentlist").up('div').show();
        } else
          location.reload(true)
      }
      function processpayagentpaymentResponse(e){
        var sErrorMsg = '';
        ['agentpayment_summa'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Сумма к выплате"])}</li>'; $('agentpayment_summa').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("erroragentpaymentlist").innerHTML=sErrorMsg;
          $("erroragentpaymentlist").up('div').show();
        } else
          location.reload(true)
      }
      function processRelatedResponse(e){
        if (e.responseJSON.related_id!=0){
          location.assign('${createLink(controller:'payment',action:'clientpayment')}'+'/'+e.responseJSON.related_id);
        }
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
          case 0: getRepaymentRequests();break;
          case 1: getAgentpaymentRequests();break;
        }
      }
      function getRepaymentRequests(){
        if(${payrequest.paytype==2?1:0}) $('repayments_submit_button').click();
      }
      function getAgentpaymentRequests(){
        if(${payrequest.paytype==1?1:0}) $('agentpayments_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
        jQuery("#sfacturadate").mask("99.99.9999",{placeholder:" "});
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
    </style>
  </head>
  <body onload="init()">
    <h3 class="fleft">Платеж №${payrequest.id} от ${String.format('%td.%<tm.%<tY',payrequest.paydate)}<g:if test="${payrequest.deal_id>0}">&nbsp;/&nbsp;Сделка № ${payrequest.deal_id}</g:if></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку клиентских платежей</a>
    <div class="clear"></div>
    <g:form name="newpayrequestForm" url="${[action:'updatepayrequest',id:payrequest.id]}" method="post" enctype="multipart/form-data" target="upload_target">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="paytype" disabled>Тип платежа:</label>
      <input type="text" id="paytype" disabled name="paytype" value="${payrequest.is_clientcommission?'возврат комиссии':payrequest.is_midcommission?'возврат посреднику':payrequest.paytype==1?'исходящий':payrequest.paytype==2?'входящий':payrequest.paytype==3?'внутренний':payrequest.paytype==4?'списание':payrequest.paytype==8?'откуп':payrequest.paytype==7?'абон. плата':payrequest.paytype==9?'комиссия':payrequest.paytype==10?'связанный входящий':payrequest.paytype==11?'внешний':'пополнение'}"/>
      <br/><label for="modstatus" disabled>Статус:</label>
      <g:select name="modstatus" value="${payrequest.modstatus}" from="['новый','в работе','выполнен','подтвержден']" keys="0123" disabled="true" />
      <label for="instatus" disabled>Статус получения:</label>
      <g:select name="instatus" value="${payrequest?.instatus?:0}" from="['отклонен', 'новый', 'в полете', 'получен', 'подтвержден']" keys="${-1..3}" disabled="true" />
      <label for="intcompany" disabled>Компания холд.</label>
      <input type="text" id="intcompany" disabled name="intcompany" value="${intcompany}"/>
      <label for="extcompany" disabled>Внешняя компания</label>
      <input type="text" id="extcompany" disabled name="extcompany" value="${extcompany}"/>
      <label for="initiator" disabled>Инициатор</label>
      <input type="text" id="initiator" disabled name="initiator" value="${initiator}"/>
      <label for="clientadmin" disabled>Модификатор</label>
      <input type="text" id="clientadmin" disabled name="clientadmin" value="${clientadmin}"/>
      <hr class="admin" />

      <label for="summa" disabled>Сумма:</label>
      <input type="text" id="summa" name="summa" disabled value="${number(value:payrequest.summa)}"/>
      <label class="auto" for="is_bankmoney">
        <input type="checkbox" id="is_bankmoney" name="is_bankmoney" value="1" <g:if test="${payrequest.is_bankmoney}">checked</g:if> />
        СС банка
      </label><br/>
      <label for="client_id">Клиент:</label>
      <g:select name="client_id" value="${payrequest.client_id}" from="${clients}" optionKey="id" optionValue="name" noSelection="${['0':'не указан']}" disabled="${payrequest.deal_id>0||payrequest.clientcommission>0}" onchange="getSubclientsList(this.value)"/>
    <g:if test="${payrequest.paytype<4||payrequest.paytype in [10,11]}">
      <label for="subclient_id">Подклиент:</label>
      <span id="subclientslist"><g:select name="subclient_id" value="${payrequest.subclient_id}" from="${subclients}" optionKey="id" optionValue="name" noSelection="${['0':'нет']}" onchange="togglesubcomsection(this.value)"/></span>
    </g:if>
    <g:if test="${!payrequest.is_bankmoney}">
      <br/><label for="clientcommission" disabled>Комиссия<br/><small>по агентскому договору</small></label>
      <input type="text" id="clientcommission" name="clientcommission" disabled value="${number(value:payrequest.clientcommission)}"/>
      <label for="agentcommission" disabled>Агентские выплаты</label>
      <input type="text" id="agentcommission" name="agentcommission" disabled value="${number(value:payrequest.agentcommission)}"/>
    <g:if test="${iscansf}">
      <label for="sfactura">Номер с-фактуры</label>
      <input type="text" id="sfactura" name="sfactura" value="${payrequest.sfactura}"/>
      <label for="sfacturadate">Дата с-фактуры:</label>
      <g:datepicker class="normal nopad" name="sfacturadate" value="${payrequest.sfacturadate?String.format('%td.%<tm.%<tY',payrequest.sfacturadate):''}"/>
    </g:if>
    <g:if test="${!payrequest.is_clientcommission&&!payrequest.is_midcommission&&(payrequest.paytype<4||payrequest.paytype in [10,11])}">
      <label for="percenttype">Тип комиссии:</label>
      <g:select name="percenttype" value="${payrequest.percenttype}" from="['умножение','деление']" keys="01" onchange="computeComission()"/><br/>
      <label for="compercent">Процент комиссии:</label>
      <input type="text" id="compercent" name="compercent" value="${number(value:payrequest.compercent)}" onkeyup="computeComission()"/>
      <label for="comission" disabled>Комиссия:</label>
      <input type="text" id="comission" disabled value="${number(value:payrequest.comission)}"/>
      <span id="midcomsection" style="${!(payrequest.subclient_id in midclients)?'display:none':''}"><label for="midpercent">% посредника:</label>
      <input type="text" id="midpercent" name="midpercent" value="${number(value:payrequest.midpercent)}" onkeyup="computeMidComission()"/>
      <label for="midcomission" disabled>Посреднику:</label>
      <input type="text" id="midcomission" disabled value="${number(value:payrequest.midcomission)}"/></span>
      <span id="supcomsection" style="${!(payrequest.subclient_id in supclients)?'display:none':''}"><label for="supcompercent">Процент клиента:</label>
      <input type="text" id="supcompercent" name="supcompercent" value="${number(value:payrequest.supcompercent)}" onkeyup="computeSupComission()"/>
      <label for="supcomission" disabled>Комиссия клиента:</label>
      <input type="text" id="supcomission" disabled value="${number(value:payrequest.supcomission)}"/></span>
      <label for="saldo" disabled>Сальдо:</label>
      <input type="text" id="saldo" disabled value="${number(value:curclientsaldo+dinclientsaldo+payrequest.computeClientdelta())}"/>
      <label for="file">Новый скан:</label>
      <input type="file" id="file" name="file" style="width:256px"/>
    </g:if>
    </g:if>

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
      <g:if test="${payrequest.file_id}">
        <a class="button" href="${createLink(controller:'payment',action:'showscan',id:payrequest.file_id,params:[code:Tools.generateModeParam(payrequest.file_id)])}" target="_blank">Просмотреть скан-подтверждение &nbsp;<i class="icon-angle-right icon-large"></i></a>
      </g:if>
      <g:if test="${payrequest.paytype==5&&payrequest.modstatus==2&&iscanedit}">
        <g:remoteLink class="button" url="${[controller:'payment',action:'closerefillpayment',id:payrequest.id]}" onSuccess="location.reload(true)">Закрыть платеж &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink>
      </g:if>
      <g:if test="${payrequest.paytype in [1,3,11]&&payrequest.related_id==0&&payrequest.client_id>0&&iscanedit}">
        <g:remoteLink class="button" url="${[controller:'payment',action:'createrelatedpayment',id:payrequest.id]}" onSuccess="processRelatedResponse(e)">Создать связанный &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink>
      </g:if>
      <g:if test="${payrequest.related_id}">
        <a class="button" href="${createLink(controller:'payment',action:'clientpayment',id:payrequest.related_id)}" target="_blank">Связанный платеж &nbsp;<i class="icon-angle-right icon-large"></i></a>
      </g:if>
      <g:if test="${payrequest.instatus==2&&payrequest.paytype in [2,11]}">
        <g:remoteLink class="button" url="${[controller:'payment',action:'cancelreceiveprincome',id:payrequest.id]}" onSuccess="location.reload(true)">Отменить получение &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink>
      </g:if>
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${!payrequest.confirmstatus&&iscanedit&&payrequest.paytype in [1,2,3,5,10,11]}">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm()"/>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      </g:if>
      </div>
    </g:form>
  <g:if test="${payrequest.instatus==1&&payrequest.paytype in [2,11]}">
    <div class="clear"></div>
    <hr class="admin" />
    <g:formRemote style="margin-top:20px" name="inconfirmForm" url="${[controller:'payment',action:'receiveprincome',id:payrequest.id]}" onSuccess="location.reload(true)">
      <label for="indate">Дата получения:</label>
      <g:datepicker class="normal nopad" name="indate" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
      <div class="fright" id="btns">
        <input type="submit" class="button" value="Подтвердить получение"/>
      </div>
    </g:formRemote>
    <hr class="admin" />
  </g:if>
    <div class="clear"></div>
  <g:if test="${payrequest.paytype<3&&!payrequest.confirmstatus&&!payrequest.is_bankmoney}">
    <div class="tabs">
      <ul class="nav">
        <li style="${payrequest.paytype==1?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(0)">Запросы на списание</a></li>
        <li style="${payrequest.paytype==2?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(1)">Запросы на агентские выплаты</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="repaymentsForm" url="[action:'repayments',id:payrequest.id]" update="details">
      <input type="submit" class="button" id="repayments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="agentpaymentsForm" url="[action:'agentpayments',id:payrequest.id]" update="details">
      <input type="submit" class="button" id="agentpayments_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <iframe id="upload_target" name="upload_target" style="display:none"></iframe>
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>