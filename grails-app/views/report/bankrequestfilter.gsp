<g:form name="allForm" url="[controller:controllerName,action:'bankrequest']" target="_blank">
  <label class="auto" for="id">Компания:</label>
  <g:select name="id" from="${companies}" optionKey="company_id" optionValue="cname" onchange="getBankList(this.value,1)"/>
  <label class="auto" for="acc_id">Банк:</label>
  <span id="banklist"><g:select name="acc_id" style="width:480px" from="${banks}" optionKey="id" optionValue="bankname"/></span>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Сформировать" />
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('list').innerHTML='';
</script>