<style type="text/css">
  select[id='platperiod_year'] { width: auto; }
  select[id='platperiod_month'] { width: 110px; }
</style>
<g:form name="paymentForm" url="[controller:'payment',action:'paymentlistXLS']" target="_blank">
  <label class="auto" for="paydate">Дата платежа</label>
  <g:datepicker class="normal nopad" name="paydate" value="${inrequest?.paydate?String.format('%td.%<tm.%<tY',inrequest.paydate):''}"/>
  <label class="auto" for="platperiod_month">Период</label>
  <g:datePicker class="auto" name="platperiod" precision="month" value="${perioddate}" default="none" relativeYears="[114-(perioddate?.getYear()?:new Date().getYear())..new Date().getYear()-(perioddate?.getYear()?:new Date().getYear())]" noSelection="${['':'все']}"/>
  <label class="auto" for="modstatus">Статус</label>
  <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['неидентифицированный','идентифицированный']" keys="12" noSelection="${['-100':'не выбран']}"/>
  <label class="auto" for="is_bankmoney">
    <input type="checkbox" id="is_bankmoney" name="is_bankmoney" value="1" <g:if test="${inrequest?.is_bankmoney}">checked</g:if> />
    СС банка
  </label>
  <br/>
  <label class="auto" for="fromcompany">Плат.:</label>
  <input type="text" id="fromcompany" name="fromcompany" value="${inrequest?.fromcompany?:''}"/>
  <label class="auto" for="tocompany">Получ.:</label>
  <input type="text" id="tocompany" name="tocompany" value="${inrequest?.tocompany?:''}" />
  <label class="auto" for="is_fact">
    <input type="checkbox" id="is_fact" name="is_fact" value="1" <g:if test="${inrequest?.is_fact}">checked</g:if> />
    Без факт|тег
  </label>
  <br/>
  <hr class="admin" style="width:650px;float:left"/><a id="expandlink" style="text-decoration:none" href="javascript:void(0)" onclick="toggleaddition()">&nbsp;&nbsp;Развернуть&nbsp;<i class="icon-collapse"></i></a><hr class="admin" style="width:180px;float:right"/>
  <div id="addition" style="display:none;width:945px;clear:both">
    <label class="auto" for="pid">Id:</label>
    <input type="text" class="mini" id="pid" name="pid" value="${inrequest?.pid?:''}"/>
    <label class="auto" for="finstatus">Фин. статус:</label>
    <g:select name="finstatus" class="mini" value="${inrequest?.finstatus}" from="['новый','действующий']" keys="[0,1]" noSelection="${['-1':'не выбран']}"/>
    <label class="auto" for="paytype">Тип:</label>
    <g:select name="paytype" class="mini" value="${inrequest?.paytype}" from="['исходящий','входящий']" keys="[1,2]" noSelection="${[0:'не выбран']}"/>
    <label class="auto" for="internal">Платеж:</label>
    <g:select name="internal" class="mini" value="${inrequest?.internal}" from="['внешний','внутренний']" keys="[1,2]" noSelection="${[0:'все']}"/>
    <label for="paycat">Категория:</label>
    <g:select class="mini" name="paycat" value="${inrequest?.paycat}" from="['договорной', 'бюджетный', 'персональный','прочее', 'банковский', 'счета']" keys="123456" noSelection="${[0:'не выбран']}" onchange="toggleKbk(this.value);togglePers(this.value)"/>
    <label for="platnumber">№ документа:</label>
    <input type="text" id="platnumber" name="platnumber" value="${inrequest?.platnumber?:''}"/>
    <label for="summa">Сумма:</label>
    <input type="text" class="mini" id="summa" name="summa" value="${inrequest?.summa?:''}"/>
    <label for="destination">Назначение:</label>
    <input type="text" style="width:655px" id="destination" name="destination" value="${inrequest?.destination}"/>
    <label class="auto" for="is_dest">
      <input type="checkbox" id="is_dest" name="is_dest" value="1" <g:if test="${inrequest?.is_dest}">checked</g:if> />
      Не пуст.
    </label>
    <br/>
    <label for="frombank">Банк платильщека:</label>
    <input type="text" class="fullline" id="frombank" name="frombank" value="${inrequest?.frombank}"/>
    <label for="kbk" id="kbk_label" <g:if test="${inrequest?.paycat!=2}">disabled</g:if>>КБК:</label>
    <input type="text" id="kbk" name="kbk" value="${inrequest?.kbk?:''}" <g:if test="${inrequest?.paycat!=2}">disabled</g:if>/>    
    <label for="pers" id="pers_label" <g:if test="${inrequest?.paycat!=3}">disabled</g:if>>Физ.лицо:</label>
    <input type="text" id="pers" value="${Pers.get(inrequest?.pers_id?:0)?.name?:''}" <g:if test="${inrequest?.paycat!=3}">disabled</g:if>/>
    <div class="fright">
      <input type="button" class="spacing" value="XLS" onclick="$('paymentForm').submit()"/>
      <input style="display:none" type="submit" class="spacing" value="Показать" onclick="$('form_submit_button').click();return false"/>
    </div>
    <input type="hidden" id="pers_id" name="pers_id" value="${inrequest?.pers_id?:0}"/>

    <hr class="admin" />
  </div>
  <div class="fright">
  <g:if test="${session.user?.group?.is_payedit}">
    <g:remoteLink class="button" style="z-index:1" before="if(!confirm('Вы действительно хотите сформировать фактические платежи по всем показанным позициям?')) return false" url="${[controller:controllerName,action:'createpayrequestAll']}" params="\$('paymentForm').serialize()" onSuccess="\$('form_submit_button').click();">Платежи &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink>
    <a class="button" onclick="setSaldoAll(this)">Расчет &nbsp;<i class="icon-angle-right icon-large"></i></a>
  </g:if>
    <input type="button" class="spacing reset" value="Сброс" onclick="resetPaymentFilter(0)"/>
    <g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'paymentlist']" update="list"/>
  </div>
</g:form>
<g:form name="savePaymentFileForm" url="[controller:'payment',action:'parsePaymentFile']" method="POST" enctype="multipart/form-data" target="upload_target" style="width:500px;position:relative;">
  <input type="file" name="file" accept="application/xml" style="margin: 2px 0px 2px 15px; padding: 6px 15px;"/> 
  <input type="submit" class="button" value="Загрузить"/>  
</g:form>
<iframe id="upload_target" name="upload_target" style="width:100%;height:43px;border:none;display:none"></iframe>
<div id="process_div" style="width:100%;height:auto;margin-left:25px;padding-top:10px;display:none"></div>

<div class="clear"></div>
<script type="text/javascript">
<g:if test="${inrequest?.paytype || inrequest?.paycat || inrequest?.kbk || inrequest?.pers_id || inrequest?.platnumber || inrequest?.pid || inrequest?.internal}">
  toggleaddition();
</g:if>
  new Autocomplete('fromcompany', {
    serviceUrl:'${resource(dir:"autocomplete",file:"fromcompanyname_autocomplete")}'    
  });
  new Autocomplete('tocompany', {
    serviceUrl:'${resource(dir:"autocomplete",file:"tocompanyname_autocomplete")}'    
  });
  new Autocomplete('pers', {
    serviceUrl:'${resource(dir:"autocomplete",file:"persname_autocomplete")}',
    width:252,
    onSelect: function(value, data){
      $('pers_id').value = data;
    }
  });
  new Autocomplete('kbk', {
    serviceUrl:'${resource(dir:"autocomplete",file:"kbk_autocomplete")}',
    width:252
  });
  jQuery("#paydate").mask("99.99.9999",{placeholder:" "});
  if (${!inrequest?.platperiod_month?1:0}) $('platperiod_month').selectedIndex=0;
  $('form_submit_button').click();  
</script>