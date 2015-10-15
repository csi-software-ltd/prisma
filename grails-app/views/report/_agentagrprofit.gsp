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
    <div style="float:left;font-size:9pt">Доходы по агентским договорам за ${inrequest.report_year?:2014} год от ${String.format('%td.%<tm.%<tY',new Date())}</div>
    <div style="clear:both;text-align:center"></div><br/>
    <div>
    <g:if test="${!inrequest.type}">
      <table style="width:1020px;font-size:7pt">
        <tbody>
          <tr align="center">
            <th>Клиент</th>
            <th>Банк</th>
          <g:each in="${report.groupBy{new Date(it.year-1900,it.month-1,1)}}" var="dates">
            <th width="60">${String.format('%tB',dates.key)}</th>
          </g:each>
            <th>Итого</th>
          </tr>
        <g:each in="${report.groupBy{it.agentagr_id}}" var="record">
          <tr align="center">
            <td>${agrs[record.key].client_name}</td>
            <td>${agrs[record.key].bankname}</td>
          <g:each in="${report.groupBy{new Date(it.year-1900,it.month-1,1)}}" var="dates">
            <td>${number(value:record.value.find{ dates.key==new Date(it.year-1900,it.month-1,1)}?.profit?:0.0g)}</td>
          </g:each>
            <td>${number(value:record.value.groupBy{new Date(it.year-1900,it.month-1,1)}.collect{ it.value[0] }.sum{ it.profit })}</td>
          </tr>
        </g:each>
          <tr align="center">
            <th colspan="2">ИТОГО</th>
          <g:each in="${report.groupBy{new Date(it.year-1900,it.month-1,1)}}" var="dates">
            <th width="60">${number(value:dates.value.groupBy{it.agentagr_id}.collect{ it.value[0] }.sum{ it.profit })}</th>
          </g:each>
            <th>${number(value:report.groupBy{it.agentagr_id}.collect{ it.value }.sum{ it.groupBy{new Date(it.year-1900,it.month-1,1)}.collect{ it.value[0] }.sum{ it.profit }})}</th>
          </tr>
        </tbody>
      </table>
    </g:if><g:else>
      <table style="width:1020px;font-size:7pt">
        <tbody>
          <tr align="center">
            <th>Клиент</th>
            <th>Банк</th>
            <th width="80">I</th>
            <th width="80">II</th>
            <th width="80">III</th>
            <th width="80">IV</th>
            <th>Итого</th>
          </tr>
        <g:each in="${report.groupBy{it.agentagr_id}}" var="record">
          <tr align="center">
            <td>${agrs[record.key].client_name}</td>
            <td>${agrs[record.key].bankname}</td>
          <g:each in="${1..4}" var="season">
            <td>${number(value:record.value.findAll{ season==(it.month in 1..3? 1 : it.month in 4..6? 2 : it.month in 7..9? 3 : 4)}.groupBy{new Date(it.year-1900,it.month-1,1)}.collect{ it.value[0] }.sum{ it.profit }?:0.0g)}</td>
          </g:each>
            <td>${number(value:record.value.groupBy{new Date(it.year-1900,it.month-1,1)}.collect{ it.value[0] }.sum{ it.profit })}</td>
          </tr>
        </g:each>
          <tr align="center">
            <th colspan="2">ИТОГО</th>
          <g:each in="${1..4}" var="season">
            <th width="80">${number(value:report.findAll{ season==(it.month in 1..3? 1 : it.month in 4..6? 2 : it.month in 7..9? 3 : 4) }.groupBy{it.agentagr_id}.collect{ it.value }.sum{ it.groupBy{new Date(it.year-1900,it.month-1,1)}.collect{ it.value[0] }.sum{ it.profit }})}</th>
          </g:each>
            <th>${number(value:report.groupBy{it.agentagr_id}.collect{ it.value }.sum{ it.groupBy{new Date(it.year-1900,it.month-1,1)}.collect{ it.value[0] }.sum{ it.profit }})}</th>
          </tr>
        </tbody>
      </table>
    </g:else>
    </div>
  </body>
</html>