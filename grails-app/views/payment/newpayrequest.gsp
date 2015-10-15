<html>
  <head>
    <title>Prisma: Платежи - Новый фактический платеж</title>
    <meta name="layout" content="main" /> 
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript>
      var iTaxoption_id = 0;
      var isTagOpen = true;
      var expcar_ids = ${expcar_ids};
      function returnToList(){
        $("returnToListForm").submit();
      }
      function showCars(iExpId){
        if(expcar_ids.indexOf(parseInt(iExpId))>-1) $('carsection').show();
        else $('carsection').hide();
      }
      function togglePaytype(iValue){
        <g:remoteFunction controller='payment' action='paycat' params="'id='+iValue" update="paycat_span" />;
      }
      function togglePaycat(iVal){
        var iPaytype = $('paytype').value;
        <g:remoteFunction controller='payment' action='payrequestdata' params="'cat='+iVal+'&type='+iPaytype" update="datadiv" before="jQuery('#datadiv').slideUp()" onComplete="if(iVal!=0){ jQuery('#datadiv').slideDown();}"/>;
      }
      function openCompany(){
        var iCompany_id = $('tocompany_id').value;
        if(iCompany_id)
          window.open('${createLink(controller:'company',action:'detail')}'+'/'+iCompany_id);
      }
      function newCompany(){
        window.open('${createLink(controller:'company',action:'detail')}');
      }
      function openPers(id){
        if(id>0)
          window.open('${createLink(controller:'user',action:'persdetail')}'+'/'+id);
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['paycat','paytype','summa','destination','pers_id','card','fromtax_id','totax_id','expensetype_id','agreementtype_id','agreement_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['paydate','fromcompany','tocompany','tobank'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('k-error-colored');
        });
        $('is_task').value=0;
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип"])}</li>'; $("paytype").addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата платежа"])}</li>'; $("paydate").up('span').addClassName('k-error-colored'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Категория"])}</li>'; $("paycat").addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Плат. компания"])}</li>'; $("fromcompany").up('span').addClassName('k-error-colored'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Получ. компания"])}</li>'; $("tocompany").up('span').addClassName('k-error-colored'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк получателя"])}</li>'; $("tobank").up('span').addClassName('k-error-colored'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Работник"])}</li>'; $("pers_id").addClassName('red'); break;
              case 8: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Карта"])}</li>'; $("card").addClassName('red'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип налога"])}</li>'; $("totax_id").addClassName('red'); break;
              case 10: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип налога"])}</li>'; $("fromtax_id").addClassName('red'); break;
              case 11: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $("summa").addClassName('red'); break;
              case 12: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Назначение"])}</li>'; $("destination").addClassName('red'); break;
              case 13: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Доходы-расходы"])}</li>'; $("expensetype_id").addClassName('red'); break;
              case 14: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип договора"])}</li>'; $("agreementtype_id").addClassName('red'); break;
              case 15: sErrorMsg+='<li>${message(code:"error.blank.message",args:["№ договора"])}</li>'; $("agreement_id").addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else {
          returnToList();
        }
      }
      /*function setSummaNds(iSumma){
        var nds=${nds?:0};
        if(iTaxoption_id>0) $("summands").value = (nds * iSumma / (100 + nds)).toFixed(2);
        else $("summands").value = 0;
      }*/
      function selectAgreement(iValue,iType){
        if(iValue==-1)
          iValue = $('agreementtype_id').value;
        var iCompanyId
        var iContrCompanyId = 0;
        if(iType){
          iCompanyId = $('fromcompany_id').value;
          iContrCompanyId = $('tocompany_id').value;
        } else {
          iCompanyId = $('tocompany_id').value;
          iContrCompanyId = $('fromcompany_id').value;
        }
        <g:remoteFunction controller='payment' action='agreement' params="'agreementtype_id='+iValue+'&company_id='+iCompanyId+'&ctrcompany_id='+iContrCompanyId+'&short=1'" update="agreement_span" />
        if ($('is_dop')){
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
      }
      function getPersByCompany(iCompanyId){
        <g:remoteFunction controller='payment' action='persbycompany' params="'id='+iCompanyId" update="pers_span"/>;
      }
      function getPersCard(iValue){
        <g:remoteFunction controller='payment' action='getcardtypebypers' params="'id='+iValue" update="card_span"/>;
      }
      function getBankByCompany(){
        var iCompany_id = $('tocompany_id').value;
        if(iCompany_id)
          <g:remoteFunction controller='payment' action='getbankbycompany' params="'company_id='+iCompany_id" update="tobank_span"/>;
      }
      function getBankByFromCompany(){
        var iCompany_id = $('fromcompany_id').value;
        if(iCompany_id)
          <g:remoteFunction controller='payment' action='getbankbycompany' params="'company_id='+iCompany_id+'&type=1'" update="frombank_span"/>;
      }
      function toggletagsection(){
        var tagexpandlink = document.getElementById('tagexpandlink');
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
      function init(){
        new Autocomplete('expensetype_name', {
          serviceUrl:'${resource(dir:"autocomplete",file:"expensetype_autocomplete")}',
          width : 704,
          onSelect: function(value, data){
            $('expensetype_id').value = data;
            showCars(data);
          }
        });
        jQuery("#paydate").mask("99.99.9999",{placeholder:" "});
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
    </style>
  </head>
  <body onload="init();">
    <h3 class="fleft">Новый фактический платеж</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку фактических платежей</a>
    <div class="clear"></div>

    <div class="error-box" style="display:none">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>

    <g:formRemote name="paymentDetailForm" url="${[action:'addpayrequest']}" method="post" onSuccess="processResponse(e)">
      <label for="paytype">Тип:</label>
      <g:select name="paytype" from="['исходящий','внутренний']" keys="13" noSelection="${['0':'не выбран']}" onchange="togglePaytype(this.value)"/>
      <span id="paycat_span"><label for="paycat">Категория:</label>
      <g:select name="paycat" from="['не выбран']" keys="0"/></span>
      <label for="paydate">Дата платежа:</label>
      <g:datepicker class="normal nopad" name="paydate" value="${new Date()}"/><br/>
      <label for="summa">Сумма:</label>
      <input type="text" id="summa" name="summa" value="" onblur="/*setSummaNds(this.value)*/"/>
      <label for="is_nds" class="auto">
        <input type="checkbox" id="is_nds" name="is_nds" value="1" checked />
        Наличие НДС
      </label>      
      <div id="datadiv" style="display:none"></div>
      <hr class="admin" />

      <label for="destination">Назначение платежа:</label>
      <g:textArea name="destination" value="" />

      <div class="clear" style="padding-top:10px"></div>
      <hr class="admin" style="width:780px;float:left"/><a id="tagexpandlink" style="text-decoration:none" href="javascript:void(0)">&nbsp;&nbsp;Тегировать&nbsp;<i class="icon-collapse-top"></i></a><hr class="admin" style="width:70px;float:right"/>
      <div id="tagsection" style="width:960px">
        <label for="project_id">Проект:</label>
        <g:select name="project_id" value="${payrequest?.project_id?:defproject_id}" from="${project}" optionKey="id" optionValue="name" />
        <span id="carsection" style="${!(payrequest?.expensetype_id in expcar_ids)?'display:none':''}"><label for="car_id">Машина:</label>
        <g:select name="car_id" value="${payrequest?.car_id}" from="${cars}" optionKey="id" optionValue="name" noSelection="${['0':'не выбрана']}" /></span>
        <br/><label for="expensetype_id">Доходы-расходы:</label>
        <g:select name="expensetype_id" class="fullline" value="${payrequest?.expensetype_id?:0}" from="${expensetype}" optionKey="id" noSelection="${['0':'не выбран']}" onchange="showCars(this.value)"/>
        <label for="expensetype_name">Доходы-расходы:</label>
        <input type="text" class="fullline" id="expensetype_name" value=""/>
        <label for="tagcomment">Комментарий:</label>
        <input type="text" id="tagcomment" name="tagcomment" value="" class="fullline" />

        <hr class="admin" />
      </div>
      <div class="clear"></div>
      <div class="fright" id="btns">
        <input type="button" class="button" value="Создать" onclick="$('is_task').value=1;$('submit_button').click();"/>
        <input type="submit" id="submit_button" class="button" value="Сохранить" />
      </div>
      <input type="hidden" id="is_task" name="is_task" value="0"/>
    </g:formRemote>
    <div class="clear"></div>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку фактических платежей</a>
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:'payment',action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
