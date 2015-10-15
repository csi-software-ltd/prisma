<div class="tab-content">    
  <div class="inner">
    <h2>${form?.id?'Редактировать':'Добавить'} форму собственности</h2><br/>    
    <g:formRemote url="${[controller:'catalog',action:'createform',id:form?.id?:0]}" onSuccess="processFormResponse(e)" method="POST" name="createForm">           
      <label for="name" style="width:150px">Название:</label>
      <input type="text" name="name" id="name" style="width:400px" value="${form?.name?:''}"/>                           
      <div class="fright">                   
        <input type="submit" class="button" value="${form?.id?'Редактировать':'Добавить'}"/>
        <input type="button" class="spacing" value="Отмена" onclick="hideFormWindow();"/>
      </div>             
      <label for="description">Полное название:</label>
      <input type="text" name="fullname" id="fullname" style="width:650px"  value="${form?.fullname?:''}"/>
    </g:formRemote>
  </div>
</div>
