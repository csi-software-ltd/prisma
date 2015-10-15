<g:formRemote name="allForm" url="[controller:'catalog',action:'banklist']" update="list">
  <label class="auto" for="bank_id">Бик:</label>
  <input type="text" id="bank_id" name="bank_id" value="${inrequest?.bank_id?:''}" style="width:100px"/>
  <div id="bank_id_autocomplete" class="autocomplete" style="display:none"></div>
  <label class="auto" for="bankname">Название:</label>
  <input type="text" id="bankname" name="bankname" value="${inrequest?.bankname?:''}" style="width:518px" />
  <div id="bankname_autocomplete" class="autocomplete" style="display:none"></div><br />
  <label class="auto" for="is_my">
    <input type="checkbox" id="is_my" name="is_my" value="1" <g:if test="${inrequest?.is_my}">checked</g:if> />
    Наши
  </label>
  <label class="auto" for="is_license">
    <input type="checkbox" id="is_license" name="is_license" value="1" <g:if test="${inrequest?.is_license}">checked</g:if> />
    Без лицензии
  </label>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс" onclick="resetBankFilter()"/>
    <input type="submit" id="form_submit_button" value="Показать"/>
    <g:if test="${user?.group?.is_bankinsert}"><g:link action="bankdetail" class="button">Новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link></g:if>
  </div>
</g:formRemote>
<!--<g:if test="${user?.group?.is_bankinsert}">
  <g:form name="saveBankFileForm" url="[controller:'catalog',action:'parseCSVBankFile']" method="POST" enctype="multipart/form-data" target="upload_target" style="width:600px;position:relative; top:-6px">
    <input type="file" name="file" accept="text/csv" style="margin: 2px 0px 2px 5px; padding: 6px 15px;"/> 
    <a href="javascript:void(0)"><i title="Формат файла: БИК;Название;Корсчет;Город;Дата отзыва лицензии" class="icon-question-sign"></i></a>
    <input type="submit" class="button" value="Загрузить csv файл"/>  
  </g:form>
  <iframe id="upload_target" name="upload_target" style="width:100%;height:43px;border:none;display:none"></iframe>
</g:if>-->
<div class="clear"></div>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('bank_id', {
    serviceUrl:'${resource(dir:"autocomplete",file:"bik_autocomplete")}'
  });  
  new Autocomplete('bankname', {
    serviceUrl:'${resource(dir:"autocomplete",file:"bankname_autocomplete")}'
  });
</script>
