<label for="account_bankname">Банк:</label>
<input type="text" class="fullline" id="account_bankname" name="bankname" value="${Bank.findByIdIlike(account?.bank_id?:'')?.name?:''}" onblur="banknamevalidate()" />
<div id="bankname_autocomplete" class="autocomplete" style="display:none"></div>
<label for="account_bank_id">Бик:</label>
<input type="text" class="auto" id="account_bank_id" name="bank_id" value="${account?.bank_id}" onblur="bikvalidate()"/>
<div id="bik_autocomplete" class="autocomplete" style="display:none"></div>
<label for="account_coraccount">Корсчет:</label>
<input type="text" class="auto" id="account_coraccount" name="coraccount" value="${account?.coraccount}" readonly />
<label for="account_schet">Счет:</label>
<input type="text" class="auto" id="account_schet" name="schet" value="${g.account(value:account?.schet)}"/>
<label for="account_valuta_id">Валюта счета:</label>
<g:select id="account_valuta_id" name="valuta_id" from="${Valuta.findAllByModstatus(1)}" value="${account?.valuta_id?:857}" optionValue="name" optionKey="id"/>
<label for="account_dopoffice">Доп. офис:</label>
<input type="text" class="fullline" id="account_dopoffice" name="dopoffice" value="${account?.dopoffice}"/>
<label for="account_pers_id">Директор:</label>
<g:select id="account_pers_id" name="pers_id" from="${directors}" value="${account?account.pers_id:curdirector_id}" optionValue="shortname" optionKey="pers_id" noSelection="${['0':'не указано']}"/>
<label for="directordate">Дата назначения:</label>
<g:datepicker name="directordate" value="${account?.directordate?String.format('%td.%<tm.%<tY',account.directordate):''}"/>
<label for="account_opendate">Дата открытия:</label>
<g:datepicker style="margin-right:108px" name="account_opendate" value="${String.format('%td.%<tm.%<tY',account?.opendate?:new Date())}"/>
<label for="account_closedate">Дата закрытия:</label>
<g:datepicker name="account_closedate" value="${account?.closedate?String.format('%td.%<tm.%<tY',account.closedate):''}"/>
<label for="account_typeaccount_id">Тип счета:</label>
<g:select id="account_typeaccount_id" name="typeaccount_id" value="${account?.typeaccount_id}" from="['расчетный', 'корпоративный', 'текущий', 'транзитный','накопительный','планируемый','отказ в открытии']" keys="1234567"/>
<g:if test="${company.is_holding}">
<label for="account_bankclient_id">Тип БК:</label>
<g:select id="account_bankclient_id" name="bankclient_id" from="${bankclients}" value="${account?.bankclient_id}" optionValue="name" optionKey="id" noSelection="${['0':'нет']}"/>
<label for="account_ibank_open">Дата активации БК</label>
<g:datepicker class="normal nopad" style="margin-right:108px" name="account_ibank_open" value="${account?.ibank_open?String.format('%td.%<tm.%<tY',account.ibank_open):''}"/>
<label for="account_ibankterm">Срок БК, дней</label>
<input type="text" class="auto" id="account_ibankterm" name="ibankterm" value="${account?.ibankterm?:bank?.ibankterm}"/>
<span id="addtel" style="${!account?.is_duplicate?'display:none':''}"><label for="account_smstel">Доп телефон:</label>
<input type="text" class="auto" id="account_smstel" name="smstel" value="${account?.smstel}"/></span><br/>
<label for="account_ibank_comment">Комментарий к БК:</label>
<label class="auto" for="account_is_bkactproc">
  <input type="checkbox" id="account_is_bkactproc" name="is_bkactproc" value="1" <g:if test="${account?.is_bkactproc}">checked</g:if> <g:if test="${account?.ibankstatus==1}">disabled</g:if> />
  Активация
</label>
<label class="auto" for="account_is_duplicate">
  <input type="checkbox" id="account_is_duplicate" name="is_duplicate" value="1" onclick="toggleaddtel(this)" <g:if test="${account?.is_duplicate}">checked</g:if> />
  Дубликат
</label>
<label class="auto" for="account_is_smsinfo">
  <input type="checkbox" id="account_is_smsinfo" name="is_smsinfo" value="1" <g:if test="${account?.is_smsinfo}">checked</g:if> />
  СМС инфо
</label>
<label class="auto" for="account_ibankblock">
  <input type="checkbox" id="account_ibankblock" name="ibankblock" value="1" <g:if test="${account?.ibankblock}">checked</g:if> />
  БК блок
</label>
<g:textArea name="ibank_comment" id="account_ibank_comment" value="${account?.ibank_comment}" />
<g:if test="${session.user.confaccess>0}">
<label for="account_saldo">Подтв. остаток:</label>
<input type="text" class="auto" id="account_saldo" value="${intnumber(value:account?.saldo)}" disabled/>
<label for="account_saldodate">Дата:</label>
<g:datepicker class="normal nopad" name="account_saldodate" value="${account?.saldodate?String.format('%td.%<tm.%<tY',account.saldodate):''}" disabled="true"/>
<label for="account_actsaldo">Факт. остаток:</label>
<input type="text" class="auto" id="account_actsaldo" value="${intnumber(value:account?.actsaldo)}" disabled/>
<label for="account_actsaldodate">Дата:</label>
<g:datepicker class="normal nopad" name="account_actsaldodate" value="${account?.actsaldodate?String.format('%td.%<tm.%<tY',account.actsaldodate):''}" disabled="true"/>
</g:if>
</g:if>
<div class="clear"></div>
<div class="fright">
<g:if test="${account?.typeaccount_id==1}">
<g:if test="${bank?.is_request==1}">
  <a class="button" href="${createLink(controller:'company', action:'bankrequest',id:company.id,params:[acc_id:account.id])}" target="_blank">
    Заявка в банк &nbsp;<i class="icon-angle-right icon-large"></i>
  </a>
</g:if><g:if test="${bank?.is_anketa==1}">
  <a class="button" href="${createLink(controller:'company', action:'bankanketa',id:company.id,params:[acc_id:account.id])}" target="_blank">
    Анкета в банк &nbsp;<i class="icon-angle-right icon-large"></i>
  </a>
</g:if>
</g:if>
<g:if test="${iscanedit}">
  <input type="submit" id="accountadd_submit_button" class="button" value="Сохранить" onclick="nextfield(event)"/>
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#accountAddForm').slideUp(300, function() {getAccounts()})"/>
</div>
<input type="hidden" name="company_id" value="${company.id}"/>
<input type="hidden" name="id" value="${account?.id}"/>
<div class="clear" style="padding-bottom:10px"></div>
<hr class="admin" />
<script type="text/javascript">
  $('accountadd_submit_button').focus();
  new Autocomplete('account_bankname', {
    serviceUrl:'${resource(dir:"autocomplete",file:"bankname_autocomplete")}',
    onSelect: function(value, data){
      var lsData = data.split(';');
      $('account_bank_id').value = lsData[0];
      $('account_coraccount').value = lsData[1];
      $('account_ibankterm').value = lsData[2];
      $('account_bankname').focus();
    }
  });
  new Autocomplete('account_bank_id', {
    serviceUrl:'${resource(dir:"autocomplete",file:"bik_autocomplete")}',
    onSelect: function(value, data){
      var lsData = data.split(';');
      $('account_bankname').value = lsData[0];
      $('account_coraccount').value = lsData[1];
      $('account_ibankterm').value = lsData[2];
      $('account_bank_id').focus();
    }
  });
  jQuery("#account_schet").mask("?*****.***.*.*******.****",{placeholder:" "});
  jQuery("#directordate").mask("99.99.9999",{placeholder:" "});
  jQuery("#account_opendate").mask("99.99.9999",{placeholder:" "});
  jQuery("#account_closedate").mask("99.99.9999",{placeholder:" "});
  jQuery("#account_ibank_open").mask("99.99.9999",{placeholder:" "});
  jQuery("#account_saldodate").mask("99.99.9999",{placeholder:" "});
  jQuery("#account_actsaldodate").mask("99.99.9999",{placeholder:" "});
</script>
