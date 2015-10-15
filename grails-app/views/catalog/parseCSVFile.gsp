<div id="fileUploadResult">
<g:if test="${code==1}">
  Нет входных данных
</g:if>
<g:if test="${code==2}">
  Тип файла не поддерживается
</g:if>
<g:if test="${code==3}">
  ${complete.toString()}  из  ${total}  импортировано. ${(notimport?('Не импортированы следующие строки: '+notimport.toString()+'.'):'')}  
  ${(notneededsize?('Несоответствие кол-ва полей в следующих строках '+notneededsize.toString()+'.'):'')}
</g:if>
<g:if test="${code==4}">
  Неверное имя файла
</g:if>
</div>
<script language="javascript" type="text/javascript">
	window.top.document.getElementById('upload_target').show();
	window.top.document.getElementById('form_submit_button').click();
	//window.top.window.alert("${result}");
</script>

