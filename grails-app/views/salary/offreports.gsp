<style type="text/css">
  .list td,.list th { font-size: 12px }
  tr.yellow > td { background:gold !important }
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
          <th>Компания</th>
          <th>ФИО</th>
          <th>Должность</th>
          <th>Факт. оклад</th>
          <th>К выплате</th>
          <th>Долг</th>
          <th>Осн. карта</th>
          <th>Доп. карта</th>
          <th>Касса</th>
          <th>Дата<br/>оплаты</th>
          <th>Статус<br/>основной<br/>оплаты</th>
          <th>Статус<br/>доп.<br/>оплаты</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" var="record">
        <tr class="${!record.is_haveibank?'yellow':''}" align="center">
          <td><g:link controller="company" action="detail" id="${record.company_id}" target="_blank">${record.companyname}</g:link></td>
          <td>${record.fio}</td>
          <td>${record.position}</td>
          <td>${intnumber(value:record.actsalary)}</td>
          <td>${number(value:record.netsalary)}</td>
          <td>${number(value:record.debtsalary)}</td>
        <g:if test="${record.paidmainstatus==0&&record.paidaddstatus==0&&record.perstype==2&&(record.cardmain+record.cardadd)>0&&iscanedit}">
          <td style="${!record.is_havemaincard&&record.perstype<3?'background:red !important':''}" onclick="this.firstChild.focus()"><span contenteditable="true" onblur="updateCardmain(${record.id},this.innerHTML)" onKeyDown="keyintercept(event)">${number(value:record.cardmain)}</span></td>
        </g:if><g:else>
          <td style="${!record.is_havemaincard&&record.perstype<3?'background:red !important':''}">${number(value:record.cardmain)}</td>
        </g:else>
          <td style="${!record.is_haveaddcard&&record.perstype>1?'background:red !important':''}">${number(value:record.cardadd)}</td>
          <td>${intnumber(value:record.cashsalary)}</td>
          <td>${record.paydate?String.format('%td.%<tm.%<tY',record.paydate):'нет'}</td>
          <td nowrap>${record.paidmainstatus==1?'В оплате':record.paidmainstatus==2?'Оплачено':record.paidmainstatus==-1?'Невозможно':'Новый'}</td>
          <td nowrap>${record.paidaddstatus==1?'В оплате':record.paidaddstatus==2?'Оплачено':record.paidaddstatus==-1?'Невозможно':'Новый'}</td>
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
