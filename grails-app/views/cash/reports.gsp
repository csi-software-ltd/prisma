<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${reports.count}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${reports.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${reports.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата отчета</th>
          <th>Дата подтверждения</th>
          <th>Подотчетное лицо</th>
          <th>Сумма</th>
          <th>Тип расходов</th>
          <th>Статус</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${reports.records}" status="i" var="record">
        <tr align="center" style="${!record.modstatus||(record.modstatus==1&&session.user.cashaccess in [1,3])||(record.modstatus<0&&session.user.cashaccess in [1,2])?'color:red':''}">
          <td>${String.format('%td.%<tm.%<tY',record.repdate)}</td>
          <td>${record.confirmdate?String.format('%td.%<tm.%<tY',record.confirmdate):'нет'}</td>
          <td>${record.executor_name}<g:if test="${record.type}"><br/>${departments[record.department_id]}</g:if></td>
          <td>${intnumber(value:record.summa)}</td>
          <td>${expensetypes[record.expensetype_id]}<g:if test="${record.description}"><br/>${record.description}</g:if></td>
          <td>${cashstatus[record.modstatus]}</td>
          <td width="50">
            <a class="button" href="${g.createLink(controller:'cash',action:'cashreport',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          <g:if test="${record.file_id}">
            &nbsp;&nbsp;<a class="button" style="z-index:1" href="${createLink(controller:'cash', action:'showscan', id:record.file_id, params:[code:Tools.generateModeParam(record.file_id)])}" title="Скан документа" target="_blank"><i class="icon-picture"></i></a>
          </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${reports.count}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${reports.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>