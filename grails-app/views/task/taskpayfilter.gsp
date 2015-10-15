<g:formRemote name="taskForm" url="[controller:'task',action:'taskpaylist']" update="[success:'list']">
  <label class="auto" for="tid">Код:</label>
  <input class="mini" type="text" id="tid" name="tid" value="${inrequest?.tid}" />
  <label class="auto" for="paygroup">Группа:</label>
  <g:select class="mini" name="paygroup" value="${inrequest?.paygroup}" from="['Бюджет','Кредиты и КП','Аренда','Общая']" keys="1234" noSelection="${['-100':'все']}"/>
<g:if test="${iscanaccept}">
  <label class="auto" for="is_accept">Акцепт:</label>
  <g:select class="mini" name="is_accept" value="${inrequest?.is_accept}" from="['Разрешенные','Неразрешенные']" keys="10" noSelection="${['-100':'все']}"/>
</g:if>
  <label for="termdate">Дата платежа:</label>
  <g:datepicker class="normal nopad" name="termdate" value="${inrequest?.termdate?String.format('%td.%<tm.%<tY',inrequest.termdate):''}"/>
  <label class="auto" for="taskpaystatus">Статус:</label>
  <g:select class="mini" name="taskpaystatus" value="${inrequest?.taskpaystatus}" from="${taskpaystatus}" optionKey="id" optionValue="name" noSelection="${['-2':'не выбран']}"/>
  <label class="auto" for="company">Компания:</label>
  <input type="text" id="company" name="company" value="${inrequest?.company}" />
<g:if test="${isall}">
  <label class="auto" for="executor">Исп:</label>
  <input type="text" id="executor" name="executor" value="${inrequest?.executor}" />
</g:if>
  <div class="fright" align="right">
    <input type="button" class="reset spacing" value="Сброс" onclick="resetdata()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  </div>
</g:formRemote>
<div class="clear"></div>
<script type="text/javascript">
  $('form_submit_button').click();
  jQuery("#termdate").mask("99.99.9999",{placeholder:" "});
  new Autocomplete('company', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companytaskpay_autocomplete")}'
  });
<g:if test="${isall}">
  new Autocomplete('executor', {
    serviceUrl:'${resource(dir:"autocomplete",file:"executortaskpay_autocomplete")}'
  });
</g:if>
</script>