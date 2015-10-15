<label for="bankname">Название банка:</label>
<input type="text" id="bankname" name="bankname" value="${Bank.get(persaccount?.bank_id?:'')?.name?:''}" class="auto"/>
<div id="bankname_autocomplete" class="autocomplete" style="display:none"></div>
<label for="bank_id">БИК банка:</label>
<input type="text" id="bank_id" name="bank_id" value="${Bank.get(persaccount?.bank_id?:'')?.id?:''}" />
<div id="bik_autocomplete" class="autocomplete" style="display:none"></div>
<label for="coraccount" disabled>Коррсчет банка:</label>
<input type="text" id="coraccount" value="${Bank.get(persaccount?.bank_id?:'')?.coraccount?:''}" readonly />
<label for="modstatus">Статус:</label>
<g:select name="modstatus" value="${persaccount?.modstatus}" from="['Активный','Неактивный']" keys="[1,0]"/>
<label for="nomer">Номер карты:<br/><small>1234 1234 1234 1234</small></label>
<input type="text" id="nomer" name="nomer" value="${persaccount?.nomer?:''}" />
<label for="nomer">Номер лиц. счета:<br/><small>12345.123.1.1234567.1234</small></label>
<input type="text" id="paccount" name="paccount" value="${g.account(value:persaccount?.paccount)}" />
<label for="validmonth">Действ. до месяц:</label>
<g:select class="mini" name="validmonth" value="${persaccount?.validmonth}" from="${month}" optionKey="id" optionValue="id" noSelection="${['':'не выбран']}"/>
<label for="validyear" class="auto">год:</label>
<g:select class="mini" name="validyear" value="${persaccount?.validyear}" from="${year}" optionValue="id" optionKey="id" noSelection="${['':'не выбран']}"/>
<label for="pin" class="auto">PIN код:</label>
<input type="text" class="mini" id="pin" name="pin" value="${persaccount?.pin?:''}" maxlength="4"/>
<g:if test="${persaccount}">
<label for="is_main">
  <input type="checkbox" id="is_main" value="1" <g:if test="${persaccount?.is_main}">checked</g:if> disabled="true" />
  Главный
</label>
</g:if>
<input type="hidden" name="id" value="${persaccount?.id?:0}"/>
<div class="clear"></div>
<div class="fright">
  <g:if test="${user?.group?.is_persaccountedit}">
    <input type="submit" id="savepersaccount_submit_button" class="button" value="Сохранить" />
  </g:if>  
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#persaccountAddForm').slideUp();"/>
</div>
<div class="clear" style="padding-bottom:10px"></div>
<hr class="admin" />
<script type="text/javascript">  
  new Autocomplete('bankname', {
    serviceUrl:'${resource(dir:"autocomplete",file:"bankname_autocomplete")}',
    onSelect: function(value, data){
      var sData=data.split(';');
      $('bank_id').value = sData[0];
      $('coraccount').value = sData[1];
    }
  });
  new Autocomplete('bank_id', {
    serviceUrl:'${resource(dir:"autocomplete",file:"bik_autocomplete")}',
    onSelect: function(value, data){
      var lsData = data.split(';');
      $('bankname').value = lsData[0];
      $('coraccount').value = lsData[1];
      $('bank_id').focus();
    }
  });
  jQuery(function($){
     jQuery("#nomer").mask("9999 9999 9999 9999? 99");
     jQuery("#paccount").mask("99999.999.9.9999999.9999",{placeholder:" "});     
  });  
</script>
