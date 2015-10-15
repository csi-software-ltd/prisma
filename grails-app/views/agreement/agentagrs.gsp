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
          <th>Название</th>
          <th>Клиент</th>
          <th>Банки</th>
          <th>Кол-во кредитов</th>
          <th>Суммарный портфель</th>
          <th>Остаток задолженности</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center">
          <td>${record.name}</td>
          <td>${clients[record.client_id]}</td>
          <td><g:each in="${banks[record.id]}">${it}<br/></g:each></td>
          <td>${record.kreditcount}</td>
          <td><g:if test="${record.sumrub>0}">${number(value:record.sumrub)}<i class="icon-rub"></i></g:if><g:if test="${record.sumusd>0}"><br/>${number(value:record.sumusd)}<i class="icon-usd"></i></g:if><g:if test="${record.sumeur>0}"><br/>${number(value:record.sumeur)}<i class="icon-eur"></i></g:if></td>
          <td>${number(value:record.lastbodydebt)}</td>
          <td>
            <a class="button" href="${g.createLink(controller:controllerName,action:'agent',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          <g:if test="${iscanedit&&record.kreditcount==0}">
            <br/><g:remoteLink style="z-index:1;margin-top:7px" class="button" url="${[controller:'agreement', action:'deleteagentagr', id:record.id]}" title="Удалить" onSuccess="\$('form_submit_button').click();"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
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