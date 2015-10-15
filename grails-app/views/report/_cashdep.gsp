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
    <div style="float:left;font-size:9pt">Отчет по остаткам касс</div>
    <div style="clear:both;text-align:center"></div><br/>
    <div>
      <table style="width:690px;font-size:8pt">
        <tbody>
          <tr align="center">
            <th><g:if test="${reporttype}">ФИО</g:if><g:else>Наименование отдела</g:else></th>
          <g:if test="${depreport}">
            <th>Сальдо отдела</th>
            <th>Сальдо сотрудников</th>
          </g:if><g:elseif test="${userreport}">
            <th>Тип</th>
          </g:elseif>
            <th>Итого</th>
          </tr>
        <g:each in="${depreport}" var="record">
          <tr>
            <td>${departments[record.id]}</td>
            <td align="center">${number(value:record.saldo?:0)}</td>
            <td align="center">${number(value:record.depusersaldo?:0)}</td>
            <td align="center">${number(value:(record.saldo?:0)+(record.depusersaldo?:0))}</td>
          </tr>
        </g:each>
        <g:each in="${userreport}" var="record">
          <tr>
            <td>${record.pers_name}</td>
            <td align="center">${record.cashaccess==1?'подотчетное лицо':'кассир холдинга'}</td>
            <td align="center">${number(value:record.depusersaldo)}</td>
          </tr>
        </g:each>
        <g:each in="${loanreport}" var="record">
          <tr>
            <td>${record.pers_name}</td>
            <td align="center">${number(value:record.loansaldo)}</td>
          </tr>
        </g:each>
        <g:each in="${penaltyreport}" var="record">
          <tr>
            <td>${record.pers_name}</td>
            <td align="center">${number(value:record.penalty)}</td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
  </body>
</html>