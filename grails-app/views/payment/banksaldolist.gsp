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
          <th>Факт. остаток<br/>Дата</th>
          <th>Остаток средств банка</th>
          <th>Дата средств банка</th>
          <th width="30px"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="left">
          <td><g:link controller="company" action="detail" id="${record.company_id}" target="_blank">${record.cname}</g:link><br/>${record.inn}</td>
          <td>${record.bankname}<br/>${record.bank_id}<br/><i class="icon-${valutas[record.valuta_id]}"></i>&nbsp;&nbsp;&nbsp;${record.typeaccount_id==1?'расчетный':record.typeaccount_id==2?'корпоративный':record.typeaccount_id==3?'текущий':record.typeaccount_id==4?'транзитный':'накопительный'}&nbsp;&nbsp;&nbsp;${record.schet}</td>
          <td>${number(value:record.actsaldo)}<br/>${record.actsaldodate?String.format('%td.%<tm.%<tY',record.actsaldodate):''}</td>
          <td><input type="text" id="banksaldo_${record.id}" value="${number(value:record.banksaldo)}" style="width:120px"/></td>
          <td><g:datepicker class="normal nopad" name="banksaldodate_${record?.id}" value="${String.format('%td.%<tm.%<tY',record?.banksaldodate?:new Date())}"/></td>
          <script type="text/javascript">jQuery("#banksaldodate_${record.id}").mask("99.99.9999",{placeholder:" "});</script>
          <td align="center">
            <a class="button" href="javascript:void(0)" title="Сохранить" onclick="setBankSaldo(${record?.id})"><i class="icon-ok"></i></a>
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