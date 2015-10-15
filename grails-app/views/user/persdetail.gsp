<html>
  <head>
    <title>Prisma приложение: <g:if test="${pers_user}">Редактирование физ. лица № ${pers_user.id}</g:if><g:else>Добавление нового физ. лица</g:else></title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />    
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript library="prototype/autocomplete" />    
    <g:javascript>          
      function init(){
      <g:if test="${flash?.persedit_success}">
        $("infolist").up('div').show();
      </g:if>
      <g:if test="${flash?.show_psalary}">
        viewCell(1)
      </g:if><g:else>
        viewCell(0);
      </g:else>
        jQuery("#snilsdpf").mask("999-999-999 99");
        jQuery("#passport").mask("99 99 999999");
        jQuery("#inn").mask("999999999999");
        jQuery("#passdate").mask("99.99.9999",{placeholder:" "});
        jQuery("#birthdate").mask("99.99.9999",{placeholder:" "});
        jQuery("#kodpodr").mask("999-999");
      }
      function returnToList(){
        $("returnToListForm").submit();
      }      
      function processResponse(e){        
        var sErrorMsg = '';
        ['shortname','fullname','birthdate','inn','passport','passdate','actsalary','perstype','snilsdpf','kodpodr'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.error){
          if(e.responseJSON.errorcode.length){          
            e.responseJSON.errorcode.forEach(function(err){
              switch (err) {                                   
                case 1: sErrorMsg+='<li>Не заполнено обязательное поле "Полное имя"</li>'; $("fullname").addClassName('red'); break;
                case 2: sErrorMsg+='<li>Не заполнено обязательное поле "Фамилия"</li>'; $("shortname").addClassName('red'); break;                
                case 3: sErrorMsg+='<li>Некорректные данные в поле "Дата рождения"</li>'; $("birthdate").addClassName('red'); break;
                case 4: sErrorMsg+='<li>Некорректные данные в поле "ИНН"</li>'; $("inn").addClassName('red'); break;
                case 5: sErrorMsg+='<li>Некорректные данные в поле "СНИЛС"</li>'; $("snilsdpf").addClassName('red'); break;
                case 6: sErrorMsg+='<li>Некорректные данные в поле "Паспорт"</li>'; $("passport").addClassName('red'); break;
                /*
                  case 7: sErrorMsg+='<li>Некорректные данные в поле "Дата выдачи"</li>'; $("passdate").addClassName('red'); break;  
                  case 8: sErrorMsg+='<li>Некорректные данные в поле "Фактическая зарплата"</li>'; $("actsalary").addClassName('red'); break;
                */
                case 9: sErrorMsg+='<li>Такое значение поля "Фамилия" уже существует</li>'; $("shortname").addClassName('red'); break;
                case 10: sErrorMsg+='<li>Не выбранно значение обязательного поля "Тип"</li>'; $("perstype").addClassName('red'); break;                
                case 11: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Код подразделения"])}</li>'; $("kodpodr").addClassName('red'); break;                
              }
            });
          }
          $("infolist").up('div').hide();
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();          
        } else {   
          if(e.responseJSON.pers_id>0)
            location.assign('${createLink(controller:'user',action:'persdetail')}'+'/'+e.responseJSON.pers_id);
          else             
            location.reload(true);                    
        }        
      }

      function setShortname(sVal){
        if(sVal.length){
          sVal=sVal[0].toUpperCase() + sVal.substring(1);
          $("fullname").value=sVal;
          
          if(!$("shortname").value.length){
            var sName='';
            var aName=sVal.split(' ');
            if(aName.length>0){
              sName=aName[0];              
            }  
            for(var i=1;i<4;i++){
              if(aName[i]!=undefined && aName[i].length)
                sName+=' '+aName[i][0].toUpperCase()+'.'
            }
            $("shortname").value=sName;                 
          }            
        }  
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
          case 0: getCompers();break;
          case 1: getPsalary();break;
          case 2: getPersacount();break;
          case 3: getUser();break;
        }
      }
      function getCompers(){
        if(${pers_user?1:0}) $('compers_submit_button').click();
      }
      function getPsalary(){
        if(${pers_user?1:0}) $('psalary_submit_button').click();
      }
      function getPersacount(){
        if(${pers_user?1:0}) $('persaccount_submit_button').click();
      } 
      function getUser(){
        if(${pers_user?1:0}) $('puser_submit_button').click();
      }
      
      function editPersaccount(lId){
        $('persaccount_id').value=lId;
        $('persaccountdetail_submit_button').click();
      } 
      
      function editPsalary(lId){
        $('psalary_id').value=lId;
        $('psalarydetail_submit_button').click();
      }
      function processpsalaryResponse(e){
        var sErrorMsg = '';
          ['pdate'].forEach(function(ids){
            if($(ids))
              $(ids).removeClassName('red');
          });
        if(e.responseJSON.errorcode && e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>Не заполнено обязательное поле "Дата назначения"</li>'; $('pdate').addClassName('red'); break;                     
            }
          });
          $("errorpsalarylist").innerHTML=sErrorMsg;
          $("errorpsalarylist").up('div').show();
        } else{    
          if(e.responseJSON.refresh){
            location.reload(true);
          }else{
            jQuery('#psalaryEditForm').slideUp();
            $('psalary_submit_button').click();
          }
        }         
      }
      function processpersaccountResponse(e){
        var sErrorMsg = '';
        ['bank_id','validmonth','validyear','nomer','paccount','pin'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode && e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>Не заполнено обязательное поле "БИК банка"</li>'; $('bank_id').addClassName('red'); break;
              case 2: sErrorMsg+='<li>Некорректные данные в поле "Номер карты"</li>'; $("nomer").addClassName('red'); break;
              case 3: sErrorMsg+='<li>Не заполнено обязательное поле "Номер лиц. счета"</li>'; $('paccount').addClassName('red'); break;
              case 4: sErrorMsg+='<li>Некорректные данные в поле рублевый "Номер лиц. счета"</li>'; $("paccount").addClassName('red'); break;             
              case 7: sErrorMsg+='<li>У сотрудника уже есть активный счет</li>'; break;
              case 8: sErrorMsg+='<li>У специалиста уже есть активный счет</li>'; break;
              case 9: sErrorMsg+='<li>У директора уже есть активный счет этого типа</li>'; break;
              case 10: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["БИК банка"])}</li>'; $('bank_id').addClassName('red'); break;
              case 12: sErrorMsg+='<li>Карта просрочена</li>'; $('validmonth').addClassName('red'); $('validyear').addClassName('red'); break;
              case 13: sErrorMsg+='<li>Некорректные данные в поле "PIN код"</li>'; $('pin').addClassName('red'); break;
            }
          });
          $("errorpersaccountlist").innerHTML=sErrorMsg;
          $("errorpersaccountlist").up('div').show();
        } else{               
          jQuery('#persaccountAddForm').slideUp();
          $('persaccount_submit_button').click();
        }  
      }
      function toggleIs_Fixactsalary(iValue){
        if(iValue==2){
          $("is_fixactsalary_label").show();
          
        }else
          $("is_fixactsalary_label").hide();
      }
    <g:if test="${pers_user?.perstype==2}">
      function toggleAddsalary(){
        if(jQuery("#is_fixactsalary").is(':checked')){          
          jQuery('#addpsalarybutton').parent().show();       
        } else {          
          jQuery('#addpsalarybutton').parent().hide();         
        }
      } 
    </g:if>
      function newUser(){
        var iId=${pers_user?.id?:0};
        if(iId>0)
          window.open('${createLink(controller:'user',action:'userdetail')}'+'/?pers_id='+iId);
      }      
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px}      
      input.mini{width:60px!important}
    </style>
  </head>
  <body onload="init();">
    <h3 class="fleft"><g:if test="${pers_user}">Физ. лицо № ${pers_user.id}</g:if><g:else>Добавление нового физ. лица</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку физ. лиц</a>
    <div class="clear"></div>
    
    <div class="info-box" style="display:none;margin-top:0">
      <span class="icon icon-info-sign icon-3x"></span>
      <ul id="infolist">      
        <li>Изменения сохранены</li>
      </ul>
    </div>
    <div class="error-box" style="display:none">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>
    <g:if test="${pers_user}">
      <label for="pers_user_id" disabled>Id:</label>
      <input type="text" id="pers_user_id" readonly value="${pers_user?.id}" />
      <label for="inputdate" disabled>Дата заведения:</label>
      <input type="text" id="inputdate" readonly value="${String.format('%td.%<tm.%<tY %<tT',pers_user?.inputdate)}" />
      <hr class="admin">
    </g:if>
    <g:formRemote name="persDetailForm" url="[action:'savePersDetail',id:pers_user?.id?:0]" method="post" onSuccess="processResponse(e)">          
      <label for="fullname">Полное имя:</label>
      <input type="text" id="fullname" name="fullname" value="${pers_user?.fullname?:''}" class="fullline" onblur="setShortname(this.value)" maxlength="150"/>
      <label for="shortname">Фамилия:</label>
      <input type="text" id="shortname" name="shortname" value="${pers_user?.shortname?:''}" maxlength="50"/>      
      <label for="birthdate">Дата рождения:</label>
      <g:datepicker class="normal nopad" style="margin-right:110px" name="birthdate" value="${pers_user?.birthdate?String.format('%td.%<tm.%<tY',pers_user?.birthdate):''}"/>           
      <label for="snils" disabled>Учетный номер:</label>
      <input type="text" id="snils" name="snils" value="${pers_user?.snils?:''}" disabled/>
      <label for="passport">Паспорт:<br/><small>пример: 12 12 123456</small></label>
      <input type="text" id="passport" name="passport" value="${pers_user?.passport?:''}"/>
      <label for="passdate">Дата выдачи:</label>
      <g:datepicker class="normal nopad" style="margin-right:110px" name="passdate" value="${pers_user?.passdate?:''}"/>
      <label for="kodpodr">Код подр-ния:<br/><small>пример: 333-333</small></label>
      <input type="text" id="kodpodr" name="kodpodr" value="${pers_user?.kodpodr}"/>
      <label for="passorg">Организация выд.:</label>
      <input type="text" class="fullline" id="passorg" name="passorg" value="${pers_user?.passorg?:''}"/>
      <label for="inn">ИНН:<br/><small>12 знаков</small></label>
      <input type="text" id="inn" name="inn" value="${pers_user?.inn?:''}"/>
      <label for="snilsdpf">СНИЛС:<br/><small>пример: 123-123-123 12</small></label>
      <input type="text" id="snilsdpf" name="snilsdpf" value="${pers_user?.snilsdpf?:''}"/>
      <label for="perstype" <g:if test="${pers_user}">disabled</g:if>>Тип:</label>
      <g:select name="perstype" value="${pers_user?.perstype?:0}" keys="${1..3}" from="${['сотрудник','директор','специалист']}" noSelection="${['0':'не указано']}" disabled="${pers_user?'true':'false'}" onchange="toggleIs_Fixactsalary(this.value)"/>
      <label for="is_fixactsalary" id="is_fixactsalary_label" <g:if test="${user.confaccess!=2 && (pers_user?.perstype!=2 || !user.is_tehdirleader)}">disabled</g:if> style="${pers_user?.perstype!=2?'display:none':''}">
        <input type="checkbox" id="is_fixactsalary" name="is_fixactsalary" value="1" onclick="toggleAddsalary()" <g:if test="${pers_user?.is_fixactsalary}">checked="true"</g:if> <g:if test="${user.confaccess!=2 && (pers_user?.perstype!=2 || !user.is_tehdirleader)}">disabled</g:if>/>
        Фикс. оклад
      </label>
      <br/><label for="birthcity">Место рождения:</label>
      <input type="text" id="birthcity" name="birthcity" value="${pers_user?.birthcity}" class="fullline"/>
      <label for="propiska">Прописка:</label>
      <input type="text" id="propiska" name="propiska" value="${pers_user?.propiska?:''}" class="fullline"/>
      <label for="citizen">Гражданство:</label>
      <input type="text" class="fullline" id="citizen" name="citizen" value="${pers_user?.citizen?:''}"/>
      <label for="education">Образование:</label>
      <input type="text" class="fullline" id="education" name="education" value="${pers_user?.education?:''}"/>
      <g:if test="${(user.confaccess || (pers_user?.perstype==2 && user.is_tehdirleader)) && pers_user?.perstype!=Pers.PERSTYPE_SPECIALIST}">
        <label for="actsalary" id="actsalary_label" disabled>Фактическая з.п.:</label>
        <input type="text" id="actsalary" name="actsalary" value="${intnumber(value:pers_user?.actsalary?:0)}" disabled="true"/>
        <g:if test="${pers_user}">
          <label for="cassadebt" disabled>Задолженность<br/>перед кассой:</label>
          <input type="text" id="cassadebt" name="cassadebt" value="${intnumber(value:pers_user?.cassadebt)}" readonly/>
        </g:if>
      </g:if>
      <hr class="admin">
      <div class="fright" id="btns">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
        <g:if test="${(pers_user && session.user.group?.is_persedit) || (!pers_user && session.user.group?.is_persinsert)}"><input type="submit" id="submit_button" value="Сохранить"/></g:if>
      </div>
    </g:formRemote>
    <div class="clear"></div>
    <g:if test="${pers_user}">
      <div class="tabs">
        <ul class="nav">
          <li class="selected"><a href="javascript:void(0)" onclick="viewCell(0)">Компании</a></li>       
          <li style="${!((user.confaccess || (pers_user?.perstype==2 && user.is_tehdirleader)) && pers_user?.perstype!=Pers.PERSTYPE_SPECIALIST)?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(1)">История назначений</a></li>
          <li style="${!session.user?.group?.is_persaccount?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(2)">Персональные счета</a></li>
          <li><a href="javascript:void(0)" onclick="viewCell(3)">Пользователь</a></li>          
        </ul>
        <div class="tab-content">
          <div class="inner">
            <div id="details"></div>
          </div>
        </div>
      </div>
      <g:formRemote name="compersForm" url="[action:'comperslist',id:pers_user.id]" update="[success:'details']">
        <input type="submit" class="button" id="compers_submit_button" value="Показать" style="display:none" />
      </g:formRemote>
      <g:formRemote name="psalaryForm" url="[action:'psalarylist',id:pers_user.id]" update="[success:'details']">
        <input type="submit" class="button" id="psalary_submit_button" value="Показать" style="display:none" />        
      </g:formRemote>
      <g:formRemote name="persaccountForm" url="[action:'persaccountlist',id:pers_user.id]" update="[success:'details']">
        <input type="submit" class="button" id="persaccount_submit_button" value="Показать" style="display:none" />        
      </g:formRemote>
      <g:formRemote name="userForm" url="[action:'puser',id:pers_user.id]" update="[success:'details']">
        <input type="submit" class="button" id="puser_submit_button" value="Показать" style="display:none" />        
      </g:formRemote>
    </g:if>    
    <g:form  id="returnToListForm" name="returnToListForm" url="${[action:'pers',params:[fromEdit:1]]}">
    </g:form>
  </body>
</html>
