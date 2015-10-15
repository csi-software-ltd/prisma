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
    <div style="float:left;font-size:9pt"><g:if test="${allreport}">Справка по директорам на ${String.format('%td.%<tm.%<tY',reportdate)}.</g:if><g:else>Справка по директорам компании ${company?.name}</g:else></div>
    <div style="clear:both;text-align:center"></div><br/>
    <div>
      <table style="width:1020px;font-size:8pt">
        <tbody>
        <g:if test="${allreport}">
          <tr align="center">
            <th>№</th>
            <th>Фио директора</th>
            <th>Компания</th>
            <th>Дата вступления в должность</th>
          </tr>
        </g:if><g:else>
          <tr align="center">
            <th>Фио директора</th>
            <th>Паспортные данные</th>
            <th>Образование</th>
            <th>Дата вступления в должность</th>
            <th>Дата завершения деятельности</th>
          </tr>
        </g:else>
        <g:each in="${allreport}" var="record" status="i">
          <tr align="center">
            <td>${i+1}</td>
            <td>${record.shortname}</td>
            <td>${record.position_name}</td>
            <td>${String.format('%td.%<tm.%<tY',record.jobstart)}</td>
          </tr>
        </g:each>
        <g:each in="${compreport}" var="record" status="i">
          <tr align="center">
            <td>${record.shortname}</td>
            <td>${record.collectPassData()}</td>
            <td>${record.education}</td>
            <td>${String.format('%td.%<tm.%<tY',record.jobstart)}</td>
            <td>${record.jobend?String.format('%td.%<tm.%<tY',record.jobend):'нет'}</td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
  </body>
</html>