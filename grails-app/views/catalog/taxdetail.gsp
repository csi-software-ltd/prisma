<html>
  <head>
    <title>Prisma: <g:if test="${tax}">Редактирование налоговой инспеции № ${tax.id}</g:if><g:else>Добавление новой налоговой инспеции</g:else></title>
    <meta name="layout" content="main" />                                                   
    <g:javascript>                  
      function init(){  
        <g:if test="${flash?.taxedit_success}">
          $("infolist").up('div').show();
        </g:if>        
      }
      function returnToList(){
        $("returnToListForm").submit();
      }            
      function processResponse(e){        
        var sErrorMsg = '';
          ['tax_id','name','address'].forEach(function(ids){
            $(ids).removeClassName('red');
          });
        if(e.responseJSON.error){
          if(e.responseJSON.errorcode.length){          
            e.responseJSON.errorcode.forEach(function(err){
              switch (err) {                                                  
                case 1: sErrorMsg+='<li>Не заполнено обязательное поле "Номер"</li>'; $("tax_id").addClassName('red'); break;
                case 2: sErrorMsg+='<li>Некорректные данные в поле  "Номер". Номер из 4 цифр</li>'; $("tax_id").addClassName('red'); break;
                case 3: sErrorMsg+='<li>Такой "Номер" уже существует</li>'; $("tax_id").addClassName('red'); break;
                case 4: sErrorMsg+='<li>Не заполнено обязательное поле "Название"</li>'; $("name").addClassName('red'); break;
                case 5: sErrorMsg+='<li>Не заполнено обязательное поле "Адрес"</li>'; $("address").addClassName('red'); break;                             }
            });
          }
          $("infolist").up('div').hide();
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();   
          $('print_login').value=0;          
        } else {                                    
          location.assign('${createLink(controller:'catalog',action:'taxdetail')}'+'/'+e.responseJSON.tax_id);                                                                                    
        }        
      }                                 
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      label.long{width:370px}
      input.normal{width:202px}      
      input.mini{width:60px!important}
    </style>
  </head>
  <body onload="init();">
    <h3 class="fleft"><g:if test="${tax}">Налоговая инспеция № ${tax.id}</g:if><g:else>Добавление новой налоговой инспеции</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку налоговых инспеций</a>
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
    <g:formRemote name="taxDetailForm" url="[action:'saveTaxDetail']" method="post" onSuccess="processResponse(e)">
      <label for="tax_id">Номер:</label>
      <input type="text" id="tax_id" name="id" value="${tax?.id}" <g:if test="${tax}">readonly</g:if>/><br/>    
      <label for="name">Название:</label>
      <input type="text" class="fullline" id="name" name="name" value="${tax?.name}" />             
      <label for="address">Адрес:</label>
      <input type="text" class="fullline" id="address" name="address" value="${tax?.address}" /> 
      <label for="district">Район:</label>
      <input type="text" id="district" name="district" value="${tax?.district?:''}" /><br/>             
      <label for="tel">Телефон:</label>
      <input type="text" class="fullline" id="tel" name="tel" value="${tax?.tel?:''}"/>
      <input type="hidden" name="is_edit" value="${tax?1:0}"/>      
      <hr class="admin">
      <div class="fright" id="btns">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
        <input type="submit" id="submit_button" value="Сохранить"/>        
      </div>     
    </g:formRemote>   
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:'catalog',action:'index',params:[fromDetails:1]]}">
    </g:form>    
  </body>
</html>
