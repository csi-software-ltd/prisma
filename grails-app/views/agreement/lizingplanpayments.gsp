<style type="text/css">
  tr.yellow > td { background:khaki !important }
</style>
<g:formRemote id="planpaymentAddForm" name="planpaymentAddForm" url="[action:'addlizingplanpayment']" method="post" onSuccess="processAddplanpaymentResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorplanpaymentlist">
      <li></li>
    </ul>
  </div>
  <div id="lizingplanpayment"></div>
</g:formRemote>
<g:form style="display:none" name="uploadpaymentsForm" url="${[action:'uploadlizingpayments',id:lizing.id]}" method="post" enctype="multipart/form-data" target="upload_target">
  <div class="error-box" style="display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="uploaderrorlist">
    </ul>
  </div>
  <label for="file">Файл платежей:</label>
  <input type="file" id="file" name="file" style="width:256px"/>
  <div class="fright">
    <input id="cancelluploadbutton" type="reset" class="spacing" value="Отменить" onclick="jQuery('#uploadpaymentsForm').slideUp();" />
    <input type="button" class="spacing" value="Сохранить" onclick="confirmUpload()"/>
  </div>
</g:form>
<iframe id="upload_target" name="upload_target" style="display:none"></iframe>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата платежа</th>
          <th>Сумма</th>
          <th>Статус</th>
          <th width="70"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${planpayments}" status="i" var="record">
        <tr align="center" class="${record.is_insurance?'yellow':''}">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td>${number(value:record.summa)}</td>
          <td>${record.modstatus==1?'В оплате':record.modstatus==2?'Оплачен':'Планируемый'}</td>
          <td>
          <g:if test="${iscanedit}">
          <g:if test="${record.modstatus==0}">
            <g:remoteLink class="button" url="${[controller:controllerName,action:'deletelizingplanpayment',id:record.id,params:[lizing_id:lizing.id]]}" title="Удалить" onSuccess="getPlanpayment()"><i class="icon-trash"></i></g:remoteLink>&nbsp;&nbsp;
          </g:if>
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('lizingplanpayment_id').value=${record.id};$('planpayment_submit_button').click();"><i class="icon-pencil"></i></a>
          </g:if>
          </td>
        </tr>
      </g:each>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="4" class="btns" style="text-align:center">
            <a class="button" id="addplanpaymentbutton" href="javascript:void(0)" onclick="$('lizingplanpayment_id').value=0;$('planpayment_submit_button').click();">
              Добавить платеж &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      <g:if test="${!planpayments.find{it.modstatus>0}}">
        <tr>
          <td colspan="4" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="jQuery('#planpaymentAddForm').slideUp();jQuery('#uploadpaymentsForm').slideDown();">
              Закачать платежи &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>            
          </td>
        </tr>
      </g:if>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="planpaymentForm" url="[action:'lizingplanpayment', params:[lizing_id:lizing.id]]" update="lizingplanpayment" onComplete="\$('errorplanpaymentlist').up('div').hide();jQuery('#uploadpaymentsForm').slideUp();jQuery('#planpaymentAddForm').slideDown();" style="display:none">
  <input type="hidden" id="lizingplanpayment_id" name="id" value="0"/>
  <input type="submit" class="button" id="planpayment_submit_button" value="Показать"/>
</g:formRemote>
