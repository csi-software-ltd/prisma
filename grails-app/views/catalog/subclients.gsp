<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Название</th>
          <th width="80px">Дата заведения</th>
          <th width="50px">Статус</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${subclients}" status="i" var="record">
        <tr align="center">
          <td>${record.name}</td>
          <td>${String.format('%td.%<tm.%<tY',record.inputdate)}</td>
          <td><i class="icon-${record.modstatus?'ok':'minus'}" title="${record.modstatus?'активный':'неактивный'}"></i></td>
        </tr>
      </g:each>
      <g:if test="${!subclients}">
        <tr>
          <td colspan="3" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Подклиентов не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>