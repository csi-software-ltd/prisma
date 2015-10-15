<g:formRemote name="paymentForm" url="[controller:'payment',action:'banksaldolist']" update="list">
  <label class="auto" for="company">Компания:</label>
  <input type="text" id="company" name="company" value="${Company.get(inrequest?.company_id?:0)?.name?:''}"/>
  <input type="hidden" id="company_id" name="company_id" value="${inrequest?.company_id?:0}"/>
  <label class="auto" for="inn">ИНН:</label>
  <input type="text" id="inn" name="inn" value="${inrequest?.inn?:''}" />
  <label class="auto" for="order">Сорт.:</label>
  <g:select class="mini" name="order" value="${inrequest?.order?:0}" from="['по банку','по компании','по дате остатка средств']" keys="032"/>
  <label class="auto" for="bankname">Банк:</label>
  <input type="text" id="bankname" style="width:550px" name="bankname" value="${inrequest?.bankname}" />
  <label class="auto" for="valuta_id">Валюта:</label>
  <g:select class="auto" id="valuta_id" name="valuta_id" from="${Valuta.findAllByModstatus(1)}" value="857" optionValue="name" optionKey="id"/>
  <label class="auto" for="typeaccount_id">Тип счета:</label>
  <g:select id="typeaccount_id" name="typeaccount_id" from="['расчетный', 'корпоративный', 'текущий', 'транзитный', 'накопительный']" value="${inrequest?.typeaccount_id}" keys="12345"/>
  <div class="fright">
    <input type="button" class="reset spacing" value="Сброс" onclick="resetBankSaldoFilter()"/>
    <input type="submit" id="form_submit_button" value="Показать"/>
  </div>
</g:formRemote>
<div class="clear"></div>
<script type="text/javascript">
  new Autocomplete('company', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_full_autocomplete")}',
    onSelect: function(value, data){      
      var lsData=data.split(';')
      $('company_id').value = lsData[0];
      $('inn').value = lsData[1];      
    }
  }); 
  new Autocomplete('inn', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyinn_autocomplete")}',
    onSelect: function(value, data){
      var lsData=data.split(';')
      $('company_id').value = lsData[0];
      $('company').value = lsData[1];      
    }
  }); 
  new Autocomplete('bankname', {
    serviceUrl:'${resource(dir:"autocomplete",file:"bankname_autocomplete")}'
  }); 
  $('form_submit_button').click();
</script>