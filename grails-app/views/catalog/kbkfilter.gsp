<g:formRemote name="allForm" url="[controller:'catalog',action:'kbklist']" update="list">
  <label class="auto" for="kbksearch">Код:</label>
  <input type="text" id="kbksearch" name="kbksearch" value=""/>  
  <label class="auto" for="kbkrazdel_id">Раздел:</label>
  <g:select name="kbkrazdel_id" from="${razdel}" optionKey="id" optionValue="name" noSelection="[0:'все']"/>
  <br/>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Показать" />   
  </div>
</g:formRemote>
<g:if test="${user?.group?.is_kbkinsert}">
  <g:form name="saveKbkFileForm" url="[controller:'catalog',action:'parseCSVKbkFile']" method="POST" enctype="multipart/form-data" target="upload_target" style="width:600px;position:relative;top:-5px">
    <input type="file" name="file" accept="text/csv" style="margin: 2px 0px 2px 5px; padding: 6px 15px;"/> 
    <a href="javascript:void(0)"><i title="Формат файла: Название налога;Раздел;Код налога;Код пени;Код штрафа;Код налога Код пени Код штрафа(без пробелов внутри кодов, с пробелами между кодами);Номер раздела" class="icon-question-sign"></i></a>
    <input type="submit" class="button" value="Загрузить csv файл"/>
  </g:form>
  <iframe id="upload_target" name="upload_target" style="width:100%;height:40px;border:none;display:none"></iframe>
</g:if>  
<div class="clear"></div>
<script type="text/javascript">
  $('form_submit_button').click();
</script>
