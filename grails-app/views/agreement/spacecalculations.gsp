<g:formRemote id="spacecalculationAddForm" name="spacecalculationAddForm" url="[action:'updatespacecalculation']" method="post" onSuccess="processAddspacecalculationResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorspacecalculationlist">
      <li></li>
    </ul>
  </div>
  <div id="spacecalculation"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Период расчета</th>
          <th>Дата расчета</th>
          <th>Номер счета<br/>Дата счета</th>
          <th>Сумма</th>
          <th>Доп услуги</th>
          <th width="50"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${spacecalculations}" var="record">
        <tr align="center">
          <td>${String.format('%tB %<tY',new Date(record.year-1900,record.month-1,1))}</td>
          <td>${String.format('%td.%<tm.%<tY',record.calcdate)}</td>
          <td>${record.schet?:'нет'}<g:if test="${record.schetdate}"><br/>${String.format('%td.%<tm.%<tY',record.schetdate)}</g:if></td>
          <td>${number(value:record.summa)}</td>
          <td>
          <g:if test="${record.is_dop}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('spacecalculation_id').value=${record.id};$('spacecalculation_submit_button').click();"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      <g:if test="${!spacecalculations}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Начислений не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="spacecalculationForm" url="[action:'spacecalculation', params:[space_id:space.id]]" update="spacecalculation" onComplete="\$('errorspacecalculationlist').up('div').hide();jQuery('#spacecalculationAddForm').slideDown();" style="display:none">
  <input type="hidden" id="spacecalculation_id" name="id" value="0"/>
  <input type="submit" class="button" id="spacecalculation_submit_button" value="Показать"/>
</g:formRemote>