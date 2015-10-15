<g:formRemote id="paytransferForm" name="paytransferForm" url="[action:'addpaytransfer']" method="post" onSuccess="processaddpaytransferResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorpaytransferlist">
      <li></li>
    </ul>
  </div>
  <div id="paytransfer"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата</th>
          <th>Сумма</th>
          <th>Плательщик</th>
          <th>Статус</th>
          <th width="70"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${paytransfers}" status="i" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td>${number(value:record.summa)}</td>
          <td>${record.fromcompany}</td>
          <td>${record.modstatus==1?'в задании':record.modstatus==2?'выполнен':'подтвержден'}</td>
          <td align="center">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Подробности" onclick="$('paytransfer_id').value=${record.id};$('paytransfer_submit_button').click();"><i class="icon-pencil"></i></a>
          <g:if test="${iscandelete&&record.modstatus==1&&taskpay.taskpaystatus==1}">
            &nbsp;&nbsp;<g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'deletepaytransfer',id:record.id,params:[taskpay_id:taskpay.id]]}" title="Удалить" onSuccess="getPaytransfers()"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </td>
        </tr>
      </g:each>
      <g:if test="${!paytransfers}">
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Проводок не найдено</a>
          </td>
        </tr>
      </g:if>
      <g:if test="${iscanadd}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('paytransfer_id').value=0;$('paytransfer_submit_button').click();">
              Добавить проводку &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="ptransferForm" url="[action:'paytransfer', params:[taskpay_id:taskpay.id]]" update="paytransfer" onComplete="\$('errorpaytransferlist').up('div').hide();jQuery('#paytransferForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="paytransfer_submit_button" value="Показать"/>
  <input type="hidden" id="paytransfer_id" name="id" value="0"/>
</g:formRemote>
