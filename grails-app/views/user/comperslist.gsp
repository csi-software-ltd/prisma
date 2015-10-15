<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${compers.size()}</div>   
    <div class="clear"></div>
  </div>  
<g:if test="${compers}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>      
        <tr style="line-height:15px">                              
          <th>Название компании</th>          
          <th>Позиция</th>          
          <th>Учредитель</th>                            
          <th>Дата<br/>начала работы</th>
          <th>Дата<br/>окончания работы</th>
          <th>Офиц. оклад</th>
          <th>Статус</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${compers}" status="i" var="record">
        <tr align="center">
          <td><g:link controller="company" action="detail" id="${record.company_id}" target="_blank">${Company.get(record.company_id?:0)?.name?:''}</g:link></td>
          <td>${Position.get(record.position_id?:0)?.name?:''}</td>
          <td>нет</td>
          <td>${record?.jobstart?String.format('%td.%<tm.%<tY',record?.jobstart):''}</td>
          <td>${record?.jobend?String.format('%td.%<tm.%<tY',record?.jobend):''}</td>
          <td><g:if test="${user.confaccess}">${intnumber(value:record.salary?:0)}</g:if></td>
          <td>${record.modstatus?'работает':'не работает'}</td>                    
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${compers.size()}</span> 
    <div class="clear"></div>    
  </div>  
</g:if>
</div>
