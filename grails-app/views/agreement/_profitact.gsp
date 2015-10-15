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
      table td { border-bottom: 1px solid #000; border-right: 1px solid #000; width: 115px }
    </style>
  </head>
  <body style="width:690px">
    <div>
      <div style="float:left;font-size:9pt">Доходы по кредитам клиента ${client.name} в ${bank.name} за ${String.format('%tB %<tY',new Date(act.year-1900,act.month-1,1))} от ${String.format('%td.%<tm.%<tY',act.inputdate)}</div><br/>
      <div style="clear:both;text-align:center"></div><br/>
      <table style="width:690px;font-size:9pt">
        <tbody>
          <tr align="left">
            <td colspan="5">Доходы за период(СС)</td>
            <td>${number(value:curPeriods?.sum{it.recieveSS()}?:0)}</td>
          </tr>
        <g:each in="${curPeriods}">
          <tr align="left">
            <td colspan="3" align="right" style="padding-right:15px">${contragents[it.agentkredit_id]}</td>
            <td align="center">${number(value:it.recieveSSpercent())}%</td>
            <td align="center">с ${String.format('%td.%<tm.%<tY',it.datestart)}<br/>по ${String.format('%td.%<tm.%<tY',it.dateend)}</td>
            <td>${number(value:it.recieveSS())}</td>
          </tr>
        </g:each>
          <tr align="left">
            <td colspan="5">Доходы за предыдущие периоды</td>
            <td colspan="1">${number(value:act.profitprev+act.costprev)}</td>
          </tr>
          <tr align="left">
            <td colspan="5">Задолженность перед агентами</td>
            <td colspan="1">${number(value:agentacts?.sum{ it.summa + it.summaprev + it.summafix - it.paid - act.agentfix}?:0)}</td>
          </tr>
          <tr align="left">
            <td colspan="5">Расчетная себестоимость</td>
            <td colspan="1">${number(value:act.cost+act.costprev)}</td>
          </tr>
          <tr align="left">
            <td colspan="5">Итого доход(СС)</td>
            <td colspan="1">${number(value:act.profit+act.profitprev+act.cost+act.costprev)}</td>
          </tr>
          <tr align="left">
            <td colspan="5">Итого прибыль</td>
            <td colspan="1">${number(value:act.profit+act.profitprev)}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </body>
</html>