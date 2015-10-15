<label for="vacancy_composition_id">Должность:</label>
<g:select id="vacancy_composition_id" class="fullline" name="composition_id" from="${compositions}" value="${vacancy?.composition_id}" optionValue="name" optionKey="id" noSelection="${['0':'не выбрана']}"/>
<label for="vacancy_numbers">Количество:</label>
<input type="text" id="vacancy_numbers" name="numbers" value="${vacancy?.numbers}"/>
<label for="vacancy_salary">Оклад:</label>
<input type="text" id="vacancy_salary" name="salary" value="${vacancy?.salary}"/>
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="vacancyadd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#vacancyAddForm').slideUp();"/>
</div>
<input type="hidden" name="company_id" value="${company.id}"/>
<input type="hidden" name="id" value="${vacancy?.id}"/>
<div class="clear" style="padding-bottom:10px"></div>
<hr class="admin" />