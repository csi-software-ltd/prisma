<style type="text/css">
  tr.red > td { background:mistyrose !important }
</style>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult.count}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${searchresult.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Компания</th>
          <th>Инн</th>
          <th>Тип налога</th>
          <th>Месяц, год налога</th>
          <th>Сумма</th>
          <th>Статус</th>
          <th width="60"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="center" class="${record.paystatus==-1?'red':''}">
          <td>${record.companyname}</td>
          <td>${record.inn}</td>
          <td>${taxes[record.tax_id]}</td>
          <td>${String.format('%tB %<tY',new Date(record.year-1900,record.month-1,1))}</td>
          <td>${number(value:record.summa)}</td>
          <td>${record.paystatus==1?'В оплате':record.paystatus==2?'Оплачена':record.paystatus==-1?'Не распознана':'Новая'}</td>
          <td>
          <g:if test="${iscanedit}">
          <g:if test="${record.paystatus==0}">
            <g:remoteLink class="button" style="z-index:1" title="В оплату" url="${[controller:controllerName, action:'paytaxreport', id:record.id]}" onSuccess="\$('form_submit_button').click()"><i class="icon-chevron-sign-right"></i></g:remoteLink>&nbsp;&nbsp;
          </g:if>
          <g:if test="${record.paystatus<=0}">
            <g:remoteLink class="button" style="z-index:1" title="Удалить" url="${[controller:controllerName, action:'deletetaxreport', id:record.id]}" before="if(!confirm('Вы действительно хотите удалить налоговую запись?')) return false" onSuccess="\$('form_submit_button').click()"><i class="icon-trash"></i></g:remoteLink>
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
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>