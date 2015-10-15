<style type="text/css">
  .list td,.list th { font-size: 11px !important }
</style>
<g:formRemote id="loanpaymentAddForm" name="loanpaymentAddForm" url="[action:'addloanpayment']" method="post" onSuccess="processAddloanpaymentResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorloanpaymentlist">
      <li></li>
    </ul>
  </div>
  <div id="loanpayment"></div>
</g:formRemote>
<g:form style="display:none" name="uploadpaymentsForm" url="${[action:'uploadloanpayments',id:loan.id]}" method="post" enctype="multipart/form-data" target="upload_target">
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
          <th rowspan="2">Дата платежа</th>
          <th rowspan="2">Месяц</th>
          <th colspan="3">Сумма тела</th>
          <th colspan="2">Сумма процентов</th>
          <th colspan="2">Общая сумма</th>
          <th rowspan="2">Оплата тела<br/>Дата</th>
          <th rowspan="2">Оплата процентов<br/>Дата</th>
          <th rowspan="2">Действие</th>
        </tr>
        <tr>
          <th>валюта</th>
          <th>рубли</th>
          <th>остаток</th>
          <th>валюта</th>
          <th>рубли</th>
          <th>валюта</th>
          <th>рубли</th>
        </tr>
      </thead>
      <tbody>
      <g:if test="${iscanedit}">
      <g:if test="${loan.repaymenttype_id<3&&loan.loanclass<3&&!payments.find{it.modstatus>0}}">
        <tr>
          <td colspan="12" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="confirmgenerate()">
              Сформировать платежи &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>            
          </td>
        </tr>
      </g:if>
      <g:if test="${loan.repaymenttype_id==3&&!payments.find{it.modstatus>0}}">
        <tr>
          <td colspan="12" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="jQuery('#loanpaymentAddForm').slideUp();jQuery('#uploadpaymentsForm').slideDown();">
              Закачать платежи &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>            
          </td>
        </tr>
      </g:if>
        <tr>
          <td colspan="12" class="btns" style="text-align:center">
            <a class="button" id="addplanpaymentbutton" href="javascript:void(0)" onclick="$('loanpayment_id').value=0;$('loanpayment_submit_button').click();">
              Добавить платеж &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      <g:if test="${loan.valuta_id!=857&&loan.is_cbcalc}">
        <tr>
          <td colspan="12" class="btns" style="text-align:center">
            <g:remoteLink class="button" url="${[controller:'agreement',action:'recalculateLoanRubSummas',id:loan.id]}" onSuccess="getPayrequests()">
              Пересчитать рублевые суммы &nbsp;<i class="icon-angle-right icon-large"></i>
            </g:remoteLink>
          </td>
        </tr>
      </g:if>
      </g:if>
      <g:each in="${payments}" status="i" var="record">
        <%totalbody+=record.summa%>
        <tr align="center" style="${record.modstatus==1?'color:red':record.modstatus==2?'color:green':''}">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td>${record.paidmonth}</td>
          <td>${number(value:record.summa)}</td>
          <td>${number(value:record.summarub)}</td>
          <td>${number(value:loansum-totalbody)}</td>
          <td>${number(value:record.summaperc)}</td>
          <td>${number(value:record.summapercrub)}</td>
          <td>${number(value:record.summa+record.summaperc)}</td>
          <td>${number(value:record.summarub+record.summapercrub)}</td>
          <td>${number(value:record.paid)}<g:if test="${record.paiddate}"><br/>${String.format('%td.%<tm.%<tY',record.paiddate)}</g:if></td>
          <td>${number(value:record.percpaid)}<g:if test="${record.percpaiddate}"><br/>${String.format('%td.%<tm.%<tY',record.percpaiddate)}</g:if></td>
          <td>
          <g:if test="${!record.paidstatus&&!record.percpaidstatus&&iscanedit}">
            <g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'deleteloanpayment',id:record.id,params:[loan_id:loan.id]]}" title="Удалить" onSuccess="getPayrequests()"><i class="icon-trash"></i></g:remoteLink>&nbsp;&nbsp;
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('loanpayment_id').value=${record.id};$('loanpayment_submit_button').click();"><i class="icon-pencil"></i></a>
          </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="loanpaymentForm" url="[action:'loanpayment', params:[loan_id:loan.id]]" update="loanpayment" onComplete="\$('errorloanpaymentlist').up('div').hide();jQuery('#uploadpaymentsForm').slideUp();jQuery('#loanpaymentAddForm').slideDown();" style="display:none">
  <input type="hidden" id="loanpayment_id" name="id" value="0"/>
  <input type="submit" class="button" id="loanpayment_submit_button" value="Показать"/>
</g:formRemote>
