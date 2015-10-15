import grails.converters.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import pl.touk.excel.export.WebXlsxExporter

class PaymentController {
  def requestService
  def paymentService
  def imageService

  final String PPAYPLAN = 'is_payplan'
  final String PPLANEDIT = 'is_payplanedit'
  final String PPLANTASK = 'is_payplantask'
  final String PCLIENTPAY = 'is_clientpayment'
  final String PCLPAYEDIT = 'is_clientpaymentedit'
  final String PCLPAYNEW = 'is_clientpaynew'
  final String PPAYDEL = 'is_payrequestdelete'
  final String PPAYTAG = 'is_paytag'
  final String PPAYEDIT = 'is_payedit'
  final String PPAYNAL = 'is_paynalog'
  final String PPNALEDIT = 'is_paynalogedit'
  final String PTPAY = 'is_payt'
  final String PTPAYEDIT = 'is_paytedit'
  final String PPRJPAY = 'is_payproject'
  final String PDPCPAY = 'is_dopcardpayment'
  final String PPORDER = 'is_payordering'
  final String PPSALDO = 'is_paysaldo'
  final String PPPEXEC = 'is_payplanexec'

  def beforeInterceptor = [action:this.&checkUser]

  def checkUser() {
    if(session?.user?.id!=null){
      def oTemp_notification=Temp_notification.findWhere(id:1,status:1)
      session.attention_message=oTemp_notification?oTemp_notification.text:null
    }else{
      response.sendError(401)
      return false;
    }
  }

  def checkAccess(iActionId){
    def bDenied = true
    if(session?.user)
      session.user.menu.each{
	      if (iActionId==it.id) bDenied = false
	    }
    if (bDenied) {
      response.sendError(403)
      return
	  }
  }

  private Boolean checkSectionPermission(String sField) {
    checkSectionPermission([sField])
  }

  private Boolean checkSectionPermission(lsField) {
    if(!lsField.find{ session.user.group?."$it" }) {
      response.sendError(403)
      return false;
    }
    return true
  }

  private Boolean recieveSectionPermission(String sField) {
    recieveSectionPermission([sField])
  }

  private Boolean recieveSectionPermission(lsField) {
    lsField.find{ session.user.group?."$it" } as Boolean
  }

  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Main >>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def index = {
    checkAccess(10)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails&&session.paymentlastRequest){
      session.paymentlastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.paymentlastRequest
    } else {
      hsRes+=requestService.getParams(['fromcompany_id','paymentobject'])
    }
    hsRes.isclientpayment = recieveSectionPermission(PCLIENTPAY)
    hsRes.isbudgpayment = recieveSectionPermission(PPAYNAL)
    hsRes.istpayment = recieveSectionPermission(PTPAY)
    hsRes.isdpcpayment = recieveSectionPermission(PDPCPAY)
    hsRes.ispayordering = recieveSectionPermission(PPORDER)
    hsRes.ispsaldo = recieveSectionPermission(PPSALDO)

    return hsRes
  }

  def paymentfilter = {
    checkAccess(10)
    if (!checkSectionPermission(PPORDER)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.perioddate = new Date()
    if (session.paymentlastRequest?.fromDetails){
      hsRes.inrequest = session.paymentlastRequest
      hsRes.perioddate = hsRes.inrequest?.platperiod_month&&hsRes.inrequest?.platperiod_year?new Date(hsRes.inrequest.platperiod_year-1900,hsRes.inrequest.platperiod_month-1,1):hsRes.inrequest?.platperiod_year?new Date(hsRes.inrequest.platperiod_year-1900,0,1):null
    }

    return hsRes
  }    
  def paymentlist = {
    checkAccess(10)
    if (!checkSectionPermission(PPORDER)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = session.user
    hsRes.action_id = 10

    if (session.paymentlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.paymentlastRequest
      session.paymentlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['paycat','paytype','is_fact','pid','platperiod_month','platperiod_year','is_dest','internal','is_bankmoney'],
                                      ['pers_id','summa'],['platnumber','kbk','fromcompany','tocompany','destination','frombank'])
      hsRes.inrequest.paydate = requestService.getDate('paydate')
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',-100)
      hsRes.inrequest.finstatus = requestService.getIntDef('finstatus',-1)
      hsRes.inrequest.offset = requestService.getOffset()
      session.paymentlastRequest = hsRes.inrequest
    }
    session.paymentlastRequest.paymentobject = 7

    hsRes.searchresult = new Payment().csiSelectPayment(hsRes?.inrequest,20,hsRes?.inrequest?.offset)   
    return hsRes
  }

  def paymentlistXLS = {
    checkAccess(10)
    if (!checkSectionPermission(PPORDER)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = session.user
    hsRes.action_id = 10

    hsRes+=requestService.getParams(['paycat','paytype','is_fact','pid','platperiod_month','platperiod_year','is_dest','internal'],
                                    ['pers_id','summa'],['platnumber','kbk','fromcompany','tocompany','destination','frombank'])
    hsRes.inrequest.paydate = requestService.getDate('paydate')
    hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',-100)
    hsRes.inrequest.finstatus = requestService.getIntDef('finstatus',-1)
    hsRes.inrequest.offset = requestService.getOffset()

    hsRes.searchresult = new Payment().csiSelectPayment(hsRes?.inrequest,-1,hsRes?.inrequest?.offset)   

    if (hsRes.searchresult.records.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(1, 4, "Платежи по выписке")
        putCellValue(2, 4, String.format('%td.%<tm.%<tY %<tH:%<tM',new Date()))
        fillRow(['Дата платежа','Плательщик','Банк плательщика','Получатель','Банк получателя','Сумма','Исходящий тип','Входящий тип','Признак платежа','Назначение платежа'],3,false,Tools.getXlsTableHeaderStyle(10))
        hsRes.searchresult.records.eachWithIndex{ record, index ->
          fillRow([String.format('%td.%<tm.%<tY',record.paydate),
                   record.fromcompany,
                   record.frombank,
                   record.tocompany,
                   record.tobank,
                   record.summa,
                   record.paytype==1?'исходящий':'',
                   record.paytype==2?'входящий':'',
                   record.is_internal?'внутр.':'внеш.',
                   record.destination], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(10) : index == hsRes.searchresult.records.size()-1 ? Tools.getXlsTableLastLineStyle(10) : Tools.getXlsTableLineStyle(10))
        }
        save(response.outputStream)
      }
    }
    return
  }

  def paymentdetail = {
    checkAccess(10)
    if (!checkSectionPermission(PPORDER)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = session.user
    hsRes.action_id = 10

    hsRes.payment = Payment.get(requestService.getIntDef('id',0))
    if (!hsRes.payment) {
      response.sendError(404)
      return
    }

    hsRes.agrtypes = Agreementtype.findAllByIdNotEqual(5)

		hsRes.fromcompany=Company.findByInn(hsRes.payment.frominn)	
    hsRes.frombank=Bank.get(hsRes.payment.frombankbik?:'')
		
		if(hsRes.frombank)	
      hsRes.frombankaccount=Bankaccount.findByBank_idAndCompany_idAndTypeaccount_id(hsRes.payment.frombankbik,hsRes.fromcompany?.id?:0,1)    
		
		hsRes.tocompany=Company.findByInn(hsRes.payment.toinn)
		hsRes.tobank=Bank.get(hsRes.payment.tobankbik?:'')
		
		if(hsRes.tobank)
		  hsRes.tobankaccount=Bankaccount.findByBank_idAndCompany_idAndTypeaccount_id(hsRes.payment.tobankbik,hsRes.tocompany?.id?:0,1)					

    switch(hsRes.payment.agreementtype_id){
      case 1: hsRes.agr=License.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 2: hsRes.agr=Space.getAgreementsBy(hsRes.payment.is_third?0:hsRes.fromcompany?.id,hsRes.tocompany?.id); break
      case 3: hsRes.agr=Kredit.getAgreementsBy(hsRes.payment.is_third?0:hsRes.payment.paytype!=2?hsRes.fromcompany?.id:hsRes.tocompany?.id); break
      case 4: hsRes.agr=Lizing.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 6: hsRes.agr=Cession.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 7: hsRes.agr=Trade.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 8: hsRes.agr=Service.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 9: hsRes.agr=Smr.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 10: hsRes.agr=Loan.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 11: hsRes.agr=Bankdeposit.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 12: hsRes.agr=Finlizing.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 13: hsRes.agr=Indeposit.findAllByModstatusInListAndAclass(0..1,1,[sort:'anumber',order:'asc']); break
    }
    hsRes.agr?.sort{ a, b -> b.modstatus<=>a.modstatus }

    if(hsRes.payment.pers_id)
      hsRes.pers = Pers.get(hsRes.payment.pers_id)
    else if(Persaccount.findAllByBank_idAndPaccount(hsRes.payment.tobankbik,hsRes.payment.toaccount).find{ Compers.findByCompany_idAndPers_id(hsRes.fromcompany?.id?:0,it.pers_id) }) {
      hsRes.pers = Pers.get(Persaccount.findAllByBank_idAndPaccount(hsRes.payment.tobankbik,hsRes.payment.toaccount).find{ Compers.findByCompany_idAndPers_id(hsRes.fromcompany?.id?:0,it.pers_id) }?.pers_id?:0)
    }
    hsRes.perslist = Compers.findAllByCompany_id(hsRes.fromcompany?.id?:0).collect{ it.pers_id }.unique().collect{ Pers.get(it) }
    hsRes.card = []
    if(hsRes.payment.pers_id){
      if(Persaccount.findByPers_idAndIs_mainAndModstatus(hsRes.payment.pers_id,1,1))
        hsRes.card<<[name:"${Persaccount.findByPers_idAndIs_mainAndModstatus(hsRes.payment.pers_id,1,1)?.paccount} (основная)",id:1]
      if(Persaccount.findByPers_idAndIs_mainAndModstatus(hsRes.payment.pers_id,0,1))
        hsRes.card<<[name:"${Persaccount.findByPers_idAndIs_mainAndModstatus(hsRes.payment.pers_id,0,1)?.paccount} (дополнительная)",id:0]
      hsRes.cardvalue = hsRes.payment.toaccount==Persaccount.findByPers_idAndIs_mainAndModstatus(hsRes.payment.pers_id,1,1)?.paccount?1:hsRes.payment.toaccount==Persaccount.findByPers_idAndIs_mainAndModstatus(hsRes.payment.pers_id,0,1)?.paccount?0:-1
    }

    hsRes.project = Project.findAllByModstatus(1,[sort:'name'])
    hsRes.defproject_id = Project.findByIs_main(1)?.id?:0
    hsRes.expensetype = new ExpensetypeSearch().csiGetList('',0,-1,hsRes.payment.expensetype_id)
    hsRes.cars = Car.list(sort:'name')
    hsRes.expcar_ids = Expensetype.findAllByIs_car(1).collect{it.id}?:[]
    hsRes.client = Client.get(hsRes.payment.client_id)
    hsRes.clients = Client.findAllByModstatusAndParent(1,0)
    hsRes.subclients = Client.findAllByParentGreaterThanAndParentAndModstatus(0,hsRes.payment.client_id,1)

    hsRes.kbkrazdel=Kbkrazdel.list(sort: "name", order: "asc")

    hsRes.paycat=[[id:1,name:'договорной']]
    hsRes.paycat<<[id:2,name:'бюджетный']
    hsRes.paycat<<[id:3,name:'персональный']
    hsRes.paycat<<[id:4,name:'прочий']
    hsRes.paycat<<[id:5,name:'банковский']
    hsRes.paycat<<[id:6,name:'счета']

    hsRes.agent=Agent.findAllByModstatus(1,[sort:'name',order:'asc'])

    if(hsRes.payment.client_id)
      hsRes.agentagr=Agentagr.findAllByClient_idAndModstatus(hsRes.payment.client_id,1,[sort:'name',order:'asc'])

    hsRes.payrequest = Payrequest.get(hsRes.payment.payrequest_id)
    hsRes.ishavetask = Task.findByTasktype_idAndLink(9,hsRes.payment.id)
    hsRes.iscancreate = hsRes.payment.modstatus == 2 && hsRes.payment.payrequest_id == 0 && !(hsRes.payment.is_internal && hsRes.payment.paytype == Payment.PAY_TYPE_IMPORT) && recieveSectionPermission(PPAYEDIT)
    hsRes.iscandelete = hsRes.payrequest?.is_generate == 1 && recieveSectionPermission(PPAYEDIT)
    hsRes.iscantag = recieveSectionPermission(PPAYTAG)

    return hsRes
  }

  def agreement = {
    checkAccess(10)
    requestService.init(this)
    def hsRes = [:]
    hsRes.short_version=requestService.getIntDef('short',0)
    def iAgreementtype_id=requestService.getIntDef('agreementtype_id',0)
    def iCompany_id=requestService.getIntDef('company_id',0)
    def iCtrCompany_id=requestService.getIntDef('ctrcompany_id',0)
    def sBankBik=requestService.getStr('bank_id')

    switch(iAgreementtype_id){
      case 1: hsRes.agr=iCompany_id?License.findAllByModstatusInListAndCompany_id(0..1,iCompany_id,[sort:'anumber',order:'asc']):License.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 2: hsRes.agr=Space.getAgreementsBy(iCompany_id,iCtrCompany_id); break
      case 3: hsRes.agr=Kredit.getAgreementsBy(iCompany_id); break
      case 4: hsRes.agr=iCompany_id?Lizing.findAll{ modstatus in 0..1 && ((creditor == 0 && arendator == iCompany_id) || creditor == iCompany_id || arendodatel == iCompany_id) }:Lizing.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 5: hsRes.agr=Agentagr.findAllByModstatusInList(0..1,[sort:'name',order:'asc']); break
      case 6: hsRes.agr=iCompany_id?Cession.findAllByModstatusInListAndCessionary(0..1,iCompany_id,[sort:'anumber',order:'asc']):Cession.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 7: hsRes.agr=iCompany_id?Trade.findAll{modstatus in 0..1 && (client == iCompany_id || supplier == iCompany_id )}:Trade.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 8: hsRes.agr=iCompany_id?Service.findAll{modstatus in 0..1 && (zcompany_id == iCompany_id || ecompany_id == iCompany_id )}:Service.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 9: hsRes.agr=iCompany_id?Smr.findAll{modstatus in 0..1 && (client == iCompany_id || supplier == iCompany_id )}:Smr.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 10: hsRes.agr=iCompany_id?Loan.findAll{modstatus in 0..1 && (client == iCompany_id || lender == iCompany_id )}:Loan.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 11: hsRes.agr=Bankdeposit.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 12: hsRes.agr=Finlizing.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 13: hsRes.agr=Indeposit.findAllByModstatusInListAndAclass(0..1,1,[sort:'anumber',order:'asc']); break
    }
    hsRes.agr?.sort{ a, b -> b.modstatus<=>a.modstatus }

    return hsRes
  }

  def processPayment = {
    checkAccess(10)
    requestService.init(this)
    def hsRes = [:]
    hsRes.user = session.user
    hsRes.result=[errorcode:[]]

    if (!hsRes.user?.group?.is_payedit) {
      response.sendError(403)
      return
    }

    def oPayment = Payment.get(requestService.getIntDef('id',0))
    if (!oPayment) {
      response.sendError(404)
      return
    }

    if (!requestService.getIntDef('is_error',0))
      hsRes+=requestService.getParams(['paycat','payrequest_id','agreement_id','agreementtype_id','client_id','project_id',
                                       'is_fine','subclient_id','expensetype_id','kbkrazdel_id','agent_id','agentagr_id',
                                       'is_dop','car_id','is_error','card','is_com','is_bankmoney','is_dopmain','is_persdop'],
                                      ['pers_id'],['comment','tagcomment','destination'])
    else
      hsRes+=requestService.getParams(['is_error'],null,['fromcompany_main','frominn_main','frombank_main','frombankbik_main',
                                                 'fromcorraccount_main','fromaccount_main','oktmo_main','tocompany_main',
                                                 'toinn_main','tobank_main','tobankbik_main','tocorraccount_main','toaccount_main'])
    if (oPayment.payrequest_id>0) hsRes.inrequest.paycat = oPayment.paycat

    if(!hsRes.inrequest.is_error&&oPayment.payrequest_id==0){
      if(!hsRes.inrequest.paycat){
        hsRes.result.errorcode<<1
      }else if(hsRes.inrequest.paycat==Payment.PAY_CAT_AGR){
        if(!hsRes.inrequest.agreementtype_id)     
          hsRes.result.errorcode<<2
        if(!hsRes.inrequest.agreement_id)     
          hsRes.result.errorcode<<3 
        if(!Company.findByInnAndModstatus(oPayment?.frominn,1))  
          hsRes.result.errorcode<<4
        if(!Company.findByInnAndModstatus(oPayment?.toinn,1))  
          hsRes.result.errorcode<<5 
        if(!Bank.get(oPayment?.frombankbik))  
          hsRes.result.errorcode<<6 
        if(!Bank.get(oPayment?.tobankbik))  
          hsRes.result.errorcode<<7        
      }else if(hsRes.inrequest.paycat==Payment.PAY_CAT_BUDG){
        if(oPayment?.kbk && !hsRes.inrequest.kbkrazdel_id)
          hsRes.result.errorcode<<21 

        if(oPayment?.paytype==1){
          if(!Company.findByInnAndModstatus(oPayment?.frominn,1))  
            hsRes.result.errorcode<<4
          if(!Bank.get(oPayment?.frombankbik))  
            hsRes.result.errorcode<<6 
        }else if(oPayment?.paytype==2){
          if(!Company.findByInnAndModstatus(oPayment?.toinn,1))  
            hsRes.result.errorcode<<5         
          if(!Bank.get(oPayment?.tobankbik))  
            hsRes.result.errorcode<<7      
        }        
      }else if(hsRes.inrequest.paycat==Payment.PAY_CAT_PERS){
        if(!hsRes.inrequest.pers_id)
          hsRes.result.errorcode<<31
      }
    }

    if(hsRes.result.errorcode){
      render(contentType:"application/json"){[error:true,errorcode:hsRes.result.errorcode]}
      return
    }

    oPayment.csiSetPayment(hsRes.inrequest,hsRes.inrequest.is_error).csiSetPaymentTag(hsRes.inrequest,recieveSectionPermission(PPAYTAG)).save(flush:true,failOnError:true)

    paymentService.parsePaymentByHand(oPayment)
    flash.paymentedit_success=1

    render(contentType:"application/json"){[error:false]}
    return
  }

  def createpayrequest = {
    checkAccess(10)
    if (!checkSectionPermission(PPAYEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payment = Payment.get(requestService.getIntDef('id',0))
    if (!hsRes.payment||!(hsRes.payment.modstatus == 2 && hsRes.payment.payrequest_id == 0 && !(hsRes.payment.is_internal && hsRes.payment.paytype == Payment.PAY_TYPE_IMPORT))) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payment.csiSetPayrequestId(new Payrequest().csiSetInitiator(hsRes.user.id).createFromPayment(hsRes.payment)?.save(flush:true)?.id?:0).save(failOnError:true,flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Payment/createpayrequest\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def deletegenpayrequest = {
    checkAccess(10)
    if (!checkSectionPermission(PPAYEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payment = Payment.get(requestService.getIntDef('id',0))
    hsRes.payrequest = Payrequest.get(hsRes.payment?.payrequest_id?:0)
    if (!hsRes.payment||hsRes.payrequest?.is_generate != 1) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest.delete(failOnError:true,flush:true)
      hsRes.payment.csiSetPayrequestId(0).save(failOnError:true,flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Payment/deletegenpayrequest\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def showscan = {
    checkAccess(10)    
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = session.user        

    def photo = Picture.get(requestService.getIntDef('id',0))
    if (!photo||(requestService.getStr('code')!=Tools.generateModeParam(photo?.id))) {
      response.sendError(404)
      return
    }

    //render file: photo.filedata, contentType: 'image/jpeg' //Only from grails 2.3    
    response.contentType = photo.mimetype?:'image/jpeg'
    response.outputStream << photo.filedata
    response.flushBuffer()
  }
  
  def parsePaymentFile={
    checkAccess(10)
    if (!checkSectionPermission(PPORDER)) return
    requestService.init(this)
    def hsRes =[:]        
    try {
      hsRes.result = paymentService.importXmlDataFrom1S(request.getFile('file'))
    } catch(Exception e) {
      log.debug('Error uploading XML from 1s: '+e.toString())
      hsRes.result = 'Неверный файл'
    }
    return hsRes    
  }	
	def getBank={
	  checkAccess(10)
    requestService.init(this)
    def hsRes =[:] 	
		def sInn=requestService.getStr('inn')
		def sBank_id=requestService.getStr('bank_id')
		//def bType=requestService.getIntDef('type',0)
		def sCoraccount=requestService.getStr('coraccount')
		def sSchet=requestService.getStr('schet')
		
		if(sBank_id){		
	    hsRes.bank=Bank.get(sBank_id)
		  if(hsRes.bank && sInn){      
        def oCompany=Company.findByInnAndModstatus(sInn,1)
        if(oCompany){
          hsRes.bankaccount=Bankaccount.findByBank_idAndCompany_idAndModstatusAndTypeaccount_id(sBank_id,oCompany?.id,1,1)
          if(!hsRes.bankaccount && sSchet){
            hsRes.bankaccount=new Bankaccount(company_id:oCompany.id).setData(bank_id:sBank_id,coraccount:sCoraccount,schet:sSchet,account_adate:new Date(),valuta_id:sSchet[5..7]=='810'?857:sSchet[5..7]=='840'?840:978,typeaccount_id:sSchet[5..7]=='810'?1:3).csiSetIbankblock(hsRes.bank.is_license?0:1).updateIbankstatus().save(flush:true)
          }
				}
			}
	  }
		render hsRes as JSON
    return
  }
  def getCompany={
	  checkAccess(10)
    requestService.init(this)
    def hsRes =[:] 	
		def sInn=requestService.getStr('inn')
		
		if(sInn){
		  def oCompany=Company.findByInnAndModstatus(sInn,1)
			if(oCompany){			  
			  render(contentType:"application/json"){[error:false,id:oCompany?.id,name:oCompany?.name,oktmo:oCompany?.oktmo]}
        return
			}  
		}
		render(contentType:"application/json"){[error:true]}
    return	  
	}	  
  def getPers={
    checkAccess(10)
    requestService.init(this)
    def hsRes =[:] 	
		def sPaccount=requestService.getStr('paccount')
    def sBankBik=requestService.getStr('bik')
    def iCompany_id=requestService.getIntDef('company_id',0)
		
		if(sPaccount && sBankBik && iCompany_id){      
      def lsPersaccount=Persaccount.findAllByBank_idAndPaccount(sBankBik,sPaccount)
      for(oPersacount in lsPersaccount){
        if(Compers.findByCompany_idAndPers_id(iCompany_id,oPersacount?.pers_id?:0)){
          hsRes.pers=Pers.get(oPersacount.pers_id)
        }
      }
    }
    render hsRes as JSON
    return
  }
  def setSaldo={
    checkAccess(10)
    if (!checkSectionPermission(PPAYEDIT)) return
    requestService.init(this)    	
		def iId=requestService.getIntDef('id',0)    
		def result=0
    
    def oPayment=Payment.get(iId)    
		if(oPayment){
      result=paymentService.csiSetSaldo(iId) 
    }
    if(result!=1)
      log.debug('PaymentController::setSaldo not done for Payment id='+iId)
      
    render(contentType:"application/json"){[result:result]}
    return
  }
  def setSaldoAll={
    checkAccess(10)
    if (!checkSectionPermission(PPAYEDIT)) return
    requestService.init(this)    	
		def iId=requestService.getIntDef('id',0)    
    
    def result=paymentService.csiSetSaldoAll()     
         
    render(contentType:"application/json"){[result:result]}
    return
  }  
  def agentagrbyclient={
    checkAccess(10)
    requestService.init(this)    	
		def iClientId=requestService.getIntDef('client_id',0)
    
    def hsRes=[:]
    
    if(iClientId)
      hsRes.agentagr=Agentagr.findAllByClient_idAndModstatus(iClientId,1,[sort:'name',order:'asc'])
      
    return hsRes
  }  
  def deletePayment={
    checkAccess(10)
    if (!checkSectionPermission(PPAYEDIT)) return
    requestService.init(this)    	
		def iId=requestService.getIntDef('id',0)
    
    def oPayment=Payment.get(iId)
    
    if(oPayment){
      oPayment.delete()      
    }
    
    render(contentType:"application/json"){[error:false]}
    return
  }

  def createpayrequestAll = {
    checkAccess(10)
    if (!checkSectionPermission(PPAYEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes+=requestService.getParams(['paycat','paytype','is_fact','pid','platperiod_month','platperiod_year','is_dest','internal','is_bankmoney'],
                                    ['pers_id','summa'],['platnumber','kbk','fromcompany','tocompany','destination','frombank'])
    hsRes.inrequest.paydate = requestService.getDate('paydate')
    hsRes.inrequest.modstatus = 2
    hsRes.inrequest.for_creation = 1
    hsRes.inrequest.finstatus = requestService.getIntDef('finstatus',-1)

    hsRes.searchresult = new Payment().csiSelectPayment(hsRes?.inrequest,-1,0)
    try {
      new Payment().csiSelectPayment(hsRes?.inrequest,-1,0).records.each{ payment ->
        Payment.withNewTransaction {
          payment.lock()
          payment.refresh()
          if (payment.payrequest_id == 0) payment.csiSetPayrequestId(new Payrequest().csiSetInitiator(hsRes.user.id).createFromPayment(payment)?.save(failOnError:true,flush:true)?.id?:0).save(failOnError:true,flush:true)
        }
      }
    } catch(Exception e) {
      log.debug("Error save data in Payment/createpayrequestAll\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
///////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////payrequest>>> ////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////   
   def payrequestfilter = {
    checkAccess(10)
    if (!checkSectionPermission(PPAYPLAN)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    if (session.paymentlastRequest?.fromDetails){
      hsRes.inrequest = session.paymentlastRequest
    } else {
      hsRes.inrequest=[:]
      hsRes.inrequest.companyname = Company.get(requestService.getIntDef('fromcompany_id',0))?.name?:''
    }

    hsRes.iscanedit = recieveSectionPermission(PPLANEDIT)
    hsRes.iscantask = recieveSectionPermission(PPLANTASK)

    return hsRes
  }

  def payrequestlist = {
    checkAccess(10)
    if (!checkSectionPermission(PPAYPLAN)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    if (session.paymentlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.paymentlastRequest
      session.paymentlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['paytype','platperiod_month','platperiod_year','is_notag','pid','project_id'],null,['companyname'])
      hsRes.inrequest.is_noclient = requestService.getIntDef('is_noclient',0)
      hsRes.inrequest.is_noinner = requestService.getIntDef('is_noinner',0)
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.instatus = requestService.getIntDef('instatus',0)
      hsRes.inrequest.paydate = requestService.getDate('paydate')
      hsRes.inrequest.offset = requestService.getOffset()
      session.paymentlastRequest = hsRes.inrequest
    }
    session.paymentlastRequest.paymentobject = 1

    hsRes.searchresult = new Payrequest().csiSelectPayrequest(hsRes?.inrequest,20,hsRes?.inrequest?.offset)
    hsRes.taxes = Tax.list().inject([:]){map, tax -> map[tax.id]=tax.shortname;map}
    hsRes.agrtypes = Agreementtype.list().inject([:]){map, type -> map[type.id]=type.name2;map}
    hsRes.exptypes = hsRes.searchresult.records.inject([:]){map, prequest -> map[prequest.id]=Expensetype.get(prequest.expensetype_id);map}
    hsRes.iscantag = recieveSectionPermission(PPAYTAG)

    return hsRes
  }

  def deletepayrequest = {
    checkAccess(10)
    if (!checkSectionPermission(PPLANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if ((hsRes.payrequest?.modstatus>2||hsRes.payrequest?.paytype!=2)&&(hsRes.payrequest?.modstatus!=0||hsRes.payrequest?.client_id>0)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest.delete(failOnError:true,flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Payment/deletepayrequest\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def createTaskpay = {
    checkAccess(10)
    if (!checkSectionPermission(PPLANTASK)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes+=requestService.getParams(['creating_strategy'],null,null,['term'])
    hsRes.inrequest.ids=requestService.getList('payrequest_ids',1)

    hsRes.result=[errorcode:[]]
    if(!hsRes.inrequest.term)
      hsRes.result.errorcode<<1
    else if(!(hsRes.inrequest.term).matches('\\d{2}\\.\\d{2}\\.\\d{4}'))
      hsRes.result.errorcode<<2

    if(hsRes.result.errorcode){
      render hsRes.result as JSON
      return
    }

    if (hsRes.inrequest.ids){
      (1..4).each{ paygroup ->
        def lsPayreq = Payrequest.findAllByIdInListAndModstatusAndPaytypeInListAndPaygroup(hsRes.inrequest.ids,0,[1,3,8],paygroup)
        if(lsPayreq){
          //deprecate. use creating_strategy parametr to determinate grouping option. 0 - don`t use grouping. 1 - group by bankaccount_id
          lsPayreq.groupBy{hsRes.inrequest.creating_strategy?it.bankaccount_id:it.id}.each{ lsReq ->
            def oTaskpay = new Taskpay(paygroup:paygroup).csiSetTaskpay([term:hsRes.inrequest.term,company_id:lsReq.value[0].fromcompany_id,summa:lsReq.value.sum{it.summa}]).csiSetBankaccountId(lsReq.value[0].bankaccount_id).csiSetInitiator(hsRes.user.id).save(flush:true,failOnError:true)
            lsReq.value.each{ it.csiSetTaskpay_id(oTaskpay.id).csiSetModstatus(1).save(flush:true,failOnError:true) }
          }
        }
      }
    }

    render hsRes.result as JSON
    return
  }

  def payrequestdetail = {
    checkAccess(10)
    if (!checkSectionPermission(PPLANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (!hsRes.payrequest) {
      response.sendError(404)
      return
    }
    hsRes.tag=requestService.getIntDef('tag',0)

    hsRes.company_id = hsRes.payrequest.paytype!=2?hsRes.payrequest.fromcompany_id:hsRes.payrequest.tocompany_id
    switch(hsRes.payrequest.agreementtype_id){
      case 1: hsRes.agr=hsRes.company_id?License.findAllByModstatusInListAndCompany_id(0..1,hsRes.company_id,[sort:'anumber',order:'asc']):License.list([sort:'anumber',order:'asc']); break
      case 2: hsRes.agr=Space.getAgreementsBy(hsRes.payrequest.is_third?0:hsRes.payrequest.fromcompany_id,hsRes.payrequest.tocompany_id); break
      case 3: hsRes.agr=Kredit.getAgreementsBy(hsRes.payrequest.is_third?0:hsRes.payrequest.paytype!=2?hsRes.payrequest.fromcompany_id:hsRes.payrequest.tocompany_id); break
      case 4: hsRes.agr=hsRes.company_id?Lizing.findAll{ modstatus in 0..1 && ((creditor == 0 && arendator == hsRes.company_id) || creditor == hsRes.company_id || arendodatel == hsRes.company_id) }:Lizing.findAllByModstatus(1,[sort:'anumber',order:'asc']); break
      case 5: hsRes.agr=Agentagr.findAllByModstatusInList(0..1,[sort:'name',order:'asc']); break
      case 6: hsRes.agr=hsRes.company_id?Cession.findAllByModstatusAndCessionary(1,hsRes.company_id,[sort:'anumber',order:'asc']):Cession.findAllByModstatus(1,[sort:'anumber',order:'asc']); break
      case 7: hsRes.agr=hsRes.company_id?Trade.findAll{modstatus in 0..1 && (client == hsRes.company_id || supplier == hsRes.company_id )}:Trade.findAllByModstatus(1,[sort:'anumber',order:'asc']); break
      case 8: hsRes.agr=hsRes.company_id?Service.findAll{modstatus in 0..1 && (zcompany_id == hsRes.company_id || ecompany_id == hsRes.company_id )}:Service.findAllByModstatus(1,[sort:'anumber',order:'asc']); break
      case 9: hsRes.agr=Smr.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 10: hsRes.agr=Loan.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 11: hsRes.agr=Bankdeposit.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 12: hsRes.agr=Finlizing.findAllByModstatusInList(0..1,[sort:'anumber',order:'asc']); break
      case 13: hsRes.agr=Indeposit.findAllByModstatusInListAndAclass(0..1,1,[sort:'anumber',order:'asc']); break
    }
    hsRes.agr?.sort{ a, b -> b.modstatus<=>a.modstatus }

		hsRes.fromcompany=Company.findByInnAndModstatus(hsRes.payrequest.frominn,1)	
		
		hsRes.tocompany=Company.findByInnAndModstatus(hsRes.payrequest.toinn,1)
		hsRes.tobank=Bank.findByIdAndIs_license(hsRes.payrequest.tobankbik?:'',1)
		
		if(hsRes.tobank)
		  hsRes.tobankaccount=Bankaccount.findByBank_idAndCompany_idAndModstatusAndTypeaccount_id(hsRes.payrequest.tobankbik,hsRes.tocompany?.id?:0,1,1)							

    if(hsRes.payrequest.paycat==Payment.PAY_CAT_PERS)
      hsRes.pers = Persaccount.findAllByBank_idAndPaccountAndModstatus(hsRes.payrequest.tobankbik,hsRes.payrequest.toaccount,1).collect{Pers.get(it.pers_id)}

    hsRes.agrtypes = Agreementtype.findAllByIdNotEqual(5)
    hsRes.tax = Tax.list(sort: "name", order: "asc")
    hsRes.project = Project.findAllByModstatus(1,[sort:'name'])
    hsRes.defproject_id = Project.findByIs_main(1)?.id?:0
    hsRes.expensetype = new ExpensetypeSearch().csiGetList('',0,-1,hsRes.payrequest.expensetype_id)
    hsRes.cars = Car.list(sort:'name')
    hsRes.expcar_ids = Expensetype.findAllByIs_car(1).collect{it.id}?:[]

    hsRes.agent = Agent.get(hsRes.payrequest.agent_id)
    hsRes.client = Client.get(hsRes.payrequest.client_id)
    hsRes.clients = Client.findAllByModstatusAndParent(1,0)
    hsRes.subclients = Client.findAllByParentGreaterThanAndParentAndModstatus(0,hsRes.payrequest.client_id,1)

    hsRes.iscansetrefill = hsRes.payrequest.paytype==1&&hsRes.payrequest.agreementtype_id==3&&Kredit.get(hsRes.payrequest.agreement_id)?.is_real==1
    hsRes.iscantag = recieveSectionPermission(PPAYTAG)
    hsRes.iscandecline = recieveSectionPermission(PPLANTASK)&&hsRes.payrequest.modstatus==0&&hsRes.payrequest.paycat==4
    hsRes.iscanrestore = recieveSectionPermission(PPLANTASK)&&hsRes.payrequest.modstatus==-1

    return hsRes
  }

  def receiveprincome = {
    checkAccess(10)
    if (!checkSectionPermission([PPLANEDIT,PCLPAYEDIT])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (hsRes.payrequest?.instatus!=1) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest.receiveincome(requestService.getDate('indate')?:new Date()).save(failOnError:true,flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Payment/receiveprincome\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def cancelreceiveprincome = {
    checkAccess(10)
    if (!checkSectionPermission([PPLANEDIT,PCLPAYEDIT])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (hsRes.payrequest?.instatus!=2) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest.cancellreceiveincome().save(failOnError:true,flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Payment/cancelreceiveprincome\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def payrequestsetrefill = {
    checkAccess(10)
    if (!checkSectionPermission(PPLANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (hsRes.payrequest?.paytype!=1||hsRes.payrequest?.agreementtype_id!=3||Kredit.get(hsRes.payrequest?.agreement_id)?.is_real!=1) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest.csisetrefill().save(failOnError:true,flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Payment/payrequestsetrefill\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def cancellrefill = {
    checkAccess(10)
    if (!checkSectionPermission(PPLANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (hsRes.payrequest?.paytype!=5||hsRes.payrequest?.client_id>0) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest.cancellrefill().save(failOnError:true,flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Payment/cancellrefill\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def payrequestdecline = {
    checkAccess(10)
    if (!checkSectionPermission(PPLANTASK)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (hsRes.payrequest?.modstatus!=0||hsRes.payrequest?.paycat!=4) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest.csidecline().save(failOnError:true,flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Payment/payrequestdecline\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def payrequestrestore = {
    checkAccess(10)
    if (!checkSectionPermission(PPLANTASK)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (hsRes.payrequest?.modstatus!=-1) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest.csirestore().save(failOnError:true,flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Payment/payrequestrestore\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def newpayrequest = {
    checkAccess(10)
    if (!checkSectionPermission(PPLANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.nds = Tools.getIntVal(ConfigurationHolder.config.payment.nds,18)
    hsRes.project = Project.findAllByModstatus(1,[sort:'name'])
    hsRes.defproject_id = Project.findByIs_main(1)?.id?:0
    hsRes.expensetype = new ExpensetypeSearch().csiGetList()
    hsRes.cars = Car.list(sort:'name')
    hsRes.expcar_ids = Expensetype.findAllByIs_car(1).collect{it.id}?:[]
    hsRes.clients = Client.findAllByModstatusAndParent(1,0)
    hsRes.subclients = Client.findAllByParentGreaterThanAndParentAndModstatus(0,0,1)
    hsRes.iscanedit = recieveSectionPermission(PPAYTAG)

    return hsRes
  }

  def payrequestdata = {
    checkAccess(10)
    if (!checkSectionPermission(PPLANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.agrtypes = Agreementtype.findAllByIdNotEqual(5)
    hsRes.tax = Tax.list(sort: "name", order: "asc")
    hsRes.paycat = requestService.getIntDef('cat',0)
    hsRes.paytype = requestService.getIntDef('type',0)

    if (!hsRes.paycat||!hsRes.paytype){
      render ''
      return
    }
    render(view: hsRes.paytype==1?'outgoingrequest':hsRes.paytype==2?'incomingrequest':'innerrequest', model: hsRes)
    return
  }

  def persbycompany = {
    checkAccess(10)
    if (!checkSectionPermission(PPLANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    return [pers:Compers.findAllByCompany_id(requestService.getIntDef('id',0)).collect{ it.pers_id }.unique().collect{ Pers.get(it) }]
  }

  def getbankbycompany={
    checkAccess(10)
    requestService.init(this)
    def hsRes = [:]
    
    def iId=requestService.getIntDef('company_id',0) 
    hsRes.cut_version=requestService.getIntDef('cut',0)
    hsRes.type=requestService.getIntDef('type',0)

    hsRes.bank=[]
    if(iId){
      for(baccount in Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(iId,1,1)){
        if (!hsRes.type) hsRes.bank<<Bank.get(baccount.bank_id)
        else hsRes.bank<<[name:baccount.schet+' в '+Bank.get(baccount.bank_id)?.name,id:baccount.id]
      }
    }
    hsRes.bank.sort{it.name}
    return hsRes          
  }

  def getbankaccountbycompany = {
    checkAccess(10)
    requestService.init(this)
    def hsRes = [:]

    hsRes.bankaccount = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(requestService.getIntDef('company_id',0),1,1,[sort:'schet',order:'asc'])

    return hsRes
  }

  def bankaccountdata = {
    checkAccess(10)
    requestService.init(this)
    def hsRes = [:]

    hsRes.curbankaccount = Bankaccount.findByIdAndModstatus(requestService.getIntDef('id',0),1)
    if (!hsRes.curbankaccount) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.bank = Bank.get(hsRes.curbankaccount.bank_id)
    hsRes.cursaldo = hsRes.curbankaccount.actsaldo - (Taskpay.findAllByModdateGreaterThanEqualsAndBankaccount_idAndTaskpaystatusInList(hsRes.curbankaccount.actsaldodate?:new Date()-365,hsRes.curbankaccount.id,[2,4]).sum{it.summa}?:0) + (Payrequest.findAllByPaydateGreaterThanEqualsAndTobankaccount_idAndPaytypeAndModstatusGreaterThan(hsRes.curbankaccount.actsaldodate?:new Date()-365,hsRes.curbankaccount.id,2,2).sum{it.summa}?:0) + (Payrequest.findAllByPaydateGreaterThanEqualsAndTobankaccount_idAndPaytypeAndModstatusGreaterThanAndPaycatAndIs_dop(hsRes.curbankaccount.actsaldodate?:new Date()-365,hsRes.curbankaccount.id,3,2,4,1).sum{it.summa}?:0) - (Payrequest.findAllByPaydateGreaterThanEqualsAndBankaccount_idAndPaytypeAndModstatusGreaterThanAndPaycatAndIs_dop(hsRes.curbankaccount.actsaldodate?:new Date()-365,hsRes.curbankaccount.id,3,2,4,1).sum{it.summa}?:0) - (Taskpay.findAllByBankaccount_idAndTaskpaystatusInListAndIs_accept(hsRes.curbankaccount.id,[0,1,3,5],1).sum{it.summa}?:0)
    hsRes.accsaldo = Taskpay.findAllByBankaccount_idAndTaskpaystatusInList(hsRes.curbankaccount.id,[-1,0,1,3,5]).sum{it.summa}?:0.0g
    hsRes.compsaldo = Taskpay.findAllByCompany_idAndBankaccount_idAndTaskpaystatusInList(hsRes.curbankaccount.company_id,0,[-1,0,1,3,5]).sum{it.summa}?:0.0g
    hsRes.totalsaldo = hsRes.cursaldo - requestService.getBigDecimalDef('summa',0.0g)

    return hsRes
  }

  def getBankaccountByBank_idAndCompany={
    checkAccess(10)
    requestService.init(this)
    def hsRes = [:]    
    
    def iCompanyId=requestService.getIntDef('company_id',0)
    def sBank_id=requestService.getStr('bank_id')       
    
    hsRes.bankaccount=[:]
    
    if(iCompanyId && sBank_id){
      hsRes.bankaccount=Bankaccount.findByBank_idAndCompany_idAndModstatusAndTypeaccount_id(sBank_id,iCompanyId,1,1)
    }   
          
    render hsRes as JSON
    return    
  }

  def addpayrequest = {
    checkAccess(10)
    if (!checkSectionPermission(PPLANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['card','paycat','paytype','fromcompany_id','tocompany_id','agreement_id','is_fine',
                                     'agreementtype_id','project_id','expensetype_id','fromtax_id','frombank','totax_id',
                                     'agent_id','agentagr_id','is_dop','is_nds','car_id','is_com','is_dopmain','is_task'],
                                     ['pers_id'],['tobank','destination','comment','tagcomment'],['paydate'],['summa'])

    if(!hsRes.inrequest.paytype)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.paycat)
      hsRes.result.errorcode<<3

    if(hsRes.result.errorcode){
      render hsRes.result as JSON
      return
    }

    if(!hsRes.inrequest.paydate)
      hsRes.result.errorcode<<2
    if(hsRes.inrequest.paytype!=2 && hsRes.inrequest.paycat!=2 && !hsRes.inrequest.fromcompany_id)
      hsRes.result.errorcode<<4
    if(((hsRes.inrequest.paytype==1 && (hsRes.inrequest.paycat==1 || hsRes.inrequest.paycat==4 || hsRes.inrequest.paycat==6)) ||
      (hsRes.inrequest.paytype==2 && (hsRes.inrequest.paycat==1 || hsRes.inrequest.paycat==2 || hsRes.inrequest.paycat==4)) ||
      (hsRes.inrequest.paytype==3)) &&
      !hsRes.inrequest.tocompany_id)
      hsRes.result.errorcode<<5

    if((((hsRes.inrequest.paytype==1 || hsRes.inrequest.paytype==2) && (hsRes.inrequest.paycat==1 || hsRes.inrequest.paycat==4 || hsRes.inrequest.paycat==6)) ||
      hsRes.inrequest.paytype==3) && !hsRes.inrequest.tobank)
      hsRes.result.errorcode<<6 

    if(hsRes.inrequest.paytype==1 && hsRes.inrequest.paycat==3){
      if(!hsRes.inrequest.pers_id)
        hsRes.result.errorcode<<7
      if(hsRes.inrequest.card==-1)
        hsRes.result.errorcode<<8
    }

    if(hsRes.inrequest.paycat==2){
      if(hsRes.inrequest.paytype==1 && !hsRes.inrequest.totax_id)
        hsRes.result.errorcode<<9
      else if(hsRes.inrequest.paytype==2 && !hsRes.inrequest.fromtax_id)
        hsRes.result.errorcode<<10
    }
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<11
    if(!hsRes.inrequest.destination)
      hsRes.result.errorcode<<12
    if(recieveSectionPermission(PPAYTAG)&&!hsRes.inrequest.expensetype_id)
      hsRes.result.errorcode<<13
    if(hsRes.inrequest.paycat==1&&!hsRes.inrequest.agreementtype_id)
      hsRes.result.errorcode<<14
    if(hsRes.inrequest.paycat==1&&!hsRes.inrequest.agreement_id)
      hsRes.result.errorcode<<15


    if(!hsRes.result.errorcode){
      try {
        def prequest = new Payrequest().csiSetPayrequest(hsRes.inrequest).csiSetPayrequestTag(hsRes.inrequest,recieveSectionPermission(PPAYTAG),hsRes.user.id).csiSetBankaccount_id(hsRes.inrequest.paytype!=2?(Bankaccount.findByIdAndCompany_idAndModstatusAndTypeaccount_id(hsRes.inrequest.frombank,hsRes.inrequest.fromcompany_id,1,1)?.id?:0):0).csiSetInitiator(hsRes.user.id).csiSetPersDop().save(flush:true,failOnError:true)
        if(hsRes.inrequest.is_task){
          def taskpay_id = new Taskpay(paygroup:prequest.paygroup,is_manual:1).csiSetTaskpay(term:String.format('%td.%<tm.%<tY',new Date()),company_id:prequest.fromcompany_id,summa:prequest.summa).csiSetInitiator(hsRes.user.id).csiSetBankaccountId(prequest.bankaccount_id).csiSetTaskpaystatus(0).csiSetInternal(prequest.paycat==3?1:0).save(flush:true,failOnError:true)?.id?:0
          prequest.csiSetTaskpay_id(taskpay_id).csiSetModstatus(1).save(flush:true,failOnError:true)
        }
        hsRes.result.payrequest = prequest.id
      } catch(Exception e) {
        log.debug("Error save data in Payment/addpayrequest\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }

  def paycat = {
    checkAccess(10)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = session.user
    hsRes.action_id = 10

    def iPaytype=requestService.getIntDef('id',0)

    hsRes.paycat=[]
    if(iPaytype==1){
      hsRes.paycat<<[name:'договорной',id:1]
      hsRes.paycat<<[name:'счета',id:6]
      hsRes.paycat<<[name:'бюджетный',id:2]
      hsRes.paycat<<[name:'персональный',id:3]
    }else if(iPaytype==3){
      hsRes.paycat<<[name:'договорной',id:1]
    }

    return hsRes
  }

  def getcardtypebypers = {
    checkAccess(10)
    requestService.init(this)
    def hsRes = [:]
    hsRes.user = session.user
    hsRes.action_id = 10

    def lPersId = requestService.getIntDef('id',0)
    hsRes.card = []

    if(lPersId){
      if(Persaccount.findByPers_idAndIs_mainAndModstatus(lPersId,1,1))
        hsRes.card<<[name:"${Persaccount.findByPers_idAndIs_mainAndModstatus(lPersId,1,1)?.paccount} (основная)",id:1]
      if(Persaccount.findByPers_idAndIs_mainAndModstatus(lPersId,0,1))
        hsRes.card<<[name:"${Persaccount.findByPers_idAndIs_mainAndModstatus(lPersId,0,1)?.paccount} (дополнительная)",id:0]
    }

    return hsRes
  }

  def savepayrequestdetail={
    checkAccess(10)
    if (!checkSectionPermission(PPLANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10
    hsRes.result=[errorcode:[]]

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (!hsRes.payrequest) {
      response.sendError(404)
      return
    }

    hsRes+=requestService.getParams(['project_id','expensetype_id','tax_id','agreementtype_id','agreement_id','car_id','is_task',
                                     'is_bankmoney','is_dop','client_id','subclient_id','is_com','is_dopmain','is_persdop'],
                                     null,['tagcomment','comment','destination'],null,['summa'])
    if (hsRes.payrequest.modstatus!=0) hsRes.inrequest.summa = hsRes.payrequest.summa

    if(hsRes.payrequest.modstatus==2){
      def hsData
      imageService.init(this)
      hsData = imageService.rawUpload('file',true)
      if(hsData.error==2){
        //hsRes.result.errorcode<<5
      }else if(hsData.error)
        hsRes.result.errorcode<<1
    }
    if(hsRes.payrequest.paycat==1&&!hsRes.inrequest.agreementtype_id)
      hsRes.result.errorcode<<2
    else if(hsRes.payrequest.paycat==1&&!hsRes.inrequest.agreement_id)
      hsRes.result.errorcode<<3
    if(hsRes.payrequest.paycat==2&&!hsRes.inrequest.tax_id)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.destination)
      hsRes.result.errorcode<<5
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<6

    if(!hsRes.result.errorcode){
      try {
        hsRes.payrequest.setDetailData(hsRes.inrequest).csiSetSumma(hsRes.inrequest.summa).csiSetPayrequestTag(hsRes.inrequest,recieveSectionPermission(PPAYTAG),hsRes.user.id).save(flush:true,failOnError:true)
        if(hsRes.payrequest.modstatus==2)
          hsRes.payrequest.csiSetFileId(imageService.rawUpload('file').fileid).save(flush:true,failOnError:true)
        if (hsRes.inrequest.is_task&&hsRes.payrequest.taskpay_id==0&&hsRes.payrequest.paytype in [1,3]){
          def taskpay_id = new Taskpay(paygroup:hsRes.payrequest.paygroup,is_manual:1).csiSetTaskpay(term:String.format('%td.%<tm.%<tY',hsRes.payrequest.paydate),company_id:hsRes.payrequest.fromcompany_id,summa:hsRes.payrequest.summa).csiSetInitiator(hsRes.user.id).csiSetBankaccountId(hsRes.payrequest.bankaccount_id).csiSetTaskpaystatus(0).csiSetClient(hsRes.payrequest.client_id?1:0).csiSetInternal(hsRes.payrequest.paycat==3?1:0).save(flush:true,failOnError:true)?.id?:0
          hsRes.payrequest.csiSetTaskpay_id(taskpay_id).csiSetModstatus(1).save(flush:true,failOnError:true)
        }
      } catch(Exception e) {
        log.debug("Error save data in Payment/savepayrequestdetail\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    return hsRes.result
  }

  def newpayinternal = {
    checkAccess(10)
    if (!checkSectionPermission(PPLANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    return hsRes
  }

  def incertinternalpayment = {
    checkAccess(10)
    if (!checkSectionPermission(PPLANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['paycat','paytype','fromcompany_id','tocompany_id','is_nds','frombank','is_urgent'],null,
                                    ['tobank','destination','comment','tagcomment','plan'],['paydate'],['summa'])
    def hsData

    if(!hsRes.inrequest.paytype)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.paycat)
      hsRes.result.errorcode<<3

    if(hsRes.result.errorcode){
      return hsRes.result
    }

    if(!hsRes.inrequest.paydate)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.fromcompany_id)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.tocompany_id)
      hsRes.result.errorcode<<5
    if(!hsRes.inrequest.tobank)
      hsRes.result.errorcode<<6
    if(!hsRes.inrequest.frombank)
      hsRes.result.errorcode<<7
    else if(!Bankaccount.findByIdAndCompany_idAndModstatusAndTypeaccount_id(hsRes.inrequest.frombank,hsRes.inrequest.fromcompany_id,1,1))
      hsRes.result.errorcode<<8
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<11
    if(!hsRes.inrequest.comment)
      hsRes.result.errorcode<<12

    if(!hsRes.result.errorcode){
      try {
        def prequest = new Payrequest().csiSetPayrequest(hsRes.inrequest).csiSetBankaccount_id(Bankaccount.findByIdAndCompany_idAndModstatusAndTypeaccount_id(hsRes.inrequest.frombank,hsRes.inrequest.fromcompany_id,1,1)?.id?:0).csiSetInitiator(hsRes.user.id).save(flush:true,failOnError:true)
        def oTaskpay = new Taskpay(paygroup:prequest.paygroup,company_id:prequest.fromcompany_id,summa:prequest.summa).csiSetInitiator(hsRes.user.id).updateTaskpay(plan:hsRes.inrequest.plan,term:String.format('%td.%<tm.%<tY',new Date()),bankaccount_id:prequest.bankaccount_id).csiSetInternal(1).csiSetUrgent(hsRes.inrequest.is_urgent?:0).save(flush:true,failOnError:true)
        prequest.csiSetTaskpay_id(oTaskpay.id).csiSetModstatus(1).save(flush:true,failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Payment/incertinternalpayment\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    return hsRes.result
  }
//////////////////////////////////////////////////////////////////////////////////////////
//////////////////////paymentsaldo>>> ////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////
  def saldofilter={
    checkAccess(10)
    if (!checkSectionPermission(PPSALDO)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10
    
    if (session.paymentlastRequest?.fromDetails){
      hsRes.inrequest = session.paymentlastRequest
    }

    return hsRes
  }
/////////////////////////////////////////////////////////
  def saldolist = {
    checkAccess(10)
    if (!checkSectionPermission(PPSALDO)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    if (session.paymentlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.paymentlastRequest
      session.paymentlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['company_id','order','valuta_id','typeaccount_id'],null,['bankname'])      
      session.paymentlastRequest = hsRes.inrequest
    }
    session.paymentlastRequest.paymentobject = 2

    hsRes.searchresult = new BankaccountSearch().csiFindBankaccount(hsRes.inrequest.bankname,hsRes.inrequest.company_id,hsRes.inrequest.valuta_id?:857,hsRes.inrequest.typeaccount_id?:1,-100,hsRes.inrequest.order,20,requestService.getOffset())
    hsRes.valutas = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code.toLowerCase();map}
    return hsRes
  }

  def setActSaldo = {
    checkAccess(10)
    if (!checkSectionPermission(PPSALDO)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    def iId=requestService.getIntDef('id',0)
    def iActsaldo=requestService.getIntDef('actsaldo',0)
    def sActsaldodate=requestService.getDate('actsaldodate')

    def oBankaccount=Bankaccount.get(iId)
    if(oBankaccount)
      oBankaccount.csiSetActsaldo(iActsaldo,sActsaldodate).save(flush:true,failOnError:true)

    render(contentType:"application/json"){[error:false]}
    return
  }

  def setAccountBlock = {
    checkAccess(10)
    if (!checkSectionPermission(PPSALDO)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    Bankaccount.get(requestService.getIntDef('id',0))?.csiSetIbankblock(requestService.getIntDef('block',0))?.updateIbankstatus()?.save(flush:true)

    render(contentType:"application/json"){[error:false]}
    return
  }

  def setNoSms = {
    checkAccess(10)
    if (!checkSectionPermission(PPSALDO)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    Bankaccount.get(requestService.getIntDef('id',0))?.csiSetNoSms(requestService.getIntDef('nosms',0))?.save(flush:true)

    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////paymentsaldo<<< ////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////banksaldo>>> ///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def banksaldofilter = {
    checkAccess(10)
    if (!checkSectionPermission(PCLIENTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    if (session.paymentlastRequest?.fromDetails){
      hsRes.inrequest = session.paymentlastRequest
    }

    return hsRes
  }

  def banksaldolist = {
    checkAccess(10)
    if (!checkSectionPermission(PCLIENTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    if (session.paymentlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.paymentlastRequest
      session.paymentlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['company_id','order','valuta_id','typeaccount_id'],null,['bankname'])
      hsRes.inrequest.offset = requestService.getOffset()
      session.paymentlastRequest = hsRes.inrequest
    }
    session.paymentlastRequest.paymentobject = 12

    hsRes.searchresult = new BankaccountSearch().csiFindBankaccount(hsRes.inrequest.bankname,hsRes.inrequest.company_id,hsRes.inrequest.valuta_id?:857,hsRes.inrequest.typeaccount_id?:1,-101,hsRes.inrequest.order,20,hsRes.inrequest.offset)
    hsRes.valutas = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code.toLowerCase();map}

    return hsRes
  }

  def setBanksaldo = {
    checkAccess(10)
    if (!checkSectionPermission(PCLPAYEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    try {
      Bankaccount.get(requestService.getIntDef('id',0))?.csiSetBanksaldo(requestService.getIntDef('banksaldo',0),requestService.getDate('banksaldodate'))?.save(flush:true,failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Payment/setBanksaldo\n"+e.toString())
    }
    session.paymentlastRequest.fromDetails = 1

    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////banksaldo<<< ///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Client payments >>>///////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def clpaymentsfilter = {
    checkAccess(10)
    if (!checkSectionPermission(PCLIENTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    if (session.paymentlastRequest?.fromDetails){
      hsRes.inrequest = session.paymentlastRequest
    }
    hsRes.clients = Client.findAllByParentAndModstatus(0,1)
    hsRes.iscandeal = recieveSectionPermission(PCLPAYEDIT)
    hsRes.iscannew = recieveSectionPermission(PCLPAYNEW)
    hsRes.iscancreatetask = recieveSectionPermission(PPLANTASK)
    hsRes.subclients = Client.findAllByParentGreaterThanAndParentAndModstatus(0,hsRes.inrequest?.client_id?:0,1)

    return hsRes
  }

  def clientpayments = {
    checkAccess(10)
    if (!checkSectionPermission(PCLIENTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    if (session.paymentlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.paymentlastRequest
      session.paymentlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['client_id','is_deal','modstatus','paytype','subclient_id','clid'],null,['company_name'])
      hsRes.inrequest.is_noinner = requestService.getIntDef('is_noinner',0)
      hsRes.inrequest.paydate_start = requestService.getDate('paydate_start')
      hsRes.inrequest.paydate_end = requestService.getDate('paydate_end')
      hsRes.inrequest.offset = requestService.getOffset()
      session.paymentlastRequest = hsRes.inrequest
    }
    session.paymentlastRequest.paymentobject = 3

    hsRes.searchresult = new PayrequestClientSearch().csiSelectPayments(hsRes.inrequest.client_id?:0,hsRes.inrequest.company_name?:'',
                                                    hsRes.inrequest.is_deal?:0,hsRes.inrequest.paydate_start,hsRes.inrequest.paydate_end,
                                                    hsRes.inrequest.modstatus?:0,0,hsRes.inrequest.paytype?:-100,hsRes.inrequest.is_noinner?:0,
                                                    hsRes.inrequest.subclient_id?:0,hsRes.inrequest.clid?:0,20,hsRes.inrequest.offset?:0)
    hsRes.iscanedit = recieveSectionPermission(PCLPAYEDIT)
    hsRes.iscandelete = recieveSectionPermission(PPAYDEL)

    return hsRes
  }

  def createdeal = {
    checkAccess(10)
    if (!checkSectionPermission(PCLPAYEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    def payrequestIds = requestService.getIntIds('payrequestids')

    if (!payrequestIds) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    try {
      Payrequest.findAllByIdInListAndDeal_idAndClient_idGreaterThanAndModstatusGreaterThanAndPaytypeInListAndSubclient_id(payrequestIds,0,0,1,[1,2,3,7,8,10,11],0).groupBy{it.client_id}.each{ requestlist ->
        paymentService.updatedeal(new Deal(client_id:requestlist.key),requestlist.value)
      }
    } catch(Exception e) {
      log.debug("Error save data in Payment/createdeal\n"+e.toString());
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def createclienttask = {
    checkAccess(10)
    checkSectionPermission(PPLANTASK)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    Payrequest.findAllByModstatusAndPaytypeInListAndFromcompany_idGreaterThanAndClient_idGreaterThan(0,[1,3,8],0,0).collect{it.fromcompany_id}.unique().each{ company_id ->
      (1..4).each{ paygroup ->
        def lsPayreq = Payrequest.findAllByFromcompany_idAndModstatusAndPaytypeInListAndPaygroupAndClient_idGreaterThan(company_id,0,[1,3,8],paygroup,0)
        if(lsPayreq){
          lsPayreq.groupBy{it.bankaccount_id}.each{ lsReq ->
            def oTaskpay = new Taskpay(paygroup:paygroup).csiSetTaskpay([term:String.format('%td.%<tm.%<tY',new Date()),company_id:company_id,summa:lsReq.value.sum{it.summa}]).csiSetBankaccountId(lsReq.key).csiSetInitiator(hsRes.user.id).save(flush:true,failOnError:true)
            lsReq.value.each{ it.csiSetTaskpay_id(oTaskpay.id).csiSetModstatus(1).save(flush:true,failOnError:true) }
          }
        }
      }
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def deleteclientpayment = {
    checkAccess(10)
    if (!checkSectionPermission(PCLPAYEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (!(hsRes.payrequest?.paytype in [1,2,10,11])||(hsRes.payrequest?.modstatus!=0&&hsRes.payrequest?.paytype==1)||hsRes.payrequest?.related_id!=0||(hsRes.payrequest?.modstatus!=2&&hsRes.payrequest?.paytype==2)||hsRes.payrequest?.deal_id>0||hsRes.payrequest?.clientcommission>0||(hsRes.payrequest?.initiator!=hsRes.user.id&&!recieveSectionPermission(PPAYDEL))) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Payment/deleteclientpayment\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def newclientpayment = {
    checkAccess(10)
    if (!checkSectionPermission(PCLPAYNEW)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.agrtypes = Agreementtype.findAllByIdNotEqual(5)
    hsRes.client = Client.findAllByIs_tAndModstatusAndParent(0,1,0)
    hsRes.expensetype = new ExpensetypeSearch().csiGetList()
    if(session.paymentlastRequest?.paymentobject != 3) session.paymentlastRequest = [paymentobject:3,is_noinner:1]

    return hsRes
  }

  def clientpaymentdata = {
    checkAccess(10)
    if (!checkSectionPermission(PCLPAYNEW)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.agrtypes = Agreementtype.findAllByIdNotEqual(5)
    hsRes.tax = Tax.list(sort: "name", order: "asc")
    hsRes.paycat = requestService.getIntDef('cat',0)
    hsRes.paytype = requestService.getIntDef('type',0)

    if (!hsRes.paycat||!hsRes.paytype){
      render ''
      return
    }
    hsRes.executor = new UserpersSearch().csiFindByAccessrigth(PPPEXEC)
    render(view: hsRes.paytype==1?'outgoingclientpayment':hsRes.paytype==2?'incomingclientpayment':hsRes.paytype==11?'outerclientpayment':'innerclientpayment', model: hsRes)
    return
  }

  def incertclientpayment = {
    checkAccess(10)
    if (!checkSectionPermission(PCLPAYNEW)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['paycat','paytype','fromcompany_id','tocompany_id','agreement_id','agreementtype_id',
                                     'client_id','is_clientcommission','subclient_id','is_dop','is_nds','frombank','is_fine',
                                     'is_midcommission','is_accept','is_urgent','expensetype_id'],['executor'],['tobank','destination',
                                     'tagcomment','comment','plan'],['paydate'],['summa'])
    hsRes.result.is_detail = requestService.getIntDef('is_detail',0)
    def hsData

    imageService.init(this)

    if(!hsRes.inrequest.paytype)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.paycat)
      hsRes.result.errorcode<<3

    if(hsRes.result.errorcode){
      return hsRes.result
    }

    if(!hsRes.inrequest.destination)
      hsRes.result.errorcode<<12
    if(!hsRes.inrequest.paydate)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.fromcompany_id)
      hsRes.result.errorcode<<4
    if(hsRes.inrequest.paytype!=1&&!hsRes.inrequest.tocompany_id)
      hsRes.result.errorcode<<5
    if(hsRes.inrequest.paytype!=1&&!hsRes.inrequest.tobank)
      hsRes.result.errorcode<<6
    if(hsRes.inrequest.paytype!=2&&!hsRes.inrequest.frombank)
      hsRes.result.errorcode<<7
    else if(hsRes.inrequest.paytype!=2&&!Bankaccount.findByIdAndCompany_idAndModstatusAndTypeaccount_id(hsRes.inrequest.frombank,hsRes.inrequest.fromcompany_id,1,1))
      hsRes.result.errorcode<<8
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<11
    if(hsRes.inrequest.paytype!=3&&hsRes.inrequest.paytype!=2&&!(hsRes.inrequest.paytype==1&&hsRes.inrequest.paycat==6)&&!hsRes.inrequest.client_id)
      hsRes.result.errorcode<<10
    if(Client.get(hsRes.inrequest.client_id?:0)?.is_super==1&&!hsRes.inrequest.subclient_id)
      hsRes.result.errorcode<<14
    if(hsRes.inrequest.paycat==1&&!hsRes.inrequest.agreementtype_id)
      hsRes.result.errorcode<<15
    if(hsRes.inrequest.paycat==1&&!hsRes.inrequest.agreement_id)
      hsRes.result.errorcode<<16
    if (hsRes.inrequest.paytype==1) {
      hsData = imageService.rawUpload('file',true)
      if(hsData.error in [1,3])
        hsRes.result.errorcode<<9
      else if((!hsRes.inrequest.tocompany_id||!hsRes.inrequest.tobank)&&!hsRes.inrequest.comment&&hsData.error==2)
        hsRes.result.errorcode<<13
    }

    if(!hsRes.result.errorcode){
      try {
        def prequest = new Payrequest().csiSetPayrequest(hsRes.inrequest).csiSetPayrequestTag(hsRes.inrequest,recieveSectionPermission(PPAYTAG),hsRes.user.id).csiSetFileId(hsRes.inrequest.paytype!=1?0:imageService.rawUpload('file').fileid).csiSetBankaccount_id(hsRes.inrequest.paytype!=2?(Bankaccount.findByIdAndCompany_idAndModstatusAndTypeaccount_id(hsRes.inrequest.frombank,hsRes.inrequest.fromcompany_id,1,1)?.id?:0):0).csiSetInitiator(hsRes.user.id).csiSetClientadmin(hsRes.user.id).save(flush:true,failOnError:true)
        if(hsRes.inrequest.paytype!=2&&hsRes.inrequest.paytype!=11){
          hsRes.result.taskpay_id = new Taskpay(paygroup:prequest.paygroup,is_manual:1).csiSetTaskpay(term:String.format('%td.%<tm.%<tY',new Date()),company_id:prequest.fromcompany_id,summa:prequest.summa).csiSetInitiator(hsRes.user.id).csiSetBankaccountId(prequest.bankaccount_id).acceptTask(hsRes.inrequest.is_accept?hsRes.user.id:0l).updatePlanData(plan:hsRes.inrequest.plan,executor:hsRes.inrequest.executor).csiSetTaskpaystatus(hsRes.inrequest.is_accept?1:0).csiSetClient(prequest.client_id?1:0).csiSetInternal(prequest.paycat==3?1:0).csiSetUrgent(hsRes.inrequest.is_urgent?:0).save(flush:true,failOnError:true)?.id?:0
          prequest.csiSetTaskpay_id(hsRes.result.taskpay_id).csiSetModstatus(1).save(flush:true,failOnError:true)
        }
      } catch(Exception e) {
        log.debug("Error save data in Payment/incertclientpayment\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    return hsRes.result
  }

  def clientpayment = {
    checkAccess(10)
    if (!checkSectionPermission(PCLIENTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (!hsRes.payrequest) {
      response.sendError(404)
      return
    }

    hsRes.intcompany = Company.get(hsRes.payrequest.paytype in [1,8]?hsRes.payrequest.fromcompany_id:hsRes.payrequest.tocompany_id)?.name?:hsRes.payrequest.paytype in [1,8]?hsRes.payrequest.fromcompany:hsRes.payrequest.tocompany
    hsRes.extcompany = Company.get(hsRes.payrequest.paytype in [1,8]?hsRes.payrequest.tocompany_id:hsRes.payrequest.fromcompany_id)?.name?:hsRes.payrequest.paytype in [1,8]?hsRes.payrequest.tocompany:hsRes.payrequest.fromcompany
    hsRes.dinclientsaldo = (hsRes.payrequest.client_id==0?0:hsRes.payrequest.subclient_id==0?(Payrequest.findAll{ modstatus >= 0 && client_id == hsRes.payrequest.client_id && subclient_id==0 && agent_id==0 && ( paydate < hsRes.payrequest.paydate || ( paydate == hsRes.payrequest.paydate && id < hsRes.payrequest.id )) }.sum{ it.computeClientdelta() }?:0):Payrequest.findAll{ modstatus >= 0 && subclient_id == hsRes.payrequest.subclient_id && agent_id==0 && ( paydate < hsRes.payrequest.paydate || ( paydate == hsRes.payrequest.paydate && id < hsRes.payrequest.id )) }.sum{ it.computeClientdelta() }?:0)
    hsRes.curclientsaldo = Client.get(hsRes.payrequest.client_id)?.computeCurSaldo()?:0
    hsRes.subclients = Client.findAllByParentGreaterThanAndParentAndModstatus(0,hsRes.payrequest.client_id,1)
    hsRes.initiator = User.get(hsRes.payrequest.initiator)?.name
    hsRes.clientadmin = User.get(hsRes.payrequest.clientadmin)?.name
    hsRes.clients = Client.findAllByModstatusAndParent(1,0)
    hsRes.supclients = Client.findAllByIs_clientcomm(1).collect{it.id}
    hsRes.midclients = Client.findAllByIs_middleman(1).collect{it.id}
    hsRes.iscansf = hsRes.payrequest.is_nds==1&&Company.get(hsRes.payrequest.fromcompany_id)?.is_bank==0&&Company.get(hsRes.payrequest.tocompany_id)?.is_bank==0
    hsRes.iscanedit = recieveSectionPermission(PCLPAYEDIT)

    return hsRes
  }

  def subclientslist={
    checkAccess(10)
    requestService.init(this)

    return [subclients:Client.findAllByParentGreaterThanAndParentAndModstatus(0,requestService.getIntDef('client_id',0),1)]
  }

  def updatepayrequest = {
    checkAccess(10)
    if (!checkSectionPermission(PCLPAYEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10
    hsRes.result=[errorcode:[]]
 
    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (!hsRes.payrequest||!(hsRes.payrequest.paytype in [1,2,3,5,10,11])) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['client_id','subclient_id','percenttype','is_bankmoney'],null,['sfactura'],null,['compercent','midpercent','supcompercent'])
    hsRes.inrequest.sfacturadate = requestService.getDate('sfacturadate')
    def hsData

    imageService.init(this)

    if(hsRes.inrequest.compercent&&hsRes.inrequest.compercent<0)
      hsRes.result.errorcode<<2
    else if(hsRes.inrequest.compercent&&hsRes.inrequest.compercent>99)
      hsRes.result.errorcode<<2
    if(hsRes.inrequest.subcompercent&&hsRes.inrequest.subcompercent<0)
      hsRes.result.errorcode<<3
    else if(hsRes.inrequest.subcompercent&&hsRes.inrequest.subcompercent>99)
      hsRes.result.errorcode<<3
    else if(hsRes.inrequest.subcompercent>hsRes.inrequest.compercent)
      hsRes.result.errorcode<<3
    if (hsRes.payrequest.paytype<3) {
      hsData = imageService.rawUpload('file',true)
      if(hsData.error in [1,3])
        hsRes.result.errorcode<<1
    }

    if(!hsRes.result.errorcode){
      try {
        hsRes.payrequest.setClientData(hsRes.inrequest).csiSetFileId(imageService.rawUpload('file').fileid).csiSetClientadmin(hsRes.user.id).csiSetSfactura(hsRes.inrequest).save(failOnError:true,flush:true)
        if (hsRes.payrequest.deal_id) Deal.get(hsRes.payrequest.deal_id)?.recomputeDeal()?.save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Payment/updatepayrequest\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    return hsRes.result
  }

  def closerefillpayment = {
    checkAccess(10)
    if (!checkSectionPermission(PCLPAYEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (hsRes.payrequest?.paytype!=5) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest.closerefill().save(failOnError:true,flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Payment/closerefillpayment\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def createrelatedpayment = {
    checkAccess(10)
    if (!checkSectionPermission(PCLPAYEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (hsRes.payrequest?.related_id!=0||!hsRes.payrequest?.client_id||!(hsRes.payrequest?.paytype in [1,3,11])) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.related_id = hsRes.payrequest.csiSetRelated(new Payrequest().csiSetInitiator(hsRes.user.id).fillRelatedFrom(hsRes.payrequest).save(failOnError:true,flush:true)?.id).save(failOnError:true,flush:true).related_id
    } catch(Exception e) {
      log.debug("Error save data in Payment/createrelatedpayment\n"+e.toString())
    }

    render(contentType:"application/json"){[related_id:hsRes.related_id]}
    return
  }

  def repayments = {
    checkAccess(10)
    if (!checkSectionPermission(PCLIENTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (!hsRes.payrequest||hsRes.payrequest.paytype!=2||hsRes.payrequest.confirmstatus||hsRes.payrequest.is_bankmoney) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.repayments = new Payrequest().csiSelectRepayments(hsRes.payrequest.client_id,hsRes.payrequest.agentagr_id)
    hsRes.iscanedit = recieveSectionPermission(PCLPAYEDIT)

    return hsRes
  }

  def repayment = {
    checkAccess(10)
    if (!checkSectionPermission(PCLIENTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('payrequest_id',0))
    hsRes.repayment = Payrequest.get(requestService.getIntDef('id',0))
    if (!hsRes.repayment||!hsRes.payrequest||hsRes.payrequest.paytype!=2||hsRes.payrequest.confirmstatus||hsRes.payrequest.is_bankmoney) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    return hsRes
  }

  def payrepayment = {
    checkAccess(10)
    if (!checkSectionPermission(PCLIENTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10
    hsRes.result=[errorcode:[]]

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    hsRes.repayment = Payrequest.get(requestService.getIntDef('repayment_id',0))
    if (!hsRes.repayment||!hsRes.payrequest||hsRes.payrequest.paytype!=2||hsRes.payrequest.confirmstatus||(hsRes.payrequest.agentagr_id?:hsRes.repayment.agentagr_id)!=hsRes.repayment.agentagr_id) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,null,null,['summa'])
    hsRes.inrequest.summa = hsRes.inrequest.summa ?: hsRes.repayment.summa - hsRes.repayment.clientcommission
    if (hsRes.inrequest.summa>0) hsRes.inrequest.summa = hsRes.inrequest.summa <= (hsRes.payrequest.summa - hsRes.payrequest.clientcommission - hsRes.payrequest.agentcommission) ? hsRes.inrequest.summa : hsRes.payrequest.summa - hsRes.payrequest.clientcommission - hsRes.payrequest.agentcommission
    else if (hsRes.inrequest.summa<0) hsRes.inrequest.summa = hsRes.inrequest.summa >= -hsRes.payrequest.clientcommission ? hsRes.inrequest.summa : -hsRes.payrequest.clientcommission

    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<1
    else if(hsRes.repayment.summa - hsRes.repayment.clientcommission - hsRes.inrequest.summa < 0)
      hsRes.result.errorcode<<1
    else if(hsRes.repayment.summa - hsRes.repayment.clientcommission - hsRes.inrequest.summa > hsRes.repayment.summa)
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        hsRes.repayment.payRepayment(hsRes.inrequest.summa).csiSetClientadmin(hsRes.user.id).save(failOnError:true)
        hsRes.payrequest.updateClientcomission(hsRes.inrequest.summa,hsRes.repayment.agentagr_id).csiSetClientadmin(hsRes.user.id).save(flush:true,failOnError:true)
        if (hsRes.payrequest.deal_id) Deal.get(hsRes.payrequest.deal_id)?.recomputeDeal()?.save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Payment/payrepayment\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }

  def agentpayments = {
    checkAccess(10)
    if (!checkSectionPermission(PCLIENTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (!hsRes.payrequest||hsRes.payrequest.paytype!=1||hsRes.payrequest.confirmstatus||hsRes.payrequest.is_bankmoney) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.agentpayments = new Payrequest().csiSelectAgentpayments(hsRes.payrequest.client_id)
    hsRes.iscanedit = recieveSectionPermission(PCLPAYEDIT)

    return hsRes
  }

  def agentpayment = {
    checkAccess(10)
    if (!checkSectionPermission(PCLIENTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('payrequest_id',0))
    hsRes.agentpayment = Payrequest.get(requestService.getIntDef('id',0))
    if (!hsRes.agentpayment||!hsRes.payrequest||hsRes.payrequest.paytype!=1||hsRes.payrequest.confirmstatus||hsRes.payrequest.is_bankmoney) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    return hsRes
  }

  def payagentpayment = {
    checkAccess(10)
    if (!checkSectionPermission(PCLIENTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10
    hsRes.result=[errorcode:[]]

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    hsRes.agentpayment = Payrequest.get(requestService.getIntDef('agentpayment_id',0))
    if (!hsRes.agentpayment||!hsRes.payrequest||hsRes.payrequest.paytype!=1||hsRes.payrequest.confirmstatus||hsRes.payrequest.is_bankmoney||hsRes.payrequest.client_id!=hsRes.agentpayment.client_id) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,null,null,['summa'])
    hsRes.inrequest.summa = hsRes.inrequest.summa ?: hsRes.agentpayment.summa - hsRes.agentpayment.agentcommission
    if (hsRes.inrequest.summa>0) hsRes.inrequest.summa = hsRes.inrequest.summa <= (hsRes.payrequest.summa - hsRes.payrequest.clientcommission - hsRes.payrequest.agentcommission) ? hsRes.inrequest.summa : hsRes.payrequest.summa - hsRes.payrequest.clientcommission - hsRes.payrequest.agentcommission
    else if (hsRes.inrequest.summa<0) hsRes.inrequest.summa = hsRes.inrequest.summa >= -hsRes.payrequest.agentcommission ? hsRes.inrequest.summa : -hsRes.payrequest.agentcommission

    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<1
    else if(hsRes.agentpayment.summa - hsRes.agentpayment.agentcommission - hsRes.inrequest.summa < 0)
      hsRes.result.errorcode<<1
    else if(hsRes.agentpayment.summa - hsRes.agentpayment.agentcommission - hsRes.inrequest.summa > hsRes.agentpayment.summa)
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        hsRes.agentpayment.payAgentpayment(hsRes.inrequest.summa).csiSetClientadmin(hsRes.user.id).save(failOnError:true)
        hsRes.payrequest.updateAgentcomission(hsRes.inrequest.summa).csiSetClientadmin(hsRes.user.id).save(flush:true,failOnError:true)
        if (hsRes.payrequest.deal_id) Deal.get(hsRes.payrequest.deal_id)?.recomputeDeal()?.save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Payment/payagentpayment\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Client payments >>>///////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Deals >>>/////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def dealfilter = {
    checkAccess(10)
    if (!checkSectionPermission(PCLIENTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    if (session.paymentlastRequest?.fromDetails){
      hsRes.inrequest = session.paymentlastRequest
    }
    hsRes.clients = Client.findAllByModstatus(1)

    return hsRes
  }

  def deals = {
    checkAccess(10)
    if (!checkSectionPermission(PCLIENTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    if (session.paymentlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.paymentlastRequest
      session.paymentlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['client_id'])
      hsRes.inrequest.offset = requestService.getOffset()
      session.paymentlastRequest = hsRes.inrequest
    }
    session.paymentlastRequest.paymentobject = 4

    hsRes.searchresult = new DealSearch().csiSelectDeals(hsRes.inrequest.client_id?:0,20,hsRes.inrequest.offset)

    return hsRes
  }

  def deal = {
    checkAccess(10)
    if (!checkSectionPermission(PCLIENTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.deal = Deal.get(requestService.getIntDef('id',0))
    if (!hsRes.deal) {
      response.sendError(404)
      return
    }

    hsRes.iscanedit = recieveSectionPermission(PCLPAYEDIT)
    hsRes.ishavepayments = Payrequest.findAllByDeal_id(hsRes.deal.id)

    return hsRes
  }

  def dealprequests = {
    checkAccess(10)
    if (!checkSectionPermission(PCLIENTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.deal = Deal.get(requestService.getIntDef('id',0))
    if (!hsRes.deal) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payrequests = new PayrequestClientSearch().csiSelectDealPayments(hsRes.deal.id,hsRes.deal.client_id)
    hsRes.curclientsaldo = Client.get(hsRes.deal.client_id)?.computeCurSaldo()?:0
    hsRes.iscanedit = recieveSectionPermission(PCLPAYEDIT)

    return hsRes
  }

  def removedealpayment = {
    checkAccess(10)
    if (!checkSectionPermission(PCLPAYEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.deal = Deal.get(requestService.getIntDef('deal_id',0))
    hsRes.payrequest = Payrequest.findByDeal_idAndId(hsRes.deal?.id?:0,requestService.getIntDef('id',0))
    if (!hsRes.deal||!hsRes.payrequest) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      if (hsRes.payrequest.deal_id){
        hsRes.payrequest.csiSetDeal_id(0).save(flush:true,failOnError:true)
        hsRes.deal.recomputeDeal().save(failOnError:true)
      }
    } catch(Exception e) {
      log.debug("Error save data in Payment/removedealpayment\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def dealaddprequests = {
    checkAccess(10)
    if (!checkSectionPermission(PCLIENTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.deal = Deal.get(requestService.getIntDef('id',0))
    if (!hsRes.deal) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payrequests = new PayrequestClientSearch().csiSelectDealPayments(0,hsRes.deal.client_id)
    hsRes.curclientsaldo = Client.get(hsRes.deal.client_id)?.computeCurSaldo()?:0
    hsRes.iscanedit = recieveSectionPermission(PCLPAYEDIT)

    return hsRes
  }

  def updatedeal = {
    checkAccess(10)
    if (!checkSectionPermission(PCLPAYEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.deal = Deal.get(requestService.getIntDef('id',0))
    def payrequestIds = requestService.getIntIds('payrequestids')
    if (hsRes.deal?.modstatus!=0||!payrequestIds) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Payrequest.findAllByIdInListAndDeal_idAndClient_idAndModstatusGreaterThan(payrequestIds,0,hsRes.deal.client_id,1).groupBy{it.client_id}.each{ requestlist ->
        paymentService.updatedeal(hsRes.deal,requestlist.value)
      }
    } catch(Exception e) {
      log.debug("Error save data in Payment/updatedeal\n"+e.toString());
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def deletedeal = {
    checkAccess(10)
    if (!checkSectionPermission(PCLPAYEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.deal = Deal.get(requestService.getIntDef('id',0))
    if (!hsRes.deal||Payrequest.findAllByDeal_id(hsRes.deal?.id?:0)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.deal.delete(flush:true)

    render(contentType:"application/json"){[error:false]}
    return
  }

  def confirmdeal = {
    checkAccess(10)
    if (!checkSectionPermission(PCLPAYEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.deal = Deal.get(requestService.getIntDef('id',0))
    if (hsRes.deal?.modstatus!=0||!Payrequest.findAllByDeal_id(hsRes.deal?.id?:0)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.deal.confirmDeal().save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Payment/confirmdeal\n"+e.toString());
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def cancelldeal = {
    checkAccess(10)
    if (!checkSectionPermission(PCLPAYEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.deal = Deal.get(requestService.getIntDef('id',0))
    if (hsRes.deal?.modstatus!=1) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.deal.cancellDeal().save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Payment/cancelldeal\n"+e.toString());
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def printdeal = {
    checkAccess(10)
    if (!checkSectionPermission(PCLIENTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.deal = Deal.get(requestService.getIntDef('id',0))
    if (!hsRes.deal) {
      response.sendError(404)
      return
    }

    hsRes.client = Client.get(hsRes.deal.client_id)

    renderPdf(template:'deal',model:hsRes,filename:"deal_${hsRes.deal.id}.pdf")
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Deals >>>/////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Budget payments >>>///////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def budgpaymentsfilter = {
    checkAccess(10)
    if (!checkSectionPermission(PPAYNAL)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    if (session.paymentlastRequest?.fromDetails){
      hsRes.inrequest = session.paymentlastRequest
    }
    hsRes.iscanadd = recieveSectionPermission(PPNALEDIT)

    return hsRes
  }

  def budgpayments = {
    checkAccess(10)
    if (!checkSectionPermission(PPAYNAL)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    if (session.paymentlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.paymentlastRequest
      session.paymentlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['platperiod_month','platperiod_year'],null,['companyname'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.paydate = requestService.getDate('paydate')
      hsRes.inrequest.offset = requestService.getOffset()
      session.paymentlastRequest = hsRes.inrequest
    }
    session.paymentlastRequest.paymentobject = 5

    hsRes.searchresult = new Payrequest().csiSelectPayrequest(hsRes?.inrequest+[paycat:2],20,hsRes?.inrequest?.offset)
    hsRes.taxes = Tax.list().inject([:]){map, tax -> map[tax.id]=tax.shortname;map}
    hsRes.payments = hsRes.searchresult.records.inject([:]){map, prequest -> map[prequest.id]=prequest.modstatus!=3?0:Payment.findByPayrequest_id(prequest.id)?.id?:0;map}
    hsRes.iscandelete = recieveSectionPermission(PPNALEDIT)

    return hsRes
  }

  def newbudgrequest = {
    checkAccess(10)
    if (!checkSectionPermission(PPNALEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.tax = Tax.list(sort: "name", order: "asc")

    return hsRes
  }

  def incertbudgrequest = {
    checkAccess(10)
    if (!checkSectionPermission(PPNALEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['totax_id','fromcompany_id'],null,['destination'],['paydate'],['summa'])

    if(!hsRes.inrequest.paydate)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.fromcompany_id)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.totax_id)
      hsRes.result.errorcode<<4

    if(!hsRes.result.errorcode){
      try {
        def prequest = new Payrequest().csiSetPayrequest(hsRes.inrequest+[paytype:1,paycat:2]).csiSetInitiator(hsRes.user.id).save(flush:true,failOnError:true)
        def taskpay_id = new Taskpay(paygroup:prequest.paygroup).csiSetTaskpay(term:String.format('%td.%<tm.%<tY',prequest.paydate),company_id:prequest.fromcompany_id,summa:prequest.summa).csiSetInitiator(hsRes.user.id).csiSetTaskpaystatus(0).save(flush:true,failOnError:true)?.id?:0
        prequest.csiSetTaskpay_id(taskpay_id).csiSetModstatus(1).save(flush:true,failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Payment/incertbudgrequest\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }

  def deletebudgrequest = {
    checkAccess(10)
    if (!checkSectionPermission(PPNALEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (hsRes.payrequest?.modstatus!=0||hsRes.payrequest?.paycat!=2) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest.delete(failOnError:true,flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Payment/deletebudgrequest\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Budget payments <<<///////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////T payments >>>////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def tpaymentsfilter = {
    checkAccess(10)
    if (!checkSectionPermission(PTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    if (session.paymentlastRequest?.fromDetails){
      hsRes.inrequest = session.paymentlastRequest
    }
    hsRes.iscanadd = recieveSectionPermission(PTPAYEDIT)

    return hsRes
  }

  def tpayments = {
    checkAccess(10)
    if (!checkSectionPermission(PTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    if (session.paymentlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.paymentlastRequest
      session.paymentlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['modstatus'],null,['company_name'])
      hsRes.inrequest.paydate_start = requestService.getDate('paydate_start')
      hsRes.inrequest.paydate_end = requestService.getDate('paydate_end')
      hsRes.inrequest.offset = requestService.getOffset()
      session.paymentlastRequest = hsRes.inrequest
    }
    session.paymentlastRequest.paymentobject = 6

    hsRes.searchresult = new PayrequestClientSearch().csiSelectTPayments(Client.findByIs_t(1)?.id?:-100,
                                                    hsRes.inrequest.company_name?:'', 0,
                                                    hsRes.inrequest.paydate_start,hsRes.inrequest.paydate_end,
                                                    hsRes.inrequest.modstatus?:0,0,20,hsRes.inrequest.offset)
    hsRes.iscanedit = recieveSectionPermission(PTPAYEDIT)

    return hsRes
  }

  def deletetpayment = {
    checkAccess(10)
    if (!checkSectionPermission(PTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (hsRes.payrequest?.modstatus>0||hsRes.payrequest?.deal_id>0||hsRes.payrequest?.paytype==2||hsRes.payrequest.clientcommission>0||(hsRes.payrequest?.initiator!=hsRes.user.id&&!recieveSectionPermission(PTPAYEDIT))) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Payment/deletetpayment\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def newtpayment = {
    checkAccess(10)
    if (!checkSectionPermission(PTPAYEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.fromcompany = Company.findByInn(Dynconfig.findByName('company.service.inn')?.value?:'000000000000')
    hsRes.payoffperc = requestService.getBigDecimalDef('payoffperc',0.0g)
    hsRes.payoffsumma = requestService.getBigDecimalDef('payoffsumma',0.0g) / (1 - hsRes.payoffperc / 100)
    hsRes.cashrequest_id = requestService.getIntDef('cashrequest_id',0)

    return hsRes
  }

  def incerttpayment = {
    checkAccess(10)
    if (!checkSectionPermission(PTPAYEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['tocompany_id','is_nds','cashrequest_id'],null,['tobank','destination',
                                     'comment'],['paydate'],['summa','payoffperc'])

    if(!hsRes.inrequest.paydate)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.tocompany_id)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.tobank)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<4

    if(!hsRes.result.errorcode){
      try {
        new Payrequest(cashrequest_id:hsRes.inrequest.cashrequest_id?:0).csiSetPayrequest(hsRes.inrequest+[paycat:4,paytype:hsRes.inrequest.cashrequest_id?8:1,fromcompany_id:Company.findByInn(Dynconfig.findByName('company.service.inn')?.value?:'000000000000')?.id,client_id:Client.findByIs_t(1)?.id]).csiSetPayoff(hsRes.inrequest.payoffperc).csiSetInitiator(hsRes.user.id).save(flush:true,failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Payment/incerttpayment\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }

  def tpayment = {
    checkAccess(10)
    if (!checkSectionPermission(PTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (!hsRes.payrequest||hsRes.payrequest.paytype==3) {
      response.sendError(404)
      return
    }

    hsRes.intcompany = Company.get(hsRes.payrequest.paytype in [1,8]?hsRes.payrequest.fromcompany_id:hsRes.payrequest.tocompany_id)?.name?:hsRes.payrequest.paytype in [1,8]?hsRes.payrequest.fromcompany:hsRes.payrequest.tocompany
    hsRes.extcompany = Company.get(hsRes.payrequest.paytype in [1,8]?hsRes.payrequest.tocompany_id:hsRes.payrequest.fromcompany_id)?.name?:hsRes.payrequest.paytype in [1,8]?hsRes.payrequest.tocompany:hsRes.payrequest.fromcompany
    hsRes.dinclientsaldo = (hsRes.payrequest.client_id==0?0:hsRes.payrequest.subclient_id==0?(Payrequest.findAll{ modstatus >= 0 && client_id == hsRes.payrequest.client_id && subclient_id==0 && agent_id==0 && ( paydate < hsRes.payrequest.paydate || ( paydate == hsRes.payrequest.paydate && id < hsRes.payrequest.id )) }.sum{ it.computeClientdelta() }?:0):Payrequest.findAll{ modstatus >= 0 && subclient_id == hsRes.payrequest.subclient_id && agent_id==0 && ( paydate < hsRes.payrequest.paydate || ( paydate == hsRes.payrequest.paydate && id < hsRes.payrequest.id )) }.sum{ it.computeClientdelta() }?:0)
    hsRes.curclientsaldo = Client.get(hsRes.payrequest.client_id)?.computeCurSaldo()?:0
    hsRes.initiator = User.get(hsRes.payrequest.initiator)?.name
    hsRes.clientadmin = User.get(hsRes.payrequest.clientadmin)?.name
    hsRes.iscanedit = recieveSectionPermission(PTPAYEDIT)

    return hsRes
  }

  def updatetpayrequest = {
    checkAccess(10)
    if (!checkSectionPermission(PTPAYEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10
    hsRes.result=[errorcode:[]]
 
    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (!hsRes.payrequest||!(hsRes.payrequest.paytype in [1,2,8])) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['client_id'],null,null,null,['payoffperc'])
    def hsData

    imageService.init(this)

    hsData = imageService.rawUpload('file',true)
    if(hsData.error in [1,3])
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        hsRes.payrequest.setTClientData(hsRes.inrequest).csiSetFileId(imageService.rawUpload('file').fileid).csiSetPayoff(hsRes.inrequest.payoffperc).csiSetClientadmin(hsRes.user.id).save(failOnError:true,flush:true)
        if (hsRes.payrequest.deal_id) Deal.get(hsRes.payrequest.deal_id)?.recomputeDeal()?.save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Payment/updatetpayrequest\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    return hsRes.result
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////T payments <<<////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////  
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////payproject >>> /////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def prjpaymentsfilter = {
    checkAccess(10)
    if (!checkSectionPermission(PPRJPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10
    
    if (session.paymentlastRequest?.fromDetails){
      hsRes.inrequest = session.paymentlastRequest
    }     
    hsRes.projects = new Project().csiSearchUserProject(hsRes.user?.id)    
    
    return hsRes
  }

  def prjpayments = {
    checkAccess(10)
    if (!checkSectionPermission(PPRJPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10
    hsRes.searchresult=[]

    if (session.paymentlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.paymentlastRequest
      session.paymentlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['project_id'])
      hsRes.inrequest.offset = requestService.getOffset()
      session.paymentlastRequest = hsRes.inrequest      
    }
    session.paymentlastRequest.paymentobject = 8    
    
    if(hsRes.inrequest.project_id>0){
      hsRes.project = Project.get(hsRes.inrequest.project_id)
      hsRes.searchresult = new PayrequestProjectSearch().csiSelectProjectPayments(hsRes.project?.id)
      hsRes.taxes = Tax.list().inject([:]){map, tax -> map[tax.id]=tax.shortname;map}
      hsRes.agrtypes = Agreementtype.list().inject([:]){map, type -> map[type.id]=type.name2;map}
    }
    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////payproject <<< /////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Dop cards >>> ////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def dcpaymentsfilter = {
    checkAccess(10)
    if (!checkSectionPermission(PDPCPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    if (session.sallastRequest?.fromDetails){
      hsRes.inrequest = session.sallastRequest
    }

    hsRes.reports = Salaryreport.findAllBySalarytype_id(3,[order:'desc',sort:'repdate']).collect{[disvalue:String.format('%tY-%<tm',new Date(it.year-1900,it.month-1,1)),keyvalue:String.format('%td.%<tm.%<tY',new Date(it.year-1900,it.month-1,1))]}

    return hsRes
  }

  def dcpayments = {
    checkAccess(10)
    if (!checkSectionPermission(PDPCPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    session.paymentlastRequest = [:]
    session.paymentlastRequest.paymentobject = 9
    hsRes+=requestService.getParams(null,null,['company_name','bank_name'])
    hsRes.inrequest.repdate = requestService.getDate('repdate')?:new Date()

    hsRes.searchresult = new SalarycompSearch().csiSelectDCPayments(hsRes.inrequest.repdate,hsRes.inrequest.bank_name?:'',hsRes.inrequest.company_name?:'',requestService.getStr('viewtype')=='table'?20:-1,requestService.getOffset())

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'dcpayments', model: hsRes, filename: "dcpayments.pdf")
      return
    }

    return hsRes
  }

  def dcpaymentsXLS = {
    checkAccess(10)
    if (!checkSectionPermission(PDPCPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    session.paymentlastRequest = [:]
    session.paymentlastRequest.paymentobject = 9
    hsRes+=requestService.getParams(null,null,['company_name','bank_name'])
    hsRes.inrequest.repdate = requestService.getDate('repdate')?:new Date()

    hsRes.searchresult = new SalarycompSearch().csiSelectDCPayments(hsRes.inrequest.repdate,hsRes.inrequest.bank_name?:'',hsRes.inrequest.company_name?:'',requestService.getStr('viewtype')=='table'?20:-1,requestService.getOffset())

    if (hsRes.searchresult.records.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        fillRow(['Компания','Работник','Банк','Номер карты','PIN код','Сумма','Статус'],3,false)
        hsRes.searchresult.records.each{ record ->
          fillRow([record.companyname,
                   record.fio,
                   record.bankname,
                   record.nomer,
                   record.pin,
                   Tools.toFixed(record.cardadd,2),
                   record.paidaddstatus==1?'В оплате':record.paidaddstatus==2?'Оплачено':'Новый'], rowCounter++, false)
        }
        save(response.outputStream)
      }
    }
    return
  }

  def dcincomefilter = {
    checkAccess(10)
    if (!checkSectionPermission(PDPCPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    if (session.sallastRequest?.fromDetails){
      hsRes.inrequest = session.sallastRequest
    }

    return hsRes
  }

  def dcincome = {
    checkAccess(10)
    if (!checkSectionPermission(PDPCPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    session.paymentlastRequest = [:]
    session.paymentlastRequest.paymentobject = 10
    hsRes.incomedate = new Date(requestService.getIntDef('incomedate_year',2015)-1900,requestService.getIntDef('incomedate_month',1)-1,1)
    hsRes.offset = requestService.getOffset()

    hsRes.searchresult = new CashSearch().csiSelectDCReturn(hsRes.incomedate,20,hsRes.offset)

    return hsRes
  }

  def dccomissionfilter = {
    checkAccess(10)
    if (!checkSectionPermission(PDPCPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    if (session.sallastRequest?.fromDetails){
      hsRes.inrequest = session.sallastRequest
    }

    return hsRes
  }

  def dccomissions = {
    checkAccess(10)
    if (!checkSectionPermission(PDPCPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    session.paymentlastRequest = [:]
    session.paymentlastRequest.paymentobject = 11

    hsRes.searchresult = new Payrequest().csiSelectPayrequest([platperiod_month:requestService.getIntDef('comissiondate_month',1),platperiod_year:requestService.getIntDef('comissiondate_year',2015),paytype:9],20,requestService.getOffset())

    return hsRes
  }

  def dccomission = {
    checkAccess(10)
    if (!checkSectionPermission(PDPCPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10

    def lId=requestService.getIntDef('id',0)
    hsRes.dccomission = Payrequest.get(lId)
    if (!hsRes.dccomission&&lId) {
      response.sendError(404)
      return
    }

    hsRes.iscanedit = (hsRes.dccomission?.id==Payrequest.getLastComissionId()&&hsRes.dccomission?.deal_id==0)||!hsRes.dccomission

    return hsRes
  }

  def updatedccomission = {
    checkAccess(10)
    if (!checkSectionPermission(PDPCPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 10
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.dccomission = Payrequest.findByIdAndPaytype(lId,9)
    if (!hsRes.dccomission&&lId) {
      response.sendError(404)
      return
    }

    hsRes+=requestService.getParams(null,null,['comment'],['paydate'],['summa'])
    hsRes.inrequest.platperiod = String.format('%tm.%<tY',new Date(requestService.getIntDef('platperiod_year',2015)-1900,requestService.getIntDef('platperiod_month',1)-1,1))
    hsRes.iscanedit = (hsRes.dccomission?.id==Payrequest.getLastComissionId()&&hsRes.dccomission?.deal_id==0)||!hsRes.dccomission
    if(!hsRes.iscanedit) hsRes.inrequest.summa = hsRes.dccomission.summa

    if(!hsRes.inrequest.paydate)
      hsRes.result.errorcode<<1
    if(hsRes.iscanedit&&!hsRes.inrequest.summa)
      hsRes.result.errorcode<<2

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.dccomission = new Payrequest()
        hsRes.result.dccomission = hsRes.dccomission.csiSetPayrequest(hsRes.inrequest+[paytype:9,paycat:4,client_id:Client.findByIs_t(1)?.id]).csiSetInitiator(hsRes.user.id).save(flush:true,failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Payment/updatedccomission\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Dop cards <<< ////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
}