<style type="text/css">
  .list td,.list th { font-size: 10px !important }
</style>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата</th>
          <th>Название<br/>адрес</th>
          <th>Налоговая</th>
          <th>ОКТМО</th>
          <th>КПП</th>
          <th>ОКАТО</th>
          <th>Тел.</th>
          <th>Уставной капитал<br/>Дата<br/>Обеспечение<br/>Оплата</th>
          <th>Налоги</th>
          <th>ПФР<br/>ФСС</th>
          <th>Статус</th>
          <th>Автор</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${history}" status="i" var="record">
        <tr align="center">
          <td width="40">${shortDate(date:record.inputdate)}</td>
          <td>${record.legalname}<g:if test="${record.namedate}">&nbsp;(${String.format('%td.%<tm.%<tY',record.namedate)})</g:if><br/>${record.legaladr}<g:if test="${record.adrdate}">&nbsp;(${String.format('%td.%<tm.%<tY',record.adrdate)})</g:if></td>
          <td>${record.taxinspection_id}</td>
          <td>${record.oktmo}</td>
          <td>${record.kpp}</td>
          <td>${record.okato}</td>
          <td>${record.tel}</td>
          <td>${intnumber(value:record.capital)}<br/>${record.capitaldate?String.format('%td.%<tm.%<tY',record.capitaldate):'нет'}<br/>${record.capitalsecure==1?'Имуществом':'Деньгами'}<br/>${record.capitalpaid==1?'Да':'Нет'}</td>
          <td>${taxoptions[record.taxoption_id]}</td>
          <td>${record.pfrfreg}<br/>${record.fssreg}</td>
          <td>${statuses[record.activitystatus_id]}</td>
          <td>${record.admin_name}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>