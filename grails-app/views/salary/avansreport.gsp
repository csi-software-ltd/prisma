<style type="text/css">
  .list td,.list th { font-size: 12px }
</style>
<g:formRemote id="prepaymentupdateForm" name="prepaymentupdateForm" url="[action:'prepaymentupdate', params:[avans_id:avans.id]]" method="post" onSuccess="processprepaymentupdateResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorprepaymentlist">
      <li></li>
    </ul>
  </div>
  <div id="prepayment"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Фио</th>
          <th>Факт. оклад</th>
          <th>Сумма аванса</th>
          <th>Корректировка</th>
          <th>Статус аванса</th>
          <th>Дата оплаты аванса</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${prepayments}" status="i" var="record">
        <tr align="center">
          <td>${record.shortname}</td>
          <td>${intnumber(value:record.actsalary)}</td>
          <td>${intnumber(value:record.prepayment)}</td>
          <td>${number(value:record.prevfix)}</td>
          <td>${record.prepaystatus==-1?'не начисляется':record.prepaystatus==1?'начислено':record.prepaystatus==2?'оплачено':'новый'}</td>
          <td>${record.prepaydate?String.format('%td.%<tm.%<tY',record.prepaydate):'нет'}</td>
          <td width="50">
          <g:if test="${iscanedit}">
          <g:if test="${avans.modstatus==0}">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('salary_id').value=${record.id};$('prepayment_submit_button').click();"><i class="icon-pencil"></i></a>
          </g:if><g:if test="${record.prepaystatus==1&&avans.is_confirm==1}">
            <a class="button nopad" style="z-index:1" title="Оплатить" onclick="payprepayment(${record.id},1)"><i class="icon-money"></i></a>
          </g:if><g:if test="${record.prepaystatus==2&&avans.modstatus==1}">
            <a class="button nopad" style="z-index:1" title="Вернуть аванс" onclick="payprepayment(${record.id},2)"><i class="icon-repeat"></i></a>
          </g:if>
          </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="prepaymentForm" url="[action:'prepayment', params:[avans_id:avans.id]]" update="prepayment" onComplete="\$('errorprepaymentlist').up('div').hide();jQuery('#prepaymentupdateForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="prepayment_submit_button" value="Показать"/>
  <input type="hidden" id="salary_id" name="id" value="0"/>
</g:formRemote>