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
            <th colspan="4">Сводка за месяц:</th>
          </tr>
          <tr align="center">
            <td width="300"></td>
            <td>Сумма поступлений по клиентским платежам</td>
            <td>${number(value:statistic.income)}</td>
            <td width="100"></td>
          </tr>
          <tr align="center">
            <td width="300"></td>
            <td>Количество входящих платежей</td>
            <td>${intnumber(value:statistic.incomecount)}</td>
            <td width="100"></td>
          </tr>
          <tr align="center">
            <td width="300"></td>
            <td>Сумма вывода по клиентским платежам</td>
            <td>${number(value:statistic.outlay)}</td>
            <td width="100"></td>
          </tr>
          <tr align="center">
            <td width="300"></td>
            <td>Количество исходящих платежей</td>
            <td>${intnumber(value:statistic.outlaycount)}</td>
            <td width="100"></td>
          </tr>
          <tr align="center">
            <td width="300"></td>
            <td>Суммарная комиссия</td>
            <td>${number(value:statistic.comission)}</td>
            <td width="100"></td>
          </tr>
          <tr align="center">
            <td width="300"></td>
            <td>Сумма к возврату комиссионных</td>
            <td>${number(value:statistic.subcomission)}</td>
            <td width="100"></td>
          </tr>
          <tr align="center">
            <td width="300"></td>
            <td>Сумма возврата комиссионных</td>
            <td>${number(value:statistic.retcomission)}</td>
            <td width="100"></td>
          </tr>
          <tr align="center">
            <td width="300"></td>
            <td>Сумма списаний</td>
            <td>${number(value:statistic.repayment)}</td>
            <td width="100"></td>
          </tr>
          <tr align="center">
            <td width="300"></td>
            <td>Остаток клиентских средств на начало месяца</td>
            <td>${number(value:statistic.startclientsaldo)}</td>
            <td width="100"></td>
          </tr>
          <tr align="center">
            <td width="300"></td>
            <td>Остаток клиентских средств на конец месяца</td>
            <td>${number(value:statistic.endclientsaldo)}</td>
            <td width="100"></td>
          </tr>
        </tbody>
      </table>
      <table style="width:690px;font-size:6pt">
        <tbody>
          <tr align="center">
            <th>Клиент</th>
            <th>Сумма прихода</th>
            <th>Сумма вывода</th>
            <th>Сумма к возврату комиссии</th>
            <th>Сумма возврата комиссии</th>
            <th>Сумма списаний</th>
            <th>Сумма комиссий</th>
            <th>Остаток счета</th>
          </tr>
        <g:each in="${report}" status="i" var="record">
          <tr align="center" style="${record.curclientsaldo+record.dinclientsaldo<0?'color:red':''}">
            <td>${record.client_name}</td>
            <td>${number(value:record.income?:0.0g)}</td>
            <td>${number(value:record.outlay?:0.0g)}</td>
            <td>${number(value:record.clsubcomission?:0.0g)}</td>
            <td>${number(value:record.clretcomission?:0.0g)}</td>
            <td>${number(value:record.clrepayment?:0.0g)}</td>
            <td>${number(value:record.clcomission?:0.0g)}</td>
            <td>${number(value:record.curclientsaldo+record.dinclientsaldo)}</td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
  </body>
</html>