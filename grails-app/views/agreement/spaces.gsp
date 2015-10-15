<style type="text/css">
  table.list thead th, table.list tbody th, table.list thead td, table.list tbody td { font-size: 11px }
  tr.yellow > td { background:lightyellow !important }
  tr.disabled > td { background:silver !important; opacity:0.7 !important }
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
          <th>Код</th>
          <th>Арендатор</th>
          <th>Арендодатель</th>
          <th>Тип помещений</th>
          <th>Признак аренды</th>
          <th>Адрес</th>
          <th>Договор<br/>Дата<br/>Срок</th>
          <th>Тип аренды</th>
          <th>Плата</th>
          <th>Долг<br/>Доп. долг</th>
          <th>Возмож-<br/>ность<br/>платежа</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr class="${record.modstatus<1?'disabled':record.arendatype_id==1?'yellow':''}" align="center">
          <td>${record.id}</td>
          <td>${record.arendator_name}</td>
          <td>${record.arendodatel_name}</td>
          <td>${spacetypes[record.spacetype_id]}</td>
          <td>${record.asort?'аренда':!record.subsub_id?'субаренда':'субсубаренда'}</td>
          <td>${record.shortaddress}</td>
          <td>${record.anumber}<br/>${record.adate?String.format('%td.%<tm.%<tY',record.adate):'нет'}<br/>${record.enddate?String.format('%td.%<tm.%<tY',record.enddate):'нет'}</td>
          <td>${arendatypes[record.arendatype_id]}</td>
          <td>${number(value:record.rate)}</td>
          <td style="${debts[record.id].maindebt>0||debts[record.id].dopdebt>0?'color:red':''}"><g:if test="${debts[record.id].maindebt>0}">${number(value:debts[record.id].maindebt)}</g:if><g:else>нет</g:else><br/><g:if test="${debts[record.id].dopdebt>0}">${number(value:debts[record.id].dopdebt)}</g:if><g:else>нет</g:else></td>
          <td>
          <g:if test="${record.paystatus}"><abbr title="возможно"><i class="icon-check"></i></abbr></g:if>
          <g:else><abbr title="невозможно"><i class="icon-ban-circle"></i></abbr></g:else>
          </td>
          <td>
            <a class="button" href="${g.createLink(controller:controllerName,action:'space',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
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
