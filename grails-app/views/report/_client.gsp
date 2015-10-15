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
    <div style="float:left;font-size:9pt">Отчет по клиенту ${client?.name} за ${String.format('%tB %<tY',reportdate)}</div>
    <div style="clear:both;text-align:center"></div><br/>
    <div>
      <table style="width:690px;font-size:8pt">
        <tbody>
          <tr align="center">
            <td>Сумма поступлений клиентских средств</td>
            <td>${number(value:statistic.income)}</td>
          </tr>
          <tr align="center">
            <td>Количество входящих платежей</td>
            <td>${intnumber(value:statistic.incomecount)}</td>
          </tr>
          <tr align="center">
            <td>Сумма вывода клиентских средств</td>
            <td>${number(value:statistic.outlay)}</td>
          </tr>
          <tr align="center">
            <td>Количество исходящих платежей</td>
            <td>${intnumber(value:statistic.outlaycount)}</td>
          </tr>
          <tr align="center">
            <td>Суммарная комиссия</td>
            <td>${number(value:statistic.comission)}</td>
          </tr>
          <tr align="center">
            <td>Остаток клиентских средств на начало месяца</td>
            <td>${number(value:statistic.startclientsaldo)}</td>
          </tr>
          <tr align="center">
            <td>Остаток клиентских средств на конец месяца</td>
            <td>${number(value:statistic.endclientsaldo)}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </body>
</html>