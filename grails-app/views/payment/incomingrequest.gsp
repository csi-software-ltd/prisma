﻿<g:if test="${paycat!=2}">
  <label for="fromcompany">Плат. компания:</label>
  <span class="input-append">
    <input type="text" id="fromcompany" name="fromcompany" value="" style="width:200px"/>
    <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
  </span>
  <div id="fromcompany_autocomplete" class="autocomplete" style="display:none;"></div>
  <input type="hidden" id="fromcompany_id" name="fromcompany_id" value=""/>
</g:if><g:else>
  <label for="fromtax_id">Тип налога:</label>
  <g:select id="fromtax_id" name="fromtax_id" from="${tax}" optionKey="id" optionValue="name" noSelection="${[0:'не выбран']}" />
</g:else>
  <label for="tocompany" id="tocompany_label">Получ. компания:</label>
  <span class="input-append">
    <input type="text" id="tocompany" name="tocompany" value="" style="width:200px"/>
    <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
  </span>
  <div id="tocompany_autocomplete" class="autocomplete" style="display:none"></div>
  <input type="hidden" id="tocompany_id" name="tocompany_id" value=""/>
  <div id="bank_div">
    <label for="tobank" id="tobank_label">Банк получателя:</label>
    <span id="tobank_span" class="input-append">
      <g:select class="fullline nopad normal" name="tobank" value="" from="['не выбран']" keys="['']"/>
    </span>
  </div>
<g:if test="${paycat==1}">
  <div id="paycat_agr">
    <label for="agreementtype_id">Тип договора:</label>
    <g:select name="agreementtype_id" value="" from="${agrtypes}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}" onchange="selectAgreement(this.value,0)"/>
    <label for="agreement_id">№ договора:</label>
    <span id="agreement_span">
      <g:select name="agreement_id" from="['не выбран']" keys="0"/>
    </span>
  </div>
</g:if><g:elseif test="${paycat==4}">
  <span id="comment_span">
    <label for="comment">Комментарий:</label>
    <g:textArea name="comment" value="" />
  </span>
</g:elseif>
<script type="text/javascript">
<g:if test="${paycat!=2}">
  new Autocomplete('fromcompany', {
      serviceUrl:'${resource(dir:"autocomplete",file:"companyname_ext_autocomplete")}',
      onSelect: function(value, data){
        var lsData = data.split(';');
        $('fromcompany_id').value = lsData[0];
        if(${paycat==1?1:0}){
          $('agreementtype_id').selectedIndex=0;
          selectAgreement(0,0)
        }
      }
  });
</g:if>
  new Autocomplete('tocompany', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_int_autocomplete")}',
    onSelect: function(value, data){
      var lsData = data.split(';');
      $('tocompany_id').value = lsData[0];
      if(${paycat==1?1:0}){
        $('agreementtype_id').selectedIndex=0;
        selectAgreement(0,0)
      }
      getBankByCompany();
    }
  });
</script>