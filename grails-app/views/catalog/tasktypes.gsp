﻿<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th width="50">Код</th>
          <th>Название</th>
          <th width="30"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult}" var="record">
        <tr align="center">
          <td>${record.id}</td>
          <td align="left">${record.name}</td>
          <td>
            <a class="button" href="${createLink(controller:controllerName,action:'tasktype',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>