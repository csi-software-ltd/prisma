<style type="text/css">
  table.list thead th, table.list tbody th, table.list thead td, table.list tbody td { font-size: 11px }
</style>
<g:formRemote id="cashpaymentupdateForm" name="cashpaymentupdateForm" url="[action:'cashpaymentupdate', params:[creport_id:cashreport.id]]" method="post" onSuccess="processcashpaymentupdateResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorcashpaymentlist">
      <li></li>
    </ul>
  </div>
  <div id="cashpayment"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th rowspan="2">Фио</th>
          <th rowspan="2">Факт. оклад</th>
          <th rowspan="2">Аванс</th>
          <th rowspan="2">Офф. зарплата</th>
          <th rowspan="2">Бонус</th>
          <th rowspan="2">Штраф</th>
          <th colspan="2">Переработка</th>
          <th colspan="2">Отпускные</th>
          <th rowspan="2">До срока</th>
          <th rowspan="2">Корр.</th>
          <th rowspan="2">К выдаче</th>
          <th rowspan="2">Долг</th>
          <th rowspan="2">Статус</th>
          <th rowspan="2">Дата оплаты</th>
          <th rowspan="2"></th>
        </tr>
        <tr>
          <th>дн</th>
          <th>руб</th>
          <th>расчет</th>
          <th style="border-right:1px solid #E9E9E4">перерасчет</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${cashpayments}" status="i" var="record">
        <tr align="center" style="font-weight:${record.is_haveagr?700:400}">
          <td>${record.shortname}</td>
          <td>${intnumber(value:record.actsalary)}</td>
          <td>${intnumber(value:record.prepayment)}</td>
          <td>${number(value:record.cardmain)}</td>
          <td>${intnumber(value:record.bonus)}</td>
          <td>${intnumber(value:record.shtraf)}</td>
          <td>${record.overloadhour}</td>
          <td>${intnumber(value:record.overloadsumma)}</td>
          <td>${intnumber(value:record.holiday)}</td>
          <td>${intnumber(value:record.reholiday)}</td>
          <td>${intnumber(value:record.precashpayment)}</td>
          <td>${number(value:record.prevfix)}</td>
          <td>${intnumber(value:record.cash)}</td>
          <td>${intnumber(value:persusers[record.id]?.cassadebt?:0)}</td>
          <td>${record.cashstatus==-1?'не начисляется':record.cashstatus==1?'начислено':record.cashstatus==2?'оплачено':'новый'}</td>
          <td>${record.cashdate?String.format('%td.%<tm.%<tY',record.cashdate):'нет'}</td>
          <td width="20">
          <g:if test="${iscanedit}">
          <g:if test="${cashreport.modstatus<2&&record.cashstatus<2}">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('salary_id').value=${record.id};$('cashpayment_submit_button').click();"><i class="icon-pencil"></i></a>
          </g:if><g:if test="${record.cashstatus==1}">
            <a class="button nopad" style="z-index:1" title="Оплатить" onclick="paycashpayment(${record.id},1)"><i class="icon-money"></i></a>
          </g:if><g:if test="${record.cashstatus==2&&cashreport.modstatus==1&&record.cash>0}">
            <a class="button nopad" style="z-index:1" title="Отменить выдачу" onclick="paycashpayment(${record.id},2)"><i class="icon-repeat"></i></a>
          </g:if>
          </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="cashpaymentForm" url="[action:'cashpayment', params:[creport_id:cashreport.id]]" update="cashpayment" onComplete="\$('errorcashpaymentlist').up('div').hide();jQuery('#cashpaymentupdateForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="cashpayment_submit_button" value="Показать"/>
  <input type="hidden" id="salary_id" name="id" value="0"/>
</g:formRemote>