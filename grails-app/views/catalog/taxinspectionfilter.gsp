<g:formRemote name="allForm" url="[controller:'catalog',action:'taxinspectionlist']" update="list">
  <label class="auto" for="taxinspection_id">Номер:</label>
  <input type="text" class="mini" id="taxinspection_id" name="taxinspection_id" value="${inrequest?.taxinspection_id?:''}" />
  <br/>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Показать" />
    <g:if test="${user?.group?.is_taxinsert}"><g:link action="taxdetail" class="button">Новая &nbsp;<i class="icon-angle-right icon-large"></i></g:link></g:if>
  </div>
</g:formRemote>
<g:if test="${user?.group?.is_taxinsert}">
<g:form name="saveTaxinspectionFileForm" url="[controller:'catalog',action:'parseCSVTaxinspectionFile']" method="POST" enctype="multipart/form-data" target="upload_target" style="width:600px;position:relative;top:-5px">
  <input type="file" name="file" accept="text/csv" style="margin: 2px 0px 2px 5px; padding: 6px 15px;"/>
  <a href="javascript:void(0)"><i title="Формат файла: Номер;Название;Адрес;Телефон;Район" class="icon-question-sign"></i></a>  
  <input type="submit" class="button" value="Загрузить csv файл"/>  
</g:form>
<iframe id="upload_target" name="upload_target" style="width:100%;height:40px;border:none;display:none"></iframe>
</g:if>
<div class="clear"></div>
<script type="text/javascript">
  $('form_submit_button').click();
</script>