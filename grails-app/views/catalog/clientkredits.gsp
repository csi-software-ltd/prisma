<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Банк</th>
          <th>№ договора</th>
          <th>Дата договора<br/>Дата выдачи</th>
          <th>Сумма<br/>Ставка<br/>Факт. задолжность</th>
          <th>Тип кредита</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${kredits}" status="i" var="record">
        <tr align="center">
          <td>${Bank.get(record.bank_id)?.name?:''}</td>
          <td><g:link style="z-index:1" controller="agreement" action="kredit" id="${record.id}" target="_blank">${record.anumber}</g:link></td>
          <td>${String.format('%td.%<tm.%<tY',record.adate)}<br/>${String.format('%td.%<tm.%<tY',record.startdate)}</td>
          <td>${intnumber(value:record.summa)}<br/>${number(value:record.rate)}<br/>${intnumber(value:record.debt)}</td>
          <td>${record.kredtype==1?'Кредит':record.kredtype==2?'Кредитная линия':'Овердрафт'}</td>
        </tr>
      </g:each>
      <g:if test="${!kredits}">
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Кредитов не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>