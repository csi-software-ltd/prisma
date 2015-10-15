<label for="indepositprjoperation_project_from">Проект откуда:</label>
<g:select id="indepositprjoperation_project_from" name="project_from" value="" from="${projects}" optionValue="name" optionKey="id" noSelection="${['0':'не выбран']}"/>
<label for="indepositprjoperation_project_to">Проект куда:</label>
<g:select id="indepositprjoperation_project_to" name="project_to" value="" from="${projects}" optionValue="name" optionKey="id" noSelection="${['0':'не выбран']}"/>
<label for="indepositprjoperation_summa">Сумма:</label>
<input type="text" id="indepositprjoperation_summa" name="summa" value=""/>
<label for="indepositprjoperation_operationdate">Дата операции:</label>
<g:datepicker class="normal nopad" name="indepositprjoperation_operationdate" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="indepositprjoperationadd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#indepositprjoperationAddForm').slideUp();"/>
</div>
<input type="hidden" name="deposit_id" value="${deposit.id}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#indepositprjoperation_operationdate").mask("99.99.9999",{placeholder:" "});
</script>