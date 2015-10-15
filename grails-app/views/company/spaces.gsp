<div id="ajax_wrap">
  <div class="tabs fright">
    <a id="spaceasort1" <g:if test="${asort==1}">class="active"</g:if> onclick="setSpaceAsort(1)"><i class="icon-list icon-large"></i> Аренда </a>
    <a id="spaceasort0" <g:if test="${asort==0}">class="active"</g:if> onclick="setSpaceAsort(0)"><i class="icon-list icon-large"></i> Субаренда </a>
    <a id="spaceasort-100" <g:if test="${asort==-100}">class="active"</g:if> onclick="setSpaceAsort(-100)"><i class="icon-list icon-large"></i> История </a>
  </div>
  <div class="clear"></div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Арендатор</th>
          <th>Арендодатель</th>
          <th>Тип помещений</th>
          <th>Адрес</th>
          <th>Договор<br/>Дата<br/>Срок</th>
          <th>Тип аренды</th>
          <th>Площадь<br/>Цена<br/>Доп услуги</th>
          <th>Плата<br/>Долг<br/>Дата</th>
          <th>Статус</th>
          <th>Возмож ность<br/>платежа</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${spaces}" status="i" var="record">
        <tr align="center">
          <td>
            <g:if test="${record.arendator==company.id}">${record.arendator_name}</g:if><g:else><g:link controller="company" action="detail" id="${record.arendator}" style="z-index:1" target="_blank">${record.arendator_name}</g:link></g:else>
          </td>
          <td>
            <g:if test="${record.arendodatel==company.id}">${record.arendodatel_name}</g:if><g:else><g:link controller="company" action="detail" id="${record.arendodatel}" style="z-index:1" target="_blank">${record.arendodatel_name}</g:link></g:else>
          </td>
          <td>${spacetypes[record.spacetype_id]}</td>
          <td>${record.fulladdress}</td>
          <td>${record.anumber}<br/>${record.adate?String.format('%td.%<tm.%<tY',record.adate):'нет'}<br/>${record.enddate?String.format('%td.%<tm.%<tY',record.enddate):'нет'}</td>
          <td>${arendatypes[record.arendatype_id]}</td>
          <td>
            ${number(value:record.area)}<br/>${number(value:record.ratemeter)}<br/>
          <g:if test="${record.is_addpayment}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>${number(value:record.rate)}<br/>${number(value:record.debt)}<br/>${record.debtdate?String.format('%td.%<tm.%<tY',record.debtdate):'нет'}</td>
          <td>
          <g:if test="${record.modstatus}"><abbr title="активный"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="неактивный"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
          <g:if test="${record.paystatus}"><abbr title="возможно"><i class="icon-check"></i></abbr></g:if>
          <g:else><abbr title="невозможно"><i class="icon-ban-circle"></i></abbr></g:else>
          </td>
          <td>
            <a class="button" style="z-index:1" href="${g.createLink(controller:'agreement',action:'space',id:record.id)}" title="Редактировать" target="_blank"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      <g:if test="${!spaces}">
        <tr>
          <td colspan="11" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Объектов аренды не найдено</a>
          </td>
        </tr>
      </g:if>
        <tr>
          <td colspan="11" class="btns" style="text-align:center">
          <g:if test="${asort==1}">
            <a class="button" href="javascript:void(0)" onclick="$('newspace_asort').value=1;$('newspaceForm').submit();">
              Новый договор аренды &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </g:if><g:else>
            <a class="button" href="javascript:void(0)" onclick="$('newspace_asort').value=0;$('newspaceForm').submit();">
              Новый договор субаренды &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </g:else>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>
<g:form name="newspaceForm" url="${[controller:'agreement',action:'space']}" method="post" target="_blank">
  <input type="hidden" id="company_id" name="company_id" value="${company.id}"/>
  <input type="hidden" id="newspace_asort" name="asort" value="0"/>
</g:form>
