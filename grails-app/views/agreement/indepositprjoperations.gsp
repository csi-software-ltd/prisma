<g:formRemote id="indepositprjoperationAddForm" name="indepositprjoperationAddForm" url="[controller:controllerName, action:'addindepositprjoperation']" method="post" onSuccess="processAddindepositprjoperationResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorindepositprjoperationlist">
      <li></li>
    </ul>
  </div>
  <div id="indepositprjoperation"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">
      <label class="auto" for="project_id">Проект:</label>
      <g:select style="margin-bottom:0px" name="project_id" value="${project_id}" from="${projects}" optionValue="name" optionKey="id" noSelection="${['0':'все']}" onchange="setProjectId(this.value)"/>
    </div>
    <div class="fright" style="padding-top:10px">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${operations.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Дата</th>
          <th>Проект</th>
          <th>Тип операции</th>
          <th>Сумма</th>
          <th>Сальдо</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${operations.records}" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.operationdate)}</td>
          <td>${record.project_name}</td>
          <td>${record.is_percent?'списание процентов':record.is_transfer?'перевод':record.summa>0?'приход':'расход'}</td>
          <td>${number(value:record.summa)}</td>
          <td>${number(value:record.saldo+(record.is_main?deposit.startsaldo:0.0g))}</td>
          <td valign="middle">
          <g:if test="${iscanedit&&record.is_transfer&&record.related_id>0}">
            <g:remoteLink style="z-index:1" class="button" url="${[controller:'agreement', action:'deleteindepositprjoperation', id:record.id, params:[deposit_id:deposit.id]]}" title="Удалить" onSuccess="getOperations()"><i class="icon-trash"></i></g:remoteLink>
          </g:if>
          </td>
        </tr>
      </g:each>
      <g:if test="${!operations.records}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Операций не найдено</a>
          </td>
        </tr>
      </g:if>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="6" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)" onclick="$('indepositprjoperation_submit_button').click();">
              Добавить внутреннюю операцию &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="indepositprjoperationForm" url="[action:'indepositprjoperation', params:[deposit_id:deposit.id]]" update="indepositprjoperation" onComplete="\$('errorindepositprjoperationlist').up('div').hide();jQuery('#indepositprjoperationAddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="indepositprjoperation_submit_button" value="Показать"/>
</g:formRemote>