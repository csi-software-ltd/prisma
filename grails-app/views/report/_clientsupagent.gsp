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
    <div style="float:left;font-size:9pt">Агентские начисления и выплаты по агенту ${client?.name} с даты ${String.format('%td.%<tm.%<tY',reportdate)}</div>
    <div style="clear:both;text-align:center"></div><br/>
    <div>
      <table style="width:690px;font-size:6pt">
        <tbody>
          <tr align="center">
            <th>Начислено на выплату</th>
            <th>Выплачено</th>
            <th>Не оплачено</th>
          </tr>
          <tr align="center">
            <td>${number(value:summary.accrued)}</td>
            <td>${number(value:summary.paid)}</td>
            <td>${number(value:summary.accrued-summary.paid)}</td>
          </tr>
        </tbody>
      </table>
      <table style="width:690px;font-size:6pt">
        <tbody>
          <tr align="center">
            <th>дата начисления</th>
            <th>месяц начисления</th>
            <th>дата списания</th>
            <th>сумма начисления</th>
            <th>сумма списания</th>
          </tr>
        <g:each in="${report}" status="i" var="record">
          <tr align="center">
            <td>${String.format('%td.%<tm.%<tY',record.inputdate)}</td>
            <td>${record.platperiod}</td>
            <td>${record.execdate?String.format('%td.%<tm.%<tY',record.execdate):'нет'}</td>
            <td>${number(value:record.summa)}</td>
            <td>${number(value:record.agentcommission)}</td>
          </tr>
        </g:each>
        <g:if test="${!report}">
          <tr>
            <td colspan="5" style="text-align:center">
              Платежей не найдено
            </td>
          </tr>
        </g:if>
        </tbody>
      </table>
    </div>
  </body>
</html>