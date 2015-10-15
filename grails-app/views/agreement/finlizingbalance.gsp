<style type="text/css">
  table.list thead th, table.list tbody th, table.list thead td, table.list tbody td { font-size: 10px; padding: 8px 5px; }
</style>
<g:formRemote id="flperiodAddForm" name="flperiodAddForm" url="[action:'addfinlizingperiod']" method="post" onSuccess="processAddFlperiodResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorflperiodlist">
      <li></li>
    </ul>
  </div>
  <div id="finlizingperiod"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Месяц</th>
          <th>Число дней</th>
          <th>Сумма исходящих</th>
          <th>Компенсация</th>
          <th>Расход банка</th>
          <th>Сумма процентов<br/>Сумма погашения</th>
          <th>Приход банка</th>
          <th>Факт. задолженность</th>
          <th>1%</th>
          <th>Результат</th>
          <th>Возвраты комиссий</th>
          <th>Баланс</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${periods}" status="i" var="record">
        <%percent = bodydebt*0.01/365*record.qdays%>
        <%balance+= record.compensation+percent-record.returnsumma-refunds[record.id]%>
        <tr align="center">
          <td>${String.format('%tB %<tY',record.fmonth)}</td>
          <td>${record.qdays}</td>
          <td>${number(value:record.summa+payrequests[record.id])}</td>
          <td>${number(value:record.compensation)}</td>
          <td>${number(value:record.summa+payrequests[record.id]+record.compensation)}</td>
          <td>${number(value:record.procent)}<br/>${number(value:record.body)}</td>
          <td>${number(value:record.procent+record.body)}</td>
          <td>${number(value:bodydebt)}</td>
          <td>${number(value:percent)}</td>
          <td>${number(value:record.compensation+percent)}</td>
          <td>${number(value:record.returnsumma+refunds[record.id])}</td>
          <td>${number(value:balance)}</td>
          <td>
          <g:if test="${iscanedit}">
            <g:remoteLink class="button" style="z-index:1" before="if(!confirm('Вы действительно хотите удалить период?')) return false" url="${[controller:controllerName,action:'deletefinlizingperiod',id:record.id,params:[flizing_id:flizing.id]]}" title="Удалить" onSuccess="getBalance()"><i class="icon-trash"></i></g:remoteLink><br/>
          </g:if>
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('flperiod_id').value=${record.id};$('flperiod_submit_button').click();"><i class="icon-pencil"></i></a>
          </td>
        </tr>
        <%bodydebt-= record.body%>
      </g:each>
        <tr>
          <td colspan="14" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('flperiod_id').value=0;$('flperiod_submit_button').click();">
              Добавить период &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="flperiodForm" url="[action:'finlizingperiod', params:[flizing_id:flizing.id]]" update="finlizingperiod" onComplete="\$('errorflperiodlist').up('div').hide();jQuery('#flperiodAddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="flperiod_submit_button" value="Показать"/>
  <input type="hidden" id="flperiod_id" name="id" value="0"/>
</g:formRemote>