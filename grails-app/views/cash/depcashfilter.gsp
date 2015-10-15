<g:form name="depcashForm" url="[controller:'cash']" target="_blank">
  <label class="auto" for="todate">На дату:</label>
  <g:datepicker class="normal nopad" name="todate" value="${inrequest?.todate?String.format('%td.%<tm.%<tY',inrequest?.todate):''}"/>
  <label class="auto" for="department_id">Отдел:</label>
  <g:if test="${session.user.cashaccess==3}"><g:select name="department_id" value="${inrequest?.department_id}" from="${departments}" optionValue="name" optionKey="id" noSelection="${['0':'все']}" onchange="updatepersspan(this.value)"/></g:if><g:else><g:select name="department_id" value="${inrequest?.department_id?:user.department_id}" from="${departments}" optionValue="name" optionKey="id" disabled="${!user.is_tehdirleader?true:false}" onchange="updatepersspan(this.value)"/></g:else>
  <span id="persspan"><label class="auto" for="pers_id">Сотр.:</label>
  <g:select name="pers_id" value="${inrequest?.pers_id}" from="${perslist}" optionValue="shortname" optionKey="id" noSelection="${['0':'нет']}"/></span>
  <label class="auto" for="depcashtype">Тип:</label>
  <g:select class="mini" name="depcashtype" value="${inrequest?.depcashtype}" from="${['Выдача','Получение','Возврат','Возврат в главную кассу','Начисление','Отчет']}" keys="123459" noSelection="${['-100':'все']}"/>
  <label class="auto" for="depcashclass">Класс:</label>
  <g:select class="mini" name="depcashclass" value="${inrequest?.depcashclass}" from="${['Зарплата','Подотчет','Заем']}" keys="123" noSelection="${['-100':'все']}"/>
  <label class="auto" for="opdatestart">Дата опер. с:</label>
  <g:datepicker class="normal nopad" name="opdatestart" value="${inrequest?.opdatestart?String.format('%td.%<tm.%<tY',inrequest?.opdatestart):''}"/>
  <label class="auto" for="opdateend">по:</label>
  <g:datepicker class="normal nopad" name="opdateend" value="${inrequest?.opdateend?String.format('%td.%<tm.%<tY',inrequest?.opdateend):''}"/>
  <input type="hidden" id="is_depcash" name="is_depcash" value="1" />
  <div class="fright">
    <g:actionSubmit value="PDF" style="display:none" class="spacing" action="list" onclick="\$('form_submit_button').click();return false"/>
    <g:actionSubmit value="XLS" class="spacing" action="depcashXLS"/>
    <input type="reset" class="spacing" value="Сброс"/>
    <g:submitToRemote class="button" id="form_submit_button" value="Показать" url="[action:'list']" update="list"/>
  <g:if test="${session.user.cashaccess==2}">
    <g:link action="depcashrecord" class="button">Новая операция&nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('form_submit_button').click();
  jQuery("#todate").mask("99.99.9999",{placeholder:" "});
  jQuery("#opdatestart").mask("99.99.9999",{placeholder:" "});
  jQuery("#opdateend").mask("99.99.9999",{placeholder:" "});
</script>