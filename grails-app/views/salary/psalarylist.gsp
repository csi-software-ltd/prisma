<g:formRemote name="psalaryEditForm" url="[action:'updatePsalary']" method="post" onSuccess="processpsalaryResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorpsalarylist">
      <li></li>
    </ul>
  </div>
  <div id="psalary"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr style="line-height:15px">
          <th>Дата</th>
          <th>Дата<br/>модификации</th>
          <th>Фактическая зарплата</th>
          <th width="400">Комментарий</th>
          <th width="30"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${psalary}" status="i" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.pdate)}</td>
          <td>${record.moddate?String.format('%td.%<tm.%<tY',record.moddate):''}</td>
          <td>${intnumber(value:record.actsalary)}</td>
          <td>${record.comment}</td>
          <td valign="middle" align="center">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('psalary_id').value=${record.id};$('psalary_submit_button').click();"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each> 
        <tr align="center">
          <td colspan="5">
            <a class="button" id="addpsalarybutton" href="javascript:void(0)" onclick="$('psalary_id').value=0;$('psalary_submit_button').click();">Назначить новый оклад &nbsp;<i class="icon-angle-right icon-large"></i></a>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="psalaryForm" url="[action:'psalary',params:[pers_id:pers_user.id]]" update="psalary" onComplete="\$('errorpsalarylist').up('div').hide();jQuery('#psalaryEditForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="psalary_submit_button" value="Показать"/>
  <input type="hidden" id="psalary_id" name="id" value="0"/>
</g:formRemote>