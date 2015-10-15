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
          <th>БИК</th>
          <th>Название</th>
          <th>Город</th>
          <th>Дата отзыва<br/>лицензии</th>
          <th>Телефон</th>
          <th>Контактная<br/>информация</th>
          <th width="30"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="left" style="${record.is_license&&!record.is_sanation?'':'color:red'}">
          <td>${record.id}</td>
          <td>${record.name}</td>
          <td>${record.city}</td>
          <td align="center">${record.stopdate?String.format('%td.%<tm.%<tY',record.stopdate):'нет'}</td>
          <td>${record.tel}</td>
          <td>${record.contactinfo}</td>
          <td align="center">
            <a class="button" href="${g.createLink(controller:'catalog',action:'bankdetail',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
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
