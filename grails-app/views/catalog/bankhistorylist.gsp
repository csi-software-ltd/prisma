<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${searchresult.records.size()}</div>    
    <div class="clear"></div>
  </div>
<g:if test="${searchresult.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>          
          <th>Дата изменений</th>
          <th>Название</th>
	        <th>Корсчет</th>
	        <th>Телефон</th>
	        <th>Лицензия</th>
	        <th>Дата отзыва</th>
          <th>Автор</th>          
        </tr>
      </thead>
      <tbody>
      <g:each in="${searchresult.records}" status="i" var="record">
        <tr align="left">         
	        <td>${String.format('%td.%<tm.%<tY %<tT',record.moddate)}</td>
          <td>${record.name}</td>
          <td>${record.coraccount}</td> 
	        <td>${record.tel}</td>
	        <td><g:if test="${record?.is_license!=null}"><i class="icon-${record.is_license?'ok':'minus'}" title="${record.is_license?'есть':'нет'}"></i></g:if></td>
	        <td>${record.stopdate?String.format('%td.%<tm.%<tY',record.stopdate):''}</td>
	        <td>${User.get(record.admin_id?:0l)?.name?:''}</td>                
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${searchresult.records.size()}</span>   
  </div>
</g:if>
</div>