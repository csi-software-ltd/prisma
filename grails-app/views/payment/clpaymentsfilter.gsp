<g:formRemote name="allForm" url="[controller:controllerName,action:'clientpayments']" update="list">
  <label class="auto" for="clid">Код:</label>
  <input type="text" class="mini" id="clid" name="clid" value="${inrequest?.clid?:''}" />
  <label class="auto" for="company_name">Компания:</label>
  <input type="text" id="company_name" name="company_name" value="${inrequest?.company_name?:''}" />
  <div id="companyname_autocomplete" class="autocomplete" style="display:none"></div>
  <label class="auto" for="paytype">Тип</label>
  <g:select class="mini" name="paytype" value="${inrequest?.paytype}" from="['Исходящий', 'Входящий', 'Внутренний', 'Списание', 'Пополнение', 'Абон. плата', 'Откуп', 'Комиссия', 'Возврат комиссии','Связанный входящий','Внешний']" keys="[1,2,3,4,5,7,8,9,0,10,11]" noSelection="${['-100':'все']}"/>
  <label class="auto" for="is_noinner">
    <input type="checkbox" id="is_noinner" name="is_noinner" value="1" <g:if test="${inrequest?.is_noinner!=0}">checked</g:if> />
    Без внутренних
  </label><br/>
  <label class="auto" for="paydate_start">Период платежей от:</label>
  <g:datepicker class="normal nopad" name="paydate_start" value="${inrequest?.paydate_start?String.format('%td.%<tm.%<tY',inrequest.paydate_start):''}"/>
  <label class="auto" for="paydate_end">до:</label>
  <g:datepicker class="normal nopad" name="paydate_end" value="${inrequest?.paydate_end?String.format('%td.%<tm.%<tY',inrequest.paydate_end):''}"/>
  <label class="auto" for="modstatus">Статус исполнения</label>
  <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['Заявки','Выполнены']" keys="12" noSelection="${['0':'все']}"/>
  <label class="auto" for="client_id">Клиент:</label>
  <g:select name="client_id" value="${inrequest?.client_id}" from="${clients}" optionKey="id" optionValue="name" noSelection="${['0':'все']}" onchange="getSubclientsList(this.value)"/>
  <label for="subclient_id">Подклиент:</label>
  <span id="subclientslist"><g:select name="subclient_id" value="${inrequest?.subclient_id}" from="${subclients}" optionKey="id" optionValue="name" noSelection="${['0':'нет']}" onchange="togglesubcomsection(this.value)"/></span>
  <label class="auto" for="is_repayment">
    <input type="checkbox" id="is_repayment" name="is_repayment" value="1" <g:if test="${inrequest?.is_repayment}">checked</g:if> />
    Со списаниями
  </label>
  <div class="fright">
  <g:if test="${iscancreatetask}">
    <g:remoteLink class="button" url="${[controller:controllerName,action:'createclienttask']}">Сформировать новые задания &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink>
  </g:if>
  <g:if test="${iscandeal}">
    <input type="button" class="spacing" value="Сформировать сделки" onclick="$('createprequests_form_submit_button').click()"/>
  </g:if>
    <input type="button" class="reset spacing" value="Сброс" onclick="resetclfilter()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${iscannew}">
    <g:link action="newclientpayment" class="button">Добавить новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  jQuery("#paydate_start").mask("99.99.9999",{placeholder:" "});
  jQuery("#paydate_end").mask("99.99.9999",{placeholder:" "});
  new Autocomplete('company_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_autocomplete")}'
  });
  $('form_submit_button').click();
</script>