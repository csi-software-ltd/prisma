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
          <th>Займодавец</th>
          <th>Заемщик</th>
          <th>Тип займа</th>
          <th>Договор<br/>Дата<br/>Выдача</th>
          <th>Сумма<br/>Ставка<br/>Задолженность</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center">
          <td>${record.lender_name?:record.lenderpers_name}</td>
          <td>${record.client_name?:record.clientpers_name}</td>
          <td>${record.loantype==1?'Заем у внешней':record.loantype==2?'Выдача внешней':record.loantype==3?'Внутренний займ':record.loantype==4?'Займ учредителя':'Займ работнику'}</td>
          <td>${record.anumber}<br/>${String.format('%td.%<tm.%<tY',record.adate)}<br/>${String.format('%td.%<tm.%<tY',record.startdate)}</td>
          <td nowrap>${intnumber(value:record.summa)}<i class="icon-${valutas[record.valuta_id]}"></i><br/>${number(value:record.rate)}<br/>${intnumber(value:record.debt)}</td>
          <td>
            <a class="button" href="${g.createLink(controller:controllerName,action:'loan',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
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