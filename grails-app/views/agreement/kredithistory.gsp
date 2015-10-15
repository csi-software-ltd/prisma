<style type="text/css">
  .list td,.list th { font-size: 12px }
</style>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th rowspan="2">Дата</th>
          <th rowspan="2">Сумма<br/>Ставка</th>
          <th rowspan="2">Срок кредита</th>
          <th rowspan="2">Пояснение к сроку кредита</th>
          <th rowspan="2">Тип кредита</th>
          <th colspan="4">Статусы</th>
          <th rowspan="2">Ответственный</th>
          <th rowspan="2">Автор</th>
        </tr>
        <tr>
          <th>Реал</th>
          <th>Техн</th>
          <th>Реалтех</th>
          <th>Нал.дог.</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${history}" status="i" var="record">
        <tr align="center">
          <td>${shortDate(date:record.inputdate)}</td>
          <td>${number(value:record.summa)}<br/>${number(value:record.rate)}</td>
          <td>${String.format('%td.%<tm.%<tY',record.enddate)}</td>
          <td>${record.dopagrcomment}</td>
          <td>${record.kredtype==1?'Кредит':record.kredtype==2?'Кредитная линия':'Овердрафт'}</td>
          <td>
          <g:if test="${record.is_real}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
          <g:if test="${record.is_tech}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
          <g:if test="${record.is_realtech}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
          <g:if test="${record.is_agr}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>${record.responsible_name}</td>
          <td>${record.admin_name}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>