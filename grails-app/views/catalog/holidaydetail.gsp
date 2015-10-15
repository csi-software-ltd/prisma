<div class="tab-content">    
  <div class="inner">
    <h2>${holiday?.id?'Редактировать':'Добавить'} исключение из общего календаря выходных</h2><br/>    
    <g:formRemote url="${[controller:'catalog',action:'saveHolidayDetail',id:holiday?.id?:0]}" onSuccess="processHolidayResponse(e)" method="POST" name="createHolidayForm">           
      <label for="hdate">Дата:</label>
      <g:datepicker class="normal nopad" style="width:200px" name="hdate" value="${holiday?.hdate?String.format('%td.%<tm.%<tY',holiday?.hdate):''}"/>
      <label for="status">Статус:</label>
      <g:select name="status" value="${(holiday?.status!=0)?1:0}" keys="${1..0}" from="${['выходной','рабочий']}"/>          
      <div class="fright">                   
        <input type="submit" class="button" value="${holiday?.id?'Редактировать':'Добавить'}"/>
        <input type="button" class="spacing" value="Отмена" onclick="hideHolidayWindow();"/>
      </div>                   
    </g:formRemote>
  </div>
</div>
