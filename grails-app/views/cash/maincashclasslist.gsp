﻿<label for="maincashclass">Класс:</label>
<g:select name="maincashclass" from="${cashclasses}" noSelection="${['0':'не выбрано']}" optionKey="id" optionValue="name" onchange="toggleagent(this.value)"/>