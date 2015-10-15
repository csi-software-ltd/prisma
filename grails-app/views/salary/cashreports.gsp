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
          <th>Месяц, год начисления</th>
          <th>Отдел</th>
          <th>Дата выплаты</th>
          <th>Сумма ведомости</th>
          <th>Подтверждено</th>
          <th>Статус</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center">
          <td>${String.format('%tB %<tY',new Date(record.year-1900,record.month-1,1))}</td>
          <td>${departments[record.department_id]}</td>
          <td>${String.format('%td.%<tm.%<tY',record.repdate)}</td>
          <td>${number(value:record.summa)}</td>
          <td>
          <g:if test="${record.is_confirm==1}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:elseif test="${record.is_confirm==-1}"><abbr title="На согласовании"><i class="icon-refresh"></i></abbr></g:elseif>
          <g:elseif test="${record.is_confirm==-2}"><abbr title="Отказано"><i class="icon-ban-circle"></i></abbr></g:elseif>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>${record.modstatus==1?'К выплате':record.modstatus==2?'Закрыта':'Новая'}</td>
          <td>
            <a class="button" href="${g.createLink(controller:controllerName,action:'cashreport',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
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