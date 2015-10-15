<g:formRemote name="allForm" url="[controller:'cash',action:'list']" update="list">
  <label class="auto" for="todate">На дату:</label>
  <g:datepicker class="normal nopad" name="todate" value="${inrequest?.todate?String.format('%td.%<tm.%<tY',inrequest?.todate):''}"/>
  <label class="auto" for="modstatus">Статус:</label>
  <g:select class="mini" name="modstatus" value="${inrequest?.modstatus?:0}" from="${status}" optionValue="name" optionKey="id" noSelection="${['-100':'все']}"/>
  <label class="auto" style="${session.user.cashaccess<3||session.user.cashaccess>=6?'display:none':''}" for="username">Инициатор:</label>
  <input style="${session.user.cashaccess<3||session.user.cashaccess>=6?'display:none':''}" type="text" id="username" name="username" value="${inrequest?.username?:''}" />
  <div id="executor_autocomplete" class="autocomplete" style="display:none"></div>
  <label style="${session.user.cashaccess<3||session.user.cashaccess>=6?'display:none':''}" class="auto" for="department_id">Отдел:</label>
  <g:select style="${session.user.cashaccess<3||session.user.cashaccess>=6?'display:none':''}" name="department_id" value="${inrequest?.department_id?:session.user.cashaccess<3||session.user.cashaccess>=6?user.department_id:0}" from="${Department.list()}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Показать" />
    <g:link action="detail" class="button">Новая заявка &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
  </div>
  <div class="clear"></div>
</g:formRemote>
<script type="text/javascript">
  $('form_submit_button').click();
  new Autocomplete('username', {
    serviceUrl:'${resource(dir:"autocomplete",file:"executor_autocomplete")}'
  });
</script>
