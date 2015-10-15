<g:if test="${group}">
  <div id="ajax_wrap">  
    <g:formRemote url="${[controller:'user',action:'groupRightssave',id:group?.id?:0]}" name="groupForm" onSuccess="proccesGroupRights(e)">
      <table>                 
      <g:each in="${formaccess}" var="item" status="i">      
        <tr>
          <td> 
            <g:if test="${i}"><hr class="admin"></g:if>
            <ul>        
              <li style="margin-bottom:5px;">                                      
                <label><input type="checkbox" name="${item?.fid}" value="1" <g:if test="${group[item?.fid]}">checked</g:if> onclick="${Formaccess.findByParent(item?.id)?'toggleUl(\''+item?.fid+'\')':''}"/><g:rawHtml>${item?.fname}</g:rawHtml></label>          
                <ul id="${item?.fid}_ul" style="display:${group[item?.fid]?'block':'none'};">        
                <g:each in="${Formaccess.findAllByParent(item?.id,[sort:'id',order:'asc'])}" var="item1">
                  <li style="margin-bottom:5px;margin-left:20px;width:100%">                                       
                    <label><input type="checkbox" name="${item1?.fid}" value="1" <g:if test="${group[item1?.fid]}">checked</g:if> onclick="${Formaccess.findByParent(item1?.id)?'toggleUl(\''+item1?.fid+'\')':''}"/><g:rawHtml>${item1?.fname}</g:rawHtml></label>
                    <ul id="${item1?.fid}_ul" style="display:${Formaccess.findByParent(item1?.id) && group[item1?.fid]?'block':'none'};">                            
                    <g:each in="${Formaccess.findAllByParent(item1?.id,[sort:'id',order:'asc'])}" var="item2">
                      <li style="margin-bottom:5px;margin-left:20px;width:100%">                                       
                        <label><input type="checkbox" name="${item2?.fid}" value="1" <g:if test="${group[item2?.fid]}">checked</g:if>/><g:rawHtml>${item2?.fname}</g:rawHtml></label>
                      </li><br/>
                    </g:each>
                    </ul>
                  </li><br/>                    
                </g:each>        
                </ul>
              </li>                     
            </ul>
          </td>
        </tr>
      </g:each>                                      
      </table>      
      <div class="fright">
        <input type="reset" class="spacing" value="Отмена"/>
        <g:if test="${user?.group?.is_usergroupedit}">
          <input type="submit" class="button" value="Сохранить"/>
        </g:if>  
      </div>
      <div class="clear"></div>
    </g:formRemote>
  </div>
</g:if>
