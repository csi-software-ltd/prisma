<style type="text/css">
  .list td,.list th { font-size: 12px }
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
          <th>Лицензиат</th>
          <th>Лицензиар</th>
          <th>Направление</th>
          <th>Номер договора</th>
          <th>Срок договора</th>
          <th>Вступительный<br/>взнос</th>
          <th>Сумма допуска<br/>Оплаченый допуск</th>
          <th>Тип оплаты<br/>вступ. взноса</th>
          <th>Членские взносы</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center">
          <td><g:link controller="company" action="detail" id="$record.company_id" target="_blank">${record.company_name}</g:link></td>
          <td><g:link controller="company" action="detail" id="$record.sro_id" target="_blank">${record.sro_name}</g:link></td>
          <td>${industries[record.industry_id]}</td>
          <td>${record.anumber}</td>
          <td><g:rawHtml>${record.adate?String.format('%td.%<tm.%<tY',record.adate)+'<br/>':''}</g:rawHtml>${record.enddate?String.format('%td.%<tm.%<tY',record.enddate):'бессрочно'}</td>
          <td>${intnumber(value:record.entryfee)}</td>
          <td>${intnumber(value:record.alimit)}<br/>${intnumber(value:record.paidfee)}</td>
          <td>${record.paytype==1?'Единовременно':record.paytype==2?'График':''}</td>
          <td>${intnumber(value:record.regfee)}</td>
          <td>
            <a class="button" href="${g.createLink(controller:controllerName,action:'license',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
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