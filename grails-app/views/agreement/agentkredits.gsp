<g:formRemote id="agentkreditAddForm" name="agentkreditAddForm" url="[action:'addagentkredit']" method="post" before="updateagentnumber()" onSuccess="processAddkreditResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorkreditlist">
      <li></li>
    </ul>
  </div>
  <div id="agentkredit"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Заемщик<br/>Банк</th>
          <th>Сумма кредита<br/>Остаток тела</th>
          <th>Дата договора<br/>Срок действия</th>
          <th>Процент<br/>СС</th>
          <th>Расчитан до</th>
          <th>Условия расчета</th>
          <th>Агенты</th>
          <th>Действие</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${agentkredits}" status="i" var="record">
        <tr align="center">
          <td>${record.client_name}<br/>${record.bank_name}</td>
          <td>${number(value:record.kr_summa)}<i class="icon-${valutas[record.valuta_id]}"></i><br/>${record.lastbodydebt>=0?number(value:record.lastbodydebt):'нет данных'}</td>
          <td>${String.format('%td.%<tm.%<tY',record.period_start)}<br/>${String.format('%td.%<tm.%<tY',record.period_end)}</td>
          <td>${number(value:record.rate)}<br/>${number(value:record.rate-(record.agentpercent?:0))}</td>
          <td>${record.calcdate?String.format('%td.%<tm.%<tY',record.calcdate):'нет'}</td>
          <td>${record.payterm?'по месяцам':'по дням'}</td>
          <td>
          <g:if test="${record.agentexist}"><abbr title="есть"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="нет"><i class="icon-ban-circle"></i></abbr></g:else>
          </td>
          <td>
            <a class="button" style="z-index:1;margin-left:0px" href="javascript:void(0)" title="Редактировать" onclick="$('agentkredit_id').value=${record.id};$('agentkredit_submit_button').click();"><i class="icon-pencil"></i></a>
          <g:if test="${!record.agentexist&&!record.periodexist&&iscanedit}">
            &nbsp;&nbsp;<g:remoteLink class="button" style="z-index:1;" url="${[controller:'agreement',action:'deleteagentkredit',id:record.id,params:[agentagr_id:agentagr.id]]}" title="Удалить" onSuccess="getKredits()"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </td>
        </tr>
      </g:each>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="9" class="btns" style="text-align:center">
            <a class="button" id="addplanpaymentbutton" href="javascript:void(0)" onclick="updateclient(0)">
              Добавить кредиты &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="agentkreditForm" url="[action:'agentkredit', params:[agentagr_id:agentagr.id]]" update="agentkredit" before="newagentnumber=0" onComplete="\$('errorkreditlist').up('div').hide();jQuery('#agentkreditAddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="agentkredit_submit_button" value="Показать"/>
  <input type="hidden" id="agentkredit_id" name="id" value="0"/>
</g:formRemote>
<g:formRemote name="bankkreditForm" url="[action:'bankkredit', params:[agentagr_id:agentagr.id]]" update="agentkredit"  onComplete="\$('errorkreditlist').up('div').hide();jQuery('#agentkreditAddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="bankkredit_submit_button" value="Показать"/>
  <input type="hidden" id="bankkredits_client_id" name="client" value="0"/>
</g:formRemote>
