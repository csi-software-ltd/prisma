<html>
  <head>
    <title>Prisma: Платежи - Добавление внутреннего платежа</title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="jquery.maskedinput.min" />
    <g:javascript>
      var isTagOpen = true;
      function returnToList(){
        $("returnToListForm").submit();
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
        jQuery("#paydate").mask("99.99.9999",{placeholder:" "});
        new Autocomplete('fromcompany', {
          serviceUrl:'${resource(dir:"autocomplete",file:"companyname_int_autocomplete")}',
          onSelect: function(value, data){
            var lsData = data.split(';');
            $('fromcompany_id').value = lsData[0];
            getbankaccountbycompany();
          }
        });
        new Autocomplete('tocompany', {
          serviceUrl:'${resource(dir:"autocomplete",file:"companyname_int_autocomplete")}',
          onSelect: function(value, data){
            var lsData=data.split(';');
            $('tocompany_id').value = lsData[0];
            getBankByCompany('to');
          }
        });
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
    </style>
  </head>
  <body onload="init();">
    <h3 class="fleft">Новый внутренний платеж</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку платежей</a>
    <div class="clear"></div>

    <div class="error-box" style="display:none">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>

    <g:form name="internalpaymentDetailForm" url="${[action:'incertinternalpayment']}" method="post" enctype="multipart/form-data" target="upload_target">
      <label for="paytype">Тип:</label>
      <g:select name="paytype" from="['внутренний']" keys="3"/>
      <label for="paycat">Категория:</label>
      <g:select name="paycat" from="['прочий']" keys="4"/>
      <label for="paydate">Дата платежа:</label>
      <g:datepicker class="normal nopad" name="paydate" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
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
      <div id="datadiv">
        <label for="fromcompany">Плат. компания:</label>
        <span class="input-append">
          <input type="text" id="fromcompany" name="fromcompany" value="" style="width:200px"/>
          <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
        </span>
        <input type="hidden" id="fromcompany_id" name="fromcompany_id" value=""/>
        <label for="tocompany" id="tocompany_label">Получ. компания:</label>
        <span class="input-append">
          <input type="text" id="tocompany" name="tocompany" value="" style="width:200px"/>
          <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
        </span>
        <input type="hidden" id="tocompany_id" name="tocompany_id" value=""/>
        <label for="frombank" id="frombank_label">Счет плательщика:</label>
        <span id="frombank_span" class="input-append">
          <g:select class="fullline nopad normal" name="frombank" value="" from="['не выбран']" keys="['']"/>
        </span>
        <div class="clear"></div>
        <div id="bankaccountdata_div">
        </div>
        <label for="tobank" id="tobank_label">Банк получателя:</label>
        <span id="tobank_span" class="input-append">
          <g:select class="fullline nopad normal" name="tobank" value="" from="['не выбран']" keys="['']"/>
        </span>
        <label for="plan">План исполнения:</label>
        <g:textArea id="plan" name="plan" value=""/>
      </div>
      <hr class="admin" />

      <label for="destination">Назначение платежа:</label>
      <g:textArea name="destination" value="" />
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" value="" />

      <div class="clear" style="padding-top:10px"></div>
      <div id="tagblock">
        <hr class="admin" style="width:780px;float:left"/><a id="tagexpandlink" style="text-decoration:none" href="javascript:void(0)">&nbsp;&nbsp;Тегировать&nbsp;<i class="icon-collapse-top"></i></a><hr class="admin" style="width:70px;float:right"/>
        <div id="tagsection" style="width:960px">
          <label for="tagcomment">Комментарий:</label>
          <input type="text" id="tagcomment" name="tagcomment" value="" class="fullline" />

          <hr class="admin" />
        </div>
      </div>
      <div class="clear"></div>
      <div class="fright" id="btns" style="padding-top:15px">
        <input type="submit" value="Создать"/>
      </div>
    </g:form>
    <iframe id="upload_target" name="upload_target" style="display:none"></iframe>
    <g:form id="returnToListForm" name="returnToListForm" url="${[action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>