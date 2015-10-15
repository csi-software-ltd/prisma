<html>
  <head>
    <title>${infotext?.title?:'Prisma - Платежи'}</title>
    <meta name="layout" content="main" /> 
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript>     
      var iAnchorId=0;
      var iPayrequestAnchorId=0;
      var sLinkHref='';
      var sPayrequestLinkHref='';
      var iPayrequestCheckboxesChecked=0;
      
      function clickPaginate(event){
        event.stop();
        var link = event.element();
        if(link.href == null){
          return;
        }
        sLinkHref=link.href;
        new Ajax.Updater(
          { success: $('ajax_wrap') },
          link.href,
          { evalScripts: true });
      }
      function setactivelink(iStatus){
        jQuery('.tabs a[class="active"]').removeClass('active');
        $('paymentlink'+iStatus).addClassName('active');
      }      
      function resetPaymentFilter(iVal){
        if(iVal==0){
          $('paydate').value='';
          $('modstatus').selectedIndex=0;
          $('fromcompany').value='';
          $('tocompany').value='';
          $('is_fact').checked = false;
        }
        $('pid').value='';
        $('summa').value='';
        $('destination').value='';
        $('platnumber').value='';
        $('frombank').value='';
        $('finstatus').selectedIndex=0;
        $('paytype').selectedIndex=0;
        $('paycat').selectedIndex=0;
        $('kbk').value='';
        $('pers').value='';
        $('pers_id').value=0;
        $('is_dest').checked = false;
      }
      function toggleKbk(iValue){
        $("kbk_label").removeAttribute("disabled");
        if(iValue==2){          
          $("kbk").disabled=false;
        }else{
          $("kbk_label").setAttribute("disabled","true");
          $("kbk").disabled=true;
          $("kbk").value='';
        }
      }
      function togglePers(iValue){
        $("pers_label").removeAttribute("disabled");
        if(iValue==3){          
          $("pers").disabled=false;
        }else{
          $("pers_label").setAttribute("disabled","true");
          $("pers").disabled=true;
          $("pers").value='';
        }
      }
      function setActSaldo(iId){
        if(iId){
          var iActsaldo=$('actsaldo_'+iId).value;
          var sActsaldodate=$('actsaldodate_'+iId).value;
          iAnchorId=iId;
          <g:remoteFunction controller='payment' action='setActSaldo' params="'id='+iId+'&actsaldo='+iActsaldo+'&actsaldodate='+sActsaldodate" onSuccess="refreshListing()" />
        }
      }
      function setAccountBlock(iId,iBlock){
        if(iId){
          iAnchorId=iId;
          <g:remoteFunction controller='payment' action='setAccountBlock' params="'id='+iId+'&block='+iBlock" onSuccess="refreshListing()" />
        } 
      }
      function setNoSms(iId,iNoSms){
        if(iId){
          iAnchorId=iId;
          <g:remoteFunction controller='payment' action='setNoSms' params="'id='+iId+'&nosms='+iNoSms" onSuccess="refreshListing()" />
        }
      }
      function scrollToAnchor(){
        if(iAnchorId){
          var aTag = jQuery("#actsaldo_"+iAnchorId);          
          jQuery('html,body').animate({scrollTop: aTag.offset().top-200},'slow');
        }
      }       
      function refreshListing(){
        if(sLinkHref){
          new Ajax.Updater(
          { success: $('ajax_wrap') },
          sLinkHref,
          { evalScripts: true, onComplete:function(){scrollToAnchor()} });
        }else{
          $('form_submit_button').click();  
        }        
      }
      function submitLogic(){
        iAnchorId=0;
        sLinkHref='';
        $('form_submit_button').click();
      }
      function resetSaldoFilter(){
        $("bank_id").value='';
        $("company_id").value=0;
      }
      //>payrequest//////////////////////////////////////////////////////////
      function resetPayrequestFilter(){
        $("companyname").value = '';
        $("paydate").value = '';
        $("pid").value = '';
        $("modstatus").selectedIndex = 0;
        $("instatus").selectedIndex = 0;
        $("paytype").selectedIndex = 0;
        $('is_noclient').checked = true;
        $('is_noinner').checked = true;
        $('is_notag').checked = false;
      }
      function scrollPayrequestToAnchor(){        
        if(iPayrequestAnchorId){
          var aTag = jQuery("#tr_"+iPayrequestAnchorId);          
          jQuery('html,body').animate({scrollTop: aTag.offset().top-200},'slow');
        }
      }
      function submitPayrequestLogic(){        
        iPayrequestAnchorId=0;
        sPayrequestLinkHref='';
        $('form_submit_button').click();
      }
      function toggleCheckboxes(){             
        if(iPayrequestCheckboxesChecked){
          jQuery("table input:checkbox").each(function() {
            jQuery(this).prop('checked', false);
          });          
          iPayrequestCheckboxesChecked=0;
        }else{        
          jQuery("table input:checkbox").each(function() {
            jQuery(this).prop('checked', true);
          }); 
          iPayrequestCheckboxesChecked=1;
        }  
      }       
      function submitTaskLogic(iVal){
        getCheckedIds()
        if (iVal) $('creating_strategy').value=1;
        else $('creating_strategy').value=0;
        $("form_task_submit_button").click();
      }
      function getCheckedIds(){
        var aIds=[];
        jQuery("table input:checkbox:checked").each(function() {
          aIds.push(jQuery(this).val());
        });
        $("payrequest_ids").value=aIds;
        return aIds.length;
      }
      function processPayrequestTask(e){
        var sErrorMsg = '';
        ['term'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });

        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>Не выбран "Срок исполнения:"</li>'; $('term').up('span').addClassName('k-error-colored'); break;
              case 2: sErrorMsg+='<li>Некорректные данные в поле "Срок исполнения"</li>'; $("term").up('span').addClassName('k-error-colored'); break;
             }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else {
          $("errorlist").up('div').hide();
          $("form_submit_button_logic").click();
        }
      }
      function toggleaddition(){        
        if(!jQuery("#addition").is(':hidden')){
          $("expandlink").innerHTML = '&nbsp;&nbsp;Развернуть&nbsp;<i class="icon-collapse"></i>';
          jQuery('#addition').slideUp(); 
          resetPaymentFilter(1);          
        } else {
          $("expandlink").innerHTML = '&nbsp;&nbsp;Скрыть&nbsp;<i class="icon-collapse-top"></i>';
          jQuery('#addition').slideDown();         
        }
      }
      function setSaldo(iId){
        if(iId){       
          <g:remoteFunction controller='payment' action='setSaldo' params="'id='+iId" onSuccess="saldoMessage(e)" />
        }
      }
      function saldoMessage(e){
        if(e.responseJSON.result==1){
          alert('Операция успешно выполнена');
          location.assign('${createLink(controller:controllerName,action:'index',params:[fromDetails:1])}');        
        }else{       
          if(e.responseJSON.result==2)
            alert('Ошибка! Не найден расчетный счет!');
          else          
            alert('Ошибка!');
        }  
      }
      function setSaldoAll(el){
        el.hide();
        <g:remoteFunction controller='payment' action='setSaldoAll' onSuccess="processSetSaldoAll(e,el)" />
      }
      function processSetSaldoAll(e,el){
        var sResult='Ошибка!';
        if(e.responseJSON.result.total>=0){
          sResult=e.responseJSON.result.complete +' из '+e.responseJSON.result.total+' расчитано.';
          if(e.responseJSON.result.notdone.length)
            sResult+=' Не расчитаны следующие платежи: '+e.responseJSON.result.snotdone+'.';
        }
        $("process_div").update(sResult);
        $("process_div").show();
        el.show();
        $("form_submit_button").click();
      }
      function deletePayment(iId,sNum){
        if(confirm('Вы уверены, что хотите удалить платеж № '+sNum+'?')) { 
          <g:remoteFunction controller='payment' action='deletePayment' params="'id='+iId" onSuccess="location.assign('${createLink(controller:controllerName,action:'index',params:[fromDetails:1])}');" />          
        }        
      }
      function togglecheck(){
        if(document.getElementById('groupcheckbox').checked)
          jQuery('#createDealForm :checkbox:not(:checked):not(:disabled)').each(function(){ this.checked=true; });
        else
          jQuery('#createDealForm :checkbox:checked').each(function(){ this.checked=false; });
      }
      function resetclfilter(){
        $('company_name').value = '';
        $('paydate_start').value = '';
        $('paydate_end').value = '';
        $('clid').value = '';
        $('modstatus').selectedIndex = 0;
        $('client_id').selectedIndex = 0;
        $('paytype').selectedIndex = 0;
        $('is_deal').checked = false;
        $('is_noinner').checked = true;
        getSubclientsList(0)
      }
      function resetbudgfilter(){
        $("companyname").value = '';
        $("paydate").value = '';
        $("modstatus").selectedIndex = 0;
      }
      function resettfilter(){
        $('company_name').value = '';
        $('paydate_start').value = '';
        $('paydate_end').value = '';
        $('modstatus').selectedIndex = 0;
      }
      function resetPrjFilter(){
        $('project_id').selectedIndex = 0;
      }
      function init(iVal){
        if(iVal==-1) jQuery('.tabs').find('a:first').click();
        else $('paymentlink'+iVal).click();
      }
      function setBankSaldo(iId){
        if(iId){
          var iBanksaldo=$('banksaldo_'+iId).value;
          var sBanksaldodate=$('banksaldodate_'+iId).value;
          <g:remoteFunction controller='payment' action='setBanksaldo' params="'id='+iId+'&banksaldo='+iBanksaldo+'&banksaldodate='+sBanksaldodate" onSuccess="\$('form_submit_button').click()" />
        }
      }
      function resetBankSaldoFilter(){
        $("company").value='';
        $("inn").value='';
        $("bank").value='';
        $("bank_id").value='';
        $("company_id").value=0;
        $('order').selectedIndex = 0;
        $('valuta_id').selectedIndex = 0;
        $('typeaccount_id').selectedIndex = 0;
      }
      function getSubclientsList(sClId){
        <g:remoteFunction controller='payment' action='subclientslist' params="'client_id='+sClId" update="subclientslist"/>
      }
    </g:javascript>
  </head>
  <body onload="init(${inrequest?.paymentobject?:-1})">
    <div class="tabs padtop fright">
    <g:if test="${session?.user?.group?.is_payplan}">
      <g:remoteLink id="paymentlink1" before="setactivelink(1)" url="${[controller:controllerName, action:'payrequestfilter', params:inrequest]}" update="filter"><i class="icon-list icon-large"></i> Фактические </g:remoteLink>
    </g:if>
    <g:if test="${isclientpayment}">
      <g:remoteLink id="paymentlink3" before="setactivelink(3)" url="${[controller:controllerName, action:'clpaymentsfilter']}" update="filter"><i class="icon-list icon-large"></i> Клиентские </g:remoteLink>
    </g:if>
    <g:if test="${ispayordering}">
      <g:remoteLink id="paymentlink7" before="setactivelink(7)" url="${[controller:controllerName,action:'paymentfilter']}" update="filter"><i class="icon-list icon-large"></i> Выписка </g:remoteLink>
    </g:if>
    <g:if test="${isclientpayment}">
      <g:remoteLink id="paymentlink4" before="setactivelink(4)" url="${[controller:controllerName, action:'dealfilter']}" update="filter"><i class="icon-list icon-large"></i> Сделки </g:remoteLink>
      <g:remoteLink id="paymentlink12" before="setactivelink(12)" url="${[controller:controllerName,action:'banksaldofilter']}"  update="filter"><i class="icon-list icon-large"></i> Средства банка </g:remoteLink>
    </g:if>
    <g:if test="${ispsaldo}">
      <g:remoteLink id="paymentlink2" before="setactivelink(2)" url="${[controller:controllerName,action:'saldofilter']}"  update="filter"><i class="icon-list icon-large"></i> Сверка остатков </g:remoteLink>
    </g:if>
    <g:if test="${isbudgpayment}">
      <g:remoteLink id="paymentlink5" before="setactivelink(5)" url="${[controller:controllerName, action:'budgpaymentsfilter']}" update="filter"><i class="icon-list icon-large"></i> Бюджетные </g:remoteLink>
    </g:if>
    <g:if test="${session.user.group.is_payproject}">
      <g:remoteLink id="paymentlink8" before="setactivelink(8)" url="${[controller:controllerName, action:'prjpaymentsfilter']}" update="filter"><i class="icon-list icon-large"></i> Проектные </g:remoteLink>
    </g:if>    
    <g:if test="${istpayment}">
      <g:remoteLink id="paymentlink6" before="setactivelink(6)" url="${[controller:controllerName, action:'tpaymentsfilter']}" update="filter"><i class="icon-tumblr icon-large"></i> Отдел Т </g:remoteLink>
    </g:if>
    <g:if test="${isdpcpayment}">
      <g:remoteLink id="paymentlink9" before="setactivelink(9)" url="${[controller:controllerName, action:'dcpaymentsfilter']}" update="filter"><i class="icon-credit-card icon-large"></i> Доп. карты. Выплаты </g:remoteLink>
      <g:remoteLink id="paymentlink10" before="setactivelink(10)" url="${[controller:controllerName, action:'dcincomefilter']}" update="filter"><i class="icon-credit-card icon-large"></i> Доп. карты. Поступления </g:remoteLink>
      <g:remoteLink id="paymentlink11" before="setactivelink(11)" url="${[controller:controllerName, action:'dccomissionfilter']}" update="filter"><i class="icon-credit-card icon-large"></i> Доп. карты. Комиссии </g:remoteLink>
    </g:if>
    </div>
    <div class="clear"></div>
    <div id="filter" class="padtop filter">
    </div>
    <div id="list"></div>
  </body>
</html>
