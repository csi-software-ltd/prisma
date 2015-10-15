<html>
  <head>
    <title>Prisma: Платежи - Платеж №${payrequest.id}</title>
    <meta name="layout" content="main" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function submitForm(){
        $('submit_button').click();
      }
      function computeComission(){
        var summa = parseFloat(parseFloat($('summa').value.replace(",",".").replace("\u00A0","")).toFixed(2));
        var percent = parseFloat(parseFloat($('payoffperc').value.replace(",",".").replace("\u00A0","")).toFixed(2));
        $('payoffsumma').value = ( summa * percent / 100 ).toFixed(2);
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
    </style>
  </head>
  <body>
    <h3 class="fleft">Платеж №${payrequest.id} от ${String.format('%td.%<tm.%<tY',payrequest.paydate)}<g:if test="${payrequest.deal_id>0}">&nbsp;/&nbsp;Сделка № ${payrequest.deal_id}</g:if></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку платежей отдела Т</a>
    <div class="clear"></div>
    <g:form name="newpayrequestForm" url="${[action:'updatetpayrequest',id:payrequest.id]}" method="post" enctype="multipart/form-data" target="upload_target">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="paytype" disabled>Тип платежа:</label>
      <input type="text" id="paytype" disabled name="paytype" value="${payrequest.paytype==1?'исходящий':payrequest.paytype==2?'входящий':payrequest.paytype==8?'откуп':payrequest.paytype==7?'абон. плата':payrequest.paytype==9?'комиссия':'пополнение'}"/>
      <label for="modstatus" disabled>Статус:</label>
      <g:select name="modstatus" value="${payrequest.modstatus}" from="['новый','в работе','выполнен','подтвержден']" keys="0123" disabled="true" />
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
      <label for="client_id">Клиент:</label>
      <g:select name="client_id" value="${payrequest.client_id}" from="${Client.findAllByIs_t(1)}" optionKey="id" optionValue="name" noSelection="${['0':'не указан']}" disabled="${payrequest.deal_id>0||payrequest.clientcommission>0}"/>
    <g:if test="${payrequest.paytype==8}">
      <label for="payoffperc">Процент комиссии</label>
      <input type="text" id="payoffperc" name="payoffperc" value="${number(value:payrequest.payoffperc)}" onkeyup="computeComission()"/>
      <label for="payoffsumma" disabled>Комиссия на откуп</label>
      <input type="text" id="payoffsumma" disabled value="${number(value:payrequest.payoffsumma)}"/>
    </g:if>
    <g:if test="${payrequest.client_id}">
      <label for="saldo" disabled>Сальдо:</label>
      <input type="text" id="saldo" disabled value="${number(value:curclientsaldo+dinclientsaldo+payrequest.computeClientdelta())}"/>
    </g:if>
      <label for="file">Новый скан:</label>
      <input type="file" id="file" name="file" style="width:256px"/>

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
      <g:if test="${payrequest.file_id}">
        <a class="button" href="${createLink(controller:'payment',action:'showscan',id:payrequest.file_id,params:[code:Tools.generateModeParam(payrequest.file_id)])}" target="_blank">Просмотреть скан-подтверждение</a>
      </g:if>
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${!payrequest.confirmstatus&&iscanedit&&payrequest.paytype in [1,2,8]}">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm()"/>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      </g:if>
      </div>
    </g:form>
    <iframe id="upload_target" name="upload_target" style="display:none"></iframe>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}"></g:form>
  </body>
</html>