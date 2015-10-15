<style type="text/css">
  .list td,.list th { font-size: 12px }
  abbr { vertical-align: middle; }
</style>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${directorscount}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="10" total="${directorscount}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${directors}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>ФИО</th>
          <th>Фикс. оплата</th>
          <th>За ген.<br/>директора</th>
          <th>За главбуха</th>
          <th>За договора</th>
          <th>Текущий оклад</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${directors}" var="record">
        <tr align="center">
          <td><g:link style="z-index:1" controller="user" action="persdetail" id="${record.id}" target="_blank">${record.shortname}</g:link></td>
          <td>
            <g:if test="${record.is_fixactsalary}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
            <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
            ${intnumber(value:bonusrates.gdbonus*dirdetails[record.id].gdcompanies.size())}
            <g:each in="${dirdetails[record.id].gdcompanies}" var="company">
              <br/><g:link controller="company" action="detail" id="${company.id}" target="_blank">${company.name} с ${String.format('%td.%<tm.%<tY',dirdetails[record.id].jobstartdates[company.id])}</g:link>
            </g:each>
          </td>
          <td>
            ${intnumber(value:dirdetails[record.id].gbcompanies.size()?bonusrates.gbbonus:0)}
            <g:each in="${dirdetails[record.id].gbcompanies}" var="company">
              <br/><g:link controller="company" action="detail" id="${company.id}" target="_blank">${company.name}</g:link>
            </g:each>
          </td>
          <td>
            ${intnumber(value:bonusrates.agrbonus*dirdetails[record.id].agrcount)}
            <g:each in="${dirdetails[record.id].agrcomplist}" var="agr">
              <br/><g:link controller="agreement" action="${agr.class.toString()-'class '}" id="${agr.id}" target="_blank">${Company.get(agr.class==Kredit?agr.client:agr.class==Cession?agr.cessionary:agr.arendator)?.name}&nbsp;-&nbsp;${agr.class.toString()-'class '}&nbsp;-&nbsp;${String.format('%td.%<tm.%<tY',agr.adate)}</g:link>
            </g:each>
          </td>
          <td>${intnumber(value:record.actsalary)}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${directorscount}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="10" total="${directorscount}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>