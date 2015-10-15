<label for="agent_id">Агент:</label>
<g:select name="agent_id" from="${agents}" optionKey="agent_id" optionValue="agent_name" noSelection="${['0':'не выбран']}"/>