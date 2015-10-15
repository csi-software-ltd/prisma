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
          <th>Займодавец</th>
          <th>Заемщик</th>
          <th>Тип займа</th>
          <th>Договор<br/>Дата<br/>Выдача</th>
          <th>Сумма<br/>Ставка<br/>Задолженность</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${loans}" status="i" var="record">
        <tr align="center">
          <td>${record.lender_name?:record.lenderpers_name}</td>
          <td>${record.client_name?:record.clientpers_name}</td>
          <td>${record.loantype==1?'Заем у внешней':record.loantype==2?'Выдача внешней':record.loantype==3?'Внутренний займ':record.loantype==4?'Займ учредителя':'Займ работнику'}</td>
          <td>${record.anumber}<br/>${String.format('%td.%<tm.%<tY',record.adate)}<br/>${String.format('%td.%<tm.%<tY',record.startdate)}</td>
          <td nowrap>${intnumber(value:record.summa)}<i class="icon-${valutas[record.valuta_id]}"></i><br/>${number(value:record.rate)}<br/>${intnumber(value:record.debt)}</td>
          <td>
            <a class="button" style="z-index:1" href="${g.createLink(controller:'agreement',action:'loan',id:record.id)}" title="Редактировать" target="_blank"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      <g:if test="${!loans}">
        <tr>
          <td colspan="10" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Займов не найдено</a>
          </td>
        </tr>
      </g:if>
      <g:if test="${modstatus==1}">
        <tr>
          <td colspan="10" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('newloanForm').submit();">
              Новый договор займа &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:form name="newloanForm" url="${[controller:'agreement',action:'loan']}" method="post" target="_blank">
  <input type="hidden" id="company_id" name="company_id" value="${company.id}"/>
</g:form>
