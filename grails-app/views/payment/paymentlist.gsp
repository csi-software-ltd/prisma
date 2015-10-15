<style type="text/css">
  table.list thead th, table.list tbody th, table.list thead td, table.list tbody td { font-size: 11px }
  tr.yellow > td{background:yellow !important;}
  tr.green > td{background:#87F717 !important;}
  tr.red > td{color:red;}
  tr.soft > td { opacity:0.7 !important }
</style>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult.count}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
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
          <th>Id</th>
          <th>Дата<br/>№</th>
          <th>Плательщик<br/>ИНН</th>
          <th>Получатель<br/>ИНН</th>
          <th>Сумма</th>
          <th>Тип</th>
          <th>Назначение</th>
          <th>Факт</th>
          <th width="30"></th>
        </tr>
      </thead>
      <tbody>     
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="left" class="<g:if test="${record.modstatus==2&&record.is_internal&&record.paytype==Payment.PAY_TYPE_IMPORT}">soft</g:if><g:elseif test="${record.modstatus==2&&record.payrequest_id>0&&(record.client_id||record.expensetype_id)}">green</g:elseif><g:elseif test="${record.modstatus==2}">yellow</g:elseif>">
          <td>${record.id}</td>
          <td>${String.format('%td.%<tm.%<tY',record.paydate)}<br/>${record.platnumber}</td>
          <td>${record.fromcompany}<br/>${record.frominn}</td>
          <td>${record.tocompany}<br/>${record.toinn}</td>
          <td>${number(value:record.summa,fdigs:2)}</td>
          <td>${record.is_internal?'внутр.':'внеш.'}<br/>${record.paytype==1?'исходящий':'входящий'}</td>
          <td><g:if test="${record.paycat in 1..3}">${record.paycat==3?(Pers.get(record.pers_id?:0)?.shortname?:''):record.paycat==1?((Agreementtype.get(record?.agreementtype_id?:0)?.name?:'')+(record?.agreementnumber?' № '+record?.agreementnumber:'')):record.paycat==2?(Kbkrazdel.get(record?.kbkrazdel_id?:'')?.name?:''):''}<br/></g:if><g:shortString length="40" text="${record.destination}"/></td>
          <td align="center">
            <g:if test="${record.payrequest_id>0}"><a class="button" href="${createLink(controller:'payment',action:'payrequestdetail',id:record.payrequest_id)}" title="есть" target="_blank"><i class="icon-ok"></i></a></g:if>
            <g:else><abbr title="нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td align="center" nowrap>
            <g:if test="${user?.group?.is_payordering}"><a class="button" href="${createLink(controller:'payment',action:'paymentdetail',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a> &nbsp; </g:if>
            <g:if test="${record?.modstatus==2 && !record?.finstatus && user?.group?.is_payedit}"><a class="button" href="javascript:void(0)" title="Применить" onclick="setSaldo(${record?.id})"><i class="icon-ok"></i></a></g:if>
            <g:if test="${record?.modstatus<2 && user?.group?.is_payedit}"><a class="button" href="javascript:void(0)" title="Удалить" onclick="deletePayment(${record?.id},'${record.platnumber}')"><i class="icon-trash"></i></a></g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.count}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${searchresult.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
