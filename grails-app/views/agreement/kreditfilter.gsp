<g:formRemote name="allForm" url="[controller:controllerName,action:'kredits']" update="list">
  <label class="auto" for="kid">Код</label>
  <input type="text" class="mini" id="kid" name="kid" value="${inrequest?.kid?:''}" />
  <label class="auto" for="inn">ИНН</label>
  <input type="text" class="mini" id="inn" name="inn" value="${inrequest?.inn?:''}" />
  <label class="auto" for="company_name">Компания</label>
  <input type="text" id="company_name" name="company_name" value="${inrequest?.company_name?:''}" />
  <label class="auto" for="modstatus">Статус</label>
  <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['Активные','Архив']" keys="[1,0]"/>
  <label class="auto" for="bankname">Банк:</label>
  <input type="text" id="bankname" style="width:320px" name="bankname" value="${inrequest?.bankname}"/>
  <label class="auto" for="zalogstatus">Залог:</label>
  <g:select class="mini" name="zalogstatus" value="${inrequest?.zalogstatus}" from="['Нет','Есть']" keys="${1..2}" noSelection="${['0':'все']}"/>
  <label class="auto" for="responsible">Ответственный:</label>
  <g:select class="mini" name="responsible" value="${inrequest?.responsible?:0}" from="${users}" optionValue="value" optionKey="key" noSelection="${['0':'все']}"/>
  <label class="auto" for="valuta_id">Валюта:</label>
  <g:select class="mini" name="valuta_id" value="${inrequest?.valuta_id}" from="${Valuta.findAllByModstatus(1)}" optionKey="id" optionValue="name" noSelection="${['0':'любая']}"/>
<g:if test="${is_showreal}">
  <label class="auto" for="is_real">
    <input type="checkbox" id="is_real" name="is_real" value="1" <g:if test="${inrequest?.is_real}">checked</g:if> />
    Реал
  </label>
  <label class="auto" for="is_realtech">
    <input type="checkbox" id="is_realtech" name="is_realtech" value="1" <g:if test="${inrequest?.is_realtech}">checked</g:if> />
    Реалтех
  </label>
  <label class="auto" for="is_tech">
    <input type="checkbox" id="is_tech" name="is_tech" value="1" <g:if test="${inrequest?.is_tech}">checked</g:if> />
    Техн
  </label>
</g:if>
  <label class="auto" for="cessionstatus">
    <input type="checkbox" id="cessionstatus" name="cessionstatus" value="1" <g:if test="${inrequest?.cessionstatus}">checked</g:if> />
    Уступка
  </label>
  <label class="auto" for="is_nocheck">
    <input type="checkbox" id="is_nocheck" name="is_nocheck" value="1" <g:if test="${inrequest?.is_nocheck}">checked</g:if> />
    Не проверен
  </label><br/>
  <div class="fleft">
    <g:link action="kreditpayrequests" class="button">
      Заявки на платежи &nbsp;<i class="icon-angle-right icon-large"></i>
    </g:link>
  </div>
  <div class="fright">
    <input type="button" class="spacing reset" value="Сброс" onclick="resetkreditfilter()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${iscanedit}">
    <g:link action="kredit" class="button">Добавить новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  new Autocomplete('company_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_autocomplete")}'
  });
  new Autocomplete('bankname', {
    serviceUrl:'${resource(dir:"autocomplete",file:"banknamebik_autocomplete")}'
  });
  $('form_submit_button').click();
</script>
