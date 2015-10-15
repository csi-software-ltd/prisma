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
          <th>Покупатель</th>
          <th>Поставщик</th>
          <th>Тип договора</th>
          <th>Договор<br/>Дата</th>
          <th>Тип оплаты</th>
          <th>Категория</th>
          <th>Сумма</th>
          <th>Долг<br/>Дата</th>
          <th>Статус</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center">
          <td>${record.client_name}</td>
          <td>${record.supplier_name}</td>
          <td>${record.tradetype?'Поставки':'Услуги'}</td>
          <td>${record.anumber}<br/>${String.format('%td.%<tm.%<tY',record.adate)}</td>
          <td>${record.paytype?'Единовременно':'Регулярные платежи'}</td>
          <td>${tradecats[record.tradecat_id]}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td style="${record.debt>0?'color:red':''}">${intnumber(value:record.debt)}<br/>${record.debtdate?String.format('%td.%<tm.%<tY',record.debtdate):'нет'}</td>
          <td>
          <g:if test="${record.modstatus}"><abbr title="активный"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="неактивный"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
            <a class="button" href="${g.createLink(controller:controllerName,action:'trade',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
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