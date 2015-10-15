<style type="text/css">
  tr.pink > td { background:lightpink !important }
</style>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${depcashrecords.count}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${depcashrecords.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${depcashrecords.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата операции</th>
          <th>Сумма</th>
          <th>Тип</th>
          <th>Класс</th>
          <th>Кому</th>
          <th>Остаток<br/>подотчетных средств</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${depcashrecords.records}" status="i" var="record">
        <tr align="center" class="${record.summa<0?'pink':''}">
          <td>${String.format('%td.%<tm.%<tY',record.operationdate)}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td>${record.type==1?'выдача':record.type==2?'получение':record.type==3?'возврат':record.type==4?'возврат в гл. кассу':record.type==5?'начисление':'отчет'}</td>
          <td>${record.cashclass==1?'зарплата':record.cashclass==2?'подотчет':record.cashclass==3?'заем':record.cashclass==4?'расход':record.cashclass==5?'штраф':'прочее'}<g:if test="${record.expensetype_id}"><br/>${expensetypes[record.expensetype_id]}</g:if></td>
          <td><g:if test="${!(record.pers_fio?:record.pers_name)}">${departments[record.department_id]}</g:if><g:else>${record.pers_fio?:record.pers_name}</g:else><g:if test="${record.comment}"><br/>${record.comment}</g:if></td>
          <td>${intnumber(value:record.saldo)}</td>
          <td width="50">
            <a class="button" href="${g.createLink(controller:'cash',action:'depcashrecord',id:record.id)}" title="Детали"><i class="icon-pencil"></i></a>
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
    <span class="fleft">Найдено: ${depcashrecords.count}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${depcashrecords.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>