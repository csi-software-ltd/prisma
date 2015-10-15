﻿<script src="/Prisma/static/js/prototype/prototype.js" type="text/javascript" ></script>
<script type="text/javascript">
  var sErrorMsg = '';
  ['compercent','file','subcompercent'].forEach(function(ids){
    if(window.top.document.getElementById(ids))
      window.top.document.getElementById(ids).removeClassName('red');
  });
  var errorcode = ${errorcode};
  if(${errorcode.size()?1:0}){
    errorcode.forEach(function(err){
      switch (err) {
        case 1: sErrorMsg+='<li>${message(code:"error.file.upload.message")}</li>'; window.top.document.getElementById('file').addClassName('red'); break;
        case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Процент комиссии"])}</li>'; window.top.document.getElementById('compercent').addClassName('red'); break;
        case 3: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Процент возврата"])}</li>'; window.top.document.getElementById('subcompercent').addClassName('red'); break;
        case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
      }
    });
    window.top.document.getElementById("errorlist").innerHTML = sErrorMsg;
    window.top.document.getElementById("errorlist").up('div').show();
  } else
    window.top.document.location.reload(true);
</script>