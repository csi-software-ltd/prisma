<label for="complicense_name">Название:</label>
<input type="text" class="fullline" id="complicense_name" name="name" value="${complicense?.name}"/><br/>
<label for="complicense_ldate">Дата выдачи:</label>
<g:datepicker class="normal nopad" name="complicense_ldate" value="${complicense?.ldate?String.format('%td.%<tm.%<tY',complicense.ldate):''}"/>
<label for="complicense_validity">Срок действия:</label>
<g:datepicker class="normal nopad" name="complicense_validity" value="${complicense?.validity?String.format('%td.%<tm.%<tY',complicense.validity):''}"/><br/>
<label for="complicense_nomer">Номер:</label>
<input type="text" id="complicense_nomer" name="nomer" value="${complicense?.nomer}"/>
<label for="complicense_formnumber">Номер бланка:</label>
<input type="text" id="complicense_formnumber" name="formnumber" value="${complicense?.formnumber}"/>
<label for="complicense_authority">Выдавший орган:</label>
<input type="text" id="complicense_authority" name="authority" value="${complicense?.authority}"/>
<label for="complicense_status">Статус:</label>
<g:select id="complicense_status" name="modstatus" value="${complicense?.modstatus}" from="['Активная','Неактивная']" keys="10"/>
<label for="complicense_comment">Комментарий:</label>
<g:textArea id="complicense_comment" name="comment" value="${complicense?.comment}" />
<div class="clear" style="padding-bottom:10px"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="eployeeadd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#complicenseAddForm').slideUp();"/>
</div>
<input type="hidden" name="company_id" value="${company.id}"/>
<input type="hidden" name="id" value="${complicense?.id}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#complicense_ldate").mask("99.99.9999",{placeholder:" "});
  jQuery("#complicense_validity").mask("99.99.9999",{placeholder:" "});
</script>