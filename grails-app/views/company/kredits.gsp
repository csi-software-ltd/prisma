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
          <th rowspan="2">Банк</th>
          <th rowspan="2">Договор<br/>Выдача</th>
          <th rowspan="2">Сумма<br/>Ставка<br/>Факт. задолженность</th>
          <th rowspan="2">Тип кредита</th>
          <th colspan="6">Статусы</th>
          <th rowspan="2" width="30"></th>
        </tr>
        <tr>
          <th>Реал</th>
          <th>Техн</th>
          <th>Реалтех</th>
          <th>Залог</th>
          <th>Уступка</th>
          <th>Договора</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${kredits}" status="i" var="record">
        <tr align="center">
          <td>${record.bank_name}</td>
          <td>${record.anumber}<br/>${record.startdate?String.format('%td.%<tm.%<tY',record.startdate):'нет'}</td>
          <td nowrap>${number(value:record.summa)}<i class="icon-${valutas[record.valuta_id]}"></i><br/>${number(value:record.rate)}<br/>${number(value:record.debt)}</td>
          <td>${record.kredtype==1?'Кредит':record.kredtype==2?'Кредитная линия':record.kredtype==3?'Овердрафт':'Линия с лимитом задолженности'}</td>
          <td>
          <g:if test="${record.is_real}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
          <g:if test="${record.is_tech}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
          <g:if test="${record.is_realtech}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
          <g:if test="${record.zalogstatus==1}"><abbr title="Нет"><i class="icon-minus"></i></abbr></g:if>
          <g:elseif test="${record.zalogstatus==2}"><abbr title="Да"><i class="icon-plus"></i></abbr></g:elseif>
          </td>
          <td>
          <g:if test="${record.cessionstatus}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
          <g:if test="${record.is_agr}"><abbr title="есть договор"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="нет договора"><i class="icon-ban-circle"></i></abbr></g:else>
          </td>
          <td>
            <a class="button" style="z-index:1" href="${g.createLink(controller:'agreement',action:'kredit',id:record.id)}" title="Редактировать" target="_blank"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      <g:if test="${!kredits}">
        <tr>
          <td colspan="10" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Кредитов не найдено</a>
          </td>
        </tr>
      </g:if>
      <g:if test="${modstatus==1}">
        <tr>
          <td colspan="10" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('newkreditForm').submit();">
              Новый кредитный договор &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:form name="newkreditForm" url="${[controller:'agreement',action:'kredit']}" method="post" target="_blank">
  <input type="hidden" id="company_id" name="company_id" value="${company.id}"/>
</g:form>
