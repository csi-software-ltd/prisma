<html>
  <head>
    <title>Prisma: Справочники - <g:if test="${visgroup}">Группа видимости компаний "${visgroup.name}"</g:if><g:else>Новая группа видимости компаний</g:else></title>
    <meta name="layout" content="main" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['name'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Название"])}</li>'; $('name').addClassName('red'); break;              
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${visgroup?1:0}){
          location.reload(true);
        } else if(e.responseJSON.visgroup_id){
          location.assign('${createLink(controller:controllerName,action:'visualgroup')}'+'/'+e.responseJSON.visgroup_id);
        } else
          returnToList();
      }
      function viewCell(iNum){
        var tabs = jQuery('.nav').find('li');
        for(var i=0; i<tabs.length; i++){
          if(i==iNum)
            tabs[i].addClassName('selected');
          else
            tabs[i].removeClassName('selected');
        }

        switch(iNum){
          case 0: getCompanies();break;
        }
      }
      function getCompanies(){
        if(${visgroup?1:0}) $('viscompanies_submit_button').click();
      }
      function init(){
        jQuery('.nav').find('li:visible > a:first').click();
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px}
    </style>
  </head>
  <body onload="init()">
    <h3 class="fleft"><g:if test="${visgroup}">Группа видимости компаний "${visgroup.name}"</g:if><g:else>Новая группа видимости компаний</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку </a>
    <div class="clear"></div>

    <div class="error-box" style="display:none">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>

    <g:formRemote name="visgroupDetailForm" url="${[action:'updatevisualgroup',id:visgroup?.id?:0]}" method="post" onSuccess="processResponse(e)">
      <label for="name">Название:</label>
      <input type="text" class="fullline" id="name" name="name" value="${visgroup?.name}" maxlength="150"/>

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
        <input class="spacing" type="submit" id="submit_button" value="Сохранить" />
      </div>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${visgroup}">
    <div class="tabs">
      <ul class="nav">
        <li><a href="javascript:void(0)" onclick="viewCell(0)">Компании</a></li>
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>
    <g:formRemote name="viscompaniesForm" url="[action:'viscompanies',id:visgroup.id]" update="details">
      <input type="submit" class="button" id="viscompanies_submit_button" value="Показать" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>