<g:formRemote name="allForm" url="[controller:controllerName,action:'offreports']" update="list">
  <label class="auto" for="repdate">Дата:</label>
  <g:select class="mini" name="repdate" from="${offreports}" optionKey="keyvalue" optionValue="disvalue" onchange="\$('form_submit_button').click();updateoffbuttons(this.value);"/>
  <label class="auto" for="company_name">Компания:</label>
  <input type="text" id="company_name" name="company_name" />
  <div id="buhcompanyname_autocomplete" class="autocomplete" style="display:none"></div>
  <label class="auto" for="pers_name">ФИО</label>
  <input type="text" id="pers_name" name="pers_name" />
  <div id="buhpersname_autocomplete" class="autocomplete" style="display:none"></div><br/>
  <label class="auto" for="perstype">Тип:</label>
  <g:select class="auto" name="perstype" from="${['Сотрудник','Директор','Специалист']}" keys="123" noSelection="${['-100':'все']}"/>
  <div class="fright">
    <span id="addoffbuttons">
    <g:if test="${curreport?.modstatus==0&&iscanedit}">
      <g:remoteLink class="button" url="${[controller:'salary',action:'createpayrequests',id:curreport.id]}" onSuccess="\$('form_submit_button').click();updateoffbuttons('${String.format('%td.%<tm.%<tY',new Date(curreport.year-1900,curreport.month-1,1))}');">В оплату</g:remoteLink>
      <g:remoteLink class="button" url="${[controller:'salary',action:'recomputeofficial',id:curreport.id]}" onSuccess="\$('form_submit_button').click();">Пересчитать</g:remoteLink>
    </g:if><g:if test="${curreport?.modstatus==1&&iscanedit}">
      <g:remoteLink class="button" url="${[controller:'salary',action:'closebuhreport',id:curreport.id]}" onSuccess="\$('form_submit_button').click();updateoffbuttons('${String.format('%td.%<tm.%<tY',new Date(curreport.year-1900,curreport.month-1,1))}');">Закрыть</g:remoteLink>
    </g:if>
    </span>
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('company_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"buhcompanyname_autocomplete")}'
  });
  new Autocomplete('pers_name', {
    serviceUrl:'${resource(dir:"autocomplete",file:"buhpersname_autocomplete")}'
  });
</script>