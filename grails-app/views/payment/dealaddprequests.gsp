<div id="ajax_wrap">
  <div id="resultList">
    <g:formRemote name="addrequestDealForm" url="[action:'updatedeal',id:deal.id]" onSuccess="location.reload(true)">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
        <g:if test="${iscanedit}">
          <th><input type="checkbox" id="groupcheckbox" onclick="togglecheck()"></th>
        </g:if>
          <th>Дата платежа<br/>Номер платежа</th>
          <th>Контрагенты</th>
          <th>Тип платежа</th>
          <th>Сумма</th>
          <th>Подклиент</th>
          <th>Процент<br/>Тип процента</th>
          <th>Комиссия</th>
          <th>Остаток счета</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${payrequests}" status="i" var="record">
        <tr align="center">
        <g:if test="${iscanedit}">
          <td><input type="checkbox" name="payrequestids" value="${record.id}"></td>
        </g:if>
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}<br/><g:link controller="payment" action="clientpayment" id="${record.id}" target="_blank">${record.id}</g:link></td>
          <td>${record.fromcompany_name?:record.fromcompany}<br/>${record.tocompany_name?:record.tocompany}</td>
          <td>
          <g:if test="${record.is_clientcommission}"><abbr title="Возврат комиссии"><i class="icon-gift icon-large"></i></abbr></g:if>
          <g:elseif test="${record.is_midcommission}"><abbr title="Возврат посреднику"><i class="icon-link icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==1}"><abbr title="Исходящий"><i class="icon-signout icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==2}"><abbr title="Входящий"><i class="icon-signin icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==4}"><abbr title="Списание"><i class="icon-trash icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==8}"><abbr title="Откуп"><i class="icon-magic icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==9}"><abbr title="Комиссия"><i class="icon-money icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==7}"><abbr title="Абон. плата"><i class="icon-calendar icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==10}"><abbr title="Связанный входящий"><i class="icon-exchange icon-large"></i></abbr></g:elseif>
          <g:elseif test="${record.paytype==11}"><abbr title="Внешний"><i class="icon-external-link-square icon-large"></i></abbr></g:elseif>
          <g:else><abbr title="Пополнение"><i class="icon-certificate icon-large"></i></abbr></g:else>
          </td>
          <td>${number(value:record.summa)}</td>
          <td>${record.subclient_name?:'нет'}</td>
          <td>${number(value:record.compercent)}<br/>${record.percenttype?'деление':'умножение'}</td>
          <td>${number(value:record.comission)}</td>
          <td>${number(value:curclientsaldo+record.dinclientsaldo+record.computeClientdelta())}</td>
          <td>
          <g:if test="${record.file_id}">
            <a class="button" style="z-index:1" href="${createLink(controller:'payment',action:'showscan',id:record.file_id,params:[code:Tools.generateModeParam(record.file_id)])}" title="Скан документа" target="_blank"><i class="icon-picture"></i></a>
          </g:if>
          </td>
        </tr>
      </g:each>
      <g:if test="${!payrequests}">
        <tr>
          <td colspan="10" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Новых платежей не найдено</a>
          </td>
        </tr>
      </g:if><g:elseif test="${deal.modstatus==0&&iscanedit}">
        <tr>
          <td colspan="10" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('addrequestDeal_form_submit_button').click()">Добавить в сделку</a>
          </td>
        </tr>
      </g:elseif>
      </tbody>
    </table>
      <input type="submit" id="addrequestDeal_form_submit_button" style="display:none"/>
    </g:formRemote>
  </div>
</div>