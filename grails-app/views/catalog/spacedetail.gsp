<div class="tab-content">    
  <div class="inner">
    <h2>${space?.id?'Редактировать':'Добавить'} тип помещений:</h2><br/>    
    <g:formRemote url="${[controller:'catalog',action:'saveSpacetypeDetail',id:space?.id?:0]}" onSuccess="processSpaceResponse(e)" method="POST" name="createForm">           
      <label for="name" style="width:150px">Название:</label>
      <input type="text" name="name" id="name" style="width:400px" value="${space?.name?:''}"/>                           
      <div class="fright">
        <input type="reset" class="spacing" value="Отмена" onclick="hideSpaceWindow();"/>                   
        <input type="submit" class="button" value="${space?.id?'Редактировать':'Добавить'}"/>        
      </div>                  
    </g:formRemote>
  </div>
</div>
