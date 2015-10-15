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
    <div style="float:left;font-size:9pt">Отчет по остаткам c ${String.format('%td.%<tm.%<tY',reportstart)} по ${String.format('%td.%<tm.%<tY',reportend)}</div>
    <div style="float:right;font-size:7pt">${String.format('%td.%<tm.%<tY %<tH:%<tM',new Date())}</div><br/>
    <div style="clear:both;text-align:center"></div><br/>
    <div>
      <table style="width:1020px;font-size:9pt">
        <tbody>
          <tr align="center">
            <th>Дата</th>
            <th>Банк</th>
            <th>Компания</th>
            <th>Факт. сальдо</th>
            <th>СС банка</th>
            <th>СС компании</th>
            <th>Подтв. сальдо</th>
          </tr>
        <g:each in="${searchresult.records}" status="i" var="record">
          <tr align="center" class="${record.s2_saldo!=null&&record.actsaldo!=record.s2_saldo?'yellow':''}">
            <td>${String.format('%td.%<tm.%<tY',record.actsaldodate)}</td>
            <td>${record.bankname}<br/><i>${valutas[record.valuta_id]}</i>&nbsp;&nbsp;&nbsp;${record.typeaccount_id==1?'расчетный':record.typeaccount_id==2?'корпоративный':record.typeaccount_id==3?'текущий':record.typeaccount_id==4?'транзитный':'накопительный'}</td>
            <td>${record.company_name}</td>
            <td>${number(value:record.actsaldo)}</td>
            <td>${number(value:record.banksaldo)}<g:if test="${record.banksaldodate}"><br/>${String.format('%td.%<tm.%<tY',record.banksaldodate)}</g:if></td>
            <td>${number(value:record.actsaldo-record.banksaldo)}</td>
            <td>${number(value:record.s2_saldo)}<g:if test="${record.s2_saldodate}"><br/>${String.format('%td.%<tm.%<tY',record.s2_saldodate)}</g:if></td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
  </body>
</html>