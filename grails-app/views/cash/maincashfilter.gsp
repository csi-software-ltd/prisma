<g:if test="${session.user.cashaccess==3&&requests.records}">
<div style="margin-left:15px">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>На дату</th>
          <th>Кому</th>
          <th>Назначение</th>
          <th>Сумма</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${requests.records}" status="i" var="record">
        <tr align="center">
          <td>${String.format('%td.%<tm.%<tY',record.todate)}</td>
          <td>${record.department_id?departments[record.department_id]:record.initiator_name}</td>
          <td>${record.purpose}</td>
          <td>${intnumber(value:record.summa)}</td>
          <td width="50">
            <g:if test="${record.modstatus==2}"><g:remoteLink class="button" url="${[controller:'cash',action:'allocatezakaz',id:record.id]}" title="Выделить" onSuccess="\$('cashlink4').click()"><i class="icon-download"></i></g:remoteLink>&nbsp;&nbsp;&nbsp;
            </g:if><g:else><g:remoteLink class="button" url="${[controller:'cash',action:'completezakaz',id:record.id]}" title="Выдать" onSuccess="\$('cashlink4').click()"><i class="icon-ok"></i></g:remoteLink>&nbsp;&nbsp;</g:else>
            <a class="button" href="javascript:void(0)" title="Повторно согласовать" onclick="repeatzakaz(${record.id})"><i class="icon-repeat"></i></a>            
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</div>
<div class="clear" style="padding-bottom:20px"></div>
</g:if>
<g:formRemote name="allForm" url="[controller:'cash',action:'list']" update="list">
  <label class="auto" for="mcid">Код</label>
  <input type="text" class="mini" id="mcid" name="mcid" value="${inrequest?.mcid}" />
  <label class="auto" for="department_id">Отдел:</label>
  <g:select name="department_id" value="${inrequest?.department_id}" from="${Department.list(sort:'name',order:'asc')}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/>
  <label class="auto" for="maincashtype">Тип:</label>
  <g:select name="maincashtype" value="${inrequest?.maincashtype}" from="${['Выдача','Получение','Возврат','Финансирование']}" keys="1234" noSelection="${['-100':'все']}" onchange="getcashclasses(this.value)"/>
  <br/><label class="auto" for="exprazdel_id">Раздел:</label>
  <g:select class="mini" name="exprazdel_id" value="${inrequest?.exprazdel_id}" from="${exprazdel}" optionValue="name" optionKey="id" noSelection="${['0':'все']}" onchange="getpodrazdel(this.value)"/>
  <label class="auto" for="exppodrazdel_id">Подраздел:</label>
  <span id="exppodrazdelsection"><g:select class="mini" name="exppodrazdel_id" value="${inrequest?.exppodrazdel_id}" from="${exppodrazdel}" optionValue="name" optionKey="id" noSelection="${['0':'все']}" onchange="getexptypes(this.value)"/></span>
  <label class="auto" for="expensetype_id">Статьи расходов:</label>
  <span id="exptypesection"><g:select name="expensetype_id" value="${inrequest?.expensetype_id}" from="${expensetypes}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/></span>
  <span id="cashclasssection"><label for="maincashclass">Класс:</label>
  <g:select name="maincashclass" value="${inrequest?.maincashclass}" from="${cashclasses}" optionKey="id" optionValue="name" noSelection="${['0':'все']}"/></span>
  <label class="auto" for="opdatestart">Дата операции с:</label>
  <g:datepicker class="normal nopad" name="opdatestart" value="${inrequest?.opdatestart?String.format('%td.%<tm.%<tY',inrequest?.opdatestart):''}"/>
  <label class="auto" for="opdateend">по:</label>
  <g:datepicker class="normal nopad" name="opdateend" value="${inrequest?.opdateend?String.format('%td.%<tm.%<tY',inrequest?.opdateend):''}"/>
  <br/><label class="auto" for="comment">Комментарий</label>
  <input type="text" id="comment" name="comment" value="${inrequest?.comment}" />
  <input type="hidden" id="is_maincash" name="is_maincash" value="1" />
  <div class="fright">
    <input type="button" class="reset spacing" value="Сброс" onclick="resetmaincashfilter()"/>
    <input type="submit" id="form_submit_button" value="Показать" />
  <g:if test="${session.user.cashaccess==3}">
    <g:link action="maincashrecord" class="button">Новая операция &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </g:if>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  jQuery("#todate").mask("99.99.9999",{placeholder:" "});
  jQuery("#opdatestart").mask("99.99.9999",{placeholder:" "});
  jQuery("#opdateend").mask("99.99.9999",{placeholder:" "});
  $('form_submit_button').click();
</script>
