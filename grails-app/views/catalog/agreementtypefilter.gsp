<div class="clear"></div>    
<div class="info-box" style="display:none;margin-top:0">
  <span class="icon icon-info-sign icon-3x"></span>
  <ul id="infolistAgreementtype">              
  </ul>
</div>
<div class="error-box" style="display:none">
  <span class="icon icon-warning-sign icon-3x"></span>
  <ul id="errorlistAgreementtype">
    <li></li>
  </ul>
</div>
<g:formRemote name="allForm" url="[controller:'catalog',action:'agreementtypelist']" update="list">
  <input type="submit" id="form_submit_button" value="Показать" style="display:none" />    
</g:formRemote>
<div id="createAgreementtype" class="tabs" style="display:none">
</div> 
<div class="clear"></div>
<script type="text/javascript">
  $('form_submit_button').click();
</script>