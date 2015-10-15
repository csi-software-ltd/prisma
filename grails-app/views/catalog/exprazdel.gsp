<html>
  <head>
    <title>Prisma: Справочники - <g:if test="${expensetype}">Раздел дохода/расхода "${expensetype.name}"</g:if><g:else>Новый раздел дохода/расхода</g:else></title>
    <meta name="layout" content="main" />
    <g:javascript>
      function init(){  
        <g:if test="${expensetype}">
          getExpensetypes2();
        </g:if>        
      }
      function getExpensetypes2(){
        if(${expensetype?1:0}){ 
          var iId= ${expensetype?.id?:0};        
          <g:remoteFunction controller='catalog' action='expensetypes2' update='details' params="'id='+iId"/>;
        }  
      }
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
          $("infolist").up('div').hide();
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${expensetype?1:0}){
          location.reload(true);
        } else if(e.responseJSON.exp_id){
          location.assign('${createLink(controller:controllerName,action:'exprazdel')}'+'/'+e.responseJSON.exp_id);
        } else
          returnToList();
      }         
      function process2Response(e){
        var sErrorMsg = '';
        ['subname','expensetype1_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Название"])}</li>'; $('subname').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Раздел"])}</li>'; $('expensetype1_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("error2list").innerHTML=sErrorMsg;
          $("error2list").up('div').show();
        } else
          jQuery('#updateForm').slideUp(300, function(){getExpensetypes2();});
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px}
    </style>
  </head>
  <body onload="init()">
    <h3 class="fleft"><g:if test="${expensetype}">Раздел дохода/расхода "${expensetype.name}"</g:if><g:else>Новый раздел дохода/расхода</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку разделов</a>
    <div class="clear"></div>

    <div class="info-box" style="display:none;margin-top:0">
      <span class="icon icon-info-sign icon-3x"></span>
      <ul id="infolist">      
        <li>Изменения сохранены</li>
      </ul>
    </div>    
    <div class="error-box" style="display:none">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>

    <g:formRemote name="exptypeDetailForm" url="${[action:'updateexprazdel',id:expensetype?.id?:0]}" method="post" onSuccess="processResponse(e)">      
      <label for="tname">Название:</label>
      <input type="text" class="fullline" id="tname" name="name" value="${expensetype?.name}" maxlength="50"/>
      <hr class="admin" />
      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
        <input class="spacing" type="submit" id="submit_button" value="Сохранить" />
      </g:if>
      </div>
    </g:formRemote>
    <div class="clear"></div>
  <g:if test="${expensetype}">
    <div class="tabs">
      <ul class="nav">
        <li class="selected"><a href="javascript:void(0)">Подразделы</a></li>        
      </ul>
      <div class="tab-content">
        <div class="inner">
          <div id="details"></div>
        </div>
      </div>
    </div>    
  </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'index',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
