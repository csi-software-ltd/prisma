<g:formRemote name="persaccountAddForm" url="[action:'savePersaccountDetail',params:[pers_id:inrequest?.pers_id?:0]]" method="post" onSuccess="processpersaccountResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorpersaccountlist">
      <li></li>
    </ul>
  </div>
  <div id="persaccount"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult.size()}</div>   
    <div class="clear"></div>
  </div>  
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>      
        <tr style="line-height:15px">                              
          <th>БИК банка</th>          
          <th>Название банка</th>          
          <th width="230">Номер</th>                                     
          <th>Действует до<br>месяц/год</th>          
          <th>Статус</th>
          <th width="70"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult}" status="i" var="record">
        <tr align="left" style="font-weight:${record.is_main?700:400}">
          <td>${Bank.get(record.bank_id?:0)?.id?:''}</td>
          <td>${Bank.get(record.bank_id?:0)?.name?:''}</td>
          <td>карта: ${record?.nomer}<br/>лиц. счет: ${g.account(value:record?.paccount)}</td>                            
          <td align="center">${record?.validmonth}/${record?.validyear}</td>                 
          <td align="center"><i class="icon-${record?.modstatus?'ok':'minus'}" title="${record.modstatus?'активный':'неактивный'}"></i></td>
          <td align="center" nowrap>       
          <g:if test="${user?.group?.is_persaccountedit}">
            <g:if test="${!record.is_main && record.modstatus && Pers.get(record?.pers_id?:0).perstype==Pers.PERSTYPE_DIRECTOR}">
              <g:remoteLink class="button" style="z-index:1" url="${[controller:'user',action:'setmainpersaccount',id:record.id,params:[pers_id:record.pers_id]]}" title="Сделать главным" onSuccess="getPersacount()"><i class="icon-warning-sign"></i></g:remoteLink> &nbsp;
            </g:if>                      
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="editPersaccount('${record?.id?:0}')"><i class="icon-pencil"></i></a>
            <g:if test="${record.modstatus==0}">
              &nbsp; <g:remoteLink class="button" style="z-index:1" url="${[controller:'user', action:'deletepersaccount', id:record.id, params:[pers_id:record.pers_id]]}" title="Удалить" onSuccess="getPersacount()"><i class="icon-trash"></i></g:remoteLink>
            </g:if>
          </g:if> 
          </td>          
        </tr>
      </g:each>
      <g:if test="${user?.group?.is_persaccountedit}">
        <tr align="center">
          <td colspan="6" class="btns">
            <a class="button" id="addpersaccountbutton" href="javascript:void(0)" onclick="editPersaccount(0)">Добавить новый счет &nbsp;<i class="icon-angle-right icon-large"></i></a>
          </td>
        </tr>
      </g:if>  
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.size()}</span> 
    <div class="clear"></div>    
  </div>  
</div>
<g:formRemote name="persaccountForm" url="[action:'persaccount']" update="persaccount" onComplete="\$('errorpersaccountlist').up('div').hide();jQuery('#persaccountAddForm').slideDown();" style="display:none">
  <input type="text" id="persaccount_id" name="id" value="0" style="display:none"/>
  <input type="submit" class="button" id="persaccountdetail_submit_button" value="Показать"/>
</g:formRemote>
