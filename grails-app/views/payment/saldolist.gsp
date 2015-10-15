<style type="text/css">
  table.list thead th, table.list tbody th, table.list thead td, table.list tbody td { font-size: 11px }
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
          <th>Компания<br/>ИНН</th>
          <th>Банк<br/>БИК</th>
          <th>Подтв. остаток<br/>Дата</th>
          <th>Факт. остаток</th>
          <th>Дата факт. остатка</th>
          <th>Дата мод.<br/>факт. остатка</th>
          <th width="30">Блок</th>
          <th width="30">SMS</th>
          <th width="30"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="left">
          <td><g:link controller="company" action="detail" id="${record.company_id}" target="_blank">${record.cname}</g:link><br/>${record.inn}</td>         
          <td>${record.bankname}<br/>${record.bank_id}<br/><i class="icon-${valutas[record.valuta_id]}"></i>&nbsp;&nbsp;&nbsp;${record.typeaccount_id==1?'расчетный':record.typeaccount_id==2?'корпоративный':record.typeaccount_id==3?'текущий':record.typeaccount_id==4?'транзитный':'накопительный'}&nbsp;&nbsp;&nbsp;${record.schet}</td>
          <td>${intnumber(value:record.saldo)}<br/>${record?.saldodate?shortDateNoTime(date:record?.saldodate):''}</td>
          <td><input type="text" id="actsaldo_${record.id}" value="${intnumber(value:record.actsaldo)}" style="width:120px"/></td>
          <td><g:datepicker class="normal nopad" name="actsaldodate_${record.id}" value="${String.format('%td.%<tm.%<tY',record?.actsaldodate?:new Date()-1)}"/></td>                 
          <td>${String.format('%td.%<tm.%<tY %<tT',record.actmoddate?:new Date()-1)}</td>
          <td align="center">
            <a class="button" href="javascript:void(0)" title="${!record.ibankblock?'Заблокировать':'Разблокировать'}" onclick="setAccountBlock(${record.id},${record.ibankblock?0:1})"><i class="icon-${record.ibankblock?'ban-circle':'minus'}"></i></a><br/>
          </td>
          <td align="center">
            <a class="button" href="javascript:void(0)" title="${!record.is_nosms?'Установить "Без смс"':'Установить "С смс"'}" onclick="setNoSms(${record.id},${record.is_nosms?0:1})"><i class="icon-${record.is_nosms?'minus':'ok'}"></i></a><br/>
          </td>
          <td align="center">
            <a class="button" href="javascript:void(0)" title="Сохранить" onclick="setActSaldo(${record.id})"><i class="icon-ok"></i></a>
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