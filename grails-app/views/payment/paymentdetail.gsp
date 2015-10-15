<html>
  <head>
    <title>Prisma: Редактирование платежа № ${payment.id1c} (${payment.id})<g:if test="${payment.is_error}">&nbsp;(корр.)</g:if></title>
    <meta name="layout" content="main" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript>  
      var bSetSaldo=0;
      var bIsExit = false;
      var expcar_ids = ${expcar_ids};
      function init(){
        <g:if test="${flash?.paymentedit_success}">
          $("infolist").up('div').show();
        </g:if>
        new Autocomplete('expensetype_name', {
          serviceUrl:'${resource(dir:"autocomplete",file:"expensetype_autocomplete")}',
          onSelect: function(value, data){
            $('expensetype_id').value = data;
            showCars(data);
          }
        });
      }
      function returnToList(){
        $("returnToListForm").submit();
      }
      function showCars(iExpId){
        if(expcar_ids.indexOf(parseInt(iExpId))>-1) $('carsection').show();
        else $('carsection').hide();
      }
      function submitExit(){
        bIsExit = true;
        $('submit_button').click();
      }
      function newCompany(sFrom){
        var company='';
        var inn='';
        var kpp='';
        if(sFrom=='from'){
          company='${payment?.fromcompany?:''}';
          inn='${payment?.frominn?:''}';
          kpp='${payment?.fromkpp?:''}';
        }else{
          company='${payment?.tocompany?:''}';
          inn='${payment?.toinn?:''}';
          kpp='${payment?.tokpp?:''}';
        }              
        window.open('${createLink(controller:'company',action:'detail')}'+'/?is_holding=-1&name='+company+'&inn='+inn+'&kpp='+kpp);
      } 
      function openCompany(id){        
        if(id>0)
          window.open('${createLink(controller:'company',action:'detail')}'+'/'+id);          
      }
      function openPers(id){        
        if(id>0)
          window.open('${createLink(controller:'user',action:'persdetail')}'+'/'+id);          
      }
      function processResponse(e){ 
        var sErrorMsg = '';
        ['paycat','fromcompany','tocompany','frombank','tobank','agreementtype_id','agreement_id','tax_type','pers_id','fromkbk','tokbk','fromkbkrazdel_id','tokbkrazdel_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode && e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>Не выбрана "Категория"</li>'; $('paycat').addClassName('red'); break;
              case 2: sErrorMsg+='<li>Не выбран "Тип договора"</li>'; $('agreementtype_id').addClassName('red'); break;
              case 3: sErrorMsg+='<li>Не выбран "№ договора"</li>'; $('agreement_id').addClassName('red'); break;               
              case 4: sErrorMsg+='<li>Не выбрана "Плат. компания"</li>'; $('fromcompany').addClassName('red'); break;
              case 5: sErrorMsg+='<li>Не выбрана "Получ. компания"</li>'; $("tocompany").addClassName('red'); break;
              case 6: sErrorMsg+='<li>Не выбран "Банк плательщика"</li>'; $('frombank').addClassName('red'); break;
              case 7: sErrorMsg+='<li>Не выбран "Банк получателя"</li>'; $("tobank").addClassName('red'); break;
              case 21: sErrorMsg+='<li>Не выбран "Тип налога"</li>'; <g:if test="${(payment?.paytype?:0)==2}">$("fromkbkrazdel_id").addClassName('red');</g:if><g:if test="${(payment?.paytype?:0)==1}">$("tokbkrazdel_id").addClassName('red');</g:if> break;
              case 31: sErrorMsg+='<li>Не выбран "Работник"</li>'; $("pers_id").addClassName('red'); break;  
            }
           });
          $("infolist").up('div').hide();
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
          bSetSaldo=0;
        } else {
          if(bSetSaldo)
            setSaldo();
          else if (bIsExit)
           returnToList();
          else
           location.reload(true);
        }
      }
      function selectAgreement(iValue){
        if(iValue==-1)
          iValue = $('agreementtype_id').value;
        var iCompanyId = 0;
        var sBankBik = '';
        var iContrCompanyId = 0;
        var bNeedCompany = $('is_third').checked;
        if (${payment.paytype!=2?1:0}){
          if (!bNeedCompany) iCompanyId = $('fromcompany_id').value;
          iContrCompanyId = $('tocompany_id').value;
          sBankBik = $('tobankbik').value;
        } else {
          if (!bNeedCompany) iCompanyId = $('tocompany_id').value;
          iContrCompanyId = $('fromcompany_id').value;
          sBankBik = $('frombankbik').value;
        }
        <g:remoteFunction controller='payment' action='agreement' params="'agreementtype_id='+iValue+'&company_id='+iCompanyId+'&bank_id='+sBankBik+'&ctrcompany_id='+iContrCompanyId" update="agreement_span" />
        if (iValue=='3'){
          $('is_dop').up('label').show();
          $('is_fine').up('label').show();
          $('is_com').up('label').hide();
          $('is_dopmain').up('label').hide();
        } else if (iValue=='11'){
          $('is_dop').up('label').show();
          $('is_fine').up('label').hide();
          $('is_com').up('label').hide();
          $('is_dopmain').up('label').hide();
        } else if (iValue=='12'){
          $('is_dop').up('label').hide();
          $('is_fine').up('label').hide();
          $('is_dopmain').up('label').hide();
          $('is_com').up('label').show();
        } else if (iValue=='2'){
          $('is_dop').up('label').hide();
          $('is_fine').up('label').hide();
          $('is_com').up('label').hide();
          $('is_dopmain').up('label').show();
        } else {
          $('is_dop').up('label').hide();
          $('is_fine').up('label').hide();
          $('is_com').up('label').hide();
          $('is_dopmain').up('label').hide();
        }
      }
			function refreshFromCompany(){
			  var sInn='${payment?.frominn}';
			  <g:remoteFunction controller='payment' action='getCompany' params="'inn='+sInn" onSuccess="processFromCompany(e)"/>			            			  
			}
			function processFromCompany(e){
			  if(!e.responseJSON.error){
				  $('fromcompany').value=e.responseJSON.name;
					$('fromcompany_id').value=e.responseJSON.id;
					$('oktmo').value=e.responseJSON.oktmo;
          getPersByCompany(e.responseJSON.id);
				}else{
				  $('fromcompany').value='';
					$('fromcompany_id').value=0;
					$('oktmo').value='';
          getPersByCompany(0);
				}
        getPersCard(0);
			}
			function refreshToCompany(){
			  var sInn='${payment?.toinn}';
			  <g:remoteFunction controller='payment' action='getCompany' params="'inn='+sInn" onSuccess="processToCompany(e)"/>			            			  
			}
			function processToCompany(e){
			  if(!e.responseJSON.error){
				  $('tocompany').value=e.responseJSON.name;
					$('tocompany_id').value=e.responseJSON.id;									
				}else{
				  $('tocompany').value='';
					$('tocompany_id').value=0;					
				}				
			}
			function refreshFromBank(){
			  var sBankId='${payment?.frombankbik}';
				var sInn='${payment?.frominn}';
				var sCoracount='${payment?.fromcorraccount}';
				var sSchet='${payment?.fromaccount}';
				<g:remoteFunction controller='payment' action='getBank' params="'bank_id='+sBankId+'&inn='+sInn+'&coraccount='+sCoracount+'&schet='+sSchet"  onSuccess="processFromBank(e)"/>
			}
			function processFromBank(e){			  				  												  				  				
				$("frombank").value=e.responseJSON.bank.name;
				$("fromcorraccount").value=e.responseJSON.bankaccount.coraccount;
				$("fromaccount").value=e.responseJSON.bankaccount.schet;
			}	
      function refreshToBank(){
			  var sBankId='${payment?.tobankbik}';
				var sInn='${payment?.toinn}';
				var sCoracount='${payment?.tocorraccount}';
				var sSchet='${payment?.toaccount}';
				<g:remoteFunction controller='payment' action='getBank' params="'bank_id='+sBankId+'&inn='+sInn+'&coraccount='+sCoracount+'&schet='+sSchet"  onSuccess="processToBank(e)"/>
			}
			function processToBank(e){			  				  												  				  				
				$("tobank").value=e.responseJSON.bank.name;
				$("tocorraccount").value=e.responseJSON.bankaccount.coraccount;
				$("toaccount").value=e.responseJSON.bankaccount.schet;
			}				   
      function getPers(){
        var sValue=$('toaccount').value;
        var sBankBik=$('tobankbik_pers').value;
        var iFromCompanyId=$("fromcompany_id").value;
        
        if(sValue.length && sBankBik.length && iFromCompanyId>0)
          <g:remoteFunction controller='payment' action='getPers' params="'paccount='+sValue+'&bik='+sBankBik+'&company_id='+iFromCompanyId" onSuccess="processPers(e)"/>;
      }
      function processPers(e){        
        if(e.responseJSON.pers){       
          $("pers_id").value=e.responseJSON.pers.id;
          $("shortname").value=e.responseJSON.pers.shortname;
        }
      }
      function togglePayCat(iVal){     
        $('paycat_agr').hide(); 
        $('fromcompany_div').hide();
        $('tocompany_div').hide();
        $('toinn_div').hide();
        $('tobank_div').hide();
      <g:if test="${payment?.paytype==2}">
        $('from_paycat_budg').hide();
      </g:if><g:elseif test="${payment?.paytype==1}">
        $('to_paycat_budg').hide();
      </g:elseif>
        $('paycat_pers').hide();
        $('paycat_perscard').hide();
        $('paycat_persschet').hide();
        $('paycat_other').hide();
        $('is_persdop').up('label').hide();
        if ($('tasklink')) $('tasklink').hide();

        if(iVal==3){
          setAccount(1);
          setToInn(1);
          setToBankBik(1);          
        }  
        else{ 
          setAccount(0);
          setToInn(0);
          setToBankBik(0);
        }  
          
        switch(iVal){
          case '1': $('paycat_agr').show();
                    $('fromcompany_div').show();
                    $('tocompany_div').show();
                    $('toinn_div').show();
                    $('tobank_div').show();
                    $('paycat_persschet').show();
                    if ($('tasklink')) $('tasklink').show();
                    break;
          case '2':
                  <g:if test="${(payment?.paytype?:0)==1}">
                    $('to_paycat_budg').show();
                    $('fromcompany_div').show();
                  </g:if><g:else>
                    $('from_paycat_budg').show();
                    $('tocompany_div').show();
                    $('toinn_div').show();
                    $('tobank_div').show();
                  </g:else>
                    break;
          case '3': $('fromcompany_div').show();
                    $('paycat_pers').show();
                    $('paycat_perscard').show();
                    $('toinn_div').show();
                    $('tobank_div').show();
                    $('is_persdop').up('label').show();
                    break;
          case '4':
          case '5':
          case '6': $('fromcompany_div').show();
                    $('paycat_other').show();
                    $('tocompany_div').show();
                    $('toinn_div').show();
                    $('tobank_div').show();
                    $('paycat_persschet').show();
                    break;          
        }
      }

      function setAccount(bPers){
        if(bPers){
          $("toaccount_label").update('Лиц. счет:');
          $("toaccount").value='${payment?.toaccount?:''}';
        }else{
          $("toaccount_label").update('Расч. счет банка<br/>получателя:');
          $("toaccount").value='${tobankaccount?.schet?:''}';
        }        
      }
      function setToInn(bPers){
        var sHtml='<label for="toinn" disabled>ИНН получателя:</label>';
        
        if(bPers){
          sHtml+='<input type="text" id="toinn" name="toinn" value="${payment?.toinn}" disabled />';
        }else{
          sHtml+='<span class="input-append">'+
                   '<input type="text" class="nopad normal" id="toinn" name="toinn" value="${payment?.toinn}" disabled />'+
                   '<span class="add-on" onclick="refreshToCompany()"><abbr title="Обновить компанию"><i class="icon-refresh"></i></abbr></span>'+
                 '</span>';
        }        
        $("toinn_div").update(sHtml);           
      }
      function setToBankBik(bPers){
        var sHtml='<label for="tobankbik" disabled>БИК банка получ.:<a href="${g.createLink(controller:'catalog',action:'bankdetail',id:payment?.tobankbik)}" target="_blank"><i class="icon-pencil"></i></a></label>';          
        if(bPers){
          sHtml+='<input type="text" id="tobankbik_pers" name="tobank" value="${payment?.tobankbik?:''}" disabled />';
        }else{
          sHtml+='<span class="input-append">'+
                  '<input type="text" class="nopad normal" id="tobankbik" name="tobankbik" value="${payment?.tobankbik}" disabled />'+
                  '<span class="add-on" onclick="refreshToBank()"><abbr title="Обновить банк"><i class="icon-refresh"></i></abbr></span>'+
                 '</span>';
        }        
        $("tobankbik_div").update(sHtml);            
      } 
      
      function newAgr(){
        var iAgrTypeId=$('agreementtype_id').value;
        var sLink='';
        switch(iAgrTypeId){
          case '1': sLink='${createLink(controller:'agreement',action:'license')}'; break;
          case '2': sLink='${createLink(controller:'agreement',action:'space')}'; break;
          case '3': sLink='${createLink(controller:'agreement',action:'kredit')}'; break;
          case '4': sLink='${createLink(controller:'agreement',action:'lizing')}'; break;          
          case '5': sLink='${createLink(controller:'agreement',action:'agent')}'; break;
          case '6': sLink='${createLink(controller:'agreement',action:'cession')}'; break;
          case '7': sLink='${createLink(controller:'agreement',action:'trade')}'; break;
          case '8': sLink='${createLink(controller:'agreement',action:'service')}'; break;
          case '9': sLink='${createLink(controller:'agreement',action:'smr')}'; break;
          case '10': sLink='${createLink(controller:'agreement',action:'loan')}'; break;
          case '11': sLink='${createLink(controller:'agreement',action:'deposit')}'; break;
          case '12': sLink='${createLink(controller:'agreement',action:'finlizing')}'; break;
        }
        window.open(sLink);
      }
      function getAgentagr(iValue){
        <g:remoteFunction controller='payment' action='agentagrbyclient' params="'client_id='+iValue" update="agentagr_span"/>;
      }
      function setSaldo(){        
        var iId=${payment?.id?:0};
        
        if(iId)
          <g:remoteFunction controller='payment' action='setSaldo' params="'id='+iId" onSuccess="location.reload(true)" />;
      }
      function getSubclientsList(sClId){
        <g:remoteFunction controller='payment' action='subclientslist' params="'client_id='+sClId" update="subclientslist"/>
      }
      function toggleDisabled(){
        if(document.getElementById('is_error').checked)
          jQuery('#paymentDetailForm .maindata').each(function(){ this.disabled=false; });
        else
          jQuery('#paymentDetailForm .maindata').each(function(){ this.disabled=true; });
      }
      function getPersByCompany(iCompanyId){
        <g:remoteFunction controller='payment' action='persbycompany' params="'id='+iCompanyId" update="pers_span"/>;
      }
      function getPersCard(iValue){
        <g:remoteFunction controller='payment' action='getcardtypebypers' params="'id='+iValue" update="card_span"/>;
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
      .tabs a:hover,.tabs a.active,.tabs a.active i{color:#000}
    </style>    
  </head>
  <body onload="init();">
    <h3 class="fleft">Платеж № ${payment.id1c} (${payment.id})<g:if test="${payment.is_error}">&nbsp;(корр.)</g:if></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку платежей</a>
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
    <g:formRemote name="paymentDetailForm" url="${[action:'processPayment',id:payment?.id?:0]}" method="post" onSuccess="processResponse(e)"> 
      <label for="paydate" disabled>Дата платежа:</label>
      <input type="text" id="paydate" value="${String.format('%td.%<tm.%<tY',payment.paydate)}" disabled />
			<label for="platnumber" disabled>№ платежки:</label>
      <input type="text" id="platnumber" value="${payment.platnumber}" disabled />
			<label for="inputdate" disabled>Дата ввода:</label>
      <input type="text" id="inputdate" value="${String.format('%td.%<tm.%<tY',payment.inputdate)}" disabled />
      <label for="moddate" disabled>Дата модиф.:</label>
      <input type="text" id="moddate" value="${String.format('%td.%<tm.%<tY',payment.moddate)}" disabled />			
			<label for="modstatus" disabled>Статус:</label>
      <g:select name="modstatus" value="${payment?.modstatus}" from="['неидентифицированный','идентифицированный']" keys="12" noSelection="${['-100':'не выбран']}" disabled="true" />
      <label for="finstatus" disabled>Фин. статус:</label>
      <g:select name="finstatus" value="${payment?.finstatus?:0}" from="['новый','действующий']" keys="[0,1]" disabled="true" />
      <label for="ptype" disabled>Вид:</label>
      <input type="text" id="ptype" value="${payment.ptype}" disabled />
			<label for="is_internal" disabled>Признак:</label>
      <g:select name="is_internal" value="${payment?.is_internal?:0}" from="['внешний','внутренний']" keys="[0,1]" disabled="true" />
      <label for="paytype" disabled>Тип:</label>
      <g:select name="paytype" value="${payment?.paytype?:0}" from="['исходящий','входящий']" keys="[1,2]" disabled="true"/>			
			<label for="summands" disabled>Сумма НДС:</label>
      <input type="text" id="summa" value="${number(value:payment.summands,fdigs:2)}" disabled />
			<label for="summa" disabled>Сумма:</label>
      <input type="text" id="summa" value="${number(value:payment.summa,fdigs:2)}" disabled />
			<label for="summaold" disabled>Прим. сумма:</label>
      <input type="text" id="summaold" value="${number(value:payment.summaold,fdigs:2)}" disabled />
      <label for="destination" style="width:185px">Назначение платежа:</label>
      <label for="is_nds" class="auto" disabled>
        <input type="checkbox" id="is_nds" value="1" disabled <g:if test="${payment.summands>0}">checked</g:if> />
        Наличие НДС
      </label>
      <label class="auto" for="is_persdop" style="${!(payment?.paycat in [3])?'display:none':''}">
        <input type="checkbox" id="is_persdop" name="is_persdop" value="1" <g:if test="${payment?.is_dop}">checked</g:if>/>
        Доп. платеж
      </label>
      <label class="auto" for="is_bankmoney">
        <input type="checkbox" id="is_bankmoney" name="is_bankmoney" value="1" <g:if test="${payment.is_bankmoney}">checked</g:if> />
        СС банка
      </label>
      <label for="is_error" class="auto" <g:if test="${payment.payrequest_id>0}">disabled</g:if>>
        <input type="checkbox" id="is_error" name="is_error" value="1" onclick="toggleDisabled()" <g:if test="${payment.payrequest_id>0}">disabled</g:if>/>
        Корректировать источник
      </label>
      <g:textArea name="destination" value="${payment.destination}"/>
      <label for="paycat" <g:if test="${payment.payrequest_id>0}">disabled</g:if>>Категория:</label>
      <g:select name="paycat" value="${payment?.paycat?:0}" from="${paycat}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}" disabled="${payment?.payrequest_id>0?'true':'false'}" onchange="togglePayCat(this.value)"/>
      <label for="payrequest" disabled>Факт. платеж:<g:if test="${payment.payrequest_id>0}"><a href="${g.createLink(controller:'payment',action:'payrequestdetail',id:payment.payrequest_id)}" target="_blank"><i class="icon-pencil"></i></a></g:if></label>
      <input type="text" id="payrequest" value="<g:if test='${payrequest}'>${'№'+payrequest.id+' '+(payrequest.paycat==Payrequest.PAY_CAT_AGR?'договорной':payrequest.paycat==Payrequest.PAY_CAT_BUDG?'бюджетный':payrequest.paycat==Payrequest.PAY_CAT_PERS?'персональный':payrequest.paycat==Payrequest.PAY_CAT_OTHER?'другой':payrequest.paycat==Payrequest.PAY_CAT_BANK?'банковский':payrequest.paycat==Payrequest.PAY_CAT_ORDER?'счета':'')}</g:if><g:else></g:else>" disabled />               
			<hr class="admin">								

      <div style="width:100%">
        <div id="paycat_agr" <g:if test="${payment?.paycat!=Payment.PAY_CAT_AGR}">style="display:none"</g:if>>            
          <label for="agreementtype_id">Тип договора:</label>
          <g:select name="agreementtype_id" value="${payment?.agreementtype_id}" from="${agrtypes}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}" onchange="selectAgreement(this.value)"/>
          <label class="auto" for="is_com" style="${!(payment?.agreementtype_id in [12])?'display:none':''}">
            <input type="checkbox" id="is_com" name="is_com" value="1" <g:if test="${payment?.is_dop}">checked</g:if>/>
            Возврат комиссии
          </label>
          <label class="auto" for="is_dopmain" style="${!(payment?.agreementtype_id in [2])?'display:none':''}">
            <input type="checkbox" id="is_dopmain" name="is_dopmain" value="1" <g:if test="${payment?.is_dop}">checked</g:if>/>
            Доп. платежи
          </label>
          <label class="auto" for="is_dop" style="${!(payment?.agreementtype_id in [3,11])?'display:none':''}">
            <input type="checkbox" id="is_dop" name="is_dop" value="1" <g:if test="${payment?.is_dop}">checked</g:if>/>
            Проценты
          </label>
          <label class="auto" for="is_fine" style="${payment?.agreementtype_id!=3?'display:none':''}">
            <input type="checkbox" id="is_fine" name="is_fine" value="1" <g:if test="${payment?.is_fine}">checked</g:if>/>
            Пеня
          </label>
          <label class="auto" for="is_third">
            <input type="checkbox" id="is_third" name="is_third" value="1" <g:if test="${payment?.is_third}">checked</g:if> onclick="selectAgreement(-1)"/>
            За третье лицо
          </label><br/>
          <label for="agreement_id">№ договора:</label>
          <span id="agreement_span" class="input-append"> 
            <g:select name="agreement_id" value="${payment?.agreement_id}" from="${agr}" optionKey="id" noSelection="${['0':'не выбран']}" style="${payment?.agreementtype_id?'width:628px':''}"/>
            <g:if test="${payment?.agreementtype_id}">
              <span class="add-on" onclick="newAgr()"><abbr title="Добавить договор"><i class="icon-plus"></i></abbr></span> 
              <span class="add-on" onclick="selectAgreement(-1)"><abbr title="Обновить договор"><i class="icon-refresh"></i></abbr></span>          
            </g:if>  
          </span>
          <hr class="admin">
        </div>
        <div id="paycat_other" <g:if test="${payment?.paycat!=Payment.PAY_CAT_OTHER}">style="display:none"</g:if>>
          <label for="comment">Комментарий администратора:</label>
          <g:textArea name="comment" value="${payment?.comment}" />      
          <hr class="admin">
        </div>
      </div>

			<div style="width:47%;float:left;">
      <label for="fromcompany1" disabled>Плательщик:</label>          
      <input type="text" class="maindata" id="fromcompany1" name="fromcompany_main" value="${payment?.fromcompany}" disabled/>                              
      <label for="frominn1" disabled>ИНН плательщика:</label>
      <input type="text" class="maindata" id="frominn1" name="frominn_main" value="${payment?.frominn}" disabled />
      <label for="frombank1" disabled>Банк плат.:</label>
      <input type="text" class="maindata" id="frombank1" name="frombank_main" value="${payment?.frombank}" disabled />
      <label for="frombankbik1" disabled>БИК банка плат.:</label>
      <input type="text" class="maindata" id="frombankbik1" name="frombankbik_main" value="${payment?.frombankbik}" disabled />
      <label for="fromcorraccount1" disabled>Кор счет банка<br/>плательщика:</label>
      <input type="text" class="maindata" id="fromcorraccount1" name="fromcorraccount_main" value="${payment?.fromcorraccount}" disabled />
		 	<label for="fromaccount1" disabled>Расч. счет банка<br/>плательщика:</label>
		  <input type="text" class="maindata" id="fromaccount1" name="fromaccount_main" value="${payment?.fromaccount}" disabled />
      <label for="oktmo1" disabled>ОКТМО плат.:</label>
      <input type="text" class="maindata" id="oktmo1" name="oktmo_main" value="${payment?.oktmo}" disabled />
			</div>      
      <div style="width:53%;float:right;">        
        <div id="fromcompany_div" <g:if test="${!payment?.paycat || (payment?.paycat==Payment.PAY_CAT_BUDG && payment?.paytype==2)}">style="display:none"</g:if>>
          <label for="fromcompany" disabled>Плат. компания:<a href="javascript:void(0)" onclick="openCompany($('fromcompany_id').value)"><i class="icon-pencil"></i></a></label>    
          <span class="input-append">
            <input type="text" class="nopad normal <g:if test='${!payment?.fromcompany_id}'>red</g:if>" id="fromcompany" name="fromcompany" value="${fromcompany?.name}" disabled />
            <span class="add-on" onclick="newCompany('from')"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
          </span>
          <input type="hidden" id="fromcompany_id" name="fromcompany_id" value="${fromcompany?.id?:0}"/>        
          <label for="frominn" disabled>ИНН плательщика:</label>
          <span class="input-append">
            <input type="text" class="nopad normal" id="frominn" name="frominn" value="${payment?.frominn}" disabled />
            <span class="add-on" onclick="refreshFromCompany()"><abbr title="Обновить компанию"><i class="icon-refresh"></i></abbr></span>
          </span>					
          <label for="frombank" disabled>Банк плат.:</label>				
          <input type="text" id="frombank" name="frombank" value="${frombank?.name?:''}" disabled />												
          <label for="frombankbik" disabled>БИК банка плат.:<a href="${g.createLink(controller:'catalog',action:'bankdetail',id:payment?.frombankbik)}" target="_blank"><i class="icon-pencil"></i></a></label>
          <span class="input-append">
            <input type="text" class="nopad normal" id="frombankbik" name="frombankbik" value="${payment?.frombankbik}" disabled />
            <span class="add-on" onclick="refreshFromBank()"><abbr title="Обновить банк"><i class="icon-refresh"></i></abbr></span>
          </span>						
          <label for="fromcorraccount" disabled>Кор счет банка<br/>плательщика:</label>
          <input type="text" id="fromcorraccount" name="fromcorraccount" value="${frombankaccount?.coraccount?:''}" disabled />
          <label for="fromaccount" disabled>Расч. счет банка<br/>плательщика:</label>
          <input type="text" id="fromaccount" name="fromaccount" value="${frombankaccount?.schet?:''}" disabled />				
          <label for="oktmo" disabled>ОКТМО плат.:</label>
          <input type="text" id="oktmo" name="oktmo" value="${fromcompany?.oktmo?:''}" disabled />
			  </div>
        <g:if test="${(payment?.paytype?:0)==2}">
          <div id="from_paycat_budg" <g:if test="${!(payment?.paycat==Payment.PAY_CAT_BUDG && payment?.paytype==2)}">style="display:none"</g:if>>
            <label for="kbk" disabled>КБК:</label>          
            <input type="text" id="fromkbk" name="kbk" value="${payment?.kbk}" disabled />                          
            <label for="fromkbkrazdel_id">Тип налога:</label>
            <g:select id="fromkbkrazdel_id" name="kbkrazdel_id" value="${payment.kbkrazdel_id}" from="${kbkrazdel}" optionKey="id" optionValue="name" noSelection="${[0:'не выбран']}" />                        
            <label for="platperiod" disabled>Налог. период:</label>
            <input type="text" id="platperiod" name="platperiod" value="${payment?.platperiod}" disabled />      
          </div>
        </g:if>
      </div>
      <div class="clear" style="padding-top:10px"></div>
      <hr class="admin">
			<div style="width:47%;float:left;">        
				<label for="tocompany1" disabled>Получатель:</label>       					
			  <input type="text" class="maindata" id="tocompany1" name="tocompany_main" value="${payment?.tocompany}" disabled/>						  				     													
				<label for="toinn1" disabled>ИНН получателя:</label>
				<input type="text" class="maindata" id="toinn1" name="toinn_main" value="${payment?.toinn}" disabled />
				<label for="tobank1" disabled>Банк получателя:</label>
				<input type="text" class="maindata" id="tobank1" name="tobank_main" value="${payment?.tobank}" disabled />      
				<label for="tobankbik1" disabled>БИК банка получ.:</label>
				<input type="text" class="maindata" id="tobankbik1" name="tobankbik_main" value="${payment?.tobankbik}" disabled />
				<label for="tocorraccount1" disabled>Кор. счет банка<br/>получателя:</label>
				<input type="text" class="maindata" id="tocorraccount1" name="tocorraccount_main" value="${payment?.tocorraccount}" disabled />
        <label for="toaccount1" disabled><g:if test="${payment?.paycat==Payment.PAY_CAT_PERS}">Лиц. счет</g:if><g:else>Расч. счет банка<br/>получателя:</g:else></label>
				<input type="text" class="maindata" id="toaccount1" name="toaccount_main" value="${payment?.toaccount}" disabled />				
			</div>
      <div style="width:53%;float:right;">
        <div id="paycat_pers" <g:if test="${payment?.paycat!=Payment.PAY_CAT_PERS}">style="display:none"</g:if>>							
          <label for="shortname" disabled>Работник:<a href="javascript:void(0)" onclick="openPers($('pers_id').value)"><i class="icon-pencil"></i></a></label>
          <span id="pers_span"><g:select name="pers_id" value="${pers?.id?:0}" from="${perslist}" optionKey="id" optionValue="shortname" noSelection="${['0':'не выбран']}" onchange="getPersCard(this.value)"/></span>
        </div>
        <div id="tocompany_div" <g:if test='${!(payment?.paycat==Payment.PAY_CAT_AGR || payment?.paycat==Payment.PAY_CAT_OTHER || payment?.paycat==Payment.PAY_CAT_BANK || payment?.paycat==Payment.PAY_CAT_ORDER) && !(payment?.paycat==Payment.PAY_CAT_BUDG && payment?.paytype==2)}'>style="display:none"</g:if>>
          <label for="tocompany" disabled>Получ. компания:<a href="javascript:void(0)" onclick="openCompany($('tocompany_id').value)"><i class="icon-pencil"></i></a></label>       
          <span class="input-append">
            <input type="text" class="nopad normal <g:if test='${!payment?.tocompany_id}'>red</g:if>" id="tocompany" name="tocompany" value="${tocompany?.name}" disabled />
            <span class="add-on" onclick="newCompany('to')"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
          </span>
          <input type="hidden" id="tocompany_id" name="tocompany_id" value="${tocompany?.id?:0}"/>           
        </div>
        <div id="toinn_div" <g:if test='${!(payment?.paycat==Payment.PAY_CAT_PERS || payment?.paycat==Payment.PAY_CAT_AGR || payment?.paycat==Payment.PAY_CAT_OTHER || payment?.paycat==Payment.PAY_CAT_BANK || payment?.paycat==Payment.PAY_CAT_ORDER) && !(payment?.paycat==Payment.PAY_CAT_BUDG && payment?.paytype==2)}'>style="display:none"</g:if>>         
          <label for="toinn" disabled>ИНН получателя:</label>                    
          <g:if test="${payment?.paycat==Payment.PAY_CAT_PERS}">
            <input type="text" id="toinn" name="toinn" value="${payment?.toinn}" disabled />
          </g:if>
          <g:else>
            <span class="input-append">
              <input type="text" class="nopad normal" id="toinn" name="toinn" value="${payment?.toinn}" disabled />
              <span class="add-on" onclick="refreshToCompany()"><abbr title="Обновить компанию"><i class="icon-refresh"></i></abbr></span>
            </span>
          </g:else>          
        </div>  
        <div id="tobank_div" <g:if test='${!(payment?.paycat==Payment.PAY_CAT_PERS || payment?.paycat==Payment.PAY_CAT_AGR || payment?.paycat==Payment.PAY_CAT_OTHER || payment?.paycat==Payment.PAY_CAT_BANK || payment?.paycat==Payment.PAY_CAT_ORDER) && !(payment?.paycat==Payment.PAY_CAT_BUDG && payment?.paytype==2)}'>style="display:none"</g:if>>
          <label for="tobank" disabled>Банк получателя:</label>
          <input type="text" id="tobank" name="tobank" value="${tobank?.name?:''}" disabled />					
          <div id="tobankbik_div">
            <label for="tobankbik" disabled>БИК банка получ.:<a href="${g.createLink(controller:'catalog',action:'bankdetail',id:payment?.tobankbik)}" target="_blank"><i class="icon-pencil"></i></a></label>          
            <g:if test="${payment?.paycat==Payment.PAY_CAT_PERS}">
              <input type="text" id="tobankbik_pers" name="tobank" value="${payment?.tobankbik?:''}" disabled />
            </g:if>          
            <g:else>
              <span class="input-append">
                <input type="text" class="nopad normal" id="tobankbik" name="tobankbik" value="${payment?.tobankbik}" disabled />
                <span class="add-on" onclick="refreshToBank()"><abbr title="Обновить банк"><i class="icon-refresh"></i></abbr></span>
              </span>				
            </g:else>
          </div>
          <label for="tocorraccount" disabled>Кор. счет банка<br/>получателя:</label>
          <input type="text" id="tocorraccount" name="tocorraccount" value="${tobankaccount?.coraccount?:''}" disabled />
          <div id="paycat_persschet" <g:if test="${payment?.paycat==Payment.PAY_CAT_PERS}">style="display:none"</g:if>>
            <label for="toaccount" id="toaccount_label" disabled>Расч. счет банка<br/>получателя:</label>
            <input type="text" id="toaccount" name="toaccount" value="${tobankaccount?.schet}" disabled />
          </div>
        </div>
        <div id="paycat_perscard" <g:if test="${payment?.paycat!=Payment.PAY_CAT_PERS}">style="display:none"</g:if>>
          <label for="card">Карта:</label>
          <span id="card_span"><g:select name="card" from="${card}" value="${cardvalue}" optionKey="id" optionValue="name" noSelection="${['-1':'не выбрана']}"/></span>
        </div>
      <g:if test="${(payment?.paytype?:0)==1}">        
        <div id="to_paycat_budg" <g:if test="${!(payment?.paycat==Payment.PAY_CAT_BUDG && payment?.paytype==1)}">style="display:none"</g:if>>
          <label for="tokbk" disabled>КБК:</label>         
          <input type="text" id="tokbk" name="kbk" value="${payment?.kbk}" disabled />                    
          <label for="tokbkrazdel_id">Тип налога:</label>
          <g:select id="tokbkrazdel_id" name="kbkrazdel_id" value="${payment.kbkrazdel_id}" from="${kbkrazdel}" optionKey="id" optionValue="name" noSelection="${[0:'не выбран']}" />              
          <label for="platperiod" disabled>Налог. период:</label>
          <input type="text" id="platperiod" name="platperiod" value="${payment?.platperiod}" disabled />      
        </div>                
      </g:if>
      </div>
      <div class="clear" style="padding-top:10px"></div>
      <hr class="admin">
      <g:if test="${iscantag}">
        <label for="client_id">Клиент:</label>
        <g:select name="client_id" from="${clients}" value="${payment?.client_id}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}" onchange="getSubclientsList(this.value)"/>
        <label for="subclient_id">Подклиент:</label>
        <span id="subclientslist"><g:select name="subclient_id" from="${subclients}" value="${payment?.subclient_id}" optionKey="id" optionValue="name" noSelection="${['0':'нет']}"/></span>
        <label for="project_id">Проект:</label>
        <g:select name="project_id" value="${payment?.project_id?:defproject_id}" from="${project}" optionKey="id" optionValue="name" />
        <span id="carsection" style="${!(payment?.expensetype_id in expcar_ids)?'display:none':''}"><label for="car_id">Машина:</label>
        <g:select name="car_id" value="${payment?.car_id}" from="${cars}" optionKey="id" optionValue="name" noSelection="${['0':'не выбрана']}" /></span>
        <br/><label for="expensetype_id">Доходы-расходы:</label>
        <g:select name="expensetype_id" class="fullline" value="${payment?.expensetype_id?:0}" from="${expensetype}" optionKey="id" noSelection="${['0':'не выбран']}" onchange="showCars(this.value)"/>
        <label for="expensetype_name">Доходы-расходы:</label>
        <input type="text" class="fullline" id="expensetype_name" value=""/>
        <label for="tagcomment">Комментарий:</label>
        <input type="text" class="fullline" id="tagcomment" name="tagcomment" value="${payment?.tagcomment?:''}"/>
        <hr class="admin">
      </g:if>
      <div class="fright" id="btns">
        <g:if test="${payrequest?.file_id}">
          <a class="button" href="${createLink(controller:'payment',action:'showscan',id:payrequest.file_id,params:[code:Tools.generateModeParam(payrequest.file_id)])}" target="_blank">Просмотреть скан платежа &nbsp;<i class="icon-angle-right icon-large"></i></a> 
        </g:if>
        <g:if test="${!ishavetask}">
          <a class="button" id="tasklink" style="display:none" href="${createLink(controller:'task',action:'taskdetail',params:[payment_id:payment.id])}" target="_blank">Задание на договор &nbsp;<i class="icon-angle-right icon-large"></i></a>
        </g:if>
        <g:if test="${iscancreate}">
          <g:remoteLink class="button" url="${[controller:'payment',action:'createpayrequest',id:payment.id]}" onSuccess="location.reload(true)">Сформировать фактический платеж &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink>
        </g:if><g:elseif test="${iscandelete}">
          <g:remoteLink class="button" url="${[controller:'payment',action:'deletegenpayrequest',id:payment.id]}" onSuccess="location.reload(true)">Удалить фактический платеж &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink>
        </g:elseif>
        <g:if test="${user?.group?.is_payedit}">
          <input type="button" value="Сохранить и выйти" onclick="submitExit()"/>
          <input type="submit" id="submit_button" value="Сохранить"/>
        </g:if>
        <g:if test="${payment?.modstatus==2 && !payment?.finstatus}">
          <input type="button" id="set_saldo_button" value="Перерасчет" onclick="setSaldo();"/>
        </g:if>
        <input type="reset" class="spacing" value="Сброс" />
      </div>
    </g:formRemote>     
    <div class="clear"></div>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку платежей</a>    
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:'payment',action:'index',params:[fromDetails:1]]}">
    </g:form>    
  </body>
</html>
