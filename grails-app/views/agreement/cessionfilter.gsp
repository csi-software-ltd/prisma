<g:formRemote name="allForm" url="[controller:controllerName,action:'cessions']" update="list">
  <label class="auto" for="inn">ИНН:</label>
  <input type="text" class="mini" id="inn" name="inn" value="${inrequest?.inn?:''}" />
  <label class="auto" for="company_name">Компания:</label>
  <input type="text" id="company_name" name="company_name" value="${inrequest?.company_name?:''}" />
  <div id="companyname_autocomplete" class="autocomplete" style="display:none"></div>
  <label class="auto" for="agr_id">Код договора:</label>
  <input type="text" class="mini" id="agr_id" name="agr_id" value="${inrequest?.agr_id?:''}" />
  <label class="auto" for="bank_id">Банк кредитор:</label>
  <g:select class="mini" name="bank_id" value="${inrequest?.bank_id?:''}" from="${banks}" optionKey="id" noSelection="${['':'любой']}"/>
  <label class="auto" for="valuta_id">Валюта:</label>
  <g:select class="auto" name="valuta_id" value="${inrequest?.valuta_id}" from="${Valuta.findAllByModstatus(1)}" optionKey="id" optionValue="name" noSelection="${['0':'любая']}"/>
  <label class="auto" for="changetype">Класс договора:</label>
  <g:select class="auto" name="changetype" value="${inrequest?.changetype}" from="['С внешней','На внешнюю','Внутренняя смена']" keys="${1..3}" noSelection="${['0':'все']}"/>
  <label class="auto" for="modstatus">Статус:</label>
  <g:select name="modstatus" value="${inrequest?.modstatus}" from="['Активные','Архив']" keys="[1,0]"/>
  <div class="fright">
    <input type="button" class="spacing reset" value="Сброс" onclick="resetcessionfilter()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${iscanedit}">
    <g:link action="cession" class="button">Добавить новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('company_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"companyname_autocomplete")}'
  });
</script>