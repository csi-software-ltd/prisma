<div id="ajax_wrap">
<g:if test="${users}">
  <div id="resultList">  
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr style="line-height:15px">                              
          <th>Пользователь</th>
          <th>Краткое имя</th>
          <th>Руководитель</th>                                  
        </tr>
      </thead>
      <tbody>
      <g:each in="${users}">
        <tr align="left">
          <td>${it.name}</td>
          <td>${Pers.get(it.pers_id?:0)?.shortname?:''}</td>
          <td align="center"><i class="icon-${it.is_leader?'ok':'minus'}" title="${it.is_leader?'Руководитель':'Сотрудник'}"></i></td>                                                      
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</g:if>
  <div style="padding:10px">
    <div class="fleft">Найдено: ${users.size()}</div>   
    <div class="clear"></div>   
  </div>  
</div>
