import grails.converters.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class CatalogController {
  def requestService
  def parseService

  final String CENQEDIT = 'is_enquiryedit'
  final String CCLEDIT = 'is_clientedit'
  final String CPREDIT = 'is_projectedit'
  final String CAGEDIT = 'is_agentedit'
  final String CEXPEDIT = 'is_expenseedit'
  final String CPOSEDIT = 'is_positionedit'
  final String CDEPEDIT = 'is_departmentedit'
  final String CKREDEDIT = 'is_kreditedit'
  final String CAGENT = 'is_agent'
  final String CCONFIG = 'is_config'
  final String CCGROUP = 'is_cgroup'
  final String CTTYPE = 'is_taskall'
  final String CCAR = 'is_lizingedit'
  final String CVISGREDIT = 'is_visualgroup'

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
	    redirect(controller:'user',action:'panel')
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
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails&&session.cataloglastRequest){
      session.cataloglastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.cataloglastRequest      
    }else{     
      hsRes.inrequest=[:]
      hsRes.inrequest.catobject=requestService.getIntDef('catobject',0)        
    }
    hsRes.isenquiry = recieveSectionPermission(CENQEDIT)
    hsRes.iscgroup = recieveSectionPermission(CCGROUP)
    hsRes.isttype = recieveSectionPermission(CTTYPE)
    hsRes.iscar = recieveSectionPermission(CCAR)
    hsRes.isvgroup = recieveSectionPermission(CVISGREDIT)

    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Bank >>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def bankfilter = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = session.user
    hsRes.action_id = 6

    if (session.cataloglastRequest?.fromDetails){
      hsRes.inrequest = session.cataloglastRequest
    }

    return hsRes
  }

  def banklist = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = session.user
    hsRes.action_id = 6

    if (session.cataloglastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.cataloglastRequest
      session.cataloglastRequest.fromDetails = 0
    } else {
      session.cataloglastRequest = null
      hsRes+=requestService.getParams(['is_license','is_my'],null,['bank_id','bankname','bankcity'])
      hsRes.inrequest.offset = requestService.getOffset()
      session.cataloglastRequest = hsRes.inrequest
    }
    session.cataloglastRequest.catobject = 0

    hsRes.searchresult = new Bank().csiSelectBanks(hsRes.inrequest.bank_id?:'',hsRes.inrequest.bankname?:'',hsRes.inrequest.bankcity?:'',
                                                   hsRes.inrequest.is_license?:0,hsRes.inrequest.is_my?:0,20,hsRes.inrequest.offset)

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def bankdetail={
    checkAccess(6)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    hsRes.action_id = 6
    hsRes.user = session.user        

    def sId=requestService.getStr('id')    
    hsRes.bank = Bank.get(sId?:'0')
    if ((!hsRes.bank&&sId) || (!sId && !session.user?.group?.is_bankinsert)) {
      response.sendError(404)
      return
    }       
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def saveBankDetail = {
    checkAccess(6)
    requestService.init(this)
    def hsRes=[:]   
    hsRes.user = session.user
    
    hsRes+=requestService.getParams(['is_edit','is_license','rko_rate','open_rate','ibankopen_rate','ibankserv_rate','is_foreign',
                                     'plat_rate','platreturn_rate','race_rate','income_rate','urgent_rate','besp_rate',
                                     'spravka_rate','addline_rate','vypiska_rate','is_local','ibankterm','is_sanation'],null,
                                    ['id','address','city','name','coraccount','stopdate','tel','contactinfo','operinfo',
                                    'techinfo','comment','prevnameinfo','shortname'])
    
    hsRes.result=[errorcode:[]]
    if(!hsRes.inrequest.id)     
      hsRes.result.errorcode<<1
    else if(!hsRes.inrequest.is_foreign && !hsRes.inrequest.id.matches('\\d{9}'))     
      hsRes.result.errorcode<<111  
    else if(!hsRes.inrequest.is_edit && Bank.get(hsRes.inrequest.id))  
      hsRes.result.errorcode<<112
      
    if(requestService.getStr('ibankterm') && requestService.getStr('ibankterm')!='0' && !hsRes.inrequest.ibankterm)     
      hsRes.result.errorcode<<113

    if(!hsRes.inrequest.is_license && !hsRes.inrequest.stopdate)     
      hsRes.result.errorcode<<1121
    else if(hsRes.inrequest.is_license && hsRes.inrequest.stopdate)     
      hsRes.result.errorcode<<1122
      
    if(!hsRes.inrequest.name)     
      hsRes.result.errorcode<<2  
    /*if(!hsRes.inrequest.coraccount)     
      hsRes.result.errorcode<<3*/
    if(!hsRes.inrequest.is_foreign && hsRes.inrequest.coraccount && !hsRes.inrequest.coraccount.matches('\\d{20}'))     
      hsRes.result.errorcode<<4           
    if(hsRes.inrequest.stopdate && !(hsRes.inrequest.stopdate).matches('\\d{2}\\.\\d{2}\\.\\d{4}'))     
      hsRes.result.errorcode<<5  
      
    if(!hsRes.inrequest.is_edit){
      
      if(requestService.getStr('rko_rate') && requestService.getStr('rko_rate')!='0' && !hsRes.inrequest.rko_rate)     
        hsRes.result.errorcode<<6
      if(requestService.getStr('open_rate') && requestService.getStr('open_rate')!='0' && !hsRes.inrequest.open_rate)     
        hsRes.result.errorcode<<7 
      if(requestService.getStr('ibankopen_rate') && requestService.getStr('ibankopen_rate')!='0' && !hsRes.inrequest.ibankopen_rate)     
        hsRes.result.errorcode<<8
      if(requestService.getStr('ibankserv_rate') && requestService.getStr('ibankserv_rate')!='0' && !hsRes.inrequest.ibankserv_rate)     
        hsRes.result.errorcode<<9      
      if(requestService.getStr('plat_rate') && requestService.getStr('plat_rate')!='0' && !hsRes.inrequest.plat_rate)     
        hsRes.result.errorcode<<10  
      if(requestService.getStr('platreturn_rate') && requestService.getStr('platreturn_rate')!='0' && !hsRes.inrequest.platreturn_rate)     
        hsRes.result.errorcode<<11   
      if(requestService.getStr('race_rate') && requestService.getStr('race_rate')!='0' && !hsRes.inrequest.race_rate)     
        hsRes.result.errorcode<<12 
      if(requestService.getStr('income_rate') && requestService.getStr('income_rate')!='0' && !hsRes.inrequest.income_rate)     
        hsRes.result.errorcode<<13 
      if(requestService.getStr('urgent_rate') && requestService.getStr('urgent_rate')!='0' && !hsRes.inrequest.urgent_rate)     
        hsRes.result.errorcode<<14 
      if(requestService.getStr('besp_rate') && requestService.getStr('besp_rate')!='0' && !hsRes.inrequest.besp_rate)     
        hsRes.result.errorcode<<15  
      if(requestService.getStr('spravka_rate') && requestService.getStr('spravka_rate')!='0' && !hsRes.inrequest.spravka_rate)     
        hsRes.result.errorcode<<16
      if(requestService.getStr('addline_rate') && requestService.getStr('addline_rate')!='0' && !hsRes.inrequest.addline_rate)     
        hsRes.result.errorcode<<17
      if(requestService.getStr('vypiska_rate') && requestService.getStr('vypiska_rate')!='0' && !hsRes.inrequest.vypiska_rate)     
        hsRes.result.errorcode<<18   
    }      
    if(hsRes.result.errorcode){
      render(contentType:"application/json"){[error:true,errorcode:hsRes.result.errorcode]}
      return
    }     
      
    def oBank        
             
    if(hsRes.inrequest.id){
      if(hsRes.inrequest.is_edit)
        oBank=Bank.get(hsRes.inrequest.id)
      else    
        oBank=new Bank()
    }        
    if ((!oBank&&hsRes.inrequest.is_edit) || (hsRes.inrequest.is_edit && !session.user?.group?.is_bankedit) || (!hsRes.inrequest.is_edit && !session.user?.group?.is_bankinsert)) {
      render(contentType:"application/json"){[error:true]}
      return
    }     
    
    def hsHistory=[:]
    def bHistory=0
    if(oBank){
      if((oBank.name!=(hsRes.inrequest?.name?:'')) || (oBank.coraccount!=(hsRes.inrequest?.coraccount?:'')) ||
        (oBank.is_license!=(hsRes.inrequest?.is_license?:0)) || (oBank.stopdate!=Tools.getDate(hsRes.inrequest?.stopdate?:'')) ||
        (oBank.tel!=(hsRes.inrequest?.tel?:''))){       
	      bHistory=1
      }
    }  
    hsHistory?.is_license=oBank.is_license
    
    oBank.csiSetBank(hsRes.inrequest).save(flush:true,failOnError:true)      
    
    if(bHistory){
      hsRes.inrequest.bank_id=oBank.id
      new Bankhistory().csiSetBankhistory(hsRes.inrequest,hsRes.user.id).save(flush:true,failOnError:true)                    
    }

    if((hsHistory?.is_license==1 && !hsRes.inrequest?.is_license)||hsRes.inrequest.is_sanation){
      for(oCompany in new Company().csiFindBankAccounts(oBank.id,-1,0).records){
        def oBankaccount = Bankaccount.findByCompany_idAndBank_id(oCompany.id?:0,oBank.id)
        if(oBankaccount){
          oBankaccount.csiSetIbankblock(1).updateIbankstatus().save(flush:true,failOnError:true)
        }
      }
    }

    flash.bankedit_success=1

    render(contentType:"application/json"){[error:false,bank_id:oBank.id?:0]}
    return
  }

  def bankcompanylist={
    checkAccess(6)
    requestService.init(this)
    def hsRes=[inrequest:[:]]   
    hsRes.user = session.user
    
    hsRes.inrequest.id=requestService.getStr('id')
    if(hsRes.inrequest.id && hsRes.user?.group?.is_bankedit){
      hsRes.searchresult = new Company().csiFindBankAccounts(hsRes.inrequest.id,20,requestService.getOffset())
      
      hsRes.bankaccount=[]       
      for(oCompany in hsRes.searchresult.records){               
        hsRes.bankaccount<<Bankaccount.findByModstatusAndCompany_idAndBank_id(1,oCompany?.id,hsRes.inrequest.id)
      }  
    }
    return hsRes      
  }
  def parseCSVBankFile={
    checkAccess(6)
    if(session.user?.group?.is_bankinsert)    
      render(view:'parseCSVFile',model:parseService.parseCSVBankFile(request.getFile('file')))    
    return
  }
  def bankhistorylist={
    checkAccess(6)
    requestService.init(this)
    def hsRes=[inrequest:[:]]   
    hsRes.user = session.user
    
    def sId=requestService.getStr('id')
    
    hsRes.searchresult=[:]    
    hsRes.searchresult.records=Bankhistory.findAllByBank_id(sId,[sort:'id',order:'desc'])
    return hsRes
  }  
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Bank <<<//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Tax >>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def taxinspectionfilter = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = session.user
    hsRes.action_id = 6
    
    if (session.cataloglastRequest?.fromDetails){
      hsRes.inrequest = session.cataloglastRequest
    }    

    return hsRes
  }

  def taxinspectionlist = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = session.user
    hsRes.action_id = 6

    if (session.cataloglastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.cataloglastRequest
      session.cataloglastRequest.fromDetails = 0
    } else {
      session.cataloglastRequest = null
      hsRes+=requestService.getParams(null,null,['taxinspection_id'])
      hsRes.inrequest.offset = requestService.getOffset()
      session.cataloglastRequest = hsRes.inrequest
    }
    session.cataloglastRequest.catobject = 1

    hsRes.searchresult = new Taxinspection().csiSelectInspections(hsRes.inrequest.taxinspection_id?:'',20,hsRes.inrequest.offset)

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def taxdetail={
    checkAccess(6)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    hsRes.action_id = 6
    hsRes.user = session.user        

    def lId=requestService.getStr('id')    
    hsRes.tax = Taxinspection.get(lId?:'0')
    if ((!hsRes.tax&&lId) || (lId && !session.user?.group?.is_taxedit) || (!lId && !session.user?.group?.is_taxinsert)) {
      response.sendError(404)
      return
    }       
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def saveTaxDetail = {
    checkAccess(6)
    requestService.init(this)
    def hsRes=[:]   
    hsRes.user = session.user
    
    hsRes+=requestService.getParams(['is_edit'],[],['id','address','district','name','tel'])
    
    hsRes.result=[errorcode:[]]
    if(!hsRes.inrequest.id)     
      hsRes.result.errorcode<<1
    else if(!hsRes.inrequest.id.matches('\\d{4}'))     
      hsRes.result.errorcode<<2  
    else if(!hsRes.inrequest.is_edit && Taxinspection.get(hsRes.inrequest.id))  
      hsRes.result.errorcode<<3    
     
    def oTaxinspection
      
    if(!hsRes.result.errorcode){          
      if(hsRes.inrequest.id){
        if(hsRes.inrequest.is_edit)
          oTaxinspection=Taxinspection.get(hsRes.inrequest.id)
        else  
          oTaxinspection=new Taxinspection()
      }        
      if ((!oTaxinspection&&hsRes.inrequest.id)  || (hsRes.inrequest.id && !session.user?.group?.is_taxedit) || (!hsRes.inrequest.id && !session.user?.group?.is_taxinsert)) {
        render(contentType:"application/json"){[error:true]}
        return
      }
    }
     
    if(!hsRes.inrequest.name)     
      hsRes.result.errorcode<<4       
    if(!hsRes.inrequest.address)     
      hsRes.result.errorcode<<5          
      
    if(hsRes.result.errorcode){
      render(contentType:"application/json"){[error:true,errorcode:hsRes.result.errorcode]}
      return
    }            
    
    oTaxinspection.csiSetTaxinspection(hsRes.inrequest).save(flush:true,failOnError:true)      
    
    flash.taxedit_success=1
    
    render(contentType:"application/json"){[error:false,tax_id:oTaxinspection.id?:0]}
    return      
  }
  def parseCSVTaxinspectionFile={ 
    checkAccess(6)
    if(session.user?.group?.is_taxinsert)   
      render(view:'parseCSVFile',model:parseService.parseCSVTaxinspectionFile(request.getFile('file')))    
    return     
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Tax <<<//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////OKTMO >>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def oktmofilter = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = session.user
    hsRes.action_id = 6    

    return hsRes
  }

  def oktmolist = {
    checkAccess(6)
    requestService.init(this)    
    def hsRes=[:]
    hsRes+=requestService.getParams(null,null,['oktmo','okato'])    

    hsRes.searchresult = new Oktmo().csiSelectOktmo(hsRes.inrequest.oktmo?:'',hsRes.inrequest.okato?:'',20,requestService.getOffset())

    return hsRes
  }  
  def parseCSVOktmoFile={
    checkAccess(6)
    requestService.init(this)

    if(session.user?.group?.is_oktmoinsert)       
      render(view:'parseCSVFile',model:parseService.parseCSVOktmoFile(request.getFile('file')))    
    return    
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////OKTMO <<<//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////OKVED >>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def okvedfilter = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = session.user
    hsRes.action_id = 6    

    return hsRes
  }

  def okvedlist = {
    checkAccess(6)
    requestService.init(this)    
    def hsRes=[:]
    hsRes+=requestService.getParams(null,null,['id','name','razdel'])    
    
    hsRes.searchresult = new Okved().csiSelectOkved(hsRes.inrequest.id?:'',hsRes.inrequest.name?:'',hsRes.inrequest.razdel?:'',20,requestService.getOffset())

    return hsRes
  }
  def parseCSVOkvedFile={
    checkAccess(6)
    requestService.init(this)    
    
    if(session.user?.group?.is_okvedinsert)
      render(view:'parseCSVFile',model:parseService.parseCSVOkvedFile(request.getFile('file')))    
    return     
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////OKVED <<<//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
 //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////OKTMO <<<//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////KBK >>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def kbkfilter = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = session.user
    hsRes.action_id = 6 
    
    hsRes.razdel= Kbkrazdel.list()

    return hsRes
  }

  def kbklist = {
    checkAccess(6)
    requestService.init(this)   
    def hsRes=[:]
    hsRes+=requestService.getParams(['kbkrazdel_id'],null,['kbksearch'])    
    
    hsRes.searchresult = new Kbk().csiSelectKbk(hsRes.inrequest.kbksearch?:'',hsRes.inrequest.kbkrazdel_id?:0,20,requestService.getOffset())

    return hsRes
  }
  def parseCSVKbkFile={ 
    checkAccess(6)
    requestService.init(this)
    
    if(session.user?.group?.is_kbkinsert)    
      render(view:'parseCSVFile',model:parseService.parseCSVKbkFile(request.getFile('file')))    
    return     
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////KBK <<<//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Rashod Razdel >>>///////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def exprazdelfilter = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6
    
    if (session.cataloglastRequest)
      hsRes.inrequest = session.cataloglastRequest
    
    hsRes.iscanadd = recieveSectionPermission(CEXPEDIT)    

    return hsRes
  }
  def exprazdellist = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    if (session.cataloglastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.cataloglastRequest
      session.cataloglastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(null,null,['name'])
      session.cataloglastRequest = hsRes.inrequest
    }
    session.cataloglastRequest.catobject = 5

    if(hsRes.inrequest.name)
      hsRes.expensetypes = Expensetype1.findAllByName(hsRes.inrequest.name)
    else
      hsRes.expensetypes = Expensetype1.list()
    hsRes.exp2counts = hsRes.expensetypes.inject([:]){map, exp -> map[exp.id]=Expensetype2.countByExpensetype1_id(exp.id);map}

    return hsRes
  }
  def exprazdel = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    def lId = requestService.getIntDef('id',0)
    hsRes.expensetype = Expensetype1.get(lId)
    if (!hsRes.expensetype&&lId) {
      response.sendError(404)
      return
    }
    hsRes.iscanedit = recieveSectionPermission(CEXPEDIT)
    session.cataloglastRequest.catobject = 5

    return hsRes
  }
  
  def updateexprazdel = {
    checkAccess(6)
    if (!checkSectionPermission(CEXPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.expensetype = Expensetype1.get(lId)
    if (!hsRes.expensetype&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,['name'])

    if(!hsRes.inrequest.name)
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.expensetype = new Expensetype1()
        hsRes.result.exp_id = hsRes.expensetype.setData(hsRes.inrequest).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Catalog/updateexprazdel\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  
  def removeexprazdel = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6
    hsRes.result=[errorcode:[]]
    
    def lId = requestService.getIntDef('id',0)
     
    if(Expensetype2.findAllByExpensetype1_id(lId))
      hsRes.result.errorcode<<1
      
    if(!hsRes.result.errorcode){
      try {
        Expensetype1.get(lId)?.delete(flush:true)
      } catch(Exception e) {
        log.debug("Error save data in Catalog/removeexprazdel\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }
  
  def expensetypes2 = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = [:]
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6
    
    def lId = requestService.getIntDef('id',0)
    hsRes.expensetype = Expensetype1.get(lId)
    if (!hsRes.expensetype&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.expensetypes2 = Expensetype2.findAllByExpensetype1_id(lId)
    hsRes.exptypescounts = hsRes.expensetypes2.inject([:]){map, exp -> map[exp.id]=Expensetype.countByExpensetype1_idAndExpensetype2_id(lId,exp.id);map}
    
    return hsRes
  }

  def expensetype2 = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    def lId = requestService.getIntDef('id',0)
    hsRes+=requestService.getParams(['expensetype1_id'])
    hsRes.expensetype2 = Expensetype2.get(lId)
    if (!hsRes.expensetype2&&lId) {
      response.sendError(404)
      return
    }
    hsRes.iscanedit = recieveSectionPermission(CEXPEDIT)

    return hsRes
  }
  
  def updateexpensetype2 = {
    checkAccess(6)
    if (!checkSectionPermission(CEXPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.expensetype2 = Expensetype2.get(lId)
    if (!hsRes.expensetype2&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['expensetype1_id'],null,['name'])

    if(!hsRes.inrequest.name)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.expensetype1_id)
      hsRes.result.errorcode<<2

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.expensetype2 = new Expensetype2()
        hsRes.result.exp_id = hsRes.expensetype2.setData(hsRes.inrequest).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Catalog/updateexpensetype2\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  
  def removeexpensetype2 = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    def oExpensetype2 = Expensetype2.get(requestService.getIntDef('id',0))

    try {
      if (!Expensetype.findByExpensetype1_idAndExpensetype2_id(oExpensetype2?.expensetype1_id?:0,oExpensetype2?.id?:0)) oExpensetype2?.delete(flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Catalog/removeexpensetype2\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }  
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Rashod >>>////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def expensetypes = {
    checkAccess(19)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 19

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails&&session.explastRequest){
      session.explastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.explastRequest
    }
    hsRes.iscanadd = recieveSectionPermission(CEXPEDIT)

    return hsRes
  }

  def expensetypeslist = {
    checkAccess(19)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 19

    if (session.explastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.explastRequest
      session.explastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['expensetype1_id','expensetype2_id','modstatus'],null,['name'])
      hsRes.inrequest.offset = requestService.getOffset()
      session.explastRequest = hsRes.inrequest
    }

    hsRes.expensetypes = new ExpensetypeSearch().csiSelectTypes(hsRes.inrequest.expensetype1_id?:0,hsRes.inrequest.expensetype2_id?:0,hsRes.inrequest.name?:'',hsRes.inrequest.modstatus?:0,20,hsRes.inrequest.offset)

    return hsRes
  }

  def expensetype = {
    checkAccess(19)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 19

    def lId = requestService.getIntDef('id',0)
    hsRes.exptype = Expensetype.get(lId)
    if (!hsRes.exptype&&lId) {
      response.sendError(404)
      return
    }
    hsRes.exp1 = Expensetype1.list(sort:'name')
    hsRes.exp2 = Expensetype2.list(sort:'name')
    hsRes.iscanedit = recieveSectionPermission(CEXPEDIT)

    return hsRes
  }
  
  def expensetype2list = {
    checkAccess(19)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 19

    def iId=requestService.getIntDef('id',0)    
    if(iId>=0)
      hsRes.expensetype2=Expensetype2.findAllByExpensetype1_id(iId,[sort:'name'])
    
    return hsRes
  }

  def updateexpensetype = {
    checkAccess(19)
    if (!checkSectionPermission(CEXPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 19
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.exptype = Expensetype.get(lId)
    if (!hsRes.exptype&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['expensetype1_id','expensetype2_id','is_car','modstatus'],null,['name'])

    if(!hsRes.inrequest.name)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.expensetype1_id)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.expensetype2_id)
      hsRes.result.errorcode<<3

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.exptype = new Expensetype()
        hsRes.result.exptype = hsRes.exptype.setData(hsRes.inrequest).csiSetModstatus(hsRes.inrequest.modstatus).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Catalog/updateexpensetype\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Rashod <<<////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Department >>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  
  def departments = {
    checkAccess(18)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 18
    
    def fromDetails = requestService.getIntDef('fromDetails',0)
    
    if (fromDetails&&session.cataloglastRequest){
      session.cataloglastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.cataloglastRequest
    }
    hsRes.iscanadd = recieveSectionPermission(CDEPEDIT)
    
    return hsRes
  }

  def departmentlist = {
    checkAccess(18)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 18
    
    if (session.cataloglastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.cataloglastRequest
      session.cataloglastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(null,null,['name'])
      session.cataloglastRequest = hsRes.inrequest
    }
    
    if(hsRes.inrequest.name)
      hsRes.departments = Department.findAllByNameIlike('%'+hsRes.inrequest.name+'%',[sort:'name',order:'ask'])
    else  
      hsRes.departments = Department.list(sort:'name',order:'ask')
      
    hsRes.users_count = []
    for(record in hsRes.departments)
      hsRes.users_count << User.countByDepartment_idAndModstatus(record.id,1)
      
    hsRes.iscanedit = recieveSectionPermission(CDEPEDIT)

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def departmentdetail = {
    checkAccess(18)
    requestService.init(this)  
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = session.user
    hsRes.action_id = 18
    
    def iId = requestService.getIntDef('id',0)    
    if(iId)
      hsRes.department = Department.get(iId)      
    
    if((iId && !hsRes.department) || !session.user?.group?.is_departmentedit){
      response.sendError(404)
      return
    }
    hsRes.projects = Project.findAll('from Project order by is_main desc, name asc')
    
    return hsRes   
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def saveDepartmentDetail= {
    checkAccess(18)
    requestService.init(this)    
    def hsRes = [:]  
    hsRes.user = session.user              
    hsRes+= [error:false]
    hsRes.errorcode=[]
    
    def iId = requestService.getIntDef('id',0)
    hsRes+=requestService.getParams(['is_dep','parent','is_cashextstaff','project_id'],null,['name'])
     
    def oDepartment
    if (hsRes.inrequest.name) {
      if(iId)
        oDepartment=Department.get(iId)         
      else
        oDepartment=new Department() 

      if((iId && !oDepartment) || !session.user?.group?.is_departmentedit){
        response.sendError(404)
        return
      }        
      
      oDepartment.csiSetDepartment(hsRes.inrequest).save(flush:true,failOnError:true)      
      
    }else{
      hsRes = [error:true,errorcode:1]
    }
    if(!hsRes.error)
      flash.department_success=1
      
    hsRes.department_id = oDepartment.id
    
    render hsRes as JSON
    return
  }
   ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def departmentexpensetype = {
    checkAccess(18)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 18

    hsRes.department = Department.get(requestService.getIntDef('id',0))
    if (!hsRes.department) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.expensetypeRazdel = new ExpensetypeSearch().csiGetRazdel()
    hsRes.expensetypes = Expensetype.list()
    hsRes.depexptypes = new ExpensetypeSearch().csiGetDepartmentTypes(hsRes.department.id)

    return hsRes
  }

  def departmentExpensetypeSave = {
    checkAccess(18)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 18

    hsRes.department = Department.get(requestService.getIntDef('id',0))
    if(!hsRes.department){
      render(contentType:"application/json"){[error:true]}
      return
    }

    try{
      Expense2dep.findAllByDepartment_id(hsRes.department.id).each{ it.delete(flush:true) }
      Expensetype.list().each { exptype ->
        if(requestService.getIntDef("exp_id_"+exptype.id,0)){
          new Expense2dep().csiSetExpense2dep(expensetype_id:exptype.id,department_id:hsRes.department.id).save(failOnError:true)
        }
      }
    } catch(Exception e){
      log.debug('error in Catalog.departmentExpensetypeSave')
      log.debug(e.toString())
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def departmentuserlist = {
    checkAccess(18)
    requestService.init(this)
    def hsRes = [:] 
    hsRes.user = session.user  
    
    hsRes.inrequest = [id:requestService.getIntDef('id',0)]
    hsRes.department=Department.get(hsRes.inrequest.id)    
    if(hsRes.department)
      hsRes.users=User.findAllByDepartment_id(hsRes.inrequest.id,[sort:'name',order:'asc'])
    
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def departmentsublist = {
    checkAccess(18)
    requestService.init(this)
    def hsRes = [:] 
    hsRes.user = session.user  
    
    hsRes.inrequest = [id:requestService.getIntDef('id',0)]
    hsRes.department=Department.get(hsRes.inrequest.id)    
    if(hsRes.department)
      hsRes.subs=Department.findAllByIs_depAndParent(0,hsRes.inrequest.id,[sort:'name',order:'asc'])
    
    return hsRes
  }  
    
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Department <<<//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Project >>>///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def projects = {
    checkAccess(14)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 14

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails&&session.cataloglastRequest){
      session.cataloglastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.cataloglastRequest
    }
    hsRes.iscanadd = recieveSectionPermission(CPREDIT)

    return hsRes
  }

  def projectlist = {
    checkAccess(14)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)

    if (session.cataloglastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.cataloglastRequest
      session.cataloglastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['modstatus'],null,['name'])
      hsRes.inrequest.offset = requestService.getOffset()
      session.cataloglastRequest = hsRes.inrequest
    }

    hsRes.searchresult = new Project().csiSelectProject(hsRes.inrequest.name?:'',hsRes.inrequest.modstatus?:0,20,hsRes.inrequest.offset)

    return hsRes
  }

  def project = {
    checkAccess(14)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 14

    def lId=requestService.getIntDef('id',0)
    hsRes.project = Project.get(lId)
    if (!hsRes.project&&lId) {
      response.sendError(404)
      return
    }
    hsRes.iscanedit = recieveSectionPermission(CPREDIT)
    hsRes.income = Payrequest.findAllByPaytypeAndProject_idAndModstatusGreaterThan(2,hsRes.project?.id,1).sum{ it.summa }?:0.0g
    hsRes.outlay = Payrequest.findAllByPaytypeAndProject_idAndModstatusGreaterThan(1,hsRes.project?.id,1).sum{ it.summa }?:0.0g

    return hsRes
  }

  def updateproject = {
    checkAccess(14)
    if (!checkSectionPermission(CPREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 14
    hsRes.result=[errorcode:[]]

    def iId = requestService.getIntDef('id',0)
    hsRes.project = Project.get(iId)
    if (!hsRes.project&&iId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,['name','description'])
    hsRes.inrequest.startdate = requestService.getDate('startdate')
    hsRes.inrequest.enddate = requestService.getDate('enddate')

    if(!hsRes.inrequest.name)
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        if(!iId) hsRes.project = new Project()
        hsRes.result.project_id = hsRes.project.csiSetProject(hsRes.inrequest).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Catalog/updateproject\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def projectpayments = {
    checkAccess(14)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 14

    hsRes.project = Project.get(requestService.getIntDef('id',0))
    if (!hsRes.project) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payments = new PayrequestProjectSearch().csiSelectProjectPayments([project_id:hsRes.project.id],20,0)

    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Project <<<///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
   //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Spacetype >>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def spacefilter = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = session.user
    hsRes.action_id = 6    
    
    if (session.cataloglastRequest?.fromDetails){
      hsRes.inrequest = session.cataloglastRequest
    }
    
    session.cataloglastRequest.catobject = 8

    return hsRes
  }

  def spacelist = {
    checkAccess(6)
    requestService.init(this)    
    def hsRes=[searchresult:[:],inrequest:[:]]
    hsRes.user = session.user    
    
    if (session.cataloglastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.cataloglastRequest
      session.cataloglastRequest.fromDetails = 0
    } else {      
      hsRes.inrequest.offset = requestService.getOffset()
      session.cataloglastRequest = hsRes.inrequest
    }
    session.cataloglastRequest.catobject = 8
    
    hsRes.searchresult = Spacetype.list(sort: "name", order: "asc")

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def spacedetail={
    checkAccess(6)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    hsRes.action_id = 6
    hsRes.user = session.user        

    def lId=requestService.getIntDef('id',0)    
    hsRes.space = Spacetype.get(lId)
    if ((!hsRes.space&&lId) || !session?.user?.group?.is_spacediredit) {
      response.sendError(404)
      return
    }       
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def saveSpacetypeDetail = {
    checkAccess(6)
    requestService.init(this)
    def hsRes=[:]   
    hsRes.user = session.user
    
    hsRes+=requestService.getParams(['id'],[],['name'])
    
    hsRes.result=[errorcode:[]]
    if(!hsRes.inrequest.name)     
      hsRes.result.errorcode<<1  
      
    if(hsRes.result.errorcode){
      render(contentType:"application/json"){[error:true,errorcode:hsRes.result.errorcode]}
      return
    }  
     
    def oSpacetype
      
    if(!hsRes.result.errorcode){          
      if(hsRes.inrequest.id)        
        oSpacetype=Spacetype.get(hsRes.inrequest.id)
      else  
        oSpacetype=new Spacetype()
            
      if ((!oSpacetype&&hsRes.inrequest.id) || !session?.user?.group?.is_spacediredit) {
        render(contentType:"application/json"){[error:true]}
        return
      }
    }                                      
    
    oSpacetype.csiSetSpacetype(hsRes.inrequest).save(flush:true,failOnError:true)      
    
    flash.spacetypeedit_success=1
    
    render(contentType:"application/json"){[error:false,spacetype_id:oSpacetype.id?:0]}
    return      
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Spacetype <<<//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Holiday >>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def holidayfilter = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = session.user
    hsRes.action_id = 6    
    
    if (session.cataloglastRequest?.fromDetails){
      hsRes.inrequest = session.cataloglastRequest
    }else{
      session.cataloglastRequest=[:]
    }
    session.cataloglastRequest.catobject = 9
    
    hsRes.year=[]
    for(def i=2014;i<=2020;i++)
      hsRes.year<<[id:i]

    return hsRes
  }

  def holidaylist = {
    checkAccess(6)
    requestService.init(this)    
    def hsRes=[searchresult:[:],inrequest:[:]]
    hsRes.user = session.user    
    
    if (session.cataloglastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.cataloglastRequest
      session.cataloglastRequest.fromDetails = 0
    } else {      
      hsRes.inrequest.offset = requestService.getOffset()
      hsRes.inrequest.year = requestService.getIntDef('year',0)
      session.cataloglastRequest = hsRes.inrequest
    }
    session.cataloglastRequest.catobject = 9    
   
    hsRes.searchresult = new Holiday().csiSelectHoliday(hsRes.inrequest?.year?:0)    

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def holidaydetail={
    checkAccess(6)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    hsRes.action_id = 6
    hsRes.user = session.user        

    def lId=requestService.getIntDef('id',0)    
    hsRes.holiday = Holiday.get(lId)
    if ((!hsRes.holiday&&lId) || !session?.user?.group?.is_holidayedit) {
      response.sendError(404)
      return
    }       
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def saveHolidayDetail = {
    checkAccess(6)
    requestService.init(this)
    def hsRes=[:]   
    hsRes.user = session.user
    
    hsRes+=requestService.getParams(['id','status'],[],[],['hdate'])
    
    hsRes.result=[errorcode:[]]
    if(!hsRes.inrequest.hdate)     
      hsRes.result.errorcode<<1  
    else{  
      if(hsRes.inrequest.status && (Tools.getDate(hsRes.inrequest.hdate)[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY || Tools.getDate(hsRes.inrequest.hdate)[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY))
        hsRes.result.errorcode<<2        
      else if(!hsRes.inrequest.status && Tools.getDate(hsRes.inrequest.hdate)[Calendar.DAY_OF_WEEK] >= Calendar.MONDAY && Tools.getDate(hsRes.inrequest.hdate)[Calendar.DAY_OF_WEEK] <= Calendar.FRIDAY)
        hsRes.result.errorcode<<3                 
    }  
      
    if(hsRes.result.errorcode){
      render(contentType:"application/json"){[error:true,errorcode:hsRes.result.errorcode]}
      return
    }  
     
    def oHoliday
      
    if(!hsRes.result.errorcode){          
      if(hsRes.inrequest.id)        
        oHoliday=Holiday.get(hsRes.inrequest.id)
      else  
        oHoliday=new Holiday()
            
      if ((!oHoliday && hsRes.inrequest.id) || !session?.user?.group?.is_holidayedit) {
        render(contentType:"application/json"){[error:true]}
        return
      }
    }                                      
    
    oHoliday.csiSetHoliday(hsRes.inrequest).save(flush:true,failOnError:true)      
    
    flash.holidayedit_success=1
    
    render(contentType:"application/json"){[error:false,holiday_id:oHoliday.id?:0]}
    return      
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def remholiday={
    checkAccess(6)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    hsRes.action_id = 6
    hsRes.user = session.user        

    def lId=requestService.getIntDef('id',0)    
    hsRes.holiday = Holiday.get(lId)
    if (!hsRes.holiday || !session?.user?.group?.is_holidayedit) {
      response.sendError(404)
      return
    }       
    
    hsRes.holiday.delete()
    
    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Holiday<<<//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Agreementtype >>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def agreementtypefilter = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = session.user
    hsRes.action_id = 6    
    
    if (session.cataloglastRequest?.fromDetails){
      hsRes.inrequest = session.cataloglastRequest
    }else{
      session.cataloglastRequest=[:]
    }
    session.cataloglastRequest.catobject = 10    
    
    return hsRes
  }

  def agreementtypelist = {
    checkAccess(6)
    requestService.init(this)    
    def hsRes=[searchresult:[:],inrequest:[:]] 
    hsRes.user = session.user    
    
    session.cataloglastRequest.catobject = 10    
   
    hsRes.searchresult = Agreementtype.list()

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def agreementtypedetail={
    checkAccess(6)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    hsRes.action_id = 6
    hsRes.user = session.user        

    def iId=requestService.getIntDef('id',0)    
    hsRes.agreementtype = Agreementtype.get(iId)
    if ((!hsRes.agreementtype&&iId) || !session.user?.group?.is_agrtypeedit) {
      response.sendError(404)
      return
    }       
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def saveAgreementtypeDetail = {
    checkAccess(6)
    requestService.init(this)
    def hsRes=[:]   
    hsRes.user = session.user
    
    hsRes+=requestService.getParams(['id','sortorder'],[],['name'],[])
    
    hsRes.result=[errorcode:[]]
    
    if(!hsRes.inrequest.id)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.name)     
      hsRes.result.errorcode<<2
      
    if(hsRes.result.errorcode){
      render(contentType:"application/json"){[error:true,errorcode:hsRes.result.errorcode]}
      return
    }  
     
    def oAgreementtype=Agreementtype.get(hsRes.inrequest.id)
                  
    if ((!oAgreementtype && hsRes.inrequest.id) || !session.user?.group?.is_agrtypeedit) {
      render(contentType:"application/json"){[error:true]}
      return
    }                                          
    
    oAgreementtype.csiSetAgreementtype(hsRes.inrequest).save(flush:true,failOnError:true)      
    
    flash.agreementtypeedit_success=1
    
    render(contentType:"application/json"){[error:false]}
    return      
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Agreementtype<<<//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Composition<<<//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def compositionfilter = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = session.user
    hsRes.action_id = 6    
    
    def fromDetails = requestService.getIntDef('fromDetails',0)
    
    if (fromDetails&&session.cataloglastRequest){
      session.cataloglastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.cataloglastRequest
    }
    
    hsRes.iscanadd = recieveSectionPermission(CPOSEDIT)
    session.cataloglastRequest.catobject = 11    
    
    return hsRes
  }

  def compositionlist = {
    checkAccess(6)
    requestService.init(this)    
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = session.user
    hsRes.action_id = 6    
    
    if (session.cataloglastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.cataloglastRequest
      session.cataloglastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['position_id'],null,['name'])
      session.cataloglastRequest = hsRes.inrequest
    }
    
    hsRes.compositions = new Composition().csiFindComposition(hsRes.inrequest.position_id?:0,hsRes.inrequest.name?:'')
    hsRes.is_compers = []
    for(record in hsRes.compositions)
      hsRes.is_compers << Compers.countByComposition_id(record.id)?1:0

    session.cataloglastRequest.catobject = 11   
    hsRes.iscanedit = recieveSectionPermission(CPOSEDIT)
    
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def compositiondetail = {
    checkAccess(6)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 6
    hsRes.user = session.user

    def iId = requestService.getIntDef('id',0)
    hsRes.composition = Composition.get(iId)
    if(iId && !hsRes.composition){
      response.sendError(404)
      return
    }

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def saveCompositionDetail = {
    checkAccess(6)
    if (!checkSectionPermission(CPOSEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes+=requestService.getParams(['position_id'],null,['name'])

    hsRes.composition = Composition.get(lId)
    if (!hsRes.composition&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.name)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.position_id)
      hsRes.result.errorcode<<2

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.composition = new Composition()
        hsRes.composition.csiSetData(hsRes.inrequest).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Catalog/saveCompositionDetail\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }

  def removecomposition = {
    checkAccess(6)
    if (!checkSectionPermission(CPOSEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    try {
      Composition.get(requestService.getIntDef('id',0))?.delete(flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Catalog/removecomposition\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Composition<<<////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Outsource >>>/////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def outsourcefilter = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6
    
    if (session.cataloglastRequest)
      hsRes.inrequest = session.cataloglastRequest
    
    hsRes.iscanadd = recieveSectionPermission(CDEPEDIT)    

    return hsRes
  }
  def outsourcelist = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    if (session.cataloglastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.cataloglastRequest
      session.cataloglastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['modstatus'],null,['name'])
      session.cataloglastRequest = hsRes.inrequest
    }
    session.cataloglastRequest.catobject = 6
    
    hsRes.outsources = new Outsource().csiSelectOutsource(hsRes.inrequest.name?:'',hsRes.inrequest.modstatus?:0)
    
    return hsRes
  }
  def outsource = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    def lId = requestService.getIntDef('id',0)
    hsRes.outsource = Outsource.get(lId)
    if (!hsRes.outsource&&lId) {
      response.sendError(404)
      return
    }
    hsRes.iscanedit = recieveSectionPermission(CDEPEDIT)
    hsRes.companies = Company.findAllByOutsource_id(lId)

    return hsRes
  }
  
  def updateoutsource = {
    checkAccess(6)
    if (!checkSectionPermission(CDEPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.outsource = Outsource.get(lId)
    if (!hsRes.outsource&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['modstatus'],null,['name'])

    if(!hsRes.inrequest.name)
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.outsource = new Outsource()
        hsRes.result.outsource_id = hsRes.outsource.setData(hsRes.inrequest).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Catalog/updateoutsource\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }  
  def outsourcecompanylist = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = [:] 
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6
    
    def lId = requestService.getIntDef('id',0)
    hsRes.outsource = Outsource.get(lId)
    if (!hsRes.outsource&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.companies = Company.findAllByOutsource_id(lId)   
    
    return hsRes   
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Outsource>>>//////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Zalogtype >>>/////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def zalogtypefilter = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    if (session.cataloglastRequest)
      hsRes.inrequest = session.cataloglastRequest

    hsRes.iscanadd = recieveSectionPermission(CKREDEDIT)

    return hsRes
  }

  def zalogtypelist = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    if (session.cataloglastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.cataloglastRequest
      session.cataloglastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(null,null,['name'])
      session.cataloglastRequest = hsRes.inrequest
    }
    session.cataloglastRequest.catobject = 7

    hsRes.zalogtypes = new ZalogtypeSearch().csiSelectZalogtype(hsRes.inrequest.name?:'')

    return hsRes
  }

  def zalogtype = {
    checkAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    def lId = requestService.getIntDef('id',0)
    hsRes.zalogtype = Zalogtype.get(lId)
    if (!hsRes.zalogtype&&lId) {
      response.sendError(404)
      return
    }
    hsRes.iscanedit = recieveSectionPermission(CKREDEDIT)

    return hsRes
  }

  def updatezalogtype = {
    checkAccess(6)
    if (!checkSectionPermission(CKREDEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.zalogtype = Zalogtype.get(lId)
    if (!hsRes.zalogtype&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,['name'])

    if(!hsRes.inrequest.name)
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.zalogtype = new Zalogtype()
        hsRes.result.zalogtype_id = hsRes.zalogtype.setData(hsRes.inrequest).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Catalog/updatezalogtype\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Zalogtype <<</////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Client>>>/////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def clients = {
    checkAccess(13)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = session.user
    hsRes.action_id = 13

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails&&session.cataloglastRequest){
      session.cataloglastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.cataloglastRequest
    }
    hsRes.iscanadd = recieveSectionPermission(CCLEDIT)

    return hsRes
  }

  def clientlist = {
    checkAccess(13)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = session.user

    if (session.cataloglastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.cataloglastRequest
      session.cataloglastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['modstatus','parent'],null,['name'])
      hsRes.inrequest.offset = requestService.getOffset()
      session.cataloglastRequest = hsRes.inrequest
    }

    hsRes.searchresult = new Client().csiSelectClient(hsRes.inrequest.name?:'',hsRes.inrequest.parent?:0,hsRes.inrequest.modstatus?:0,20,hsRes.inrequest.offset)
    hsRes.mainclients = Client.findAllByIs_super(1).inject([:]){map, client -> map[client.id]=client.name;map}

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def client = {
    checkAccess(13)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary()
    hsRes.action_id = 13
    hsRes.user = User.get(session.user.id)

    def iId = requestService.getIntDef('id',0)
    hsRes.client = Client.get(iId)
    if (!hsRes.client&&iId) {
      response.sendError(404)
      return
    }

    hsRes.iscanedit = recieveSectionPermission(CCLEDIT)
    hsRes.isagent = recieveSectionPermission(CAGENT)
    hsRes.dinclientsaldo = hsRes.client?.parent==0?(Payrequest.findAll{ modstatus >= 0 && client_id == (hsRes.client?.id?:0) && subclient_id==0 && agent_id==0 }.sum{ it.computeClientdelta() }?:0):(Payrequest.findAll{ modstatus >= 0 && subclient_id == (hsRes.client?.id?:0) && agent_id==0 }.sum{ it.computeClientdelta() }?:0)
    hsRes.curclientsaldo = hsRes.client?.computeCurSaldo()?:0

    return hsRes
  }

  def updateclient = {
    checkAccess(13)
    if (!checkSectionPermission(CCLEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 13
    hsRes.result=[errorcode:[]]

    def iId = requestService.getIntDef('id',0)
    hsRes.client = Client.get(iId)
    if (!hsRes.client&&iId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['parent','is_clientcomm','is_middleman'],null,['name'],null,['fee','saldo','addsaldo','midsaldo'])

    if(!hsRes.inrequest.name)
      hsRes.result.errorcode<<1
    if(Client.findByNameAndIdNotEqual(hsRes.inrequest.name,hsRes.client?.id?:0))
      hsRes.result.errorcode<<2

    if(!hsRes.result.errorcode){
      try {
        if(!iId) hsRes.client = new Client()
        hsRes.result.client_id = hsRes.client.csiSetClient(hsRes.inrequest).csiSetBaseSaldo(hsRes.inrequest).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Catalog/updateclient\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def setClientStatus = {
    checkAccess(13)
    if (!checkSectionPermission(CCLEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 13

    Client.get(requestService.getIntDef('id',0))?.csiSetModstatus(requestService.getIntDef('modstatus',0))?.save(flush:true,failOnError:true)

    render(contentType:"application/json"){[error:false]}
    return
  }

  def clientpayments = {
    checkAccess(13)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 13

    hsRes.client = Client.get(requestService.getIntDef('id',0))
    if (!hsRes.client) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payments = new Payment().csiSelectPayment([client_id:hsRes.client.id],20,0)
    return hsRes
  }

  def clientkredits = {
    checkAccess(13)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 13

    hsRes.client = Client.get(requestService.getIntDef('id',0))
    if (!hsRes.client) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.kredits = Kredit.findAllByClient_idAndModstatus(hsRes.client.id,1)
    return hsRes
  }

  def clientagentagrs = {
    checkAccess(13)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 13

    hsRes.client = Client.get(requestService.getIntDef('id',0))
    if (!hsRes.client) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.agentagrs = new AgentagrSearch().csiSelectAgentagrs(0,hsRes.client.id,'','',1,-1,0)
    hsRes.banks = hsRes.agentagrs.records.inject([:]){map, agentagr -> map[agentagr.id]=Agentagrbank.findAllByAgentagr_id(agentagr.id).collect{Bank.get(it.bank_id)};map}
    return hsRes
  }

  def subclients = {
    checkAccess(13)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 13

    hsRes.client = Client.get(requestService.getIntDef('id',0))
    if (!hsRes.client) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.subclients = Client.findAllByParent(hsRes.client.id)
    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Client<<</////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Agent>>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def agents = {
    checkAccess(15)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = session.user
    hsRes.action_id = 15

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails&&session.cataloglastRequest){
      session.cataloglastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.cataloglastRequest
    }
    hsRes.iscanadd = recieveSectionPermission(CAGEDIT)

    return hsRes
  }

  def agentlist = {
    checkAccess(15)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 15

    if (session.cataloglastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.cataloglastRequest
      session.cataloglastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['modstatus'],null,['name'])
      hsRes.inrequest.offset = requestService.getOffset()
      session.cataloglastRequest = hsRes.inrequest
    }

    hsRes.searchresult = new Agent().csiSelectAgent(hsRes.inrequest.name?:'',hsRes.inrequest.modstatus?:0,20,hsRes.inrequest.offset)
    hsRes.iscanedit = recieveSectionPermission(CAGEDIT)

    return hsRes
  }

  def setAgentStatus = {
    checkAccess(15)
    if (!checkSectionPermission(CAGEDIT)) return
    requestService.init(this)

    try {
      Agent.get(requestService.getIntDef('id',0))?.csiSetModstatus(requestService.getIntDef('modstatus',0))?.save(flush:true,failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Catalog/setAgentStatus\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def agent = {
    checkAccess(15)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 15

    def lId=requestService.getIntDef('id',0)
    hsRes.agent = Agent.get(lId)
    if (!hsRes.agent&&lId) {
      response.sendError(404)
      return
    }

    hsRes.client = Client.findAllByModstatus(1,[sort:'name',order:'asc'])
    if(hsRes.agent){
      hsRes.accrued = Actagent.findAllByIs_reportAndAgent_id(0,hsRes.agent.id).sum{it.summa}?:0.0g
      hsRes.agreed = Actagent.findAllByIs_reportAndAgent_idAndModstatus(0,hsRes.agent.id,1).sum{it.summa}?:0.0g
      hsRes.cashpaid = Cash.findAllByAgent_id(hsRes.agent.id).sum{ it.type==1?it.summa:-it.summa }?:0.0g
      hsRes.paid = Payrequest.findAllByPaytypeAndAgent_idAndModstatusGreaterThan(1,hsRes.agent.id,1).sum{ it.summa }?:0.0g
      hsRes.agentfix = Agentfix.findAllByAgent_id(hsRes.agent.id).sum{ it.summa }?:0.0g
      hsRes.balance = hsRes.agreed - hsRes.cashpaid - hsRes.paid - hsRes.agentfix
    }
    hsRes.iscanedit = recieveSectionPermission(CAGEDIT)

    return hsRes
  }

  def updateagent = {
    checkAccess(15)
    if (!checkSectionPermission(CAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 15
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes+=requestService.getParams(['client_id'],null,['aname'])

    hsRes.agent = Agent.get(lId)
    if (!hsRes.agent&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.aname)
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.agent = new Agent()
        hsRes.result.agent = hsRes.agent.csiSetAgent(hsRes.inrequest).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Catalog/updateagent\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def agentagreements = {
    checkAccess(15)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 15

    hsRes.agent = Agent.get(requestService.getIntDef('id',0))
    if (!hsRes.agent) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.agreements = new AgentplanSearch().csiSelectAgentagrs(hsRes.agent.id)
    hsRes.agrs = Agentagr.list().inject([:]){map, agr -> map[agr.id]=agr.name;map}
    hsRes.clients = Client.list().inject([:]){map, client -> map[client.id]=client.name;map}

    return hsRes
  }

  def agentpayments = {
    checkAccess(15)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 15

    hsRes.agent = Agent.get(requestService.getIntDef('id',0))
    if (!hsRes.agent) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payments = Payrequest.findAllByPaytypeAndAgent_idAndModstatusGreaterThan(1,hsRes.agent.id,-1,[sort:'paydate',order:'desc'])
    hsRes.agrs = Agentagr.list().inject([:]){map, agr -> map[agr.id]=agr.name;map}

    return hsRes
  }

  def agentcashpayments = {
    checkAccess(15)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 15

    hsRes.agent = Agent.get(requestService.getIntDef('id',0))
    if (!hsRes.agent) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.cashpayments = Cash.findAllByAgent_id(hsRes.agent.id,[sort:'operationdate',order:'desc'])
    hsRes.agrs = Agentagr.list().inject([:]){map, agr -> map[agr.id]=agr.name;map}

    return hsRes
  }

  def agentfixes = {
    checkAccess(15)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 15

    hsRes.agent = Agent.get(requestService.getIntDef('id',0))
    if (!hsRes.agent) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.fixes = Agentfix.findAllByAgent_id(hsRes.agent.id)
    hsRes.agrs = Agentagr.list().inject([:]){map, agr -> map[agr.id]=agr.name;map}

    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Agent<<<//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Enqtype>>>////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def enqfilter = {
    checkAccess(6)
    if (!checkSectionPermission(CENQEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = session.user
    hsRes.action_id = 6

    if (session.cataloglastRequest?.fromDetails){
      hsRes.inrequest = session.cataloglastRequest
    }

    return hsRes
  }

  def enqtypes = {
    checkAccess(6)
    if (!checkSectionPermission(CENQEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    if (session.cataloglastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.cataloglastRequest
      session.cataloglastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['type'])
      hsRes.inrequest.offset = requestService.getOffset()
      session.cataloglastRequest = hsRes.inrequest
    }
    session.cataloglastRequest.catobject = 13

    hsRes.searchresult = new EnquirySearch().csiSelectTypes(hsRes.inrequest.type?:0,20,hsRes.inrequest.offset)

    return hsRes
  }

  def enqtype = {
    checkAccess(6)
    if (!checkSectionPermission(CENQEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    def lId=requestService.getIntDef('id',0)
    hsRes.enqtype = Enqtype.get(lId)
    if (!hsRes.enqtype&&lId) {
      response.sendError(404)
      return
    }

    return hsRes
  }

  def updateenqtype = {
    checkAccess(6)
    if (!checkSectionPermission(CENQEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6
    hsRes.result=[errorcode:[]]

    def lId = requestService.getLongDef('id',0)
    hsRes+=requestService.getParams(['type','term','longterm'],null,['tname'])

    hsRes.enqtype = Enqtype.get(lId)
    if (!hsRes.enqtype&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.tname)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.type)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.term)
      hsRes.result.errorcode<<3
    else if (hsRes.inrequest.term<0)
      hsRes.result.errorcode<<4
    if(hsRes.inrequest.type==2&&!hsRes.inrequest.longterm)
      hsRes.result.errorcode<<5
    else if (hsRes.inrequest.type==2&&hsRes.inrequest.longterm<0)
      hsRes.result.errorcode<<6

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.enqtype = new Enqtype()
        hsRes.result.enqtype = hsRes.enqtype.setData(hsRes.inrequest).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Catalog/updateenqtype\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Enqtype<<<////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Cgroup >>>////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def cgroupfilter = {
    checkAccess(6)
    if (!checkSectionPermission(CCGROUP)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    if (session.cataloglastRequest)
      hsRes.inrequest = session.cataloglastRequest

    hsRes.iscanadd = recieveSectionPermission(CKREDEDIT)

    return hsRes
  }

  def cgrouplist = {
    checkAccess(6)
    if (!checkSectionPermission(CCGROUP)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    if (session.cataloglastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.cataloglastRequest
      session.cataloglastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(null,null,['name'])
      session.cataloglastRequest = hsRes.inrequest
    }
    session.cataloglastRequest.catobject = 12

    hsRes.cgroupes = new CgroupSearch().csiSelectCgroup(hsRes.inrequest.name?:'')

    return hsRes
  }

  def cgroup = {
    checkAccess(6)
    if (!checkSectionPermission(CCGROUP)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    def lId = requestService.getIntDef('id',0)
    hsRes.cgroup = Cgroup.get(lId)
    if (!hsRes.cgroup&&lId) {
      response.sendError(404)
      return
    }

    return hsRes
  }

  def updatecgroup = {
    checkAccess(6)
    if (!checkSectionPermission(CCGROUP)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.cgroup = Cgroup.get(lId)
    if (!hsRes.cgroup&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,['name'])

    if(!hsRes.inrequest.name)
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.cgroup = new Cgroup()
        hsRes.result.cgroup_id = hsRes.cgroup.setData(hsRes.inrequest).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Catalog/updatecgroup\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Cgroup <<<////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Tasktype>>>///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def tasktypefilter = {
    checkAccess(6)
    if (!checkSectionPermission(CTTYPE)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = session.user
    hsRes.action_id = 6

    if (session.cataloglastRequest?.fromDetails){
      hsRes.inrequest = session.cataloglastRequest
    }

    return hsRes
  }

  def tasktypes = {
    checkAccess(6)
    if (!checkSectionPermission(CTTYPE)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    if (session.cataloglastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.cataloglastRequest
      session.cataloglastRequest.fromDetails = 0
    } else {
      session.cataloglastRequest = [:]
    }
    session.cataloglastRequest.catobject = 14

    hsRes.searchresult = Tasktype.list(sort:'name',order:'asc')

    return hsRes
  }

  def tasktype = {
    checkAccess(6)
    if (!checkSectionPermission(CTTYPE)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    def lId=requestService.getIntDef('id',0)
    hsRes.tasktype = Tasktype.get(lId)
    if (!hsRes.tasktype&&lId) {
      response.sendError(404)
      return
    }

    return hsRes
  }

  def updatetasktype = {
    checkAccess(6)
    if (!checkSectionPermission(CTTYPE)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6
    hsRes.result=[errorcode:[]]

    def lId = requestService.getLongDef('id',0)
    hsRes+=requestService.getParams(null,null,['tname'])

    hsRes.tasktype = Tasktype.get(lId)
    if (!hsRes.tasktype&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.tname)
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.tasktype = new Tasktype()
        hsRes.result.tasktype = hsRes.tasktype.setData(hsRes.inrequest).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Catalog/updatetasktype\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Tasktype<<<///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Car>>>////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def carfilter = {
    checkAccess(6)
    if (!checkSectionPermission(CCAR)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = session.user
    hsRes.action_id = 6

    if (session.cataloglastRequest?.fromDetails){
      hsRes.inrequest = session.cataloglastRequest
    }

    return hsRes
  }

  def cars = {
    checkAccess(6)
    if (!checkSectionPermission(CCAR)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    if (session.cataloglastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.cataloglastRequest
      session.cataloglastRequest.fromDetails = 0
    } else {
      session.cataloglastRequest = [:]
    }
    session.cataloglastRequest.catobject = 15

    hsRes.searchresult = Car.list(sort:'name',order:'asc')

    return hsRes
  }

  def car = {
    checkAccess(6)
    if (!checkSectionPermission(CCAR)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    def lId=requestService.getIntDef('id',0)
    hsRes.car = Car.get(lId)
    if (!hsRes.car&&lId) {
      response.sendError(404)
      return
    }

    return hsRes
  }

  def updatecar = {
    checkAccess(6)
    if (!checkSectionPermission(CCAR)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6
    hsRes.result=[errorcode:[]]

    def lId = requestService.getLongDef('id',0)
    hsRes.car = Car.get(lId)
    if (!hsRes.car&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['modstatus'],null,['cname'])

    if(!hsRes.inrequest.cname)
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.car = new Car()
        hsRes.result.car = hsRes.car.csiSetData(hsRes.inrequest).csiSetModstatus(hsRes.inrequest.modstatus).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Catalog/updatecar\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Car<<<////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Visgroup >>>//////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def visgroupfilter = {
    checkAccess(6)
    if (!checkSectionPermission(CVISGREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    if (session.cataloglastRequest)
      hsRes.inrequest = session.cataloglastRequest

    return hsRes
  }

  def visgrouplist = {
    checkAccess(6)
    if (!checkSectionPermission(CVISGREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    if (session.cataloglastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.cataloglastRequest
      session.cataloglastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(null,null,['name'])
      session.cataloglastRequest = hsRes.inrequest
    }
    session.cataloglastRequest.catobject = 16

    hsRes.visgroupes = new VisgroupSearch().csiSelectCgroup(hsRes.inrequest.name?:'')

    return hsRes
  }

  def visualgroup = {
    checkAccess(6)
    if (!checkSectionPermission(CVISGREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    def lId = requestService.getIntDef('id',0)
    hsRes.visgroup = Visualgroup.get(lId)
    if (!hsRes.visgroup&&lId) {
      response.sendError(404)
      return
    }

    return hsRes
  }

  def updatevisualgroup = {
    checkAccess(6)
    if (!checkSectionPermission(CVISGREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.visgroup = Visualgroup.get(lId)
    if (!hsRes.visgroup&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,['name'])

    if(!hsRes.inrequest.name)
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.visgroup = new Visualgroup()
        hsRes.result.visgroup_id = hsRes.visgroup.setData(hsRes.inrequest).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Catalog/updatevisualgroup\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def viscompanies = {
    checkAccess(6)
    if (!checkSectionPermission(CVISGREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 6

    hsRes.visgroup = Visualgroup.get(requestService.getIntDef('id',0))
    if (!hsRes.visgroup) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.companies = Company.findAllByIs_holdingAndVisualgroup_id(1,hsRes.visgroup.id)

    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Visgroup <<<//////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Config>>>/////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def configs = {
    checkAccess(16)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = session.user
    hsRes.action_id = 16

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails&&session.cataloglastRequest){
      session.cataloglastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.cataloglastRequest
    }
    hsRes.configs = Dynconfig.findAllByIs_secret(0)
    hsRes.expensetypes = new ExpensetypeSearch().csiGetFullList()

    return hsRes
  }

  def updateconfig = {
    checkAccess(16)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 16

    try {
      Dynconfig.findAllByIs_secret(0).each{ conf -> conf.updateValue(requestService.getStr(conf.name)).save(failOnError:true) }
    } catch(Exception e) {
      log.debug("Error save data in Catalog/updateconfig\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Config<<</////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
}
