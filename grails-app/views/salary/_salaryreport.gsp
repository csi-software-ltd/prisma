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
      table th { border-bottom: 2px solid #000; border-right: 1px solid #000 }
      table td { border-bottom: 1px solid #000; border-right: 1px solid #000 }
    </style>
  </head>
  <body style="width:690px">
    <div style="text-align:center;font-size:9pt">${salaryreport.salarytype_id==1?'Авансовая':'Итоговая'} ведомость. ${salaryreport.department_id?department?.name?.capitalize():'Директора'}. ${String.format('%tB %<tY',new Date(salaryreport.year-1900,salaryreport.month-1,1))}</div><br/>
    <div style="float:left">
      <table style="width:330px;font-size:9pt">
        <thead>
          <tr align="center">
            <th>Фамилия</th>
            <th>Дата</th>
            <th>Подпись</th>
          </tr>
        </thead>
        <tbody>
        <g:each in="${salaries}" var="sal" status="i">
        <g:if test="${i<45}">
          <tr align="left">
            <td>${sal.shortname}</td>
            <td width="60"></td>
            <td width="60"></td>
          </tr>
        </g:if>
        </g:each>
        </tbody>
      </table>
    </div>
  <g:if test="${salaries.size()>45}">
    <div style="float:right">
      <table style="width:330px;font-size:9pt;margin-left:30px">
        <thead>
          <tr align="center">
            <th>Фамилия</th>
            <th>Дата</th>
            <th>Подпись</th>
          </tr>
        </thead>
        <tbody>
        <g:each in="${salaries}" var="sal" status="i">
        <g:if test="${i>44}">
          <tr align="left">
            <td>${sal.shortname}</td>
            <td width="60"></td>
            <td width="60"></td>
          </tr>
        </g:if>
        </g:each>
        </tbody>
      </table>
    </div>
  </g:if>
  </body>
</html>