<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr style="line-height:15px">
          <th width="50px">Дата изменения</th>
          <th>Адресат</th>
          <th>Статус</th>
          <th>Описание</th>
          <th>Администратор</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${taskevent}" status="i" var="record">
        <tr align="left">
          <td>${shortDate(date:record.inputdate)}</td>
          <td>${User.get(record?.executor?:0)?.name?:''}</td>
          <td>${Taskstatus.get(record?.taskstatus?:0).name?:''}</td>
          <td>${record?.description?:''}</td>
          <td>${User.get(record.admin_id?:0)?.name?:''}</td>
        </tr>
      </g:each>
      <g:if test="${!taskevent}">
        <tr>
          <td colspan="9" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Истории не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>