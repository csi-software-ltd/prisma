<div class="tab-content">    
  <div class="inner">
    <h2>${composition?.id?'Редактировать':'Добавить'} должность:</h2><br/>   
    <g:formRemote url="${[controller:'catalog',action:'saveCompositionDetail',id:composition?.id?:0]}" onSuccess="processCompositionResponse(e)" method="post" name="createForm">           
      <label for="name">Название:</label>
      <g:textField class="fullline" name="name" value="${composition?.name?:''}"/><br />      
      <label for="position_id">Тип:</label>
      <g:select name="position_id" value="${composition?.position_id?:0}" from="${Position.list()}" optionKey="id" optionValue="name" noSelection="${['0':'не выбран']}"/>
      <div class="fright">                   
        <input type="reset" class="spacing" value="Отмена" onclick="hideCompositionWindow();" />
        <input type="submit" class="button" value="${composition?.id?'Редактировать':'Добавить'}" />        
      </div>                 
    </g:formRemote>     
  </div>
</div>
