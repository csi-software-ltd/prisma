<g:formRemote name="allForm" url="[controller:controllerName,action:'mysalary']" update="list">
  <label class="auto" for="repdate">Дата:</label>
  <g:select class="mini" name="repdate" from="${reportdates}" optionKey="keyvalue" optionValue="disvalue" noSelection="${['':'все']}"/>
  <label style="${!isallsal?'display:none':''}" class="auto" for="user_id">Сотрудник:</label>
  <g:select style="${!isallsal?'display:none':''}" name="user_id" value="${inrequest?.user_id?:session.user.id}" from="${users}" optionValue="pers_name" optionKey="id"/>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>