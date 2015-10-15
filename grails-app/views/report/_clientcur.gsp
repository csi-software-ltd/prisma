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
            <th colspan="4">Сводка по клиенту ${client?.name} за период с ${String.format('%td.%<tm.%<tY',reportstart)} по ${String.format('%td.%<tm.%<tY',reportend)}:</th>
          </tr>
          <tr align="center">
            <td width="250"></td>
            <td>Остаток клиентских средств на начало периода</td>
            <td>${number(value:startclientsaldo)}</td>
            <td width="100"></td>
          </tr>
          <tr align="center">
            <td width="250"></td>
            <td>Общая сумма поступлений по клиентским платежам</td>
            <td>${number(value:(report.records.sum{ it.computeIncome() }?:0.0g))}</td>
            <td width="100"></td>
          </tr>
          <tr align="center">
            <td width="250"></td>
            <td>Общая сумма вывода по клиентским платежам</td>
            <td>${number(value:(report.records.sum{ it.computeOutlay() }?:0.0g))}</td>
            <td width="100"></td>
          </tr>
          <tr align="center">
            <td width="250"></td>
            <td>Начислено к возврату комиссионных</td>
            <td>${number(value:(report.records.sum{ it.subcomission }?:0.0g))}</td>
            <td width="100"></td>
          </tr>
          <tr align="center">
            <td width="250"></td>
            <td>Возврат комиссионных</td>
            <td>${number(value:(report.records.sum{ it.is_clientcommission?it.clientdelta:0.0g }?:0.0g))}</td>
            <td width="100"></td>
          </tr>
          <tr align="center">
            <td width="250"></td>
            <td>Сумма списаний по агентским договорам</td>
            <td>${number(value:(report.records.sum{ it.clientcommission }?:0.0g))}</td>
            <td width="100"></td>
          </tr>
          <tr align="center">
            <td width="250"></td>
            <td>Суммарная комиссия</td>
            <td>${number(value:(report.records.sum{ it.comission }?:0.0g))}</td>
            <td width="100"></td>
          </tr>
          <tr align="center">
            <td width="250"></td>
            <td>Остаток клиентских средств на конец периода</td>
            <td>${number(value:endclientsaldo)}</td>
            <td width="100"></td>
          </tr>
        </tbody>
      </table>
      <table style="width:690px;font-size:6pt">
        <tbody>
          <tr align="center"><th colspan="6">Детализация по подклиентам за период</th></tr>
          <tr align="center">
            <th>Подклиент</th>
            <th>Сумма прихода</th>
            <th>Сумма вывода</th>
            <th>Сумма комиссий</th>
            <th>Сумма к возврату комиссии</th>
            <th>Сумма возврата комиссии</th>
          </tr>
        <g:each in="${report.records.groupBy{it.subclient_id}}" status="i" var="record">
        <g:if test="${record.key>0}">
          <tr align="center">
            <td>${clients[record.key]}</td>
            <td>${number(value:(record.value.sum{ it.computeIncome() }?:0.0g))}</td>
            <td>${number(value:(record.value.sum{ it.computeOutlay() }?:0.0g))}</td>
            <td>${number(value:(record.value.sum{ it.comission }?:0.0g))}</td>
            <td>${number(value:(record.value.sum{ it.subcomission }?:0.0g))}</td>
            <td>${number(value:(record.value.sum{ it.is_clientcommission?it.clientdelta:0.0g }?:0.0g))}</td>
          </tr>
        </g:if>
        </g:each>
        </tbody>
      </table>
    </div>
  </body>
</html>