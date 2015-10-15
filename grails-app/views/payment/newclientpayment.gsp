<html>
  <head>
    <title>Prisma: Платежи - Добавление клиентского платежа</title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript>
      var t = '${createLink(controller:'task',action:'index',params:[taskobject:3])}';
      var iTaxoption_id = 0;
      var isTagOpen = true;
      function returnToList(){
        $("returnToListForm").submit();
      }
      function togglePaytype(iValue){
        if(iValue==1){
          $('is_clientcommission').up('label').show();
          $('is_midcommission').up('label').show();
        } else {
          $('is_clientcommission').up('label').hide();
          $('is_midcommission').up('label').hide();
        }
        if(iValue==2||iValue==11){
          $('is_urgent').up('label').hide();
          $('pay_button').hide();
          $('detail_button').hide();
        } else {
          $('is_urgent').up('label').show();
          $('pay_button').show();
          $('detail_button').show();
        }
        jQuery("#paycat option[value!=4]").each(function(el){
          var el = jQuery(this);
          if(el.parent().is( "span" )) el.unwrap();
        });
        $("is_clientcommission").checked = false;
        $("is_midcommission").checked = false;
        $("is_urgent").checked = false;
        if(iValue==3||iValue==11){
          jQuery('#paycat option[value!=4]').wrap("<span>");
          togglePaycat(4);
        } else {
          togglePaycat(0);
        }
        $('paycat').selectedIndex = 0;
      }
      function togglePaycat(iVal){
        var iPaytype = $('paytype').value;
        <g:remoteFunction controller='payment' action='clientpaymentdata' params="'cat='+iVal+'&type='+iPaytype" update="datadiv" before="jQuery('#datadiv').slideUp()" onComplete="if(iVal!=0){ jQuery('#datadiv').slideDown();}"/>;
        if((iPaytype==1&&iVal==6)||iPaytype==3){
          $('expensetype_tagsection').show();
        }else{
          $('expensetype_tagsection').hide();
          $('expensetype_id').selectedIndex = 0;
          $('expensetype_name').value = '';
        }
      }
      function openCompany(sName){
        sName = sName || 'to'
        var iCompany_id
        if (sName=='to') iCompany_id = $('tocompany_id').value;
        else iCompany_id = $('fromcompany_id').value;
        if(iCompany_id)
          window.open('${createLink(controller:'company',action:'detail')}'+'/'+iCompany_id);
      }
      function newCompany(){
        window.open('${createLink(controller:'company',action:'detail')}');
      }
      function getBankByCompany(sName){
        sName = sName || 'to'
        var iCompany_id
        if (sName=='to'){
          iCompany_id = $('tocompany_id').value;
          if(iCompany_id)
            <g:remoteFunction controller='payment' action='getbankbycompany' params="'company_id='+iCompany_id" update="tobank_span"/>;
        } else {
          iCompany_id = $('fromcompany_id').value;
          if(iCompany_id)
            <g:remoteFunction controller='payment' action='getbankbycompany' params="'company_id='+iCompany_id+'&type=1'" update="frombank_span"/>;
        }
      }
      function getbankaccountbycompany(){
        var iCompany_id = $('fromcompany_id').value;
        if(iCompany_id)
          <g:remoteFunction controller='payment' action='getbankaccountbycompany' params="'company_id='+iCompany_id" update="frombank_span"/>;
      }
      function getBankaccount(iValue){
        var summa = $('summa').value;
        <g:remoteFunction controller='payment' action='bankaccountdata' params="'id='+iValue+'&summa='+summa" update="bankaccountdata_div" />
      }
      function init(){
        new Autocomplete('expensetype_name', {
          serviceUrl:'${resource(dir:"autocomplete",file:"expensetype_autocomplete")}',
          width : 704,
          onSelect: function(value, data){
            $('expensetype_id').value = data;
          }
        });
        jQuery("#paydate").mask("99.99.9999",{placeholder:" "});
      }
      function selectAgreement(iValue){
        if(iValue==-1)
          iValue = $('agreementtype_id').value;
        var iCompanyId
        var iContrCompanyId = 0;
        if($('paytype').value!=2){
          iCompanyId = $('fromcompany_id').value;
          iContrCompanyId = $('tocompany_id').value;
        } else {
          iCompanyId = $('tocompany_id').value;
          iContrCompanyId = $('fromcompany_id').value;
        }
        <g:remoteFunction controller='payment' action='agreement' params="'agreementtype_id='+iValue+'&company_id='+iCompanyId+'&ctrcompany_id='+iContrCompanyId+'&short=1'" update="agreement_span" />
        if ($('is_dop')){
          $('is_dop').up('label').hide();
          $('is_fine').up('label').hide();
          $('is_com').up('label').hide();
          $('is_dopmain').up('label').hide();
          if (iValue=='3'){
            $('is_dop').up('label').show();
            $('is_fine').up('label').show();
          } else if (iValue=='11'){
            $('is_dop').up('label').show();
          } else if (iValue=='12'){
            $('is_com').up('label').show();
          } else if (iValue=='2'){
            $('is_dopmain').up('label').show();
          }
        }
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
      function getSubclientsList(sClId){
        <g:remoteFunction controller='payment' action='subclientslist' params="'client_id='+sClId" update="subclientslist"/>
      }
      function togglesubcomsection(sClId){
        return false;
      }
      function submitForm(iAccept,iDetails){
        $('is_accept').value = iAccept
        $('is_detail').value = iDetails
        $('clientpaymentDetailForm').submit();
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
    </style>
  </head>
  <body onload="init();">
    <h3 class="fleft">Новый клиентский платеж</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку платежей</a>
    <div class="clear"></div>

    <div class="error-box" style="display:none">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>

    <g:form name="clientpaymentDetailForm" url="${[action:'incertclientpayment']}" method="post" enctype="multipart/form-data" target="upload_target">
      <label for="paytype">Тип:</label>
      <g:select name="paytype" from="['исходящий','входящий','внутренний','внешний']" keys="[1,2,3,11]" noSelection="${['0':'не выбран']}" onchange="togglePaytype(this.value)"/>
      <label for="paycat">Категория:</label>
      <g:select name="paycat" from="['прочий','договорной','счета']" keys="416" onchange="togglePaycat(this.value)" noSelection="${['0':'не выбран']}"/>
      <label for="paydate">Дата платежа:</label>
      <g:datepicker class="normal nopad" name="paydate" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
      <label class="auto" for="is_clientcommission">
        <input type="checkbox" id="is_clientcommission" name="is_clientcommission" value="1" onclick="$('is_midcommission').checked=false;"/>
        Возврат комиссионных
      </label>
      <label class="auto" for="is_midcommission">
        <input type="checkbox" id="is_midcommission" name="is_midcommission" value="1" onclick="$('is_clientcommission').checked=false;"/>
        Возврат посреднику
      </label>
      <br/><label for="summa">Сумма:</label>
      <input type="text" id="summa" name="summa" value=""/>
      <label class="auto" for="is_nds">
        <input type="checkbox" id="is_nds" name="is_nds" value="1" checked />
        Наличие НДС
      </label>
      <label class="auto" for="is_urgent">
        <input type="checkbox" id="is_urgent" name="is_urgent" value="1" />
        Срочное задание
      </label>
      <div id="datadiv" style="display:none"></div>
      <hr class="admin" />

      <label for="destination">Назначение платежа:</label>
      <g:textArea name="destination" value="" />
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" value="" />

      <div class="clear" style="padding-top:10px"></div>
      <div id="tagblock">
        <hr class="admin" style="width:780px;float:left"/><a id="tagexpandlink" style="text-decoration:none" href="javascript:void(0)">&nbsp;&nbsp;Тегировать&nbsp;<i class="icon-collapse-top"></i></a><hr class="admin" style="width:70px;float:right"/>
        <div id="tagsection" style="width:960px">
          <label for="client_id">Клиент:</label>
          <g:select name="client_id" from="${client}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}" onchange="getSubclientsList(this.value)"/>
          <label for="subclient_id">Подклиент:</label>
          <span id="subclientslist"><g:select name="subclient_id" value="" from="['нет']" keys="0"/></span>
          <label for="tagcomment">Комментарий:</label>
          <input type="text" id="tagcomment" name="tagcomment" value="" class="fullline" />

          <hr class="admin" />
        </div>
        <div id="expensetype_tagsection" style="width:960px;display:none">
          <label for="expensetype_id">Доходы-расходы:</label>
          <g:select name="expensetype_id" class="fullline" value="" from="${expensetype}" optionKey="id" noSelection="${['0':'не выбран']}"/>
          <label for="expensetype_name">Доходы-расходы:</label>
          <input type="text" class="fullline" id="expensetype_name" value=""/>

          <hr class="admin" />
        </div>
      </div>
      <div class="clear"></div>
      <div class="fright" id="btns" style="padding-top:15px">
        <input type="reset" class="spacing" value="Сброс" onclick="togglePaytype(0)"/>
        <input type="button" id="pay_button" class="spacing" value="В оплату" onclick="submitForm(1,0)"/>
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(0,0)"/>
        <input type="button" id="detail_button" class="spacing" value="Детализация плана" onclick="submitForm(0,1)"/>
      </div>
      <input type="hidden" id="is_accept" name="is_accept" value="0"/>
      <input type="hidden" id="is_detail" name="is_detail" value="1"/>
    </g:form>
    <div class="clear"></div>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку платежей</a>
    <iframe id="upload_target" name="upload_target" style="display:none"></iframe>
    <g:form id="returnToListForm" name="returnToListForm" url="${[action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
