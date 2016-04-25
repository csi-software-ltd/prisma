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
            <td>${company.opendate?(String.format('%td.%<tm.%<tY',company.opendate)):''} <g:if test="${company.reregdate}"> (перерегистрация ${String.format('%td.%<tm.%<tY',company.reregdate)})</g:if></td>
          </tr>
          <tr>
            <td>Регистрирующий орган</td>
            <td>${company.regauthority}</td>
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
          <tr>  
            <td>ОКВЭД (действующий)</td>
            <td><g:each in="${compokved_active}">
                 ${it.okved_id}<g:if test="${it.moddate}">&nbsp;(${String.format('%td.%<tm.%<tY',it.moddate)})</g:if>${it?.is_main?' (основной)':''} - ${it.okvedname?:''}<br/>
                </g:each>
            </td>
          </tr>
          <tr>  
            <td>ОКВЭД (недействующий)</td>
            <td><g:each in="${compokved_not_active}">
                 ${it.okved_id}${it?.is_main?' (основной)':''} - ${it.okvedname?:''}<br/>
                </g:each>
            </td>
          </tr>
          <tr>  
            <td>Юридический адрес (действующий)</td>
            <td>${company?.legaladr?:''}</td>
          </tr>
          <g:if test="${companyhist}">
            <tr>  
              <td>Юридический адрес (недействующий)</td>
              <td>
                <g:each in="${companyhist}" var="item" status="i">
                  <g:if test="${i}">
                    <br/>
                  </g:if>             
                  ${item}                             
                </g:each>
              </td>              
            </tr>
          </g:if>
          <tr>
            <td>Почтовый адрес</td>
            <td>${company?.postadr?:''}</td>
          </tr>
          <tr>
            <td>Адрес офиса</td>
            <td>
              <g:each in="${office}" var="item" status="i">
                <g:if test="${i}">
                  <br/>
                </g:if>
                ${item?.fulladdress?:''}
              </g:each>
            </td>
          </tr>
          <tr>
            <td>Адрес офиса (архив)</td>
            <td>
              <g:each in="${officeold}" var="item" status="i">
                <g:if test="${i}">
                  <br/>
                </g:if>
                ${item?.fulladdress?:''}
              </g:each>
            </td>
          </tr>
          <tr>
            <td>Адрес склада</td>
            <td>
              <g:each in="${wh}" var="item" status="i">
                <g:if test="${i}">
                  <br/>
                </g:if>
                ${item?.fulladdress?:''}
              </g:each>
            </td>
          </tr>
          <tr>
            <td>Адрес склада (архив)</td>
            <td>
              <g:each in="${whold}" var="item" status="i">
                <g:if test="${i}">
                  <br/>
                </g:if>
                ${item?.fulladdress?:''}
              </g:each>
            </td>
          </tr>
          <tr>
            <td>Контактная информация</td>
            <td>${company.tel?'тел.:'+company.tel:''}&nbsp;${company.email?'email:'+company.email:''}&nbsp;${company.www?'сайт:'+company.www:''}</td>
          </tr>
        <g:if test="${company?.is_holding}">
        <g:each in="${complicenses}">
          <tr>
            <td>Лицензия</td>
            <td>
              ${it.name} (${it.nomer})<br/>
              Дата выдачи: ${String.format('%td.%<tm.%<tY',it.ldate)}<br/>
              Срок действия: <g:if test="${it.validity}"> ${String.format('%td.%<tm.%<tY',it.validity)}</g:if><g:else> бессрочно</g:else><br/>
              Лицензирующий орган: ${it.authority}
            </td>
          </tr>
        </g:each>
        <g:if test="${!complicenses}">
          <tr>
            <td>Лицензия</td>
            <td>нет лицензий</td>
          </tr>
        </g:if>
          <tr>
            <td>Уставной капитал</td>
            <td>
              ${intnumber(value:company?.capital)} - ${company.capitaldate?String.format('%td.%<tm.%<tY',company.capitaldate):'нет'} - ${company.capitalsecure==1?'Имуществом':'Деньгами'}<g:if test="${company.capitalsecure!=1}"> - ${company.capitalpaid==1?'Оплачен':'Не оплачен'}</g:if>
            </td>
          </tr>
          <tr>
            <td>Регистрационные коды</td>
            <td>
              ПФР - ${company.pfrfreg?:'нет'}<br/>
              ФСС - ${company.fssreg?:'нет'}
            </td>
          </tr>
        <g:each in="${owners}" var="owner">
          <tr>
            <td>Учредитель (действующий)</td>
            <td>
              ${owner.fullname?:owner.company_name}<br/>
              с ${String.format('%td.%<tm.%<tY',owner.startdate)}<br/>
              <g:if test="${owner.fullname}">Паспортные данные: ${owner.passport} выдан ${owner.passdate} ${owner.passorg}<br/>
              Прописка: ${owner.propiska}<br/></g:if>
              Доля в процентах: ${owner.share}%<br/>
              Доля в тыс.руб: ${intnumber(value:owner.summa)}
            </td>
          </tr>
        </g:each>
        <g:each in="${owners_old}" var="owner">
          <tr>
            <td>Учредитель (недействующий)</td>
            <td>
              ${owner.fullname?:owner.company_name}<br/>
              с ${String.format('%td.%<tm.%<tY',owner.startdate)} по ${String.format('%td.%<tm.%<tY',owner.enddate)}<br/>
              <g:if test="${owner.fullname}">Паспортные данные: ${owner.passport} выдан ${owner.passdate} ${owner.passorg}<br/>
              Прописка: ${owner.propiska}<br/></g:if>
              Доля в процентах: ${owner.share}%<br/>
              Доля в тыс.руб: ${intnumber(value:owner.summa)}
            </td>
          </tr>
        </g:each>
        </g:if>
          <tr>
            <td>Генеральный директор (действующий)</td>
            <td>
            <g:if test="${general}">
              ${general.fullname}<br/>
              Работает: с ${String.format('%td.%<tm.%<tY',general.jobstart)} <g:if test="${general.gd_valid}">по ${String.format('%td.%<tm.%<tY',general.gd_valid)} (${(general.gd_valid-general.jobstart)/30 as Integer} мес.)</g:if><br/>
              Паспортные данные: ${general.passport} выдан ${general.passdate} ${general.passorg}<br/>
              Прописка: ${general.propiska}<br/>
              Гражданство: ${general.citizen}<br/>
              Образование: ${general.education}<br/>
              Работа в отрасли: ${general.industrywork}<br/>
              Пред. работа: ${general.prevwork}
            </g:if>
            </td>
          </tr>
        <g:each in="${general_old}">
          <tr>
            <td>Генеральный директор (недействующий)</td>
            <td>
              ${it.fullname}<br/>
              Работает: с ${String.format('%td.%<tm.%<tY',it.jobstart)} <g:if test="${it.gd_valid}">по ${String.format('%td.%<tm.%<tY',it.gd_valid)} (${(it.gd_valid-it.jobstart)/30 as Integer} мес.)</g:if><br/>
              Паспортные данные: ${it.passport} выдан ${it.passdate} ${it.passorg}<br/>
              Прописка: ${it.propiska}<br/>
              Гражданство: ${it.citizen}
            </td>
          </tr>
        </g:each>
          <tr>
            <td>Главный бухгалтер (действующий)</td>
            <td>
            <g:if test="${gb}">
              ${gb.fullname}<br/>
              Работает: с ${String.format('%td.%<tm.%<tY',gb.jobstart)} <g:if test="${gb.gd_valid}">по ${String.format('%td.%<tm.%<tY',gb.gd_valid)} (${(gb.gd_valid-gb.jobstart)/30 as Integer} мес.)</g:if><br/>
              Паспортные данные: ${gb.passport} выдан ${gb.passdate} ${gb.passorg}<br/>
              Прописка: ${gb.propiska}<br/>
              Гражданство: ${gb.citizen}
            </g:if><g:else>
            <g:each in="${gboutsources}" var="gb">
              Договор бух.обслуживания: ${gb.ecompany_name}, договор ${gb.anumber} от ${String.format('%td.%<tm.%<tY',gb.adate)}<br/>
            </g:each>
            </g:else>
            </td>
          </tr>
        <g:each in="${gb_old}" var="gb">
          <tr>
            <td>Главный бухгалтер (недействующий)</td>
            <td>
              ${gb.fullname}<br/>
              Работает: с ${String.format('%td.%<tm.%<tY',gb.jobstart)} <g:if test="${gb.gd_valid}">по ${String.format('%td.%<tm.%<tY',gb.gd_valid)} (${(gb.gd_valid-gb.jobstart)/30 as Integer} мес.)</g:if><br/>
              Паспортные данные: ${gb.passport} выдан ${gb.passdate} ${gb.passorg}<br/>
              Прописка: ${gb.propiska}<br/>
              Гражданство: ${gb.citizen}
            </td>
          </tr>
        </g:each>
          <g:each in="${accounts}">
            <tr>  
              <td colspan="2" align="center" style="font-size:12pt">${it?.bankname?:''}<g:if test="${it.prevnameinfo}"><br/>(бывш. ${it.prevnameinfo})</g:if></td>           
            </tr>                                    
            <tr>  
              <td>Открыт/закрыт</td>
              <td>${it?.opendate?(' Открыт '+String.format('%td.%<tm.%<tY',it?.opendate)):''} ${it?.closedate?(' /закрыт '+String.format('%td.%<tm.%<tY',it?.closedate)):''}  ${(!it?.is_license && it?.stopdate)?(' /отозвана лицензия '+String.format('%td.%<tm.%<tY',it?.stopdate)):''}</td>
            </tr>
            <tr>  
              <td>${Typeaccount.get(it?.typeaccount_id?:0)?.name?:''}</td>
              <td>${g.account(value:it?.schet)} ${it.valuta_id==857?'в рублях':(it.valuta_id==840?'в долларах':(it.valuta_id==978)?'в евро':'')}</td>
            </tr>
            <tr>  
              <td>Корреспондентский счет</td>
              <td>${Bank.get(it?.bank_id)?.coraccount?:''}</td>
            </tr>
            <tr>  
              <td>БИК</td>
              <td>${it?.bank_id}</td>
            </tr>
            <tr>  
              <td>Банк получателя</td>
              <td>${it?.bankname?:''}</td>
            </tr>
            <tr>  
              <td>Директор</td>
              <td>${Pers.get(it?.pers_id?:0)?.shortname?:''}<g:if test="${it.directordate}">&nbsp;(${String.format('%td.%<tm.%<tY',it.directordate)})</g:if></td>
            </tr>
          </g:each>          
        </tbody>
      </table>
    </div>
  </g:else>
  </body>
</html>
