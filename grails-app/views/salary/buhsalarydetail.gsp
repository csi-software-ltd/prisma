<html>
  <head>
    <title>Prisma: Зарплата - Бухгалтерская ведомость. ${scomp.is_pers?scomp.fio:scomp.companyname}. ${String.format('%tB %<tY',new Date(scomp.year-1900,scomp.month-1,1))}</title>
    <meta name="layout" content="main" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['fullsalary','debtsalary','ndfl','debtndfl','fss_tempinvalid','debtfss_tempinvalid','fss_accident','debtfss_accident','ffoms','debtffoms','pf','debtpf'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:[scomp.is_pers?"Начислено":"ФОТ"])}</li>'; $('fullsalary').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Долг"])}</li>'; $('debtsalary').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["НДФЛ"])}</li>'; $('ndfl').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Долг НДФЛ"])}</li>'; $('debtndfl').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["ВНиМ"])}</li>'; $('fss_tempinvalid').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Долг ВНиМ"])}</li>'; $('debtfss_tempinvalid').addClassName('red'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["НС и ПЗ"])}</li>'; $('fss_accident').addClassName('red'); break;
              case 8: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Долг НС и ПЗ"])}</li>'; $('debtfss_accident').addClassName('red'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["ФФОМС"])}</li>'; $('ffoms').addClassName('red'); break;
              case 10: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Долг ФФОМС"])}</li>'; $('debtffoms').addClassName('red'); break;
              case 11: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["ПФ"])}</li>'; $('pf').addClassName('red'); break;
              case 12: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Долг ПФ"])}</li>'; $('debtpf').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else {
          returnToList();
        }
      }
      function submitForm(iStatus){
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
    <h3 class="fleft">Бухгалтерская ведомость. ${scomp.is_pers?scomp.fio:scomp.companyname}. ${String.format('%tB %<tY',new Date(scomp.year-1900,scomp.month-1,1))}</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку авансовых ведомостей</a>
    <div class="clear"></div>
    <g:formRemote name="salarycompDetailForm" url="${[action:'updatebuhsalarycomp',id:scomp.id]}" method="post" onSuccess="processResponse(e)">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <g:if test="${scomp.is_pers}">
        <label for="fullsalary">Начислено:</label>
        <input type="text" id="fullsalary" name="fullsalary" value="${number(value:scomp.fullsalary)}"/>
        <label for="debtsalary">Долг:</label>
        <input type="text" id="debtsalary" name="debtsalary" value="${number(value:scomp.debtsalary)}"/>
      </g:if><g:else>
        <label for="fullsalary">ФОТ:</label>
        <input type="text" id="fullsalary" name="fullsalary" value="${number(value:scomp.fullsalary)}"/><br/>
        <label for="ndfl">НДФЛ:</label>
        <input type="text" id="ndfl" name="ndfl" value="${number(value:scomp.ndfl)}"/>
        <label for="debtndfl">Долг НДФЛ:</label>
        <input type="text" id="debtndfl" name="debtndfl" value="${number(value:scomp.debtndfl)}"/>
        <label for="fss_tempinvalid">ВНиМ:</label>
        <input type="text" id="fss_tempinvalid" name="fss_tempinvalid" value="${number(value:scomp.fss_tempinvalid)}"/>
        <label for="debtfss_tempinvalid">Долг ВНиМ:</label>
        <input type="text" id="debtfss_tempinvalid" name="debtfss_tempinvalid" value="${number(value:scomp.debtfss_tempinvalid)}"/>
        <label for="fss_accident">НС и ПЗ:</label>
        <input type="text" id="fss_accident" name="fss_accident" value="${number(value:scomp.fss_accident)}"/>
        <label for="debtfss_accident">Долг НС и ПЗ:</label>
        <input type="text" id="debtfss_accident" name="debtfss_accident" value="${number(value:scomp.debtfss_accident)}"/>
        <label for="ffoms">ФФОМС:</label>
        <input type="text" id="ffoms" name="ffoms" value="${number(value:scomp.ffoms)}"/>
        <label for="debtffoms">Долг ФФОМС:</label>
        <input type="text" id="debtffoms" name="debtffoms" value="${number(value:scomp.debtffoms)}"/>
        <label for="pf">ПФ:</label>
        <input type="text" id="pf" name="pf" value="${number(value:scomp.pf)}"/>
        <label for="debtpf">Долг ПФ:</label>
        <input type="text" id="debtpf" name="debtpf" value="${number(value:scomp.debtpf)}"/>
      </g:else>

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(0)"/>
      </g:if>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      </div>
    </g:formRemote>
    <div class="clear"></div>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>