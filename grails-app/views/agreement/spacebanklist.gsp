﻿<g:select class="${!notauto?'auto':'fullline'}" name="bank_id" value="${banks.size()==1?banks[0].id:''}" from="${banks}" optionValue="name" optionKey="id" noSelection="${['':'не выбран']}"/>