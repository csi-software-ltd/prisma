﻿<g:select class="nopad normal" name="tobank" value="" from="${banks}" optionKey="id" optionValue="name" noSelection="${['':'не выбран']}" style="width:630px"/> 
<span class="add-on" onclick="openCompany()"><abbr title="Добавить банк получателя"><i class="icon-plus"></i></abbr></span> 
<span class="add-on" onclick="getBankListByCompany()"><abbr title="Обновить банк получателя"><i class="icon-refresh"></i></abbr></span>