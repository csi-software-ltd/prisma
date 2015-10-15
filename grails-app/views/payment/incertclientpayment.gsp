<script src="/Prisma/static/js/prototype/prototype.js" type="text/javascript" ></script>
<script type="text/javascript">
  var sErrorMsg = '';
  ['paycat','paytype','summa','comment','client_id','file','subclient_id','frombank_span','destination','agreementtype_id','agreement_id'].forEach(function(ids){
    if(window.top.document.getElementById(ids))
      window.top.document.getElementById(ids).removeClassName('red');
  });
  ['paydate','fromcompany','tocompany','tobank'].forEach(function(ids){
    if(window.top.document.getElementById(ids))
      window.top.document.getElementById(ids).up('span').removeClassName('k-error-colored');
  });
  var errorcode = ${errorcode};
  if(${errorcode.size()?1:0}){
    errorcode.forEach(function(err){
      switch (err) {
        case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип"])}</li>'; window.top.document.getElementById("paytype").addClassName('red'); break;
        case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата платежа"])}</li>'; window.top.document.getElementById("paydate").up('span').addClassName('k-error-colored'); break;
        case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Категория"])}</li>'; window.top.document.getElementById("paycat").addClassName('red'); break;
        case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Плат. компания"])}</li>'; window.top.document.getElementById("fromcompany").up('span').addClassName('k-error-colored'); break;
        case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Получ. компания"])}</li>'; window.top.document.getElementById("tocompany").up('span').addClassName('k-error-colored'); break;
        case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк получателя"])}</li>'; window.top.document.getElementById("tobank").up('span').addClassName('k-error-colored'); break;
        case 7: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Банк плательщика"])}</li>'; window.top.document.getElementById("frombank_span").addClassName('red'); break;
        case 8: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Банк плательщика"])}</li>'; window.top.document.getElementById("frombank_span").addClassName('red'); break;
        case 9: sErrorMsg+='<li>${message(code:"error.file.upload.message")}</li>'; window.top.document.getElementById('file').addClassName('red'); break;
        case 10: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Клиент"])}</li>'; window.top.document.getElementById("client_id").addClassName('red'); break;
        case 11: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; window.top.document.getElementById("summa").addClassName('red'); break;
        case 12: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Назначение"])}</li>'; window.top.document.getElementById("destination").addClassName('red'); break;
        case 13: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Файл реквизитов или Комментарий"])}</li>'; window.top.document.getElementById('file').addClassName('red'); window.top.document.getElementById("comment").addClassName('red'); break;
        case 14: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Подклиент"])}</li>'; window.top.document.getElementById("subclient_id").addClassName('red'); break;
        case 15: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип договора"])}</li>'; window.top.document.getElementById("agreementtype_id").addClassName('red'); break;
        case 16: sErrorMsg+='<li>${message(code:"error.blank.message",args:["№ договора"])}</li>'; window.top.document.getElementById("agreement_id").addClassName('red'); break;
        case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
      }
    });
    window.top.document.getElementById("errorlist").innerHTML = sErrorMsg;
    window.top.document.getElementById("errorlist").up('div').show();
  } else if (${is_detail&&taskpay_id>0?1:0}){
    window.top.location.assign('${createLink(controller:'task',action:'taskpaydetail',id:taskpay_id)}');
  } else if (${taskpay_id>0?1:0}){
    window.top.location.assign('${createLink(controller:'task',action:'index',params:[taskobject:3])}');
  } else
    window.top.returnToList();
</script>