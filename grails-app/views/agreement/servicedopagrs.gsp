<g:formRemote id="servicedopagrAddForm" name="servicedopagrAddForm" url="[controller:controllerName, action:'addservicedopagr']" method="post" onSuccess="processAddservicedopagrResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorservicedopagrlist">
      <li></li>
    </ul>
  </div>
  <div id="servicedopagr"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Номер</th>
          <th>Дата начала</th>
          <th>Дата окончания</th>
          <th>Сумма</th>
          <th width="50"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${servicedopagrs}" var="record">
        <tr align="center">
          <td>${record.nomer}<g:if test="${record.id==firstagrid}"> (осн.договор)</g:if></td>
          <td>${String.format('%td.%<tm.%<tY',record.startdate)}</td>
          <td>${String.format('%td.%<tm.%<tY',record.enddate)}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td>
          <g:if test="${iscanedit&&record.id!=firstagrid}">
            <g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'deleteservicedopagr',id:record.id,params:[service_id:service.id]]}" title="Удалить" onSuccess="getDopAgrs()"><i class="icon-trash"></i></g:remoteLink>&nbsp;&nbsp;
          </g:if>
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('servicedopagr_id').value=${record.id};$('servicedopagr_submit_button').click();"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      <g:if test="${!servicedopagrs}">
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Доп. соглашений не найдено</a>
          </td>
        </tr>
      </g:if>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('servicedopagr_id').value=0;$('servicedopagr_submit_button').click();">
              Добавить соглашение &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="servicedopagrForm" url="[action:'servicedopagr', params:[service_id:service.id]]" update="servicedopagr" onComplete="\$('errorservicedopagrlist').up('div').hide();jQuery('#servicedopagrAddForm').slideDown();" style="display:none">
  <input type="hidden" id="servicedopagr_id" name="id" value="0"/>
  <input type="submit" class="button" id="servicedopagr_submit_button" value="Показать"/>
</g:formRemote>