<html>
  <head>
    <title>Prisma: Задания - Заявка на справку №${enquiry.id}</title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function toggleValuta(sType){
        if(sType=='2') $('valutadetail').show()
        else $('valutadetail').hide()
      }
      function deleteenquiry(){
        if(confirm('Вы уверены, что хотите удалить заявку?')) { 
          <g:remoteFunction url="${[controller:controllerName,action:'deleteenquiry',id:enquiry.id]}" onSuccess="returnToList()"/>
        }
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['valuta_id','comment'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['termdate','ondate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Валюта"])}</li>'; $('valuta_id').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок ответа"])}</li>'; $('termdate').up('span').addClassName('k-error-colored'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Комментарий"])}</li>'; $('comment').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["На дату"])}</li>'; $('ondate').up('span').addClassName('k-error-colored'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else {
          location.reload(true);
        }
      }
      function submitForm(iStatus){
        $('modstatus').value = iStatus;
        $('submit_button').click();
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
      .tabs a:hover,.tabs a.active,.tabs a.active i{color:#000}
    </style>
  </head>
  <body>
    <h3 class="fleft">Заявка на справку №${enquiry.id}</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку справок</a>
    <div class="clear"></div>
    <g:formRemote name="enquiryDetailForm" url="${[action:'updateenquiry',id:enquiry.id]}" method="post" onSuccess="processResponse(e)">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="enquiry_id" disabled>Код заявки:</label>
      <input type="text" id="enquiry_id" disabled value="${enquiry.id}" />
      <label for="inputdate" disabled>Дата создания:</label>
      <input type="text" name="inputdate" disabled value="${String.format('%td.%<tm.%<tY %<tT',enquiry.inputdate)}" />
      <label for="company" disabled>Компания заявки:</label>
      <input type="text" id="company" disabled value="${company.name}" />
      <label for="initiator" disabled>Автор изменений:</label>
      <input type="text" id="initiator" disabled value="${initiator}" />
      <label for="enquiry" disabled>Статус заявки:</label>
      <input type="text" id="enquiry" disabled value="${!enquiry.modstatus?'Новая':enquiry.modstatus==1?'Заявка принята':enquiry.modstatus==2?'Справка выдана':enquiry.modstatus==3?'Требуется перезапрос':'Отказ'}" />
      <label for="whereto" disabled>Куда:</label>
      <input type="text" id="whereto" disabled value="${enquiry.whereto==1?'В налоговую':'В банк'}" />
      <label for="contragent" disabled>${enquiry.whereto==1?'ИФНС:':'БАНК:'}</label>
      <input type="text" class="fullline" id="contragent" disabled value="${bank?.name?:taxinspection?.name}" />

      <hr class="admin" />

      <label for="enqtype_id" disabled>Тип справки:</label>
      <g:select name="enqtype_id" value="${enquiry.enqtype_id}" from="${Enqtype.list()}" optionKey="id" optionValue="name" disabled="true"/>
      <label for="ondate">На дату:</label>
      <g:datepicker class="normal nopad" style="margin-right:108px" name="ondate" value="${String.format('%td.%<tm.%<tY',enquiry.ondate)}"/>
    <g:if test="${valutas.find{it!=857}}">
      <br/><label for="accounttype">Тип запроса:</label>
      <g:select name="accounttype" value="${enquiry.accounttype}" from="['по расчетному счету','по всем счетам','по валютным счетам']" keys="012" onchange="toggleValuta(this.value)"/>
      <span id="valutadetail" style="${enquiry.accounttype!=2?'display:none':''}"><label for="valuta_id">Валюта:</label>
      <g:select name="valuta_id" value="${enquiry.valuta_id}" from="${valutas}" optionValue="${{Valuta.get(it).name}}" noSelection="${['0':'не выбрана']}"/></span>
    </g:if>

      <hr class="admin" />

      <label for="termdate">Срок ответа:</label>
      <g:datepicker class="normal nopad" style="margin-right:108px" name="termdate" value="${enquiry?.termdate?String.format('%td.%<tm.%<tY',enquiry.termdate):''}"/>
      <label for="enddate">Дата ответа:</label>
      <g:datepicker class="normal nopad" name="enddate" value="${enquiry?.enddate?String.format('%td.%<tm.%<tY',enquiry.enddate):''}"/>

      <hr class="admin" />

      <label for="endetails">Описание запроса:</label>
      <g:textArea name="endetails" id="endetails" value="${enquiry.endetails}" />
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" id="comment" value="${enquiry.comment}" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(${enquiry.modstatus})"/>
      <g:if test="${enquiry.modstatus==0}">
        <input type="button" class="spacing" value="Принять заявку" onclick="submitForm(1)"/>
        <input type="button" class="spacing" value="Удалить" onclick="deleteenquiry()"/>
      </g:if>
      <g:if test="${enquiry.modstatus==1}">
        <input type="button" class="spacing" value="Справка получена" onclick="submitForm(2)"/>
        <input type="button" class="spacing" value="Требуется перезапрос" onclick="submitForm(3)"/>
        <input type="button" class="spacing" value="Отказ" onclick="submitForm(-1)"/>
      </g:if>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      </g:if>
      </div>
      <input type="hidden" id="modstatus" name="modstatus" value="0"/>
    </g:formRemote>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>