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
          <th>Заказчик</th>
          <th>Исполнитель</th>
          <th>Тип договора</th>
          <th>Признак договора</th>
          <th>Договор<br/>Дата<br/>Срок</th>
          <th>Сумма</th>
          <th>Порядок оплаты</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center">
          <td>${record.id}</td>
          <td>${record.zcompany_name}</td>
          <td>${record.ecompany_name}</td>
          <td>${stypes[record.atype]}</td>
          <td>${record.asort==1?'Внешний':record.asort==2?'Внутренний':'Для внешних'}</td>
          <td>${record.anumber}<br/>${record.adate?String.format('%td.%<tm.%<tY',record.adate):'нет'}<br/>${record.enddate?String.format('%td.%<tm.%<tY',record.enddate):'нет'}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td>${record.paycondition==1?'Ежемесячно':record.paycondition==2?'Ежеквартально':'Без оплаты'}</td>
          <td>
            <a class="button" href="${g.createLink(controller:controllerName,action:'service',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
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