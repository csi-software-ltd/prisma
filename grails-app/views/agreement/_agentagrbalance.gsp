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
    <div>
      <div style="float:left;font-size:9pt">Баланс расчетов по агентскому договору "${agentagr.name}" от ${String.format('%td.%<tm.%<tY',new Date())}</div><br/>
      <div style="float:left;font-size:9pt">Клиент ${client.name}</div><br/>
      <div style="float:left;font-size:9pt;width:690px">
        Банки:&nbsp;
        <g:each in="${banks}"><span>${it}</span><br/></g:each>
      </div>
      <div style="clear:both;text-align:center"></div><br/>
      <table style="width:690px;font-size:9pt">
        <tbody>
          <tr align="center">
            <th>Месяц</th>
            <th>Комиссия</th>
            <th>Оплата за месяц</th>
            <th>Даты оплат</th>
            <th>Баланс по месяцу</th>
            <th>Итоговый баланс</th>
          </tr>
        <g:each in="${acts}" var="act">
          <tr align="left">
            <td>${String.format('%tB %<tY',new Date(act.year-1900,act.month-1,1))}</td>
            <td>${number(value:act.summa+act.summafix)}</td>
            <td>${number(value:(paysummas[act.id].payrequests.sum{it.clientcommission}?:0.0g)+(paysummas[act.id].cash.sum{it.summa}?:0.0g)+(paysummas[act.id].agentfix.sum{it.summa}?:0.0g))}</td>
            <td>${((paysummas[act.id].payrequests.collect{String.format('%td.%<tm.%<tY',it.paydate)}?:[])+(paysummas[act.id].cash.collect{String.format('%td.%<tm.%<tY',it.operationdate)})+(paysummas[act.id].agentfix.collect{String.format('%td.%<tm.%<tY',it.paydate)})).sort().join(', ')}</td>
            <td>${number(value:act.summa+act.summafix-((paysummas[act.id].payrequests.sum{it.clientcommission}?:0.0g)+(paysummas[act.id].cash.sum{it.summa}?:0.0g)+(paysummas[act.id].agentfix.sum{it.summa}?:0.0g)))}</td>
          <%totalbalance+=(act.summa+act.summafix-((paysummas[act.id].payrequests.sum{it.clientcommission}?:0.0g)+(paysummas[act.id].cash.sum{it.summa}?:0.0g)+(paysummas[act.id].agentfix.sum{it.summa}?:0.0g)))%>
            <td>${number(value:totalbalance)}</td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
  </body>
</html>