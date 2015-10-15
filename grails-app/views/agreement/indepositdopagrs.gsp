<g:formRemote id="indepositdopagrAddForm" name="indepositdopagrAddForm" url="[controller:controllerName, action:'addindepositdopagr']" method="post" onSuccess="processAddindepositdopagrResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorindepositdopagrlist">
      <li></li>
    </ul>
  </div>
  <div id="indepositdopagr"></div>
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
          <th>Ставка<br/>Комиссия</th>
          <th width="50"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${indepositdopagrs}" var="record">
        <tr align="center">
          <td>${record.nomer}<g:if test="${record.id==firstagrid}"> (осн.договор)</g:if></td>
          <td>${String.format('%td.%<tm.%<tY',record.startdate)}</td>
          <td>${record.enddate?String.format('%td.%<tm.%<tY',record.enddate):'бессрочный'}</td>
          <td>${number(value:record.summa)}</td>
          <td>${number(value:record.rate)}<br/>${number(value:record.comrate)}</td>
          <td>
          <g:if test="${iscanedit&&record.id!=firstagrid}">
            <g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'deleteindepositdopagr',id:record.id,params:[deposit_id:deposit.id]]}" title="Удалить" onSuccess="getDopAgrs()"><i class="icon-trash"></i></g:remoteLink>&nbsp;&nbsp;
          </g:if>
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('indepositdopagr_id').value=${record.id};$('indepositdopagr_submit_button').click();"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      <g:if test="${!indepositdopagrs}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Доп. соглашений не найдено</a>
          </td>
        </tr>
      </g:if>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('indepositdopagr_id').value=0;$('indepositdopagr_submit_button').click();">
              Добавить соглашение &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="indepositdopagrForm" url="[action:'indepositdopagr', params:[deposit_id:deposit.id]]" update="indepositdopagr" onComplete="\$('errorindepositdopagrlist').up('div').hide();jQuery('#indepositdopagrAddForm').slideDown();" style="display:none">
  <input type="hidden" id="indepositdopagr_id" name="id" value="0"/>
  <input type="submit" class="button" id="indepositdopagr_submit_button" value="Показать"/>
</g:formRemote>