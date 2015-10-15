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
    <div style="float:left;font-size:9pt">Расширенный отчет по доходам по агентским договорам${reportstart?' с '+String.format('%tB %<tY',reportstart):''}${reportend?' по '+String.format('%tB %<tY',reportend):''} от ${String.format('%td.%<tm.%<tY',new Date())}</div>
    <div style="clear:both;text-align:center"></div><br/>
    <div>
    <g:if test="${!inrequest.type}">
      <table style="width:1020px;font-size:7pt">
        <tbody>
          <tr align="center">
            <th>Клиент</th>
            <th>Банк</th>
            <th>Заемщик</th>
            <th>Договор</th>
          <g:each in="${report.groupBy{new Date(it.year-1900,it.month-1,1)}}" var="dates">
            <th width="60">${String.format('%tB',dates.key)}</th>
          </g:each>
            <th>Итого</th>
          </tr>
        <g:each in="${report.groupBy{it.agentkredit_id}}" var="record">
          <tr align="center">
            <td>${agrs[record.value[0].agentagr_id].client_name}</td>
            <td>${agrs[record.value[0].agentagr_id].bankname}</td>
            <td>${record.value[0].clientname}</td>
            <td>${record.value[0].anumber} от ${String.format('%td.%<tm.%<tY',record.value[0].adate)}</td>
          <g:each in="${report.groupBy{new Date(it.year-1900,it.month-1,1)}}" var="dates">
            <td>${number(value:record.value.find{ dates.key==new Date(it.year-1900,it.month-1,1)}?.recieveProfit()?:0.0g)}</td>
          </g:each>
            <td>${number(value:record.value.sum{ it.recieveProfit() }?:0.0g)}</td>
          </tr>
        </g:each>
          <tr align="center">
            <th colspan="4">ИТОГО</th>
          <g:each in="${report.groupBy{new Date(it.year-1900,it.month-1,1)}}" var="dates">
            <th width="60">${number(value:dates.value.sum{ it.recieveProfit() }?:0.0g)}</th>
          </g:each>
            <th>${number(value:report.sum{ it.recieveProfit() }?:0.0g)}</th>
          </tr>
        </tbody>
      </table>
    </g:if><g:else>
      <table style="width:1020px;font-size:7pt">
        <tbody>
          <tr align="center">
            <th>Клиент</th>
            <th>Банк</th>
            <th>Заемщик</th>
            <th>Договор</th>
          <g:each in="${report.groupBy{new Date(it.year-1900,(it.month in 1..3? 1 : it.month in 4..6? 2 : it.month in 7..9? 3 : 4),1)}}" var="season">
            <th width="80">${(season.key.getMonth()==1?'I':season.key.getMonth()==2?'II':season.key.getMonth()==3?'III':'IV')+'.'+(season.key.getYear()+1900)}</th>
          </g:each>
            <th>Итого</th>
          </tr>
        <g:each in="${report.groupBy{it.agentkredit_id}}" var="record">
          <tr align="center">
            <td>${agrs[record.value[0].agentagr_id].client_name}</td>
            <td>${agrs[record.value[0].agentagr_id].bankname}</td>
            <td>${record.value[0].clientname}</td>
            <td>${record.value[0].anumber} от ${String.format('%td.%<tm.%<tY',record.value[0].adate)}</td>
          <g:each in="${report.groupBy{new Date(it.year-1900,(it.month in 1..3? 1 : it.month in 4..6? 2 : it.month in 7..9? 3 : 4),1)}}" var="season">
            <td>${number(value:record.value.findAll{ season.key==new Date(it.year-1900,(it.month in 1..3? 1 : it.month in 4..6? 2 : it.month in 7..9? 3 : 4),1)}.sum{ it.recieveProfit() }?:0.0g)}</td>
          </g:each>
            <td>${number(value:record.value.sum{ it.recieveProfit() }?:0.0g)}</td>
          </tr>
        </g:each>
          <tr align="center">
            <th colspan="4">ИТОГО</th>
          <g:each in="${report.groupBy{new Date(it.year-1900,(it.month in 1..3? 1 : it.month in 4..6? 2 : it.month in 7..9? 3 : 4),1)}}" var="season">
            <th width="80">${number(value:season.value.sum{ it.recieveProfit() })}</th>
          </g:each>
            <th>${number(value:report.sum{ it.recieveProfit() }?:0.0g)}</th>
          </tr>
        </tbody>
      </table>
    </g:else>
    </div>
  </body>
</html>