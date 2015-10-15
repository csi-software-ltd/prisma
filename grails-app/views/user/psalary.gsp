<label for="pdate">Дата назначения:</label>
<g:datepicker class="normal nopad" style="margin-right:108px" name="pdate" value="${psalary?.pdate?String.format('%td.%<tm.%<tY',psalary?.pdate):''}"/>           
<label for="actsalary" id="actsalary_label">Фактическая з.п.:</label>
<input type="text" id="actsalary" name="actsalary" value="${psalary?intnumber(value:psalary?.actsalary?:0):''}" />
<label for="comment">Комментарий:</label>
<input type="text" class="fullline" id="comment" name="comment" value="${psalary?.comment}" />
<input type="hidden" name="id" value="${psalary?.id?:0}"/>
<div class="clear"></div>
<div class="fright">  
  <input type="submit" id="savepersaccount_submit_button" class="button" value="Сохранить" />   
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#psalaryEditForm').slideUp();"/>
</div>
<div class="clear" style="padding-bottom:10px"></div>
<hr class="admin" />
<script type="text/javascript">
  jQuery("#pdate").mask("99.99.9999",{placeholder:" "});
</script>