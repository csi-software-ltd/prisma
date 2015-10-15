<style type="text/css">
  .list td,.list th { font-size: 12px }
</style>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>На дату</th>
          <th>Кому</th>
          <th>Сумма</th>
          <th>Цель</th>
        <g:if test="${session.user.cashaccess==3&&cashrequest.modstatus<4}">
          <th></th>
        </g:if>
        </tr>
      </thead>
      <tbody>
      <g:each in="${cashzakazlist.records}" status="i" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.todate)}</td>
          <td>${record.department_id?departments[record.department_id]:record.initiator_name}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td>${record.purpose}</td>
        <g:if test="${session.user.cashaccess==3&&cashrequest.modstatus<4}">
          <td width="40">
            <a class="button" href="${g.createLink(controller:'cash',action:'detail',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>&nbsp;&nbsp;
            <a class="button nopad" style="z-index:1" title="Удалить" onclick="removefromrequest(${record.id})"><i class="icon-trash"></i></a>
          </td>
        </g:if>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>