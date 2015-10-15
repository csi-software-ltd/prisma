<g:formRemote name="allForm" url="[controller:controllerName,action:'avanses']" update="list">
  <label class="auto" for="repdate">Дата:</label>
  <g:select class="mini" name="repdate" value="${inrequest?.repdate?String.format('%td.%<tm.%<tY',inrequest?.repdate):''}" from="${repdates}" optionKey="keyvalue" optionValue="disvalue" noSelection="${['':'все']}"/>
  <label class="auto" for="modstatus">Статус:</label>
  <g:select class="auto" name="modstatus" value="${inrequest?.modstatus}" from="${['Новая','К выплате','Закрыта']}" keys="012" noSelection="${['-100':'все']}"/>
  <label style="${!isalldep?'display:none':''}" class="auto" for="department_id">Отдел:</label>
  <g:select style="${!isalldep?'display:none':''}" name="department_id" value="${inrequest?.department_id?:!isalldep?user.department_id:0}" from="${Department.list()}" optionValue="name" optionKey="id" noSelection="${['-100':'все']}"/>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${iscanincert}">
    <g:link action="addavans" class="button">Новая ведомость &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>