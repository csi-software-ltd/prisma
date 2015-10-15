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
          <th>Дата</th>
          <th>Арендатор</th>
          <th>Арендодатель</th>
          <th>Договор<br/>Срок</th>
          <th>Площадь</th>
          <th>Цена<br/>Плата<br/>Доп услуги</th>
          <th>Автор</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${history}" status="i" var="record">
        <tr align="center">
          <td>${shortDate(date:record.inputdate)}</td>
          <td>${record.arendator_name}</td>
          <td>${record.arendodatel_name}</td>
          <td>${record.anumber}<br/>${record.enddate?String.format('%td.%<tm.%<tY',record.enddate):'нет'}</td>
          <td>${number(value:record.area)}</td>
          <td>
            ${number(value:record.ratemeter)}<br/>${number(value:record.rate)}<br/>
          <g:if test="${record.is_addpayment}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>${record.admin_name}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>