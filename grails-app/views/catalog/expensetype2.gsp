<label for="name">Название:</label>
<input type="text" class="fullline" id="subname" name="name" value="${expensetype2?.name}" maxlength="50"/>
<div class="fright">
  <input type="hidden" name="expensetype1_id" value="${expensetype2?.expensetype1_id?:inrequest?.expensetype1_id?:0}"/>
  <input type="hidden" name="id" value="${expensetype2?.id}" />
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#updateForm').slideUp();"/>
  <input type="submit" id="update_submit_button" class="button" value="Сохранить" />  
</div>
<div class="clear"></div>
<hr class="admin" />
