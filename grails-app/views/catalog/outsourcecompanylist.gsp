<div id="ajax_wrap">  
  <div style="padding:10px">
    <span class="fleft">Найдено: ${companies.size()}</span>    
    <div class="clear"></div>
  </div>
<g:if test="${companies}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th width="30">Код</th>
          <th>Название</th>          
        </tr>
      </thead>
      <tbody>
      <g:each in="${companies}" status="i" var="record">
        <tr align="center">
          <td>${record.id}</td>
          <td align="left">${record.name}</td>          
        </tr>
      </g:each>
      </tbody>
    </table>       
  </div>
</g:if>
</div>
