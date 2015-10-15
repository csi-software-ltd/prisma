<g:formRemote id="updateForm" name="updateForm" url="[action:'updateexpensetype2']" method="post" onSuccess="process2Response(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="error2list">
      <li></li>
    </ul>
  </div>
  <div id="expensetype2"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th width="30">Код</th>
          <th>Название</th>
          <th width="50"></th>               
        </tr>
      </thead>
      <tbody>
        <tr align="center">
          <td colspan="3">
            <a class="button" id="addbutton" href="javascript:void(0)" onclick="$('expensetype_id').value=0;$('expensetype_submit_button').click();">
              Добавить подраздел &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      <g:each in="${expensetypes2}" status="i" var="record">
        <tr align="center">
          <td>${record.id}</td>
          <td align="left">${record.name}</td>          
          <td>
            <a class="button" href="javascript:void(0)" title="Редактировать" onclick="$('expensetype_id').value=${record.id};$('expensetype_submit_button').click();"><i class="icon-pencil"></i></a>
          <g:if test="${exptypescounts[record.id]==0}">
            &nbsp;<g:remoteLink class="button" before="if(!confirm('Удалить подраздел?')) return false" url="${[controller:controllerName,action:'removeexpensetype2',id:record.id]}" title="Удалить" onSuccess="getExpensetypes2()"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>       
  </div>
</div>
<g:formRemote name="expensetypeForm" url="[action:'expensetype2',params:[expensetype1_id:expensetype?.id]]" update="expensetype2" onComplete="\$('error2list').up('div').hide();jQuery('#updateForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="expensetype_submit_button" value="Показать" /> 
  <input type="hidden" id="expensetype_id" name="id" value="0" />  
</g:formRemote>
