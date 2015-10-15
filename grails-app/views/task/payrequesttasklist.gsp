<g:formRemote id="payrequestForm" name="payrequestForm" url="[action:'updatetaskpayrequest']" method="post" onSuccess="processTaskpayrequestResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorpayrequestlist">
      <li></li>
    </ul>
  </div>
  <div id="payrequest"></div>
</g:formRemote>
<g:formRemote id="splitrequestForm" name="splitrequestForm" url="[action:'splitpayrequest', params:[taskpay_id:taskpay.id]]" method="post" onSuccess="processsplitrequestResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorsplitrequestlist">
      <li></li>
    </ul>
  </div>
  <div>
    <label for="splitrequest_summa">Сумма:</label>
    <input type="text" id="splitrequest_summa" name="summa" value=""/>
    <div class="clear"></div>
    <div class="fright">
      <input type="submit" class="button" value="Разделить"/>
      <input type="reset" class="button" value="Отмена" onclick="closesplitform()"/>
    </div>
    <input type="hidden" id="splitrequest_id" name="id" value="0"/>
    <div class="clear" style="padding-bottom:10px"></div>
  </div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Срок</th>
          <th>Исполнение</th>
          <th>Сумма</th>
          <th>Категория</th>
          <th>Получатель</th>
          <th>Путь исполнения</th>
          <th>Статус</th>
          <th width="70"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${payrequests}" status="i" var="record">
        <tr align="center">
          <td>${record.paydate?String.format('%td.%<tm.%<tY',record.paydate):''}</td>
          <td>${record.execdate?String.format('%td.%<tm.%<tY %<tT',record.execdate):'нет'}</td>
          <td>${number(value:record.summa)}</td>
          <td>
            <g:if test="${record.paycat==4&&record.is_dop}">Внутренняя проводка</g:if>
            <g:else>${record.paycat==1?'Договорной':record.paycat==2?'Бюджетный':record.paycat==3?'Персональный':record.paycat==4?'прочий':record.paycat==5?'банковский':'счета'}</g:else>
          </td>
          <td>
          <g:if test="${record.paycat in [1,4]}">${record.tocompany}</g:if>
          <g:elseif test="${record.paycat==2}">${taxes[record.tax_id]}</g:elseif>
          <g:else>${Pers.get(record.pers_id)?.shortname}</g:else>
          </td>
          <td>${!record.payway?'Банк-клиент':'Ручное поручение'}</td>
          <td>${record.modstatus==0?'необработанный':record.modstatus==1?'в задании':record.modstatus==2?'выполнен':'подтвержден'}</td>
          <td align="center">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Подробности" onclick="$('payrequest_id').value=${record.id};$('payrequest_submit_button').click();"><i class="icon-pencil"></i></a>
          <g:if test="${iscanedit&&record.modstatus==1&&user.id==taskpay.executor&&taskpay.taskpaystatus>0}">
            &nbsp; <g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'setPayrequestModstatus',id:record.id,params:[taskpay_id:taskpay.id,status:2]]}" title="Выполнить" onSuccess="processPayrequestModstatus(e)"><i class="icon-ok"></i></g:remoteLink>
          </g:if>
          <g:if test="${iscanedit&&record.modstatus==2&&user.id==taskpay.executor}">
            &nbsp; <g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'setPayrequestModstatus',id:record.id,params:[taskpay_id:taskpay.id,status:1]]}" title="Отменить выполнение" onSuccess="processPayrequestModstatus(e)"><i class="icon-undo"></i></g:remoteLink>
          </g:if>
          <g:if test="${iscandelete&&record.modstatus==1&&taskpay.taskpaystatus in [0,1,3,5]}">
            &nbsp; <g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'removefromtask',id:record.id,params:[taskpay_id:taskpay.id]]}" title="Удалить из задания" onSuccess="location.reload(true)"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          <g:if test="${iscandelete&&record.modstatus==1&&taskpay.is_accept==0}">
            &nbsp; <a class="button" style="z-index:1" href="javascript:void(0)" title="Разделить платеж" onclick="$('splitrequest_summa').value='';$('splitrequest_id').value=${record.id};jQuery('#payrequestForm').slideUp();jQuery('#splitrequestForm').slideDown();"><i class="icon-unlink"></i></a>
          </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="prequestForm" url="[action:'payrequest', params:[taskpay_id:taskpay.id]]" update="payrequest" onComplete="\$('errorpayrequestlist').up('div').hide();closesplitform();jQuery('#payrequestForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="payrequest_submit_button" value="Показать"/>
  <input type="hidden" id="payrequest_id" name="id" value="0"/>
</g:formRemote>
