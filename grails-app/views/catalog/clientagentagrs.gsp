<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Название</th>
          <th>Банки</th>
          <th>Суммарный портфель</th>
          <th>Остаток задолженности</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${agentagrs.records}" status="i" var="record">
        <tr align="center">
          <td><g:link style="z-index:1" controller="agreement" action="agent" id="${record.id}" target="_blank">${record.name}</g:link></td>
          <td><g:each in="${banks[record.id]}">${it}<br/></g:each></td>
          <td><g:if test="${record.sumrub>0}">${number(value:record.sumrub)}<i class="icon-rub"></i></g:if><g:if test="${record.sumusd>0}"><br/>${number(value:record.sumusd)}<i class="icon-usd"></i></g:if><g:if test="${record.sumeur>0}"><br/>${number(value:record.sumeur)}<i class="icon-eur"></i></g:if></td>
          <td>${number(value:record.lastbodydebt)}</td>
        </tr>
      </g:each>
      <g:if test="${!agentagrs.records}">
        <tr>
          <td colspan="4" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Договоров не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>