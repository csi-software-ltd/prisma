<div id="ajax_wrap">
  <div style="padding:5px 10px">&nbsp;</div>
  <div id="resultList">
    <table class="list" width="60%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Запросов подано</th>
          <th>Должно быть получено</th>
          <th>Полученные</th>
          <th>Отказы</th>
        </tr>
      </thead>
      <tbody>
        <tr align="center">
          <td>${newenquiries}</td>
          <td>${expectedenquiries}</td>
          <td>${receivedenquiries}</td>
          <td width="100">${denyenquiries}</td>
        </tr>
      </tbody>
    </table>
    <div style="padding:10px">
      <h2>Получено справок по типам</h2>
    </div> 
    <table class="list" width="60%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Тип</th>
          <th>Кол-во</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${enqtypes}">
        <tr>          
          <td>${it.name}</td>
          <td align="center">${it.count}</td>          
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>
