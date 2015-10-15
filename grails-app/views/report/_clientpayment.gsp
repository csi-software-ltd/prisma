<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <style type="text/css">
      @font-face {
        src: url('http://nbps.ru:8080/font/arial.ttf');
        -fs-pdf-font-embed: embed;
        -fs-pdf-font-encoding: Identity-H;
      }
      @page {
        size: 21cm 29.7cm;
      }
      body { font-family: "Arial Unicode MS", Arial, sans-serif; }
      table { border-top: 2px solid #000; border-left: 1px solid #000; }
      table td { border-bottom: 1px solid #000; border-right: 1px solid #000; }
      table th { border-bottom: 1px solid #000; border-right: 1px solid #000; }
      table tr.yellow > td { background:#FFFFE0 !important }
      table tr > th { background:#FFD700 !important }
    </style>
  </head>
  <body style="width:690px">
    <div>
      <table style="width:690px;font-size:6pt">
        <tbody>
          <tr align="center">
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
        <g:each in="${report.records}" status="i" var="record">
          <tr align="center" class="${record.modstatus<2?'yellow':''}" style="${record.client_id&&record.curclientsaldo+record.dinclientsaldo+record.computeClientdelta()<0?'color:red':''}">
            <td>${String.format('%td.%<tm.%<tY',record.paydate)}<br/>${record.id}</td>
            <td>${record.client_name}<br/>${record.subclient_name}</td>
            <td>
            <g:if test="${record.is_clientcommission}">Возврат комиссии</g:if>
            <g:elseif test="${record.is_midcommission}">Возврат посреднику</g:elseif>
            <g:elseif test="${record.paytype==1}">Исходящий</g:elseif>
            <g:elseif test="${record.paytype==2}">Входящий</g:elseif>
            <g:elseif test="${record.paytype==3}">Внутренний</g:elseif>
            <g:elseif test="${record.paytype==4}">Списание</g:elseif>
            <g:elseif test="${record.paytype==8}">Откуп</g:elseif>
            <g:elseif test="${record.paytype==9}">Комиссия</g:elseif>
            <g:elseif test="${record.paytype==7}">Абон. плата</g:elseif>
            <g:elseif test="${record.paytype==10}">Связанный входящий</g:elseif>
            <g:elseif test="${record.paytype==11}">Внешний</g:elseif>
            <g:else>Пополнение</g:else>
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
        </tbody>
      </table>
    </div>
  </body>
</html>