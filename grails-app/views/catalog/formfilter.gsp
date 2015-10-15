<div class="clear"></div>    
<div class="info-box" style="display:none;margin-top:0">
  <span class="icon icon-info-sign icon-3x"></span>
  <ul id="infolistForm">              
  </ul>
</div>
<div class="error-box" style="display:none">
  <span class="icon icon-warning-sign icon-3x"></span>
  <ul id="errorlistForm">
    <li></li>
  </ul>
</div>
<div class="fright">
  <a class="button" href="javascript:void(0)" onclick="showFormWindow()">Новая &nbsp;<i class="icon-angle-right icon-large"></i></a>
</div>
<div id="createform" class="tabs" style="display:none">
</div>  
<g:formRemote name="allForm" url="[controller:'catalog',action:'formlist']" update="list">
  <input type="submit" id="form_submit_button" value="Показать" style="display:none"/>
</g:formRemote>
<div class="clear"></div>
<script type="text/javascript">
  $('form_submit_button').click(); 
</script>