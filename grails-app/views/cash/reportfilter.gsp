<g:formRemote name="allForm" url="[controller:'cash',action:'list']" update="list">
  <label class="auto" for="repdate">На дату:</label>
  <g:datepicker class="normal nopad" name="repdate" value="${inrequest?.repdate?String.format('%td.%<tm.%<tY',inrequest?.repdate):''}"/>
  <label for="executor_name">Подотчетное лицо:</label>
  <input type="text" id="executor_name" name="executor_name" value="${inrequest?.executor_name}"/>
  <label class="auto" for="repstatus">Статус:</label>
  <g:select class="mini" name="repstatus" value="${inrequest?.repstatus}" from="${reportstatus}" optionValue="name" optionKey="id" noSelection="${['-100':'все']}"/>
  <label class="auto" for="exprazdel_id">Раздел:</label>
  <g:select class="mini" name="exprazdel_id" value="${inrequest?.exprazdel_id}" from="${exprazdel}" optionValue="name" optionKey="id" noSelection="${['0':'все']}" onchange="getpodrazdel(this.value)"/>
  <label class="auto" for="exppodrazdel_id">Подраздел:</label>
  <span id="exppodrazdelsection"><g:select class="mini" name="exppodrazdel_id" value="${inrequest?.exppodrazdel_id}" from="${exppodrazdel}" optionValue="name" optionKey="id" noSelection="${['0':'все']}" onchange="getexptypes(this.value)"/></span>
  <label class="auto" for="expensetype_id">Статьи расходов:</label>
  <span id="exptypesection"><g:select name="expensetype_id" value="${inrequest?.expensetype_id}" from="${expensetypes}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/></span>
  <label class="auto" for="project_id">Проект:</label>
  <g:select name="project_id" value="${inrequest?.project_id}" from="${Project.list()}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/>
  <input type="hidden" id="is_reports" name="is_reports" value="1" />
  <div class="fright">
    <input type="button" class="reset spacing" value="Сброс" onclick="resetreportfilter()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${session.user.cashaccess!=5}">
    <g:link action="addcashreport" class="button">Новый отчет &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  new Autocomplete('executor_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"persname_autocomplete")}'
  });
  $('form_submit_button').click();
</script>