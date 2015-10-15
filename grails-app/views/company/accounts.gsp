<style type="text/css">
  .list td,.list th { font-size: 12px }
</style>
<g:formRemote id="accountAddForm" name="accountAddForm" url="[action:'addaccount']" method="post" onSuccess="processaddaccountResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="erroraccountlist">
      <li></li>
    </ul>
  </div>
  <div id="account"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div class="tabs fright">
    <a id="accountstatus1" <g:if test="${modstatus==1}">class="active"</g:if> onclick="setAccountstatus(1)"><i class="icon-list icon-large"></i> Открытые </a>
    <a id="accountstatus1" <g:if test="${modstatus==2}">class="active"</g:if> onclick="setAccountstatus(2)"><i class="icon-list icon-large"></i> Планируемые </a>
    <a id="accountstatus0" <g:if test="${modstatus==0}">class="active"</g:if> onclick="setAccountstatus(0)"><i class="icon-list icon-large"></i> Закрытые </a>
  </div>
  <div class="clear"></div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Банк</th>
          <th>Бик</th>
          <th>№ Счета</th>
        <g:if test="${company.is_holding}">
          <th>Срок БК</th>
        <g:if test="${session.user.confaccess>0}">
          <th>Подтв. остаток</th>
          <th>Факт. остаток</th>
        </g:if>
          <th>Статус БК</th>
          <th>Дубликат</th>
        </g:if>
          <th width="50"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${accounts}" status="i" var="record">
        <tr align="center" style="${!record.is_license?'color:red':''}">
          <td>${record.bankname}<g:if test="${!record.is_license}"><br/>(Лицензия отозвана ${record.stopdate?String.format('%td.%<tm.%<tY',record.stopdate):''})</g:if><g:if test="${record.prevnameinfo}"><br/>(бывш. ${record.prevnameinfo})</g:if></td>
          <td>${record.bank_id}</td>
          <td>${account(value:record.schet)}<br/><i class="icon-${valutas[record.valuta_id]}"></i>&nbsp;&nbsp;&nbsp;${record.typeaccount_id==1?'расчетный':record.typeaccount_id==2?'корпоративный':record.typeaccount_id==3?'текущий':record.typeaccount_id==4?'транзитный':record.typeaccount_id==5?'накопительный':record.typeaccount_id==6?'планируемый':'отказ в открытии'}</td>
        <g:if test="${company.is_holding}">
          <td>${record.ibank_close?String.format('%td.%<tm.%<tY',record.ibank_close):'неограничен'}</td>
        <g:if test="${session.user.confaccess>0}">
          <td>${intnumber(value:record.saldo)}<g:if test="${record.saldodate}"><br/>${String.format('%td.%<tm.%<tY',record.saldodate)}</g:if></td>
          <td>${intnumber(value:record.actsaldo)}<g:if test="${record.actsaldodate}"><br/>${String.format('%td.%<tm.%<tY',record.actsaldodate)}</g:if></td>
        </g:if>
          <td>
          <g:if test="${record.ibankstatus==1}"><abbr title="активен"><i class="icon-ok"></i></abbr></g:if>
          <g:elseif test="${record.ibankstatus==2}"><abbr title="просрочен"><i class="icon-ban-circle"></i></abbr></g:elseif>
          <g:elseif test="${record.ibankstatus==-1}"><abbr title="заблокирован"><i class="icon-lock"></i></abbr></g:elseif>
          <g:else><abbr title="нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
          <g:if test="${record.is_duplicate==1}"><abbr title="да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
        </g:if>
          <td valign="middle" width="50">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('account_id').value=${record.id};$('account_submit_button').click();"><i class="icon-pencil"></i></a>
          <g:if test="${record.modstatus==0&&iscanedit}">
            &nbsp;&nbsp;<g:remoteLink style="z-index:1" class="button" url="${[controller:'company', action:'deleteaccount', id:record.id, params:[company_id:company.id]]}" title="Удалить" onSuccess="setAccountstatus(0)"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </td>
        </tr>
      </g:each>
      <g:if test="${modstatus>0&&iscanedit}">
        <tr>
          <td colspan="9" class="btns" style="text-align:center">
            <a class="button" id="addaccountbutton" href="javascript:void(0)" onclick="$('account_id').value=0;$('account_submit_button').click();">
              Добавить новый счет &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="accountForm" url="[action:'account', params:[company_id:company.id]]" update="account" onComplete="\$('erroraccountlist').up('div').hide();jQuery('#accountAddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="account_submit_button" value="Показать"/>
  <input type="hidden" id="account_id" name="id" value="0"/>
</g:formRemote>