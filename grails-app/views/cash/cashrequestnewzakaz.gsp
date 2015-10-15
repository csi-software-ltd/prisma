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
            <a class="button nopad" style="z-index:1" title="Добавить в запрос" onclick="addtorequest(${record.id})"><i class="icon-play"></i></a>
          </td>
        </g:if>
        </tr>
      </g:each>
        <tr>
          <td colspan="5" class="btns" style="text-align:center">
            <g:link class="button" style="z-index:1" controller="cash" action="detail" target="_blank">Новая заявка &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</div>