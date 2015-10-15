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
    <div style="float:left;font-size:9pt">Отчет по сделке на ${String.format('%td.%<tm.%<tY',new Date())}. Клиент ${client?.name}</div>
    <div style="clear:both;text-align:center"></div><br/>
    <div>
      <table style="width:690px;font-size:8pt">
        <tbody>
          <tr align="center">
            <td>Дата начала операций по сделке</td>
            <td>${String.format('%td.%<tm.%<tY',deal.dstart)}</td>
          </tr>
          <tr align="center">
            <td>Дата окончания операций по сделке</td>
            <td>${String.format('%td.%<tm.%<tY',deal.dend)}</td>
          </tr>
          <tr align="center">
            <td>Суммарный приход</td>
            <td>${number(value:deal.income)}</td>
          </tr>
          <tr align="center">
            <td>Суммарный вывод</td>
            <td>${number(value:deal.outlay)}</td>
          </tr>
          <tr align="center">
            <td>Начислено комиссионных к возврату</td>
            <td>${number(value:deal.subcommission)}</td>
          </tr>
          <tr align="center">
            <td>Возврат комиссионных</td>
            <td>${number(value:deal.retcommission)}</td>
          </tr>
          <tr align="center">
            <td>Сумма списаний по агентским договорам</td>
            <td>${number(value:deal.repayment)}</td>
          </tr>
          <tr align="center">
            <td>Комиссия</td>
            <td>${number(value:deal.commission)}</td>
          </tr>
          <tr align="center">
            <td>Остаток по сделке (итоговое сальдо)</td>
            <td>${number(value:deal.dealsaldo)}</td>
          </tr>
          <tr align="center">
            <td>Статус согласования сделки</td>
            <td>${deal.modstatus?'согласована':'новая сделка'}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </body>
</html>