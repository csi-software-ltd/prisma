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
    <div style="float:left;font-size:9pt">Отчет по зарплате за месяц. ${String.format('%tB %<tY',inrequest.repdate)}.</div>
    <div style="clear:both;text-align:center"></div><br/>
    <div>
      <table style="width:1020px;font-size:7pt">
        <tbody>
        <tr align="center">
          <th rowspan="2">Департамент</th>
          <th rowspan="2">Отдел</th>
          <th rowspan="2">ФИО</th>
          <th rowspan="2">Факт. оклад</th>
          <th rowspan="2">Аванс</th>
          <th rowspan="2">Официальный б/н</th>
          <th rowspan="2">Б/н вне ведомости</th>
          <th rowspan="2">Бонус</th>
          <th rowspan="2">Штраф</th>
          <th rowspan="2">Переработка</th>
          <th rowspan="2">Отпускные</th>
          <th rowspan="2">Отп. перерасчет</th>
          <th rowspan="2">Нал до срока</th>
          <th rowspan="2">Сумма к нал. выплате</th>
          <th colspan="3">Статусы оплаты</th>
        </tr>
        <tr align="center">
          <th>Аванс</th>
          <th>Б/Н</th>
          <th>Итог</th>
        </tr>
      <g:each in="${searchresult.records}" var="record">
        <tr align="center">
          <td>${record.parent?departments[record.parent]:''}</td>
          <td>${record.d_name}</td>
          <td>${record.p_shortname}</td>
          <td>${number(value:record.actsalary)}</td>
          <td>${intnumber(value:record.prepayment)}</td>
          <td>${number(value:record.offsalary)}</td>
          <td>${number(value:record.prevfix)}</td>
          <td>${intnumber(value:record.bonus)}</td>
          <td>${intnumber(value:record.shtraf)}</td>
          <td>${intnumber(value:record.overloadsumma)}</td>
          <td>${intnumber(value:record.holiday)}</td>
          <td>${intnumber(value:record.reholiday)}</td>
          <td>${intnumber(value:record.precashpayment)}</td>
          <td>${intnumber(value:record.cash)}</td>
          <td>
          <g:if test="${record.prepaystatus==2}">Да</g:if>
          <g:elseif test="${record.prepaystatus==1}">Начислено</g:elseif>
          <g:else>Нет</g:else>
          <g:if test="${record.prepaydate}"><br/>${String.format('%td.%<tm.%<tY',record.prepaydate)}</g:if>
          </td>
          <td>
          <g:if test="${record.offstatus==2}">Да"><i class="icon-ok"></i></abbr></g:if>
          <g:elseif test="${record.offstatus==1}">Начислено</g:elseif>
          <g:else>Нет</g:else>
          <g:if test="${record.offdate}"><br/>${String.format('%td.%<tm.%<tY',record.offdate)}</g:if>
          </td>
          <td>
          <g:if test="${record.cashstatus==2}">Да"><i class="icon-ok"></i></abbr></g:if>
          <g:elseif test="${record.cashstatus==1}">Начислено</g:elseif>
          <g:else>Нет</g:else>
          <g:if test="${record.cashdate}"><br/>${String.format('%td.%<tm.%<tY',record.cashdate)}</g:if>
          </td>
        </tr>
      </g:each>
        </tbody>
      </table>
    </div>
  </body>
</html>