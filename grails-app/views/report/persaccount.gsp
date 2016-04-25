<style type="text/css">
  abbr { vertical-align: middle; }
</style>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${report.count}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${report.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${report.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>ФИО</th>
          <th>Банк</th>
          <th>Номер счета</th>
          <th>Тип</th>
          <th>Срок действия</th>
          <th>Статус</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${report.records}" var="record">
        <tr align="center">
          <td><g:link style="z-index:1" controller="user" action="persdetail" id="${record.pers_id}" target="_blank">${record.shortname}</g:link></td>
          <td>${record.toStringBankname()}</td>
          <td>${g.account(value:record.paccount)}</td>
          <td>${record.is_main?'Основная':'Дополнительная'}</td>
          <td>${record?.validmonth}/${record?.validyear}</td>
          <td>
            <g:if test="${record.modstatus}"><abbr title="Активная"><i class="icon-ok"></i></abbr></g:if>
            <g:else><abbr title="Неактивная"><i class="icon-minus"></i></abbr></g:else>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${report.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>