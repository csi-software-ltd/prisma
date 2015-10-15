<g:if test="${bank}">
  <label for="actsaldo" disabled>Факт. остаток:</label>
  <input type="text" id="actsaldo" disabled value="${intnumber(value:curbankaccount?.actsaldo?:0)}"/>  
  <label for="actsaldodate" disabled>Дата факт остатка:</label>
  <input type="text" id="actsaldodate" disabled value="${curbankaccount?.actsaldodate?String.format('%td.%<tm.%<tY',curbankaccount?.actsaldodate):''}" />
  <label for="accsaldo" disabled>Резерв по счету:</label>
  <input type="text" id="accsaldo" disabled value="${number(value:accsaldo)}"/>
  <label for="compsaldo" disabled>По компании:</label>
  <input type="text" id="compsaldo" disabled value="${number(value:compsaldo)}"/>
  <label for="cursaldo" disabled>Текущий остаток:</label>
  <input type="text" id="cursaldo" disabled value="${number(value:cursaldo?:0)}"/>
  <label for="totalsaldo" disabled>Итоговый остаток:</label>
  <input type="text" id="totalsaldo" disabled value="${number(value:totalsaldo?:0)}"/>
</g:if>