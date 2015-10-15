<html>
  <head>
    <title>Prisma: Платежи - <g:if test="${dccomission}">Комиссия №${dccomission.id}</g:if><g:else>Новая комиссия</g:else></title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['summa'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['paydate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата операции"])}</li>'; $('paydate').up('span').addClassName('k-error-colored'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма комиссии"])}</li>'; $('summa').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${dccomission?1:0}){
          location.reload(true);
        } else if(e.responseJSON.dccomission){
          location.assign('${createLink(controller:controllerName,action:'dccomission')}'+'/'+e.responseJSON.dccomission);
        } else
          returnToList();
      }
      function init(){
        jQuery("#paydate").mask("99.99.9999",{placeholder:" "});
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px}
      select[id*="platperiod"] { width: 125px; }
    </style>
  </head>
  <body onload="init()">
    <h3 class="fleft"><g:if test="${dccomission}">Комиссия №${dccomission.id}</g:if><g:else>Новая комиссия</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку комиссий</a>
    <div class="clear"></div>
    <g:formRemote name="tpaymentDetailForm" url="${[action:'updatedccomission',id:dccomission?.id?:0]}" method="post" onSuccess="processResponse(e)">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="summa">Сумма комиссии:</label>
      <input type="text" id="summa" name="summa" value="${number(value:dccomission?.summa)}" ${!iscanedit?'disabled':''} /><br/>
      <label for="paydate">Дата операции:</label>
      <g:datepicker style="margin-right:108px" name="paydate" value="${String.format('%td.%<tm.%<tY',dccomission?.paydate?:new Date())}"/>
      <label for="platperiod_month">Период:</label>
      <g:datePicker name="platperiod" precision="month" value="${dccomission?.platperiod?Date.parse('MM.yyyy', dccomission.platperiod):new Date()}" relativeYears="[114-new Date().getYear()..0]"/>

      <label for="comment">Комментарий к операции:</label>
      <g:textArea name="comment" value="${dccomission?.comment}" />

      <div class="clear"></div>
      <div class="fright" id="btns" style="padding-top:15px">
        <input type="submit" value="Сохранить"/>
      </div>
    </g:formRemote>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
