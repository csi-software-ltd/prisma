<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата платежа</th>
          <th>Тип платежа</th>
          <th>Категория</th>
          <th>Контрагент</th>
          <th>Сумма</th>
          <th>Назначение</th>
          <th>Статус</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${payrequests}" status="i" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td>${record.paytype==1?'исходящий':record.paytype==2?'входящий':'внутренний'}</td>
          <td>${record.paycat==1?'договорной':record.paycat==2?'бюджетный':record.paycat==3?'персональный':record.paycat==4?'прочий':record.paycat==5?'банковский':'счета'}</td>
          <td>
            <g:if test="${record.fromcompany_id!=company.id}">${record.fromcompany}</g:if><g:else>${record.tocompany}</g:else>
          </td>
          <td>${number(value:record.summa)}</td>
          <td><g:if test="${record.paycat==2}">${taxes[record.tax_id]}</g:if><g:else>${record.destination}</g:else></td>
          <td>${record.modstatus==1?'в задании':record.modstatus==2?'выполнен':record.modstatus==3?'подтвержден':'необработанный'}</td>
        </tr>
      </g:each>
      <g:if test="${!payrequests}">
        <tr>
          <td colspan="8" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Платежей не найдено</a>
          </td>
        </tr>
      </g:if>
        <tr>
          <td colspan="8" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('fullpayrequestlistForm').submit();">
              Полный список платежей &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>
<g:form name="fullpayrequestlistForm" url="${[controller:'payment',action:'index']}" method="post" target="_blank">
  <input type="hidden" name="fromcompany_id" value="${company.id}"/>
  <input type="hidden" name="paymentobject" value="1"/>
</g:form>
