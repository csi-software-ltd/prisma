<style type="text/css">
  .list td,.list th { font-size: 11px !important}
  tr.yellow > td { background:lightyellow !important }
</style>
<div id="ajax_wrap">
  <div style="padding:5px 10px">&nbsp;</div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата платежа<br/>Номер платежа</th>
          <th>Клиент<br/>Подклиент</th>
          <th>Тип платежа</th>
          <th>Контрагенты</th>
          <th>Сумма</th>
          <th>Процент<br/>Тип процента</th>
          <th>Процент клиента<br/>посредника</th>
          <th>Комиссия</th>
          <th>Возврат клиента<br/>посредника</th>
          <th>Учет списания</th>
          <th>Остаток счета</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${report.records}" status="i" var="record">
        <tr align="center" class="${record.modstatus<2?'yellow':''}" style="${record.client_id&&record.curclientsaldo+record.dinclientsaldo+record.computeClientdelta()<0?'color:red':''}">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}<br/>${record.id}</td>
          <td>${record.client_name}<br/>${record.subclient_name}</td>
          <td>
          <g:if test="${record.is_clientcommission}"><abbr title="Возврат комиссии"><i class="icon-gift icon-large"></i></abbr></g:if>
          <g:elseif test="${record.is_midcommission}"><abbr title="Возврат посреднику"><i class="icon-link icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==1}"><abbr title="Исходящий"><i class="icon-signout icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==2}"><abbr title="Входящий"><i class="icon-signin icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==3}"><abbr title="Внутренний"><i class="icon-refresh icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==4}"><abbr title="Списание"><i class="icon-trash icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==8}"><abbr title="Откуп"><i class="icon-magic icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==9}"><abbr title="Комиссия"><i class="icon-money icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==7}"><abbr title="Абон. плата"><i class="icon-calendar icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==10}"><abbr title="Связанный входящий"><i class="icon-exchange icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==11}"><abbr title="Внешний"><i class="icon-external-link-square icon-large"></i></abbr></g:elseif>
          <g:else><abbr title="Пополнение"><i class="icon-certificate icon-large"></i></abbr></g:else>
          </td>
          <td><g:if test="${record.paytype==4}">Списание по агентскому договору</g:if><g:else>${record.fromcompany_name?:record.fromcompany?:'нет'}<br/>${record.tocompany_name?:record.tocompany?:'нет'}</g:else></td>
          <td>${number(value:record.summa)}<g:if test="${record.clientcommission>0}"><br/>${number(value:record.clientcommission)}</g:if><g:if test="${record.agentcommission>0}"><br/>${number(value:record.agentcommission)}</g:if></td>
          <td>${number(value:record.compercent)}<br/>${record.percenttype?'деление':'умножение'}</td>
          <td><g:if test="${record.subclient_id}">${number(value:record.supcompercent)}<br/>${number(value:record.midpercent)}</g:if><g:else>нет</g:else></td>
          <td>${number(value:record.comission)}</td>
          <td><g:if test="${record.subclient_id}">${number(value:record.supcomission)}<br/>${number(value:record.midcomission)}</g:if><g:else>нет</g:else></td>
          <td>${number(value:record.clientcommission)}</td>
          <td>${number(value:record.curclientsaldo+record.dinclientsaldo+record.computeClientdelta())}</td>
        </tr>
      </g:each>
      <g:if test="${!report.records}">
        <tr>
          <td colspan="11" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Платежей не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
