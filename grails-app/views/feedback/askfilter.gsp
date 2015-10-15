<g:formRemote name="allForm" url="[controller:'feedback',action:'questions']" update="list">
  <label class="auto" for="feedtype">Тип вопроса:</label>
  <g:select class="mini" name="feedtype" value="${inrequest?.feedtype}" from="${feedtypes}" optionValue="name" optionKey="id" noSelection="${['-100':'все']}"/>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Показать" />
    <g:link action="newquestion" class="button">Новый вопрос &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>