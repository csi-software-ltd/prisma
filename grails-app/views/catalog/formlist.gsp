<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult.records.size()}</div>    
    <div class="clear"></div>
  </div>
<g:if test="${searchresult.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>          
          <th>Название</th>
          <th>Полное название</th>
          <th>Действия</th>          
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="left">         
          <td>${record.name}</td>
          <td>${record.fullname}</td> 
          <td>
            <a class="button" title="Редактировать" onclick="showFormWindow(${record?.id?:0})"><i class="icon-pencil"></i></a>
          </td>          
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.records.size()}</span>   
  </div>
</g:if>
</div>