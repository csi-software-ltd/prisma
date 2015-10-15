<html>
  <head>
    <title>Prisma: <g:if test="${bank}">Редактирование банка № ${bank.id}</g:if><g:else>Добавление нового банка</g:else></title>
    <meta name="layout" content="main" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="jquery.maskedinput.min" />    
    <g:javascript>                  
      function init(){  
        <g:if test="${flash?.bankedit_success}">
          $("infolist").up('div').show();
        </g:if>
        jQuery(function($){
          jQuery("#bank_id").mask("?*********"); 
          jQuery("#coraccount").mask("?99999999999999999999");
          jQuery("#stopdate").mask("99.99.9999",{placeholder:" "});           
        });
        
        viewCell(0);          
      }
      function returnToList(){
        $("returnToListForm").submit();
      }            
      function processResponse(e){        
        var sErrorMsg = '';
          ['bank_id','name','coraccount','stopdate',
          'rko_rate','open_rate','ibankopen_rate','ibankserv_rate','plat_rate','platreturn_rate',
          'race_rate','income_rate','urgent_rate','besp_rate','spravka_rate','addline_rate','vypiska_rate','ibankterm',
          'stopdate','is_license'].forEach(function(ids){
            $(ids).removeClassName('red');
          });
        if(e.responseJSON.error){
          if(e.responseJSON.errorcode.length){          
            e.responseJSON.errorcode.forEach(function(err){
              switch (err) {                                                  
                case 1: sErrorMsg+='<li>Не заполнено обязательное поле "БИК"</li>'; $("bank_id").addClassName('red'); break;
                case 111: sErrorMsg+='<li>Некорректные данные в поле  "БИК". БИК из 9 цифр</li>'; $("bank_id").addClassName('red'); break;
                case 112: sErrorMsg+='<li>Такой "БИК" уже существует</li>'; $("bank_id").addClassName('red'); break;
                case 1121: sErrorMsg+='<li>Неактивная "Лицензия" требует заполнения поля "Дата отзыва лиц."</li>'; $("stopdate").addClassName('red'); break;
                case 1122: sErrorMsg+='<li>Заполнение поля "Дата отзыва лиц." требует неактивного поля "Лицензия"</li>'; $("is_license").addClassName('red'); break;
                case 113: sErrorMsg+='<li>Некорректные данные в поле "Срок действия сертификата банк-клиента в днях"</li>'; $("ibankterm").addClassName('red'); break;
                case 2: sErrorMsg+='<li>Не заполнено обязательное поле "Название"</li>'; $("name").addClassName('red'); break;
                //case 3: sErrorMsg+='<li>Не заполнено обязательное поле "Коррсчет"</li>'; $("coraccount").addClassName('red'); break;
                case 4: sErrorMsg+='<li>Некорректные данные в поле "Коррсчет".Коррсчет из 20 цифр</li>'; $("coraccount").addClassName('red'); break;                
                case 5: sErrorMsg+='<li>Некорректные данные в поле "Дата отзыва лиц."</li>'; $("stopdate").addClassName('red'); break;
                case 6: sErrorMsg+='<li>Некорректные данные в поле "Стоимость рко"</li>'; $("rko_rate").addClassName('red'); break;
                case 7: sErrorMsg+='<li>Некорректные данные в поле "Стоимость откр. счета"</li>'; $("open_rate").addClassName('red'); break;
                case 8: sErrorMsg+='<li>Некорректные данные в поле "Стоимость открытия банк-клиента"</li>'; $("ibankopen_rate").addClassName('red'); break;
                case 9: sErrorMsg+='<li>Некорректные данные в поле "Стоимость обслуживания банк-клиента"</li>'; $("ibankserv_rate").addClassName('red'); break;
                case 10: sErrorMsg+='<li>Некорректные данные в поле "Стоимость платежного поручения"</li>'; $("plat_rate").addClassName('red'); break;
                case 11: sErrorMsg+='<li>Некорректные данные в поле "Стоимость отзыва платежного поручения"</li>'; $("platreturn_rate").addClassName('red'); break;
                case 12: sErrorMsg+='<li>Некорректные данные в поле "Стоимость рейсов"</li>'; $("race_rate").addClassName('red'); break;
                case 13: sErrorMsg+='<li>Некорректные данные в поле "Стоимость работ под поступления тек. дня"</li>'; $("income_rate").addClassName('red'); break;
                case 14: sErrorMsg+='<li>Некорректные данные в поле "Стоимость срочного исполнения"</li>'; $("urgent_rate").addClassName('red'); break;
                case 15: sErrorMsg+='<li>Некорректные данные в поле "Стоимость БЭСП"</li>'; $("besp_rate").addClassName('red'); break;
                case 16: sErrorMsg+='<li>Некорректные данные в поле "Стоимость выдачи справок"</li>'; $("spravka_rate").addClassName('red'); break;
                case 17: sErrorMsg+='<li>Некорректные данные в поле "Стоимость добавить строку тарифа"</li>'; $("addline_rate").addClassName('red'); break;
                case 18: sErrorMsg+='<li>Некорректные данные в поле "Стоимость выписки"</li>'; $("vypiska_rate").addClassName('red'); break;
              }
            });
          }
          $("infolist").up('div').hide();
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();   
          $('print_login').value=0;          
        } else {                                    
          location.assign('${createLink(controller:'catalog',action:'bankdetail')}'+'/'+e.responseJSON.bank_id);                                                                                    
        }        
      }
      function viewCell(iNum){             
        var tabs = jQuery('.nav').find('li');
        for(var i=0; i<tabs.length; i++){
          if(i==iNum)
            tabs[i].addClassName('selected');
          else
            tabs[i].removeClassName('selected');
        }
        
        <g:if test="${!user?.group?.is_bankedit}">
          iNum++;
        </g:if>

        switch(iNum){
          case 0: getBankCompany();break;
          case 1: getBankHistory();break;          
        }
      }      
      function getBankCompany(){
        if(${bank?1:0}) $('bankcompany_submit_button').click();
      }
      function getBankHistory(){
        if(${bank?1:0}) $('bankhistory_submit_button').click();
      }
      function clickPaginate(event){
        event.stop();
        var link = event.element();
        if(link.href == null){
          return;
        }
        new Ajax.Updater(
          { success: $('ajax_wrap') },
          link.href,
          { evalScripts: true });
      }
      function togglebankprice(){
        if(!jQuery("#bankprice").is(':hidden')){
          $("expandlink").innerHTML = '&nbsp;&nbsp;Развернуть&nbsp;<i class="icon-collapse"></i>';
          jQuery('#bankprice').slideUp();       
        } else {
          $("expandlink").innerHTML = '&nbsp;&nbsp;Скрыть&nbsp;<i class="icon-collapse-top"></i>';
          jQuery('#bankprice').slideDown();         
        }
      }  
      function togglestopdate(){
        if(!jQuery("#stop").is(':hidden')){          
          jQuery('#stop').slideUp();       
        } else {          
          jQuery('#stop').slideDown();         
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
    <h3 class="fleft"><g:if test="${bank}">Банк № ${bank.id}</g:if><g:else>Добавление нового банка</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку банков</a>
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
    <g:formRemote name="bankDetailForm" url="[action:'saveBankDetail']" method="post" onSuccess="processResponse(e)">
      <label for="bank_id" <g:if test="${bank}">disabled</g:if>>БИК:</label>
      <input type="text" id="bank_id" name="id" value="${bank?.id}" <g:if test="${bank}">readonly</g:if>/>
      <label for="coraccount">Коррсчет:</label>
      <input type="text" id="coraccount" name="coraccount" value="${bank?.coraccount?:''}"/> 
      <label for="name">Название:</label>
      <input type="text" class="fullline" id="name" name="name" value="${bank?.name}" />
      <label for="shortname">Кор. название:</label>
      <input type="text" id="shortname" name="shortname" value="${bank?.shortname}" />
      <label for="city" <g:if test="${bank}">disabled</g:if>>Город:</label>
      <input type="text" id="city" name="city" value="${bank?.city?:''}" <g:if test="${bank}">disabled</g:if> />
      <label for="address" <g:if test="${bank}">disabled</g:if>>Адрес:</label>
      <input type="text" class="fullline" id="address" name="address" value="${bank?.address?:''}" <g:if test="${bank}">disabled</g:if>/>
      <label for="is_foreign">
        <input type="checkbox" id="is_foreign" name="is_foreign" value="1" <g:if test="${bank?.is_foreign}">checked</g:if>/>
        Зарубежный
      </label>
      <label for="is_license">
        <input type="checkbox" id="is_license" name="is_license" value="1" onclick="togglestopdate()" <g:if test="${bank?.is_license || !bank}">checked</g:if>/>
        Лицензия
      </label>
      <span id="stop" style="display:${(bank?.is_license || !bank)?'none':'inline-block'}">
        <label for="stopdate" class="auto">Дата отзыва лиц.:</label>
        <g:datepicker class="normal nopad" name="stopdate" value="${bank?.stopdate?String.format('%td.%<tm.%<tY',bank?.stopdate):''}"/>
      </span>
      <label for="is_sanation">
        <input type="checkbox" id="is_sanation" name="is_sanation" value="1" <g:if test="${bank?.is_sanation}">checked</g:if>/>
        Санация
      </label>
      <br />
      <label for="ibankterm">Срок действия сертификата банк-клиента в днях:</label>
      <input class="mini" type="text" id="ibankterm" name="ibankterm" value="${bank?.ibankterm?:0}" /><br/>
      <label for="is_local" <g:if test="${bank}">disabled</g:if>>Региональный:</label>
      <g:select name="is_local" value="${bank?.is_local?1:0}" keys="${1..0}" from="${['да','нет']}" disabled="${bank?'true':'false'}"/><br/> 
      <label for="tel">Телефон:</label>
      <input type="text" class="fullline" id="tel" name="tel" value="${bank?.tel?:''}"/>                 
      <label for="contactinfo">Контактная. инф.:</label>
      <input type="text" class="fullline" id="contactinfo" name="contactinfo" value="${bank?.contactinfo?:''}" />          
      <label for="operinfo">Инф. по операц.-у:</label>
      <input type="text" class="fullline" id="operinfo" name="operinfo" value="${bank?.operinfo?:''}" />
      <label for="techinfo">Тех. поддержка:</label>
      <input type="text" class="fullline" id="techinfo" name="techinfo" value="${bank?.techinfo?:''}" />
      <label for="prevnameinfo">Пред. название:</label>
      <input type="text" class="fullline" id="prevnameinfo" name="prevnameinfo" value="${bank?.prevnameinfo}"/>
      <label for="comment">Общий комментарий:</label>
      <g:textArea name="comment" value="${bank?.comment}"/><br />
      <hr class="admin" style="width:780px;float:left"/><a id="expandlink" style="text-decoration:none" href="javascript:void(0)" onclick="togglebankprice()">&nbsp;&nbsp;Развернуть&nbsp;<i class="icon-collapse"></i></a><hr class="admin" style="width:65px;float:right"/>
      <div id="bankprice" style="display:none;width:960px">
        <h3 class="fleft">Блок расценок</h3>
        <div class="clear"></div>
        <label for="rko_rate" class="long" <g:if test="${bank}">disabled</g:if>>Стоимость рко:</label>
        <input type="text" id="rko_rate" name="rko_rate" value="${bank?.rko_rate?:0}" <g:if test="${bank}">disabled</g:if>/><br/>
        <label for="open_rate" class="long" <g:if test="${bank}">disabled</g:if>>Стоимость откр. счета:</label>
        <input type="text" id="open_rate" name="open_rate" value="${bank?.open_rate?:0}" <g:if test="${bank}">disabled</g:if>/><br/>
        <label for="ibankopen_rate" class="long" <g:if test="${bank}">disabled</g:if>>Стоимость открытия банк-клиента:</label>
        <input type="text" align="right" id="ibankopen_rate" name="ibankopen_rate" value="${bank?.ibankopen_rate?:0}" <g:if test="${bank}">disabled</g:if>/><br/>
        <label for="ibankserv_rate" class="long" <g:if test="${bank}">disabled</g:if>>Стоимость обслуживания банк-клиента:</label>
        <input type="text" id="ibankserv_rate" name="ibankserv_rate" value="${bank?.ibankserv_rate?:0}" <g:if test="${bank}">disabled</g:if>/><br/>
        <label for="plat_rate" class="long" <g:if test="${bank}">disabled</g:if>>Стоимость платежного поручения:</label>
        <input type="text" id="plat_rate" name="plat_rate" value="${bank?.plat_rate?:0}" <g:if test="${bank}">disabled</g:if>/><br/>
        <label for="platreturn_rate" class="long" <g:if test="${bank}">disabled</g:if>>Стоимость отзыва платежного поручения:</label>
        <input type="text" id="platreturn_rate" name="platreturn_rate" value="${bank?.platreturn_rate?:0}" <g:if test="${bank}">disabled</g:if>/><br/>
        <label for="race_rate" class="long" <g:if test="${bank}">disabled</g:if>>Стоимость рейсов:</label>
        <input type="text" id="race_rate" name="race_rate" value="${bank?.race_rate?:0}" <g:if test="${bank}">disabled</g:if>/><br/>
        <label for="income_rate" class="long" <g:if test="${bank}">disabled</g:if>>Стоимость работ под поступления тек. дня:</label>
        <input type="text" id="income_rate" name="income_rate" value="${bank?.income_rate?:0}" <g:if test="${bank}">disabled</g:if>/><br/>
        <label for="urgent_rate" class="long" <g:if test="${bank}">disabled</g:if>>Стоимость срочного исполнения:</label>
        <input type="text" id="urgent_rate" name="urgent_rate" value="${bank?.urgent_rate?:0}" <g:if test="${bank}">disabled</g:if>/><br/>
        <label for="besp_rate" class="long" <g:if test="${bank}">disabled</g:if>>Стоимость БЭСП:</label>
        <input type="text" id="besp_rate" name="besp_rate" value="${bank?.besp_rate?:0}" <g:if test="${bank}">disabled</g:if>/><br/>
        <label for="spravka_rate" class="long" <g:if test="${bank}">disabled</g:if>>Стоимость выдачи справок:</label>
        <input type="text" id="spravka_rate" name="spravka_rate" value="${bank?.spravka_rate?:0}" <g:if test="${bank}">disabled</g:if>/><br/>
        <label for="addline_rate" class="long" <g:if test="${bank}">disabled</g:if>>Стоимость добавить строку тарифа:</label>
        <input type="text" id="addline_rate" name="addline_rate" value="${bank?.addline_rate?:0}" <g:if test="${bank}">disabled</g:if>/><br/>
        <label for="vypiska_rate" class="long" <g:if test="${bank}">disabled</g:if>>Стоимость выписки:</label>
        <input type="text" id="vypiska_rate" name="vypiska_rate" value="${bank?.vypiska_rate?:0}" <g:if test="${bank}">disabled</g:if>/>
        <hr class="admin">
      </div>  
      <input type="hidden" name="is_edit" value="${bank?1:0}"/>            
      <div class="fright" id="btns" style="padding-top:10px">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
        <g:if test="${(!bank && user?.group?.is_bankinsert) || (bank && user?.group?.is_bankedit)}">
          <input type="submit" id="submit_button" value="Сохранить"/>        
        </g:if>  
      </div>     
    </g:formRemote>
    <div class="clear"></div>
    <g:if test="${bank}">
      <div class="tabs">
        <ul class="nav">
          <g:if test="${user?.group?.is_bankedit}"><li class="selected"><a href="javascript:void(0)" onclick="viewCell(0)">Компании</a></li></g:if>
          <li <g:if test="${!user?.group?.is_bankedit}">class="selected"</g:if>><a href="javascript:void(0)" onclick="viewCell(1)">История</a></li>  
        </ul>
        <div class="tab-content">
          <div class="inner">
            <div id="details"></div>
          </div>
        </div>
      </div>
      <g:formRemote name="companyform" url="[action:'bankcompanylist',id:bank.id]" update="[success:'details']">
        <input type="submit" class="button" id="bankcompany_submit_button" value="Показать" style="display:none" />
      </g:formRemote>      
      <g:formRemote name="historyform" url="[action:'bankhistorylist',id:bank.id]" update="[success:'details']">
        <input type="submit" class="button" id="bankhistory_submit_button" value="Показать" style="display:none" />
      </g:formRemote>
    </g:if>    
    <g:form id="returnToListForm" name="returnToListForm" url="${[controller:'catalog', action:'index',params:[fromDetails:1]]}">
    </g:form>    
  </body>
</html>
