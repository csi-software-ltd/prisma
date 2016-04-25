<style type="text/css">
  .list td,.list th { font-size: 12px }
  tr.yellow > td { background:gold !important }
</style>
<div id="ajax_wrap">
<g:if test="${warningstatus}">
  <div class="error-box">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorlist">
      <li>Не все действующие сотрудники отражены или найдены в ведомости. <g:remoteLink action="nonfindemployee" id="${salreport?.id}" class="button" style="padding-top:4px" update="list">Показать &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink></li>
    </ul>
  </div>
</g:if>
  <div style="padding:10px">
    <div class="fleft">Найдено компаний: ${companies.count}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="10" total="${companies.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${companies.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Компания</th>
          <th>ИНН<br/>СНИЛС</th>
          <th>ФИО<br/>Должность</th>
          <th>НДФЛ<br/>Начислено</th>
          <th>К выплате</th>
          <th>ВНиМ</th>
          <th>НС и ПЗ</th>
          <th>ФФОМС</th>
          <th>ПФ</th>
          <th>Сумма</th>
          <th width="100">Статус</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${companies.records}" var="company">
        <tr class="${!company.compstatus?'yellow':''}" align="center">
          <td>${company.companyname}</td>
        <g:if test="${company.compstatus<2&&iscanedit}">
          <td onclick="this.firstChild.focus()" nowrap><span contenteditable="true" onblur="updateInn(${company.id},this.innerHTML)" onKeyDown="keyintercept(event)">${company.companyinn}</span></td>
        </g:if><g:else>
          <td nowrap>${company.companyinn}</td>
        </g:else>
          <td>НАЛОГИ</td>
          <td>${number(value:company.ndfl)}<g:if test="${company.debtndfl}"><br/><font color="red">${number(value:company.debtndfl)}</font></g:if></td>
          <td></td>
          <td>${number(value:company.fss_tempinvalid)}<g:if test="${company.debtfss_tempinvalid}"><br/><font color="red">${number(value:company.debtfss_tempinvalid)}</font></g:if></td>
          <td>${number(value:company.fss_accident)}<g:if test="${company.debtfss_accident}"><br/><font color="red">${number(value:company.debtfss_accident)}</font></g:if></td>
          <td>${number(value:company.ffoms)}<g:if test="${company.debtffoms}"><br/><font color="red">${number(value:company.debtffoms)}</font></g:if></td>
          <td>${number(value:company.pf)}<g:if test="${company.debtpf}"><br/><font color="red">${number(value:company.debtpf)}</font></g:if></td>
          <td>${number(value:company.ndfl+company.fss_tempinvalid+company.fss_accident+company.ffoms+company.pf)}</td>
          <td id="stat${company.id}">${company.compstatus==1?'Привязано':company.compstatus==2?'Начислено':'Не распознана'}</td>
          <td>
          <g:if test="${company.compstatus<2&&iscanedit}">
            <a class="button" href="${createLink(controller:'salary',action:'buhsalarydetail',id:company.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          </g:if>
          </td>
        </tr>
      <g:each in="${salarypers.records}" var="pers">
      <g:if test="${company.companyinn==pers.companyinn&&!inrequest.is_tax}">
        <tr class="${!pers.perstatus?'yellow':''}" align="center">
          <td></td>
        <g:if test="${pers.perstatus<2&&iscanedit}">
          <td onclick="this.firstChild.focus()" nowrap><span contenteditable="true" onblur="updateSnils(${pers.id},this.innerHTML)" onKeyDown="keyintercept(event)">${pers.snils}</span></td>
        </g:if><g:else>
          <td nowrap>${pers.snils}</td>
        </g:else>
          <td>${pers.fio}<br/>${pers.position}</td>
          <td>${number(value:pers.fullsalary)}</td>
          <td>${number(value:pers.netsalary)}<g:if test="${pers.debtsalary}"><br/>${number(value:pers.debtsalary)}</g:if></td>
          <td colspan="5"></td>
          <td id="stat${pers.id}">${pers.perstatus==1?'Привязано':pers.perstatus==2?'Начислено':'Не распознан'}</td>
          <td>
          <g:if test="${pers.perstatus<2&&iscanedit}">
            <a class="button" href="${createLink(controller:'salary',action:'buhsalarydetail',id:pers.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          </g:if>
          </td>
        </tr>
      </g:if>
      </g:each>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено компаний: ${companies.count}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="10" total="${companies.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>