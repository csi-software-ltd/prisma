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
    <div style="float:left;font-size:9pt">Отчет по неисполненным заявкам на платежи.</div>
    <div style="float:right;font-size:7pt">${String.format('%td.%<tm.%<tY %<tH:%<tM',new Date())}</div><br/>
    <div style="clear:both;text-align:center"></div><br/>
    <div>
      <table style="width:1020px;font-size:7pt">
        <tbody>
        <tr align="center">
          <th>Срок платежа</th>
          <th>Дата акцепта</th>
          <th>Компания</th>
          <th>Банк</th>
          <th>Тип платежа</th>
          <th>Контрагент</th>
          <th>Назначение</th>
          <th>Сумма</th>
          <th>Клиент</th>
          <th>Проект</th>
          <th>Статья расходов</th>
          <th>Остаток на счете</th>
        </tr>
      <g:each in="${searchresult.records}" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td>${record.acceptdate?String.format('%td.%<tm.%<tY',record.acceptdate):'нет'}</td>
          <td>${record.fromcompany}</td>
          <td>${record.bank_name}</td>
          <td>${record.paytype==1?'исходящий':'внутренний'}</td>
          <td>${record.tocompany}</td>
          <td><g:shortString length="50" text="${record.destination}"/></td>
          <td>${number(value:record.summa)}</td>
          <td>${clients[record.client_id]}</td>
          <td>${projects[record.project_id]}</td>
          <td>${exptypes[record.id]}</td>
          <td>${number(value:saldos[record.bankaccount_id])}</td>
        </tr>
      </g:each>
        </tbody>
      </table>
    </div>
  </body>
</html>