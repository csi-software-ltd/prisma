<style type="text/css">
  table.list thead th, table.list tbody th, table.list thead td, table.list tbody td { font-size: 11px }
  tr.yellow > td { background:lightyellow !important }
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
          <th class="tbl" rowspan="2">&nbsp;</th>          
          <th rowspan="2">Код</th>
          <th rowspan="2">Клиент</th>
          <th rowspan="2">Банк</th>
          <th rowspan="2">Договор<br/>Дата<br/>Окончание</th>
          <th rowspan="2">Сумма<br/>Ставка<br/>Факт. задолженность</th>
          <th rowspan="2">Тип кредита</th>
          <th colspan="6">Статусы</th>
          <th rowspan="2"></th>
        </tr>
        <tr>        
          <th>Реал</th>
          <th>Техн</th>
          <th>Реалтех</th>
          <th>Залог</th>
          <th>Уступка</th>
          <th>Дог.</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center" class="${!record.is_check?'yellow':''}">
          <td class="tbl"></td>
          <td>${record.id}</td>
          <td><g:if test="${record.creditor_name}">${record.creditor_name} (${record.client_name})</g:if><g:else>${record.client_name}</g:else></td>
          <td>${record.bank_name}</td>
          <td>${record.anumber}<br/>${String.format('%td.%<tm.%<tY',record.adate)}<br/>${String.format('%td.%<tm.%<tY',record.enddate)}</td>
          <td nowrap>${number(value:record.summa)}<i class="icon-${valutas[record.valuta_id]}"></i><br/>${number(value:record.rate)}<br/>${number(value:debts[record.id])}</td>
          <td>${record.kredtype==1?'Кредит':record.kredtype==2?'Кредитная линия':record.kredtype==3?'Овердрафт':'Линия с лимитом задолженности'}</td>
          <td>
          <g:if test="${record.is_real}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
          <g:if test="${record.is_tech}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
          <g:if test="${record.is_realtech}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
          <g:if test="${record.zalogstatus==1}"><abbr title="Нет"><i class="icon-minus"></i></abbr></g:if>
          <g:elseif test="${record.zalogstatus==2}"><abbr title="Да"><i class="icon-plus"></i></abbr></g:elseif>
          </td>
          <td>
          <g:if test="${record.cessionstatus}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
          <g:if test="${record.is_agr}"><abbr title="есть договор"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="нет договора"><i class="icon-ban-circle"></i></abbr></g:else>
          </td>
          <td>
            <a class="button" href="${g.createLink(controller:controllerName,action:'kredit',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
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
