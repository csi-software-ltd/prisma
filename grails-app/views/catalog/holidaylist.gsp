<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult.size()}</div>    
    <div class="clear"></div>
  </div>
<g:if test="${searchresult}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>          
          <th>Название</th>
          <th>Полное название</th>
          <th width="50"></th>          
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult}" status="i" var="record">
        <tr align="left" style="${record.status?'color:green':''}">         
          <td>${record?.hdate?String.format('%td.%<tm.%<tY',record.hdate):''}</td>
          <td>${record.status?'праздничный день, попадающий на будний':'рабочий день, попадающий на выходной'}</td> 
          <td align="center">
            <g:if test="${user?.group?.is_holidayedit}">
              <a class="button" title="Редактировать" onclick="showHolidayWindow(${record?.id?:0})"><i class="icon-pencil"></i></a> &nbsp;
              <g:remoteLink class="button" url="${[controller:'catalog',action:'remholiday',id:record.id]}" title="Удалить" onSuccess="\$('form_submit_button').click();" before="if(!confirm('Подтвердите удаление!')) return false"><i class="icon-trash"></i></g:remoteLink>
            </g:if>
          </td>          
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.size()}</span>   
  </div>
</g:if>
</div>
