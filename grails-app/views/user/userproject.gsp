<label for="project_id">Проект:</label>
<g:select name="project_id" value="${project?.id?:0}" from="${projects}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}"/>
<div class="clear"></div>
<div class="fright"> 
  <input type="submit" id="addproject_submit_button" class="button" value="Сохранить" />   
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#projectAddForm').slideUp();"/>
</div>
<div class="clear" style="padding-bottom:10px"></div>
<hr class="admin" />
