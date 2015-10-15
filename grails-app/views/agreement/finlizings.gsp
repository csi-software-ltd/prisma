<style type="text/css">
  table.list thead th, table.list tbody th, table.list thead td, table.list tbody td { font-size: 11px }
  tr.yellow > td { background:lightyellow !important }
</style>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult.count}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${searchresult.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Код</th>
          <th>Лизингополучатель</th>
          <th>Лизингодатель</th>
          <th>Банк лизингодателя</th>
          <th>Номер<br/>Дата<br/>Окончание</th>
          <th>Сумма</th>
          <th>Ставка</th>
          <th>Текущий остаток</th>
          <th>Баланс</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center">
          <td>${record.id}</td>
          <td>${record.flpoluchatel_name}</td>
          <td>${record.fldatel_name}</td>
          <td>${record.bankcompany_name}</td>
          <td>${record.anumber}<br/>${String.format('%td.%<tm.%<tY',record.adate)}<br/>${String.format('%td.%<tm.%<tY',record.enddate)}</td>
          <td>${number(value:record.summa)}</td>
          <td>${number(value:record.rate)}</td>
          <td nowrap>${number(value:summaries[record.id].bodydebt)}</td>
          <td nowrap>${number(value:summaries[record.id].balance)}</td>
          <td>
            <a class="button" href="${g.createLink(controller:controllerName,action:'finlizing',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.count}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>