<style type="text/css">
  .list td,.list th { font-size: 12px }
</style>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult.count}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${searchresult.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>ФИО</th>
          <th>Месяц, год начисления</th>
          <th>Факт. заплата</th>
          <th>Премия</th>
          <th>Штраф</th>
          <th>Переработка</th>
          <th>Отпускные</th>
          <th>Аванс</th>
          <th>На карту</th>
          <th>На руки</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center">
          <td>${record.shortname}</td>
          <td>${String.format('%tB %<tY',new Date(record.year-1900,record.month-1,1))}</td>
          <td>${number(value:record.actsalary)}</td>
          <td>${intnumber(value:record.bonus)}</td>
          <td>${intnumber(value:record.shtraf)}</td>
          <td>${intnumber(value:record.overloadsumma)}</td>
          <td>${intnumber(value:record.holiday)}</td>
          <td>${intnumber(value:record.prepayment)}</td>
          <td>${number(value:record.cardmain)}</td>
          <td>${intnumber(value:record.cash)}</td>
          <td>
            <a class="button" href="${g.createLink(controller:controllerName,action:'printmysalary',id:record.id)}" title="Распечатать" target="_blank"><i class="icon-print"></i></a>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.count}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>