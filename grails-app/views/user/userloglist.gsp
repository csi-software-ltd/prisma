<div id="ajax_wrap">
<g:if test="${userlog.count}">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${userlog.count}</div>
    <div class="fright">
      <g:paginate controller="user" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${userlog.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>      
        <tr style="line-height:15px">                              
          <th>Дата</th>          
          <th>Успешность</th>          
          <th>IP</th>                                     
        </tr>
      </thead>
      <tbody>
      <g:each in="${userlog.records}" status="i" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY %<tT',record?.logtime)}</td>
          <td>
            <g:if test="${record.success==1}"><i class="icon-ok"></i></g:if>
            <g:elseif test="${record.success==0}"><i class="icon-minus"></i></g:elseif>
            <g:elseif test="${record.success==2}">временно заблокирован</g:elseif>
            <g:elseif test="${record.success==3}">заблокирован</g:elseif>
            <g:elseif test="${record.success==-2}"><i class="icon-minus"></i>&nbsp; SMS</g:elseif>
          </td>
          <td>${record.ip}</td>                              
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <div class="fleft">Найдено: ${userlog.count}</div>
    <div class="fright">
      <g:paginate controller="user" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${userlog.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div  
</g:if>
</div>
