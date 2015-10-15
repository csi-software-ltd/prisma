<g:formRemote id="complicenseAddForm" name="complicenseAddForm" url="[action:'addcomplicense']" method="post" onSuccess="processaddcomplicenseResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorcomplicenselist">
      <li></li>
    </ul>
  </div>
  <div id="complicense"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Название</th>
          <th>Дата выдачи</th>
          <th>Срок действия</th>
          <th>Номер<br/>Номер бланка</th>
          <th>Выдавший орган</th>
          <th>Статус</th>
          <th>Действия</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${complicenses}" status="i" var="record">
        <tr align="center">
          <td>${record.name}</td>
          <td>${String.format('%td.%<tm.%<tY',record.ldate)}</td>
          <td>${String.format('%td.%<tm.%<tY',record.validity)}</td>
          <td>${record.nomer}<br/>${record.formnumber}</td>
          <td>${record.authority}</td>
          <td>
          <g:if test="${record.modstatus}"><abbr title="активная"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="неактивная"><i class="icon-ban-circle"></i></abbr></g:else>
          </td>
          <td valign="middle">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('complicense_id').value=${record.id};$('complicense_submit_button').click();"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="7" class="btns" style="text-align:center">
            <a class="button" id="addcomplicensebutton" href="javascript:void(0)" onclick="$('complicense_id').value=0;$('complicense_submit_button').click();">
              Добавить новую лицензию &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="complicenseForm" url="[action:'complicense', params:[company_id:company.id]]" update="complicense" onComplete="\$('errorcomplicenselist').up('div').hide();jQuery('#complicenseAddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="complicense_submit_button" value="Показать"/>
  <input type="hidden" id="complicense_id" name="id" value="0"/>
</g:formRemote>