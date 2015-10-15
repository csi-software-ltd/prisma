<div id="ajax_wrap">
<g:if test="${subs}">
  <div id="resultList">  
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>                              
          <th>Название</th>                                  
        </tr>
      </thead>
      <tbody>
      <g:each in="${subs}">
        <tr align="left">
          <td>${it.name}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</g:if>
  <div style="padding:10px">
    <div class="fleft">Найдено: ${subs.size()}</div>   
    <div class="clear"></div>   
  </div>  
</div>
