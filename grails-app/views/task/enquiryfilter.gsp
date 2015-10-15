<g:form name="allForm" controller="${controllerName}" target="_blank">
  <label class="auto" for="company_name">Компания:</label>
  <input type="text" id="company_name" name="company_name" value="${inrequest?.company_name?:''}" />
  <div id="companyname_autocomplete" class="autocomplete" style="display:none"></div>
  <label class="auto" for="bank_id">Банк кредитор:</label>
  <g:select class="mini" name="bank_id" value="${inrequest?.bank_id?:''}" from="${banks}" optionKey="id" noSelection="${['':'любой']}"/>
  <label class="auto" for="ondate">На дату</label>
  <g:datepicker class="normal nopad" name="ondate" value="${inrequest?.ondate?String.format('%td.%<tm.%<tY',inrequest.ondate):''}"/><br/>
  <label class="auto" for="taxinspection_id">ИФНС:</label>
  <g:select name="taxinspection_id" value="${inrequest?.taxinspection_id?:''}" from="${taxinspections}" optionKey="id" noSelection="${['':'любой']}"/>
  <label class="auto" for="inputdate_start">Дата создания от:</label>
  <g:datepicker class="normal nopad" name="inputdate_start" value="${inrequest?.inputdate_start?String.format('%td.%<tm.%<tY',inrequest.inputdate_start):''}"/>
  <label class="auto" for="inputdate_end">до:</label>
  <g:datepicker class="normal nopad" name="inputdate_end" value="${inrequest?.inputdate_end?String.format('%td.%<tm.%<tY',inrequest.inputdate_end):''}"/>
  <label class="auto" for="termdate">Срок ответа</label>
  <g:datepicker class="normal nopad" name="termdate" value="${inrequest?.termdate?String.format('%td.%<tm.%<tY',inrequest.termdate):''}"/>
  <label for="whereto">Тип запроса:</label>
  <g:select class="mini" name="whereto" value="${inrequest?.whereto}" from="['В налоговую','В банк']" keys="12" noSelection="${['-100':'все']}"/>
  <label for="modstatus">Статус запроса:</label>
  <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['Новый запрос','Запрос принят','Справка выдана','Требуется перезапрос','Отказ']" keys="[0,1,2,3,-1]" noSelection="${['-100':'все']}"/>
  <div class="fright">
    <input type="button" class="reset spacing" value="Сброс" onclick="resetEnquiryForm()"/>
    <g:actionSubmit value="XLS" class="spacing" action="enquiries"/>
    <g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'enquiries',params:[is_table:1]]" update="list"/>
  <g:if test="${iscanadd}">
    <g:link action="addenquiry" class="button">Новая заявка на справку &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('company_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_autocomplete")}'
  });
</script>