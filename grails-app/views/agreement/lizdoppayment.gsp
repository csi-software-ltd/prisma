<label for="lizdoppayment_paycat">Категория:</label>
<g:select id="lizdoppayment_paycat" name="paycat" from="['прочий']" keys="4"/><br/>
<label for="lizdoppayment_fromcompany">Плат. компания:</label>
<span class="input-append">
  <input type="text" class="nopad normal" id="lizdoppayment_fromcompany" name="fromcompany" value=""/>
  <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
</span>
<input type="hidden" id="lizdoppayment_fromcompany_id" name="fromcompany_id" value=""/>
<label for="lizdoppayment_tocompany">Получ. компания:</label>
<span class="input-append">
  <input type="text" class="nopad normal" id="lizdoppayment_tocompany" name="tocompany" value=""/>
  <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
</span>
<input type="hidden" id="lizdoppayment_tocompany_id" name="tocompany_id" value=""/>

<div id="lizdoppayment_bank_div" style="display:none">
  <label for="lizdoppayment_tobank">Банк получателя:</label>
  <span id="lizdoppayment_tobank_span">
    <g:select id="lizdoppayment_tobank" class="fullline" name="tobank" value="" from="" noSelection="${['':'не выбран']}"/>
  </span>
</div>

<hr class="admin" />

<label for="lizdoppayment_paydate">Дата платежа:</label>
<g:datepicker class="normal nopad" name="lizdoppayment_paydate" value="${String.format('%td.%<tm.%<tY',new Date())}"/><br/>
<label for="lizdoppayment_summa">Сумма:</label>
<input type="text" id="lizdoppayment_summa" name="summa" value="" />
<label for="is_nds" class="auto">
  <input type="checkbox" id="is_nds" name="is_nds" value="1" checked />
  С учетом НДС
</label><br />  
<label for="lizdoppayment_destination">Назначение платежа:</label>
<g:textArea id="lizdoppayment_destination" name="destination" value="" />
<hr class="admin" style="width:756px;float:left"/><a id="tagexpandlink" style="text-decoration:none" href="javascript:void(0)">&nbsp;&nbsp;Тегировать&nbsp;<i class="icon-collapse-top"></i></a><hr class="admin" style="width:70px;float:right"/>
<div id="tagsection" style="width:936px">
  <label for="lizdoppayment_project_id">Проект:</label>
  <g:select id="lizdoppayment_project_id" name="project_id" value="${defproject_id}" from="${project}" optionKey="id" optionValue="name" />
  <br/><label for="lizdoppayment_expensetype_id">Доходы-расходы:</label>
  <g:select id="lizdoppayment_expensetype_id" name="expensetype_id" class="fullline" value="" from="${expensetype}" optionKey="id" noSelection="${['0':'не выбран']}"/>

  <hr class="admin" />
</div>
<div class="clear"></div>
<div class="fright">
  <input type="button" class="button" value="Создать" onclick="$('lizdoppayment_is_task').value=1;$('lizdoppaymentadd_submit_button').click();"/>
  <input type="submit" id="lizdoppaymentadd_submit_button" class="button" value="Сохранить"/>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#lizdoppaymentaddForm').slideUp();"/>
</div>
<input type="hidden" id="lizdoppayment_is_task" name="is_task" value="0"/>
<input type="hidden" name="agr_id" value="${agr.id}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#lizdoppayment_paydate").mask("99.99.9999",{placeholder:" "});
  new Autocomplete('lizdoppayment_fromcompany', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_int_autocomplete")}',
    onSelect: function(value, data){
      var lsData=data.split(';');
      $('lizdoppayment_fromcompany_id').value=lsData[0];
    }
  });
  new Autocomplete('lizdoppayment_tocompany', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_ext_autocomplete")}',
    onSelect: function(value, data){
      var lsData=data.split(';');
      $('lizdoppayment_tocompany_id').value=lsData[0];
      getBankByCompany();
    }
  });
</script>