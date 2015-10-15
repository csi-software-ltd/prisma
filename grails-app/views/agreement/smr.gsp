<html>
  <head>
    <title>Prisma: <g:if test="${smr}">Договор СМР №${smr.anumber}</g:if><g:else>Новый договор СМР</g:else></title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function computeAvans(iType){
        var summa = 0;
        if ($('summa').value) summa = parseInt($('summa').value.replace("\u00A0","")).toFixed(2);
        if (iType==0){
          var avanspercent = 0;
          if ($('avanspercent').value) avanspercent = parseFloat($('avanspercent').value.replace(",",".").replace("\u00A0","")).toFixed(2);
          if (summa&&avanspercent) $('avans').value = (summa * avanspercent / 100).toFixed(0);
          else $('avans').value = '';
        } else {
          var avans = 0;
          if ($('avans').value) avans = parseInt($('avans').value.replace("\u00A0","")).toFixed(2);
          if (summa&&avans) $('avanspercent').value = (avans / summa * 100).toFixed(2);
          else $('avanspercent').value = '';
        }
      }
      function toggleavans(iId){
        if(iId==2) $('avanssection').show();
        else $('avanssection').hide();
      }
      function deleteAgr(){
        if(confirm('Вы уверены, что хотите удалить договор СМР №${smr?.anumber}?')) { submitForm(-1) }
      }
      function newCompany(){
        window.open('${createLink(controller:'company',action:'detail')}');
      }
      function getclBankList(){
        var companyname = $('clientcompany').value
        <g:remoteFunction controller='agreement' action='smrbanklist' params="'companyname='+companyname" update="clbanklist" />
      }
      function getsupBankList(){
        var companyname = $('suppliercompany').value
        <g:remoteFunction controller='agreement' action='smrbanklist' params="'companyname='+companyname+'&type=1'" update="supbanklist" />
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['cbank_id','sbank_id','smrcat_id','anumber','summa','responsible','avans'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['adate','enddate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        ['clientcompany','suppliercompany'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Заказчик"])}</li>'; $('clientcompany').up('span').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Заказчик"])}</li>'; $('clientcompany').up('span').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Заказчика","Заказчик"])}</li>'; $('clientcompany').up('span').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Подрядчик"])}</li>'; $('suppliercompany').up('span').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Подрядчик"])}</li>'; $('suppliercompany').up('span').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Подрядчика","Подрядчик"])}</li>'; $('suppliercompany').up('span').addClassName('red'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк заказчика"])}</li>'; $('cbank_id').addClassName('red'); break;
              case 8: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк подрядчика"])}</li>'; $('sbank_id').addClassName('red'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип работ"])}</li>'; $('smrcat_id').addClassName('red'); break;
              case 10: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Ответственный"])}</li>'; $('responsible').addClassName('red'); break;
              case 11: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Номер договора"])}</li>'; $('anumber').addClassName('red'); break;
              case 12: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Стоимость"])}</li>'; $('summa').addClassName('red'); break;
              case 13: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Стоимость"])}</li>'; $('summa').addClassName('red'); break;
              case 14: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата договора"])}</li>'; $('adate').up('span').addClassName('k-error-colored'); break;
              case 15: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок выполнения"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 16: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Срок выполнения"])}</li>'; $('enddate').up('span').addClassName('k-error-colored'); break;
              case 17: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма аванса"])}</li>'; $('avans').addClassName('red'); break;
              case 18: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Сумма аванса"])}</li>'; $('avans').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${smr?1:0}){
          location.reload(true);
        } else if(e.responseJSON.smr){
          location.assign('${createLink(controller:controllerName,action:'smr')}'+'/'+e.responseJSON.smr);
        } else
          returnToList();
      }
      function processaddpayrequestResponse(e){
        var sErrorMsg = '';
        ['payrequest_summa','payrequest_summands'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['payrequest_paydate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('payrequest_summa').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Сумма"])}</li>'; $('payrequest_summa').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Сумма НДС"])}</li>'; $('payrequest_summands').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата платежа"])}</li>'; $('payrequest_paydate').up('span').addClassName('k-error-colored'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorpayrequestlist").innerHTML=sErrorMsg;
          $("errorpayrequestlist").up('div').show();
        } else
          jQuery('#payrequestAddForm').slideUp(300, function() { getPayrequests(); });
      }
      function submitForm(iStatus){
        $('modstatus').value = iStatus
        $('submit_button').click();
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
          case 0: getPayrequests();break;
          case 1: getHistory();break;
        }
      }
      function getPayrequests(){
        if(${smr?1:0}) $('payrequests_submit_button').click();
      }
      function getHistory(){
        if(${smr?1:0}) $('history_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
        new Autocomplete('clientcompany', {
          serviceUrl:'${resource(dir:"autocomplete",file:"space_arendodatel_autocomplete")}',
          onSelect: function(value, data){
            $('clientcompany').focus();
          }
        });
        new Autocomplete('suppliercompany', {
          serviceUrl:'${resource(dir:"autocomplete",file:"space_arendodatel_autocomplete")}',
          onSelect: function(value, data){
            $('suppliercompany').focus();
          }
        });
        jQuery("#adate").mask("99.99.9999",{placeholder:" "});
        jQuery("#enddate").mask("99.99.9999",{placeholder:" "});
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
      .tabs a:hover,.tabs a.active,.tabs a.active i{color:#000}
      .k-ff { overflow: inherit !important;}
    </style>
  </head>
  <body onload="init()">
    <h3 class="fleft"><g:if test="${smr}">Договор СМР №${smr.anumber}</g:if><g:else>Новый договор СМР</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку СМР</a>
    <div class="clear"></div>
    <g:formRemote name="requestDetailForm" url="${[action:'updatesmr',id:smr?.id?:0]}" method="post" onSuccess="processResponse(e)">

    <g:if test="${smr?.modstatus==0}">
      <div class="info-box" style="margin-top:0">
        <span class="icon icon-info-sign icon-3x"></span>
        <ul id="infolist">
          <li>Внимание! Срок действия договора истек.</li>
        </ul>
      </div>
    </g:if>

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

    <g:if test="${smr}">
      <label for="inputdate" disabled>Дата создания:</label>
      <input type="text" name="inputdate" disabled value="${String.format('%td.%<tm.%<tY %<tT',smr.inputdate)}" />
      <label for="status" disabled>Статус:</label>
      <g:select name="status" value="${smr.modstatus}" from="['Архив','Активный']" keys="[0,1]" disabled="true"/>
      <hr class="admin" />
    </g:if>

      <label for="clientcompany">Заказчик:</label>
      <span class="input-append">
        <input type="text" class="nopad normal" ${smr?'disabled':''} id="clientcompany" name="clientcompany" value="${clientcompany?.name}" onblur="getclBankList();"/>
        <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
      </span>
      <div id="clientcompanyname_autocomplete" class="autocomplete" style="display:none"></div>
      <label for="suppliercompany">Подрядчик:</label>
      <span class="input-append">
        <input type="text" class="nopad normal" ${smr?'disabled':''} id="suppliercompany" name="suppliercompany" value="${suppliercompany?.name}" onblur="getsupBankList();"/>
        <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
      </span>
      <div id="suppliercompanyname_autocomplete" class="autocomplete" style="display:none"></div>
      <label for="cbank_id">Банк заказчика:</label>
      <span id="clbanklist"><g:select class="fullline" name="cbank_id" value="${smr?.cbank_id}" from="${cbanks}" optionValue="name" optionKey="id" noSelection="${['':clientcompany?'не выбран':'заказчик не указан']}"/></span>
      <label for="sbank_id">Банк подрядчика:</label>
      <span id="supbanklist"><g:select class="fullline" name="sbank_id" value="${smr?.sbank_id}" from="${sbanks}" optionValue="name" optionKey="id" noSelection="${['':suppliercompany?'не выбран':'подрядчик не указан']}"/></span>
      <label for="smrcat_id">Тип работ:</label>
      <g:select name="smrcat_id" value="${smr?.smrcat_id}" from="${smrcats}" optionKey="id" optionValue="name" noSelection="${['0':'не указан']}"/>
    <g:if test="${smr}">
      <label for="smrsort" disabled>Признак договора:</label>
      <g:select name="smrsort" value="${smr?.smrsort}" from="['Внешний подряд','Внутренний подряд','Внешний заказчик']" keys="123" noSelection="${['0':'не указан']}" disabled="true"/>
    </g:if>
      <hr class="admin" />

      <label for="anumber">Номер договора:</label>
      <input type="text" id="anumber" name="anumber" value="${smr?.anumber}"/>
      <label for="summa">Стоимость:</label>
      <input type="text" id="summa" name="summa" value="${intnumber(value:smr?.summa)}" onkeyup="computeAvans(1)"/>
      <label for="adate">Дата договора:</label>
      <g:datepicker class="normal nopad" style="margin-right:108px" name="adate" value="${smr?.adate?String.format('%td.%<tm.%<tY',smr.adate):''}"/>
      <label for="enddate">Срок выполнения:</label>
      <g:datepicker class="normal nopad" name="enddate" value="${smr?.enddate?String.format('%td.%<tm.%<tY',smr.enddate):''}"/><br/>

      <hr class="admin" />

      <label for="paytype">Порядок оплаты</label>
      <g:select name="paytype" value="${smr?.paytype}" from="['единовременно','аванс']" keys="12" onchange="toggleavans(this.value)"/><br/>
      <div id="avanssection" style="${smr?.paytype!=2?'display:none':''}"><label for="avanspercent">Аванс в %:</label>
      <input type="text" id="avanspercent" name="avanspercent" value="${number(value:smr?.avanspercent)}" onkeyup="computeAvans(0)"/>
      <label for="avans">Сумма аванса:</label>
      <input type="text" id="avans" name="avans" value="${intnumber(value:smr?.avans)}" onkeyup="computeAvans(1)"/></div>
      <label for="project_id">Проект:</label>
      <g:select name="project_id" value="${smr?.project_id}" from="${Project.list()}" optionKey="id" optionValue="name" noSelection="${['0':'не указан']}"/>
      <label for="responsible">Ответственный:</label>
      <g:select name="responsible" value="${smr?.responsible?:session.user.id}" from="${users}" optionValue="name" optionKey="id" noSelection="${['0':'не выбрано']}"/>
      <label for="description">Описание:</label>
      <g:textArea name="description" id="description" value="${smr?.description}" />
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" id="comment" value="${smr?.comment}" />

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(1)"/>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      </g:if>
      <g:if test="${smr?.modstatus>-1&&isCanDelete}">
        <input type="button" class="spacing reset" value="К удалению" onclick="deleteAgr()" />
      </g:if>
      </div>
      <input type="hidden" id="modstatus" name="modstatus" value="1"/>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${smr}">
    <div class="tabs">
      <ul class="nav">
        <li><a href="javascript:void(0)" onclick="viewCell(0)">Платежи</a></li>
        <li><a href="javascript:void(0)" onclick="viewCell(1)">История</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="payrequestsForm" url="[action:'smrpayrequests',id:smr.id]" update="details">
      <input type="submit" class="button" id="payrequests_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="historyForm" url="[action:'smrhistory',id:smr.id]" update="details">
      <input type="submit" class="button" id="history_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>