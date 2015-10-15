<div id="ajax_wrap">
  <g:formRemote url="${[controller:'user',action:'updateuserexpensetypes',id:useredit.id]}" name="userexpentypesForm" onSuccess="getExpensetypes()">
  <g:each in="${expensetypeRazdel}" var="item" status="i">
  <g:if test="${i}"><hr class="admin"></g:if>
    <ul>
      <li style="margin-bottom:5px;">
        <label onclick="toggleUl(${i})">${item.razdel_name}&nbsp;<i id="${i}_label" class="icon-collapse${userexptypes.find{it.expensetype1_id==item.expensetype1_id}?'-top':''}"></i></label>
        <div id="${i}_div" style="width:916px;padding-left:20px;${!userexptypes.find{it.expensetype1_id==item.expensetype1_id}?'display:none':''}">
        <g:each in="${Expensetype2.findAllByExpensetype1_idAndIdInList(item.expensetype1_id,expensetypes.findAll{it.expensetype1_id==item.expensetype1_id}.collect{it.expensetype2_id}.unique(),[sort:'name',order:'asc'])}" var="item1">
          <label onclick="toggleUl2(${item1.id})">${item1.name}&nbsp;<i id="${item1.id}_label2" class="icon-collapse${userexptypes.find{it.expensetype2_id==item1.id}?'-top':''}"></i></label>
          <div id="${item1.id}_div2" style="width:896px;padding-left:20px;${!userexptypes.find{it.expensetype2_id==item1.id}?'display:none':''}">
          <g:each in="${expensetypes}" var="exptype">
          <g:if test="${exptype.expensetype1_id==item.expensetype1_id&&exptype.expensetype2_id==item1.id}">
            <label><input type="checkbox" name="exp_id_${exptype.id}" value="1" <g:if test="${userexptypes.find{it.id==exptype.id}}">checked</g:if>/>${exptype.name}</label><br/>
          </g:if>
          </g:each>
          </div><br/>
        </g:each>
        </div>
      </li>
    </ul>
    <div class="clear"></div>
  </g:each>
    <div class="fright">
      <input type="reset" class="spacing" value="Отмена"/>
      <input type="submit" class="button" value="Сохранить"/>
    </div>
    <div class="clear"></div>
  </g:formRemote>
</div>