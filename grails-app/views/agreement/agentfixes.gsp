<g:formRemote id="fixaddForm" name="fixaddForm" url="[action:'addagentfix']" method="post" onSuccess="processaddfixResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorfixlist">
      <li></li>
    </ul>
  </div>
  <div id="agentfix"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата проводки</th>
          <th>Агент</th>
          <th>Сумма</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${fixes}" status="i" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td>${record.agent_name}</td>
          <td>${number(value:record.summa)}</td>
          <td valign="middle">
          <g:if test="${iscanedit}">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('agentfix_id').value=${record.id};$('agentfix_submit_button').click();"><i class="icon-pencil"></i></a>&nbsp;&nbsp;
            <g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'deleteagentfix',id:agentagr.id,params:[agentfix_id:record.id]]}" title="Удалить" onSuccess="getAgentfixes()"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </td>
        </tr>
      </g:each>
      <g:if test="${!fixes}">
        <tr>
          <td colspan="4" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Корректировок не найдено</a>
          </td>
        </tr>
      </g:if>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="4" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('agentfix_id').value=0;$('agentfix_submit_button').click();">
              Добавить корректировку &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="fixForm" url="[action:'agentfix', params:[agentagr_id:agentagr.id]]" update="agentfix" onComplete="\$('errorfixlist').up('div').hide();jQuery('#fixaddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="agentfix_submit_button" value="Показать"/>
  <input type="hidden" id="agentfix_id" name="id" value="0"/>
</g:formRemote>
