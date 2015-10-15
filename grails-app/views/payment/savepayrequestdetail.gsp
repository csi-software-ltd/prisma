<script src="/Prisma/static/js/prototype/prototype.js" type="text/javascript" ></script>
<script type="text/javascript">
  var sErrorMsg = '';
  ['file','agreementtype_id','agreement_id','tax_id','destination','summa'].forEach(function(ids){
    if(window.top.document.getElementById(ids))
      window.top.document.getElementById(ids).removeClassName('red');
  });
  window.top.document.getElementById("is_task").value=0;
  var errorcode = ${errorcode};
  if(${errorcode.size()?1:0}){
    errorcode.forEach(function(err){
      switch (err) {
        case 1: sErrorMsg+='<li>${message(code:"error.file.upload.message")}</li>'; window.top.document.getElementById('file').addClassName('red'); break;
        case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип договора"])}</li>'; window.top.document.getElementById('agreementtype_id').addClassName('red'); break;
        case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["№ договора"])}</li>'; window.top.document.getElementById('agreement_id').addClassName('red'); break;
        case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип налога"])}</li>'; window.top.document.getElementById('tax_id').addClassName('red'); break;
        case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Назначение платежа"])}</li>'; window.top.document.getElementById('destination').addClassName('red'); break;
        case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; window.top.document.getElementById('summa').addClassName('red'); break;
        case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
     }
    });
    window.top.document.getElementById("errorlist").innerHTML=sErrorMsg;
    window.top.document.getElementById("errorlist").up('div').show();
  } else
    window.top.document.location.reload(true);
</script>