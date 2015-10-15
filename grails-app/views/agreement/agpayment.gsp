<label for="agpayment_paycat">Категория:</label>
<g:select id="agpayment_paycat" name="paycat" from="['прочий','договорной']" keys="41" onchange="togglePaycat(this.value)"/><br/>
<label for="agpayment_fromcompany">Плат. компания:</label>
<span class="input-append">
  <input type="text" class="nopad normal" id="agpayment_fromcompany" name="fromcompany" value=""/>
  <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
</span>
<div id="fromcompany_autocomplete" class="autocomplete" style="display:none;"></div>
<input type="hidden" id="agpayment_fromcompany_id" name="fromcompany_id" value=""/>
<label for="agpayment_tocompany">Получ. компания:</label>
<span class="input-append">
  <input type="text" class="nopad normal" id="agpayment_tocompany" name="tocompany" value=""/>
  <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
</span>
<div id="tocompany_autocomplete" class="autocomplete" style="display:none"></div>
<input type="hidden" id="agpayment_tocompany_id" name="tocompany_id" value=""/>

<div id="agpayment_bank_div" style="display:none">
  <label for="agpayment_tobank">Банк получателя:</label>
  <span id="agpayment_tobank_span">
    <g:select id="agpayment_tobank" class="fullline" name="tobank" value="" from="" noSelection="${['':'не выбран']}"/>
  </span>
</div>

<div id="agpayment_paycat_agr" style="display:none">
  <label for="agpayment_agreementtype_id">Тип договора:</label>
  <g:select id="agpayment_agreementtype_id" name="agreementtype_id" value="" from="${agrtypes}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}" onchange="selectAgreement(this.value)"/>
  <label for="agpayment_agreement_id">№ договора:</label>
  <span id="agpayment_agreement_span"><g:select id="agpayment_agreement_id" name="agreement_id" from="['не выбран']" keys="0"/></span>
</div>

<hr class="admin" />

<label for="agpayment_paydate">Дата платежа:</label>
<g:datepicker class="normal nopad" name="agpayment_paydate" value="${String.format('%td.%<tm.%<tY',new Date())}"/><br/>
<label for="agpayment_summa">Сумма:</label>
<input type="text" id="agpayment_summa" name="summa" value="" onblur="setSummaNds(this.value)"/>
<!--<label for="agpayment_summands">Сумма НДС:</label>
<input type="text" id="agpayment_summands" value="" name="summands"/>-->
<label for="is_nds" class="auto">
  <input type="checkbox" id="is_nds" name="is_nds" value="1" checked />
  С учетом НДС
</label><br />  
<label for="agpayment_destination">Назначение платежа:</label>
<g:textArea id="agpayment_destination" name="destination" value="" />

<span id="agpayment_comment_span">
  <label for="agpayment_comment">Комментарий:</label>
  <g:textArea id="agpayment_comment" name="comment" value="" />
</span>

<div>
  <label for="agpayment_agent_id">Агент:</label>
  <g:select id="agpayment_agent_id" name="agent_id" from="${agents}" value="" optionValue="name" optionKey="id" noSelection="${['0':'не выбран']}"/><br/>
  <label for="agpayment_tagcomment">Комментарий:</label>
  <input type="text" id="agpayment_tagcomment" name="tagcomment" value="" class="fullline" />
</div>

<div class="clear"></div>
<div class="fright">
  <input type="submit" class="button" value="Сохранить"/>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#agpaymentaddForm').slideUp();"/>
</div>
<input type="hidden" name="agentagr_id" value="${agentagr.id}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#agpayment_paydate").mask("99.99.9999",{placeholder:" "});
  new Autocomplete('agpayment_fromcompany', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_int_autocomplete")}',
    onSelect: function(value, data){
      var lsData=data.split(';');
      $('agpayment_fromcompany_id').value=lsData[0];
      iTaxoption_id=lsData[1];
      setSummaNds($('agpayment_summa').value);
    }
  });
  new Autocomplete('agpayment_tocompany', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_ext_autocomplete")}',
    onSelect: function(value, data){
      var lsData=data.split(';');
      $('agpayment_tocompany_id').value=lsData[0];
      getBankByCompany();
    }
  });
</script>
