<html>
  <head>
    <title>Prisma - конфигурация приложения</title>
    <meta name="layout" content="main" />
  </head>
  <body>
    <div class="grid_12 alpha">
      <h3>Конфигурация приложения</h3>
      <g:formRemote id="configsForm" name="configsForm" url="[action:'updateconfig']" method="post">
        <table class="list">
          <thead>
            <th>Параметр</th>
            <th>Значение</th>
          </thead>
          <tbody>
          <g:each in="${configs}" var="record">
            <tr>
              <td>${record.comment}</td>
              <td>
              <g:if test="${record.id.toInteger() in [11,12,13,17,18,19,20,21,22,23,24,25,26,29]}">
                <g:select name="${record.name}" value="${record.value.toInteger()}" from="${expensetypes}" optionKey="id"/>
              </g:if><g:elseif test="${record.id.toInteger() in [27]}">
                <g:select name="${record.name}" value="${record.value.toInteger()}" from="${Department.list(sort:'name',order:'asc')}" optionKey="id" optionValue="name"/>
              </g:elseif><g:else>
                <input type="text" class="nopad" name="${record.name}" value="${record.value}" />
              </g:else>
              </td>
            </tr>
          </g:each>
          </tbody>
        </table>
        <div class="fright" style="padding-top:10px">
          <input type="submit" class="button" value="Сохранить"/>
        </div>
      </g:formRemote>
    </div>
    <div class="clear"></div>
  </body>
</html>
