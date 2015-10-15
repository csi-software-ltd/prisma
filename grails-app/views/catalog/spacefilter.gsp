<div class="clear"></div>    
<div class="info-box" style="display:none;margin-top:0">
  <span class="icon icon-info-sign icon-3x"></span>
  <ul id="infolistSpace">              
  </ul>
</div>
<div class="error-box" style="display:none">
  <span class="icon icon-warning-sign icon-3x"></span>
  <ul id="errorlistSpace">
    <li></li>
  </ul>
</div>
<div class="fright" id="link">
  <g:if test="${user?.group?.is_spacediredit}"><a class="button" href="javascript:void(0)" onclick="showSpaceWindow()">Новое &nbsp;<i class="icon-angle-right icon-large"></i></a></g:if>
</div>
<div id="createspace" class="tabs" style="display:none">
</div>  
<g:formRemote name="allForm" url="[controller:'catalog',action:'spacelist']" update="list">
  <input type="submit" id="form_submit_button" value="Показать" style="display:none"/>
</g:formRemote>
<div class="clear"></div>
<script type="text/javascript">
  $('form_submit_button').click(); 
</script>
