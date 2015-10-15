<html>
  <head>
    <title>${infotext?.title?:'Prisma - Обратная связь'}</title>
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
      function setactivelink(iStatus){
        jQuery('.tabs a[class="active"]').removeClass('active');
        $('feedlink'+iStatus).addClassName('active');
        if (iStatus=='2') $('filter').hide();
        else $('filter').show();
      }
      function showdetails(el,e){
        jQuery(el).next("tr").children("td").children("div").html(e.responseText);
        jQuery(el).next("tr").children("td").css('padding','10px').children("div").slideDown(400);
        jQuery(el).next("tr").siblings("tr.detail").children("td").css('padding','0').find("div:visible").slideUp(400);
        jQuery(el).addClass("current");
        jQuery(el).siblings("tr").removeClass("current");
      }
      function getdetail(el,iId){
        <g:remoteFunction controller='feedback' action='faqanswer' params="'faq_id='+iId" onComplete="showdetails(el,e)" />
      }
      function resetAnswerFilter(){
        $('username').value = '';
        $('keyword').value = '';
        $('fid').value = '';
        $('feedtype').selectedIndex = 0;
        $('modstatus').selectedIndex = 0;
      }
    </g:javascript>
  </head>
  <body onload="\$('feedlink${inrequest?.feedsection?:isSuper?1:0}').click();">
    <div class="tabs padtop fright">
    <g:if test="${!isSuper}">
      <g:remoteLink id="feedlink0" before="setactivelink(0);" url="${[action:'askfilter']}" update="filter"><i class="icon-question-sign icon-large"></i> Задать вопрос </g:remoteLink>
    </g:if><g:else>
      <g:remoteLink id="feedlink1" before="setactivelink(1);" url="${[action:'answerfilter']}" update="filter"><i class="icon-bullhorn icon-large"></i> Ответы на вопросы </g:remoteLink>
    </g:else>
      <g:remoteLink id="feedlink2" before="setactivelink(2);" url="${[action:'faq']}" update="list"><i class="icon-bug icon-large"></i> Часто задаваемые вопросы </g:remoteLink>
    </div>
    <div class="clear"></div>
    <div id="filter" class="padtop filter">
    </div>
    <div id="list"></div>
  </body>
</html>
