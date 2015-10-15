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
      table td { border-bottom: 1px solid #000; border-right: 1px solid #000; width: 138px }
    </style>
  </head>
  <body style="width:690px">
    <div>
      <table style="width:690px;font-size:9pt">
        <tbody>
          <tr align="left">
            <td colspan="3">${pers.shortname}</td>
            <td colspan="2">за ${String.format('%tB %<tY',new Date(salary.year-1900,salary.month-1,1))}</td>
          </tr>
          <tr align="left">
            <td colspan="3">Факт. оклад</td>
            <td colspan="2">${number(value:salary.actsalary)}</td>
          </tr>
          <tr align="left">
            <td colspan="3">Премия</td>
            <td colspan="2">${intnumber(value:salary.bonus)}</td>
          </tr>
          <tr align="left">
            <td colspan="3">Штраф</td>
            <td colspan="2">${intnumber(value:salary.shtraf)}</td>
          </tr>
          <tr align="left">
            <td colspan="3">Переработка</td>
            <td>${salary.overloadhour} дн.</td>
            <td>${intnumber(value:salary.overloadsumma)} руб.</td>
          </tr>
          <tr align="left">
            <td colspan="3">Отпускные</td>
            <td colspan="2">${salary.holiday?intnumber(value:salary.holiday):salary.reholiday?intnumber(value:-salary.reholiday):0}</td>
          </tr>
          <tr align="left">
            <td colspan="2" style="border-right: 0px">Аванс</td>
            <td>${salary.prepaydate?String.format('%td.%<tm.%<tY',salary.prepaydate):''}</td>
            <td colspan="2">${intnumber(value:salary.prepayment)}</td>
          </tr>
          <tr align="left">
            <td colspan="2" style="border-right: 0px">На карту</td>
            <td>${salary.offdate?String.format('%td.%<tm.%<tY',salary.offdate):''}</td>
            <td colspan="2">${number(value:salary.cardmain)}</td>
          </tr>
        <g:if test="${salarycomps}">
          <tr align="left">
            <td style="border-right: 0px">Из них</td>
            <td colspan="4"></td>
          </tr>
        <g:each in="${salarycomps}">
          <tr align="left">
            <td></td>
            <td>${it.companyname}</td>
            <td>${it.position}</td>
            <td colspan="2">${number(value:it.cardmain)}</td>
          </tr>
        </g:each>
        </g:if>
          <tr align="left">
            <td colspan="2" style="border-right: 0px">На руки</td>
            <td>${salary.cashdate?String.format('%td.%<tm.%<tY',salary.cashdate):''}</td>
            <td colspan="2">${intnumber(value:salary.cash)}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </body>
</html>