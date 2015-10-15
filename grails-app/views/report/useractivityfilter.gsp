<g:form name="allForm" controller="${controllerName}" target="_blank">
  <label class="auto" for="reportstart">Дата от:</label>
  <g:datepicker class="normal nopad" name="reportstart" value=""/>
  <label class="auto" for="reportend">Дата до:</label>
  <g:datepicker class="normal nopad" name="reportend" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
  <label class="auto" for="department_id">Отдел:</label>
  <g:select name="department_id" class="mini" from="${departments}" optionValue="name" optionKey="id" noSelection="${['0':'все']}" onchange="updatepersspan(this.value)"/>
  <span id="persspan"><label class="auto" for="pers_id">Сотр.:</label>
  <g:select name="pers_id" class="mini" from="${perslist}" optionValue="shortname" optionKey="id" noSelection="${['0':'все']}"/></span>
  <label class="auto" for="is_active">
    <input type="checkbox" id="is_active" name="is_active" value="1" checked/>
    Только по активным
  </label>
  <div class="fright">
    <g:actionSubmit value="PDF" class="spacing" action="useractivity"/>
    <input type="reset" class="spacing" value="Сброс" onclick="updatepersspan(0)"/>
		<g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'useractivity',params:[viewtype:'table']]" update="list"/>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
</script>