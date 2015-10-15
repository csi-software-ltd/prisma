<g:form name="allForm" url="[controller:controllerName,action:'bankdir']" target="_blank">
  <label class="auto" for="cgroup_id">Группа:</label>
  <g:select name="cgroup_id" from="${Cgroup.list(sort:'name',order:'asc')}" optionKey="id" optionValue="name" noSelection="${['0':'все']}"/>
  <label class="auto" for="bd_bankname">Банк:</label>
  <input type="text" style="width:495px" id="bd_bankname" name="bankname" value=""/>
  <label class="auto" for="bd_dirname">Директор:</label>
  <input type="text" style="width:405px" id="bd_dirname" name="dirname" value=""/>
  <label for="typeaccount_id">Тип счета:</label>
  <g:select id="typeaccount_id" name="typeaccount_id" from="['расчетный', 'корпоративный', 'текущий', 'транзитный','накопительный','планируемый','отказ в открытии']" keys="1234567" noSelection="${['0':'все']}"/>
  <br/><label class="auto" for="activitystatus_id">Статус:</label>
  <g:select name="activitystatus_id" from="${Activitystatus.list()}" optionKey="id" optionValue="name" noSelection="${['0':'все']}"/>
  <label class="auto" for="is_noclosed">
    <input type="checkbox" id="is_noclosed" name="is_noclosed" value="1" checked/>
    Без закрытых счетов
  </label>
  <label class="auto" for="is_active">
    <input type="checkbox" id="is_active" name="is_active" value="1" checked/>
    По активным банкам
  </label>
  <div class="fright">
    <input type="reset" class="spacing" value="Сброс"/>
    <input type="submit" id="form_submit_button" value="Сформировать" />
  </div>
  <div class="clear"></div>
</g:form>
<script type="text/javascript">
  $('list').innerHTML='';
  new Autocomplete('bd_bankname', {
    serviceUrl:'${resource(dir:"autocomplete",file:"banknameholding_autocomplete")}'
  });
  new Autocomplete('bd_dirname', {
    serviceUrl:'${resource(dir:"autocomplete",file:"bankdirpers_autocomplete")}'
  });
</script>