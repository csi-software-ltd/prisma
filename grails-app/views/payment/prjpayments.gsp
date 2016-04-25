<style type="text/css">
  .list td,.list th { font-size: 11px !important }
  tr.disabled > td { background:silver !important; opacity:0.4 !important }
</style>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult.count}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${searchresult.count}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th><i class="icon-check" onclick="toggleCheckboxes()"></i></th>
          <th width="50px">Срок<br/>Дата</th>
          <th>Плательщик</th>
          <th>Тип<br/>Категория</th>
          <th>Сумма</th>
          <th>Получатель</th>
          <th>Назначение</th>
          <th>№ задания</th>
          <th>Статус</th>
          <th>Статус<br/>получения</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center" class="${record.is_bankmoney?'disabled':''}" id="tr_${record?.id}">
          <td><input type="checkbox" id="checkbox${i}" value="${record.id}"/></td>
          <td>${record.paydate?String.format('%td.%<tm.%<tY',record.paydate):''}<br/>${record.execdate?String.format('%td.%<tm.%<tY',record.execdate):''}</td>
          <td><g:if test="${record.fromcompany_id}"><g:link controller="company" action="detail" id="${record.fromcompany_id}" target="_blank">${record.fromcompany}</g:link></g:if><g:else>${record.fromcompany}</g:else></td>
          <td>
            ${record.paytype==1?'исходящий':record.paytype==2?'входящий':record.paytype==3?'внутренний':record.paytype==4?'списание':record.paytype==6?'агентские':record.paytype==7?'абон. плата':record.paytype==8?'откуп':record.paytype==9?'комиссия':payrequest.paytype==10?'связанный входящий':payrequest.paytype==11?'внешний':'пополнение'}<br/>${record.paycat==1?'договорной':record.paycat==2?'бюджетный':record.paycat==3?'персональный':record.paycat==4?'прочий':record.paycat==5?'банковский':'счета'}
          </td>
          <td>${number(value:record.summa)}</td>                     
          <td>
          <g:if test="${record.paycat in [1,4]}"><g:if test="${record.tocompany_id}"><g:link controller="company" action="detail" id="${record.tocompany_id}" target="_blank">${record.tocompany}</g:link></g:if><g:else>${record.tocompany}</g:else></g:if>
          <g:elseif test="${record.paycat==2}">${taxes[record.tax_id]}</g:elseif>
          <g:else>${Pers.get(record.pers_id)?.shortname}</g:else>
          </td>
          <td>${record.destination}<g:if test="${record.agreementtype_id}"><br/>&nbsp;${agrtypes[record.agreementtype_id]}&nbsp;&ndash;&nbsp;${record.agreementnumber}</g:if></td>
          <td><g:link controller="task" action="taskpaydetail" id="${record.taskpay_id}" target="_blank">${record?.taskpay_id?:''}</g:link></td>
          <td>${record.modstatus==0?'новый':record.modstatus==1?'в задании':record.modstatus==2?'выполнен':'подтвержден'}</td>
          <td>${record.instatus==3?'подтвержден':record.instatus==2?'получен':record.instatus==1?'в полете':record.instatus==0?'новый':'отклонен'}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.count}</span>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
  </div>
</g:if>
</div>
