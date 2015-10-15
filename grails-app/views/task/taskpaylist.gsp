<style type="text/css">
  tr.yellow > td { background:lightyellow !important }
  tr.blue > td { background:#def !important }
</style>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult?.count?:0}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult?.count?:0}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${searchresult?.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Код</th>
          <th>Срок исполнения</th>
          <th>Компания</th>
          <th>Банк</th>
          <th>Сумма</th>
          <th>Группа</th>
          <th>Исполнитель</th>
          <th>Акцепт</th>
          <th>Статус выполнения</th>
          <th width="50"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center" class="${record.is_urgent?'yellow':''}">
          <td>${record.id}</td>
          <td>${record.term?String.format('%td.%<tm.%<tY',record.term):'нет'}</td>
          <td>${record.company_name}</td>
          <td>${record.bank_name}</td>
          <td>${number(value:record.summa)}</td>
          <td>${record.paygroup==1?'Бюджет':record.paygroup==2?'Кредиты и КП':record.paygroup==3?'Аренда':'Общая'}</td>
          <td>${record.executor_name}</td>
          <td>
          <g:if test="${record.is_accept}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>${taskpaystatuses[record.taskpaystatus]}</td>
          <td>
            <a class="button" href="${createLink(controller:'task',action:'taskpaydetail',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          <g:if test="${iscandelete&&record.taskpaystatus in [0,1,5]&&record.is_manual}">
            &nbsp; <g:remoteLink class="button" style="z-index:1" before="if(!confirm('Вы действительно хотите удалить задание и все платежи по нему?')) return false" url="${[controller:controllerName,action:'deletetaskpay',id:record.id]}" title="Удалить задание" onSuccess="\$('form_submit_button').click()"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult?.count?:0}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult?.count?:0}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
