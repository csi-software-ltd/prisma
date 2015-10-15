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
    <div style="float:left;font-size:9pt">Окончание срока БК</div>
    <div style="float:right;font-size:7pt">${String.format('%td.%<tm.%<tY %<tH:%<tM',new Date())}</div><br/>
    <div style="clear:both;text-align:center"></div><br/>
    <div>
      <table style="width:1020px;font-size:9pt">
        <tbody>
          <tr align="center">
            <th>Банк</th>
            <th>Компания</th>
            <th>Тип счета</th>
            <th>Номер счета</th>
            <th>Дата активации бк</th>
            <th>Срок действия бк</th>
            <th>Статус бк</th>
            <th>Директор в компании</th>
            <th>Директор по сведениям банка</th>
          </tr>
        <g:each in="${searchresult}" status="i" var="record">
          <tr align="center">
            <td>${record.shortname}</td>
            <td>${record.cname}</td>
            <td>${record.typeaccount_id==1?'расчетный':record.typeaccount_id==2?'корпоративный':record.typeaccount_id==3?'текущий':record.typeaccount_id==4?'транзитный':'накопительный'}</td>
            <td>${record.schet}</td>
            <td>${String.format('%td.%<tm.%<tY',record.ibank_open)}</td>
            <td>${String.format('%td.%<tm.%<tY',record.ibank_close)}</td>
            <td>
            <g:if test="${record.ibankstatus==1}">активен</g:if>
            <g:elseif test="${record.ibankstatus==2}">просрочен</g:elseif>
            <g:elseif test="${record.ibankstatus==-1}">заблокирован</g:elseif>
            <g:else>нет</g:else>
            </td>
            <td>${record.gd}</td>
            <td>${Pers.get(record.pers_id)?.shortname}</td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
  </body>
</html>