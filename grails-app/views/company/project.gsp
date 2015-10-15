<label for="project_id">Проект:</label>
<g:select class="auto" id="project_id" name="project_id" from="${projects}" optionValue="name" optionKey="id" noSelection="${['0':'не выбран']}"/>
<div class="clear"></div>
<div class="fright">
  <input type="submit" id="submit_button" class="button" value="Сохранить" />
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#projectAddForm').slideUp();"/>
</div>
<input type="hidden" name="company_id" value="${company.id}"/>
<div class="clear" style="padding-bottom:10px"></div>
<hr class="admin" />