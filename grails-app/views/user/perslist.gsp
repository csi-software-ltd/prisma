<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${pers.count}</div>
    <div class="fright">
      <g:paginate controller="user" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${pers.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>  
<g:if test="${pers.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>                              
          <th width="30">№</th>
          <th>Фамилия</th>
          <th width="150">Реквизиты</th>
          <th width="90">Паспорт</th>
          <th>Пользователь</th>
          <th>Должность<br />Название компании</th>
          <th>Тип</th>          
          <th width="30"></th>          
        </tr>
      </thead>
      <tbody>
      <g:each in="${pers.records}" status="i" var="record">
        <tr align="left">
          <td align="center">${record.id?:0}</td>
          <td>${record.shortname}</td>          
          <td>
            Учетный номер: ${record.snils?:'нет'}<br/>
            ИНН: ${record.inn?:'нет'}</br>
            СНИЛС: ${record.snilsdpf?:'нет'}
          </td>           
          <td align="center">${record.passport}</td>
          <td align="center"><i class="icon-${User.findWhere(pers_id:record.id,modstatus:1)?'ok':'minus'}" title="${User.findWhere(pers_id:record.id,modstatus:1)?'активный':'неактивный'}"></i></td>          
          <td>
          <g:each in="${Compers.findAllByPers_idAndModstatus(record?.id,1,[sort:'company_id',order:'asc'])}">
            ${compositions[it.composition_id]}<br /><g:link controller="company" action="detail" id="${it.company_id}" target="_blank">${Company.get(it.company_id)?.name?:''}</g:link><br />
          </g:each>
          </td>
          <td align="center">${((record.perstype?:0)==1)?'сотрудник':(((record.perstype?:0)==2)?'директор':(((record.perstype?:0)==3)?'специалист':''))}</td>      
          <td align="center">
            <a id="edit${i}" class="button" href="${createLink(action:'persdetail',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>           
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${pers.count}</span>
    <span class="fright">
      <g:paginate controller="user" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${pers.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>  
</g:if>
</div>
