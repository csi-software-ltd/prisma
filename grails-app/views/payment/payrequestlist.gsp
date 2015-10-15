<style type="text/css">
  .list td,.list th { font-size: 11px !important }
  tr.disabled > td { background:silver !important; opacity:0.4 !important }
  tr.yellow > td { background:lightyellow !important }
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
<g:if test="${searchresult.records}">
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
          <th width="20px"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center" class="${record.is_bankmoney?'disabled':exptypes[record.id]?.type==0?'yellow':''}" id="tr_${record?.id}">
          <td><input type="checkbox" id="checkbox${i}" value="${record.id}"/></td>
          <td>${record.paydate?String.format('%td.%<tm.%<tY',record.paydate):''}<g:if test="${record.execdate}"><br/>${String.format('%td.%<tm.%<tY',record.execdate)}</g:if><br/>${record.id}</td>
          <td><g:if test="${record.fromcompany_id}"><g:link controller="company" action="detail" id="${record.fromcompany_id}" target="_blank">${record.fromcompany}</g:link></g:if><g:else>${record.fromcompany}</g:else></td>
          <td>
            ${record.paytype==1?'исходящий':record.paytype==2?'входящий':record.paytype==3?'внутренний':record.paytype==4?'списание':record.paytype==6?'агентские':record.paytype==7?'абон. плата':record.paytype==8?'откуп':record.paytype==9?'комиссия':record.paytype==10?'связанный входящий':record.paytype==11?'внешний':'пополнение'}<br/>${record.paycat==1?'договорной':record.paycat==2?'бюджетный':record.paycat==3?'персональный':record.paycat==4?'прочий':record.paycat==5?'банковский':'счета'}
          </td>
          <td>${number(value:record.summa)}</td>                     
          <td>
          <g:if test="${record.paycat in [1,4,5,6]}"><g:if test="${record.tocompany_id}"><g:link controller="company" action="detail" id="${record.tocompany_id}" target="_blank">${record.tocompany}</g:link></g:if><g:else>${record.tocompany}</g:else></g:if>
          <g:elseif test="${record.paycat==2}">${taxes[record.tax_id]}</g:elseif>
          <g:else>${Pers.get(record.pers_id)?.shortname}</g:else>
          </td>
          <td>${record.destination}<g:if test="${record.agreementtype_id}"><br/>&nbsp;${agrtypes[record.agreementtype_id]}&nbsp;&ndash;&nbsp;${record.agreementnumber}</g:if></td>
          <td><g:link controller="task" action="taskpaydetail" id="${record.taskpay_id}" target="_blank">${record?.taskpay_id?:''}</g:link></td>
          <td>${record.modstatus==0?'новый':record.modstatus==1?'в задании':record.modstatus==2?'выполнен':record.modstatus==3?'подтвержден':'отклонен'}</td>
          <td>${record.instatus==3?'подтвержден':record.instatus==2?'получен':record.instatus==1?'в полете':record.instatus==0?'новый':'отклонен'}</td>
          <td align="center">
            <a class="button" href="${createLink(controller:'payment',action:'payrequestdetail',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          <g:if test="${!record.expensetype_id&&!record.client_id&&record.paytype!=3&&iscantag}">
            <br/><a class="button" style="margin-top:7px" href="${createLink(controller:'payment',action:'payrequestdetail',id:record.id,params:[tag:1])}" title="Тегировать"><i class="icon-plus"></i></a>
          </g:if>
          <g:if test="${(record.paytype==2&&record.modstatus<3)||(record.modstatus==0&&record.client_id==0)}">
            <br/><g:remoteLink class="button" style="z-index:1;margin-top:7px" url="${[controller:'payment',action:'deletepayrequest',id:record.id]}" title="Удалить" before="if(!confirm('Вы действительно хотите удалить платеж?')) return false" onSuccess="\$('form_submit_button').click();"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.count}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
