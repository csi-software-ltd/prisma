<g:formRemote id="servicecalculationAddForm" name="servicecalculationAddForm" url="[action:'updateservicecalculation']" method="post" onSuccess="processAddservicecalculationResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorservicecalculationlist">
      <li></li>
    </ul>
  </div>
  <div id="servicecalculation"></div>
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
          <th width="70"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${servicecalculations}" var="record">
        <tr align="center">
          <td>${String.format('%tB %<tY',new Date(record.year-1900,record.month-1,1))}</td>
          <td>${String.format('%td.%<tm.%<tY',record.calcdate)}</td>
          <td>${record.schet?:'нет'}<g:if test="${record.schetdate}"><br/>${String.format('%td.%<tm.%<tY',record.schetdate)}</g:if></td>
          <td>${number(value:record.summa)}</td>
          <td>
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('servicecalculation_id').value=${record.id};$('servicecalculation_submit_button').click();"><i class="icon-pencil"></i></a>&nbsp;
            <g:remoteLink class="button" url="${[controller:controllerName,action:'deleteservicecalculation',id:record.id,params:[service_id:service.id]]}" title="Удалить" onSuccess="getCalcs()"><i class="icon-trash"></i></g:remoteLink>
          </td>
        </tr>
      </g:each>
      <g:if test="${!servicecalculations}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Начислений не найдено</a>
          </td>
        </tr>
      </g:if>
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('servicecalculation_id').value=0;$('servicecalculation_submit_button').click();">
              Добавить начисление &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="servicecalculationForm" url="[action:'servicecalculation', params:[service_id:service.id]]" update="servicecalculation" onComplete="\$('errorservicecalculationlist').up('div').hide();jQuery('#servicecalculationAddForm').slideDown();" style="display:none">
  <input type="hidden" id="servicecalculation_id" name="id" value="0"/>
  <input type="submit" class="button" id="servicecalculation_submit_button" value="Показать"/>
</g:formRemote>