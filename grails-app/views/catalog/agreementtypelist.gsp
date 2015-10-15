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
          <th width="30px">Порядок<br/>сортировки</th>
          <th width="30"></th>          
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult}" status="i" var="record">
        <tr align="left">         
          <td>${record?.name}</td>
          <td align="center">${record?.sortorder}</td> 
          <td align="center">
            <g:if test="${user?.group?.is_agrtypeedit}"><a class="button" title="Редактировать" onclick="showAgreementtypeWindow(${record?.id?:0})"><i class="icon-pencil"></i></a></g:if>
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
