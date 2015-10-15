<html>
  <head>
    <title>Prisma: <g:if test="${exptype}">Статья расхода/дохода "${exptype.name}"</g:if><g:else>Новая статья расхода/дахода</g:else></title>
    <meta name="layout" content="main" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['tname','expensetype1_id','expensetype2_id'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Название"])}</li>'; $('tname').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Раздел"])}</li>'; $('expensetype1_id').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Подраздел"])}</li>'; $('expensetype2_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(${exptype?1:0}){
          location.reload(true);
        } else if(e.responseJSON.exptype){
          location.assign('${createLink(controller:controllerName,action:'expensetype')}'+'/'+e.responseJSON.exptype);
        } else
          returnToList();
      }
      function getExpensetypes2(iId){ 
        <g:remoteFunction controller='catalog' action='expensetype2list' update='expensetype2_id' params="'id='+iId"/>;
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px}
    </style>
  </head>
  <body>
    <h3 class="fleft"><g:if test="${exptype}">Статья дахода/расхода "${exptype.name}"</g:if><g:else>Новая статья дохода/расхода</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку статей</a>
    <div class="clear"></div>
    <g:formRemote name="exptypeDetailForm" url="${[action:'updateexpensetype',id:exptype?.id?:0]}" method="post" onSuccess="processResponse(e)">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
        </ul>
      </div>

      <label for="tname">Название:</label>
      <input type="text" id="tname" name="name" value="${exptype?.name}" maxlength="50" style="width:450px"/>
      <label for="is_car" class="auto">
        <input type="checkbox" id="is_car" name="is_car" value="1" <g:if test="${exptype?.is_car}">checked</g:if> />
        Связь с автомашиной
      </label><br/>
      <label for="expensetype1_id">Раздел:</label>
      <g:select name="expensetype1_id" value="${exptype?.expensetype1_id?:0}" from="${exp1}" optionKey="id" optionValue="name" noSelection="${['0':'не указан']}" onchange="getExpensetypes2(this.value)"/>      
      <label for="expensetype2_id">Подраздел:</label>
      <g:select name="expensetype2_id" value="${exptype?.expensetype2_id?:0}" from="${exp2}" optionKey="id" optionValue="name" noSelection="${['0':'не указан']}"/>
      <label for="modstatus">Статус</label>
      <g:select name="modstatus" value="${exptype?.modstatus}" from="['Активные','Архив']" keys="10"/>

      <hr class="admin" />

      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${iscanedit}">
        <input class="spacing" type="submit" id="submit_button" value="Сохранить" />
      </g:if>
      </div>
    </g:formRemote>
    <div class="clear"></div>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:controllerName,action:'expensetypes',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
