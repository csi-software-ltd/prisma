<html>
  <head>
    <title>Prisma приложение</title>
    <meta name="layout" content="main" />
    <g:javascript>      
      function processResponse(e){  
        var sErrorMsg='';      
        if(e.responseJSON.error){                   
            switch (e.responseJSON.error_type) {                                  
              case 1: sErrorMsg+='<li>Введите sms код</li>'; break;
              case 2: sErrorMsg+='<li>Sms код неверен</li>'; break;
              case 3: sErrorMsg+='<li>Пользователь временно заблокирован</li>'; break;  
              case 4: sErrorMsg+='<li>Истекло время жизни SMS кода. Сгенерируйте новый код.</li>'; break;              
            }                   
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();                                                 
        } else {       
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').hide();         
          location.assign('${createLink(controller:'user',action:'panel')}');
        }        
      }  
      function sendMessage(){
        $("verifyButton").hide();
        $('loader').show();
        <g:remoteFunction controller='user' action='verifyUser' params="'user_id=${flash.user_id}'" onSuccess='processResponseNext(e)' />
      }
      function processResponseNext(e){
        if(e.responseJSON.error){
          $('loader').hide();
          $("verifyError").up('div').show();          
          $("verifyOk").up('div').hide();         
        }else{
          $('loader').hide();
          $("verifyError").up('div').hide();          
          $("verifyOk").up('div').show();          
        }
        $("verifyButton").show();
      }      
    </g:javascript>
  </head>  
  <body>   
    <div class="grid_3">&nbsp;</div>
    <div class="grid_6 padtop">
      <h3>Войти в панель управления</h3>

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
          <li></li>
        </ul>
      </div>      

      <g:formRemote name="verifySMSCodeForm" url="${[controller:'user',action:'verifySMSCode',id:flash.user_id?:0]}" method="post" onSuccess="processResponse(e)">
        <label for="smscode">SMS код:</label>
        <span class="input-prepend">
          <span class="add-on"><i class="icon-lock"></i></span>
          <input type="text" class="nopad normal" name="smscode" id="smscode" placeholder="код"/>
        </span><br/>        
        <input type="submit" class="fright" value="Войти" />                  
      </g:formRemote>
      <div class="clear"></div>      
      <div class="info-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="verifyOk">
          <li>SMS отправлено.</li>
        </ul>
      </div>
      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="verifyError">
          <li>Ошибка: SMS не отправлено.</li>
        </ul>
      </div>      
      <div id="loader" style="display: none">
        <img src="${resource(dir:'images',file:'spinner.gif')}" border="0"/>
      </div>
      <div class="padd20" id="verifyButton" style="clear:both;">      
        <div class="rounded">         
          <span class="action_button orange" style="margin-right:0">
            <a class="icon none" href="javascript:void(0)" onclick="sendMessage()">Сгенерировать новый код</a>
          </span>
        </div>
      </div>      
    </div>        
  </body>
</html>
