class Payment {
  def searchService

  static mapping = {
    version false
  }
  static constraints = {
    id1c(unique:true)
    paydate(nullable:true)
    paycat(nullable:true)
    summaold(nullable:true)
    moddate(nullable:true)
    file_id(nullable:true)
  }

  public static final PAY_TYPE_EXPORT=1
  public static final PAY_TYPE_IMPORT=2
  public static final PAY_CAT_AGR=1
  public static final PAY_CAT_BUDG=2
  public static final PAY_CAT_PERS=3
  public static final PAY_CAT_OTHER=4
  public static final PAY_CAT_BANK=5
  public static final PAY_CAT_ORDER=6

  Integer id
  String platnumber
  Date paydate
  Date inputdate
  BigDecimal summa
  BigDecimal summands
	BigDecimal summaold=0d
  String fromcompany
  String frominn
  String fromkpp
  Integer fromcompany_id = 0
  String frombank
  String frombankbik
  String fromaccount
  String fromcorraccount
  String tocompany = ''
  Integer tocompany_id =0
  String toinn
  String tokpp
  String tobank
  String tobankbik
  String toaccount
  String tocorraccount
  String kbk = ''
  String platperiod = ''
  Integer paytype = 0
  Integer paycat = 0
  Integer agreementtype_id = 0
  Integer agreement_id = 0
  Integer is_dop = 0
  Integer is_fine = 0
  Long pers_id = 0
  String agreementnumber = ''
  Integer payrequest_id = 0
  String destination
  Integer modstatus = 1
  Integer kredit_id = 0
  String comment = ''
  String oktmo  
  String ptype 
  String id1c
  String tagcomment = ''
  Integer is_internal = 0
  Integer client_id = 0
  Integer subclient_id = 0
  Integer project_id = 0
  Integer expensetype_id = 0
	Date moddate
	Integer finstatus = 0
  Integer kbkrazdel_id = 0
  Integer agentagr_id = 0
  Integer agent_id = 0
  Integer tagstatus = 0
  Long file_id = 0
  Integer car_id = 0
  Integer is_error = 0
  Integer is_third = 0
  Integer is_bankmoney = 0

  def beforeUpdate(){
    if((isDirty('summa')||isDirty('is_dop'))&&payrequest_id>0) Payrequest.withNewSession{ Payrequest.get(payrequest_id)?.updatesumma(summa)?.updateisDop(is_dop)?.save() }
  }
 ////////////////////////////////////////////////////////////
  Payment linkPayment(_xmlNode){   
    id1c = _xmlNode.@НомерПлатежа1С.text()
    
    if(_xmlNode.ТипПлатежа.text()=='Исходящий')
      paytype=1
    else if(_xmlNode.ТипПлатежа.text()=='Входящий')
      paytype=2              
    
    paydate = Tools.getDate(_xmlNode.Дата.text())
    
    try{
      summa = (_xmlNode.Сумма.text().replace(',','.')?:'0').toBigDecimal()
      summands = (_xmlNode.СуммаНДС.text().replace(',','.')?:'0').toBigDecimal()
    }catch (Exception e){
      log.debug("Payment linkPayment: Exception: " + e.toString());
    }  

    platnumber = _xmlNode.НомерПлатежа.text()
    
    fromcompany = _xmlNode.Плательщик.text()            
    frominn = _xmlNode.ИННПлательщика.text()
    fromkpp = _xmlNode.КПППлательщика.text()
    frombank = _xmlNode.БанкПлательщика.text()
    frombankbik = _xmlNode.БИКБанкПлательщика.text()
    fromcorraccount = _xmlNode.КорСчетБанкПлательщика.text()
    fromaccount = _xmlNode.РасчетныйСчетПлательщика.text()
    tocompany = _xmlNode.Получатель.text()
    toinn = _xmlNode.ИННПолучателя.text()
    tokpp = _xmlNode.КПППолучателя.text()
    
    if(!tocompany && !toinn && !tokpp){
      tocompany = fromcompany
      toinn = frominn
      tokpp = fromkpp
    }           
    
    tobank = _xmlNode.БанкПолучателя.text()
    tobankbik = _xmlNode.БИКБанкПолучателя.text()
    tocorraccount = _xmlNode.КорСчетБанкПолучателя.text()
    toaccount = _xmlNode.РасчетныйСчетПолучателя.text()
    kbk = _xmlNode.КБК.text()
    ptype = _xmlNode.ВидПлатежа.text()
    oktmo = _xmlNode.ОКТМОПлательщика.text()
    if(_xmlNode.children().find{it.name()=='НалоговыйПериод'}?true:false)
      platperiod = _xmlNode.НалоговыйПериод.text()
    destination = _xmlNode.Назначение.text()   

    inputdate = new Date()   
    
    moddate=new Date()			
   
    this
  }

  Payment csiUpdatePayment(_xmlNode){
    try{
      summa = (_xmlNode.Сумма.text().replace(',','.')?:'0').toBigDecimal()
      summands = (_xmlNode.СуммаНДС.text().replace(',','.')?:'0').toBigDecimal()
    }catch (Exception e){
      log.debug("Payment csiUpdatePayment: Exception: " + e.toString())
    }

    platnumber = _xmlNode.НомерПлатежа.text()
    paydate = Tools.getDate(_xmlNode.Дата.text())
    if(_xmlNode.children().find{it.name()=='НалоговыйПериод'}?true:false)
      platperiod = _xmlNode.НалоговыйПериод.text()
    destination = destination?:_xmlNode.Назначение.text()

		moddate = new Date()

    if(summa!=summaold)
      finstatus = 0

    this
  }

  def csiSetPayment(hsInrequest, bIsError){
    if(bIsError){
      fromcompany = hsInrequest.fromcompany_main?:''
      frominn = hsInrequest.frominn_main?:''
      frombank = hsInrequest.frombank_main?:''
      frombankbik = hsInrequest.frombankbik_main?:''
      fromcorraccount = hsInrequest.fromcorraccount_main?:''
      fromaccount = hsInrequest.fromaccount_main?:''
      oktmo = hsInrequest.oktmo_main?:''
      tocompany = hsInrequest.tocompany_main?:''
      toinn = hsInrequest.toinn_main?:''
      tobank = hsInrequest.tobank_main?:''
      tobankbik = hsInrequest.tobankbik_main?:''
      tocorraccount = hsInrequest.tocorraccount_main?:''
      toaccount = hsInrequest.toaccount_main?:''
      is_error = 1
    } else if(payrequest_id==0) csiSetPayment(hsInrequest)
    this
  }

  def csiSetPayment(hsInrequest){
    fromcompany_id=Company.findByInnAndModstatus(frominn,1)?.id?:0
    tocompany_id=Company.findByInnAndModstatus(toinn,1)?.id?:0
    destination = hsInrequest?.destination?:''

    if(Company.get(fromcompany_id?:0)?.is_holding && Company.get(tocompany_id?:0)?.is_holding)
      is_internal = 1
    else
      is_internal = 0

    paycat = hsInrequest?.paycat?:paycat
    is_dop = 0
    is_third = hsInrequest?.is_third?:0

    if(paycat==Payment.PAY_CAT_AGR){
      agreement_id=hsInrequest?.agreement_id?:0
      agreementtype_id=hsInrequest?.agreementtype_id?:0

      def oAgreement

      switch(agreementtype_id){
        case 1: oAgreement=License.get(agreement_id?:0); break
        case 2: oAgreement=Space.get(agreement_id?:0); is_dop = hsInrequest?.is_dopmain?:0; break
        case 3: oAgreement=Kredit.get(agreement_id?:0); is_dop = hsInrequest?.is_dop?:0; is_fine = hsInrequest?.is_fine?:0; break
        case 4: oAgreement=Lizing.get(agreement_id?:0); break
        case 5: oAgreement=Agentagr.get(agreement_id?:0); break
        case 6: oAgreement=Cession.get(agreement_id?:0); break
        case 7: oAgreement=Trade.get(agreement_id?:0); break
        case 8: oAgreement=Service.get(agreement_id); break
        case 9: oAgreement=Smr.get(agreement_id); break
        case 10: oAgreement=Loan.get(agreement_id); break
        case 11: oAgreement=Bankdeposit.get(agreement_id); is_dop = hsInrequest?.is_dop?:0; break
        case 12: oAgreement=Finlizing.get(agreement_id); is_dop = hsInrequest?.is_com?:0; break
        case 13: oAgreement=Indeposit.get(agreement_id); break
      }

      if(oAgreement)
        agreementnumber=oAgreement.instanceOf(Agentagr)?(oAgreement?.name):(oAgreement?.anumber +' от '+String.format('%td.%<tm.%<tY',oAgreement.adate))
    } else {
      agreement_id = 0
      agreementtype_id = 0
      is_fine = 0
      agreementnumber = ''
    }

    if(paycat==Payment.PAY_CAT_PERS){
      pers_id=hsInrequest?.pers_id?:0
      toaccount = hsInrequest.card==-1?toaccount:Persaccount.findByPers_idAndIs_mainAndModstatus(pers_id,hsInrequest.card?:0,1)?.paccount?:toaccount
      is_dop = hsInrequest?.is_persdop?:0
    } else pers_id = 0

    if(paycat==Payment.PAY_CAT_BUDG)
      kbkrazdel_id=hsInrequest?.kbkrazdel_id?:0
    else kbkrazdel_id = 0

    if(paycat==Payment.PAY_CAT_OTHER)
      comment=hsInrequest?.comment?:''
    else
      comment = ''

    moddate=new Date()

    this
  }

  Payment csiSetPayrequestId(iId){
    payrequest_id = iId?:0
    this
  }

  Payment csiSetFileId(iFileId){
    file_id = iFileId?:file_id
    this
  }

  Payment csiSetPaymentTag(hsInrequest,bIsCanTag){
    if(bIsCanTag){
      project_id = hsInrequest?.project_id?:0
      expensetype_id = hsInrequest?.expensetype_id?:0
      car_id = !Expensetype.get(expensetype_id)?.is_car?0:hsInrequest.car_id?:0
      tagcomment = hsInrequest?.tagcomment?:''
      client_id = hsInrequest?.client_id?:0
      subclient_id = hsInrequest?.subclient_id?:0
      is_bankmoney = hsInrequest.is_bankmoney?:0
    }
    this
  }

  Payment csiSetPayrequestTag(hsInrequest){
    csiSetPayrequestTag(hsInrequest,true)
  }

  String collectInfodata(){
    "Дата платежа:${String.format('%td.%<tm.%<tY',paydate)}\nПлательщик:$fromcompany\nИНН Плательщика:$frominn\nПолучатель:$tocompany\nИНН получателя:$toinn\nСумма платежа:$summa\nНазначение платежа:$destination"
  }

  def csiSelectPayment(hsInrequest,iMax,iOffset,iVision=0){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from="payment"
    hsSql.where="1=1"+
      ((hsInrequest?.fromcompany)?' AND fromcompany like concat("%",:fromcompany,"%")':'')+
      ((hsInrequest?.tocompany)?' AND tocompany like concat("%",:tocompany,"%")':'')+
      ((hsInrequest?.destination)?' AND destination like concat("%",:destination,"%")':'')+
      ((hsInrequest?.frombank)?' AND frombank like concat("%",:frombank,"%")':'')+
      (hsInrequest?.pid>0?' AND id=:pid':'')+
      ((hsInrequest?.paycat>0)?' AND paycat=:paycat':'')+
      ((hsInrequest?.paytype>0)?' AND paytype=:paytype':'')+
      ((hsInrequest?.modstatus>-100)?' AND modstatus=:modstatus':'')+
      ((hsInrequest?.pers_id>0)?' AND pers_id=:pers_id':'')+
      ((hsInrequest?.paydate)?' AND paydate=:paydate':'')+
      ((hsInrequest?.platnumber?.size())?' AND (platnumber=:platnumber OR id1c=:platnumber)':'')+
      ((hsInrequest?.kbk?.size())?' AND kbk=:kbk':'')+
      ((hsInrequest?.finstatus>-1)?' AND finstatus=:finstatus':'')+
      ((hsInrequest?.tagstatus>-1)?' AND tagstatus=:tagstatus':'')+
      ((hsInrequest?.client_id>0)?' AND client_id=:client_id':'')+
      ((hsInrequest?.is_fact)?' AND modstatus=2 and (payrequest_id=0 OR (client_id=0 and expensetype_id=0)) and (is_internal=0 OR paytype=1)':'')+
      ((hsInrequest?.is_dest)?' AND destination!=""':'')+
      ((hsInrequest?.is_bankmoney)?' AND is_bankmoney=1':'')+
      (hsInrequest?.internal==1?' AND is_internal=0':hsInrequest?.internal==2?' AND is_internal=1':'')+
      (hsInrequest?.summa>0?' AND summa>=:summa_min and summa<:summa_max':hsInrequest?.strsumma=='0'?' AND summa=0':'')+
      (hsInrequest?.for_creation==1?' AND payrequest_id=0 AND (is_internal != 1 OR paytype != 2)':'')+
      ((hsInrequest?.platperiod_year)?((hsInrequest?.platperiod_month)?' AND year(paydate)=:platyear and month(paydate)=:platmonth':' AND year(paydate)=:platyear'):'')+
      (iVision>0?' and ((fromcompany_id=0 and tocompany_id=0) or IFNULL((select visualgroup_id from company where company.id=payment.fromcompany_id and is_holding=1),0)=:visualgroup_id or IFNULL((select visualgroup_id from company where company.id=payment.tocompany_id and is_holding=1),0)=:visualgroup_id)':'')

    hsSql.order="id desc"

    if(hsInrequest?.fromcompany)
      hsString['fromcompany'] = hsInrequest.fromcompany
    if(hsInrequest?.tocompany)
      hsString['tocompany'] = hsInrequest.tocompany
    if(hsInrequest?.destination)
      hsString['destination'] = hsInrequest.destination
    if(hsInrequest?.frombank)
      hsString['frombank'] = hsInrequest.frombank
    if(hsInrequest?.pid>0)
      hsLong['pid'] = hsInrequest.pid
    if(hsInrequest?.paycat>0)
      hsLong['paycat'] = hsInrequest.paycat
    if(hsInrequest?.paytype>0)
      hsLong['paytype'] = hsInrequest.paytype
    if(hsInrequest?.modstatus>-100)
      hsLong['modstatus'] = hsInrequest.modstatus
    if(hsInrequest?.finstatus>-1)
      hsLong['finstatus'] = hsInrequest.finstatus
    if(hsInrequest?.tagstatus>-1)
      hsLong['tagstatus'] = hsInrequest.tagstatus
    if(hsInrequest?.summa>0){
      hsLong['summa_min'] = hsInrequest.summa
      hsLong['summa_max'] = hsInrequest.summa+1
    }

    if(hsInrequest?.pers_id>0)
      hsLong['pers_id'] = hsInrequest.pers_id

    if(hsInrequest?.paydate)
      hsString['paydate'] = String.format('%tF',hsInrequest.paydate)
    if(hsInrequest?.platnumber?.size())
      hsString['platnumber'] = hsInrequest.platnumber
    if(hsInrequest?.kbk?.size())
      hsString['kbk'] = hsInrequest.kbk

    if(hsInrequest?.client_id>0)
      hsLong['client_id'] = hsInrequest.client_id
    if(hsInrequest?.platperiod_year){
      hsLong['platyear'] = hsInrequest.platperiod_year
      if(hsInrequest?.platperiod_month)
        hsLong['platmonth'] = hsInrequest.platperiod_month
    }
    if(iVision>0)
      hsLong['visualgroup_id'] = iVision

    searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,null,null,iMax,iOffset,'id',true,Payment.class)
  }
}