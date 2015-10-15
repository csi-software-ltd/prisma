<g:each in="${bankaccount}" var="item" status="i">
  <label for="bankaccount_id${item.id}" style="margin-bottom:15px">
    <g:radio style="margin-bottom:15px" name="frombank" id="bankaccount_id${item.id}" value="${item.id}" onclick="getBankaccount(this.value)"/>
    <input type="text" value="${Bank.get(item.bank_id)?.name}" readonly onclick="$('bankaccount_id${item.id}').click()"/>
    <input type="text" value="${g.account(value:item.schet)}" readonly onclick="$('bankaccount_id${item.id}').click()"/>
  </label><br/>
</g:each>