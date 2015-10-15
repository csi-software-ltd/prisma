<div id="ajax_wrap">  
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.count}</span>    
    <div class="clear"></div>
  </div>
<g:if test="${searchresult.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Имя</th>
          <th>Логин</th>
          <th>Email</th>               
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="left">
          <td>${record.name?:''}</td>
          <td>${record.login?:''}</td>
          <td>${record.email?:''}</td>
        </tr>
      </g:each>
      </tbody>
    </table>       
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.count}</span> 
    <div class="clear"></div>
  </div>
</g:if>
</div>
