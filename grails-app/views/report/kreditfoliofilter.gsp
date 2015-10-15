<g:form name="allForm" url="[controller:controllerName,action:'kreditfolio']" target="_blank">
  <label class="auto" for="bank_id">Банк кредитор:</label>
  <g:select name="bank_id" from="${banks}" optionKey="id" noSelection="${['':'любой']}" onchange="getCompanyList(this.value)"/>
  <span id="companylist"><label class="auto" for="company_id">Заемщик:</label>
  <g:select class="mini" name="company_id" from="${companies}" optionKey="id" optionValue="name" noSelection="${[0:'все']}"/></span>
  <label class="auto" for="is_nolicense">
    <input type="checkbox" id="is_nolicense" name="is_nolicense" value="1" />
    по отозв. лицензиям
  </label><br/>
  <label class="auto" for="kreditfoliodate">Дата портфеля:</label>
  <g:datepicker class="normal nopad" name="kreditfoliodate" value="${String.format('%td.%<tm.%<tY',new Date())}"/>
  <label class="auto" for="kreditdate_start">Погашение с</label>
  <g:datepicker class="normal nopad" name="kreditdate_start"/>
  <label class="auto" for="kreditdate_end">по</label>
  <g:datepicker class="normal nopad" name="kreditdate_end"/>
  <label class="auto" for="is_agr">
    <input type="checkbox" id="is_agr" name="is_agr" value="1" />
    Договор
  </label><br/>
  <label class="auto" for="zalog_id">Залог:</label>
  <g:select class="mini" name="zalog_id" from="${[[id:0,name:'любой']]+Zalogtype.list()}" optionKey="id" optionValue="name" noSelection="${['-100':'все']}"/>
  <label class="auto" for="responsible">Ответственный:</label>
  <g:select class="mini" name="responsible" from="${users}" optionValue="value" optionKey="key" noSelection="${['0':'все']}"/>
  <label class="auto" for="is_debt">
    <input type="checkbox" id="is_debt" name="is_debt" value="1" />
    Просроченные
  </label>
  <label class="auto" for="is_active">
    <input type="checkbox" id="is_active" name="is_active" value="1" />
    Только действующие
  </label>
  <br/><label class="auto" for="activitystatus">Статус активности:</label>
  <g:select name="activitystatus" value="${inrequest?.activitystatus}" from="['действующий', 'реорганизация - планируется', 'реорганизация', 'банкротство - планируется', 'банкротство', 'ликвидация - планируется', 'ликвидация']" keys="1235689" noSelection="${['0':'все']}"/>
  <div class="fright">
    <input type="button" class="reset spacing" value="Сброс" onclick="resetKreditfolioFilter()"/>
    <input type="submit" id="form_submit_button" value="Сформировать" />
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('list').innerHTML='';
</script>