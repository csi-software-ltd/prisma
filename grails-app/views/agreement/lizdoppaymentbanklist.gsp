﻿<g:select id="lizdoppayment_tobank" class="fullline" name="tobank" value="${banks.size()==1?banks[0].id:''}" from="${banks}" optionKey="id" optionValue="name" noSelection="${['':'не выбран']}"/>