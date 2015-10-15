<html>
  <head>
    <title>Prisma: Платежи - Добавление платежа отдела Т</title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function init(){
        jQuery("#paydate").mask("99.99.9999",{placeholder:" "});
        new Autocomplete('tocompany', {
          serviceUrl:'${resource(dir:"autocomplete",file:"companyname_ext_autocomplete")}',
          onSelect: function(value, data){
            var lsData=data.split(';');
            $('tocompany_id').value=lsData[0];
            getBankByCompany();
          }
        });
      }
      function getBankByCompany(){
        var iCompany_id=$('tocompany_id').value;
        if(iCompany_id)
          <g:remoteFunction controller='payment' action='getbankbycompany' params="'company_id='+iCompany_id" update="tobank_span" onSuccess="\$('bank_div').show()"/>;
      }
      function newCompany(){
        window.open('${createLink(controller:'company',action:'detail')}');
      }
      function computeComission(){
        if(${cashrequest_id?0:1}) return false;
        var summa = parseFloat(parseFloat($('summa').value.replace(",",".").replace("\u00A0","")).toFixed(2));
        var percent = parseFloat(parseFloat($('payoffperc').value.replace(",",".").replace("\u00A0","")).toFixed(2));
        $('payoffsumma').value = ( summa * percent / 100 ).toFixed(2);
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['summa'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['paydate','tocompany','tobank'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата платежа"])}</li>'; $("paydate").up('span').addClassName('k-error-colored'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Получ. компания"])}</li>'; $("tocompany").up('span').addClassName('k-error-colored'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк получателя"])}</li>'; $("tobank").up('span').addClassName('k-error-colored'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $("summa").addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else {
          returnToList();
        }
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
    </style>
  </head>
  <body onload="init();">
    <h3 class="fleft">Новый платеж отдела Т</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку платежей</a>
    <div class="clear"></div>

    <div class="error-box" style="display:none">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>

    <g:formRemote name="tpaymentDetailForm" url="${[action:'incerttpayment']}" method="post" onSuccess="processResponse(e)">
      <label for="paytype" disabled>Тип:</label>
      <g:select name="paytype" from="['исходящий','откуп']" keys="18" value="${cashrequest_id?8:1}" disabled="true"/>
      <label for="paycat" disabled>Категория:</label>
      <g:select name="paycat" from="['прочий']" keys="4" disabled="true"/>
      <label for="fromcompany" disabled>Плат. компания:</label>
      <input type="text" id="fromcompany" name="fromcompany" value="${fromcompany.name}" disabled/>
      <label for="tocompany">Получ. компания:</label>
      <span class="input-append">
        <input type="text" class="nopad normal" id="tocompany" name="tocompany" value=""/>
        <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
      </span>
      <div id="tocompany_autocomplete" class="autocomplete" style="display:none"></div>
      <input type="hidden" id="tocompany_id" name="tocompany_id" value=""/>

      <div id="bank_div" style="display:none">
        <label for="tobank" id="tobank_label">Банк получателя:</label>
        <span id="tobank_span" class="input-append">
          <input type="text" id="tobank" name="tobank" value="" disabled class="fullline"/>
        </span>
      </div>

      <hr class="admin" />

      <label for="paydate">Дата платежа:</label>
      <g:datepicker class="normal nopad" name="paydate" value="${new Date()}"/><br/>
      <label for="summa">Сумма:</label>
      <input type="text" id="summa" name="summa" value="${number(value:payoffsumma)}" onkeyup="computeComission()"/>
      <label class="auto" for="is_nds">
        <input type="checkbox" id="is_nds" name="is_nds" value="1" checked />
        Наличие НДС
      </label><br/>
    <g:if test="${cashrequest_id}">
      <label for="payoffperc">Процент комиссии</label>
      <input type="text" id="payoffperc" name="payoffperc" value="${number(value:payoffperc)}" onkeyup="computeComission()"/>
      <label for="payoffsumma" disabled>Комиссия на откуп</label>
      <input type="text" id="payoffsumma" disabled value="${number(value:payoffsumma*payoffperc/100)}"/>
      <input type="hidden" name="cashrequest_id" value="${cashrequest_id}"/>
    </g:if>

      <label for="destination">Назначение платежа:</label>
      <g:textArea name="destination" value="" />
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" value="${cashrequest_id?'Запрос на откуп':''}" />

      <div class="clear"></div>
      <div class="fright" id="btns" style="padding-top:15px">
        <input type="submit" value="Создать"/>
      </div>
    </g:formRemote>
    <g:form id="returnToListForm" name="returnToListForm" url="${[action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
