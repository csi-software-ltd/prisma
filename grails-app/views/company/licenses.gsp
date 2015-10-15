<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>СРО</th>
          <th>Лицензия</th>
          <th>Срок договора</th>
          <th>Номер договора</th>
          <th>Сумма допуска</th>
          <th>Вступительный<br/>взнос</th>
          <th>Оплаченый допуск</th>
          <th>Тип оплаты<br/>вступ. взноса</th>
          <th>Членские взносы</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${licenses}" status="i" var="record">
        <tr align="center">
          <td><g:link controller="company" action="detail" id="${record.sro_id}" target="_blank">${record.sro_name}</g:link></td>
          <td>${industries[record.industry_id]}</td>
          <td><g:rawHtml>${record.adate?String.format('%td.%<tm.%<tY',record.adate)+'<br/>':''}</g:rawHtml>${record.enddate?String.format('%td.%<tm.%<tY',record.enddate):'бессрочно'}</td>
          <td>${record.anumber}</td>
          <td>${intnumber(value:record.alimit)}</td>
          <td>${intnumber(value:record.entryfee)}</td>
          <td>${intnumber(value:record.paidfee)}</td>
          <td>${record.paytype==1?'Единовременно':record.paytype==2?'Ежемесячно':''}</td>
          <td>${intnumber(value:record.regfee)}</td>
          <td>
            <a class="button" style="z-index:1" href="${g.createLink(controller:'agreement',action:'license',id:record.id)}" title="Редактировать" target="_blank"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      <g:if test="${!licenses}">
        <tr>
          <td colspan="10" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Лицензий не найдено</a>
          </td>
        </tr>
      </g:if>
        <tr>
          <td colspan="10" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('newlicenseForm').submit();">
              Новый лицензионный договор &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>
<g:form name="newlicenseForm" url="${[controller:'agreement',action:'license']}" method="post" target="_blank">
  <input type="hidden" id="company_id" name="company_id" value="${company.id}"/>
</g:form>
