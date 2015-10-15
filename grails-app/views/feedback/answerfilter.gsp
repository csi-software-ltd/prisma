<g:formRemote name="allForm" url="[controller:'feedback',action:'answers']" update="list">
  <label class="auto" for="feedtype">Тип вопроса:</label>
  <g:select class="mini" name="feedtype" value="${inrequest?.feedtype}" from="${feedtypes}" optionValue="name" optionKey="id" noSelection="${['-100':'все']}"/>
  <label class="auto" for="modstatus">Статус:</label>
  <g:select class="mini" name="modstatus" value="${inrequest?.modstatus}" from="['Новый вопрос','Ответ','FaQ']" keys="012" noSelection="${['-100':'все']}"/>
	<label for="username">Фамилия ИО:</label>
	<input type="text" id="username" name="username" value="${inrequest?.username}"/>
  <label for="keyword">Ключевое слово:</label>
  <input type="text" id="keyword" name="keyword" value="${inrequest?.keyword}"/>
  <label class="auto" for="fid">Код:</label>
  <input type="text" id="fid" class="mini" name="fid" value="${inrequest?.fid}"/>
  <div class="fright">
    <input type="button" class="reset spacing" value="Сброс" onclick="resetAnswerFilter()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('username', {
    serviceUrl:'${resource(dir:"autocomplete",file:"persname_autocomplete")}'
  });
</script>