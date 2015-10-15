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
    </style>
  </head>
  <body style="width:1020px">
    <div style="float:left;font-size:9pt">Отчет по справкам за ${String.format('%tm.%<tY',reportdate)}</div>
    <div style="float:right;font-size:7pt">${String.format('%td.%<tm.%<tY %<tH:%<tM',new Date())}</div><br/>
    <div style="clear:both;text-align:center"></div><br/>
    <div>
      <table style="width:1020px;font-size:9pt">
        <tbody>
          <tr align="center">
            <td style="background:#F0E68C">Запросов подано</td>
            <td style="background:#F0E68C">Должно быть получено</td>
            <td style="background:#F0E68C">Полученные</td>
            <td style="background:#F0E68C">Отказы</td>
          </tr>
          <tr align="center">
            <td>${newenquiries}</td>
            <td>${expectedenquiries}</td>
            <td>${receivedenquiries}</td>
            <td width="100">${denyenquiries}</td>
          </tr>
        </tbody>
      </table>
      <div style="clear:both;text-align:center"></div><br/>
      <table style="width:1020px;font-size:9pt">
        <tbody>
          <tr align="center">
            <td style="background:#F0E68C" colspan="4">Получено справок по типам:</td>
          </tr>
        <g:each in="${enqtypes}">
          <tr align="center">
            <td width="300"></td>
            <td>${it.name}</td>
            <td>${it.count}</td>
            <td width="100"></td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
  </body>
</html>