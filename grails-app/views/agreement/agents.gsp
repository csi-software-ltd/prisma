<style type="text/css">
  tr td{ cursor: pointer; }
  tr.current td{ background:#aaa !important; }
  tr.detailed td{
    background:#aaa !important;
    cursor: default!important;
    -webkit-border-bottom-left-radius:0px !important;
    -webkit-border-radius: 0px 0px 0px 0px !important;
    border-bottom-left-radius:0px !important;
  }
  tr.detailed:last-child td:first-child{
    -webkit-border-bottom-left-radius:8px !important;
    -webkit-border-radius: 0px 0px 0px 8px !important;
    border-bottom-left-radius:8px !important;
  }
  tr.detailed:last-child td:last-child{
    -webkit-border-bottom-right-radius:8px !important;
    -webkit-border-radius: 0px 0px 8px 0px !important;
    border-bottom-right-radius:8px !important;
  }
</style>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="936" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Имя</th>
          <th>Сумма начисленная</th>
          <th>Сумма корректировок</th>
          <th>Сумма выплат</th>
          <th>К оплате агенту</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${agents}" status="i" var="record">
        <tr align="center" onclick="getdetail(this,${record.agent_id})">
          <td width="100">${record.agent_name}</td>
          <td>${number(value:record.summa+record.summafix+record.summaprev)}</td>
          <td>${number(value:record.agentfix)}</td>
          <td>${number(value:record.actpaidsum)}</td>
          <td>${number(value:record.summa+record.summafix+record.summaprev-record.actpaidsum)}</td>
        </tr>
        <tr class="detail"><td colspan="5"><div style="display:none;"></div></td></tr>
      </g:each>
      <g:if test="${!agents}">
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Расчетов не найдено</a>
          </td>
        </tr>
      </g:if>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <g:remoteLink class="button" url="${[controller:controllerName,action:'computeagentact',id:agentagr.id]}" onSuccess="getAgents()">
              Новые акты &nbsp;<i class="icon-angle-right icon-large"></i>
            </g:remoteLink>
          </td>
        </tr>
      <g:if test="${isHaveAct}">
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <g:remoteLink class="button" url="${[controller:controllerName,action:'computnexteagentact',id:agentagr.id]}" onSuccess="getAgents()">
              Акты на следующий период &nbsp;<i class="icon-angle-right icon-large"></i>
            </g:remoteLink>
          </td>
        </tr>
      </g:if>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
