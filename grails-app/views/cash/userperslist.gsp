<label for="pers_id">Сотрудник:</label>
<g:select name="pers_id" value="" from="${perslist}" optionValue="name" optionKey="id" noSelection="${['0':'не выбран']}"/>