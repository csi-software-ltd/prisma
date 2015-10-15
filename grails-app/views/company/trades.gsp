<style type="text/css">
  .list td,.list th { font-size: 12px }
</style>
<div id="ajax_wrap">
  <div class="tabs fright">
  <g:each in="${Agreementtype.findAllByCompanyactionNotEqual('')}">
  <g:if test="${session.user.group."$it.checkfield"}">
    <g:remoteLink class="${(actionName==it.companyaction && modstatus==1)?'active':''}" url="${[controller:controllerName,action:it.companyaction,id:company.id]}" update="details"><i class="icon-${it.icon} icon-large"></i> ${it.name} </g:remoteLink>
  </g:if>
  </g:each><br />
    <g:remoteLink class="${(actionName=='kredits' && modstatus==0)?'active':''}" url="${[controller:controllerName,action:'kredits',id:company.id,params:[modstatus:0]]}" update="details"><i class="icon-list icon-large"></i> Архив Кредиты</g:remoteLink>
    <g:remoteLink class="${(actionName=='lizings' && modstatus==0)?'active':''}" url="${[controller:controllerName,action:'lizings',id:company.id,params:[modstatus:0]]}" update="details"><i class="icon-list icon-large"></i> Архив Лизинги</g:remoteLink>
    <g:remoteLink class="${(actionName=='loans' && modstatus==0)?'active':''}" url="${[controller:controllerName,action:'loans',id:company.id,params:[modstatus:0]]}" update="details"><i class="icon-list icon-large"></i> Архив Займы</g:remoteLink>
  </div>
  <div class="clear"></div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Покупатель</th>
          <th>Поставщик</th>
          <th>Тип договора</th>
          <th>Договор<br/>Дата</th>
          <th>Тип оплаты</th>
          <th>Сумма</th>
          <th>Статус</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${trades}" status="i" var="record">
        <tr align="center">
          <td>
            <g:if test="${record.client==company.id}">${record.client_name}</g:if><g:else><g:link controller="company" action="detail" id="${record.client}" style="z-index:1" target="_blank">${record.client_name}</g:link></g:else>
          </td>
          <td>
            <g:if test="${record.supplier==company.id}">${record.supplier_name}</g:if><g:else><g:link controller="company" action="detail" id="${record.supplier}" style="z-index:1" target="_blank">${record.supplier_name}</g:link></g:else>
          </td>
          <td>${record.tradetype?'Поставки':'Услуги'}</td>
          <td>${record.anumber}<br/>${String.format('%td.%<tm.%<tY',record.adate)}</td>
          <td>${record.paytype?'Единовременно':'Регулярные платежи'}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td>
          <g:if test="${record.modstatus}"><abbr title="активный"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="неактивный"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
            <a class="button" style="z-index:1" href="${g.createLink(controller:'agreement',action:'trade',id:record.id)}" title="Редактировать" target="_blank"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      <g:if test="${!trades}">
        <tr>
          <td colspan="8" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Поставок не найдено</a>
          </td>
        </tr>
      </g:if>
        <tr>
          <td colspan="8" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('newtradeForm').submit();">
              Новый договор поставок и услуг &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>
<g:form name="newtradeForm" url="${[controller:'agreement',action:'trade']}" method="post" target="_blank">
  <input type="hidden" id="company_id" name="company_id" value="${company.id}"/>
</g:form>
