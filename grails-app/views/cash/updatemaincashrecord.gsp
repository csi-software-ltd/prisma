<script src="/Prisma/static/js/prototype/prototype.js" type="text/javascript" ></script>
<script type="text/javascript">
  var sErrorMsg = '';
  ['maincashclass','summa','file','department_id','pers_id','agentagr_id','agent_id','expensetype_id','loaner_id','parkinger_id','comment','indeposit_id'].forEach(function(ids){
    if(window.top.document.getElementById(ids))
      window.top.document.getElementById(ids).removeClassName('red');
  });
  ['operationdate'].forEach(function(ids){
    if(window.top.document.getElementById(ids))
      window.top.document.getElementById(ids).up('span').removeClassName('k-error-colored');
  });
  var errorcode = ${errorcode};
  if(${errorcode.size()?1:0}){
    errorcode.forEach(function(err){
      switch (err) {
        case 1: sErrorMsg+='<li>${message(code:"error.file.upload.message")}</li>'; window.top.document.getElementById('file').addClassName('red'); break;
        case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; window.top.document.getElementById('summa').addClassName('red'); break;
        case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Класс"])}</li>'; window.top.document.getElementById('maincashclass').addClassName('red'); break;
        case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Отдел или Подотчетное лицо"])}</li>'; window.top.document.getElementById('pers_id').addClassName('red'); window.top.document.getElementById('department_id').addClassName('red'); break;
        case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата операции"])}</li>'; window.top.document.getElementById('operationdate').up('span').addClassName('k-error-colored'); break;
        case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Скан"])}</li>'; window.top.document.getElementById('file').addClassName('red'); break;
        case 7: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Агентский договор или Агент"])}</li>'; window.top.document.getElementById('agentagr_id').addClassName('red'); window.top.document.getElementById('agent_id').addClassName('red'); break;
        case 8: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Агентский договор"])}</li>'; window.top.document.getElementById('agentagr_id').addClassName('red'); break;
        case 9: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Доходы-расходы"])}</li>'; window.top.document.getElementById('expensetype_id').addClassName('red'); break;
        case 10: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Отдел"])}</li>'; window.top.document.getElementById('department_id').addClassName('red'); break;
        case 11: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Заёмщик"])}</li>'; window.top.document.getElementById('loaner_id').addClassName('red'); break;
        case 12: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сотрудник"])}</li>'; window.top.document.getElementById('parkinger_id').addClassName('red'); break;
        case 13: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Комментарий"])}</li>'; window.top.document.getElementById('comment').addClassName('red'); break;
        case 14: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Договор депозита"])}</li>'; window.top.document.getElementById('indeposit_id').addClassName('red'); break;
        case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
        case 101: sErrorMsg+='<li>${message(code:"error.bdmaxpackets.message")}</li>'; break;
      }
    });
    window.top.document.getElementById("errorlist").innerHTML=sErrorMsg;
    window.top.document.getElementById("errorlist").up('div').show();
  } else
    window.top.returnToList();
</script>