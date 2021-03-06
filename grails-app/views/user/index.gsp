﻿<html>
  <head>
    <title>Prisma приложение</title>
    <meta name="layout" content="main" />    
  </head>  
  <body>  
    <div class="grid_3">&nbsp;</div>
    <div class="grid_6 padtop">
      <h3>Войти в панель управления</h3>
      <g:if test="${flash?.error}">
        <div class="error-box">
          <span class="icon icon-warning-sign icon-3x"></span>
          <ul>
            <g:if test="${flash.error==1}"><li>Не введен логин</li></g:if>
            <g:elseif test="${flash.error==2}"><li>Пароль введен неверно, или клиента с таким логином не существует</li></g:elseif>
            <g:elseif test="${flash.error==3}"><li>Доступ временно заблокирован</li></g:elseif> 
            <g:elseif test="${flash.error==4}"><li>Доступ для вашего ip заблокирован</li></g:elseif>
            <g:elseif test="${flash.error==5}"><li>Доступ заблокирован</li></g:elseif>            
          </ul>
        </div>
      </g:if>      
      <g:form url="[controller:'user',action:'login']" method="post" autocomplete='off'>
        <label for="login">Логин:</label>
        <span class="input-prepend <g:if test="${flash?.error==1}">red</g:if>">
          <span class="add-on"><i class="icon-lock"></i></span>
          <input type="text" class="nopad normal" name="login" id="login" placeholder="Логин"/>
        </span><br/>
        <label for="password">Пароль:</label>
        <span class="input-prepend <g:if test="${flash?.error==2}">red</g:if>">
          <span class="add-on"><i class="icon-key"></i></span>
          <input type="password" class="nopad normal" name="password" placeholder="Пароль"/>
        </span><br/>
        <input type="submit" class="fright" value="Войти" />                  
      </g:form>    
    </div>        
  </body>
</html>
