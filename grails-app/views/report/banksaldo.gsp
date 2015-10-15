<style type="text/css">
  tr.yellow > td { background:lightyellow !important }
</style>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult.count}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${searchresult.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Банк</th>
          <th>Компания</th>
          <th>Факт. сальдо<br/>Дата</th>
          <th>Текущий остаток</th>
          <th>Расчетный остаток</th>
          <th>СС банка<br/>Дата</th>
          <th>СС компании</th>
          <th>Подтв. сальдо<br/>Дата</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center" style="${record.ibankstatus==-1?'color:red':''}">
          <td>${record.bankname}<br/><i class="icon-${valutas[record.valuta_id]}"></i>&nbsp;&nbsp;&nbsp;${record.typeaccount_id==1?'расчетный':record.typeaccount_id==2?'корпоративный':record.typeaccount_id==3?'текущий':record.typeaccount_id==4?'транзитный':'накопительный'}</td>
          <td>${record.cname}</td>
          <td>${number(value:record.actsaldo)}<g:if test="${record.actsaldodate}"><br/>${String.format('%td.%<tm.%<tY',record.actsaldodate)}</g:if></td>
          <td>${number(value:saldos[record.id].cursaldo)}</td>
          <td>${number(value:saldos[record.id].computedsaldo)}</td>
          <td>${number(value:record.banksaldo)}<g:if test="${record.banksaldodate}"><br/>${String.format('%td.%<tm.%<tY',record.banksaldodate)}</g:if></td>
          <td>${number(value:record.actsaldo-record.banksaldo)}</td>
          <td>${number(value:record.saldo)}<g:if test="${record.saldodate}"><br/>${String.format('%td.%<tm.%<tY',record.saldodate)}</g:if></td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.count}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>