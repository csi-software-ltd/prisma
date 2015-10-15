<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <style type="text/css">
      @font-face {
        src: url('http://nbps.ru:8080/font/arial.ttf');
        -fs-pdf-font-embed: embed;
        -fs-pdf-font-encoding: Identity-H;
      }
      @page {
        size: 21cm 29.7cm;
      }
      body { font-family: "Arial Unicode MS", Arial, sans-serif; }
      table { border-top: 2px solid #000; border-left: 1px solid #000; }      
      table td { border-bottom: 1px solid #000; border-right: 1px solid #000 }
    </style>
  </head>
  <body style="width:720px">
  <g:if test="${!taskpay}">
    <h1>Нет данных</h1>
  </g:if>
  <g:else>
    <div>
      <div style="font-size:20pt"  align="center">ЗАДАНИЕ НА ПЛАНОВЫЕ ПЛАТЕЖИ</div><br/>
      <div style="clear:both;text-align:center"></div><br/>
      <table style="width:680px;font-size:9pt">        
        <tbody>        
          <tr>
            <td width="180px">Исполнитель</td>
            <td style="font-size:12pt"><b>${User.get(taskpay?.executor?:0l)?.name?:''}</b></td>
          </tr>
          <tr>
            <td>Срок исполнения</td>
            <td>${taskpay?.term?(String.format('%td.%<tm.%<tY',taskpay?.term)):''}</td>
          </tr>
          <tr>
            <td>Статус задания</td>
            <td>${taskpaystatus?.name?:''}</td>
          </tr>
          <tr>
            <td>Сумма</td>
            <td>${number(value:taskpay?.summa?:0)}</td>
          </tr>
          <g:if test="${taskpay?.description}">
            <tr>
              <td>Описание</td>
              <td>${taskpay?.description?:''}</td>
            </tr>
          </g:if>
          <g:if test="${company}">
            <tr>
              <td>Название компании</td>
              <td>${company?.name?:''}</td>
            </tr>
            <tr>
              <td>Инн компании</td>
              <td>${company?.inn?:''}</td>
            </tr>
            <tr>
              <td>Название банка</td>
              <td>${bank?.name?:''}</td>
            </tr>
            <tr>
              <td>Бик банка</td>
              <td>${bank?.id?:''}</td>
            </tr>
            <tr>
              <td>Корр. счет банка</td>
              <td>${bank?.coraccount?:''}</td>
            </tr>
            <tr>
              <td>Расчетный счет</td>
              <td>${bankaccount?.schet?:''}</td>
            </tr>                   
          </g:if>                            
          <g:each in="${payrequest}">
            <tr>  
              <td colspan="2" align="center" style="font-size:12pt">Платеж</td>           
            </tr>          
            <tr>  
              <td>Компания получатель</td>
              <td>${it?.tocompany}</td>
            </tr>
            <tr>  
              <td>Сумма платежа</td>
              <td>${number(value:it?.summa?:0)}</td>
            </tr>
            <tr>  
              <td>Сумма НДС</td>
              <td>${number(value:it?.summands)}</td>
            </tr>
            <tr>  
              <td>Банк получателя</td>
              <td>${it?.tobank}</td>
            </tr>
            <tr>  
              <td>Бик банка получателя</td>
              <td>${it?.tobankbik}</td>
            </tr>
            <tr>  
              <td>Корсчет получателя</td>
              <td>${it?.tocorraccount}</td>
            </tr>
            <tr>  
              <td>Расчетный счет получателя</td>
              <td>${it?.toaccount}</td>
            </tr>
            <g:if test="${it?.tax_id}">
              <tr>  
                <td>Тип налога:</td>
                <td>${Tax.get(it?.tax_id?:0)?.name?:''}</td>
              </tr> 
            </g:if>            
            <tr>  
              <td>Налоговый период</td>
              <td>${it?.platperiod}</td>
            </tr>
            <tr>  
              <td>Назначение платежа</td>
              <td>${it?.destination}</td>
            </tr>                                                                      
          </g:each>          
        </tbody>
      </table>
    </div>
  </g:else>
  </body>
</html>
