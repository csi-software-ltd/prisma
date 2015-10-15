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
    <div style="float:left;font-size:9pt">Отчет по движению средств по доп.картам на ${String.format('%td.%<tm.%<tY',new Date())}.</div>
    <div style="clear:both;text-align:center"></div><br/>
    <div>
      <table style="width:1020px;font-size:8pt">
        <tbody>
          <tr align="center">
            <th>ФИО</th>
            <th>Фикс. оплата</th>
            <th>За ген.<br/>директора</th>
            <th>За главбуха</th>
            <th>За договора</th>
            <th>Текущий оклад</th>
          </tr>
        <g:each in="${directors}" var="record">
          <tr align="center">
            <td>${record.shortname}</td>
            <td>
              <g:if test="${record.is_fixactsalary}">Да</g:if><g:else>Нет</g:else>
            </td>
            <td>
              ${intnumber(value:bonusrates.gdbonus*dirdetails[record.id].gdcompanies.size())}
              <g:each in="${dirdetails[record.id].gdcompanies}" var="company">
                <br/>${company.name} с ${String.format('%td.%<tm.%<tY',dirdetails[record.id].jobstartdates[company.id])}
              </g:each>
            </td>
            <td>
              ${intnumber(value:dirdetails[record.id].gbcompanies.size()?bonusrates.gbbonus:0)}
              <g:each in="${dirdetails[record.id].gbcompanies}" var="company">
                <br/>${company.name}
              </g:each>
            </td>
            <td>
              ${intnumber(value:bonusrates.agrbonus*dirdetails[record.id].agrcount)}
              <g:each in="${dirdetails[record.id].agrcomplist}" var="agr">
                <br/>${Company.get(agr.class==Kredit?agr.client:agr.class==Cession?agr.cessionary:agr.arendator)?.name}&nbsp;-&nbsp;${agr.class.toString()-'class '}&nbsp;-&nbsp;${String.format('%td.%<tm.%<tY',agr.adate)}
              </g:each>
            </td>
            <td>${intnumber(value:record.actsalary)}</td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
  </body>
</html>