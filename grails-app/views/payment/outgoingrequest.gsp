  <label for="fromcompany">Плат. компания:</label>
  <span class="input-append">
    <input type="text" id="fromcompany" name="fromcompany" value="" style="width:200px"/>
    <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
  </span>
  <div id="fromcompany_autocomplete" class="autocomplete" style="display:none;"></div>
  <input type="hidden" id="fromcompany_id" name="fromcompany_id" value=""/>
<g:if test="${paycat==2}">
  <label for="totax_id">Тип налога:</label>
  <g:select id="totax_id" name="totax_id" from="${tax}" optionKey="id" optionValue="name" noSelection="${[0:'не выбран']}" />
  <label for="frombank" id="frombank_label">Банк плательщика:</label>
  <span id="frombank_span" class="input-append">
    <g:select class="fullline nopad normal" name="frombank" value="" from="['не выбран']" keys="['']"/>
  </span>
</g:if><g:elseif test="${paycat==3}">
  <br/><label for="frombank" id="frombank_label">Банк плательщика:</label>
  <span id="frombank_span" class="input-append">
    <g:select class="fullline nopad normal" name="frombank" value="" from="['не выбран']" keys="['']"/>
  </span>
  <label for="pers_id">Работник:<a href="javascript:void(0)" onclick="openPers($('pers_id').value)"><i class="icon-pencil"></i></a></label>
  <span id="pers_span"><g:select name="pers_id" from="['не выбран']" keys="0"/></span>
  <label for="card">Карта:</label>
  <span id="card_span"><g:select name="card" from="['не выбрана']" keys="['-1']"/></span>
</g:elseif><g:else>
  <label for="tocompany" id="tocompany_label">Получ. компания:</label>
  <span class="input-append">
    <input type="text" id="tocompany" name="tocompany" value="" style="width:200px"/>
    <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
  </span>
  <div id="tocompany_autocomplete" class="autocomplete" style="display:none"></div>
  <input type="hidden" id="tocompany_id" name="tocompany_id" value=""/>
  <label for="frombank" id="frombank_label">Банк плательщика:</label>
  <span id="frombank_span" class="input-append">
    <g:select class="fullline nopad normal" name="frombank" value="" from="['не выбран']" keys="['']"/>
  </span>
  <div id="bank_div">
    <label for="tobank" id="tobank_label">Банк получателя:</label>
    <span id="tobank_span" class="input-append">
      <g:select class="fullline nopad normal" name="tobank" value="" from="['не выбран']" keys="['']"/>
    </span>
  </div>
<g:if test="${paycat==1}">
  <div id="paycat_agr">
    <label for="agreementtype_id">Тип договора:</label>
    <g:select name="agreementtype_id" value="" from="${agrtypes}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}" onchange="selectAgreement(this.value,1)"/><br/>
    <label for="agreement_id">№ договора:</label>
    <span id="agreement_span">
      <g:select name="agreement_id" from="['не выбран']" keys="0"/>
    </span>
    <label for="is_com" style="display:none">
      <input type="checkbox" id="is_com" name="is_com" value="1" />
      Возврат комиссии
    </label>
    <label for="is_dopmain" style="display:none">
      <input type="checkbox" id="is_dopmain" name="is_dopmain" value="1" />
      Доп. платежи
    </label>
    <label class="auto" for="is_dop" style="display:none">
      <input type="checkbox" id="is_dop" name="is_dop" value="1" />
      Погашение процентов
    </label>
    <label class="auto" for="is_fine" style="display:none">
      <input type="checkbox" id="is_fine" name="is_fine" value="1" />
      Пеня
    </label>
  </div>
</g:if><g:elseif test="${paycat==4}">
  <span id="comment_span">
    <label for="comment">Комментарий:</label>
    <g:textArea name="comment" value="" />
  </span>
</g:elseif>
</g:else>
<script type="text/javascript">
  new Autocomplete('fromcompany', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_int_autocomplete")}',
    onSelect: function(value, data){
      var lsData = data.split(';');
      $('fromcompany_id').value = lsData[0];
      iTaxoption_id = lsData[1];
      if(${paycat==3?1:0}) getPersByCompany(lsData[0]);
      if(${paycat==1?1:0}){
        $('agreementtype_id').selectedIndex=0;
        selectAgreement(0,1)
      }
      getBankByFromCompany();
    }
  });
  new Autocomplete('tocompany', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_ext_autocomplete")}',
    onSelect: function(value, data){
      var lsData=data.split(';');
      $('tocompany_id').value = lsData[0];
      if(${paycat==1?1:0}){
        $('agreementtype_id').selectedIndex=0;
        selectAgreement(0,1)
      }
      getBankByCompany();
    }
  });
</script>