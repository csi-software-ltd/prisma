<div class="clear"></div>    
<div class="info-box" style="display:none;margin-top:0">
  <span class="icon icon-info-sign icon-3x"></span>
  <ul id="infolistHoliday">              
  </ul>
</div>
<div class="error-box" style="display:none">
  <span class="icon icon-warning-sign icon-3x"></span>
  <ul id="errorlistHoliday">
    <li></li>
  </ul>
</div>
<g:formRemote name="allForm" url="[controller:'catalog',action:'holidaylist']" update="list">
  <label class="auto" for="year">Год:</label>
  <g:select name="year" value="${new Date().getYear()+1900}" from="${year}" optionKey="id" optionValue="id" class="mini"/>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Показать" /> 
    <g:if test="${user?.group?.is_holidayedit}"><a class="button" href="javascript:void(0)" onclick="showHolidayWindow()">Новый &nbsp;<i class="icon-angle-right icon-large"></i></a></g:if>   
  </div>
</g:formRemote>
<div id="createHoliday" class="tabs" style="display:none">
</div> 
<div class="clear"></div>
<script type="text/javascript">
  $('form_submit_button').click();
</script>