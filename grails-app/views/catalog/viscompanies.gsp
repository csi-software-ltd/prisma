<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="936" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Компания</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${companies}" status="i" var="record">
        <tr align="center">
          <td><g:link style="z-index:1" controller="company" action="detail" id="${record.id}">${record.name}</g:link></td>
        </tr>
      </g:each>
      <g:if test="${!companies}">
        <tr>
          <td colspan="1" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Компаний не найдено</a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>