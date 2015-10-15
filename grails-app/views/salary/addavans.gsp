<html>
  <head>
    <title>Prisma: Зарплата - Новая авансовая ведомость</title>
    <meta name="layout" content="main" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['repdate_month','repdate_year'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["На дату"])}</li>'; $('repdate_month').addClassName('red'); $('repdate_year').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.not.unique.message",args:["Авансовая ведомость","датой"])}</li>'; $('repdate_month').addClassName('red'); $('repdate_year').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(e.responseJSON.avans){
          location.assign('${createLink(controller:controllerName,action:'avans')}'+'/'+e.responseJSON.avans);
        } else
          location.assign('${createLink(controller:controllerName,action:'index')}');
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
    <h3 class="fleft">Новая авансовая ведомость</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку авансовых ведомостей</a>
    <div class="clear"></div>
    <g:formRemote name="newcashrequestForm" url="${[action:'incertavans']}" method="post" onSuccess="processResponse(e)">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <hr class="admin" />

      <label for="repdate">Месяц:</label>
      <g:datePicker name="repdate" value="${new Date()}" precision="month" years="${2014..new Date().getYear()+1900}"/><br/>

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
        <input type="submit" class="spacing" value="Сформировать"/>
      </div>
    </g:formRemote>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>