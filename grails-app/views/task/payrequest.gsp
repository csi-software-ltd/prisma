  <label for="paydate" disabled>Срок платежа:</label>
  <input type="text" id="paydate" value="${String.format('%td.%<tm.%<tY',payrequest.paydate)}" disabled />
  <label for="payrequest_summa" ${!iscanedit?'disabled':''}>Сумма платежа:</label>
  <input type="text" id="payrequest_summa" ${!iscanedit?'disabled':''} name="summa" value="${number(value:payrequest.summa,fdigs:2)}" />
  <label for="paycat" disabled>Категория:</label>
  <g:select name="paycat" value="${payrequest.paycat}" from="['договорной','бюджетный','персональный','прочий', 'банковский', 'счета']" keys="${1..6}" noSelection="${['0':'не выбран']}" disabled="true"/>
  <label for="modstatus" disabled>Статус:</label>
  <g:select name="modstatus" value="${payrequest?.modstatus}" from="['в задании','выполнен','подтвержден']" keys="123" disabled="true" />
  <label for="fromcompany" disabled>Плат. компания:</label>
  <input type="text" id="fromcompany" name="fromcompany" value="${payrequest.fromcompany}" disabled />
  <label for="frombankbik" disabled>БИК банка плат.:</label>
  <input type="text" id="frombankbik" name="frombankbik" value="${frombank?.id}" disabled />
  <label for="frombank" disabled>Банк плательщика:</label>
  <input type="text" id="frombank" name="frombank" value="${frombank?.name}" disabled class="fullline" />
<g:if test="${payrequest.paycat in [Payment.PAY_CAT_AGR,Payment.PAY_CAT_OTHER,Payment.PAY_CAT_BANK,Payment.PAY_CAT_ORDER]}">
  <label for="tocompany" id="tocompany_label" ${!iscanedit?'disabled':''}>Получ. компания:</label>
  <span class="input-append">
    <input type="text" id="tocompany" name="tocompany" value="${payrequest.tocompany}" style="width:200px" ${!iscanedit?'disabled':''}/>
    <span class="add-on" onclick="newCompany()"><abbr title="Добавить компанию"><i class="icon-plus"></i></abbr></span>
  </span>
  <input type="hidden" id="tocompany_id" name="tocompany_id" value="${payrequest.tocompany_id}"/>
  <label for="tobankbik" disabled>БИК банка получ.:</label>
  <input type="text" id="tobankbik" name="tobankbik" value="${payrequest.tobankbik}" disabled/>
</g:if><g:elseif test="${payrequest.paycat==Payment.PAY_CAT_BUDG}">
  <label for="tax_type" disabled>Тип налога:</label>
  <input type="text" id="tax_type" name="tax_type" value="${Tax.get(payrequest.tax_id?:0)?.shortname}" disabled />
  <label for="tobankbik" disabled>БИК банка получ.:</label>
  <input type="text" id="tobankbik" name="tobankbik" value="${payrequest.tobankbik}" disabled />
</g:elseif><g:else>
  <label for="pers" disabled>Работник:</label>
  <input type="text" id="pers_id" value="${Pers.get(payrequest.pers_id?:0l)?.shortname}" disabled />
  <label for="tobankbik" disabled>БИК банка получ.:</label>
  <input type="text" id="tobankbik" name="tobankbik" value="${payrequest.tobankbik}" disabled />
</g:else>
  <label for="tobank" id="tobank_label">Банк получателя:</label>
  <span id="tobank_span" class="input-append">
    <g:select class="fullline nopad normal" name="tobank" value="${payrequest.tobankbik}" from="${tobanks}" noSelection="${['0':'не выбран']}" optionKey="id" optionValue="name" disabled="${!iscanedit}"/>
  </span>
  <label for="tocorraccount" disabled>Кор. счет банка<br/>получателя:</label>
  <input type="text" id="tocorraccount" name="tocorraccount" value="${payrequest.tocorraccount}" disabled />
  <label for="toaccount" disabled>Расч. счет банка<br/>получателя:</label>
  <input type="text" id="toaccount" name="toaccount" value="${payrequest.toaccount}" disabled />
  <label for="platperiod" disabled>Налог. период:</label>
  <input type="text" id="platperiod" name="platperiod" value="${payrequest.platperiod}" disabled />
  <label for="oktmo" disabled>ОКТМО плат.:</label>
  <input type="text" id="oktmo" name="oktmo" value="${payrequest.oktmo}" disabled />
  <label for="summa" disabled>Сумма:</label>
  <input type="text" id="summa" value="${number(value:payrequest.summa,fdigs:2)}" disabled />
  <label for="summands" disabled>Сумма НДС:</label>
  <input type="text" id="summa" value="${number(value:payrequest.summands,fdigs:2)}" disabled />
  <label for="payrequest_destination" ${!iscanedit?'disabled':''}>Назначение платежа:</label>
  <g:textArea id="payrequest_destination" name="destination" value="${payrequest.destination}" disabled="${!iscanedit}" />
  <label for="comment" disabled>Комментарий:</label>
  <g:textArea name="comment" value="${payrequest.comment}" disabled="disabled" />
  <div class="fright">
  <g:if test="${payrequest.file_id}">
    <a class="button" href="${createLink(controller:'payment',action:'showscan',id:payrequest.file_id,params:[code:Tools.generateModeParam(payrequest.file_id)])}" target="_blank">Просмотреть скан реквизитов</a>
  </g:if>
  <g:if test="${iscanedit}">
    <input type="submit" id="taskpayrequest_submit_button" class="button" value="Сохранить"/>
  </g:if>
    <input type="reset" value="Закрыть форму" onclick="jQuery('#payrequestForm').slideUp();"/>
  </div>
  <input type="hidden" name="taskpay_id" value="${taskpay.id}"/>
  <input type="hidden" name="id" value="${payrequest.id}"/>
  <div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  new Autocomplete('tocompany', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_ext_autocomplete")}',
    onSelect: function(value, data){
      var lsData=data.split(';');
      $('tocompany_id').value = lsData[0];
      getBankListByCompany();
    }
  });
</script>