<html>
  <head>
    <title>Prisma: Обратная связь - Вопрос администратору №${question.id}</title>
    <meta name="layout" content="main" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function deletequestion(){
        if(confirm('Вы уверены, что хотите удалить вопрос?')) {
          <g:remoteFunction controller='feedback' action='delete' id="${question.id}" onSuccess="returnToList()" />
        }
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      .textfield{
        background: #fff;
        border: 1px solid #A99F9A;
        border-radius: 8px;
        box-shadow: 0px 1px 1px rgba(0, 0, 0, 0.071) inset;
        padding: 6px;        
        width: 90.8%;
        min-height: 125px;
        margin: 15px;
        font: 18px Tahoma,​Arial,​sans-serif;
        cursor: not-allowed;
      }
    </style>
  </head>
  <body>
    <h3 class="fleft">Вопрос администратору №${question.id}</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку вопросов</a>
    <div class="clear"></div>
    <g:form name="questionForm" url="${[action:'updatequestion',id:question.id]}" method="post" enctype="multipart/form-data" target="upload_target">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="qdate" disabled>Дата вопроса:</label>
      <input type="text" name="qdate" disabled value="${String.format('%td.%<tm.%<tY %<tT',question.qdate)}" />
      <label for="adate" disabled>Дата модификации:</label>
      <input type="text" name="adate" disabled value="${question.adate?String.format('%td.%<tm.%<tY %<tT',question.adate):'нет'}" />
      <label for="feedbacktype_id" disabled>Тип вопроса:</label>
      <g:select name="feedbacktype_id" value="${question.feedbacktype_id}" from="${ftypes}" optionValue="name" optionKey="id" disabled="true"/>
      <label for="user_id" disabled>Автор:</label>
      <input type="text" name="user_id" disabled value="${author.shortname}" />
      <hr class="admin" />
    <g:if test="${isSuper}">
      <label for="atext">Текст ответа:</label>
      <g:textArea id="atext" rows="6" name="atext" value=""/>
    </g:if><g:else>
      <label for="qtext">Уточнить вопрос:</label>
      <g:textArea id="qtext" rows="6" name="qtext" value=""/>
    </g:else>
      <label for="file">Загрузить файл:</label>
      <input type="file" id="file" name="file" style="width:256px"/>

      <div class="clear"></div>
      <div class="fright" id="btns" style="padding-top:10px;margin-bottom:20px">
      <g:if test="${question.example_id}">
        <a class="button" href="${createLink(action:'showscan',id:question.example_id,params:[code:Tools.generateModeParam(question.example_id)])}" target="_blank">
          Файл пример&nbsp;<i class="icon-angle-right icon-large"></i>
        </a>
      </g:if>
      <g:if test="${question.file_id}">
        <a class="button" href="${createLink(action:'showscan',id:question.file_id,params:[code:Tools.generateModeParam(question.file_id)])}" target="_blank">
          Файл ответ&nbsp;<i class="icon-angle-right icon-large"></i>
        </a>
      </g:if>
      <g:if test="${question.modstatus==1&&isSuper}">
        <g:remoteLink class="button" url="${[action:'movetofaq',id:question.id,params:[status:2]]}" onSuccess="location.reload(true)">Сделать общим вопросом &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink>
      </g:if><g:if test="${question.modstatus==2&&isSuper}">
        <g:remoteLink class="button" url="${[action:'movetofaq',id:question.id,params:[status:1]]}" onSuccess="location.reload(true)">Убрать из общих вопросов &nbsp;<i class="icon-angle-right icon-large"></i></g:remoteLink>
      </g:if>
      <g:if test="${question.modstatus==0&&isSuper}">
        <input type="button" class="spacing" value="Удалить" onclick="deletequestion()"/>
      </g:if>
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
        <input type="submit" class="spacing" value="${isSuper?'Ответить':'Уточнить'}"/>
      </div>

      <div class="clear"></div>
      <hr class="admin" style="width:70px;float:left"/><a style="text-decoration:none" href="javascript:void(0)">&nbsp;&nbsp;Текст&nbsp;переписки&nbsp;</a><hr class="admin" style="width:730px;float:right"/>

      <div class="textfield"><g:rawHtml>${question.fullstory}</g:rawHtml></div>
    </g:form>
    <iframe id="upload_target" name="upload_target" style="display:none"></iframe>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>