﻿<g:select class="fullline" name="${type?'zbank_id':'ebank_id'}" value="${banks.size()==1?banks[0].id:''}" from="${banks}" optionValue="name" optionKey="id" noSelection="${['':'не выбран']}"/>