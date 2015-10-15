<html>
  <head>
    <title>${infotext?.title?:'Prisma - Компании'}</title>
    <meta name="layout" content="main" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript>
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
      function setHolding(iStatus){
        $('is_holding').value=iStatus%2;
        jQuery('.tabs a[class="active"]').removeClass('active');
        $('holding'+iStatus%2).addClassName('active');
        $('form_submit_button').click();
      }
      new Autocomplete('okved', {
        serviceUrl:'${resource(dir:"autocomplete",file:"okved_autocomplete")}'
      });
      new Autocomplete('cname', {
        serviceUrl:'${resource(dir:"autocomplete",file:"companyname_autocomplete")}'
      });
      new Autocomplete('bankname', {
        serviceUrl:'${resource(dir:"autocomplete",file:"bankname_autocomplete")}'
      });
      new Autocomplete('gd', {
        serviceUrl:'${resource(dir:"autocomplete",file:"gd_autocomplete")}',
        width:252
      });
      new Autocomplete('district', {
        serviceUrl:'${resource(dir:"autocomplete",file:"district_autocomplete")}',
        width:252
      });
      function resetfilter(){
        $('cname').value="";
        $('bankname').value="";
        $('okved').value="";
        $('gd').value="";
        $('district').value="";
        $('project_id').selectedIndex=0;
        $('bankaccount').selectedIndex=0;
        $('responsible').selectedIndex=0;
        $('color').selectedIndex=0;
        $('colorfill').selectedIndex=0;
        $('is_license').checked=false;
      }
      function setColor(iId,sColor){        
        <g:remoteFunction controller='company' action='setcolor' params="'id='+iId+'&color='+sColor" onSuccess="processColorResponse(e,iId,sColor);"/>
      }
      function processColorResponse(e,iId,sColor){       
        jQuery('#color'+iId).css('color',sColor);        
      }      
      function setColorFill(iId,iFill){        
        <g:remoteFunction controller='company' action='setcolorfill' params="'id='+iId+'&colorfill='+iFill" onSuccess="processFillResponse(e,iId,iFill);"/>
      }
      function processFillResponse(e,iId,iFill){       
        jQuery('#color'+iId).attr('class',iFill?'icon-flag icon-large':'icon-flag-checkered icon-large');        
      }            
      function toggleaddition(){        
        if(!jQuery("#addition").is(':hidden')){
          $("expandlink").innerHTML = '&nbsp;&nbsp;Развернуть&nbsp;<i class="icon-collapse"></i>';
          jQuery('#addition').slideUp();                 
        } else {
          $("expandlink").innerHTML = '&nbsp;&nbsp;Скрыть&nbsp;<i class="icon-collapse-top"></i>';
          jQuery('#addition').slideDown();         
        }
      }      
    </g:javascript>
  </head>
  <body onload="setHolding(${inrequest?.is_holding?:0})">
    <g:formRemote name="allForm" url="[controller:'company',action:'list']" update="list">
      <div class="tabs padtop fright">
        <a id="holding1" onclick="setHolding(1)"><i class="icon-list icon-large"></i> Холдинг </a>
        <a id="holding0" onclick="setHolding(0)"><i class="icon-list icon-large"></i> Внешние </a>
      </div>
      <div class="clear"></div>
      <div class="padtop filter">
        <label class="auto" for="cname">Название:</label>
        <input type="text" id="cname" name="cname" value="${inrequest?.cname?:''}" />
        <div id="companyname_autocomplete" class="autocomplete" style="display:none"></div>
        <label class="auto" for="bankname">Банк:</label>
        <input type="text" id="bankname" name="bankname" value="${inrequest?.bankname?:''}" />
        <div id="bankname_autocomplete" class="autocomplete" style="display:none"></div>
        <label class="auto" for="okved">ОКВЭД:</label>
        <input type="text" id="okved" name="okved" value="${inrequest?.okved?:''}" style="width:117px" />
        <div id="okved_autocomplete" class="autocomplete" style="display:none"></div>
        <hr class="admin fleft" style="width:650px;" /><a id="expandlink" style="text-decoration:none" href="javascript:void(0)" onclick="toggleaddition()">&nbsp;&nbsp;Развернуть&nbsp;<i class="icon-collapse"></i></a><hr class="admin fright" style="width:180px;" />
        <div id="addition" style="display:none;clear:both;">  
          <label class="auto" for="project_id">Проект:</label>
          <g:select class="mini" name="project_id" value="${inrequest?.project_id?:0}" from="${projects}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/>
          <label class="auto" for="bankaccount">Расчетный счет:</label>
          <g:select class="mini" name="bankaccount" value="${inrequest?.bankaccount}" from="['Да','Нет']" keys="[1,0]" noSelection="${['-100':'все']}"/>
          <label class="auto" for="gd">Ген. директор:</label>
          <input type="text" id="gd" name="gd" value="${inrequest?.gd?:''}" />
          <div id="gd_autocomplete" class="autocomplete" style="display:none"></div>
          <label class="auto" for="responsible">Исполнитель:</label>
          <g:select class="mini" name="responsible" value="${inrequest?.responsible?:0}" from="${responsiblies}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/>
          <label class="auto" for="district">Район налоговой:</label>
          <input type="text" id="district" name="district" value="${inrequest?.district?:''}" />
          <div id="district_autocomplete" class="autocomplete" style="display:none"></div>
          <label class="auto" for="is_license">
            <input type="checkbox" id="is_license" name="is_license" value="1" <g:if test="${inrequest?.is_license}">checked</g:if> />
            Активные лицензии
          </label>
          <label class="auto" for="www">Сайт:</label>
          <g:select class="mini" name="www" value="${inrequest?.www}" from="['Да','Нет']" keys="[1,0]" noSelection="${['-100':'все']}"/>
          <label class="auto" for="color">Цвет:</label>
          <select class="mini" id="color" name="color">
            <option value="" <g:if test="${inrequest?.color==''}">selected="selected"</g:if>>все</option>
            <option value="transparent" <g:if test="${inrequest?.color=='transparent'}">selected="selected"</g:if>>прозрачный</option>
          <g:each in="${colors}" var="it">
            <option value="${it.code}" style="background: -moz-linear-gradient(left, ${it.code} 10%, #fff 10%);
              background: -webkit-gradient(linear, left top, right top, color-stop(10%,${it.code}), color-stop(10%,#fff));
              background: -webkit-linear-gradient(left, ${it.code} 10%, #fff 10%);
              background-image: -o-linear-gradient(left, ${it.code} 10%, #fff 10%);
              background: -ms-linear-gradient(left, ${it.code} 10%, #fff 10%);
              background: linear-gradient(to right, ${it.code} 10%, #fff 10%);
              filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='${it.code}', endColorstr='#fff', GradientType=1);" <g:if test="${it.code==inrequest?.color}">selected="selected"</g:if>>${it.name}</option>
          </g:each>
          </select>
          <label class="auto" for="colorfill">Заполнение</label>            
          <g:select class="mini" name="colorfill" value="${inrequest?.colorfill}" from="['да','нет']" keys="[1,0]" noSelection="${['-100':'все']}"/>
        </div>
        <div class="fright">
          <input type="button" class="spacing reset" value="Сброс" onclick="resetfilter()"/>
          <input type="submit" id="form_submit_button" value="Показать" />
        <g:if test="${iscanincert}">
          <g:link action="detail" class="button">Новая &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
        </g:if>
        </div>
        <div class="clear"></div>
      </div>
      <input type="hidden" id="is_holding" name="is_holding" value="${inrequest?.is_holding?:0}" />
    </g:formRemote>
    <div id="list"></div>
  </body>
</html>
