<html>
  <head>
    <title>Prisma: Задания - Новая заявка на справку</title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function toggleValuta(sType){
        if(sType=='2') $('valutadetail').show()
        else $('valutadetail').hide()
      }
      function getCurBankList(sBank){
        var company = $('company').value
        <g:remoteFunction controller='task' action='enquirybanklist' params="'company='+company+'&bank='+sBank" update="bankdetail"/>
      }
      function getBankList(){
        var sType = $('enqtype_id').options[$('enqtype_id').selectedIndex].className.split('_')[1];
        setTimeout(function() {
          if(sType=='2'){
            $('inspection').hide();
            var company = $('company').value;
            <g:remoteFunction controller='task' action='enquirybanklist' params="'company='+company" update="bankdetail" onComplete="\$('bankdetail').show()"/>
          } else {
            if($('company').value||sType=='0') $('inspection').hide();
            else if(sType!='0') $('inspection').show();
            $('bankdetail').hide();
          }
        }, 200)
      }
      function init(){
        new Autocomplete('company', {
          serviceUrl:'${resource(dir:"autocomplete",file:"space_arendator_autocomplete")}',
          onSelect: function(value, data){
            $('company').focus();
          }
        });
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['company','enqtype_id','bank_id','valuta_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['ondate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Компания"])}</li>'; $('company').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Компания"])}</li>'; $('company').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Компанию","Компания"])}</li>'; $('company').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип запроса"])}</li>'; $('enqtype_id').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Тип запроса"])}</li>'; $('enqtype_id').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк"])}</li>'; $('bank_id').addClassName('red'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Банк"])}</li>'; $('bank_id').addClassName('red'); break;
              case 8: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Валюта"])}</li>'; $('valuta_id').addClassName('red'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.blank.message",args:["На дату"])}</li>'; $('ondate').up('span').addClassName('k-error-colored'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(e.responseJSON.enquiry){
          location.assign('${createLink(controller:controllerName,action:'enquiry')}'+'/'+e.responseJSON.enquiry);
        } else
          returnToList();
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
    </style>
  </head>
  <body onload="init()">
    <h3 class="fleft">Новая заявка на справку</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку справок</a>
    <div class="clear"></div>
    <g:formRemote name="requestDetailForm" url="${[action:'incertenquiry']}" method="post" onSuccess="processResponse(e)">
      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="company">Компания:</label>
      <input type="text" class="fullline" id="company" name="company" onblur="getBankList()"/>
      <div id="space_arendator_autocomplete" class="autocomplete" style="display:none"></div>
      <label for="enqtype_id">Тип справки:</label>
      <select id="enqtype_id" name="enqtype_id" onchange="getBankList()">
        <option class="type_0" value="0">не выбран</option>
      <g:each in="${Enqtype.list()}">
        <option class="type_${it.type}" value="${it.id}">${it.name}</option>
      </g:each>
      </select>
      <label for="ondate">На дату:</label>
      <g:datepicker class="normal nopad" style="margin-right:108px" name="ondate" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
      <span id="inspection" style="display:none">
        <label for="taxinspection_id">Налоговая:</label>
        <g:select class="fullline" name="taxinspection_id" from="${taxinspections}" optionKey="id" noSelection="${['':'не выбрана']}"/>
      </span>
      <span id="bankdetail" style="display:none"></span>
      <br/><label for="endetails">Описание запроса:</label>
      <g:textArea name="endetails" id="endetails" />

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
        <input type="submit" class="spacing" value="Сохранить"/>
      </div>
    </g:formRemote>
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>