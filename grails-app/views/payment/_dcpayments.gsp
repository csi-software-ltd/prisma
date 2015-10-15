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
    <div style="float:left;font-size:9pt">Выплаты на доп.карты за ${String.format('%tB %<tY',inrequest.repdate)}.</div>
    <div style="clear:both;text-align:center"></div><br/>
    <div>
      <table style="width:1020px;font-size:8pt">
        <tbody>
          <tr align="center">
            <th>Банк</th>
            <th>Компания</th>
            <th>Работник</th>
            <th>Номер карты</th>
            <th>PIN код</th>
            <th>Сумма</th>
            <th>Статус</th>
          </tr>
        <g:each in="${searchresult.records}" status="i" var="record">
          <tr align="center">
            <td>${record.bankname}</td>
            <td>${record.companyname}</td>
            <td>${record.fio}</td>
            <td>${record.nomer}</td>
            <td>${record.pin}</td>
            <td>${number(value:record.cardadd)}</td>
            <td>${record.paidaddstatus==1?'В оплате':record.paidaddstatus==2?'Оплачено':'Новый'}</td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
  </body>
</html>