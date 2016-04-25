<html>
  <head>
    <title>Prisma: Обратная связь - Новый вопрос</title>
    <meta name="layout" content="main" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function updatetext(sType){
        var sDescText = '';
        switch (sType) {
        <g:each in="${ftypes}">
          case '${it.id.toString()}': sDescText = '${it.description}'; break;
        </g:each>
        }
        $("infolist").innerHTML=sDescText;
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
    </style>
  </head>
  <body>
    <h3 class="fleft">Новый вопрос</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку вопросов</a>
    <div class="clear"></div>
    <g:form name="newquestionForm" url="${[action:'incertquestion']}" method="post" enctype="multipart/form-data" target="upload_target">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="feedbacktype_id">Тип вопроса:</label>
      <g:select name="feedbacktype_id" value="" from="${ftypes}" optionValue="name" optionKey="id" onchange="updatetext(this.value)"/><br/>

      <div class="info-box" style="margin-top:0">
        <span class="icon icon-info-sign icon-3x"></span>
        <ul id="infolist">
          ${ftypes[0].description}
        </ul>
      </div>

      <label for="qtext">Текст вопроса:</label>
      <g:textArea id="qtext" name="qtext" rows="6" value="" />
      <label for="file">Загрузить файл:</label>
      <input type="file" id="file" name="file" style="width:256px"/>

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
        <input type="submit" class="spacing" value="Задать"/>
      </div>
    </g:form>
    <iframe id="upload_target" name="upload_target" style="display:none"></iframe>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>