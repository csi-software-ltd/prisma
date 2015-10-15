<g:formRemote id="okvedAddForm" name="okvedAddForm" url="[action:'addtookved']" method="post" onSuccess="processaddokvedResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorokvedlist">
      <li></li>
    </ul>
  </div>
  <div id="okved"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div class="tabs fright">
    <a id="compokvedstatus1" <g:if test="${modstatus==1}">class="active"</g:if> onclick="setCompokvedstatus(1)"><i class="icon-list icon-large"></i> Активные </a>
    <a id="compokvedstatus0" <g:if test="${modstatus==0}">class="active"</g:if> onclick="setCompokvedstatus(0)"><i class="icon-list icon-large"></i> Архив </a>
  </div>
  <div class="clear"></div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Код</th>
          <th>Название</th>
          <th>Раздел</th>
          <th>Актуальность</th>
          <th>Дата изменения</th>
          <th>Комментарий</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:if test="${modstatus==1}">
        <tr>
          <td colspan="7" class="btns" style="text-align:center">
            <a class="button" id="addokvedbutton" href="javascript:void(0)" onclick="$('okved_id').value=0;$('okved_submit_button').click();">
              Добавить ОКВЭД &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      <g:each in="${compokved}" status="i" var="record">
        <tr align="center" style="font-weight:${record.is_main?700:400}">
          <td>${record.okved_id}</td>
          <td>${record.okvedname}</td>
          <td>${record.okvedrazdel}</td>
          <td style="${!record.ok_modstatus?'color:red':''}">${record.ok_modstatus?'активный':'устаревший'}</td>
          <td>${record.moddate?String.format('%td.%<tm.%<tY',record.moddate):''}</td>
          <td>${record.comments?:''}</td>
          <td width="60">
          <g:if test="${!record.is_main}">
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('okved_id').value=${record.id};$('okved_submit_button').click();"><i class="icon-pencil"></i></a>
            <g:remoteLink class="button" style="z-index:1" url="${[controller:'company',action:'setmainokved',id:record.okved_id,params:[company_id:company.id]]}" title="Сделать основным" onSuccess="getOKVED()"><i class="icon-warning-sign"></i></g:remoteLink>
            <g:remoteLink class="button" style="z-index:1" url="${[controller:'company',action:'removefromokved',id:record.okved_id,params:[company_id:company.id]]}" title="Удалить" onSuccess="getOKVED()"><i class="icon-trash"></i></g:remoteLink>          
          </g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="okvedForm" url="[action:'okved',params:[company_id:company.id]]" update="okved" onComplete="\$('errorokvedlist').up('div').hide();jQuery('#okvedAddForm').slideDown();" style="display:none">
  <input type="submit" class="button" id="okved_submit_button" value="Показать" /> 
  <input type="hidden" id="okved_id" name="id" value="0" />  
</g:formRemote>
