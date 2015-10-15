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
    <div style="float:left;font-size:9pt">Активность пользователей<g:if test="${inrequest.reportstart}"> c ${String.format('%td.%<tm.%<tY',inrequest.reportstart)}</g:if> по ${String.format('%td.%<tm.%<tY',inrequest.reportend)}</div>
    <div style="float:right;font-size:7pt">${String.format('%td.%<tm.%<tY %<tH:%<tM',new Date())}</div><br/>
    <div style="clear:both;text-align:center"></div><br/>
    <div>
      <table style="width:1020px;font-size:9pt">
        <tbody>
          <tr align="center">
            <th>Пользователь</th>
            <th>Отдел</th>
            <th>Успешные заходы</th>
            <th>Неуспешные заходы</th>
          </tr>
        <g:each in="${searchresult.records}" status="i" var="record">
          <tr align="left">
            <td>${record.shortname}</td>
            <td>${record.depname}</td>
            <td align="center">${record.suc_count}</td>
            <td align="center">${record.unsuc_count}</td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
  </body>
</html>