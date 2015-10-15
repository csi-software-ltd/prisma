<label for="founder_pers_name">Фамилия ИО:</label>
<g:if test="${!founder}">
  <span class="input-append">
    <input type="text" class="nopad normal" id="founder_pers_name" name="pers_name"/>
    <span class="add-on" onclick="newPers()"><abbr title="Добавить нового"><i class="icon-plus"></i></abbr></span>
  </span>
</g:if><g:else>
  <input type="text" id="founder_pers_name" disabled value="${pers?.shortname}"/>
</g:else>
<label for="founder_pers_name">Компания:</label>
<g:if test="${!founder}">
  <span class="input-append">
    <input type="text" class="nopad normal" id="founder_company_name" name="company_name"/>
    <span class="add-on" onclick="newCompany()"><abbr title="Добавить новую"><i class="icon-plus"></i></abbr></span>
  </span>
</g:if><g:else>
  <input type="text" id="founder_company_name" disabled value="${holdcompany?.name}"/>
</g:else>
<label for="founder_share">Доля:</label>
<input type="text" id="founder_share" name="share" value="${founder?.share}"/>
<label for="founder_sharesum">Сумма доли в т.р.:</label>
<input type="text" id="founder_sharesum" name="summa" value="${founder?.summa}"/>
<label for="founder_startdate">Дата начала:</label>
<g:datepicker class="normal nopad" name="founder_startdate" value="${String.format('%td.%<tm.%<tY',founder?.startdate?:new Date())}" max="${String.format('%td.%<tm.%<tY',new Date())}"/>
<label for="founder_enddate">Дата окончания:</label>
<g:datepicker class="normal nopad" name="founder_enddate" value="${founder?.enddate?String.format('%td.%<tm.%<tY',founder?.enddate):''}" min="${founder?String.format('%td.%<tm.%<tY',founder.startdate+1):''}"/><br/>
<label for="founder_comment">Комментарий:</label>
<g:textArea style="width:93%" id="founder_comment" name="comment" value="${founder?.comment}" />
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="founderadd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#founderAddForm').slideUp();"/>
</div>
<input type="hidden" name="company_id" value="${company.id}"/>
<input type="hidden" name="id" value="${founder?.id}"/>
<div class="clear" style="padding-bottom:10px"></div>
<hr class="admin" />
<script type="text/javascript">
<g:if test="${!founder}">
  new Autocomplete('founder_pers_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"persname_autocomplete")}'
  });
  new Autocomplete('founder_company_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"space_arendodatel_autocomplete")}'
  });
</g:if>
  jQuery("#founder_startdate").mask("99.99.9999",{placeholder:" "});
  jQuery("#founder_enddate").mask("99.99.9999",{placeholder:" "});
</script>