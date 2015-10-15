<g:formRemote id="spacebankcheckAddForm" name="spacebankcheckAddForm" url="[action:'addspacebankcheck']" method="post" onSuccess="processAddspacebankcheckResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorspacebankchecklist">
      <li></li>
    </ul>
  </div>
  <div id="spacebankcheck"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата проверки</th>
          <th>Банк</th>
          <th>Тип</th>
          <th>Контактная информация</th>
          <th width="50"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${spacebankchecks}" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.checkdate)}</td>
          <td>${banks[record.bank_id]}</td>
          <td>${bankchecktypes[record.checktype_id]}</td>
          <td>${record.contactinfo}</td>
          <td>
          <g:if test="${iscanedit}">
            <g:remoteLink class="button" style="z-index:1" before="if(!confirm('Вы действительно хотите удалить проверку?')) return false" url="${[controller:controllerName, action:'deletespacebankcheck', id:record.id, params:[space_id:space.id]]}" title="Удалить" onSuccess="getBankchecks()"><i class="icon-trash"></i></g:remoteLink>&nbsp;&nbsp;
          </g:if>
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('spacebankcheck_id').value=${record.id};$('spacebankcheck_submit_button').click();"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      <g:if test="${!spacebankchecks}">
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Банковских проверок не найдено</a>
          </td>
        </tr>
      </g:if>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <a class="button" id="addplanpaymentbutton" href="javascript:void(0)" onclick="$('spacebankcheck_id').value=0;$('spacebankcheck_submit_button').click();">
              Добавить проверку &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="spacebankcheckForm" url="[action:'spacebankcheck', params:[space_id:space.id]]" update="spacebankcheck" onComplete="\$('errorspacebankchecklist').up('div').hide();jQuery('#spacebankcheckAddForm').slideDown();" style="display:none">
  <input type="hidden" id="spacebankcheck_id" name="id" value="0"/>
  <input type="submit" class="button" id="spacebankcheck_submit_button" value="Показать"/>
</g:formRemote>