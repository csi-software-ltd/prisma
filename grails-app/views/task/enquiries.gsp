<style type="text/css">
  tr.green > td { background:lightgreen !important }
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
          <th>Дата</th>
          <th>Компания</th>
          <th>Куда</th>
          <th>Тип</th>
          <th>На дату</th>
          <th>Дата получения</th>
          <th>Статус</th>
          <th width="30"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center" class="${record.modstatus==2?'green':''}">
          <td>${String.format('%td.%<tm.%<tY',record.startdate?:record.inputdate)}</td>
          <td>${record.company_name}</td>
          <td>
          <g:if test="${record.bank_name}">${record.bank_name}</g:if>
          <g:elseif test="${record.inspection_district}">${record.inspection_district}&nbsp;${record.inspection_name}</g:elseif>
          <g:else>${record.inspection_name}</g:else>
          </td>
          <td>${enqtypes[record.enqtype_id]}</td>
          <td>${String.format('%td.%<tm.%<tY',record.ondate)}</td>
          <td>${String.format('%td.%<tm.%<tY',record.enddate?:record.termdate)}</td>
          <td>
          <g:if test="${record.modstatus==1}"><abbr title="Принята"><i class="icon-ok icon-large"></i></abbr></g:if>
          <g:elseif test="${record.modstatus==2}"><abbr title="Выдана"><i class="icon-flag-checkered icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.modstatus==3}"><abbr title="Требуется перезапрос"><i class="icon-repeat icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.modstatus==-1}"><abbr title="Отказ"><i class="icon-minus icon-large"></i></abbr></g:elseif>
          <g:else><abbr title="Новая"><i class="icon-exclamation icon-large"></i></abbr></g:else>
          </td>
          <td>
            <a class="button" href="${g.createLink(controller:controllerName,action:'enquiry',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
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
