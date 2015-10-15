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
      table td { border-bottom: 1px solid #000; border-right: 1px solid #000; width: 99px }
    </style>
  </head>
  <body style="width:690px">
    <div>
      <div style="float:left;font-size:9pt">Сверка с ${agent.name} по кредитам клиента ${client.name} в ${bank.name} за ${String.format('%tB %<tY',new Date(act.year-1900,act.month-1,1))} от ${String.format('%td.%<tm.%<tY',act.inputdate)}</div><br/>
      <div style="clear:both;text-align:center"></div><br/>
      <table style="width:690px;font-size:9pt">
        <tbody>
          <tr align="left">
            <td colspan="6">Начисленно за период</td>
            <td>${number(value:act.summa)}</td>
          </tr>
        <g:each in="${curPeriods}">
          <tr align="left">
            <td colspan="2" align="right" style="padding-right:15px">${contragents[it.kredit_id]}</td>
            <td>${number(value:it.plan_debt)}</td>
            <td>${number(value:it.agent_rate)}%</td>
            <td>${!it.is_sub?'агент':it.subtype?'подагент по прибыли':'подагент по СС'}</td>
            <td>от ${String.format('%td.%<tm.%<tY',it.datestart)} до ${String.format('%td.%<tm.%<tY',it.dateend)}</td>
            <td>${number(value:it.computeAgentSumma())}</td>
          </tr>
        </g:each>
          <tr align="left">
            <td colspan="6">Начисленно за предыдущие периоды</td>
            <td colspan="1">${number(value:act.summaprev)}</td>
          </tr>
          <tr align="left">
            <td colspan="6">Корректировка по предыдущим периодам</td>
            <td colspan="1">${number(value:act.summafix)}</td>
          </tr>
          <tr align="left">
            <td colspan="6">Оплаченная сумма</td>
            <td colspan="1">${number(value:act.paid)}</td>
          </tr>
        <g:each in="${payments}">
          <tr align="left">
          <g:if test="${it.class==Cash.class}">
            <td colspan="4" align="right" style="padding-right:25px">${String.format('%td.%<tm.%<tY',it.operationdate)}</td>
            <td colspan="2">Нал</td>
            <td>${number(value:it.type==1?it.summa:-it.summa)}</td>
          </g:if><g:else>
            <td colspan="4" align="right" style="padding-right:25px">${String.format('%td.%<tm.%<tY',it.paydate)}</td>
            <td colspan="2">Безнал</td>
            <td>${number(value:it.paytype==1?it.summa:it.agentcommission)}</td>
          </g:else>
          </tr>
        </g:each>
          <tr align="left">
            <td colspan="6">Корректировка по агентским выплатам</td>
            <td colspan="1">${number(value:act.agentfix)}</td>
          </tr>
        <g:each in="${fixes}">
          <tr align="left">
            <td colspan="4" align="right" style="padding-right:25px">${String.format('%td.%<tm.%<tY',it.paydate)}</td>
            <td colspan="2">${it.agent_name}</td>
            <td>${number(value:it.summa)}</td>
          </tr>
        </g:each>
          <tr align="left">
            <td colspan="6">Итого к оплате</td>
            <td colspan="1">${number(value:act.summa+act.summaprev+act.summafix-act.paid-act.agentfix)}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </body>
</html>