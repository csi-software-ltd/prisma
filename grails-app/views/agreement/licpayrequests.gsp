<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата платежа</th>
          <th>Сумма<br/>НДС</th>
          <th>Назначение</th>
          <th>Статус</th>
          <th width="45"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${payrequests}" var="record">
        <tr align="center">
          <td width="70">${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td>${number(value:record.summa)}<br/>${record.is_nds?'с НДС':'без НДС'}</td>
          <td>${record.destination}</td>
          <td width="100">${record.modstatus==1?'В работе':record.modstatus==2?'Выполнен':record.modstatus==3?'Подтвержден':'Необработанный'}</td>
          <td>
            <g:link controller="payment" action="payrequestdetail" id="${record.id}" style="z-index:1" class="button" target="_blank"><i class="icon-pencil"></i></g:link>
          <g:if test="${record.modstatus==0&&iscanedit}">
            &nbsp;&nbsp;<g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'deletelicpayrequest', id:record.id, params:[agr_id:license.id]]}" title="Удалить" onSuccess="getPayrequests()"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </td>
        </tr>
      </g:each>
      <g:if test="${!payrequests}">
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Платежей не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>