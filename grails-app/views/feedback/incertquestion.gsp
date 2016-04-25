<script src="/Prisma/static/js/prototype/prototype.js" type="text/javascript" ></script>
<script type="text/javascript">
  var sErrorMsg = '';
  ['file','qtext'].forEach(function(ids){
    if(window.top.document.getElementById(ids))
      window.top.document.getElementById(ids).removeClassName('red');
  });
  var errorcode = ${errorcode};
  if(${errorcode.size()?1:0}){
    errorcode.forEach(function(err){
      switch (err) {
        case 1: sErrorMsg+='<li>${message(code:"error.file.upload.message")}</li>'; window.top.document.getElementById('file').addClassName('red'); break;
        case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Текст вопроса"])}</li>'; window.top.document.getElementById('qtext').addClassName('red'); break;
        case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
      }
    });
    window.top.document.getElementById("errorlist").innerHTML=sErrorMsg;
    window.top.document.getElementById("errorlist").up('div').show();
  } else
    window.top.returnToList();
</script>