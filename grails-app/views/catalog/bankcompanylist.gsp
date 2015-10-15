<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult.count}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${searchresult.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th width="90px" rowspan="2">ИНН</th>
          <th rowspan="2">Название</th>         
          <th colspan="3">Счета</th>	  
        </tr>
	      <tr>                  
          <th width="60px">Статус БК</th>
          <th width="200px">Факт. остаток<br>Дата</th>
          <th width="200px">Подтв. остаток<br>Дата</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="left">
          <td>${record.inn}</td>
          <td><g:link controller="company" action="detail" id="${record.id}" target="_blank">${record.name?:''}</g:link></td>  
     	    <td  align="center"><g:if test="${bankaccount[i]}">
            <g:if test="${bankaccount[i]==1}"><abbr title="активен"><i class="icon-ok"></i></abbr></g:if>
            <g:elseif test="${bankaccount[i]==2}"><abbr title="просрочен"><i class="icon-ban-circle"></i></abbr></g:elseif>
            <g:elseif test="${bankaccount[i]==-1}"><abbr title="заблокирован"><i class="icon-lock"></i></abbr></g:elseif>
            <g:else><abbr title="нет"><i class="icon-minus"></i></abbr></g:else>
          </g:if>
          </td>
	        <td align="center"><g:if test="${user?.confaccess>0}">
                <g:if test="${bankaccount[i]}">
                  ${intnumber(value:bankaccount[i]?.actsaldo?:0)}<br/>${bankaccount[i]?.actsaldodate?shortDateNoTime(date:bankaccount[i]?.actsaldodate):''}
                </g:if>
              </g:if>  
          </td>
          <td align="center"><g:if test="${user?.confaccess>0}">
                <g:if test="${bankaccount[i]}">
                  ${number(value:bankaccount[i]?.saldo?:0)}<br/>${bankaccount[i]?.saldodate?shortDateNoTime(date:bankaccount[i]?.saldodate):''}
                </g:if>
              </g:if>
          </td>
	      </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.count}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>