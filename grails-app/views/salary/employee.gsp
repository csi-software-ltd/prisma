<html>
  <head>
    <title>Prisma приложение: Зарплата - ${pers_user.shortname}</title>
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
      function viewCell(iNum){
        var tabs = jQuery('.nav').find('li');
        for(var i=0; i<tabs.length; i++){
          if(i==iNum)
            tabs[i].addClassName('selected');
          else
            tabs[i].removeClassName('selected');
        }
        switch(iNum){
          case 0: getPsalary();break;
        }
      }
      function getPsalary(){
        if(${pers_user?1:0}) $('psalarylist_submit_button').click();
      }
      function processpsalaryResponse(e){
        var sErrorMsg = '';
        ['pdate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата назначения"])}</li>'; ('pdate').up('span').addClassName('k-error-colored'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorpsalarylist").innerHTML=sErrorMsg;
          $("errorpsalarylist").up('div').show();
        } else
          jQuery('#psalaryEditForm').slideUp(300, function() { getPsalary(); });
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px}      
      input.mini{width:60px!important}
    </style>
  </head>
  <body onload="viewCell(0)">
    <h3 class="fleft">Зарплата - ${pers_user.shortname}</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку сотрудников</a>
    <div class="clear"></div>

    <label for="pers_user_id" disabled>Id:</label>
    <input type="text" id="pers_user_id" disabled value="${pers_user.id}" />
    <label for="inputdate" disabled>Дата заведения:</label>
    <input type="text" id="inputdate" disabled value="${String.format('%td.%<tm.%<tY %<tT',pers_user.inputdate)}" />

    <hr class="admin">

    <label for="fullname">Полное имя:</label>
    <input type="text" class="fullline" id="fullname" name="fullname" value="${pers_user.fullname}"/>
    <label for="shortname">Фамилия:</label>
    <input type="text" id="shortname" name="shortname" value="${pers_user.shortname}"/>      
    <label for="birthdate">Дата рождения:</label>
    <g:datepicker class="normal nopad" style="margin-right:110px" name="birthdate" value="${pers_user.birthdate?String.format('%td.%<tm.%<tY',pers_user.birthdate):''}"/>           
    <label for="snils" disabled>Учетный номер:</label>
    <input type="text" id="snils" name="snils" value="${pers_user.snils}" disabled/>
    <label for="passport">Паспорт:</label>
    <input type="text" id="passport" name="passport" value="${pers_user.passport}"/>
    <label for="passdate">Дата выдачи:</label>
    <g:datepicker class="normal nopad" style="margin-right:110px" name="passdate" value="${pers_user.passdate}"/>
    <label for="kodpodr">Код подр-ния:</label>
    <input type="text" id="kodpodr" name="kodpodr" value="${pers_user.kodpodr}"/>
    <label for="passorg">Организация выд.:</label>
    <input type="text" class="fullline" id="passorg" name="passorg" value="${pers_user.passorg}"/>
    <label for="inn">ИНН:</label>
    <input type="text" id="inn" name="inn" value="${pers_user.inn}"/>
    <label for="snilsdpf">СНИЛС:</label>
    <input type="text" id="snilsdpf" name="snilsdpf" value="${pers_user.snilsdpf}"/>
    <br/><label for="birthcity">Место рождения:</label>
    <input type="text" class="fullline" id="birthcity" name="birthcity" value="${pers_user.birthcity}"/>
    <label for="propiska">Прописка:</label>
    <input type="text" class="fullline" id="propiska" name="propiska" value="${pers_user.propiska}"/>
    <label for="citizen">Гражданство:</label>
    <input type="text" class="fullline" id="citizen" name="citizen" value="${pers_user.citizen}"/>
    <label for="education">Образование:</label>
    <input type="text" class="fullline" id="education" name="education" value="${pers_user.education}"/>

    <hr class="admin">

    <div class="clear"></div>
    <div class="tabs">
      <ul class="nav">
        <li><a href="javascript:void(0)" onclick="viewCell(0)">История зарплат</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="psalaryForm" url="[action:'psalarylist',id:pers_user.id]" update="[success:'details']">
      <input type="submit" class="button" id="psalarylist_submit_button" value="Показать" style="display:none" />        
    </g:formRemote>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:'salary',action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>