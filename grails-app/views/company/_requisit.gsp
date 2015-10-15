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
  <g:if test="${!company}">
    <h1>Нет данных</h1>
  </g:if><g:else>
    <div>
      <div style="font-size:20pt"  align="center">КАРТОЧКА КЛИЕНТА</div><br/>
      <div style="clear:both;text-align:center"></div><br/>
      <table style="width:680px;font-size:9pt">        
        <tbody>        
          <tr>
            <td width="180px">Наименование компании</td>
            <td style="font-size:12pt"><b>${company.legalname}, ${company.name}</b></td>
          </tr>
          <g:if test="${industry}">
          <tr>  
            <td>Допуск СРО</td>            
            <td>
              <g:each in="${industry}" var="item" status="i">
                <g:if test="${i}">
                  <br/>
                </g:if>
                ${item.name?:''}
              </g:each>
            </td>
          </tr>          
          </g:if>
          <tr>  
            <td>ИНН (действующий)</td>
            <td>${company.inn}</td>
          </tr>        
          <tr>  
            <td>КПП (действующий)</td>
            <td>${company.kpp}</td>
          </tr>
          <tr>  
            <td>ОГРН</td>
            <td>${company.ogrn}</td>
          </tr>
          <tr>  
            <td>Дата регистрации</td>
            <td>${company?.opendate?(String.format('%td.%<tm.%<tY',company?.opendate)):''}</td>
          </tr>
          <tr>  
            <td>ОКАТО (действующий)</td>
            <td>${company?.okato}</td>
          </tr>
          <tr>  
            <td>ОКТМО</td>
            <td>${company?.oktmo}</td>
          </tr>
          <tr>  
            <td>ОКПО (действующий)</td>
            <td>${company?.okpo}</td>
          </tr>
        <g:if test="${compokved}">
          <tr>  
            <td>ОКВЭД</td>
            <td>
            <g:each in="${compokved}">
              ${it.okved_id}<g:if test="${it.moddate}">&nbsp;(${String.format('%td.%<tm.%<tY',it.moddate)})</g:if>${it?.is_main?' (основной)':''} - ${it.okvedname?:''}<br/>
            </g:each>
            </td>
          </tr>
        </g:if>
          <tr>  
            <td>Адрес места нахождения (действующий)</td>
            <td>${company?.legaladr?:''}</td>
          </tr>
          <tr>  
            <td>Почтовый адрес</td>
            <td>${company?.postadr?:''}</td>
          </tr>         
          <tr>          
            <td>Генеральный директор (действующий)</td>
            <td>
              <g:if test="${general}">${general?.fullname?:''} с ${general?.jobstart?(String.format('%td.%<tm.%<tY',general?.jobstart)):''}</g:if>              
            </td>
          </tr>          
          <g:if test="${account}">
            <tr>  
              <td colspan="2" align="center" style="font-size:12pt">Расчетный счет в ${bank?.name?:''}<g:if test="${bank?.prevnameinfo}"><br/>(бывш. ${bank?.prevnameinfo})</g:if></td>           
            </tr>
            <tr>  
              <td>Открыт/закрыт</td>
              <td>${account?.opendate?(' Открыт '+String.format('%td.%<tm.%<tY',account?.opendate)):''} ${account?.closedate?(' /закрыт '+String.format('%td.%<tm.%<tY',account?.closedate)):''}  ${(!bank?.is_license && bank?.stopdate)?(' /отозвана лицензия '+String.format('%td.%<tm.%<tY',bank?.stopdate)):''}</td>
            </tr>
            <tr>  
              <td>Расчетный счет</td>
              <td>${g.account(value:account?.schet)}</td>
            </tr>
            <tr>  
              <td>Корреспондентский счет</td>
              <td>${bank?.coraccount?:''}</td>
            </tr>
            <tr>  
              <td>БИК</td>
              <td>${bank?.id?:''}</td>
            </tr>
            <tr>  
              <td>Банк получателя</td>
              <td>${bank?.name?:''}</td>
            </tr>
          </g:if>          
        </tbody>
      </table>
    </div>
  </g:else>
  </body>
</html>
