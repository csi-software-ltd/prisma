<label for="okved_name">ОКВЭД:</label>
<input type="text" class="fullline" id="okved_name" name="okved_name" value="${Okved.findByIdIlike(compokved?.okved_id?:'')?.toString()?:''}" <g:if test="${compokved}">readonly="readonly"</g:if>/>
<div id="okvedname_autocomplete" class="fulline autocomplete" style="display:none"></div>
<label for="moddate">Дата изменения:</label>
<g:datepicker class="normal nopad" name="moddate" value="${String.format('%td.%<tm.%<tY',compokved?.moddate?:new Date())}"/>
<label for="comment" class="auto">Комментарий:</label>
<input type="text" id="comments" name="comments" value="${compokved?.comments?:''}" style="width:390px" />
<div class="clear"></div>
<div class="fright">
<g:if test="${compokved}">
  <a class="button" onclick="setOkvedModstatus(${compokved?.modstatus?0:1});">${compokved?.modstatus?'В архив':'Восстановить'} &nbsp;<i class="icon-angle-right icon-large"></i></a>
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#okvedAddForm').slideUp();"/>
  <input type="submit" id="addtookved_submit_button" class="button" value="Сохранить" />  
</div>
<input type="hidden" name="company_id" value="${company.id}" />
<input type="hidden" name="id" value="${compokved?.id}" />
<input type="hidden" name="modstatus" id="okved_modstatus" value="${compokved?compokved?.modstatus:1}" />
<div class="clear" style="padding-bottom:10px"></div>
<hr class="admin" />
<script type="text/javascript">
  jQuery("#moddate").mask("99.99.9999",{placeholder:" "});
  new Autocomplete('okved_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"okvedname_autocomplete")}'
  });
</script>
