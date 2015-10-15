<g:formRemote id="spacedopagrAddForm" name="spacedopagrAddForm" url="[action:'addspacedopagr']" method="post" onSuccess="processAddspacedopagrResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorspacedopagrlist">
      <li></li>
    </ul>
  </div>
  <div id="spacedopagr"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Номер</th>
          <th>Дата начала</th>
          <th>Дата окончания</th>
          <th>Сумма аренды<br/>Сумма доп. услуг</th>
          <th>Дата платежа</th>
          <th>Стоимость изменена</th>
          <th width="50"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${spacedopagrs}" var="record">
        <tr align="center">
          <td>${record.anumber}</td>
          <td>${String.format('%td.%<tm.%<tY',record.startdate)}</td>
          <td>${String.format('%td.%<tm.%<tY',record.enddate)}</td>
          <td>${number(value:record.rate)}<g:if test="${record.ratedop}"><br/>${number(value:record.ratedop)}</g:if></td>
          <td>${record.payterm}</td>
          <td>
          <g:if test="${record.is_changeprice}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
          <g:if test="${iscanedit}">
            <g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'deletespacedopagr',id:record.id,params:[space_id:space.id]]}" title="Удалить" onSuccess="getDopAgrs()"><i class="icon-trash"></i></g:remoteLink>&nbsp;&nbsp;
          </g:if>
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('spacedopagr_id').value=${record.id};$('spacedopagr_submit_button').click();"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      <g:if test="${!spacedopagrs}">
        <tr>
          <td colspan="7" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Доп. соглашений не найдено</a>
          </td>
        </tr>
      </g:if>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="7" class="btns" style="text-align:center">
            <a class="button" id="addplanpaymentbutton" href="javascript:void(0)" onclick="$('spacedopagr_id').value=0;$('spacedopagr_submit_button').click();">
              Добавить соглашение &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="spacedopagrForm" url="[action:'spacedopagr', params:[space_id:space.id]]" update="spacedopagr" onComplete="\$('errorspacedopagrlist').up('div').hide();jQuery('#spacedopagrAddForm').slideDown();" style="display:none">
  <input type="hidden" id="spacedopagr_id" name="id" value="0"/>
  <input type="submit" class="button" id="spacedopagr_submit_button" value="Показать"/>
</g:formRemote>