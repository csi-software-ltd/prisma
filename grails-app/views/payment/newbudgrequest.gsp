<html>
  <head>
    <title>Prisma: Платежи - Новый бюджетный платеж</title>
    <meta name="layout" content="main" /> 
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function newCompany(){
        window.open('${createLink(controller:'company',action:'detail')}');
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['summa','totax_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['paydate','fromcompany'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата платежа"])}</li>'; $("paydate").up('span').addClassName('k-error-colored'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Плат. компания"])}</li>'; $("fromcompany").up('span').addClassName('k-error-colored'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $("summa").addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип налога"])}</li>'; $("totax_id").addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else { returnToList(); }
      }
      function init(){
        jQuery("#paydate").mask("99.99.9999",{placeholder:" "});
        new Autocomplete('fromcompany', {
          serviceUrl:'${resource(dir:"autocomplete",file:"companyname_int_autocomplete")}',
          onSelect: function(value, data){
            var lsData = data.split(';');
            $('fromcompany_id').value = lsData[0];
          }
        });
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
    </style>
  </head>
  <body onload="init();">
    <h3 class="fleft">Новый бюджетный платеж</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку бюджетных платежей</a>
    <div class="clear"></div>

    <div class="error-box" style="display:none">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>

    <g:formRemote name="paymentDetailForm" url="${[action:'incertbudgrequest']}" method="post" onSuccess="processResponse(e)">
      <label for="summa">Сумма:</label>
      <input type="text" id="summa" name="summa" value=""/>
      <label for="paydate">Дата платежа:</label>
      <g:datepicker class="normal nopad" name="paydate" value="${new Date()}"/><br/>
      <label for="fromcompany">Плат. компания:</label>
      <span class="input-append">
        <input type="text" id="fromcompany" name="fromcompany" value="" style="width:200px"/>
        <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
      </span>
      <div id="fromcompany_autocomplete" class="autocomplete" style="display:none;"></div>
      <input type="hidden" id="fromcompany_id" name="fromcompany_id" value=""/>
      <label for="totax_id">Тип налога:</label>
      <g:select id="totax_id" name="totax_id" from="${tax}" optionKey="id" optionValue="name" noSelection="${[0:'не выбран']}" />

      <hr class="admin" />

      <label for="destination">Назначение платежа:</label>
      <g:textArea name="destination" value="" />

      <hr class="admin" />

      <div class="clear"></div>
      <div class="fright" id="btns">
        <input type="submit" value="Создать"/>
      </div>
    </g:formRemote>
    <g:form id="returnToListForm" name="returnToListForm" url="${[action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
