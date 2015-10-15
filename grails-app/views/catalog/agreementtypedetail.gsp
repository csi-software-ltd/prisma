<div class="tab-content">    
  <div class="inner">
    <h2>Редактировать тип договоров</h2><br/>    
    <g:formRemote url="${[controller:'catalog',action:'saveAgreementtypeDetail',id:agreementtype?.id?:0]}" onSuccess="processAgreementtypeResponse(e)" method="POST" name="allForm">           
      <label for="name">Название:</label>
      <input type="text" id="name" name="name" value="${agreementtype?.name?:''}" />
      <label for="status">Порядок сортировки:</label>
      <input type="text" id="sortorder" name="sortorder" value="${agreementtype?.sortorder?:0}" />   
      <div class="fright">                   
        <input type="submit" class="button" value="Редактировать"/>
        <input type="button" class="spacing" value="Отмена" onclick="hideAgreementtypeWindow();"/>
      </div>                   
    </g:formRemote>
  </div>
</div>
