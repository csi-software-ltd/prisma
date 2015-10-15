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
  <span id="frombank_span" class="input-append" style="min-height:34px;width:704px;">
    <g:select class="fullline nopad normal" name="frombank" value="" from="['не выбран']" keys="['']"/>
  </span>
  <div class="clear"></div>
  <div id="bankaccountdata_div">
  </div>
  <label for="tobank" id="tobank_label">Банк получателя:</label>
  <span id="tobank_span" class="input-append">
    <g:select class="fullline nopad normal" name="tobank" value="" from="['не выбран']" keys="['']"/>
  </span>
<script type="text/javascript">
  new Autocomplete('fromcompany', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_ext_autocomplete")}',
    onSelect: function(value, data){
      var lsData = data.split(';');
      $('fromcompany_id').value = lsData[0];
      getBankByCompany('from');
    }
  });
  new Autocomplete('tocompany', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_ext_autocomplete")}',
    onSelect: function(value, data){
      var lsData=data.split(';');
      $('tocompany_id').value = lsData[0];
      getBankByCompany('to');
    }
  });
</script>