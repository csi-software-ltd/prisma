<g:formRemote name="allForm" url="[controller:'cash',action:'list']" update="list">
  <label class="auto" for="reqdate">На дату:</label>
  <g:datepicker class="normal nopad" name="reqdate" value="${inrequest?.reqdate?String.format('%td.%<tm.%<tY',inrequest?.reqdate):''}"/>
  <label class="auto" for="reqstatus">Статус:</label>
  <g:select class="mini" name="reqstatus" value="${inrequest?.reqstatus?:0}" from="${status}" optionValue="name" optionKey="id" noSelection="${['-100':'все']}"/>
  <input type="hidden" id="is_request" name="is_request" value="1" />
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${session.user.cashaccess==3}">
    <g:link action="addcashrequest" class="button">Новый запрос &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
</script>
