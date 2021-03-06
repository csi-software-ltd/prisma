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
        size: 29.7cm 21cm;
      }
      body { font-family: "Arial Unicode MS", Arial, sans-serif; }
      table { border-top: 2px solid #000; border-left: 1px solid #000; }
      table td { border-bottom: 1px solid #000; border-right: 1px solid #000; }
      table th { border-bottom: 1px solid #000; border-right: 1px solid #000; }
      table tr.yellow > td { background:#FFFFE0 !important }
      table tr > th { background:#FFD700 !important }
    </style>
  </head>
  <body style="width:1020px">
    <div style="float:left;font-size:9pt">Справка об общей кредитной задолженности по банкам по состоянию на ${String.format('%td.%<tm.%<tY',new Date())}.</div>
    <div style="clear:both;text-align:center"></div><br/>
    <div>
      <table style="width:1020px;font-size:8pt">
        <tbody>
          <tr align="center">
            <th>№</th>
            <th>Наименование банка</th>
            <th>Сумма задолженности по кредитам</th>
            <th>Доля в общей задолженности</th>
          </tr>
        <g:each in="${report}" var="record" status="i">
          <tr align="center">
            <td>${i+1}</td>
            <td>${record.bname}</td>
            <td>${number(value:record.debt)}</td>
            <td>${number(value:record.debt/totaldebt*100)}%</td>
          </tr>
        </g:each>
          <tr align="center">
            <td></td>
            <td>Всего</td>
            <td>${number(value:totaldebt)}</td>
            <td>100%</td>
          </tr>
        </tbody>
      </table>
    </div>
  </body>
</html>