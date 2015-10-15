<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${mycashrecords.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${mycashrecords.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата</th>
          <th>Сумма</th>
          <th>Тип</th>
          <th>Класс</th>
          <th>Кому</th>
          <th>Остаток<br/>подотчетных средств</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${mycashrecords.records}" status="i" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.inputdate)}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td>${record.type==1?'выдача':record.type==2?'получение':record.type==3?'возврат':record.type==4?'возврат в гл. кассу':record.type==5?'начисление':'отчет'}</td>
          <td>${record.cashclass==1?'зарплата':record.cashclass==2?'подотчет':record.cashclass==3?'заем':record.cashclass==4?'расход':'прочее'}</td>
          <td><g:if test="${record.department_id}">${department?.name}<br/></g:if><g:if test="${record.pers_name}">${record.pers_name}</g:if><g:if test="${record.comment}"><br/>${record.comment}</g:if></td>
          <td>${intnumber(value:record.saldo)}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${mycashrecords.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>