<style type="text/css">
  tr.yellow > td { background:lightyellow !important }
  tr.pink > td { background:lightpink !important }
</style>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${maincashrecords.count}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="10" total="${maincashrecords.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${maincashrecords.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Код</th>
          <th>Дата операции</th>
          <th>Сумма</th>
          <th>Тип</th>
          <th>Класс</th>
          <th>Кому или От кого</th>
          <th>Остаток</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${maincashrecords.records}" status="i" var="record">
        <tr align="center" class="${expensetypes[record.expensetype_id]?.type==0?'yellow':record.summa<0?'pink':''}">
          <td>${record.id}</td>
          <td>${String.format('%td.%<tm.%<tY',record.operationdate)}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td>${record.type==1?'выдача':record.type==2?'получение':record.type==3?'возврат':record.type==4?'Финансирование':'Начисление'}</td>
          <td>${cashclasses[record.cashclass]}<g:if test="${record.expensetype_id}"><br/>${expensetypes[record.expensetype_id]}</g:if></td>
          <td>${record.agent_id?agents[record.agent_id]:record.agentagr_id?(Agentagr.get(record.agentagr_id)?.name?:''):record.pers_fio?:record.pers_name?:record.department_id?departments[record.department_id]:'нет'}<g:if test="${record.comment}"><br/>${record.comment}</g:if></td>
          <td>${intnumber(value:record.saldo)}</td>
          <td width="50">
            <a class="button" href="${g.createLink(controller:'cash',action:'maincashrecord',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          <g:if test="${record.receipt}">
            &nbsp;&nbsp;<a class="button" style="z-index:1" href="${createLink(controller:'cash', action:'showscan', id:record.receipt, params:[code:Tools.generateModeParam(record.receipt)])}" title="Скан документа" target="_blank"><i class="icon-picture"></i></a>
          </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${maincashrecords.count}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="10" total="${maincashrecords.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>