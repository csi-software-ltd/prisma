<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Номер</th>
          <th>Дата начала</th>
          <th>Дата окончания</th>
          <th>Сумма договора</th>
          <th width="350">Назначение договора</th>
          <th width="50"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${spaceservagrs}" var="record">
        <tr align="center">
          <td>${record.anumber}</td>
          <td>${String.format('%td.%<tm.%<tY',record.adate)}</td>
          <td>${String.format('%td.%<tm.%<tY',record.enddate)}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td align="left" width="350">${record.description}</td>
          <td>
            <g:link style="z-index:1" controller="agreement" action="trade" id="${record.id}" title="Редактировать" target="_blank"><i class="icon-pencil icon-large"></i></g:link>
          </td>
        </tr>
      </g:each>
      <g:if test="${!spaceservagrs}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Договоров услуг не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>