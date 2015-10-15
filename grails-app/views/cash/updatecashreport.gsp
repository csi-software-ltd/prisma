﻿<script src="/Prisma/static/js/prototype/prototype.js" type="text/javascript" ></script>
<script type="text/javascript">
  var sErrorMsg = '';
  ['summa','file','comment_dep','comment','expensetype_id','description'].forEach(function(ids){
    if(window.top.document.getElementById(ids))
      window.top.document.getElementById(ids).removeClassName('red');
  });
  ['repdate'].forEach(function(ids){
    if(window.top.document.getElementById(ids).up('span'))
      window.top.document.getElementById(ids).up('span').removeClassName('k-error-colored');
  });
  var errorcode = ${errorcode};
  if(${errorcode.size()?1:0}){
    errorcode.forEach(function(err){
      switch (err) {
        case 1: sErrorMsg+='<li>${message(code:"error.file.upload.message")}</li>'; window.top.document.getElementById('file').addClassName('red'); break;
        case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; window.top.document.getElementById('summa').addClassName('red'); break;
        case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Комментарий главного кассира"])}</li>'; window.top.document.getElementById('comment').addClassName('red'); break;
        case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Комментарий кассира отдела"])}</li>'; window.top.document.getElementById('comment_dep').addClassName('red'); break;
        case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата подотчета"])}</li>'; window.top.document.getElementById('repdate').up('span').addClassName('k-error-colored'); break;
        case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип расхода"])}</li>'; window.top.document.getElementById('expensetype_id').addClassName('red'); break;
        case 7: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Описание расхода"])}</li>'; window.top.document.getElementById('description').addClassName('red'); break;
        case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
      }
    });
    window.top.document.getElementById("errorlist").innerHTML=sErrorMsg;
    window.top.document.getElementById("errorlist").up('div').show();
  } else
    window.top.document.location.reload(true);
</script>