<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Банк</th>
          <th>Компания</th>
          <th>Тип счета</th>
          <th>Номер счета</th>
          <th>Дата активации бк</th>
          <th>Срок действия бк</th>
          <th>Статус бк</th>
          <th>Директор в компании</th>
          <th>Директор по сведениям банка</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult}" status="i" var="record">
        <tr align="center">
          <td>${record.shortname}</td>
          <td><g:link style="z-index:1" controller="company" action="detail" id="${record.company_id}" target="_blank">${record.cname}</g:link></td>
          <td>${record.typeaccount_id==1?'расчетный':record.typeaccount_id==2?'корпоративный':record.typeaccount_id==3?'текущий':record.typeaccount_id==4?'транзитный':'накопительный'}</td>
          <td>${record.schet}</td>
          <td>${String.format('%td.%<tm.%<tY',record.ibank_open)}</td>
          <td>${String.format('%td.%<tm.%<tY',record.ibank_close)}</td>
          <td>
          <g:if test="${record.ibankstatus==1}"><abbr title="активен"><i class="icon-ok"></i></abbr></g:if>
          <g:elseif test="${record.ibankstatus==2}"><abbr title="просрочен"><i class="icon-ban-circle"></i></abbr></g:elseif>
          <g:elseif test="${record.ibankstatus==-1}"><abbr title="заблокирован"><i class="icon-lock"></i></abbr></g:elseif>
          <g:else><abbr title="нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>${record.gd}</td>
          <td>${Pers.get(record.pers_id)?.shortname}</td>
        </tr>
      </g:each>
      <g:if test="${!searchresult}">
        <tr>
          <td colspan="9" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Истекающих БК не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>