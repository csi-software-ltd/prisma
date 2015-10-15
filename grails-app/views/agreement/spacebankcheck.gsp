<label for="spacebankcheck_bankname">Банк:</label>
<input type="text" class="fullline" id="spacebankcheck_bankname" name="bank" value="${bank?.name}"/>
<label for="spacebankcheck_checktype_id">Тип проверки:</label>
<g:select id="spacebankcheck_checktype_id" name="checktype_id" value="${spacebankcheck?.checktype_id}" from="${bankchecktypes}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}"/>
<label for="spacebankcheck_checkdate">Дата проверки:</label>
<g:datepicker class="normal nopad" name="spacebankcheck_checkdate" value="${String.format('%td.%<tm.%<tY',spacebankcheck?.checkdate?:new Date())}"/>
<label for="spacebankcheck_contactinfo">Контактная информация:</label>
<g:textArea id="spacebankcheck_contactinfo" name="contactinfo" value="${spacebankcheck?.contactinfo}" />
<label for="spacebankcheck_comment">Комментарий:</label>
<g:textArea id="spacebankcheck_comment" name="comment" value="${spacebankcheck?.comment}" />
<div class="clear"></div>
<div class="fright">
<g:if test="${iscanedit}">
  <input type="submit" id="spacebankcheckadd_submit_button" class="button" value="Сохранить" />
</g:if>
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#spacebankcheckAddForm').slideUp();"/>
</div>
<input type="hidden" name="space_id" value="${space.id}"/>
<input type="hidden" name="id" value="${spacebankcheck?.id?:0}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
  jQuery("#spacebankcheck_checkdate").mask("99.99.9999",{placeholder:" "});
  new Autocomplete('spacebankcheck_bankname', {
    serviceUrl:'${resource(dir:"autocomplete",file:"banknamebik_autocomplete")}'
  });
</script>