<label for="payrequest_summa">Сумма:</label>
<input type="text" class="auto" id="payrequest_summa" name="summa" value="${number(value:payrequest?.summa?:is_body?defaultdata.summarub:defaultdata?.summapercrub)}"/>
<label for="payrequest_paydate">Дата платежа:</label>
<g:datepicker class="normal nopad" name="payrequest_paydate" value="${String.format('%td.%<tm.%<tY',payrequest?.paydate?:defaultdata?.paydate?:new Date())}"/><br/>
<label for="payrequest_frombank">Банк плательщика:</label>
<g:select class="fullline" id="payrequest_frombank" name="frombank" value="${payrequest?.bankaccount_id?:bank.size()==1?bank[0].id:0}" from="${bank}" optionKey="id" noSelection="${[0:'не выбран']}"/>
<label for="payrequest_destination">Назначение:</label>
<label class="auto" for="payrequest_is_dop">
  <input type="checkbox" id="payrequest_is_dop" name="is_dop" value="1" <g:if test="${payrequest?.is_dop||(defaultdata&&!is_body)}">checked</g:if> />
  Погашение процентов
</label>
<label class="auto" for="payrequest_is_fine">
  <input type="checkbox" id="payrequest_is_fine" name="is_fine" value="1" <g:if test="${payrequest?.is_fine}">checked</g:if> />
  Пеня
</label>
<g:textArea id="payrequest_destination" name="destination" value="${payrequest?.destination}" />
<g:if test="${iscantag}">
<hr class="admin" style="width:756px;float:left"/><a id="tagexpandlink" style="text-decoration:none" href="javascript:void(0)">&nbsp;&nbsp;Тегировать&nbsp;<i class="icon-collapse-top"></i></a><hr class="admin" style="width:70px;float:right"/>
<div id="tagsection" style="width:936px">
  <label for="payrequest_project_id">Проект:</label>
  <g:select id="payrequest_project_id" name="project_id" value="${payrequest?.project_id?:defproject_id}" from="${project}" optionKey="id" optionValue="name" />
  <br/><label for="payrequest_expensetype_id">Доходы-расходы:</label>
  <g:select id="payrequest_expensetype_id" name="expensetype_id" class="fullline" value="${payrequest?.expensetype_id?:0}" from="${expensetype}" optionKey="id" noSelection="${['0':'не выбран']}"/>
  <label for="payrequest_expensetype_name">Доходы-расходы:</label>
  <input type="text" class="fullline" id="payrequest_expensetype_name" value=""/>

  <hr class="admin" />
</div>
</g:if>
<div class="clear"></div>
<div class="fright">
  <input type="button" class="button" value="Создать" onclick="$('payrequest_is_task').value=1;$('payrequestadd_submit_button').click();"/>
  <input type="submit" id="payrequestadd_submit_button" class="button" value="Сохранить" />
  <input type="reset" class="button" value="Отмена" onclick="jQuery('#payrequestAddForm').slideUp();"/>
</div>
<input type="hidden" id="payrequest_is_task" name="is_task" value="0"/>
<input type="hidden" name="agr_id" value="${agr.id}"/>
<input type="hidden" name="id" value="${payrequest?.id?:0}"/>
<div class="clear" style="padding-bottom:10px"></div>
<script type="text/javascript">
	new Autocomplete('payrequest_expensetype_name', {
	  serviceUrl:'${resource(dir:"autocomplete",file:"expensetype_autocomplete")}',
	  onSelect: function(value, data){
	    $('payrequest_expensetype_id').value = data;
	  }
	});
	jQuery("#payrequest_paydate").mask("99.99.9999",{placeholder:" "});
</script>