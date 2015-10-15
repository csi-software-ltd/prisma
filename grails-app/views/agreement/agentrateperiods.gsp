<g:formRemote id="agentrateperiodUpdateForm" name="agentrateperiodUpdateForm" url="[action:'updateagentrateperiod']" method="post" onSuccess="getAgentratePeriods()" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="erroragentrateperiodlist">
      <li></li>
    </ul>
  </div>
  <div id="agentrateperiod"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div class="clear"></div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Месяц</th>
          <th>Компания</th>
          <th>Дата начала</th>
          <th>Дата окончания</th>
          <th>Общий процент</th>
          <th width="30"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${agentrateperiods}" status="i" var="record">
        <tr align="center">
          <td>${String.format('%tB %<tY',new Date(record.year-1900,record.month-1,1))}</td>
          <td>${contragents[record.agentkredit_id]}</td>
          <td>${String.format('%td.%<tm.%<tY',record.datestart)}</td>
          <td>${String.format('%td.%<tm.%<tY',record.dateend)}</td>
          <td>${number(value:record.calcrate)}</td>
          <td valign="middle">
          <g:if test="${iscanedit}">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('agentrateperiod_id').value=${record.id};$('agentrateperiod_submit_button').click();"><i class="icon-pencil"></i></a>
          </g:if>
          </td>
        </tr>
      </g:each>
      <g:if test="${!agentrateperiods}">
        <tr>
          <td colspan="9" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Расчетов не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="periodForm" url="[action:'agentrateperiod', params:[agentagr_id:agentagr.id]]" update="agentrateperiod" onComplete="\$('erroragentrateperiodlist').up('div').hide();jQuery('#agentrateperiodUpdateForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="agentrateperiod_submit_button" value="Показать"/>
  <input type="hidden" id="agentrateperiod_id" name="id" value="0"/>
</g:formRemote>