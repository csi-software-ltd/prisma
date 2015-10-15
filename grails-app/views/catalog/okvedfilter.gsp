<g:formRemote name="allForm" url="[controller:'catalog',action:'okvedlist']" update="list">
  <label class="auto" for="okved_id">Код:</label>
  <input type="text" id="okved_id" name="id" value="" class="mini"/>
  <label class="auto" for="name">Название:</label>
  <input type="text" id="name" name="name" value="" />
  <label class="auto" for="razdel">Раздел:</label>
  <input type="text" id="razdel" name="razdel" value="" />
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Показать" />   
  </div>
</g:formRemote>
<g:if test="${user?.group?.is_okvedinsert}">
  <g:form name="saveOkvedFileForm" url="[controller:'catalog',action:'parseCSVOkvedFile']" method="POST" enctype="multipart/form-data" target="upload_target" style="width:600px;position:relative;top:-6px">
    <input type="file" name="file" accept="text/csv" style="margin: 2px 0px 2px 5px; padding: 6px 15px;"/> 
    <a href="javascript:void(0)"><i title="Формат файла: Код;Название;Раздел;Подраздел;Статус" class="icon-question-sign"></i></a>
    <input type="submit" class="button" value="Загрузить csv файл"/>  
  </g:form>
  <iframe id="upload_target" name="upload_target" style="width:100%;height:40px;border:none;display:none"></iframe>
</g:if>  
<div class="clear"></div>
<script type="text/javascript">
  $('form_submit_button').click();
</script>