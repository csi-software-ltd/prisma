<html>
  <head>
    <title>Prisma: <g:if test="${company}">${company.name}</g:if><g:else>Новая компания</g:else></title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript>
      var isOpen = false, isTagOpen = false, isEmpTagOpen = false;
      var expandlink,tagexpandlink;
      function returnToList(){
        $("returnToListForm").submit();
      }
      function init(){
        if (!$("legalname").value) copyname($('cname').value);
        jQuery("#opendate").mask("99.99.9999",{placeholder:" "});
        jQuery("#namedate").mask("99.99.9999",{placeholder:" "});
        jQuery("#adrdate").mask("99.99.9999",{placeholder:" "});
        jQuery("#capitaldate").mask("99.99.9999",{placeholder:" "});
        jQuery("#reregdate").mask("99.99.9999",{placeholder:" "});
        jQuery("#reqdate").mask("99.99.9999",{placeholder:" "});
        jQuery("#picdate").mask("99.99.9999",{placeholder:" "});
        jQuery("#ldocdate").mask("99.99.9999",{placeholder:" "});
        jQuery("#inn").mask("9999999999?99",{placeholder:" "});
        jQuery("#ogrn").mask("9999999999999",{placeholder:" "});
        if (jQuery("#okato")) {
          jQuery("#okato").mask("99999999999",{placeholder:" "});
          jQuery("#okpo").mask("99999999",{placeholder:" "});
          jQuery("#oktmo").mask("99999999",{placeholder:" "});
        }
        expandlink = document.getElementById('expandlink');
        tagexpandlink = document.getElementById('tagexpandlink');
        new Autocomplete('kpp', {
          serviceUrl:'${resource(dir:"autocomplete",file:"kpp_autocomplete")}',
          onSelect: function(value, data){             
            $('taxinspection_id').value = data;            
          }
        });
      }
      function nextfield(event){
        var cancelsubmit=true;
        switch(document.activeElement.id){
          case 'account_bankname': $('account_schet').focus();break;
          case 'account_schet': $('account_opendate').focus();break;
          case 'account_opendate': $('account_closedate').focus();break;
          case 'account_closedate': $('account_ibank_open').focus();break;
          case 'account_ibank_open': $('account_ibank_close').focus();break;
          case 'account_ibank_close': $('account_ibank_comment').focus();break;
          case 'accountadd_submit_button': cancelsubmit=false;break;
        }
        if (cancelsubmit) event.stop();
      }
      function newPers(){
        window.open('${createLink(controller:'user',action:'persdetail')}');
      }
      function copyadr(sAdr){
        if(!$("postadr").value) $("postadr").value = sAdr;
      }
      function copyname(sName){
        $("legalname").value=sName.replace(/(^| )ООО /," Общество с ограниченной ответственностью ").replace(/(^| )ЗАО /," Закрытое Акционерное Общество ").replace(/(^| )ОАО /," Открытое Акционерное Общество ").replace(/(^| )ИП /," Индивидуальный Предприниматель ").replace(/(^| )НП /," Некоммерческое партнерство ").replace(/(^| )СРО /," Саморегулируемая организация ").trim();
      }
      function toggleData(iHolding){
        if(iHolding=='1'){
          $('is_bank').up('label').hide();
          $('nonholdingdata').hide();
          $('holdingdata').show();
        } else {
          $('holdingdata').hide();
          $('nonholdingdata').show();
          $('is_bank').up('label').show();
        }
      }
      function toggleaddtel(el){
        if(el.checked) $('addtel').show()
        else $('addtel').hide();
      }
      function toggleemptagsection(){
        if(!document.getElementById('emptagexpandlink')) return false;
        if(isEmpTagOpen){
          document.getElementById('emptagexpandlink').innerHTML = '&nbsp;&nbsp;Тегировать&nbsp;<i class="icon-collapse"></i>';
          jQuery('#emptagsection').slideUp();
          isEmpTagOpen = false;
        } else {
          document.getElementById('emptagexpandlink').innerHTML = '&nbsp;&nbsp;Тегировать&nbsp;<i class="icon-collapse-top"></i>';
          jQuery('#emptagsection').slideDown();
          isEmpTagOpen = true;
        }
      }
      function toggletagsection(){
        if(!tagexpandlink) return false;
        if(isTagOpen){
          tagexpandlink.innerHTML = '&nbsp;&nbsp;Тегировать&nbsp;<i class="icon-collapse"></i>';
          jQuery('#tagsection').slideUp();
          isTagOpen = false;
        } else {
          tagexpandlink.innerHTML = '&nbsp;&nbsp;Тегировать&nbsp;<i class="icon-collapse-top"></i>';
          jQuery('#tagsection').slideDown();
          isTagOpen = true;
        }
      }
      function toggleCapitalPaid(iVal){
        if(iVal==2) $('capitalpaidsection').show();
        else $('capitalpaidsection').hide();
      }
      function toggledopinfo(){
        if(!expandlink) return false;
        if(isOpen){
          expandlink.innerHTML = '&nbsp;&nbsp;Доп.&nbsp;инфо&nbsp;<i class="icon-collapse"></i>';
          jQuery('#dopinfo').slideUp();
          isOpen = false;
        } else {
          expandlink.innerHTML = '&nbsp;&nbsp;Доп.&nbsp;инфо&nbsp;<i class="icon-collapse-top"></i>';
          jQuery('#dopinfo').slideDown();
          isOpen = true;
        }
      }
      function setEmployeestatus(iStatus){
        $('employee_status').value=iStatus;
        $('personal_submit_button').click();
      }
      function setAccountstatus(iStatus){
        $('account_status').value=iStatus;
        $('accounts_submit_button').click();
      }
      function setCompokvedstatus(iStatus){
        $('compokved_status').value=iStatus;
        $('okveds_submit_button').click();
      }
      function setOkvedModstatus(iStatus){
        $('okved_modstatus').value=iStatus;
        $('addtookved_submit_button').click();
      }
      function banknamevalidate(){
        setTimeout(function() { 
          var bname = $('account_bankname').value
          <g:remoteFunction controller='company' action='banknamevalidate' params="'bankname='+bname" onSuccess="\$('account_bank_id').value=e.responseText.split(';')[0];\$('account_coraccount').value=e.responseText.split(';')[1];" />
        }, 300)
      }
      function bikvalidate(){
        setTimeout(function() { 
          var bik = $('account_bank_id').value
          <g:remoteFunction controller='company' action='bikvalidate' params="'bik='+bik" onSuccess="\$('account_bankname').value=e.responseText.split(';')[0];\$('account_coraccount').value=e.responseText.split(';')[1];"/>
        }, 300)
      }
      function togglevaliddate(){
        var iValue = $('employee_composition_id').options[$('employee_composition_id').selectedIndex].className.split('_')[1];
        if(iValue=='1'){
          $('gd_valid_container').show()
        } else {
          $('gd_valid_container').hide()
        }
      }
      function setSpaceAsort(iStatus){
        $('space_asort').value=iStatus;
        jQuery('.tabs a[class="active"]').removeClass('active');
        $('spaceasort'+iStatus).addClassName('active');
        $('spaces_submit_button').click();
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['cname','inn','ogrn','email','kpp'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['opendate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Название"])}</li>'; $('cname').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["ИНН"])}</li>'; $('inn').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["ОГРН"])}</li>'; $('ogrn').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Email"])}</li>'; $('email').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["ИНН"])}</li>'; $('inn').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["КПП"])}</li>'; $('kpp').addClassName('red'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["КПП"])}</li>'; $('kpp').addClassName('red'); break;
              case 8: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата регистрации"])}</li>'; $('opendate').up('span').addClassName('k-error-colored'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.not.unique.message",args:["Компания","ИНН"])}</li>'; $('inn').addClassName('red'); break;
              case 10: sErrorMsg+='<li>${message(code:"error.blank.message",args:["ОГРН"])}</li>'; $('ogrn').addClassName('red'); break;
              case 11: sErrorMsg+='<li>${message(code:"error.not.unique2.message",args:["Компания","ОГРН"])}</li>'; $('ogrn').addClassName('red'); break;
              case 12: sErrorMsg+='<li>${message(code:"error.not.unique2.message",args:["Компания","Названием"])}</li>'; $('cname').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${company?1:0}){
          location.reload(true);
        } else if(e.responseJSON.company){
          location.assign('${createLink(controller:controllerName,action:'detail')}'+'/'+e.responseJSON.company);
        } else
          location.assign('${createLink(controller:controllerName,action:'index')}');
      }
      function processaddprojectResponse(e){
        var sErrorMsg = '';
        ['project_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Проект"])}</li>'; $('project_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorprojectlist").innerHTML=sErrorMsg;
          $("errorprojectlist").up('div').show();
        } else
          jQuery('#projectAddForm').slideUp(300, function() {$('projects_submit_button').click();});
      }
      function processaddemployeeResponse(e){
        var sErrorMsg = '';
        ['employee_comment','employee_gd_valid','employee_composition_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['employee_jobstart'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        ['employee_pers_name'].forEach(function(ids){
          if($(ids).up('span'))
            $(ids).up('span').removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Фио"])}</li>'; $('employee_pers_name').up('span').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Должность"])}</li>'; $('employee_composition_id').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата начала работы"])}</li>'; $('employee_jobstart').up('span').addClassName('k-error-colored'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок полномочий"])}</li>'; $('employee_gd_valid').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Комментарий"])}</li>'; $('employee_comment').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.not.unique2.message",args:["Этот сотрудник","типом"])}</li>'; $('employee_pers_name').up('span').addClassName('red'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.not.unique2.message",args:["Сотрудник","типом"])}</li>'; $('employee_composition_id').addClassName('red'); break;
              case 8: sErrorMsg+='<li>${message(code:"error.type.not.equal.message",args:["Сотрудника","Должности"])}</li>'; $('employee_composition_id').addClassName('red'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["ФИО"])}</li>'; $('employee_pers_name').up('span').addClassName('red'); break;
              case 10: sErrorMsg+='<li>${message(code:"error.multiple.perschoice.message",args:["Сотрудника"])}</li>'; $('employee_pers_name').up('span').addClassName('red'); break;
              case 11: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Дата изменения доли"])}</li>'; $('employee_jobstart').up('span').addClassName('k-error-colored'); break;
              case 12: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата изменения доли"])}</li>'; $('employee_jobstart').up('span').addClassName('k-error-colored'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("erroremployeelist").innerHTML=sErrorMsg;
          $("erroremployeelist").up('div').show();
        } else
          jQuery('#employeeAddForm').slideUp(300, function() {$('personal_submit_button').click();});
      }
      function processaddfounderResponse(e){
        var sErrorMsg = '';
        ['founder_share'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['founder_enddate','founder_startdate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        ['founder_pers_name','founder_company_name'].forEach(function(ids){
          if($(ids).up('span'))
            $(ids).up('span').removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Фио или Компания"])}</li>'; $('founder_pers_name').up('span').addClassName('red'); $('founder_company_name').up('span').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Фио"])}</li>'; $('founder_pers_name').up('span').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.multiple.perschoice.message",args:["Сотрудника"])}</li>'; $('founder_pers_name').up('span').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата начала"])}</li>'; $('founder_startdate').up('span').addClassName('k-error-colored'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Дата окончания"])}</li>'; $('founder_enddate').up('span').addClassName('k-error-colored'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Доля"])}</li>'; $('founder_share').addClassName('red'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.invalid.max.sum.message",args:["Долей","100%"])}</li>'; $('founder_share').addClassName('red'); break;
              case 8: sErrorMsg+='<li>${message(code:"error.not.single.data",args:["Фио","Компания"])}</li>'; $('founder_pers_name').up('span').addClassName('red'); $('founder_company_name').up('span').addClassName('red'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Компания"])}</li>'; $('founder_company_name').up('span').addClassName('red'); break;
              case 10: sErrorMsg+='<li>${message(code:"error.multiple.choice.message",args:["Компанию","Компания"])}</li>'; $('founder_company_name').up('span').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorfounderlist").innerHTML=sErrorMsg;
          $("errorfounderlist").up('div').show();
        } else
          jQuery('#founderAddForm').slideUp(300, function() {$('personal_submit_button').click();});
      }
      function processaddvacancyResponse(e){
        var sErrorMsg = '';
        ['vacancy_composition_id','vacancy_numbers','vacancy_salary'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Должность"])}</li>'; $('vacancy_composition_id').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Количество"])}</li>'; $('vacancy_numbers').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Количество"])}</li>'; $('vacancy_numbers').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Оклад"])}</li>'; $('vacancy_salary').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Оклад"])}</li>'; $('vacancy_salary').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorvacancylist").innerHTML=sErrorMsg;
          $("errorvacancylist").up('div').show();
        } else
          jQuery('#vacancyAddForm').slideUp(300, function() {$('personal_submit_button').click();});
      }
      function processaddaccountResponse(e){
        var sErrorMsg = '';
        ['account_bank_id','account_schet','account_valuta_id','account_typeaccount_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['account_closedate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Бик"])}</li>'; $('account_bank_id').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Бик"])}</li>'; $('account_bank_id').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Счет"])}</li>'; $('account_schet').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Счет"])}</li>'; $('account_schet').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Дата закрытия счета"])}</li>'; $('account_closedate').up('span').addClassName('k-error-colored'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.not.unique.account.message",args:["Счет"])}</li>'; $('account_typeaccount_id').addClassName('red'); break;
              case 8: sErrorMsg+='<li>${message(code:"error.not.unique.valuteaccount.message",args:["Счет"])}</li>'; $('account_typeaccount_id').addClassName('red'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Валюта счета для рассчетных счетов"])}</li>'; $('account_valuta_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("erroraccountlist").innerHTML=sErrorMsg;
          $("erroraccountlist").up('div').show();
        } else {
          $("erroraccountlist").up('div').hide();
          jQuery('#accountAddForm').slideUp(300, function() {$('accounts_submit_button').click();});
        }
      }
      function processaddokvedResponse(e){
        var sErrorMsg = '';
        ['okved_name'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["ОКВЭД"])}</li>'; $('okved_name').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["ОКВЭД"])}</li>'; $('okved_name').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата изменения"])}</li>'; $('moddate').up('span').addClassName('k-error-colored'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorokvedlist").innerHTML=sErrorMsg;
          $("errorokvedlist").up('div').show();
        } else
          jQuery('#okvedAddForm').slideUp(300, function() {$('okveds_submit_button').click();});
      }
      function processarchokvedResponse(e){
        var sErrorMsg = '';
        ['comments'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Комментарий"])}</li>'; $('project_id').addClassName('red'); break;              
            }
          });
          $("errorokvedlist").innerHTML=sErrorMsg;
          $("errorokvedlist").up('div').show();
        } else
          jQuery('#okvedAddForm').slideUp(300, function() {$('okved_submit_button').click();});       
      }
      function processaddcomplicenseResponse(e){
        var sErrorMsg = '';
        ['complicense_name','complicense_nomer','complicense_formnumber','complicense_authority'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['complicense_validity','complicense_ldate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Название"])}</li>'; $('complicense_name').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата выдачи"])}</li>'; $('complicense_ldate').up('span').addClassName('k-error-colored'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Срок действия"])}</li>'; $('complicense_validity').up('span').addClassName('k-error-colored'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Срок действия"])}</li>'; $('complicense_validity').up('span').addClassName('k-error-colored'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Номер"])}</li>'; $('complicense_nomer').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Номер бланка"])}</li>'; $('complicense_formnumber').addClassName('red'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Выдавший орган"])}</li>'; $('complicense_authority').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorcomplicenselist").innerHTML=sErrorMsg;
          $("errorcomplicenselist").up('div').show();
        } else
          jQuery('#complicenseAddForm').slideUp(300, function() { getCompLicenses(); });
      }
      function submitForm(){
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
          case 0: getOKVED();break;
          case 1: getAccounts();break;
          case 2: getPersonal();break;
          case 3: getSpaces();break;
          case 4: getKredits();break;
          case 5: getPayrequests();break;
          case 6: getLicenses();break;
          case 7: getCompLicenses();break;
          case 8: getProjects();break;
          case 9: getHistory();break;
        }
      }
      function getProjects(){
        if(${company?1:0}) $('projects_submit_button').click();
      }
      function getPersonal(){
        if(${company?1:0}) $('personal_submit_button').click();
      }
      function getAccounts(){
        if(${company?1:0}) $('accounts_submit_button').click();
      }
      function getOKVED(){
        if(${company?1:0}) $('okveds_submit_button').click();
      }
      function getSpaces(){
        if(${company?1:0}) $('spaces_submit_button').click();
      }
      function getKredits(){
        if(${company?1:0}) $('kredits_submit_button').click();
      }
      function getPayrequests(){
        if(${company?1:0}) $('payrequests_submit_button').click();
      }
      function getLicenses(){
        if(${company?1:0}) $('licenses_submit_button').click();
      }
      function getCompLicenses(){
        if(${company?1:0}) $('complicenses_submit_button').click();
      }
      function getHistory(){
        if(${company?1:0}) $('history_submit_button').click();
      }
      function pdf(){
        if(${company?1:0}) $("pdfForm").submit();
      }
      function showRequisit(){
        $('requisitPdfForm').show();
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      label.fordate{min-width:219px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
      .tabs a:hover,.tabs a.active,.tabs a.active i{color:#000}
    </style>
  </head>
  <body onload="getOKVED();init()">
    <h3 class="fleft"><g:if test="${company}">${company.name}</g:if><g:else>Новая компания</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку компаний</a>
    <div class="clear"></div>
    <g:formRemote name="requestDetailForm" url="${[action:'update',id:company?.id?:0]}" method="post" onSuccess="processResponse(e)">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

    <g:if test="${company}">
      <label for="company_id" disabled>Код компании:</label>
      <input type="text" id="company_id" disabled value="${company.id}" />
      <label for="inputdate" disabled>Дата создания:</label>
      <input type="text" name="inputdate" disabled value="${String.format('%td.%<tm.%<tY %<tT',company.inputdate)}" />
      <label for="status" disabled>Статус:</label>
      <input type="text" id="status" disabled value="${company.modstatus?'Активная':'Закрытая'}" />
      <label for="saldo" disabled>Остаток по счетам:</label>
      <input type="text" id="saldo" disabled value="${intnumber(value:saldo)}" />
    <g:if test="${is_holding&&session.user.confaccess>0}">
      <label for="cost" disabled>Стоимость комп.:</label>
      <input type="text" class="fullline" id="cost" disabled value="${intnumber(value:company.cost)}" />
    </g:if>
      <hr class="admin" />
    </g:if>

      <label for="is_holding">Тип компании:</label>
      <g:select name="is_holding" value="${is_holding?1:0}" from="['Внешняя компания','Компания холдинга']" keys="[0,1]" onchange="toggleData(this.value)"/>
    <g:if test="${permissions.iscgroup}">
      <label for="cgroup_id">Группа:</label>
      <g:select name="cgroup_id" value="${company?.cgroup_id?:0}" from="${Cgroup.list(sort:'name',order:'asc')}" optionKey="id" optionValue="name" noSelection="${['0':'не задана']}"/>
    </g:if>
      <br/><label for="cname">Название:</label>
      <input type="text" id="cname" name="cname" value="${company?.name?:inrequest?.name}" onblur="copyname(this.value)"/>
      <label class="fordate" for="namedate">Дата смены названия:</label>
      <g:datepicker class="normal nopad" name="namedate" value="${company?.namedate?String.format('%td.%<tm.%<tY',company?.namedate):''}"/>
      <label for="legalname">Полное название:</label>
      <input type="text" class="fullline" id="legalname" name="legalname" value="${company?.legalname}" />
      <label for="inn">ИНН:</label>
      <input type="text" id="inn" name="inn" value="${company?.inn?:inrequest?.inn}"/>
      <label for="kpp">КПП:</label>
      <input type="text" id="kpp" name="kpp" value="${company?.kpp?:inrequest?.kpp}" />
      <div id="kpp_autocomplete" class="autocomplete" style="display:none"></div>      
      <label for="ogrn">ОГРН:</label>
      <input type="text" id="ogrn" name="ogrn" value="${company?.ogrn}"/>
      <label for="opendate">Дата регистрации:</label>
      <g:datepicker class="normal nopad" name="opendate" value="${String.format('%td.%<tm.%<tY',company?.opendate?:new Date())}"/>
      <label class="auto" for="is_bank" style="${is_holding?'display:none':''}">
        <input type="checkbox" id="is_bank" name="is_bank" value="1" <g:if test="${company?.is_bank}">checked</g:if> />
        Банк
      </label>
      <label for="okvedmain" disabled>ОКВЭД:</label>
      <input type="text" id="okvedmain" disabled value="${company?.okvedmain}" />
      <label for="taxinspection_id">Налоговая:</label>
      <g:select name="taxinspection_id" value="${company?.taxinspection_id?:0}" from="${Taxinspection.list()}" optionKey="id" optionValue="id"/>
      <label for="legaladr">Юридич. адрес:</label>
      <input type="text" class="fullline" id="legaladr" name="legaladr" value="${company?.legaladr}" onblur="copyadr(this.value)"/>
      <label for="taxoption_id">Тип налогов:</label>
      <g:select name="taxoption_id" value="${company?.taxoption_id?:0}" from="${Taxoption.list()}" optionValue="name" optionKey="id"/>
      <label class="fordate" for="adrdate">Дата смены юр. адреса:</label>
      <g:datepicker class="normal nopad" name="adrdate" value="${company?.adrdate?String.format('%td.%<tm.%<tY',company?.adrdate):''}"/><br/>
      <hr class="admin" style="width:780px;float:left"/><a id="expandlink" style="text-decoration:none" href="javascript:void(0)" onclick="toggledopinfo()">&nbsp;&nbsp;Доп.&nbsp;инфо&nbsp;<i class="icon-collapse"></i></a><hr class="admin" style="width:70px;float:right"/>
      <div id="dopinfo" style="display:none;width:960px">
        <div id="holdingdata" style="${!is_holding?'display:none':''}">
          <label for="okato">ОКАТО:</label>
          <input type="text" id="okato" name="okato" value="${company?.okato}"/>
          <label for="oktmo">ОКТМО:</label>
          <input type="text" id="oktmo" name="oktmo" value="${company?.oktmo}"/>
          <label for="okpo">ОКПО:</label>
          <input type="text" id="okpo" name="okpo" value="${company?.okpo}"/>
          <label for="okogu">ОКОГУ:</label>
          <input type="text" id="okogu" name="okogu" value="${company?.okogu}"/> 
          <label for="reregdate">Дата перерегистр.:</label>
          <g:datepicker class="normal nopad" name="reregdate" value="${company?.reregdate?String.format('%td.%<tm.%<tY',company?.reregdate):''}"/>
          <label for="regauthority" style="margin-left:123px">Регистрир. орган:</label>
          <input type="text" id="regauthority" name="regauthority" value="${company?.regauthority}" />          
          <label for="capital">Уставной капитал:</label>
          <input type="text" id="capital" name="capital" value="${intnumber(value:company?.capital)}"/>
          <label class="fordate" for="capitaldate">Дата смены уст. капитала:</label>
          <g:datepicker class="normal nopad" name="capitaldate" value="${company?.capitaldate?String.format('%td.%<tm.%<tY',company?.capitaldate):''}"/>
          <label for="capitalsecure">Внесен:</label>
          <g:select name="capitalsecure" value="${company?.capitalsecure}" from="['Имуществом','Деньгами']" keys="12" onchange="toggleCapitalPaid(this.value)"/>
          <span id="capitalpaidsection" style="${company?.capitalsecure!=2?'display:none':''}"><label for="capitalpaid">Оплачен:</label>
          <g:select name="capitalpaid" value="${company?.capitalpaid}" from="['Да','Нет']" keys="12"/></span>
          <br/><label for="postadr">Почтовый адрес:</label>
          <input type="text" class="fullline" id="postadr" name="postadr" value="${company?.postadr}"/>
          <label for="pfrfreg">Рег. № ПФРФ:</label>
          <input type="text" id="pfrfreg" name="pfrfreg" value="${company?.pfrfreg}"/>
          <label for="fssreg">Рег. № ФСС:</label>
          <input type="text" id="fssreg" name="fssreg" value="${company?.fssreg}"/>
          <label for="tel">Телефон:</label>
          <input type="text" id="tel" name="tel" value="${company?.tel}"/>
          <label for="smstel">Основной тел. б/к:</label>
          <input type="text" id="smstel" name="smstel" value="${company?.smstel}"/>
          <label for="email">Email:</label>
          <input type="text" id="email" name="email" value="${company?.email}"/>
          <label for="emailpassword">Пароль к Email:</label>
          <input type="text" id="emailpassword" name="emailpassword" value="${company?.emailpassword}"/>
          <label for="activitystatus_id">Статус:</label>
          <g:select name="activitystatus_id" value="${company?.activitystatus_id}" from="${Activitystatus.list()}" optionKey="id" optionValue="name"/>
          <label class="auto" for="is_dirchange">
            <input type="checkbox" id="is_dirchange" name="is_dirchange" value="1" <g:if test="${company?.is_dirchange}">checked</g:if> />
            Смена директора
          </label><br/>
          <label for="www">Сайт:</label>
          <input type="text" id="www" name="www" value="${company?.www}"/>
          <g:if test="${is_visual}"><label for="visualgroup_id">Видимость:</label>
          <g:select name="visualgroup_id" value="${company?.visualgroup_id}" from="${Visualgroup.list()}" optionKey="id" optionValue="name"/></g:if>
          <br/><label for="responsible1">Исполнитель 1:</label>
          <g:select name="responsible1" value="${company?.responsible1}" from="${responsiblies}" noSelection="${['0':'не указан']}" optionKey="id" optionValue="name"/>
          <label for="responsible2">Исполнитель 2:</label>
          <g:select name="responsible2" value="${company?.responsible2}" from="${responsiblies}" noSelection="${['0':'не указан']}" optionKey="id" optionValue="name"/>
        <g:if test="${session.user.confaccess>0}"> 
          <label for="outsource_id" <g:if test="${session.user.confaccess==1}">disabled</g:if>>Аутсорсер:</label>
          <g:select name="outsource_id" value="${company?.outsource_id}" from="${outsources}" noSelection="${['0':'не указан']}" optionKey="id" optionValue="name" disabled="${session.user.confaccess==1}"/>
          <label for="outsourceprice" <g:if test="${session.user.confaccess==1}">disabled</g:if>>Стоимость обсл.:</label>
          <input type="text" name="outsourceprice" id="outsourceprice" value="${company?.outsourceprice}" <g:if test="${session.user.confaccess==1}">disabled</g:if> />
        <g:if test="${is_holding}">
          <label for="buycost" <g:if test="${session.user.confaccess==1}">disabled</g:if>>Стоимость покупки</label>
          <input type="text" class="fullline" id="buycost" value="${intnumber(value:company?.buycost)}" <g:if test="${session.user.confaccess==1}">disabled</g:if>/>
          <label for="salecost" <g:if test="${session.user.confaccess==1}">disabled</g:if>>Стоимость продажи</label>
          <input type="text" class="fullline" id="salecost" value="${intnumber(value:company?.salecost)}" <g:if test="${session.user.confaccess==1}">disabled</g:if>/>
        </g:if>
        </g:if>
        </div>
        <label for="comment">Комментарий:</label>
        <g:textArea name="comment" id="comment" value="${company?.comment}" />
        <div id="nonholdingdata" style="${is_holding?'display:none':''}">
          <label for="is_req">Полные реквизиты:</label>
          <g:select name="is_req" value="${company?.is_req?:0}" from="['Нет','Да']" keys="01"/>
          <label class="fordate" for="reqdate">Дата полных реквизитов:</label>
          <g:datepicker class="normal nopad" name="reqdate" value="${company?.reqdate?String.format('%td.%<tm.%<tY',company?.reqdate):''}"/><br/>
          <label for="is_pic">Наличие картинок:</label>
          <g:select name="is_pic" value="${company?.is_pic?:0}" from="['Нет','Да']" keys="01"/>
          <label class="fordate" for="picdate">Дата получения картинок:</label>
          <g:datepicker class="normal nopad" name="picdate" value="${company?.picdate?String.format('%td.%<tm.%<tY',company?.picdate):''}"/><br/>
          <label for="is_ldoc">Учр. документы:</label>
          <g:select name="is_ldoc" value="${company?.is_ldoc?:0}" from="['Нет','Да']" keys="01"/>
          <label class="fordate" for="ldocdate">Дата учр. документов:</label>
          <g:datepicker class="normal nopad" name="ldocdate" value="${company?.ldocdate?String.format('%td.%<tm.%<tY',company?.ldocdate):''}"/><br/>
        </div>
      <g:if test="${!is_holding||!permissions.istag}">

        <hr class="admin" />
      </g:if>
      </div>
    <g:if test="${is_holding&&permissions.istag}">
      <div class="clear" style="padding-top:10px"></div>
      <hr class="admin" style="width:780px;float:left"/><a id="tagexpandlink" style="text-decoration:none" href="javascript:void(0)" onclick="toggletagsection()">&nbsp;&nbsp;Тегировать&nbsp;<i class="icon-collapse"></i></a><hr class="admin" style="width:70px;float:right"/>
      <div id="tagsection" style="display:none;width:960px">
        <label for="tagproject">Проект:</label>
        <g:select name="tagproject" value="${company?.tagproject}" from="${projects}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}"/>
        <label for="tagclient">Клиент:</label>
        <g:select name="tagclient" value="${company?.tagclient}" from="${clients}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}"/>
        <br/><label for="tagcomment">Комментарий к тегированию:</label>
        <g:textArea name="tagcomment" id="tagcomment" value="${company?.tagcomment}" />

        <hr class="admin" />
      </div>
    </g:if>

      <div class="fright" id="btns" style="padding-top:10px">
      <g:if test="${company&&permissions.iscard}">
        <input type="button" class="spacing" value="Карточка компании" onclick="pdf()"/>
      </g:if>
      <g:if test="${company&&permissions.isreqcard}">
        <input type="button" class="spacing" value="Реквизиты" onclick="showRequisit()"/>
      </g:if>
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${permissions.iscanedit}">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm()"/>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      </g:if>            
      </div>
    </g:formRemote>
    <g:form id="requisitPdfForm" style="display:none" name="requisitPdfForm" controller="company" action="requisit" id="${company?.id?:0}" target="_blank">
      <div class="clear" style="padding-top:10px"></div>
      <hr class="admin" />
      <label for="okved_pdf">ОКВЭД:</label>
      <g:select id="okved_pdf" name="okved" from="${compokved+[okvedname:'Все',okved_id:'']}" optionValue="okvedname" optionKey="okved_id" noSelection="${['none':'не выбран']}"/>
      <label for="schet">Расчетный счет:</label>
      <g:select id="schet_id_pdf" name="schet_id" value="" from="${account}" optionValue="bankname" optionKey="id" noSelection="${['':'не выбран']}"/>
      <div class="fright" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="$('requisitPdfForm').hide();" />
        <input type="submit" class="spacing" value="Отчет"/>      
      </div>
      <div class="clear"></div>
      <hr class="admin" />
    </g:form>
    <div class="clear"></div>
  <g:if test="${company}">
    <div class="tabs">
      <ul class="nav">
        <li class="selected"><a href="javascript:void(0)" onclick="viewCell(0)">ОКВЭД</a></li>
        <li style="${!permissions.isaccount?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(1)">Счета</a></li>
        <li style="${!is_holding||!permissions.isstaff?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(2)">Штат</a></li>
        <li style="${!is_holding||!permissions.isarenda?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(3)">Офисы и Склады</a></li>
        <li style="${!is_holding||!permissions.isagr?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(4)">Договора</a></li>
        <li style="${!permissions.ispayplan?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(5)">Платежи</a></li>
        <li style="${!is_holding||!permissions.islicense?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(6)">СРО</a></li>
        <li style="${!is_holding?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(7)">Лицензии</a></li>
        <li style="${!permissions.isproject?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(8)">Проекты</a></li>
        <li style="${!permissions.ishistory?'display:none':''}"><a href="javascript:void(0)" onclick="viewCell(9)">История</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="projectsForm" url="[action:'projects',id:company.id]" update="details">
      <input type="submit" class="button" id="projects_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="personalForm" url="[action:'personal',id:company.id]" update="details">
      <input type="submit" class="button" id="personal_submit_button" value="Показать" style="display:none" />
      <input type="hidden" id="employee_status" name="modstatus" value="1"/>
    </g:formRemote>
    <g:formRemote name="accountsForm" url="[action:'accounts',id:company.id]" update="details">
      <input type="submit" class="button" id="accounts_submit_button" value="Показать" style="display:none" />
      <input type="hidden" id="account_status" name="modstatus" value="1"/>
    </g:formRemote>
    <g:formRemote name="okvedsForm" url="[action:'okveds',id:company.id]" update="details">
      <input type="submit" class="button" id="okveds_submit_button" value="Показать" style="display:none" />
      <input type="hidden" id="compokved_status" name="modstatus" value="1"/>
    </g:formRemote>
    <g:formRemote name="spacesForm" url="[action:'spaces',id:company.id]" update="details">
      <input type="submit" class="button" id="spaces_submit_button" value="Показать" style="display:none" />
      <input type="hidden" id="space_asort" name="asort" value="1"/>
    </g:formRemote>
    <g:formRemote name="kreditsForm" url="[action:Agreementtype.findAllByCompanyactionNotEqual('').find{session.user.group."$it.checkfield"}?.companyaction?:'kredits',id:company.id]" update="details">
      <input type="submit" class="button" id="kredits_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="payrequestsForm" url="[action:'payrequests',id:company.id]" update="details">
      <input type="submit" class="button" id="payrequests_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="licensesForm" url="[action:'licenses',id:company.id]" update="details">
      <input type="submit" class="button" id="licenses_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="complicensesForm" url="[action:'complicenses',id:company.id]" update="details">
      <input type="submit" class="button" id="complicenses_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
    <g:formRemote name="historyForm" url="[action:'history',id:company.id]" update="details">
      <input type="submit" class="button" id="history_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
    <g:form id="pdfForm" name="pdfForm" controller="company" action="report" id="${company?.id?:0}" target="_blank">
    </g:form> 

  </body>
</html>