<g:formRemote id="periodUpdateForm" name="periodUpdateForm" url="[action:'updateperiod']" method="post" onSuccess="processupdateperiodResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorperiodlist">
      <li></li>
    </ul>
  </div>
  <div id="agentperiod"></div>
</g:formRemote>
<g:formRemote id="periodDateForm" name="periodDateForm" url="[action:'computeoldperiods',id:agentagr.id]" method="post" onSuccess="processcomputeoldperiodResponse(e)" style="display:none">
  <div>
    <label for="oldperiod_computedate">Дата расчета периода:</label>
    <g:datepicker class="normal nopad" name="oldperiod_computedate" value=""/>
    <div class="fright">
    <g:if test="${iscanedit}">
      <input type="submit" class="button" value="Сохранить" />
    </g:if>
      <input type="reset" class="button" value="Отмена" onclick="jQuery('#periodDateForm').slideUp();"/>
    </div>
  </div>
  <hr class="admin" />
</g:formRemote>
<g:formRemote id="addperiodUpdateForm" name="addperiodUpdateForm" url="[action:'updateaddperiod']" method="post" onSuccess="processupdateaddperiodResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="erroraddperiodlist">
      <li></li>
    </ul>
  </div>
  <div id="agentaddperiod"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div class="tabs fright">
    <a id="periodstatus0" <g:if test="${modstatus==0}">class="active"</g:if> onclick="setPeriodstatus(0)"><i class="icon-list icon-large"></i> Новые </a>
    <a id="periodstatus1" <g:if test="${modstatus==1}">class="active"</g:if> onclick="setPeriodstatus(1)"><i class="icon-list icon-large"></i> В оплате </a>
    <a id="periodstatus-100" <g:if test="${modstatus==-100}">class="active"</g:if> onclick="setPeriodstatus(-100)"><i class="icon-list icon-large"></i> Перерасчеты </a>
  </div>
  <div class="clear"></div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Месяц</th>
          <th>Компания</th>
          <th>Задолженность</th>
          <th>Дата выдачи<br/>Дата погашения</th>
          <th>Расчет от<br/>Расчет до</th>
          <th>Общий процент</th>
          <th>Перерасчет<br/>пред. периодов</th>
          <th>К оплате</th>
          <th>Действия</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${periods}" status="i" var="record">
        <tr align="center">
          <td>${String.format('%tB %<tY',new Date(record.year-1900,record.month-1,1))}</td>
          <td>${record.client_name}</td>
          <td>${number(value:record.plan_debt)}<i class="icon-${valutas[record.valuta_id]}"></i></td>
          <td>${String.format('%td.%<tm.%<tY',record.adate)}<br/>${String.format('%td.%<tm.%<tY',record.enddate)}</td>
          <td>${String.format('%td.%<tm.%<tY',record.datestart)}<br/>${String.format('%td.%<tm.%<tY',record.dateend)}</td>
          <td>${number(value:record.plan_calcrate)}</td>
          <td>${number(value:record.clientdebt)}</td>
          <td>${number(value:record.clientdebt+record.plan_summa)}</td>
          <td valign="middle">
          <g:if test="${modstatus==0&&iscanedit&&record.is_last&&!record.ishaveact&&record.parent==0}">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('period_id').value=${record.id};$('period_submit_button').click();"><i class="icon-pencil"></i></a>&nbsp;&nbsp;
            <g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'deleteperiod',id:record.akr_id,params:[period_id:record.id]]}" title="Удалить" onSuccess="getPeriods()"><i class="icon-trash"></i></g:remoteLink>
          </g:if><g:elseif test="${iscanedit&&record.parent>0&&record.plan_modstatus==0}">
            <g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'deleteperiod',id:record.akr_id,params:[period_id:record.id]]}" title="Удалить" onSuccess="getPeriods()"><i class="icon-trash"></i></g:remoteLink>
          </g:elseif>
          </td>
        </tr>
      </g:each>
      <g:if test="${!periods}">
        <tr>
          <td colspan="9" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Расчетов не найдено</a>
          </td>
        </tr>
      </g:if>
      <g:if test="${modstatus==-100&&iscanedit}">
        <tr>
          <td colspan="9" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('addperiod_submit_button').click();">
              Добавить перерасчет периода &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      <g:if test="${modstatus==0&&iscanedit}">
        <tr>
          <td colspan="9" class="btns" style="text-align:center">
            <g:remoteLink class="button" url="${[controller:controllerName,action:'computeperiods',id:agentagr.id]}" onSuccess="getPeriods()">
              Новый расчет &nbsp;<i class="icon-angle-right icon-large"></i>
            </g:remoteLink>
          </td>
        </tr>
        <tr>
          <td colspan="9" class="btns" style="text-align:center">
            <g:remoteLink class="button" url="${[controller:controllerName,action:'computenextperiods',id:agentagr.id]}" onSuccess="getPeriods()">
              Рассчитать следующий период &nbsp;<i class="icon-angle-right icon-large"></i>
            </g:remoteLink>
          </td>
        </tr>
        <tr>
          <td colspan="9" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="jQuery('#periodUpdateForm').slideUp();jQuery('#periodDateForm').slideDown();">
              Рассчитать период за дату &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="periodForm" url="[action:'agentperiod', params:[agentagr_id:agentagr.id]]" update="agentperiod" onComplete="\$('errorperiodlist').up('div').hide();jQuery('#periodDateForm').slideUp();jQuery('#periodUpdateForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="period_submit_button" value="Показать"/>
  <input type="hidden" id="period_id" name="id" value="0"/>
</g:formRemote>
<g:formRemote name="addperiodForm" url="[action:'agentaddperiod', params:[agentagr_id:agentagr.id]]" update="agentaddperiod" onComplete="\$('erroraddperiodlist').up('div').hide();jQuery('#addperiodUpdateForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="addperiod_submit_button" value="Показать"/>
</g:formRemote>
<script type="text/javascript">
  jQuery("#oldperiod_computedate").mask("99.99.9999",{placeholder:" "});
</script>
