<g:formRemote id="clfixaddForm" name="clfixaddForm" url="[action:'addclientfix']" method="post" onSuccess="processaddclfixResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorclfixlist">
      <li></li>
    </ul>
  </div>
  <div id="clientfix"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Сумма</th>
          <th>Дата запроса</th>
          <th>Дата проводки</th>
          <th>Сумма списания</th>
          <th width="100">Статус</th>
          <th width="60"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${fixes}" status="i" var="record">
        <tr align="center">
          <td>${number(value:record.summa)}</td>
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}</td>
          <td>${record.execdate?String.format('%td.%<tm.%<tY',record.execdate):'нет'}</td>
          <td>${number(value:record.clientcommission)}</td>
          <td>${record.modstatus==2?'исполнен':record.modstatus==1?'в работе':'запрос'}</td>
          <td valign="middle">
          <g:if test="${iscanedit&&record.modstatus==0}">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('clientfix_id').value=${record.id};$('clientfix_submit_button').click();"><i class="icon-pencil"></i></a>
          <g:if test="${record.deal_id==0}">
            &nbsp;&nbsp;<g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'deleteclientfix',id:agentagr.id,params:[clientfix_id:record.id]]}" title="Удалить" onSuccess="getClientfixes()"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </g:if>
          </td>
        </tr>
      </g:each>
      <g:if test="${!fixes}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Списаний не найдено</a>
          </td>
        </tr>
      </g:if>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('clientfix_id').value=0;$('clientfix_submit_button').click();">
              Добавить списание &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="fixForm" url="[action:'clientfix', params:[agentagr_id:agentagr.id]]" update="clientfix" onComplete="\$('errorclfixlist').up('div').hide();jQuery('#clfixaddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="clientfix_submit_button" value="Показать"/>
  <input type="hidden" id="clientfix_id" name="id" value="0"/>
</g:formRemote>
