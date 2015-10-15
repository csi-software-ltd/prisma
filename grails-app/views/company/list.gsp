<style type="text/css">
  .list tr td:nth-child(2) a{font-size:14px;}
  .list td.pad0{padding:0;}
  tr.yellow > td { background:lightyellow !important }
  tr.disabled > td { background:silver !important; opacity:0.7 !important }
</style>
<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${requests.count}</div>
    <div class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${requests.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${requests.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th width="62">Цвет</th>
          <th>Название</th>
          <th>ИНН<br/>КПП</th>
          <th>ОКВЭД</th>
        <g:if test="${inrequest.is_holding}">
          <th>Ген. директор</th>
        </g:if>
          <th>Р/с в банке</th>
        <g:if test="${inrequest.is_holding}">
          <th width="40">Банк- клиент</th>
        </g:if>
          <th width="30"></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${requests.records}" status="i" var="record">        
        <tr class="${!inrequest.is_holding?'':record.activitystatus_id>1&&record.modstatus?'yellow':!record.modstatus?'disabled':''}" align="center">
          <td>
            <a class="icon-flag${!record.colorfill?'-checkered':''} icon-large" id="color${record.id}" style="color:${record.color?:'transparent'}" onclick="jQuery('#colors${record.id}').slideToggle();"></a>
            <div id="colors${record.id}" align="left" style="display:none">
              <fieldset class="color">
                <legend>Цвет</legend>
              <g:each in="${colors}" var="it"> 
                <a class="icon-stop icon-large" style="color:${it.code}" onclick="setColor(${record.id},'${it.code}');jQuery('#colors${record.id}').slideUp();"></a>
              </g:each>
                <a class="icon-check-empty icon-border icon-large" style="color:transparent;" onclick="setColor(${record.id},'transparent');jQuery('#colors${record.id}').slideUp();"></a>
              </fieldset>
              <fieldset class="color center">
                <legend>Заполнение:</legend>
                <a class="icon-dark icon-flag icon-large" onclick="setColorFill(${record.id},1);jQuery('#colors${record.id}').slideUp();"></a>
                <a class="icon-dark icon-flag-checkered icon-large" onclick="setColorFill(${record.id},0);jQuery('#colors${record.id}').slideUp();"></a>
              </fieldset>
            </div>
          </td>
          <td><a href="${createLink(controller:'company',action:'report',id:record.id)}" title="Карточка клиента">${record.name}</a></td>
          <td>${record.inn}<br/>${record.kpp}</td>
          <td>${record.okvedmain}</td>
        <g:if test="${inrequest.is_holding}">
          <td style="${record.is_dirchange?'color:red':''}">${record.gd}</td>
        </g:if>
        <g:if test="${inrequest.is_holding}">
          <td colspan="2" class="pad0">
            <table cellpadding="0" cellspacing="0" style="width:390px">
            <g:each in="${accounts[record.id]}">
              <tr class="${record.activitystatus_id>1&&record.modstatus?'yellow':!record.modstatus?'disabled':''}" style="${accounts[record.id].size()==1?'height:56px;':''}${!it.is_license?'color:red':''}">
                <td>${it.bankname}</td>
                <td width="40" align="center">
                  <g:if test="${it.ibankstatus==1}"><abbr title="активен"><i class="icon-ok"></i></abbr></g:if>
                  <g:elseif test="${it.ibankstatus==2}"><abbr title="просрочен"><i class="icon-ban-circle"></i></abbr></g:elseif>
                  <g:elseif test="${it.ibankstatus==-1}"><abbr title="заблокирован"><i class="icon-lock"></i></abbr></g:elseif>
                  <g:else><abbr title="нет"><i class="icon-minus"></i></abbr></g:else>
                </td>
              </tr>
            </g:each>
            </table>
          </td>
        </g:if><g:else>
          <td class="pad0">
            <table cellpadding="0" cellspacing="0" width="100%">
            <g:each in="${accounts[record.id]}">
              <tr>
                <td>${it.bankname}</td>
              </tr>
            </g:each>
            </table>
          </td>
        </g:else>
          <td>
            <a class="button" href="${createLink(controller:'company',action:'detail',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${requests.count}</span>
    <span class="fright">
      <g:paginate controller="${controllerName}" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${requests.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
