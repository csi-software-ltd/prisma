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
          <th>Заказчик</th>
          <th>Исполнитель</th>
          <th>Договор<br/>Дата<br/>Срок</th>
          <th>Тип работ</th>
          <th>Признак договора</th>
          <th>Сумма</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center">
          <td>${record.client_name}</td>
          <td>${record.supplier_name}</td>
          <td>${smrcats[record.smrcat_id]}</td>
          <td>${record.smrsort==1?'Внешний подряд':record.smrsort==2?'Внутренний подряд':'Внешний заказчик'}</td>
          <td>${record.anumber}<br/>${record.adate?String.format('%td.%<tm.%<tY',record.adate):'нет'}<br/>${record.enddate?String.format('%td.%<tm.%<tY',record.enddate):'нет'}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td>
            <a class="button" href="${g.createLink(controller:controllerName,action:'smr',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
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