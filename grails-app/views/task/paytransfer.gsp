<label for="paytransfer_fromcompany">Плат. компания:</label>
<input type="text" id="paytransfer_fromcompany" name="fromcompany" value="${paytransfer?.fromcompany}"/>
<div id="fromcompany_autocomplete" class="autocomplete" style="display:none;"></div>
<input type="hidden" id="paytransfer_fromcompany_id" name="fromcompany_id" value="${paytransfer?.fromcompany_id}"/>
<label for="paytransfer_tocompany">Получ. компания:</label>
<input type="text" id="paytransfer_tocompany" name="tocompany" value="${paytransfer?.tocompany}"/>
<div id="tocompany_autocomplete" class="autocomplete" style="display:none;"></div>
<input type="hidden" id="paytransfer_tocompany_id" name="tocompany_id" value="${paytransfer?.tocompany_id}"/>
<label for="paytransfer_frombank">Банк плательщика:</label>
<span id="paytransfer_frombank_span">
  <g:select id="paytransfer_frombank" class="fullline" name="frombank" value="${frombankbik}" from="${frombanks}" optionKey="id" optionValue="name" noSelection="${['':'не выбран']}"/>
</span>
<label for="paytransfer_tobank">Банк получателя:</label>
<span id="paytransfer_tobank_span">
  <g:select id="paytransfer_tobank" class="fullline" name="tobank" value="${tobankbik}" from="${tobanks}" optionKey="id" optionValue="name" noSelection="${['':'не выбран']}"/>
</span>

<hr class="admin" />

<label for="paytransfer_paydate">Дата платежа:</label>
<g:datepicker class="normal nopad" name="paytransfer_paydate" value="${String.format('%td.%<tm.%<tY',paytransfer?.paydate?:new Date())}"/><br/>
<label for="paytransfer_summa">Сумма:</label>
<input type="text" id="paytransfer_summa" name="summa" value="${paytransfer?.summa}"/>
<label for="is_nds" class="auto">
  <input type="checkbox" id="is_nds" name="is_nds" value="1" <g:if test="${payrequest?.is_nds!=0}">checked</g:if> />
  Наличие НДС
</label><br />
<label for="paytransfer_destination">Назначение платежа:</label>
<g:textArea id="paytransfer_destination" name="destination" value="${paytransfer?.destination}" />

<label for="paytransfer_comment">Комментарий:</label>
<g:textArea id="paytransfer_comment" name="comment" value="${paytransfer?.comment?:basecomment}" disabled="true"/>

<div class="clear"></div>
<div class="fright">
<g:if test="${!paytransfer}">
  <input type="submit" class="button" value="Сохранить"/>
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#paytransferForm').slideUp();"/>
</div>
<input type="hidden" name="taskpay_id" value="${taskpay.id}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#paytransfer_paydate").mask("99.99.9999",{placeholder:" "});
  new Autocomplete('paytransfer_fromcompany', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_int_autocomplete")}',
    onSelect: function(value, data){
      var lsData=data.split(';');
      $('paytransfer_fromcompany_id').value=lsData[0];
      getBankByCompany('frombank');
    }
  });
  new Autocomplete('paytransfer_tocompany', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_int_autocomplete")}',
    onSelect: function(value, data){
      var lsData=data.split(';');
      $('paytransfer_tocompany_id').value=lsData[0];
      getBankByCompany('tobank');
    }
  });
</script>