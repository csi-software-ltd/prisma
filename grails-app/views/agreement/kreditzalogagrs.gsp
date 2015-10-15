<g:formRemote id="kreditzalogagrAddForm" name="kreditzalogagrAddForm" url="[action:'addkreditzalogagr']" method="post" onSuccess="processAddkreditzalogagrResponse(e)" style="display:none">
  <div class="error-box" style="width:730px;margin-top:0;display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    <ul id="errorkreditzalogagrlist">
      <li></li>
    </ul>
  </div>
  <div id="kreditzalogagr"></div>
</g:formRemote>
<div id="ajax_wrap">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th rowspan="2">Вид обеспечения</th>
          <th rowspan="2">Залогодатель</th>
          <th rowspan="2">Номер<br/>Дата<br/>Срок</th>
          <th colspan="2">Стоимость</th>
          <th colspan="2">Страхование</th>
          <th rowspan="2">Наличие<br/>договора</th>
          <th rowspan="2">Основной<br/>договор</th>
          <th rowspan="2" width="50"></th>
        </tr>
        <tr>
          <th>Залоговая</th>
          <th>Рыночная</th>
          <th>Номер<br/>Сумма</th>
          <th>Дата<br/>Срок</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${kreditzalogagrs}" var="record">
        <tr align="center">
          <td>${zalogtypes[record.zalogtype_id]}</td>
          <td>${record.pledger}</td>
          <td><g:if test="${record.zalogagr}">${record.zalogagr}<br/>${record.zalogstart?String.format('%td.%<tm.%<tY',record.zalogstart):'нет'}<br/>${record.zalogend?String.format('%td.%<tm.%<tY',record.zalogend):'нет'}</g:if><g:else>Нет</g:else></td>
          <td>${number(value:record.zalogcost)}</td>
          <td>${number(value:record.marketcost)}</td>
          <td>${record.strakhnumber}<br/>${intnumber(value:record.strakhsumma)}</td>
          <td>${record.strakhdate?String.format('%td.%<tm.%<tY',record.strakhdate):'нет'}<br/>${record.strakhvalidity?String.format('%td.%<tm.%<tY',record.strakhvalidity):'нет'}</td>
          <td>
          <g:if test="${record.is_zalogagr}"><abbr title="Да"><i class="icon-ok"></i></abbr></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
          <g:if test="${record.parent}"><a class="button" style="z-index:1" href="javascript:void(0)" title="Основной договор" onclick="$('kreditzalogagr_id').value=${record.parent};$('kreditzalogagr_submit_button').click();"><i class="icon-link"></i></a></g:if>
          <g:else><abbr title="Нет"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td>
          <g:if test="${iscanedit&&!Kreditzalog.findByKredit_idAndParent(kredit.id,record.id)}">
            <g:remoteLink class="button" style="z-index:1" url="${[controller:controllerName,action:'deletekreditzalogagr',id:record.id,params:[kredit_id:kredit.id]]}" title="Удалить" onSuccess="getZalogAgrs()"><i class="icon-trash"></i></g:remoteLink>&nbsp;&nbsp;
          </g:if>
            <a class="button" style="z-index:1" href="javascript:void(0)" title="Редактировать" onclick="$('kreditzalogagr_id').value=${record.id};$('kreditzalogagr_submit_button').click();"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      <g:if test="${!kreditzalogagrs}">
        <tr>
          <td colspan="11" class="btns" style="text-align:center">
            <a class="button" href="javascript:void(0)">Договоров обеспечения не найдено</a>
          </td>
        </tr>
      </g:if>
      <g:if test="${iscanedit}">
        <tr>
          <td colspan="11" class="btns" style="text-align:center">
            <a class="button" id="addplanpaymentbutton" href="javascript:void(0)" onclick="$('kreditzalogagr_id').value=0;$('kreditzalogagr_submit_button').click();">
              Добавить договор &nbsp;<i class="icon-angle-right icon-large"></i>
            </a>
          </td>
        </tr>
      </g:if>
      </tbody>
    </table>
  </div>
</div>
<g:formRemote name="kreditzalogagrForm" url="[action:'kreditzalogagr', params:[kredit_id:kredit.id]]" update="kreditzalogagr" onComplete="\$('errorkreditzalogagrlist').up('div').hide();jQuery('#kreditzalogagrAddForm').slideDown();" style="display:none">
  <input type="hidden" id="kreditzalogagr_id" name="id" value="0"/>
  <input type="submit" class="button" id="kreditzalogagr_submit_button" value="Показать"/>
</g:formRemote>