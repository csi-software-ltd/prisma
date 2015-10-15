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
          <th>Код</th>
          <th>Цедент</th>
          <th>Цессионарий</th>
          <th>Должник</th>
          <th>Тип<br/>Класс<br/>Вариант</th>
          <th>Договор</th>
          <th>Договор<br/>Дата</th>
          <th>Сумма<br/>Валюта</th>
          <th>Статус</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center">
          <td>${record.id}</td>
          <td>${record.bank_name?:record.cedent_name}</td>
          <td>${record.cessionary_name}</td>
          <td>${record.debtor_name}</td>
          <td>${record.cessiontype==1?'Цессия':'Перевод долга'}<br/>${record.changetype==1?'С внешней':record.changetype==2?'На внешнюю':'Внутренняя смена'}<br/>${record.cessionvariant==1?'Кредит':'Лизинг'}</td>
          <td><g:link controller="agreement" action="${record.cessionvariant==1?'kredit':'lizing'}" id="${record.agr_id}">${record.agr_id}</g:link></td>
          <td>${record.anumber}<br/>${String.format('%td.%<tm.%<tY',record.adate)}</td>
          <td>${number(value:record.summa)}<br/>${valutacodes[record.valuta_id]}</td>
          <td>
          <g:if test="${record.modstatus}"><abbr title="активный"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="неактивный"><i class="icon-ban-circle"></i></abbr></g:else>
          </td>
          <td>
            <a class="button" href="${g.createLink(controller:controllerName,action:'cession',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
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