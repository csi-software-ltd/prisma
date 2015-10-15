<g:form id="issuancescanAddForm" name="issuancescanAddForm" url="[action:'addissuancescan']" method="post" enctype="multipart/form-data" target="upload_target" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorissuancescanlist">
      <li></li>
    </ul>
  </div>
  <div id="issuancescan">
    <label for="file">Скан:</label>
    <input type="file" id="file" name="file" style="width:256px"/>
    <div class="fright">
      <input type="submit" id="submit_button" class="button" value="Сохранить" />
      <input type="reset" class="button" value="Отмена" onclick="jQuery('#issuancescanAddForm').slideUp();"/>
    </div>
    <input type="hidden" name="zakaz_id" value="${zakaz.id}"/>
  </div>
</g:form>
<iframe id="upload_target" name="upload_target" style="display:none"></iframe>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <tbody>
      <g:if test="${zakaz.file_id}">
        <tr align="center">
          <td class="btns" style="text-align:center">
            <a class="button" href="${createLink(controller:'cash',action:'showscan',id:zakaz.file_id,params:[code:Tools.generateModeParam(zakaz.file_id)])}" target="_blank">Просмотреть скан расписки получателя</a>
          </td>
          <td width="30"><g:remoteLink class="button" url="${[controller:'cash',action:'removeissuancescan',id:zakaz.file_id,params:[zakaz_id:zakaz.id]]}" title="Удалить" onSuccess="getIssuance()"><i class="icon-trash"></i></g:remoteLink></td>
        </tr>
      </g:if><g:else>
        <tr>
          <td class="btns" style="text-align:center">
            <a class="button" id="addissuancescanbutton" href="javascript:void(0)" onclick="jQuery('#issuancescanAddForm').slideDown();">Добавить скан расписки получателя</a>
          </td>
        </tr>
      </g:else>
      </tbody>
    </table>
  </div>
</div>