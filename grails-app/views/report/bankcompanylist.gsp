<label class="auto" for="company_id">Заемщик:</label>
<g:select class="mini" name="company_id" from="${companies}" optionKey="id" optionValue="name" noSelection="${[0:'все']}"/>