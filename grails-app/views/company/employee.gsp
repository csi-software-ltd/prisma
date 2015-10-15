<label for="employee_pers_name">Фамилия ИО:</label>
<g:if test="${!employee?.pers_id}">
  <span class="input-append">
    <input type="text" class="nopad normal" id="employee_pers_name" name="pers_name"/>
    <span class="add-on" onclick="newPers()"><abbr title="Добавить нового"><i class="icon-plus"></i></abbr></span>
  </span>
  <div id="persname_autocomplete" class="autocomplete" style="display:none"></div>
</g:if><g:else>
  <input type="text" id="employee_pers_name" disabled value="${pers?.shortname}"/>
</g:else>
<g:if test="${session.user.confaccess>0}">
<label for="employee_salary">Офиц. оклад:</label>
<input type="text" class="auto" id="employee_salary" name="salary" ${session.user.confaccess==1?'readonly':''} value="${employee?.salary}"/>
</g:if><g:else><br/></g:else>
<label for="employee_composition_id">Должность:</label>
<select id="employee_composition_id" name="composition_id" onchange="togglevaliddate()">
  <option class="type_0" value="0">не выбрана</option>
<g:each in="${compositions}">
  <option class="type_${it.position_id}" value="${it.id}" <g:if test="${employee?.composition_id==it.id}">selected</g:if>>${it.name}</option>
</g:each>
</select>
<span id="gd_valid_container" style="${employee?.position_id!=1?'display:none':''}">
  <label for="employee_gd_valid">Срок полномочий:</label>
<g:if test="${employee}">
  <g:datepicker class="normal nopad" name="employee_gd_valid" value="${employee?.gd_valid?String.format('%td.%<tm.%<tY',employee.gd_valid):''}"/>
</g:if><g:else>
  <input type="text" class="auto" id="employee_gd_valid" name="gd_valid_years" placeholder="лет"/>
</g:else>
  <label for="employee_industrywork">Работа в отрасли:</label>
  <input type="text" class="auto" id="employee_industrywork" name="industrywork" value="${employee?.industrywork}"/>
  <label for="employee_prevwork">Пред. работа:</label>
  <input type="text" class="auto" id="employee_prevwork" name="prevwork" value="${employee?.prevwork}"/>
</span><br/>
<label for="employee_jobstart">Дата начала работы:</label>
<g:datepicker class="normal nopad" name="employee_jobstart" value="${employee?.jobstart?String.format('%td.%<tm.%<tY',employee.jobstart):''}"/>
<label for="employee_jobend">Дата увольнения:</label>
<g:datepicker class="normal nopad" name="employee_jobend" value="${employee?.jobend?String.format('%td.%<tm.%<tY',employee.jobend):''}" max="${String.format('%td.%<tm.%<tY',new Date())}"/><br/>
<label for="employee_comment">Комментарий:</label>
<g:textArea name="comment" id="employee_comment" value="${employee?.comment}" />
<g:if test="${employee&&iscantag}">
  <div class="clear" style="padding-top:10px"></div>
  <hr class="admin" style="width:755px;float:left"/><a id="emptagexpandlink" style="text-decoration:none" href="javascript:void(0)" onclick="toggleemptagsection()">&nbsp;&nbsp;Тегировать&nbsp;<i class="icon-collapse"></i></a><hr class="admin" style="width:65px;float:right"/>
  <div id="emptagsection" style="display:none;width:100%">
    <label for="employee_tagproject">Проект:</label>
    <g:select id="employee_tagproject" name="tagproject" value="${employee?.tagproject}" from="${projects}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}"/>
    <label for="employee_tagclient">Клиент:</label>
    <g:select id="employee_tagclient" name="tagclient" value="${employee?.tagclient}" from="${Client.list()}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}"/>
  <g:if test="${employee.position_id!=3}">
    <label for="employee_tagexpensemain">Тип расходов:</label>
    <g:select id="employee_tagexpensemain" name="tagexpensemain" value="${employee?.tagexpensemain}" from="${expensetype}" optionKey="id" noSelection="${['0':'не выбран']}"/>
  </g:if>
  <g:if test="${employee.position_id!=4}">
    <label for="employee_tagexpenseadd">Тип расходов,доп.:</label>
    <g:select id="employee_tagexpenseadd" name="tagexpenseadd" value="${employee?.tagexpenseadd}" from="${expensetype}" optionKey="id" noSelection="${['0':'не выбран']}"/>
  </g:if>
    <br/><label for="employee_tagcomment">Комментарий к тегированию:</label>
    <g:textArea id="employee_tagcomment" name="tagcomment" id="tagcomment" value="${employee?.tagcomment}" />
    <hr class="admin" />
  </div>
</g:if>
<div class="clear" style="padding-bottom:10px"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="eployeeadd_submit_button" class="button" value="Сохранить" />
<g:if test="${employee?.modstatus==0}">
  <input type="button" class="button" value="Восстановить" onclick="$('employee_status_detail').value=0;$('eployeeadd_submit_button').click();" />
</g:if>
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#employeeAddForm').slideUp();"/>
</div>
<input type="hidden" name="company_id" value="${company.id}"/>
<input type="hidden" name="id" value="${employee?.id}"/>
<input type="hidden" id="employee_status_detail" name="modstatus" value="1"/>
<div class="clear" style="padding-bottom:10px"></div>
<hr class="admin" />
<script type="text/javascript">
  isEmpTagOpen = false;
  jQuery("#employee_jobstart").mask("99.99.9999",{placeholder:" "});
  jQuery("#employee_jobend").mask("99.99.9999",{placeholder:" "});
<g:if test="${employee}">
  jQuery("#employee_gd_valid").mask("99.99.9999",{placeholder:" "});
</g:if><g:else>
  new Autocomplete('employee_pers_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"persname_autocomplete")}'
  });
</g:else>
</script>