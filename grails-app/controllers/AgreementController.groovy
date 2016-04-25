import grails.converters.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import pl.touk.excel.export.WebXlsxExporter

class AgreementController {
  def requestService
  def parseService
  def imageService
  def agentKreditService
  def paymentService

  final String AKREDEDIT = 'is_kreditedit'
  final String AKREDPAY = 'is_kreditpaymentedit'
  final String AKRCLIENT = 'is_kreditclient'
  final String AKRREAL = 'is_realkredit'
  final String ASPEDIT = 'is_arendaedit'
  final String ALIZEDIT = 'is_lizingedit'
  final String ALIZPAY = 'is_lizingpaymentedit'
  final String AAGEDIT = 'is_agentedit'
  final String ATREDIT = 'is_tradeedit'
  final String ACESEDIT = 'is_cessionedit'
  final String ACESPAY = 'is_cessionpaymentedit'
  final String ALICEDIT = 'is_licenseedit'
  final String ASEREDIT = 'is_serviceedit'
  final String ASMREDIT = 'is_smredit'
  final String ALOANEDIT = 'is_loanedit'
  final String APTAG = 'is_paytag'
  final String ADEPEDIT = 'is_depositedit'
  final String AFLEDIT = 'is_finlizingedit'

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

  Boolean checkSectionAccess(iId) {
    if(!session.user.group?."${Agreementtype.get(iId)?.checkfield}"){
      response.sendError(403)
      return false;
    }
    return true
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
    checkAccess(7)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails&&session.agrlastRequest){
      session.agrlastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.agrlastRequest
    } else {
      hsRes.inrequest=[:]
      hsRes.inrequest.agrobject = requestService.getIntDef('agrobject',0)
    }

    hsRes.agrtypes = Agreementtype.list()

    return hsRes
  }

  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////License >>>///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def licensefilter = {
    checkAccess(7)
    checkSectionAccess(1)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails){
      hsRes.inrequest = session.agrlastRequest
    }

    hsRes.industries = Industry.findAllByIs_license(1)
    hsRes.iscanedit = recieveSectionPermission(ALICEDIT)

    return hsRes
  }

  def licenses = {
    checkAccess(7)
    checkSectionAccess(1)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.agrlastRequest
      session.agrlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['company_id','industry_id'])
      hsRes.inrequest.offset = requestService.getOffset()
      session.agrlastRequest = hsRes.inrequest
    }
    session.agrlastRequest.agrobject = 1

    hsRes.searchresult = new LicenseSearch().csiSelectLicenses(hsRes.inrequest.company_id?:0,hsRes.inrequest.industry_id?:0,
                                                               session.user.group.visualgroup_id,20,hsRes.inrequest.offset)
    hsRes.industries = Industry.list().inject([:]){map, industry -> map[industry.id]=industry.name;map}

    return hsRes
  }

  def license = {
    checkAccess(7)
    checkSectionAccess(1)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.license = License.get(lId)
    if (!hsRes.license&&lId) {
      response.sendError(404)
      return
    }
    hsRes+=requestService.getParams(['company_id'])

    hsRes.companies = Company.findAllByIs_holding(1)
    hsRes.industries = Industry.findAllByIs_license(1)
    hsRes.sro = Company.get(hsRes.license?.sro_id)
    hsRes.iscanaddcompanies = session.user.group?.is_companyinsert?true:false
    hsRes.iscanedit = recieveSectionPermission(ALICEDIT)
    hsRes.isCanDelete = !Payment.findAllByAgreementtype_idAndAgreement_id(1,hsRes.license?.id?:0)&&hsRes.iscanedit

    return hsRes
  }

  def updatelicense = {
    checkAccess(7)
    if (!checkSectionAccess(1)) return
    if (!checkSectionPermission(ALICEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    def lId = requestService.getLongDef('id',0)
    hsRes+=requestService.getParams(['company_id','industry_id','paytype','entryfee','regfee','alimit','modstatus','regfeeterm','strakhfee'],null,['anumber','sro'])
    hsRes.inrequest.adate = requestService.getDate('adate')
    hsRes.inrequest.enddate = requestService.getDate('enddate')

    hsRes.license = License.get(lId)
    if (!hsRes.license&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.license){
      if(!hsRes.inrequest.company_id)
        hsRes.result.errorcode<<1
      if(!hsRes.inrequest.sro)
        hsRes.result.errorcode<<2
      else if(!Company.findByNameOrInn(hsRes.inrequest.sro,hsRes.inrequest.sro))
        hsRes.result.errorcode<<8
      if(Company.findAllByNameOrInn(hsRes.inrequest.sro,hsRes.inrequest.sro).size()>1)
        hsRes.result.errorcode<<9
    }

    if(!hsRes.inrequest.industry_id)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.anumber)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.adate)
      hsRes.result.errorcode<<5
    if(hsRes.inrequest.enddate&&hsRes.inrequest.enddate<hsRes.inrequest.adate)
      hsRes.result.errorcode<<7

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.license = new License(company_id:hsRes.inrequest.company_id,sro_id:Company.findByNameOrInn(hsRes.inrequest.sro,hsRes.inrequest.sro)?.id)
        hsRes.result.license = hsRes.license.setData(hsRes.inrequest).csiSetAdmin(session.user.id).csiSetModstatus(hsRes.inrequest.modstatus).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Agreement/updatelicense\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def addlicensescan = {
    checkAccess(7)
    if(!checkSectionAccess(1)) return
    if(!checkSectionPermission(ALICEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    hsRes.license = License.get(requestService.getLongDef('id',0))
    if (!hsRes.license) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    imageService.init(this)
    def hsData = imageService.rawUpload('file',true)
    if(hsData.error in [1,3])
        hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        hsRes.license.csiSetFileId(imageService.rawUpload('file').fileid).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Salary/addlicensescan\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    return hsRes.result
  }

  def licplanpayments = {
    checkAccess(7)
    checkSectionAccess(1)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.license = License.get(lId)
    if (!hsRes.license) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.planpayments = Licplanpayment.findAllByLicense_idAndModstatusGreaterThanEquals(hsRes.license.id,0)
    hsRes.iscanedit = recieveSectionPermission(ALICEDIT)

    return hsRes
  }

  def licplanpayment = {
    checkAccess(7)
    checkSectionAccess(1)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('license_id',0)
    hsRes.license = License.get(lId)
    if (!hsRes.license) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.iscanedit = recieveSectionPermission(ALICEDIT)

    return hsRes
  }

  def addlicplanpayment = {
    checkAccess(7)
    if (!checkSectionAccess(1)) return
    if (!checkSectionPermission(ALICEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getLongDef('license_id',0)
    hsRes+=requestService.getParams(['summa'])
    hsRes.inrequest.planpayment_paydate = requestService.getDate('planpayment_paydate')

    hsRes.license = License.get(lId)
    if (!hsRes.license) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.planpayment_paydate)
      hsRes.result.errorcode<<2

    if(!hsRes.result.errorcode){
      try {
        new Licplanpayment(license_id:hsRes.license.id).setData(hsRes.inrequest).csiSetModstatus(0).csiSetAdmin(session.user.id).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addlicplanpayment\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletelicplanpayment = {
    checkAccess(7)
    if (!checkSectionAccess(1)) return
    if (!checkSectionPermission(ALICEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId = requestService.getLongDef('license_id',0)
    hsRes+=requestService.getParams(null,null,['id'])

    hsRes.license = License.get(lId)
    if (!hsRes.license||!hsRes.inrequest.id) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Licplanpayment.get(hsRes.inrequest.id).csiSetModstatus(-1).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletelicplanpayment\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def licpayrequests = {
    checkAccess(7)
    checkSectionAccess(1)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.license = License.get(requestService.getIntDef('id',0))
    if (!hsRes.license) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payrequests = Payrequest.findAllByAgreementtype_idAndAgreement_idAndModstatusGreaterThan(1,hsRes.license.id,-1,[sort:'paydate',order:'desc'])
    hsRes.iscanedit = recieveSectionPermission(ALICEDIT)

    return hsRes
  }

  def deletelicpayrequest = {
    checkAccess(7)
    if (!checkSectionAccess(1)) return
    if (!checkSectionPermission(ALICEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agr = License.get(requestService.getLongDef('agr_id',0))
    hsRes.payrequest = Payrequest.findByAgreementtype_idAndAgreement_idAndId(1,hsRes.agr?.id?:-1,requestService.getLongDef('id',0))
    if (!hsRes.agr||hsRes.payrequest?.modstatus!=0) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletelicpayrequest\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def licpayments = {
    checkAccess(7)
    checkSectionAccess(1)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.license = License.get(lId)
    if (!hsRes.license) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payments = Payment.findAllByAgreementtype_idAndAgreement_id(1,hsRes.license.id,[sort:'paydate',order:'desc'])

    return hsRes
  }

  def licensehistory = {
    checkAccess(7)
    checkSectionAccess(1)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.license = License.get(lId)
    if (!hsRes.license) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.history = new LicensehistSearch().csiFindHistory(hsRes.license.id)

    return hsRes
  }

  def licensepayrequests = {
    checkAccess(7)
    checkSectionAccess(1)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.iscanedit = recieveSectionPermission(ALICEDIT)

    return hsRes
  }

  def licensepayrequestslist = {
    checkAccess(7)
    checkSectionAccess(1)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes+=requestService.getParams(['modstatus'],null,['client_name'])
    hsRes.inrequest.startdate = requestService.getDate('startdate')
    hsRes.inrequest.enddate = requestService.getDate('enddate')

    hsRes.searchresult = new LicplanpaymentSearch().csiSelectLicensePayments(hsRes.inrequest.client_name?:'',hsRes.inrequest.modstatus?:0,
                                            hsRes.inrequest.startdate,hsRes.inrequest.enddate,20,requestService.getOffset())

    return hsRes
  }

  def createlcpayrequests={
    checkAccess(7)
    checkSectionAccess(1)
    if (!checkSectionPermission(ALICEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lplanpaymentIds = requestService.getIds('lplanpayment')

    if (!lplanpaymentIds) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    try {
      lplanpaymentIds?.each{ Licplanpayment.get(it)?.generatePayrequest().save(failOnError:true) }
    } catch(Exception e) {
      log.debug("Error save data in Agreement/createlcpayrequests\n"+e.toString());
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////License <<<///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Arenda >>>////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def arendafilter = {
    checkAccess(7)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails){
      hsRes.inrequest = session.agrlastRequest
    }

    hsRes.arendatypes = Arendatype.list()
    hsRes.spacetypes = Spacetype.list()
    hsRes.iscanedit = recieveSectionPermission(ASPEDIT)

    return hsRes
  }

  def spaces = {
    checkAccess(7)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.agrlastRequest
      session.agrlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['sid','arendatype_id','spacetype_id','project_id','debt','is_adrsame','is_nds'],
                                      ['responsible'],['address','company_name','anumber'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.enddate = requestService.getDate('enddate')
      hsRes.inrequest.offset = requestService.getOffset()
      session.agrlastRequest = hsRes.inrequest
    }
    session.agrlastRequest.agrobject = 2

    hsRes.searchresult = new SpaceSearch().csiSelectSpaces(hsRes.inrequest.sid?:0,hsRes.inrequest.address?:'',
                                                           hsRes.inrequest.company_name?:'',hsRes.inrequest.arendatype_id?:0,
                                                           hsRes.inrequest.spacetype_id?:0,hsRes.inrequest.project_id?:0,
                                                           hsRes.inrequest.responsible?:0l,hsRes.inrequest.debt?:0,
                                                           hsRes.inrequest.is_nds?:0,hsRes.inrequest.is_adrsame?:0,
                                                           hsRes.inrequest.modstatus?:0,hsRes.inrequest.enddate,
                                                           hsRes.inrequest.anumber?:'',session.user.group.visualgroup_id,20,hsRes.inrequest.offset)
    hsRes.debts = hsRes.searchresult.records.inject([:]){map, space -> map[space.id]=agentKreditService.computeSpaceDebt(space);map}
    hsRes.spacetypes = Spacetype.list().inject([:]){map, spacetype -> map[spacetype.id]=spacetype.name;map}
    hsRes.arendatypes = Arendatype.list().inject([:]){map, arendatype -> map[arendatype.id]=arendatype.name;map}

    return hsRes
  }

  def space = {
    checkAccess(7)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.space = Space.get(lId)
    if (!hsRes.space&&lId) {
      response.sendError(404)
      return
    }
    hsRes+=requestService.getParams(['company_id','asort'])

    hsRes.spacetypes = Spacetype.list()
    hsRes.arendatypes = Arendatype.list()
    hsRes.arendator = hsRes.inrequest.asort&&hsRes.inrequest.company_id?Company.get(hsRes.inrequest.company_id):Company.get(hsRes.space?.arendator)
    hsRes.arendodatel = !hsRes.inrequest.asort&&hsRes.inrequest.company_id?Company.get(hsRes.inrequest.company_id):Company.get(hsRes.space?.arendodatel)
    hsRes.projects = Project.list()
    hsRes.users = new UserpersSearch().csiFindByAccessrigth(ASPEDIT)
    hsRes.banks = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(hsRes.arendodatel?.id,1,1).collect{Bank.get(it.bank_id)}
    hsRes.agrs = Space.findAllByArendatorAndModstatusAndIs_nosubrenting(hsRes.arendodatel?.id?:0,1,0)
    hsRes.debt = agentKreditService.computeSpaceDebt(hsRes.space)
    hsRes.iscanaddcompanies = session.user.group?.is_companyinsert?true:false
    hsRes.iscanedit = recieveSectionPermission(ASPEDIT)
    hsRes.isCanDelete = !Payment.findAllByAgreementtype_idAndAgreement_id(2,hsRes.space?.id?:0)&&hsRes.iscanedit
    hsRes.iscantag = recieveSectionPermission(APTAG)

    return hsRes
  }

  def spacebanklist={
    checkAccess(7)
    requestService.init(this)

    return [banks:Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(Company.findByNameOrInn(requestService.getStr('arendodatel'),requestService.getStr('arendodatel'))?.id,1,1).collect{Bank.get(it.bank_id)}]
  }

  def spacemainagrlist={
    checkAccess(7)
    checkSectionAccess(2)
    requestService.init(this)

    return [agrs:Space.findAllByArendatorAndModstatusAndIs_nosubrenting(Company.findByNameOrInn(requestService.getStr('arendodatel'),requestService.getStr('arendodatel'))?.id?:0,1,0)]
  }

  def updatespace = {
    checkAccess(7)
    if (!checkSectionAccess(2)) return
    if (!checkSectionPermission(ASPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    def lId = requestService.getLongDef('id',0)

    hsRes.space = Space.get(lId)
    if (!hsRes.space&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['asort','spacetype_id','is_addpayment','payterm','mainagr_id','is_nosubrenting',
                                     'project_id','is_nopayment','paytermcondition','modstatus','prolongcondition','is_subwritten',
                                     'prolongterm','is_territory','paycondition','contcol','is_adrsame','monthnotification'],
                                    ['responsible'],['arendator','arendodatel','anumber','fulladdress','bank_id','description',
                                     'comment','shortaddress'],null,['area','ratemeter','terarea','rate','ratedop','actrate'])
    hsRes.inrequest.is_noexpense = recieveSectionPermission(APTAG)?requestService.getIntDef('is_noexpense',0):hsRes.space?.is_noexpense?:0
    hsRes.inrequest.adate = requestService.getDate('adate')
    hsRes.inrequest.enddate = requestService.getDate('enddate')

    if(!hsRes.space){
      if(!hsRes.inrequest.arendator)
        hsRes.result.errorcode<<1
      else if(!Company.findByNameOrInn(hsRes.inrequest.arendator,hsRes.inrequest.arendator))
        hsRes.result.errorcode<<2
      else if(Company.findAllByNameOrInn(hsRes.inrequest.arendator,hsRes.inrequest.arendator).size()>1)
        hsRes.result.errorcode<<3
      if(!hsRes.inrequest.arendodatel)
        hsRes.result.errorcode<<4
      else if(!Company.findByNameOrInn(hsRes.inrequest.arendodatel,hsRes.inrequest.arendodatel))
        hsRes.result.errorcode<<5
      else if(Company.findAllByNameOrInn(hsRes.inrequest.arendodatel,hsRes.inrequest.arendodatel).size()>1)
        hsRes.result.errorcode<<6
    }
    if(!hsRes.inrequest.asort&&!hsRes.inrequest.mainagr_id)
      hsRes.result.errorcode<<7
    if(!hsRes.inrequest.spacetype_id)
      hsRes.result.errorcode<<10
    if(!hsRes.inrequest.fulladdress)
      hsRes.result.errorcode<<12
    if(!hsRes.inrequest.anumber)
      hsRes.result.errorcode<<13
    if(!hsRes.inrequest.adate)
      hsRes.result.errorcode<<14
    if(!hsRes.inrequest.enddate)
      hsRes.result.errorcode<<15
    if(hsRes.inrequest.enddate<hsRes.inrequest.adate)
      hsRes.result.errorcode<<16
    if(!hsRes.inrequest.payterm)
      hsRes.result.errorcode<<17
    if(hsRes.inrequest.spacetype_id==5&&hsRes.inrequest.contcol<=0)
      hsRes.result.errorcode<<8
    if(hsRes.inrequest.prolongcondition==2&&hsRes.inrequest.prolongterm<=0)
      hsRes.result.errorcode<<9
    if(hsRes.inrequest.prolongcondition==3&&hsRes.inrequest.monthnotification<=0)
      hsRes.result.errorcode<<18
    if(hsRes.space&&hsRes.space.enddate!=hsRes.inrequest.enddate&&!hsRes.inrequest.comment)
      hsRes.result.errorcode<<11

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.space = new Space(arendator:Company.findByNameOrInn(hsRes.inrequest.arendator,hsRes.inrequest.arendator)?.id,arendodatel:Company.findByNameOrInn(hsRes.inrequest.arendodatel,hsRes.inrequest.arendodatel)?.id)
        hsRes.result.space = hsRes.space.setData(hsRes.inrequest).updateModstatus(hsRes.inrequest.modstatus).computePaystatus().csiSetAdmin(session.user.id).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Agreement/updatespace\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def sppayments = {
    checkAccess(7)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.space = Space.get(lId)
    if (!hsRes.space) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payments = Payment.findAll(sort:'paydate',order:'desc'){ agreementtype_id == 2 && agreement_id == hsRes.space.id && (paytype != 2 || is_internal != 1) }

    return hsRes
  }

  def sppayrequests = {
    checkAccess(7)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.space = Space.get(lId)
    if (!hsRes.space) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.iscanedit = recieveSectionPermission(ASPEDIT)
    hsRes.payrequests = Payrequest.findAllByAgreementtype_idAndAgreement_idAndModstatusGreaterThan(2,hsRes.space.id,-1,[sort:'paydate',order:'desc'])

    return hsRes
  }

  def sppayrequest = {
    checkAccess(7)
    checkSectionAccess(2)
    if (!checkSectionPermission(ASPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agr = Space.get(requestService.getIntDef('agr_id',0))
    if (!hsRes.agr) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    hsRes.ndsdef = Company.get(hsRes.agr.arendodatel)?.taxoption_id==1?true:false
    hsRes.bank = new BankaccountSearch().csiFindAccounts(hsRes.agr.arendator,1,1)
    hsRes.project = Project.findAllByModstatus(1,[sort:'name'])
    hsRes.defproject_id = Project.findByIs_main(1)?.id?:0
    hsRes.expensetype = new ExpensetypeSearch().csiGetList()
    hsRes.iscantag = recieveSectionPermission(APTAG)

    return hsRes
  }

  def addsppayrequest = {
    checkAccess(7)
    if (!checkSectionAccess(2)) return
    if (!checkSectionPermission(ASPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['id','is_nds','is_dop','frombank','expensetype_id','is_task'],null,['destination'],null,['summa'])
    hsRes.inrequest.payrequest_paydate = requestService.getDate('payrequest_paydate')

    hsRes.agr = Space.get(requestService.getIntDef('agr_id',0))
    hsRes.payrequest = Payrequest.get(hsRes.inrequest.id)
    if (!hsRes.agr||(!hsRes.payrequest&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(hsRes.inrequest.summa<=0)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.payrequest_paydate)
      hsRes.result.errorcode<<3
    if(recieveSectionPermission(APTAG)&&!hsRes.inrequest.expensetype_id)
      hsRes.result.errorcode<<4

    if(!hsRes.result.errorcode){
      try {
        if (!hsRes.inrequest.id) hsRes.payrequest = new Payrequest(paytype:1,paycat:1,agreementtype_id:2,agreement_id:hsRes.agr.id).csiSetSpaceAgrData(hsRes.agr).csiSetInitiator(hsRes.user.id)
        hsRes.payrequest.setGeneralData(hsRes.inrequest).csiSetPayrequestTag(hsRes.inrequest,recieveSectionPermission(APTAG),hsRes.user.id).csiSetBankaccount_id(hsRes.inrequest.frombank).save(failOnError:true)
        if (hsRes.inrequest.is_task&&hsRes.payrequest.taskpay_id==0){
          def taskpay_id = new Taskpay(paygroup:hsRes.payrequest.paygroup,is_manual:1).csiSetTaskpay(term:String.format('%td.%<tm.%<tY',hsRes.payrequest.paydate),company_id:hsRes.payrequest.fromcompany_id,summa:hsRes.payrequest.summa).csiSetInitiator(hsRes.user.id).csiSetBankaccountId(hsRes.payrequest.bankaccount_id).save(flush:true,failOnError:true)?.id?:0
          hsRes.payrequest.csiSetTaskpay_id(taskpay_id).csiSetModstatus(1).save(flush:true,failOnError:true)
        }
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addsppayrequest\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def spacedopagrs = {
    checkAccess(7)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.space = Space.get(requestService.getIntDef('id',0))
    if (!hsRes.space) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.spacedopagrs = Spacedopagr.findAllBySpace_id(hsRes.space.id,[sort:'id',order:'desc'])
    hsRes.iscanedit = recieveSectionPermission(ASPEDIT)

    return hsRes
  }

  def spacedopagr = {
    checkAccess(7)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.space = Space.get(requestService.getIntDef('space_id',0))
    if (!hsRes.space) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,['id'])

    hsRes.spacedopagr = Spacedopagr.get(hsRes.inrequest.id)
    hsRes.iscanedit = recieveSectionPermission(ASPEDIT)

    return hsRes
  }

  def addspacedopagr = {
    checkAccess(7)
    if (!checkSectionAccess(2)) return
    if (!checkSectionPermission(ASPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]
 
    hsRes+=requestService.getParams(['id','is_changeprice','is_addpayment','payterm'],null,['anumber'],null,['ratemeter','rate','ratedop'])
    hsRes.inrequest.adate = requestService.getDate('spacedopagr_adate')
    hsRes.inrequest.startdate = requestService.getDate('spacedopagr_startdate')
    hsRes.inrequest.enddate = requestService.getDate('spacedopagr_enddate')

    hsRes.space = Space.get(requestService.getLongDef('space_id',0))
    hsRes.spacedopagr = Spacedopagr.get(hsRes.inrequest.id)
    if (!hsRes.space||(!hsRes.spacedopagr&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.anumber)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.adate)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.startdate)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.enddate)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.rate)
      hsRes.result.errorcode<<5
    if(hsRes.inrequest.is_addpayment&&!hsRes.inrequest.ratedop)
      hsRes.result.errorcode<<6
    if(!hsRes.inrequest.payterm)
      hsRes.result.errorcode<<7

    if(!hsRes.result.errorcode){
      try {
        if (!hsRes.inrequest.id) hsRes.spacedopagr = new Spacedopagr(space_id:hsRes.space.id)
        hsRes.spacedopagr.setData(hsRes.inrequest).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addspacedopagr\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletespacedopagr = {
    checkAccess(7)
    if (!checkSectionAccess(2)) return
    if (!checkSectionPermission(ASPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.space = Space.get(requestService.getLongDef('space_id',0))
    if (!hsRes.space) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Spacedopagr.findBySpace_idAndId(hsRes.space.id,requestService.getIntDef('id',0))?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletespacedopagr\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def spacebankchecks = {
    checkAccess(7)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.space = Space.get(requestService.getIntDef('id',0))
    if (!hsRes.space) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.spacebankchecks = Bankcheck.findAllByAgr_idAndAgrtype_id(hsRes.space.id,2,[sort:'checkdate',order:'desc'])
    hsRes.bankchecktypes = Bankchecktype.list().inject([:]){map, checktype -> map[checktype.id]=checktype.shortname;map}
    hsRes.banks = hsRes.spacebankchecks.collect{it.bank_id}.unique().inject([:]){map, bank_id -> map[bank_id]=Bank.get(bank_id);map}
    hsRes.iscanedit = recieveSectionPermission(ASPEDIT)

    return hsRes
  }

  def spacebankcheck = {
    checkAccess(7)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.space = Space.get(requestService.getIntDef('space_id',0))
    if (!hsRes.space) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,['id'])

    hsRes.spacebankcheck = Bankcheck.get(hsRes.inrequest.id)
    hsRes.bank = Bank.get(hsRes.spacebankcheck?.bank_id?:'')
    hsRes.bankchecktypes = Bankchecktype.list()

    hsRes.iscanedit = recieveSectionPermission(ASPEDIT)

    return hsRes
  }

  def addspacebankcheck = {
    checkAccess(7)
    if (!checkSectionAccess(2)) return
    if (!checkSectionPermission(ASPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]
 
    hsRes+=requestService.getParams(['id','checktype_id'],null,['bank','contactinfo','comment'])
    hsRes.inrequest.checkdate = requestService.getDate('spacebankcheck_checkdate')

    hsRes.space = Space.get(requestService.getLongDef('space_id',0))
    hsRes.spacebankcheck = Bankcheck.findByIdAndAgr_idAndAgrtype_id(hsRes.inrequest.id,hsRes.space?.id?:0,2)
    if (!hsRes.space||(!hsRes.spacebankcheck&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.bank)
      hsRes.result.errorcode<<1
    else if (!Bank.findByNameOrId(hsRes.inrequest.bank,hsRes.inrequest.bank))
      hsRes.result.errorcode<<2
    else if(Bank.findAllByNameOrId(hsRes.inrequest.bank,hsRes.inrequest.bank).size()>1)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.checktype_id)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.checkdate)
      hsRes.result.errorcode<<5

    if(!hsRes.result.errorcode){
      try {
        if (!hsRes.inrequest.id) hsRes.spacebankcheck = new Bankcheck(agr_id:hsRes.space.id,agrtype_id:2)
        hsRes.spacebankcheck.setData(hsRes.inrequest).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addspacebankcheck\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletespacebankcheck = {
    checkAccess(7)
    if (!checkSectionAccess(2)) return
    if (!checkSectionPermission(ASPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.space = Space.get(requestService.getLongDef('space_id',0))
    if (!hsRes.space) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Bankcheck.findByIdAndAgr_idAndAgrtype_id(requestService.getIntDef('id',0),hsRes.space.id,2)?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletespacebankcheck\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def spaceservagrs = {
    checkAccess(7)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.space = Space.get(requestService.getIntDef('id',0))
    if (!hsRes.space) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.spaceservagrs = Trade.findAllBySpace_idAndModstatus(hsRes.space.id,1,[sort:'id',order:'desc'])

    return hsRes
  }

  def spaceservpayments = {
    checkAccess(7)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.space = Space.get(requestService.getIntDef('id',0))
    if (!hsRes.space) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payrequests = Payrequest.findAllByAgreementtype_idAndAgreement_idInList(7,Trade.findAllBySpace_id(hsRes.space.id).collect{it.id}?:[-1],[sort:'paydate',order:'desc'])

    return hsRes
  }

  def spacecalculations = {
    checkAccess(7)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.space = Space.get(requestService.getIntDef('id',0))
    if (!hsRes.space) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.spacecalculations = Spacecalculation.findAllBySpace_id(hsRes.space.id,[sort:'calcdate',order:'desc'])
    hsRes.iscanedit = recieveSectionPermission(ASPEDIT)

    return hsRes
  }

  def deletespacecalculation = {
    checkAccess(7)
    if (!checkSectionAccess(2)) return
    if (!checkSectionPermission(ASPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.space = Space.get(requestService.getLongDef('space_id',0))
    if (!hsRes.space) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Spacecalculation.findBySpace_idAndId(hsRes.space.id,requestService.getIntDef('id',0))?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletespacecalculation\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def spacecalculation = {
    checkAccess(7)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.space = Space.get(requestService.getIntDef('space_id',0))
    if (!hsRes.space) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.spacecalc = Spacecalculation.findBySpace_idAndId(hsRes.space.id,requestService.getIntDef('id',0))
    hsRes.iscanedit = recieveSectionPermission(ASPEDIT)

    return hsRes
  }

  def updatespacecalculation = {
    checkAccess(7)
    if (!checkSectionAccess(2)) return
    if (!checkSectionPermission(ASPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['id','is_dop'],null,['schet'],null,['summa'])
    hsRes.inrequest.schetdate = requestService.getDate('spacecalc_schetdate')
    hsRes.inrequest.maindate = requestService.getDate('spacecalc_maindate')

    hsRes.space = Space.get(requestService.getLongDef('space_id',0))
    hsRes.spacecalc = Spacecalculation.findBySpace_idAndId(hsRes.space?.id?:0,hsRes.inrequest.id?:0)
    if (!hsRes.space||(!hsRes.spacecalc&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(hsRes.inrequest.summa&&hsRes.inrequest.summa<0)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.id){
      if(!hsRes.inrequest.maindate)
        hsRes.result.errorcode<<2
      else if(Spacecalculation.findAllBySpace_idAndMonthAndYearAndIs_dop(hsRes.space.id,hsRes.inrequest.maindate.getMonth()+1,hsRes.inrequest.maindate.getYear()+1900,hsRes.inrequest.is_dop?:0))
        hsRes.result.errorcode<<3
    }

    if(!hsRes.result.errorcode){
      try {
        if (!hsRes.inrequest.id) hsRes.spacecalc = new Spacecalculation(space_id:hsRes.space.id,is_dop:hsRes.inrequest.is_dop?:0).setBaseData(month:hsRes.inrequest.maindate.getMonth()+1,year:hsRes.inrequest.maindate.getYear()+1900,calcdate:new Date())
        hsRes.spacecalc.setData(hsRes.inrequest).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/updatespacecalculation\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def spacehistory = {
    checkAccess(7)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.space = Space.get(lId)
    if (!hsRes.space) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.history = new SpacehistSearch().csiFindSpaceHistory(hsRes.space.id)
    hsRes.spacetypes = Spacetype.list().inject([:]){map, spacetype -> map[spacetype.id]=spacetype.name;map}
    hsRes.arendatypes = Arendatype.list().inject([:]){map, arendatype -> map[arendatype.id]=arendatype.name;map}

    return hsRes
  }

  def spacepayrequests = {
    checkAccess(7)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.iscanedit = recieveSectionPermission(ASPEDIT)

    return hsRes
  }

  def spacepayrequestslist = {
    checkAccess(7)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes+=requestService.getParams(['modstatus'],null,['client_name'])

    hsRes.searchresult = new SpaceSearch().csiSelectSpacesForPayrequest(hsRes.inrequest.client_name?:'',hsRes.inrequest.modstatus?:0,20,requestService.getOffset())
    hsRes.today = new Date()

    return hsRes
  }

  def createsppayrequests={
    checkAccess(7)
    checkSectionAccess(2)
    if (!checkSectionPermission(ASPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def spaceIds = requestService.getIds('space')

    if (!spaceIds) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    try {
      spaceIds?.each{ Space.get(it)?.generatePayrequest() }
    } catch(Exception e) {
      log.debug("Error save data in Agreement/createsppayrequests\n"+e.toString());
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Arenda <<<////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Lizing >>>////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def lizingfilter = {
    checkAccess(7)
    checkSectionAccess(4)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails){
      hsRes.inrequest = session.agrlastRequest
    }

    hsRes.users = Lizing.getResponsibles()
    hsRes.projects = Project.list()
    hsRes.cars = Car.list(sort:'name')
    hsRes.iscanedit = recieveSectionPermission(ALIZEDIT)

    return hsRes
  }

  def lizings = {
    checkAccess(7)
    checkSectionAccess(4)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.agrlastRequest
      session.agrlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['debt','lid','project_id','car_id','cessionstatus'],['responsible'],['company_name'])
      hsRes.inrequest.lizsort = requestService.getIntDef('lizsort',0)
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.offset = requestService.getOffset()
      session.agrlastRequest = hsRes.inrequest
    }
    session.agrlastRequest.agrobject = 4

    hsRes.searchresult = new LizingSearch().csiSelectLizings(hsRes.inrequest.lid?:0,hsRes.inrequest.company_name?:'',
                                                             hsRes.inrequest.responsible?:0l,hsRes.inrequest.debt?:0,
                                                             hsRes.inrequest.lizsort?:0,hsRes.inrequest.modstatus?:0,
                                                             hsRes.inrequest.project_id?:0,hsRes.inrequest.car_id?:0,
                                                             hsRes.inrequest.cessionstatus?:0,
                                                             session.user.group.visualgroup_id,20,hsRes.inrequest.offset)
    hsRes.restfees = hsRes.searchresult.records.inject([:]){map, lizing -> map[lizing.id]=agentKreditService.computeLizingDebt(lizing);map}

    return hsRes
  }

  def lizing = {
    checkAccess(7)
    checkSectionAccess(4)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.lizing = Lizing.get(lId)
    if (!hsRes.lizing&&lId) {
      response.sendError(404)
      return
    }
    hsRes+=requestService.getParams(['company_id'])

    hsRes.arendator = Company.get(hsRes.lizing?.arendator?:hsRes.inrequest.company_id)?.name
    hsRes.arendodatel = Company.get(hsRes.lizing?.arendodatel)?.name
    hsRes.lizing_owner = Company.get(hsRes.lizing?.owner)?.name
    hsRes.users = User.list()
    hsRes.agrs = Lizing.findAllByArendatorAndModstatus(hsRes.lizing?.arendodatel?:0,1)
    hsRes.projects = Project.list(sort:'name')
    hsRes.cars = Car.list(sort:'name')
    hsRes.restfee = agentKreditService.computeLizingDebt(hsRes.lizing)
    hsRes.iscanaddcompanies = session.user.group?.is_companyinsert?true:false
    hsRes.iscanedit = recieveSectionPermission(ALIZEDIT)
    hsRes.iscanpay = recieveSectionPermission(ALIZPAY)
    hsRes.iscandelete = !(Payrequest.findAllByAgreementtype_idAndAgreement_idAndModstatusGreaterThan(4,hsRes.lizing?.id?:0,-1)||Lizingplanpayment.findAllByLizing_idAndModstatusGreaterThanEquals(hsRes.lizing?.id?:0,0))&&hsRes.iscanedit
    hsRes.isCanRestore = hsRes.lizing?.modstatus==0&&hsRes.lizing?.enddate>new Date()

    return hsRes
  }

  def lizmainagrlist={
    checkAccess(7)
    checkSectionAccess(4)
    requestService.init(this)

    return [agrs:Lizing.findAllByArendatorAndModstatus(Company.findByNameOrInn(requestService.getStr('arendodatel'),requestService.getStr('arendodatel'))?.id?:0,1)]
  }

  def updatelizing = {
    checkAccess(7)
    if (!checkSectionAccess(4)) return
    if (!checkSectionPermission(ALIZEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    def lId = requestService.getLongDef('id',0)
    hsRes+=requestService.getParams(['lizsort','is_dirsalary','mainagr_id','project_id','car_id','modstatus'],
                                    ['responsible'],['arendator','arendodatel','anumber','description','comment'],null,
                                    ['summa','initialfee','startsaldo'])
    hsRes.inrequest.adate = requestService.getDate('adate')
    hsRes.inrequest.enddate = requestService.getDate('enddate')
    hsRes.inrequest.startsaldodate = requestService.getDate('startsaldodate')

    hsRes.lizing = Lizing.get(lId)
    if (!hsRes.lizing&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.arendator)
      hsRes.result.errorcode<<1
    else if(!Company.findByNameOrInn(hsRes.inrequest.arendator,hsRes.inrequest.arendator))
      hsRes.result.errorcode<<2
    if(Company.findAllByNameOrInn(hsRes.inrequest.arendator,hsRes.inrequest.arendator).size()>1)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.arendodatel)
      hsRes.result.errorcode<<4
    else if(!Company.findByNameOrInn(hsRes.inrequest.arendodatel,hsRes.inrequest.arendodatel))
      hsRes.result.errorcode<<5
    if(Company.findAllByNameOrInn(hsRes.inrequest.arendodatel,hsRes.inrequest.arendodatel).size()>1)
      hsRes.result.errorcode<<6
    if(!hsRes.inrequest.modstatus&&!hsRes.inrequest.comment)
      hsRes.result.errorcode<<7
    if(!hsRes.inrequest.anumber)
      hsRes.result.errorcode<<10
    if(!hsRes.inrequest.adate)
      hsRes.result.errorcode<<11
    if (!hsRes.lizing){
      if(!hsRes.inrequest.enddate)
        hsRes.result.errorcode<<12
      else if(hsRes.inrequest.enddate<hsRes.inrequest.adate)
        hsRes.result.errorcode<<13
      if(!hsRes.inrequest.summa)
        hsRes.result.errorcode<<14
    }

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.lizing = new Lizing().setBaseData(hsRes.inrequest)
        hsRes.result.lizing = hsRes.lizing.setData(hsRes.inrequest).updateModstatus(hsRes.inrequest.modstatus).csiSetAdmin(session.user.id).csiSetDirSalary(hsRes.inrequest.is_dirsalary,session.user.group.is_rep_dirsalary).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Agreement/updatelizing\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def lizingplanpayments = {
    checkAccess(7)
    checkSectionAccess(4)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.lizing = Lizing.get(lId)
    if (!hsRes.lizing) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.planpayments = Lizingplanpayment.findAllByLizing_idAndModstatusGreaterThanEquals(hsRes.lizing.id,0,[sort:'paydate',order:'asc'])
    hsRes.iscanedit = recieveSectionPermission(ALIZPAY)

    return hsRes
  }

  def lizingplanpayment = {
    checkAccess(7)
    checkSectionAccess(4)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes+=requestService.getParams(null,null,['id'])
    hsRes.lizing = Lizing.get(requestService.getIntDef('lizing_id',0))
    if (!hsRes.lizing) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.lizingplanpayment = Lizingplanpayment.get(hsRes.inrequest.id)
    hsRes.iscanedit = recieveSectionPermission(ALIZPAY)

    return hsRes
  }

  def addlizingplanpayment = {
    checkAccess(7)
    if (!checkSectionAccess(4)) return
    if (!checkSectionPermission(ALIZPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getLongDef('id',0)
    hsRes.lizing = Lizing.get(requestService.getLongDef('lizing_id',0))
    hsRes.lizingplanpayment = Lizingplanpayment.get(lId)
    if (!hsRes.lizing||(!hsRes.lizingplanpayment&&lId)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['modstatus','is_insurance'],null,null,null,['summa'])
    hsRes.inrequest.planpayment_paydate = requestService.getDate('planpayment_paydate')

    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.planpayment_paydate)
      hsRes.result.errorcode<<2
    if(hsRes.inrequest.summa<0)
      hsRes.result.errorcode<<3

    if(!hsRes.result.errorcode){
      try {
        if (!lId) hsRes.lizingplanpayment = new Lizingplanpayment(lizing_id:hsRes.lizing.id)
        hsRes.lizingplanpayment.setData(hsRes.inrequest).csiSetModstatus(hsRes.inrequest.modstatus).csiSetAdmin(session.user.id).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addlizingplanpayment\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletelizingplanpayment = {
    checkAccess(7)
    if (!checkSectionAccess(4)) return
    if (!checkSectionPermission(ALIZPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId = requestService.getLongDef('lizing_id',0)
    hsRes+=requestService.getParams(null,null,['id'])

    hsRes.lizing = Lizing.get(lId)
    if (!hsRes.lizing||!hsRes.inrequest.id) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Lizingplanpayment.get(hsRes.inrequest.id).csiSetModstatus(-1).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletelizingplanpayment\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def uploadlizingpayments = {
    checkAccess(7)
    if (!checkSectionAccess(4)) return
    if (!checkSectionPermission(ALIZPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.lizing = Lizing.get(requestService.getLongDef('id',0))
    if (!hsRes.lizing) {
      response.sendError(404)
      return
    }

    hsRes.result = parseService.parseLizingpaymentsFile(request.getFile('file'))

    if(!hsRes.result.errorcode){
      try {
        if (!Lizingplanpayment.findByLizing_idAndModstatusGreaterThan(hsRes.lizing.id,0)) {
          Lizingplanpayment.findAllByLizing_id(hsRes.lizing.id).each{ it.delete(flush:true) }
          hsRes.result.preparedData.each{
            if(it.summa!=0) new Lizingplanpayment(lizing_id:hsRes.lizing.id).csiSetAdmin(session.user.id).setData(it).save(failOnError:true)
          }
        }
      } catch(Exception e) {
        log.debug("Error save data in Agreement/uploadlizingpayments\n"+e.toString())
        hsRes.result.errorcode<<100
      }
    }

    return hsRes.result
  }

  def lzpayrequests = {
    checkAccess(7)
    checkSectionAccess(4)
    if (!checkSectionPermission(ALIZPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.lizing = Lizing.get(requestService.getIntDef('id',0))
    if (!hsRes.lizing) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payrequests = Payrequest.findAllByAgreementtype_idAndAgreement_idAndModstatusGreaterThan(4,hsRes.lizing.id,-1,[sort:'paydate',order:'desc'])

    return hsRes
  }

  def lzpayrequest = {
    checkAccess(7)
    checkSectionAccess(4)
    if (!checkSectionPermission(ALIZPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agr = Lizing.get(requestService.getIntDef('agr_id',0))
    if (!hsRes.agr) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    hsRes.bank = new BankaccountSearch().csiFindAccounts(hsRes.payrequest?hsRes.payrequest.fromcompany_id:hsRes.agr.creditor?:hsRes.agr.arendator,1,1)
    hsRes.project = Project.findAllByModstatus(1,[sort:'name'])
    hsRes.defproject_id = Project.findByIs_main(1)?.id?:0
    hsRes.expensetype = new ExpensetypeSearch().csiGetList()
    hsRes.tobanks = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(hsRes.agr.arendodatel,1,1).collect{ Bank.get(it.bank_id) }
    hsRes.tobanks.sort{it.name}
    hsRes.ndsdef = Company.get(hsRes.agr.arendodatel)?.taxoption_id==1?true:false
    hsRes.defaultdata = new LizingplanpaymentSearch().csiFindDefaultDataForPayment(hsRes.agr.id)
    hsRes.iscantag = recieveSectionPermission(APTAG)

    return hsRes
  }

  def addlzpayrequest = {
    checkAccess(7)
    if (!checkSectionAccess(4)) return
    if (!checkSectionPermission(ALIZPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['id','is_nds','frombank','project_id','expensetype_id','is_task'],null,['destination','tobank'],null,['summa'])
    hsRes.inrequest.payrequest_paydate = requestService.getDate('payrequest_paydate')

    hsRes.agr = Lizing.get(requestService.getIntDef('agr_id',0))
    hsRes.payrequest = Payrequest.get(hsRes.inrequest.id)
    if (!hsRes.agr||(!hsRes.payrequest&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(hsRes.inrequest.summa<=0)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.tobank)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.payrequest_paydate)
      hsRes.result.errorcode<<3
    if(recieveSectionPermission(APTAG)&&!hsRes.inrequest.expensetype_id)
      hsRes.result.errorcode<<4

    if(!hsRes.result.errorcode){
      try {
        def agrpayment_id = Lizingplanpayment.findByLizing_idAndPaydateAndModstatusAndIs_insuranceAndSummaBetween(hsRes.agr.id,hsRes.inrequest.payrequest_paydate,0,0,hsRes.inrequest.summa.toLong(),hsRes.inrequest.summa.toLong()+1)?.id?:0
        if (!hsRes.inrequest.id) hsRes.payrequest = new Payrequest(paytype:1,paycat:1,agreementtype_id:4,agreement_id:hsRes.agr.id).csiSetLizingAgrData(hsRes.agr).csiSetAccData(hsRes.inrequest.tobank).csiSetInitiator(hsRes.user.id)
        hsRes.payrequest.setGeneralData(hsRes.inrequest).csiSetPayrequestTag(hsRes.inrequest,recieveSectionPermission(APTAG),hsRes.user.id).csiSetBankaccount_id(hsRes.inrequest.frombank).save(failOnError:true)
        if (hsRes.inrequest.is_task&&hsRes.payrequest.taskpay_id==0){
          def taskpay_id = new Taskpay(paygroup:hsRes.payrequest.paygroup,is_manual:1).csiSetTaskpay(term:String.format('%td.%<tm.%<tY',hsRes.payrequest.paydate),company_id:hsRes.payrequest.fromcompany_id,summa:hsRes.payrequest.summa).csiSetInitiator(hsRes.user.id).csiSetBankaccountId(hsRes.payrequest.bankaccount_id).save(flush:true,failOnError:true)?.id?:0
          hsRes.payrequest.csiSetTaskpay_id(taskpay_id).csiSetModstatus(1).save(flush:true,failOnError:true)
        }
        if (agrpayment_id) Lizingplanpayment.get(agrpayment_id).csiSetModstatus(1).save(flush:true,failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addlzpayrequest\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletelzpayrequest = {
    checkAccess(7)
    if (!checkSectionAccess(4)) return
    if (!checkSectionPermission(ALIZPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agr = Lizing.get(requestService.getLongDef('agr_id',0))
    hsRes.payrequest = Payrequest.findByAgreementtype_idAndAgreement_idAndId(4,hsRes.agr?.id?:-1,requestService.getLongDef('id',0))
    if (!hsRes.agr||hsRes.payrequest?.modstatus!=0) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletelzpayrequest\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def lizpayments = {
    checkAccess(7)
    checkSectionAccess(4)
    if (!checkSectionPermission(ALIZPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.lizing = Lizing.get(requestService.getIntDef('id',0))
    if (!hsRes.lizing) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payments = Payment.findAllByAgreementtype_idAndAgreement_id(4,hsRes.lizing.id,[sort:'paydate',order:'desc'])

    return hsRes
  }

  def lizdoppayments = {
    checkAccess(7)
    checkSectionAccess(4)
    if (!checkSectionPermission(ALIZPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.lizing = Lizing.get(requestService.getIntDef('id',0))
    if (!hsRes.lizing) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payments = Payrequest.findAllByAgreementtype_idAndAgreement_idAndCar_id(0,0,hsRes.lizing.car_id?:-1,[sort:'paydate',order:'desc'])
    hsRes.iscanedit = recieveSectionPermission(APTAG)

    return hsRes
  }

  def lizdoppayment = {
    checkAccess(7)
    checkSectionAccess(4)
    if (!checkSectionPermission(ALIZPAY)||!checkSectionPermission(APTAG)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agr = Lizing.get(requestService.getIntDef('agr_id',0))
    if (!hsRes.agr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.project = Project.findAllByModstatus(1,[sort:'name'])
    hsRes.defproject_id = Project.findByIs_main(1)?.id?:0
    hsRes.expensetype = new ExpensetypeSearch().csiGetCarList()

    return hsRes
  }

  def lizdoppaymentbanklist = {
    checkAccess(7)
    checkSectionAccess(4)
    requestService.init(this)

    return [banks:Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(requestService.getIntDef('company_id',0),1,1).collect{Bank.get(it.bank_id)}]
  }

  def addlizdoppayment = {
    checkAccess(7)
    checkSectionAccess(4)
    if (!checkSectionPermission(ALIZPAY)||!checkSectionPermission(APTAG)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    hsRes.agr = Lizing.get(requestService.getIntDef('agr_id',0))
    if (!hsRes.agr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['fromcompany_id','tocompany_id','is_nds','project_id','expensetype_id','is_task'],
                                     null,['tobank','destination'],null,['summa'])
    hsRes.inrequest.paydate = requestService.getDate('lizdoppayment_paydate')?.format('dd.MM.yyyy')

    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.paydate)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.fromcompany_id)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.tocompany_id)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.tobank)
      hsRes.result.errorcode<<5
    if(!hsRes.inrequest.expensetype_id)
      hsRes.result.errorcode<<6

    if(!hsRes.result.errorcode){
      try {
        hsRes.payrequest = new Payrequest().csiSetPayrequest(hsRes.inrequest+[paycat:4,paytype:1]).csiSetPayrequestTag(hsRes.inrequest+[car_id:hsRes.agr.car_id],recieveSectionPermission(APTAG),hsRes.user.id).csiSetInitiator(hsRes.user.id).save(failOnError:true)
        if (hsRes.inrequest.is_task){
          def taskpay_id = new Taskpay(paygroup:hsRes.payrequest.paygroup,is_manual:1).csiSetTaskpay(term:String.format('%td.%<tm.%<tY',hsRes.payrequest.paydate),company_id:hsRes.payrequest.fromcompany_id,summa:hsRes.payrequest.summa).csiSetInitiator(hsRes.user.id).save(flush:true,failOnError:true)?.id?:0
          hsRes.payrequest.csiSetTaskpay_id(taskpay_id).csiSetModstatus(1).save(flush:true,failOnError:true)
        }
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addlizdoppayment\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }

  def lizingdopagrs = {
    checkAccess(7)
    checkSectionAccess(4)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.lizing = Lizing.get(requestService.getIntDef('id',0))
    if (!hsRes.lizing) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.lizingdopagrs = Lizingdopagr.findAllByLizing_id(hsRes.lizing.id,[sort:'id',order:'desc'])
    hsRes.firstagrid = Lizingdopagr.getMinId(hsRes.lizing.id)
    hsRes.iscanedit = recieveSectionPermission(ALIZEDIT)

    return hsRes
  }

  def lizingdopagr = {
    checkAccess(7)
    checkSectionAccess(4)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.lizing = Lizing.get(requestService.getIntDef('lizing_id',0))
    if (!hsRes.lizing) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.lizingdopagr = Lizingdopagr.get(requestService.getIntDef('id',0))
    hsRes.iscanedit = recieveSectionPermission(ALIZEDIT)

    return hsRes
  }

  def addlizingdopagr = {
    checkAccess(7)
    if (!checkSectionAccess(4)) return
    if (!checkSectionPermission(ALIZEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]
 
    hsRes+=requestService.getParams(['id'],null,['nomer','comment'],null,['summa'])
    hsRes.inrequest.dsdate = requestService.getDate('lizingdopagr_dsdate')
    hsRes.inrequest.startdate = requestService.getDate('lizingdopagr_startdate')
    hsRes.inrequest.enddate = requestService.getDate('lizingdopagr_enddate')

    hsRes.lizing = Lizing.get(requestService.getLongDef('lizing_id',0))
    hsRes.lizingdopagr = Lizingdopagr.get(hsRes.inrequest.id)
    if (!hsRes.lizing||(!hsRes.lizingdopagr&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.nomer)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.dsdate)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.startdate)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.enddate)
      hsRes.result.errorcode<<5

    if(!hsRes.result.errorcode){
      try {
        if (!hsRes.inrequest.id) hsRes.lizingdopagr = new Lizingdopagr(lizing_id:hsRes.lizing.id)
        hsRes.lizingdopagr.setData(hsRes.inrequest).csiSetAdmin(session.user.id).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addlizingdopagr\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletelizingdopagr = {
    checkAccess(7)
    if (!checkSectionAccess(4)) return
    if (!checkSectionPermission(ALIZEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.lizing = Lizing.get(requestService.getLongDef('lizing_id',0))
    if (!hsRes.lizing||Lizingdopagr.getMinId(hsRes.lizing?.id)==requestService.getLongDef('id',0)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Lizingdopagr.findByLizing_idAndId(hsRes.lizing.id,requestService.getLongDef('id',0))?.csiSetAdmin(session.user.id)?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletelizingdopagr\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def lizinghistory = {
    checkAccess(7)
    checkSectionAccess(4)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.lizing = Lizing.get(lId)
    if (!hsRes.lizing) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.history = new LizinghistSearch().csiFindLizingHistory(hsRes.lizing.id)

    return hsRes
  }

  def lizingpayrequests = {
    checkAccess(7)
    checkSectionAccess(4)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.iscanedit = recieveSectionPermission(ALIZPAY)

    return hsRes
  }

  def lizingpayrequestslist = {
    checkAccess(7)
    checkSectionAccess(4)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes+=requestService.getParams(['modstatus'],null,['client_name'])
    hsRes.inrequest.startdate = requestService.getDate('startdate')
    hsRes.inrequest.enddate = requestService.getDate('enddate')

    hsRes.searchresult = new LizingplanpaymentSearch().csiSelectLizingsPayments(hsRes.inrequest.client_name?:'',hsRes.inrequest.modstatus?:0,
                                            hsRes.inrequest.startdate,hsRes.inrequest.enddate,20,requestService.getOffset())

    return hsRes
  }

  def createlzpayrequests={
    checkAccess(7)
    checkSectionAccess(4)
    if (!checkSectionPermission(ALIZPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lplanpaymentIds = requestService.getIds('lplanpayment')

    if (!lplanpaymentIds) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    try {
      lplanpaymentIds?.each{ Lizingplanpayment.get(it)?.generatePayrequest().save(failOnError:true) }
    } catch(Exception e) {
      log.debug("Error save data in Agreement/createlzpayrequests\n"+e.toString());
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Lizing <<<////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Trade >>>/////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def tradefilter = {
    checkAccess(7)
    checkSectionAccess(7)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails){
      hsRes.inrequest = session.agrlastRequest
    }

    hsRes.users = Trade.getResponsibles()
    hsRes.iscanedit = recieveSectionPermission(ATREDIT)

    return hsRes
  }

  def trades = {
    checkAccess(7)
    checkSectionAccess(7)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.agrlastRequest
      session.agrlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['debt'],['responsible'],['inn','company_name'])
      hsRes.inrequest.tradesort = requestService.getIntDef('tradesort',0)
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.tradetype = requestService.getIntDef('tradetype',0)
      hsRes.inrequest.offset = requestService.getOffset()
      session.agrlastRequest = hsRes.inrequest
    }
    session.agrlastRequest.agrobject = 7

    hsRes.searchresult = new TradeSearch().csiSelectTrades(hsRes.inrequest.inn?:'',hsRes.inrequest.company_name?:'',
                                                           hsRes.inrequest.responsible?:0l,hsRes.inrequest.debt?:0,
                                                           hsRes.inrequest.tradesort?:0,hsRes.inrequest.tradetype?:0,
                                                           hsRes.inrequest.modstatus?:0,session.user.group.visualgroup_id,
                                                           20,hsRes.inrequest.offset)
    hsRes.tradecats = Tradecat.list().inject([:]){map, cat -> map[cat.id]=cat.name;map}

    return hsRes
  }

  def trade = {
    checkAccess(7)
    checkSectionAccess(7)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.trade = Trade.get(lId)
    if (!hsRes.trade&&lId) {
      response.sendError(404)
      return
    }

    hsRes.client = Company.get(hsRes.trade?.client)?.name
    hsRes.supplier = Company.get(hsRes.trade?.supplier)?.name
    hsRes.spaces = hsRes.trade?.client?Space.findAllByArendatorAndIs_addpayment(hsRes.trade.client,1):Space.findAllByIs_addpayment(1)
    hsRes.responsiblies = new UserpersSearch().csiFindByAccessrigth(ATREDIT)
    hsRes.iscanaddcompanies = session.user.group?.is_companyinsert?true:false
    hsRes.tradecats = Tradecat.list()
    hsRes.iscanedit = recieveSectionPermission(ATREDIT)

    return hsRes
  }

  def tradespacelist={
    checkAccess(7)
    checkSectionAccess(7)
    requestService.init(this)

    return [spaces:Space.findAllByArendatorAndModstatusAndIs_addpayment(requestService.getIntDef('company_id',0),1,1)]
  }

  def updatetrade = {
    checkAccess(7)
    if (!checkSectionAccess(7)) return
    if (!checkSectionPermission(ATREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    def lId = requestService.getLongDef('id',0)
    hsRes+=requestService.getParams(['tradetype','tradecat_id','tradesort','summa','paytype','space_id'],['responsible'],
                                    ['client','supplier','anumber','description','comment'])
    hsRes.inrequest.adate = requestService.getDate('adate')
    hsRes.inrequest.enddate = requestService.getDate('enddate')

    hsRes.trade = Trade.get(lId)
    if (!hsRes.trade&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.client)
      hsRes.result.errorcode<<1
    else if(!Company.findByNameOrInn(hsRes.inrequest.client,hsRes.inrequest.client))
      hsRes.result.errorcode<<2
    if(Company.findAllByNameOrInn(hsRes.inrequest.client,hsRes.inrequest.client).size()>1)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.supplier)
      hsRes.result.errorcode<<4
    else if(!Company.findByNameOrInn(hsRes.inrequest.supplier,hsRes.inrequest.supplier))
      hsRes.result.errorcode<<5
    if(Company.findAllByNameOrInn(hsRes.inrequest.supplier,hsRes.inrequest.supplier).size()>1)
      hsRes.result.errorcode<<6
    if(!hsRes.inrequest.tradecat_id)
      hsRes.result.errorcode<<7
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<8
    if(!hsRes.inrequest.anumber)
      hsRes.result.errorcode<<9
    if(!hsRes.inrequest.adate)
      hsRes.result.errorcode<<10
    if(hsRes.inrequest.enddate&&hsRes.inrequest.enddate<hsRes.inrequest.adate)
      hsRes.result.errorcode<<11
    if(hsRes.inrequest.tradecat_id==7&&!hsRes.inrequest.space_id)
      hsRes.result.errorcode<<12

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.trade = new Trade()
        hsRes.result.trade = hsRes.trade.setData(hsRes.inrequest).updateModstatus().csiSetAdmin(session.user.id).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Agreement/updatetrade\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def trpayments = {
    checkAccess(7)
    checkSectionAccess(7)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.trade = Trade.get(requestService.getIntDef('id',0))
    if (!hsRes.trade) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payments = Payrequest.findAllByAgreementtype_idAndAgreement_idAndModstatusGreaterThan(7,hsRes.trade.id,-1,[sort:'paydate',order:'desc'])

    return hsRes
  }

  def tradepayrequest = {
    checkAccess(7)
    checkSectionAccess(7)
    if (!checkSectionPermission(ATREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agr = Trade.get(requestService.getIntDef('agr_id',0))
    if (!hsRes.agr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.bank = new BankaccountSearch().csiFindAccounts(hsRes.agr.client,1,1)
    hsRes.project = Project.findAllByModstatus(1,[sort:'name'])
    hsRes.defproject_id = Project.findByIs_main(1)?.id?:0
    hsRes.expensetype = new ExpensetypeSearch().csiGetList()
    hsRes.tobanks = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(hsRes.agr.supplier,1,1).collect{ Bank.get(it.bank_id) }
    hsRes.tobanks.sort{it.name}
    hsRes.iscantag = recieveSectionPermission(APTAG)

    return hsRes
  }

  def addtradepayrequest = {
    checkAccess(7)
    if (!checkSectionAccess(7)) return
    if (!checkSectionPermission(ATREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['id','is_nds','frombank','project_id','expensetype_id','is_task'],null,['destination','tobank'],null,['summa'])
    hsRes.inrequest.payrequest_paydate = requestService.getDate('payrequest_paydate')

    hsRes.agr = Trade.get(requestService.getIntDef('agr_id',0))
    hsRes.payrequest = Payrequest.get(hsRes.inrequest.id)
    if (!hsRes.agr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(hsRes.inrequest.summa<=0)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.tobank)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.payrequest_paydate)
      hsRes.result.errorcode<<3
    if(recieveSectionPermission(APTAG)&&!hsRes.inrequest.expensetype_id)
      hsRes.result.errorcode<<4

    if(!hsRes.result.errorcode){
      try {
        if (!hsRes.inrequest.id) hsRes.payrequest = new Payrequest(paytype:1,paycat:1,agreementtype_id:7,agreement_id:hsRes.agr.id).fillAgrDataFrom(hsRes.agr).csiSetAccData(hsRes.inrequest.tobank).csiSetInitiator(hsRes.user.id)
        hsRes.payrequest.setGeneralData(hsRes.inrequest).csiSetPayrequestTag(hsRes.inrequest,recieveSectionPermission(APTAG),hsRes.user.id).csiSetBankaccount_id(hsRes.inrequest.frombank).save(failOnError:true)
        if (hsRes.inrequest.is_task&&hsRes.payrequest.taskpay_id==0){
          def taskpay_id = new Taskpay(paygroup:hsRes.payrequest.paygroup,is_manual:1).csiSetTaskpay(term:String.format('%td.%<tm.%<tY',hsRes.payrequest.paydate),company_id:hsRes.payrequest.fromcompany_id,summa:hsRes.payrequest.summa).csiSetInitiator(hsRes.user.id).csiSetBankaccountId(hsRes.payrequest.bankaccount_id).save(flush:true,failOnError:true)?.id?:0
          hsRes.payrequest.csiSetTaskpay_id(taskpay_id).csiSetModstatus(1).save(flush:true,failOnError:true)
        }
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addtradepayrequest\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletetradepayrequest = {
    checkAccess(7)
    if (!checkSectionAccess(7)) return
    if (!checkSectionPermission(ATREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agr = Trade.get(requestService.getLongDef('agr_id',0))
    hsRes.payrequest = Payrequest.findByAgreementtype_idAndAgreement_idAndId(7,hsRes.agr?.id?:-1,requestService.getLongDef('id',0))
    if (!hsRes.agr||hsRes.payrequest?.modstatus!=0) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletetradepayrequest\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def tradehistory = {
    checkAccess(7)
    checkSectionAccess(7)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.trade = Trade.get(lId)
    if (!hsRes.trade) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.history = new TradehistSearch().csiFindTradeHistory(hsRes.trade.id)
    hsRes.tradecats = Tradecat.list().inject([:]){map, cat -> map[cat.id]=cat.name;map}

    return hsRes
  }

  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Trade <<</////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Kredit >>>////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def kreditfilter = {
    checkAccess(7)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails){
      hsRes.inrequest = session.agrlastRequest
    }

    hsRes.banks = Kredit.getBanks()
    hsRes.users = Kredit.getResponsibles()
    hsRes.iscanedit = recieveSectionPermission(AKREDEDIT)
    hsRes.is_showreal = recieveSectionPermission(AKRREAL)

    return hsRes
  }

  def kredits = {
    checkAccess(7)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.agrlastRequest
      session.agrlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['kid','valuta_id','is_real','is_tech','is_realtech','cessionstatus','zalogstatus','is_nocheck'],
                                      ['responsible'],['inn','company_name','bankname'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.offset = requestService.getOffset()
      session.agrlastRequest = hsRes.inrequest
    }
    session.agrlastRequest.agrobject = 3

    hsRes.searchresult = new KreditSearch().csiSelectKredits(hsRes.inrequest.kid?:0,hsRes.inrequest.inn?:'',hsRes.inrequest.company_name?:'',
                                                             hsRes.inrequest.bankname?:'',hsRes.inrequest.valuta_id?:0,
                                                             hsRes.inrequest.is_real?:0,hsRes.inrequest.is_tech?:0,hsRes.inrequest.is_realtech?:0,
                                                             hsRes.inrequest.responsible?:0,hsRes.inrequest.cessionstatus?:0,
                                                             hsRes.inrequest.zalogstatus?:0,hsRes.inrequest.modstatus?:0,
                                                             hsRes.inrequest.is_nocheck?:0,recieveSectionPermission(AKRREAL),
                                                             session.user.group.visualgroup_id,20,hsRes.inrequest.offset)
    hsRes.valutas = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code.toLowerCase();map}
    hsRes.debts = hsRes.searchresult.records.inject([:]){map, kredit -> map[kredit.id]=agentKreditService.computeKreditDebt(kredit);map}

    return hsRes
  }

  def kredit = {
    checkAccess(7)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.kredit = Kredit.get(lId)
    if (!hsRes.kredit&&lId) {
      response.sendError(404)
      return
    }
    hsRes+=requestService.getParams(['company_id'])

    hsRes.client = Company.get(hsRes.kredit?.client?:hsRes.inrequest.company_id?:0)?.name
    hsRes.bank = Bank.get(hsRes.kredit?.bank_id?:'')
    hsRes.users = new UserpersSearch().csiFindByAccessrigth(AKREDEDIT)
    hsRes.clients = Client.findAllByModstatusAndIdNotEqual(1,hsRes.kredit?.client_id?:0)+[Client.get(hsRes.kredit?.client_id?:0)]-null
    hsRes.debt = agentKreditService.computeKreditDebt(hsRes.kredit)
    hsRes.percents = agentKreditService.computeKreditPercentByDate(hsRes.kredit?.id?:0,new Date(),null)
    hsRes.projects = Project.list()
    hsRes.iscanedit = recieveSectionPermission(AKREDEDIT)
    hsRes.iscanpay = recieveSectionPermission(AKREDPAY)
    hsRes.iscanclient = recieveSectionPermission(AKRCLIENT)
    hsRes.isCanDelete = !(Payrequest.findAllByAgreementtype_idAndAgreement_idAndModstatusGreaterThan(3,hsRes.kredit?.id?:0,-1)||Kreditline.findAllByKredit_idAndModstatusGreaterThanEquals(hsRes.kredit?.id?:0,0)||Agentkredit.findAllByKredit_id(hsRes.kredit?.id?:0))&&hsRes.iscanedit
    hsRes.ishaveAgentagr = Agentkredit.findAllByKredit_id(hsRes.kredit?.id?:0) as Boolean
    hsRes.isCanClose = !Kreditpayment.findAllByKredit_idAndModstatusLessThan(hsRes.kredit?.id?:-1,2)&&hsRes.debt<=0
    hsRes.isCanEarlyclose = !hsRes.isCanClose&&!hsRes.isCanDelete&&hsRes.kredit?.modstatus==1
    hsRes.isCanRestore = hsRes.kredit?.modstatus==0&&hsRes.kredit?.enddate>new Date()

    return hsRes
  }

  def updatekredit = {
    checkAccess(7)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(AKREDEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    def lId = requestService.getLongDef('id',0)
    hsRes+=requestService.getParams(['valuta_id','is_agr','is_cbcalc','kredtype','is_check','modstatus','payterm',
                                     'repaymenttype_id','monthnumber','kreditsort','kredittransh','project_id'],
                                    ['responsible'],['client','bank','anumber','comment','aim','sschet','percschet','comschet'],null,
                                    ['summa','rate','startsumma','agentsum'])
    hsRes.inrequest.adate = requestService.getDate('adate')
    hsRes.inrequest.startdate = requestService.getDate('startdate')
    hsRes.inrequest.enddate = requestService.getDate('enddate')
    hsRes.inrequest.startsaldodate = requestService.getDate('startsaldodate')
    if(recieveSectionPermission(AKRCLIENT)) hsRes.inrequest.client_id = requestService.getIntDef('client_id',0)

    hsRes.kredit = Kredit.get(lId)
    if (!hsRes.kredit&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.kredit){
      if(!hsRes.inrequest.client)
        hsRes.result.errorcode<<1
      else if(!Company.findByNameOrInn(hsRes.inrequest.client,hsRes.inrequest.client))
        hsRes.result.errorcode<<2
      else if(Company.findAllByNameOrInn(hsRes.inrequest.client,hsRes.inrequest.client).size()>1)
        hsRes.result.errorcode<<3
      if(!hsRes.inrequest.bank)
        hsRes.result.errorcode<<4
      else if (!Bank.findByNameOrId(hsRes.inrequest.bank,hsRes.inrequest.bank))
        hsRes.result.errorcode<<11
      else if(Bank.findAllByNameOrId(hsRes.inrequest.bank,hsRes.inrequest.bank).size()>1)
        hsRes.result.errorcode<<15
      if(!hsRes.inrequest.anumber)
        hsRes.result.errorcode<<5
      if(!hsRes.inrequest.adate)
        hsRes.result.errorcode<<6
      if(!hsRes.inrequest.summa)
        hsRes.result.errorcode<<7
      if(!hsRes.inrequest.rate)
        hsRes.result.errorcode<<8
      if(!hsRes.inrequest.startdate)
        hsRes.result.errorcode<<16
      if(!hsRes.inrequest.enddate)
        hsRes.result.errorcode<<10
      if(hsRes.inrequest.startdate&&Tools.computeMonthDiff(hsRes.inrequest.startdate,hsRes.inrequest.enddate)==0)
        hsRes.result.errorcode<<12
    }
    if(hsRes.inrequest.kredtype!=3&&!hsRes.inrequest.payterm)
      hsRes.result.errorcode<<17
    if(hsRes.inrequest.kredtype==3&&hsRes.inrequest.kreditsort)
      hsRes.result.errorcode<<14
    if(hsRes.inrequest.kredtype==4&&!hsRes.inrequest.kredittransh)
      hsRes.result.errorcode<<18
    if(!hsRes.inrequest.modstatus&&!hsRes.inrequest.comment)
      hsRes.result.errorcode<<9
    if ((hsRes.kredit?Bank.findByIdIlike(hsRes.kredit.bank_id)?.is_foreign:Bank.findAllByNameOrId(hsRes.inrequest.bank,hsRes.inrequest.bank)?.is_foreign)==0){
      if(hsRes.inrequest.sschet&&!hsRes.inrequest.sschet.replace('.','').matches('\\d{20}'))
        hsRes.result.errorcode<<13
      if(hsRes.inrequest.percschet&&!hsRes.inrequest.percschet.replace('.','').matches('\\d{20}'))
        hsRes.result.errorcode<<19
      if(hsRes.inrequest.comschet&&!hsRes.inrequest.comschet.replace('.','').matches('\\d{20}'))
        hsRes.result.errorcode<<20
    }

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.kredit = new Kredit(client:Company.findByNameOrInn(hsRes.inrequest.client,hsRes.inrequest.client)?.id,bank_id:Bank.findByNameOrId(hsRes.inrequest.bank,hsRes.inrequest.bank).id).setMainAgrData(hsRes.inrequest)
        hsRes.result.kredit = hsRes.kredit.setData(hsRes.inrequest).csiSetAdmin(session.user.id).csiSetModstatus(hsRes.inrequest.modstatus).save(failOnError:true,flush:true)?.id?:0
        if (hsRes.kredit.is_tech) Agentagr.findAllByIdInListAndClient_idAndModstatus(Agentagrbank.findAllByBank_id(hsRes.kredit.bank_id).collect{it.agentagr_id},hsRes.kredit.client_id,1).each{
          if (!Agentkredit.findByAgentagr_idAndKredit_id(it.id,hsRes.kredit.id)) new Agentkredit(agentagr_id:it.id).csiSetKreditPeriod(Agentperiod.findByKredit_id(hsRes.kredit.id)).setData([:]).save(failOnError:true)
        }
        hsRes.result.debt = agentKreditService.computeKreditDebt(hsRes.kredit)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/updatekredit\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def kreditpercents = {
    checkAccess(7)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    render ([percents:Tools.toFixed(agentKreditService.computeKreditPercentByDate(requestService.getIntDef('id',0),requestService.getDate('percdate')?:new Date(),requestService.getDate('startdate')),2)] as JSON)
  }

  def kreditbalance = {
    checkAccess(7)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.kredit = Kredit.get(requestService.getIntDef('id',0))
    if (hsRes.kredit?.kredtype!=3) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payments = Payrequest.findAllByAgreementtype_idAndAgreement_idAndIs_dopAndPaytypeInListAndModstatusGreaterThanAndPaydateGreaterThanEquals(3,hsRes.kredit.id,0,[1,2],-1,hsRes.kredit.startsaldodate?:new Date(1,0,1),[sort:'paydate',order:'asc'])
    hsRes.balance = hsRes.kredit.startsaldodate?hsRes.kredit.startsumma:hsRes.kredit.summa
    hsRes.kredsumma = hsRes.kredit.summa

    return hsRes
  }

  def recalculateRubSummas = {
    checkAccess(7)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(AKREDPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.kredit = Kredit.get(requestService.getIntDef('id',0))
    if (!hsRes.kredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.kredit.recalculateRubSummas()
    } catch(Exception e) {
      log.debug("Error save data in Agreement/recalculateRubSummas\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def kreditline = {
    checkAccess(7)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.kredit = Kredit.get(lId)
    if (!hsRes.kredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.planpayments = Kreditline.findAllByKredit_idAndModstatusGreaterThanEquals(hsRes.kredit.id,0,[sort:'paydate',order:'asc'])
    hsRes.iscanedit = recieveSectionPermission(AKREDPAY)

    return hsRes
  }

  def kreditplanpayment = {
    checkAccess(7)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('kredit_id',0)
    hsRes.kredit = Kredit.get(lId)
    if (!hsRes.kredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.iscanedit = recieveSectionPermission(AKREDPAY)

    return hsRes
  }

  def addkreditplanpayment = {
    checkAccess(7)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(AKREDPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getLongDef('kredit_id',0)
    hsRes+=requestService.getParams(null,null,null,null,['summarub','summa'])
    hsRes.inrequest.planpayment_paydate = requestService.getDate('planpayment_paydate')

    hsRes.kredit = Kredit.get(lId)
    if (!hsRes.kredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.planpayment_paydate)
      hsRes.result.errorcode<<2
    if(!hsRes.kredit.isRateable()&&!hsRes.inrequest.summarub)
      hsRes.result.errorcode<<3

    if(!hsRes.result.errorcode){
      try {
        new Kreditline(kredit_id:hsRes.kredit.id).setData(hsRes.inrequest,hsRes.kredit.getvRate(),hsRes.kredit.isRateable()).csiSetModstatus(0).csiSetAdmin(session.user.id).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addkreditplanpayment\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletekreditplanpayment = {
    checkAccess(7)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(AKREDPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId = requestService.getLongDef('kredit_id',0)
    hsRes+=requestService.getParams(null,null,['id'])

    hsRes.kredit = Kredit.get(lId)
    if (!hsRes.kredit||!hsRes.inrequest.id) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Kreditline.get(hsRes.inrequest.id).csiSetModstatus(-1).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletekreditplanpayment\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def kreditpayments = {
    checkAccess(7)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.kredit = Kredit.get(lId)
    if (!hsRes.kredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payments = new KreditpaymentSearch().csiFindKreditPayment(hsRes.kredit.id)
    hsRes.kreditsum = hsRes.kredit.kredtype==1?hsRes.kredit.summa:Kreditline.findAllByKredit_idAndModstatusGreaterThanEquals(hsRes.kredit.id,0).sum{it.summa}?:0
    hsRes.iscanedit = recieveSectionPermission(AKREDPAY)
    hsRes.totalbody = 0.0g

    return hsRes
  }

  def kreditpayment = {
    checkAccess(7)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('kredit_id',0)
    hsRes+=requestService.getParams(null,null,['id'])
    hsRes.kredit = Kredit.get(lId)
    if (!hsRes.kredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.kreditpayment = Kreditpayment.get(hsRes.inrequest.id)
    hsRes.iscanedit = recieveSectionPermission(AKREDPAY)

    return hsRes
  }

  def addkreditpayment = {
    checkAccess(7)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(AKREDPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getLongDef('kredit_id',0)
    hsRes+=requestService.getParams(['id'],null,null,null,['summa','summaperc','summarub','summapercrub'])
    hsRes.inrequest.kreditpayment_paydate = requestService.getDate('kreditpayment_paydate')

    hsRes.kredit = Kredit.get(lId)
    hsRes.kreditpayment = Kreditpayment.get(hsRes.inrequest.id)
    if (!hsRes.kredit||(!hsRes.kreditpayment&&hsRes.inrequest.id)||hsRes.kreditpayment?.modstatus>1) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.summa&&!hsRes.inrequest.summaperc)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.kreditpayment_paydate)
      hsRes.result.errorcode<<2
    if(!hsRes.kredit.isRateable()&&!hsRes.inrequest.summarub&&!hsRes.inrequest.summapercrub)
      hsRes.result.errorcode<<3

    if(!hsRes.result.errorcode){
      try {
        if (!hsRes.inrequest.id) hsRes.kreditpayment = new Kreditpayment(kredit_id:hsRes.kredit.id,is_auto:0)
        hsRes.kreditpayment.setData(hsRes.inrequest,hsRes.kredit.getvRate(),hsRes.kredit.isRateable()).csiSetModstatus(0).csiSetAdmin(session.user.id).save(flush:true,failOnError:true)
        if (hsRes.kredit.repaymenttype_id<3&&hsRes.kredit.kredtype<3&&!Kreditpayment.findByKredit_idAndModstatusGreaterThan(hsRes.kredit.id,0)) {
          Kreditpayment.findAllByKredit_idAndPaydateGreaterThanAndModstatus(hsRes.kredit.id,hsRes.kreditpayment.paydate,0).each{ it.delete(flush:true) }
          hsRes.kredit.recomputepayments(hsRes.kreditpayment).each{
            if(it.basesumma+it.perssumma!=0) new Kreditpayment(kredit_id:hsRes.kredit.id).setComputedData(it).csiSetAdmin(session.user.id).save(failOnError:true)
          }
        }
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addkreditpayment\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletekreditpayment = {
    checkAccess(7)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(AKREDPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId = requestService.getLongDef('kredit_id',0)
    hsRes+=requestService.getParams(null,null,['id'])

    hsRes.kredit = Kredit.get(lId)
    if (!hsRes.kredit||!hsRes.inrequest.id) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Kreditpayment.get(hsRes.inrequest.id)?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletekreditpayment\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def generatekreditpayments = {
    checkAccess(7)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(AKREDPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId = requestService.getLongDef('id',0)

    hsRes.kredit = Kredit.get(lId)
    if (!hsRes.kredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      if (hsRes.kredit.repaymenttype_id<3&&hsRes.kredit.kredtype!=3&&!Kreditpayment.findByKredit_idAndModstatusGreaterThan(hsRes.kredit.id,0)) {
        Kreditpayment.findAllByKredit_id(hsRes.kredit.id).each{ it.delete(flush:true) }
        hsRes.kredit.computepayments().each{
          if(it.basesumma+it.perssumma!=0) new Kreditpayment(kredit_id:hsRes.kredit.id).setComputedData(it).csiSetAdmin(session.user.id).save(failOnError:true)
        }
      }
    } catch(Exception e) {
      log.debug("Error save data in Agreement/generatekreditpayments\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def deleteallkreditpayment = {
    checkAccess(7)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(AKREDPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.kredit = Kredit.get(requestService.getLongDef('id',0))
    if (!hsRes.kredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Kreditpayment.findAllByKredit_idAndModstatus(hsRes.kredit.id,0).each{ it.delete(failOnError:true) }
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deleteallkreditpayment\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def uploadkreditpayments = {
    checkAccess(7)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(AKREDPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId = requestService.getLongDef('id',0)

    hsRes.kredit = Kredit.get(lId)
    if (!hsRes.kredit) {
      response.sendError(404)
      return
    }

    hsRes.result = parseService.parseKreditpaymentsFile(request.getFile('file'))

    if(!hsRes.result.errorcode){
      try {
        if (hsRes.kredit.repaymenttype_id==3&&!Kreditpayment.findByKredit_idAndModstatusGreaterThan(hsRes.kredit.id,0)) {
          Kreditpayment.findAllByKredit_id(hsRes.kredit.id).each{ it.delete(flush:true) }
          hsRes.result.preparedData.each{
            if(it.summa+it.summaperc!=0) new Kreditpayment(kredit_id:hsRes.kredit.id).csiSetAdmin(session.user.id).setData(it,hsRes.kredit.getvRate(),true).save(failOnError:true)
          }
        }
      } catch(Exception e) {
        log.debug("Error save data in Agreement/uploadkreditpayments\n"+e.toString())
        hsRes.result.errorcode<<100
      }
    }

    return hsRes.result
  }

  def kroutpayrequests = {
    checkAccess(7)
    checkSectionAccess(3)
    if (!checkSectionPermission(AKREDPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.kredit = Kredit.get(requestService.getIntDef('id',0))
    if (!hsRes.kredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payrequests = new Payrequest().csiSelectKreditOutPayrequests(hsRes.kredit.id)

    return hsRes
  }

  def kroutpayrequest = {
    checkAccess(7)
    checkSectionAccess(3)
    if (!checkSectionPermission(AKREDPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agr = Kredit.get(requestService.getIntDef('agr_id',0))
    if (!hsRes.agr) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    hsRes.bank = new BankaccountSearch().csiFindAccounts(hsRes.payrequest?hsRes.payrequest.fromcompany_id:hsRes.agr.creditor?:hsRes.agr.client,1,1)
    hsRes.project = Project.findAllByModstatus(1,[sort:'name'])
    hsRes.defproject_id = Project.findByIs_main(1)?.id?:0
    hsRes.expensetype = new ExpensetypeSearch().csiGetList()
    hsRes.defaultdata = new KreditpaymentSearch().csiFindDefaultDataForPayment(hsRes.agr.id)
    hsRes.is_body = hsRes.defaultdata?.summarub>0&&(hsRes.defaultdata?.modstatus==0||(hsRes.defaultdata?.modstatus==1&&hsRes.defaultdata?.paidstatus!=2))
    hsRes.iscantag = recieveSectionPermission(APTAG)

    return hsRes
  }

  def addkroutpayrequest = {
    checkAccess(7)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(AKREDPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['id','is_dop','is_fine','frombank','project_id','expensetype_id','is_task'],null,['destination'],null,['summa'])
    hsRes.inrequest.payrequest_paydate = requestService.getDate('payrequest_paydate')

    hsRes.agr = Kredit.get(requestService.getIntDef('agr_id',0))
    hsRes.payrequest = Payrequest.get(hsRes.inrequest.id)
    if (!hsRes.agr||(!hsRes.payrequest&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(hsRes.inrequest.summa<=0)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.payrequest_paydate)
      hsRes.result.errorcode<<3
    if(recieveSectionPermission(APTAG)&&!hsRes.inrequest.expensetype_id)
      hsRes.result.errorcode<<4

    if(!hsRes.result.errorcode){
      if(!hsRes.inrequest.destination) hsRes.inrequest.destination = hsRes.inrequest.is_fine?'Оплата пени':hsRes.inrequest.is_dop?'Оплата процентов по договору':'Оплата тела кредита по договору'
      def agrpayment_id = 0
      if (!hsRes.inrequest.is_fine){
        if (hsRes.inrequest.is_dop) agrpayment_id = Kreditpayment.findByKredit_idAndPaydateAndModstatusInListAndSummapercrubBetween(hsRes.agr.id,hsRes.inrequest.payrequest_paydate,[0,1],hsRes.inrequest.summa.toLong(),hsRes.inrequest.summa.toLong()+1)?.id?:0
        else agrpayment_id = Kreditpayment.findByKredit_idAndPaydateAndModstatusInListAndSummarubBetween(hsRes.agr.id,hsRes.inrequest.payrequest_paydate,[0,1],hsRes.inrequest.summa.toLong(),hsRes.inrequest.summa.toLong()+1)?.id?:0
      }
      try {
        if (!hsRes.inrequest.id) hsRes.payrequest = new Payrequest(paytype:1,paycat:1,agreementtype_id:3,agreement_id:hsRes.agr.id,agrpayment_id:agrpayment_id).csiSetKreditAgrData(hsRes.agr).csiSetInitiator(hsRes.user.id)
        hsRes.payrequest.setGeneralData(hsRes.inrequest+[is_nds:1]).csiSetPayrequestTag(hsRes.inrequest,recieveSectionPermission(APTAG),hsRes.user.id).csiSetBankaccount_id(hsRes.inrequest.frombank).save(failOnError:true)
        if (hsRes.inrequest.is_task&&hsRes.payrequest.taskpay_id==0){
          def taskpay_id = new Taskpay(paygroup:hsRes.payrequest.paygroup,is_manual:1).csiSetTaskpay(term:String.format('%td.%<tm.%<tY',hsRes.payrequest.paydate),company_id:hsRes.payrequest.fromcompany_id,summa:hsRes.payrequest.summa).csiSetInitiator(hsRes.user.id).csiSetBankaccountId(hsRes.payrequest.bankaccount_id).save(flush:true,failOnError:true)?.id?:0
          hsRes.payrequest.csiSetTaskpay_id(taskpay_id).csiSetModstatus(1).save(flush:true,failOnError:true)
        }
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addkroutpayrequest\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletekroutpayrequest = {
    checkAccess(7)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(AKREDEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.kredit = Kredit.get(requestService.getLongDef('kredit_id',0))
    hsRes.payrequest = Payrequest.findByAgreementtype_idAndAgreement_idAndId(3,hsRes.kredit?.id?:-1,requestService.getLongDef('id',0))
    if (!hsRes.kredit||hsRes.payrequest?.modstatus!=0) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletekroutpayrequest\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def krinpayrequests = {
    checkAccess(7)
    checkSectionAccess(3)
    if (!checkSectionPermission(AKREDPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.kredit = Kredit.get(requestService.getIntDef('id',0))
    if (!hsRes.kredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payrequests = Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeAndModstatusGreaterThanAndIs_dop(3,hsRes.kredit.id,2,-1,0,[sort:'paydate',order:'desc'])

    return hsRes
  }

  def kreditpercpayments = {
    checkAccess(7)
    checkSectionAccess(3)
    if (!checkSectionPermission(AKREDPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.kredit = Kredit.get(requestService.getIntDef('id',0))
    if (hsRes.kredit?.kredtype!=3) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payrequests = Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeAndIs_dopAndModstatusGreaterThan(3,hsRes.kredit.id,1,1,-1,[sort:'paydate',order:'desc'])

    return hsRes
  }

  def kreditdopagrs = {
    checkAccess(7)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.kredit = Kredit.get(requestService.getIntDef('id',0))
    if (!hsRes.kredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.kreditdopagrs = Kreditdopagr.findAllByKredit_id(hsRes.kredit.id,[sort:'id',order:'desc'])
    hsRes.firstagrid = Kreditdopagr.getMinId(hsRes.kredit.id)
    hsRes.iscanedit = recieveSectionPermission(AKREDEDIT)

    return hsRes
  }

  def kreditdopagr = {
    checkAccess(7)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.kredit = Kredit.get(requestService.getIntDef('kredit_id',0))
    if (!hsRes.kredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,['id'])

    hsRes.kreditdopagr = Kreditdopagr.get(hsRes.inrequest.id)
    hsRes.iscanedit = recieveSectionPermission(AKREDEDIT)

    return hsRes
  }

  def addkreditdopagr = {
    checkAccess(7)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(AKREDEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]
 
    hsRes+=requestService.getParams(['id','is_prolong','is_zalog'],null,['nomer','comment'],null,['summa','rate'])
    hsRes.inrequest.dsdate = requestService.getDate('kreditdopagr_dsdate')
    hsRes.inrequest.startdate = requestService.getDate('kreditdopagr_startdate')
    hsRes.inrequest.enddate = requestService.getDate('kreditdopagr_enddate')

    hsRes.kredit = Kredit.get(requestService.getLongDef('kredit_id',0))
    hsRes.kreditdopagr = Kreditdopagr.get(hsRes.inrequest.id)
    if (!hsRes.kredit||(!hsRes.kreditdopagr&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.nomer)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.rate)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.dsdate)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.startdate)
      hsRes.result.errorcode<<5
    if(!hsRes.inrequest.enddate)
      hsRes.result.errorcode<<6
    else if(hsRes.inrequest.startdate&&Tools.computeMonthDiff(hsRes.inrequest.startdate,hsRes.inrequest.enddate)==0)
      hsRes.result.errorcode<<7

    if(!hsRes.result.errorcode){
      try {
        if (!hsRes.inrequest.id) hsRes.kreditdopagr = new Kreditdopagr(kredit_id:hsRes.kredit.id)
        hsRes.kreditdopagr.setData(hsRes.inrequest).csiSetAdmin(session.user.id).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addkreditdopagr\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletekreditdopagr = {
    checkAccess(7)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(AKREDEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.kredit = Kredit.get(requestService.getLongDef('kredit_id',0))
    if (!hsRes.kredit||Kreditdopagr.getMinId(hsRes.kredit?.id)==requestService.getLongDef('id',0)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Kreditdopagr.findByKredit_idAndId(hsRes.kredit.id,requestService.getLongDef('id',0))?.csiSetAdmin(session.user.id)?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletekreditdopagr\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def kreditzalogagrs = {
    checkAccess(7)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.kredit = Kredit.get(requestService.getIntDef('id',0))
    if (!hsRes.kredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.kreditzalogagrs = Kreditzalog.findAllByKredit_id(hsRes.kredit.id,[sort:'id',order:'desc'])
    hsRes.zalogtypes = Zalogtype.list().inject([:]){map, zalogtype -> map[zalogtype.id]=zalogtype.name;map}
    hsRes.iscanedit = recieveSectionPermission(AKREDEDIT)

    return hsRes
  }

  def kreditzalogagr = {
    checkAccess(7)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.kredit = Kredit.get(requestService.getIntDef('kredit_id',0))
    if (!hsRes.kredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,['id'])

    hsRes.kreditzalogagr = Kreditzalog.get(hsRes.inrequest.id)
    hsRes.client = Company.get(hsRes.kredit.client)?.name
    hsRes.wh1 = Space.findAllBySpacetype_idAndArendator(2,hsRes.kredit.client)
    hsRes.wh2 = Space.findAllBySpacetype_idAndArendatorAndIdNotEqual(2,hsRes.kredit.client,hsRes.kreditzalogagr?.space1?:0)
    hsRes.iscanedit = recieveSectionPermission(AKREDEDIT)

    return hsRes
  }

  def kreditzalogspacelist={
    checkAccess(7)
    checkSectionAccess(3)
    requestService.init(this)

    return [wh2:Space.findAllBySpacetype_idAndArendatorAndIdNotEqual(2,requestService.getIntDef('id',0),requestService.getIntDef('space',0))]
  }

  def addkreditzalogagr = {
    checkAccess(7)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(AKREDEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]
 
    hsRes+=requestService.getParams(['id','zalogtype_id','space1','space2','is_zalogagr'],['strakhsumma'],
                                    ['zalogagr','zalogprim','strakhnumber','pledger'],null,['zalogcost','marketcost'])
    hsRes.inrequest.zalogstart = requestService.getDate('kreditzalogagr_zalogstart')
    hsRes.inrequest.zalogend = requestService.getDate('kreditzalogagr_zalogend')
    hsRes.inrequest.strakhdate = requestService.getDate('kreditzalogagr_strakhdate')
    hsRes.inrequest.strakhvalidity = requestService.getDate('kreditzalogagr_strakhvalidity')

    hsRes.kredit = Kredit.get(requestService.getLongDef('kredit_id',0))
    hsRes.kreditzalogagr = Kreditzalog.get(hsRes.inrequest.id)
    if (!hsRes.kredit||(!hsRes.kreditzalogagr&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.pledger)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.zalogtype_id)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.zalogstart)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.zalogend)
      hsRes.result.errorcode<<4

    if(!hsRes.result.errorcode){
      try {
        if (!hsRes.inrequest.id) hsRes.kreditzalogagr = new Kreditzalog(kredit_id:hsRes.kredit.id)
        hsRes.kreditzalogagr.setData(hsRes.inrequest).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addkreditzalogagr\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def adddopzalog = {
    checkAccess(7)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(AKREDEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.kreditzalogagr = Kreditzalog.get(requestService.getLongDef('id',0))
    if (!hsRes.kreditzalogagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      new Kreditzalog(kredit_id:hsRes.kreditzalogagr.kredit_id,parent:hsRes.kreditzalogagr.id).cloneData(hsRes.kreditzalogagr).save(failOnError:true)?.id?:0
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletekreditzalogagr\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def deletekreditzalogagr = {
    checkAccess(7)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(AKREDEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.kredit = Kredit.get(requestService.getLongDef('kredit_id',0))
    if (!hsRes.kredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Kreditzalog.findByKredit_idAndId(hsRes.kredit.id,requestService.getLongDef('id',0))?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletekreditzalogagr\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def kredithistory = {
    checkAccess(7)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.kredit = Kredit.get(lId)
    if (!hsRes.kredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.history = new KredithistSearch().csiFindKreditHistory(hsRes.kredit.id)

    return hsRes
  }

  def kreditpayrequests = {
    checkAccess(7)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.iscanedit = recieveSectionPermission(AKREDPAY)

    return hsRes
  }

  def kreditpayrequestslist = {
    checkAccess(7)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes+=requestService.getParams(['modstatus','kredsort'],null,['client_name'])
    hsRes.inrequest.startdate = requestService.getDate('startdate')
    hsRes.inrequest.enddate = requestService.getDate('enddate')

    hsRes.searchresult = new KreditpaymentSearch().csiSelectKreditsPayments(hsRes.inrequest.client_name?:'',hsRes.inrequest.modstatus?:0,
                                            hsRes.inrequest.startdate,hsRes.inrequest.enddate,hsRes.inrequest.kredsort?:0,20,requestService.getOffset())

    return hsRes
  }

  def createkrpayrequests={
    checkAccess(7)
    checkSectionAccess(3)
    if (!checkSectionPermission(AKREDPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def bodyIds = requestService.getIds('body')
    def percIds = requestService.getIds('perc')

    if (!bodyIds&&!percIds) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    try {
      bodyIds?.each{ Kreditpayment.get(it)?.generateBodyPayrequest(hsRes.user.id).save(failOnError:true) }
      percIds?.each{ Kreditpayment.get(it)?.generatePercPayrequest(hsRes.user.id).save(failOnError:true) }
    } catch(Exception e) {
      log.debug("Error save data in Agreement/createkrpayrequests\n"+e.toString());
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Kredit <<<////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Cession >>>///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def cessionfilter = {
    checkAccess(7)
    checkSectionAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails){
      hsRes.inrequest = session.agrlastRequest
    }

    hsRes.banks = Kredit.getCessionBanks()
    hsRes.iscanedit = recieveSectionPermission(ACESEDIT)

    return hsRes
  }

  def cessions = {
    checkAccess(7)
    checkSectionAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.agrlastRequest
      session.agrlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['valuta_id','agr_id','changetype'],null,['inn','company_name','bank_id'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.offset = requestService.getOffset()
      session.agrlastRequest = hsRes.inrequest
    }
    session.agrlastRequest.agrobject = 6

    hsRes.searchresult = new CessionSearch().csiSelectCessions(hsRes.inrequest.inn?:'',hsRes.inrequest.company_name?:'',
                                                               hsRes.inrequest.bank_id?:'',hsRes.inrequest.valuta_id?:0,
                                                               hsRes.inrequest.agr_id?:0,hsRes.inrequest.changetype?:0,
                                                               hsRes.inrequest.modstatus?:0,session.user.group.visualgroup_id,
                                                               20,hsRes.inrequest.offset)
    hsRes.valutacodes = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code;map}

    return hsRes
  }

  def cession = {
    checkAccess(7)
    checkSectionAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.cession = Cession.get(lId)
    if (!hsRes.cession&&lId) {
      response.sendError(404)
      return
    }

    hsRes.kredits = (hsRes.cession?.cessionvariant==1?new KreditSearch().csiSelectKredit(hsRes.cession.agr_id):[])+(new KreditSearch().csiSelectNewCessionKredits())
    hsRes.lizings = (hsRes.cession?.cessionvariant==2?new LizingSearch().csiSelectLizing(hsRes.cession.agr_id):[])+(new LizingSearch().csiSelectNewCessionLizings())
    hsRes.cedent = hsRes.cession?.cessionvariant==1?Bank.get(hsRes.cession?.cedent):Company.get(hsRes.cession?.cedentcompany)?.name
    hsRes.cessionary = Company.get(hsRes.cession?.cessionary)?.name
    hsRes.debtor = Company.get(hsRes.cession?.debtor)?.name
    hsRes.cbanks = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(hsRes.cession?.cessionary?:0,1,1).collect{Bank.get(it.bank_id)}
    hsRes.users = new UserpersSearch().csiFindByAccessrigth(ACESEDIT)
    hsRes.clients = Client.findAllByModstatusAndIdNotEqual(1,hsRes.cession?.client_id?:0)+[Client.get(hsRes.cession?.client_id?:0)]-null
    hsRes.iscanedit = recieveSectionPermission(ACESEDIT)
    hsRes.iscanpay = recieveSectionPermission(ACESPAY)

    return hsRes
  }

  def cessbanklist={
    checkAccess(7)
    checkSectionAccess(6)
    requestService.init(this)

    return [banks:Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(Company.findByNameOrInn(requestService.getStr('companyname'),requestService.getStr('companyname'))?.id,1,1).collect{Bank.get(it.bank_id)}]
  }

  def kreditdata = {
    checkAccess(7)
    checkSectionAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def oKredit = Kredit.get(requestService.getIntDef('kredit_id',0))
    if(!oKredit) render(contentType:"application/json"){[error:true]}
    else render ([cedent:Bank.get(oKredit.bank_id).toString(),debtor:Company.get(oKredit.client).name,client_id:oKredit.curclient_id] as JSON)
  }

  def lizingdata = {
    checkAccess(7)
    checkSectionAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def oLizing = Lizing.get(requestService.getIntDef('lizing_id',0))
    if(!oLizing) render(contentType:"application/json"){[error:true]}
    else render ([cedent:Company.get(oLizing.arendodatel).name,debtor:Company.get(oLizing.arendator).name] as JSON)
  }

  def updatecession = {
    checkAccess(7)
    if (!checkSectionAccess(6)) return
    if (!checkSectionPermission(ACESEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    def lId = requestService.getLongDef('id',0)
    hsRes.cession = Cession.get(lId)
    if (!hsRes.cession&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['kredit_id','cessiontype','valuta_id','is_dirsalary','is_debtfull','cessionvariant',
                                     'lizing_id','client_id'],['responsible'],['cessionary','anumber','dopagrcomment',
                                     'description','comment','procdebtperiod','cbank_id'],null,['maindebt','procdebt'])
    hsRes.inrequest.adate = requestService.getDate('adate')
    hsRes.inrequest.enddate = requestService.getDate('enddate')
    if(hsRes.cession) hsRes.inrequest.cessionvariant = hsRes.cession.cessionvariant

    if(!hsRes.inrequest.cessionary)
      hsRes.result.errorcode<<1
    else if(!Company.findByNameOrInn(hsRes.inrequest.cessionary,hsRes.inrequest.cessionary))
      hsRes.result.errorcode<<2
    else if(Company.findAllByNameOrInn(hsRes.inrequest.cessionary,hsRes.inrequest.cessionary).size()>1)
      hsRes.result.errorcode<<3
    else if (hsRes.inrequest.cessionvariant==1){
      if(!hsRes.inrequest.kredit_id)
        hsRes.result.errorcode<<4
      else if(!Kredit.get(hsRes.inrequest.kredit_id))
        hsRes.result.errorcode<<5
      else if (Company.findByNameOrInn(hsRes.inrequest.cessionary,hsRes.inrequest.cessionary).is_holding==0&&Company.get(Kredit.get(hsRes.inrequest.kredit_id).client).is_holding==0)
        hsRes.result.errorcode<<12
    } else if (hsRes.inrequest.cessionvariant==2) {
      if(!hsRes.inrequest.lizing_id)
        hsRes.result.errorcode<<14
      else if(!Lizing.get(hsRes.inrequest.lizing_id))
        hsRes.result.errorcode<<15
      else if (Company.findByNameOrInn(hsRes.inrequest.cessionary,hsRes.inrequest.cessionary).is_holding==0&&Company.get(Lizing.get(hsRes.inrequest.lizing_id).arendator).is_holding==0)
        hsRes.result.errorcode<<12
    }
    if(!hsRes.inrequest.cessiontype)
      hsRes.result.errorcode<<6
    if(!hsRes.inrequest.anumber)
      hsRes.result.errorcode<<7
    if(!hsRes.inrequest.adate)
      hsRes.result.errorcode<<8
    if(!hsRes.inrequest.enddate)
      hsRes.result.errorcode<<9
    if(hsRes.inrequest.enddate<hsRes.inrequest.adate)
      hsRes.result.errorcode<<10
    if(hsRes.cession&&hsRes.cession.enddate!=hsRes.inrequest.enddate&&!hsRes.inrequest.dopagrcomment)
      hsRes.result.errorcode<<11
    if(!hsRes.inrequest.cbank_id)
      hsRes.result.errorcode<<13

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.cession = new Cession(cessionvariant:hsRes.inrequest.cessionvariant).csiSetClient_id(hsRes.inrequest.client_id)
        hsRes.result.cession = hsRes.cession.setData(hsRes.inrequest).csiSetAdmin(session.user.id).csiSetDopComment(hsRes.inrequest.dopagrcomment).csiSetDirSalary(hsRes.inrequest.is_dirsalary,session.user.group.is_rep_dirsalary).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Agreement/updatecession\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def cessionline = {
    checkAccess(7)
    checkSectionAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.cession = Cession.get(lId)
    if (!hsRes.cession) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.planpayments = Cessionline.findAllByCession_idAndModstatusGreaterThanEquals(hsRes.cession.id,0)
    hsRes.iscanedit = recieveSectionPermission(ACESPAY)

    return hsRes
  }

  def cessionplanpayment = {
    checkAccess(7)
    checkSectionAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('cession_id',0)
    hsRes.cession = Cession.get(lId)
    if (!hsRes.cession) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.iscanedit = recieveSectionPermission(ACESPAY)

    return hsRes
  }

  def addcessionplanpayment = {
    checkAccess(7)
    if (!checkSectionAccess(6)) return
    if (!checkSectionPermission(ACESPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getLongDef('cession_id',0)
    hsRes+=requestService.getParams(null,null,null,null,['summa'])
    hsRes.inrequest.planpayment_paydate = requestService.getDate('planpayment_paydate')

    hsRes.cession = Cession.get(lId)
    if (!hsRes.cession) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.planpayment_paydate)
      hsRes.result.errorcode<<2

    if(!hsRes.result.errorcode){
      try {
        new Cessionline(cession_id:hsRes.cession.id).setData(hsRes.inrequest).csiSetModstatus(0).csiSetAdmin(session.user.id).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addcessionplanpayment\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletecessionplanpayment = {
    checkAccess(7)
    if (!checkSectionAccess(6)) return
    if (!checkSectionPermission(ACESPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId = requestService.getLongDef('cession_id',0)
    hsRes+=requestService.getParams(null,null,['id'])

    hsRes.cession = Cession.get(lId)
    if (!hsRes.cession||!hsRes.inrequest.id) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Cessionline.get(hsRes.inrequest.id).csiSetModstatus(-1).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletecessionplanpayment\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def cespayments = {
    checkAccess(7)
    checkSectionAccess(6)
    if (!checkSectionPermission(ACESPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.cession = Cession.get(lId)
    if (!hsRes.cession) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payments = Payment.findAllByAgreementtype_idAndAgreement_id(6,hsRes.cession.id,[sort:'paydate',order:'desc'])

    return hsRes
  }

  def cessionhistory = {
    checkAccess(7)
    checkSectionAccess(6)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.cession = Cession.get(lId)
    if (!hsRes.cession) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.history = new CessionhistSearch().csiFindCessionHistory(hsRes.cession.id)

    return hsRes
  }

  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Cession <<<///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Agent >>>/////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def agentfilter = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails){
      hsRes.inrequest = session.agrlastRequest
    }
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)

    return hsRes
  }


  def agentagrs = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.agrlastRequest
      session.agrlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['client_id'],null,['anumber','bankname'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.offset = requestService.getOffset()
      session.agrlastRequest = hsRes.inrequest
    }
    session.agrlastRequest.agrobject = 5

    hsRes.searchresult = new AgentagrSearch().csiSelectAgentagrs(0,hsRes.inrequest.client_id?:0,hsRes.inrequest.bankname?:'',
                                                                 hsRes.inrequest.anumber?:'',hsRes.inrequest.modstatus?:0,20,hsRes.inrequest.offset)
    hsRes.banks = hsRes.searchresult.records.inject([:]){map, agentagr -> map[agentagr.id]=Agentagrbank.findAllByAgentagr_id(agentagr.id).collect{Bank.get(it.bank_id)};map}
    hsRes.clients = Client.list().inject([:]){map, client -> map[client.id]=client.name;map}
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)

    return hsRes
  }

  def agent = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.agentagr = Agentagr.get(lId)
    if (!hsRes.agentagr&&lId) {
      response.sendError(404)
      return
    }

    hsRes.clients = Client.findAllByModstatusAndIdNotEqual(1,hsRes.agentagr?.client_id?:0)+[Client.get(hsRes.agentagr?.client_id?:0)]-null
    hsRes.banks = Kredit.findAllByClient_id(hsRes.agentagr?.client_id?:-1).collect{it.bank_id}.unique().collect{Bank.get(it)}
    hsRes.saldo = (Client.get(hsRes.agentagr?.client_id)?.computeCurSaldo()?:0.0g)+(Payrequest.findAllByClient_idAndPaytypeNotEqualAndModstatusGreaterThan(hsRes.agentagr?.client_id,3,-1).sum{ it.computeClientdelta() }?:0.0g)
    hsRes.nds = Tools.getIntVal(ConfigurationHolder.config.payment.nds,18)
    hsRes.mainclient = Client.get(Client.get(hsRes.agentagr?.client_id?:0)?.parent?:0)
    hsRes.addbankslist = Agentagrbank.findAllByAgentagr_idAndIs_main(hsRes.agentagr?.id?:0,0)
    hsRes.addbankskredits = hsRes.addbankslist.inject([:]){map, bank -> map[bank.id]=Agentkredit.findAllByAgentagr_idAndKredit_idInList(bank.agentagr_id,Kredit.findAllByBank_id(bank.bank_id).collect{it.id});map}
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)
    hsRes.isHaveCredit = Agentkredit.findByAgentagr_id(hsRes.agentagr?.id?:0) as Boolean

    return hsRes
  }

  def agentbanklist={
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)

    render(view: "spacebanklist", model: [notauto:true,banks:Kredit.findAllByClient_idOrCurclient_id(requestService.getIntDef('client_id',0)?:-1,requestService.getIntDef('client_id',0)?:-1).collect{it.bank_id}.unique().collect{Bank.get(it)}])
    return
  }

  def agentaddbanktemplate = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.banknumber = requestService.getIntDef('banknumber',0)
    hsRes.agentagr = Agentagr.get(requestService.getIntDef('agr_id',0))
    hsRes.banks = Kredit.findAllByClient_idAndBank_idNotInList(hsRes.agentagr?.client_id?:-1,Agentagrbank.findAllByAgentagr_id(hsRes.agentagr?.id?:0).collect{it.bank_id}?:['']).collect{it.bank_id}.unique().collect{Bank.get(it)}
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)

    return hsRes
  }

  def deleteaddbank = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def oAddbank = Agentagrbank.get(requestService.getLongDef('id',0))
    if(!oAddbank||Agentkredit.findAllByAgentagr_idAndKredit_idInList(oAddbank?.agentagr_id?:0,Kredit.findAllByBank_id(oAddbank?.bank_id?:'').collect{it.id})){
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      oAddbank.delete(flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deleteaddbank\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def deleteagentagr = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    if(!hsRes.agentagr||Agentkredit.findByAgentagr_id(hsRes.agentagr.id)){
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.agentagr.delete(flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deleteagentagr\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def updateagent = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.agentagr = Agentagr.get(lId)
    if (!hsRes.agentagr&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['client_id','newaddbanknumber'],null,['name','bank_id'])
    if(hsRes.inrequest.newaddbanknumber){
      for(int i=1;i<=hsRes.inrequest.newaddbanknumber;++i){
        hsRes.inrequest+=requestService.getParams(null,null,["addbank_id_new$i"]).inrequest
      }
    }
    def isHaveCredit = Agentkredit.findByAgentagr_id(hsRes.agentagr?.id?:0) as Boolean
    if(!hsRes.inrequest.name)
      hsRes.result.errorcode<<1
    if(!isHaveCredit&&!hsRes.inrequest.client_id)
      hsRes.result.errorcode<<2
    if(!isHaveCredit&&!hsRes.inrequest.bank_id)
      hsRes.result.errorcode<<3

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.agentagr = new Agentagr()
        hsRes.result.agentagr = hsRes.agentagr.setData(hsRes.inrequest).save(failOnError:true)?.id?:0
        if (!isHaveCredit){
          Agentagrbank.findOrCreateByAgentagr_idAndIs_main(hsRes.agentagr.id,1).updateBank(hsRes.inrequest.bank_id).save(failOnError:true,flush:true)
          Agentperiod.findAllByKredit_idInListAndClient_id(Kredit.findAllByModstatusAndBank_idAndIs_tech(1,hsRes.agentagr.bank_id,1).collect{it.id}?:[0],hsRes.agentagr.client_id).each{
            if (!Agentkredit.findByAgentagr_idAndAgentperiod_id(hsRes.agentagr.id,it.id)) new Agentkredit(agentagr_id:hsRes.agentagr.id).csiSetKreditPeriod(it).setData(hsRes.inrequest).save(failOnError:true)
          }
        }
        if(hsRes.inrequest.newaddbanknumber){
          for(int i=1;i<=hsRes.inrequest.newaddbanknumber;++i){
            if(hsRes.inrequest."addbank_id_new$i"&&!Agentagrbank.findByAgentagr_idAndBank_id(hsRes.agentagr.id,hsRes.inrequest."addbank_id_new$i"))
              new Agentagrbank(agentagr_id:hsRes.agentagr.id,bank_id:hsRes.inrequest."addbank_id_new$i").save(failOnError:true,flush:true)
          }
        }
      } catch(Exception e) {
        log.debug("Error save data in Agreement/updateagent\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def agentagrbalance = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    if (!hsRes.agentagr) {
      response.sendError(404)
      return
    }

    hsRes.client = Client.get(hsRes.agentagr.client_id)
    hsRes.banks = Agentagrbank.findAllByAgentagr_id(hsRes.agentagr?.id?:0,[sort:'is_main',order:'desc']).collect{Bank.get(it.bank_id)}
    hsRes.acts = Actclient.findAllByAgentagr_id(hsRes.agentagr.id,[sort:'inputdate',order:'asc'])
    hsRes.paysummas = hsRes.acts.inject([:]){map, act -> map[act.id] = [payrequests:Payrequest.findAll{ modstatus >=2 && paytype == 2 && agentagr_id == hsRes.agentagr.id && agent_id == 0 && month(paydate) == act.month && year(paydate) == act.year },cash:Cash.findAll{ agentagr_id == hsRes.agentagr.id && agent_id == 0 && type == 2 && month(operationdate) == act.month && year(operationdate) == act.year },agentfix:Agentfix.findAll{ agentagr_id == hsRes.agentagr.id && month(paydate) == act.month && year(paydate) == act.year }];map}
    hsRes.totalbalance = 0.0g

    renderPdf(template: 'agentagrbalance', model: hsRes, filename: "agentagrbalance.pdf")
    return
  }

  def agentkredits = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.agentagr = Agentagr.get(lId)
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.agentkredits = new AgentkreditSearch().csiSelectKredits(hsRes.agentagr.id)
    hsRes.valutas = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code.toLowerCase();map}
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)

    return hsRes
  }

  def agentkredit = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('agentagr_id',0)
    hsRes.agentagr = Agentagr.get(lId)
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.agentkredit = Agentkredit.get(requestService.getIntDef('id',0))
    hsRes.agentrates = Agentrate.findAllByAgentkredit_idAndIs_sub(hsRes.agentkredit?.id?:0,0)
    hsRes.agentacts = hsRes.agentrates.inject([:]){map, agentrate -> map[agentrate.id]=Actagent.findAllByAgentagr_idAndAgent_idAndIs_report(hsRes.agentagr.id,agentrate.agent_id,0);map}
    hsRes.subagentrates = Agentrate.findAllByAgentkredit_idAndIs_sub(hsRes.agentkredit?.id?:0,1)
    hsRes.subagentacts = hsRes.subagentrates.inject([:]){map, agentrate -> map[agentrate.id]=Actagent.findAllByAgentagr_idAndAgent_idAndIs_report(hsRes.agentagr.id,agentrate.agent_id,0);map}
    hsRes.agents = Agent.list()
    hsRes.kredits = hsRes.agentkredit?[Kredit.get(hsRes.agentkredit.kredit_id)]:Kredit.findAllByModstatusAndIdNotInList(1,Agentkredit.findAllByAgentagr_id(hsRes.agentagr.id).collect{it.kredit_id}?:[0])
    if (hsRes.agentkredit) hsRes.bank = Bank.get(Kredit.get(hsRes.agentkredit.kredit_id)?.bank_id?:'')
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)
    hsRes.isHaveSubAgents = Agentrate.findByAgentkredit_idAndIs_sub(hsRes.agentkredit?.id?:0,1)?true:false
    hsRes.isHavePeriods = Agentkreditplan.findByAgentkredit_id(hsRes.agentkredit?.id?:0)?true:false

    return hsRes
  }

  def bankkredit = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('agentagr_id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['client'])

    hsRes.agentagrbanks = Agentagrbank.findAllByAgentagr_id(hsRes.agentagr.id).collect{it.bank_id}
    hsRes.clients = Kredit.getClientsBank(hsRes.agentagrbanks,hsRes.agentagr.client_id)
    hsRes.client = Company.get(hsRes.inrequest.client?:0)
    hsRes.kredits = Agentperiod.findAllByKredit_idInListAndClient_id(Kredit.findAllByBank_idInListAndClientAndModstatusAndIs_tech(hsRes.agentagrbanks,hsRes.client?.id?:0,1,1).collect{it.id}?:[0],hsRes.agentagr.client_id)
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)

    return hsRes
  }

  def agenttemplate = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agnumber = requestService.getIntDef('agnumber',0)
    hsRes.agents = Agent.list()
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)

    return hsRes
  }

  def subagenttemplate = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agnumber = requestService.getIntDef('agnumber',0)
    hsRes.agents = Agent.list()
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)

    return hsRes
  }

  def addagentkredit = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[],newagenterrorcode:[]]

    def lId = requestService.getLongDef('id',0)
    hsRes.agentkredit = Agentkredit.get(lId)
    if ((!hsRes.agentkredit&&lId)||Agentkreditplan.findByAgentkredit_id(hsRes.agentkredit?.id?:0)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['agentkredit_kredit_id','payterm','agentagr_id','newagentnumber','is_bankkredit','bankkredit_client','calcperiod'],
                                    null,null,null,['rate','cost'])
    if(hsRes.inrequest.newagentnumber){
      for(int i=1;i<=hsRes.inrequest.newagentnumber;++i){
        hsRes.inrequest+=requestService.getParams(["agentrates_agent_id_new$i","subtype_new$i","is_sub_new$i","is_display_new$i"],null,null,null,["rate_new$i"]).inrequest
      }
    }
    hsRes.agentrates = Agentrate.findAllByAgentkredit_id(hsRes.agentkredit?.id?:0).each{
      hsRes.inrequest+=requestService.getParams(["agentrates_agent_id_$it.id","subtype_$it.id","is_display_$it.id"],null,null,null,["rate_$it.id"]).inrequest
    }
    hsRes.agentagr = Agentagr.get(hsRes.inrequest.agentagr_id?:0)

    if(!hsRes.agentkredit&&!hsRes.inrequest.agentkredit_kredit_id&&!hsRes.inrequest.is_bankkredit)
      hsRes.result.errorcode<<1
    if(!hsRes.agentagr)
      hsRes.result.errorcode<<2
    if(hsRes.inrequest.newagentnumber){
      for(int i=1;i<=hsRes.inrequest.newagentnumber;++i){
        if (Agentrate.findByAgentkredit_idAndAgent_id(hsRes.agentkredit?.id?:0,hsRes.inrequest."agentrates_agent_id_new$i"?:0))
          hsRes.result.newagenterrorcode<<i
      }
    }

    if(!hsRes.result.errorcode&&!hsRes.result.newagenterrorcode){
      try {
        if(!hsRes.inrequest.is_bankkredit||hsRes.inrequest.agentkredit_kredit_id){
          if(!lId) hsRes.agentkredit = new Agentkredit(agentagr_id:hsRes.inrequest.agentagr_id).csiSetKreditPeriod(Agentperiod.get(hsRes.inrequest.agentkredit_kredit_id))
          hsRes.agentkredit.setData(hsRes.inrequest).save(failOnError:true)
          if(hsRes.inrequest.newagentnumber){
            for(int i=1;i<=hsRes.inrequest.newagentnumber;++i){
              if(hsRes.inrequest."agentrates_agent_id_new$i"&&!(hsRes.inrequest."is_sub_new$i"&&Agentrate.findByAgentkredit_idAndIs_sub(hsRes.agentkredit.id,1)))
                new Agentrate(agentkredit_id:hsRes.agentkredit.id,agent_id:hsRes.inrequest."agentrates_agent_id_new$i",rate:(hsRes.inrequest."rate_new$i"?hsRes.inrequest."rate_new$i".toFloat():0f),is_sub:hsRes.inrequest."is_sub_new$i"?:0,subtype:hsRes.inrequest."subtype_new$i"?:0,is_display:hsRes.inrequest."is_display_new$i"?:0).save(failOnError:true)
            }
          }
          hsRes.agentrates.each{
            it.setData(hsRes.inrequest).save(failOnError:true)
          }
        } else {
          def agentperiodlist = Agentperiod.findAllByKredit_idInListAndClient_id((hsRes.inrequest.bankkredit_client?Kredit.findAllByBank_idInListAndClientAndModstatusAndIs_tech(Agentagrbank.findAllByAgentagr_id(hsRes.agentagr.id).collect{it.bank_id},hsRes.inrequest.bankkredit_client,1,1):Kredit.findAllByModstatusAndBank_idInListAndIs_tech(1,Agentagrbank.findAllByAgentagr_id(hsRes.agentagr.id).collect{it.bank_id},1)).collect{it.id}?:[0],hsRes.agentagr.client_id)
          agentperiodlist.each{
            if (!Agentkredit.findByAgentagr_idAndAgentperiod_id(hsRes.inrequest.agentagr_id,it.id)) new Agentkredit(agentagr_id:hsRes.inrequest.agentagr_id).csiSetKreditPeriod(it).setData(hsRes.inrequest).save(failOnError:true)
          }
        }
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addagentkredit\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deleteagentrate = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def oAgentrate = Agentrate.get(requestService.getLongDef('id',0))
    if(!oAgentrate||Actagent.findAllByAgentagr_idAndAgent_idAndIs_report(Agentkredit.get(oAgentrate.agentkredit_id)?.agentagr_id?:0,oAgentrate.agent_id,0)){
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      oAgentrate.delete(flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deleteagentrate\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def deleteagentkredit = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId = requestService.getLongDef('agentagr_id',0)
    hsRes+=requestService.getParams(null,null,['id'])

    hsRes.agentagr = Agentagr.get(lId)
    if (!hsRes.agentagr||!hsRes.inrequest.id||Agentrate.findAllByAgentkredit_id(hsRes.inrequest.id?:0)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Agentkredit.get(hsRes.inrequest.id)?.delete(flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deleteagentkredit\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def agentperiods = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.modstatus = requestService.getIntDef('modstatus',0)
    hsRes.periods = new AgentkreditplanSearch().csiSelectPeriods(hsRes.agentagr.id,hsRes.modstatus)
    hsRes.valutas = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code.toLowerCase();map}
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)

    return hsRes
  }

  def agentperiod = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('agentagr_id',0))
    hsRes.agentperiod = Agentkreditplan.get(requestService.getIntDef('id',0))
    if (!hsRes.agentagr||!hsRes.agentperiod) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)

    return hsRes
  }

  def updateperiod = {
    checkAccess(7)
    checkSectionAccess(5)
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('agentagr_id',0))
    hsRes.agentperiod = Agentkreditplan.get(requestService.getIntDef('id',0))
    hsRes.agentkredit = Agentkredit.findByAgentagr_idAndId(hsRes.agentagr?.id?:0,hsRes.agentperiod?.agentkredit_id?:0)
    if (!hsRes.agentagr||!hsRes.agentperiod||!hsRes.agentkredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.enddate = requestService.getDate('agentperiod_dateend')

    if(hsRes.enddate<=hsRes.agentperiod.datestart)
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        def oCalendar = Calendar.getInstance()
        if (hsRes.agentperiod.modstatus==0&&hsRes.agentperiod.is_last)
          hsRes.agentkredit.updateCalcdate(hsRes.agentperiod.setDates(hsRes.agentperiod.datestart,hsRes.enddate)
                                                            .csiSetCalcrate(requestService.getBigDecimalDef('calcrate',0.0g).toFloat())
                                                            .csiSetCalccost(requestService.getBigDecimalDef('calccost',0.0g).toFloat())
                                                            .csiSetVrate(requestService.getBigDecimalDef('vrate',1.0g))
                                                            .csiSetPayterm(requestService.getIntDef('payterm',0))
                                                            .csiSetCalcperiod(requestService.getIntDef('calcperiod',0))
                                                            .calculateSum(oCalendar,Agentkreditplan.findAllByAgentkredit_idAndIdNotEqualAndParent(hsRes.agentkredit.id,hsRes.agentperiod.id,0,[sort:'dateend',order:'asc']).sum{ it.recalculatePeriod(oCalendar) }?:0g)
                                                            .save(failOnError:true)?.dateend
                                          ).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/updateperiod\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }

  def deleteperiod = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentkredit = Agentkredit.get(requestService.getIntDef('id',0))
    hsRes.period = Agentkreditplan.findByAgentkredit_idAndId(hsRes.agentkredit.id,requestService.getIntDef('period_id',0))
    if (!hsRes.agentkredit||hsRes.period?.modstatus!=0) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.period.delete(failOnError:true,flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deleteperiod\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def agentaddperiod = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('agentagr_id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.periods = new AgentkreditplanSearch().csiSelectPeriodsForFix(hsRes.agentagr.id)
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)

    return hsRes
  }

  def updateaddperiod = {
    checkAccess(7)
    checkSectionAccess(5)
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('agentagr_id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.agentperiod = Agentkreditplan.get(requestService.getIntDef('period_id',0))
    hsRes.calcrate = requestService.getBigDecimalDef('calcrate',0.0g)

    if(hsRes.calcrate<0)
      hsRes.result.errorcode<<1
    if(!hsRes.agentperiod)
      hsRes.result.errorcode<<2

    if(!hsRes.result.errorcode){
      try {
        new Agentkreditplan(parent:hsRes.agentperiod.id).fillFrom(hsRes.agentperiod).csiSetCalcrate(hsRes.calcrate.toFloat()).calculateFixperiodSum(Calendar.getInstance()).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/updateaddperiod\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }

  def computeperiods = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    try {
      agentKreditService.updateAgentagrPeriod(requestService.getIntDef('id',0),new Date())
    } catch(Exception e) {
      log.debug("Error save data in Agreement/computeperiods\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def computenextperiods = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def basedate = Calendar.getInstance()
    basedate.add(Calendar.MONTH,1)
    try {
      agentKreditService.updateAgentagrPeriod(requestService.getIntDef('id',0),basedate.getTime())
    } catch(Exception e) {
      log.debug("Error save data in Agreement/computenextperiods\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def computeoldperiods = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def basedate = requestService.getDate('oldperiod_computedate')
    try {
      if (basedate) agentKreditService.updateAgentagrPeriod(requestService.getIntDef('id',0),basedate)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/computeoldperiods\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def clientacts = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.acts = Actclient.findAllByAgentagr_id(hsRes.agentagr.id,[sort:'inputdate',order:'desc'])
    hsRes.paysummas = hsRes.acts.inject([:]){map, act -> map[act.id] = [clientpay_summa:(Payrequest.findAll{ modstatus >=2 && paytype == 2 && agentagr_id == hsRes.agentagr.id && agent_id == 0 && month(paydate) == act.month && year(paydate) == act.year }.sum{it.clientcommission}?:0g)+(Cash.findAll{ agentagr_id == hsRes.agentagr.id && agent_id == 0 && type == 2 && month(operationdate) == act.month && year(operationdate) == act.year }.sum{ it.summa }?:0g),agentfix_summa:Agentfix.findAll{ agentagr_id == hsRes.agentagr.id && month(paydate) == act.month && year(paydate) == act.year }.sum{it.summa}?:0g];map}
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)
    hsRes.isHaveAct = Actclient.findByAgentagr_idAndMonthAndYear(hsRes.agentagr.id,new Date().getMonth()+1,new Date().getYear()+1900)

    return hsRes
  }

  def computeclientact = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    try {
      agentKreditService.computeclientact(requestService.getIntDef('id',0))
    } catch(Exception e) {
      log.debug("Error save data in Agreement/computeclientact\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def computeclientactpaid = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    try {
      agentKreditService.updateclientactpaidsum(requestService.getIntDef('id',0))
      agentKreditService.updateclientactfixsum(requestService.getIntDef('id',0))
      agentKreditService.updateagentactpaidsum(requestService.getIntDef('id',0))
    } catch(Exception e) {
      log.debug("Error save data in Agreement/computeclientactpaid\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def clientactsXLS = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.acts = Actclient.findAllByAgentagr_id(hsRes.agentagr.id,[sort:'inputdate',order:'desc'])
    hsRes.paysummas = hsRes.acts.inject([:]){map, act -> map[act.id] = [clientpay_summa:(Payrequest.findAll{ modstatus >=2 && paytype == 2 && agentagr_id == hsRes.agentagr.id && agent_id == 0 && month(paydate) == act.month && year(paydate) == act.year }.sum{it.clientcommission}?:0g)+(Cash.findAll{ agentagr_id == hsRes.agentagr.id && agent_id == 0 && type == 2 && month(operationdate) == act.month && year(operationdate) == act.year }.sum{ it.summa }?:0g),agentfix_summa:Agentfix.findAll{ agentagr_id == hsRes.agentagr.id && month(paydate) == act.month && year(paydate) == act.year }.sum{it.summa}?:0g];map}

    if (hsRes.acts.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        fillRow(['Период сверки','Дата сверки','Сумма комиссии','Сумма корректировок за предыдущие периоды','Задолженность за предыдущие периоды','Сумма платежей за месяц свервки','Сумма корректировок за месяц свервки','Сумма к погашению за период','Статус'],3,false)
        hsRes.acts.each{ record ->
          fillRow([String.format('%tB %<tY',new Date(record.year-1900,record.month-1,1)),
                   String.format('%td.%<tm.%<tY',record.inputdate),
                   number(value:record.summa).toString(),
                   number(value:record.summafix).toString(),
                   number(value:record.summaprev-record.paid-record.agentfix).toString(),
                   number(value:hsRes.paysummas[record.id].clientpay_summa).toString(),
                   number(value:hsRes.paysummas[record.id].agentfix_summa).toString(),
                   number(value:record.summa+record.summafix-record.actpaid).toString(),
                   record.modstatus?'согласован':'новый'], rowCounter++, false)
        }
        save(response.outputStream)
      }
    }
    return
  }

  def genclientfix = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    hsRes.act = Actclient.findByAgentagr_idAndId(hsRes.agentagr?.id?:0,requestService.getIntDef('act_id',0))
    if (!hsRes.act?.isnotfixes()) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.mainclient = Client.get(Client.get(hsRes.agentagr.client_id)?.parent?:0)
    try {
      new Payrequest().csiSetPayrequest(paycat:4,paytype:4,summa:hsRes.act.csiGetfixesSum(),paydate:new Date().format('dd.MM.yyyy'),client_id:hsRes.mainclient?hsRes.mainclient.id:hsRes.agentagr.client_id,subclient_id:hsRes.mainclient?hsRes.agentagr.client_id:0,agentagr_id:hsRes.agentagr.id).csiSetInitiator(hsRes.user.id).save(flush:true,failOnError:true)
      agentKreditService.updateclientactfixsum(requestService.getIntDef('id',0))
    } catch(Exception e) {
      log.debug("Error save data in Agreement/genclientfix\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def agreeclientact = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Actclient.findByAgentagr_idAndId(hsRes.agentagr.id,requestService.getIntDef('act_id',0))?.csiSetModstatus(requestService.getIntDef('status',0))?.save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/agreeclientact\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def deleteclientact = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    hsRes.act = Actclient.findByAgentagr_idAndId(hsRes.agentagr?.id?:0,requestService.getIntDef('act_id',0))
    if (!hsRes.agentagr||!hsRes.act) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      if (hsRes.act.modstatus==0) hsRes.act.delete(flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deleteclientact\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def printdetailedclientact = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    hsRes.act = Actclient.findByAgentagr_idAndId(hsRes.agentagr?.id?:0,requestService.getIntDef('act_id',0))
    if (!hsRes.agentagr||!hsRes.act) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.client = Client.get(hsRes.agentagr.client_id)
    hsRes.bank = Bank.get(hsRes.agentagr.bank_id)
    hsRes.curPeriods = new AgentkreditplanSearch().csiSelectPeriodsByMonth(hsRes.agentagr.id,hsRes.act.month,hsRes.act.year)
    hsRes.kreditsummas = hsRes.curPeriods.inject([:]){map, period -> map[period.id] = Agentkreditplan.get(period.id)?.csiGetDetailedKreditSummas();map}
    hsRes.valutas = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code;map}
    hsRes.fixes = new AgentfixSearch().csiSelectFixesByDatesBetween(hsRes.agentagr.id,0,hsRes.act.inputdate-50,hsRes.act.inputdate)
    hsRes.payments = (Payrequest.findAllByPaytypeAndAgentagr_idAndAgent_idAndPaydateBetweenAndModstatusGreaterThan(2,hsRes.agentagr.id,0,hsRes.act.inputdate-30,hsRes.act.inputdate,-1,[sort:'paydate',order:'desc'])+Cash.findAllByAgentagr_idAndAgent_idAndTypeAndOperationdateBetween(hsRes.agentagr.id,0,2,hsRes.act.inputdate-30,hsRes.act.inputdate,[sort:'operationdate',order:'desc'])).sort{ it.class==Cash.class?it.operationdate:it.paydate }.reverse(true)
    hsRes.writeoffs = Payrequest.findAllByPaytypeAndAgentagr_idAndAgent_idAndPaydateBetweenAndModstatusGreaterThan(4,hsRes.agentagr.id,0,hsRes.act.inputdate-30,hsRes.act.inputdate,-1,[sort:'paydate',order:'desc'])

    renderPdf(template:'clientactdetailed',model:hsRes,filename:"verifyclientdetailed_${hsRes.agentagr.id}_${String.format('%tm_%<tY',new Date(hsRes.act.year-1900,hsRes.act.month-1,1))}.pdf")
  }

  def printclientact = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    hsRes.act = Actclient.findByAgentagr_idAndId(hsRes.agentagr?.id?:0,requestService.getIntDef('act_id',0))
    if (!hsRes.agentagr||!hsRes.act) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.client = Client.get(hsRes.agentagr.client_id)
    hsRes.bank = Bank.get(hsRes.agentagr.bank_id)
    hsRes.curPeriods = new AgentkreditplanSearch().csiSelectPeriodsByMonth(hsRes.agentagr.id,hsRes.act.month,hsRes.act.year)
    hsRes.kreditsummas = hsRes.curPeriods.inject([:]){map, period -> map[period.id] = Agentkreditplan.get(period.id)?.csiGetDetailedKreditSummas();map}
    hsRes.valutas = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code;map}

    renderPdf(template:'clientact',model:hsRes,filename:"verifyclient_${hsRes.agentagr.id}_${String.format('%tm_%<tY',new Date(hsRes.act.year-1900,hsRes.act.month-1,1))}.pdf")
  }

  def agents = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.agents = new AgentplanSearch().csiSelectAgents(hsRes.agentagr.id)
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)
    hsRes.isHaveAct = Actagent.findByAgentagr_idAndMonthAndYearAndIs_report(hsRes.agentagr.id,new Date().getMonth()+1,new Date().getYear()+1900,0)

    return hsRes
  }

  def computeagentact = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agr_id = requestService.getIntDef('id',0)
    try {
      if (Agentrate.findByAgentkredit_idInList(Agentkredit.findAllByAgentagr_id(hsRes.agr_id).collect{ it.id }?:[0])) agentKreditService.computeagentact(hsRes.agr_id,new Date())
    } catch(Exception e) {
      log.debug("Error save data in Agreement/computeagentact\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def computnexteagentact = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def basedate = Calendar.getInstance()
    basedate.add(Calendar.MONTH,1)
    try {
      agentKreditService.computeagentact(requestService.getIntDef('id',0),basedate.getTime())
    } catch(Exception e) {
      log.debug("Error save data in Agreement/computnexteagentact\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def agentrateplan = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.agent_id = requestService.getIntDef('agent_id',0)

    hsRes.acts = Actagent.findAllByAgentagr_idAndAgent_idAndIs_report(hsRes.agentagr.id,hsRes.agent_id,0,[sort:'inputdate',order:'desc'])
    hsRes.paysummas = hsRes.acts.inject([:]){map, act -> map[act.id] = [agentpay_summa:(Payrequest.findAll{ modstatus >=2 && paytype == 1 && agentagr_id == hsRes.agentagr.id && agent_id == act.agent_id && month(paydate) == act.month && year(paydate) == act.year }.sum{it.summa}?:0g)+(Payrequest.findAll{ modstatus >=1 && paytype == 6 && agentagr_id == hsRes.agentagr.id && agent_id == act.agent_id && month(paydate) == act.month && year(paydate) == act.year }.sum{it.agentcommission}?:0g)+(Cash.findAll{ agentagr_id == hsRes.agentagr.id && agent_id == act.agent_id && month(operationdate) == act.month && year(operationdate) == act.year }.sum{it.type==1?it.summa:-it.summa}?:0g),agentfix_summa:Agentfix.findAll{ agentagr_id == hsRes.agentagr.id && agent_id == act.agent_id && month(paydate) == act.month && year(paydate) == act.year }.sum{it.summa}?:0g];map}
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)

    return hsRes
  }

  def agreeagentact = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Actagent.findByAgentagr_idAndIdAndIs_report(hsRes.agentagr.id,requestService.getIntDef('act_id',0),0)?.csiSetModstatus(requestService.getIntDef('status',0))?.save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/agreeagentact\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def deleteagentact = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    hsRes.act = Actagent.findByAgentagr_idAndIdAndIs_report(hsRes.agentagr?.id?:0,requestService.getIntDef('act_id',0),0)
    if (!hsRes.agentagr||!hsRes.act) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      if (hsRes.act.modstatus==0) hsRes.act.delete(flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deleteagentact\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def printagentact = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    hsRes.act = Actagent.findByAgentagr_idAndIdAndIs_report(hsRes.agentagr?.id?:0,requestService.getIntDef('act_id',0),0)
    if (!hsRes.agentagr||!hsRes.act) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.client = Client.get(hsRes.agentagr.client_id)
    hsRes.bank = Bank.get(hsRes.agentagr.bank_id)
    hsRes.agent = Agent.get(hsRes.act.agent_id)
    hsRes.fixes = new AgentfixSearch().csiSelectFixesByDatesBetween(hsRes.agentagr.id,hsRes.act.agent_id,hsRes.act.inputdate-30,hsRes.act.inputdate)
    hsRes.curPeriods = new AgentrateplanSearch().csiSelectCurrentAgentratesForActagent(hsRes.agentagr.id,hsRes.act.agent_id,hsRes.act.month,hsRes.act.year)
    hsRes.contragents = hsRes.curPeriods.collect{it.kredit_id}.unique().inject([:]){ map, kredit_id -> map[kredit_id] = Company.get(Kredit.get(kredit_id)?.client?:0)?.name; map }
    hsRes.payments = (Payrequest.findAllByPaytypeAndAgentagr_idAndAgent_idAndModstatusGreaterThanAndPaydateBetween(1,hsRes.agentagr.id,hsRes.act.agent_id,1,hsRes.act.inputdate-30,hsRes.act.inputdate,[sort:'paydate',order:'desc'])+Payrequest.findAllByPaytypeAndAgentagr_idAndAgent_idAndModstatusGreaterThanAndPaydateBetween(6,hsRes.agentagr.id,hsRes.act.agent_id,0,hsRes.act.inputdate-30,hsRes.act.inputdate,[sort:'paydate',order:'desc'])+Cash.findAllByAgentagr_idAndAgent_idAndOperationdateBetween(hsRes.agentagr.id,hsRes.act.agent_id,hsRes.act.inputdate-30,hsRes.act.inputdate,[sort:'operationdate',order:'desc'])).sort{ it.class==Cash.class?it.operationdate:it.paydate }.reverse(true)

    renderPdf(template:'agentact',model:hsRes,filename:"verifyagent_${hsRes.agentagr.id}_${String.format('%tm_%<tY',new Date(hsRes.act.year-1900,hsRes.act.month-1,1))}.pdf")
  }

  def agentrateperiods = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.agentrateperiods = Agentratekreditplan.findAllByAgentkredit_idInListAndIs_report(Agentkredit.findAllByAgentagr_id(hsRes.agentagr.id).collect{ it.id }?:[0],0).sort{ a, b -> b.dateend <=> a.dateend }
    hsRes.contragents = Agentkredit.findAllByAgentagr_id(hsRes.agentagr.id).inject([:]){ map, akr -> map[akr.id] = Company.get(Kredit.get(akr.kredit_id)?.client?:0)?.name; map }
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)

    return hsRes
  }

  def agentrateperiod = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('agentagr_id',0))
    hsRes.agentrateperiod = Agentratekreditplan.get(requestService.getIntDef('id',0))
    if (!hsRes.agentagr||!hsRes.agentrateperiod) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.agentrates = Agentrateforperiods.findAllByAgentkredit_idAndAgentratekreditplan_idAndIs_sub(hsRes.agentrateperiod.agentkredit_id,hsRes.agentrateperiod.id,0)
    hsRes.subagentrates = Agentrateforperiods.findAllByAgentkredit_idAndAgentratekreditplan_idAndIs_sub(hsRes.agentrateperiod.agentkredit_id,hsRes.agentrateperiod.id,1)
    hsRes.agents = Agent.list()
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)

    return hsRes
  }

  def updateagentrateperiod = {
    checkAccess(7)
    checkSectionAccess(5)
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('agentagr_id',0))
    hsRes.agentrateperiod = Agentratekreditplan.get(requestService.getIntDef('id',0))
    hsRes.agentkredit = Agentkredit.findByAgentagr_idAndId(hsRes.agentagr?.id?:0,hsRes.agentrateperiod?.agentkredit_id?:0)
    if (!hsRes.agentagr||!hsRes.agentrateperiod||!hsRes.agentkredit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.calcrate = requestService.getBigDecimalDef('calcrate',0.0g).toFloat()
    hsRes.calccost = requestService.getBigDecimalDef('calccost',0.0g).toFloat()
    hsRes.agentrates = Agentrateforperiods.findAllByAgentkredit_idAndAgentratekreditplan_id(hsRes.agentrateperiod.agentkredit_id,hsRes.agentrateperiod.id).each{
      hsRes.inrequest = requestService.getParams(["agentrates_agent_id_$it.id"],null,null,null,["rate_$it.id"]).inrequest
    }

    try {
      def oCalendar = Calendar.getInstance()
      hsRes.agentrateperiod.csiSetCalcrate(hsRes.calcrate)
                           .csiSetCalccost(hsRes.calccost)
                           .csiSetVrate(requestService.getBigDecimalDef('vrate',1.0g))
                           .calculateSum(oCalendar,Agentratekreditplan.createCriteria().list(sort:'dateend',order:'desc') { eq('agentkredit_id',hsRes.agentrateperiod.agentkredit_id) ne('id',hsRes.agentrateperiod.id) or { lt('year',hsRes.agentrateperiod.year) le('month',hsRes.agentrateperiod.month) } }.sum{ it.recalculatePeriod(oCalendar) }?:0g)
                           .save(failOnError:true,flush:true)
      hsRes.agentrates.each{
        it.setData(hsRes.inrequest).save(failOnError:true)
      }
      Agentrate.findAllByAgentkredit_idInList(Agentkredit.findAllByAgentagr_id(hsRes.agentagr.id).collect{ it.id }?:[0]).collect{ it.agent_id }.unique().each{ agent_id ->
        if(!Actagent.findByAgentagr_idAndAgent_idAndMonthAndYearAndIs_report(hsRes.agentagr.id,agent_id,hsRes.agentrateperiod.month,hsRes.agentrateperiod.year,0)) Actagent.findOrCreateWhere(agentagr_id:hsRes.agentagr.id,agent_id:agent_id,is_report:1,month:hsRes.agentrateperiod.month,year:hsRes.agentrateperiod.year).csiSetOfficial().computeSummas().save(failOnError:true)
      }
    } catch(Exception e) {
      log.debug("Error save data in Agreement/updateagentrateperiod\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def agentfixes = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.fixes = new AgentfixSearch().csiSelectFixes(hsRes.agentagr.id)
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)

    return hsRes
  }

  def agentfix = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('agentagr_id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.agentfix = Agentfix.get(requestService.getIntDef('id',0))
    hsRes.agents = Agent.findAllByIdInList(Agentrate.findAllByAgentkredit_idInListAndIs_sub(Agentkredit.findAllByAgentagr_id(hsRes.agentagr.id).collect{ it.id }?:[0],0).collect{ it.agent_id }.unique()?:[0])
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)

    return hsRes
  }

  def addagentfix = {
    checkAccess(7)
    checkSectionAccess(5)
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.agentagr = Agentagr.get(requestService.getIntDef('agentagr_id',0))
    hsRes.agentfix = Agentfix.get(lId)
    if (!hsRes.agentagr||(!hsRes.agentfix&&lId)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['agent_id'],null,null,null,['summa'])
    hsRes.inrequest.paydate = requestService.getDate('agentfix_paydate')

    if(!hsRes.inrequest.agent_id)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.paydate)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<3
    else if(hsRes.inrequest.summa<=0)
      hsRes.result.errorcode<<4

    if(!hsRes.result.errorcode){
      try {
        if(!hsRes.agentfix) hsRes.agentfix = new Agentfix(agentagr_id:hsRes.agentagr.id)
        hsRes.agentfix.setData(hsRes.inrequest).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addagentfix\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }

  def deleteagentfix = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    try {
      Agentfix.findByAgentagr_idAndId(requestService.getIntDef('id',0),requestService.getIntDef('agentfix_id',0))?.delete(flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deleteagentfix\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def agentprofit = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.acts = Actagent.findAllByAgentagr_idAndIs_report(hsRes.agentagr.id,0,[sort:'inputdate',order:'desc']).groupBy{new Date(it.year-1900,it.month-1,1)}

    return hsRes
  }

  def printprofitact = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    hsRes.act = Actagent.findByAgentagr_idAndIdAndIs_report(hsRes.agentagr?.id?:0,requestService.getIntDef('act_id',0),0)
    if (!hsRes.agentagr||!hsRes.act) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.client = Client.get(hsRes.agentagr.client_id)
    hsRes.bank = Bank.get(hsRes.agentagr.bank_id)
    hsRes.agentacts = Actagent.findAllByAgentagr_idAndMonthAndYearAndIs_report(hsRes.agentagr.id,hsRes.act.month,hsRes.act.year,0)
    hsRes.akrs = Agentkredit.findAllByAgentagr_id(hsRes.agentagr.id)
    hsRes.curPeriods = Agentratekreditplan.findAllByAgentkredit_idInListAndMonthAndYear(hsRes.akrs.collect{it.id}?:[0],hsRes.act.month,hsRes.act.year)
    hsRes.contragents = hsRes.akrs.inject([:]){ map, akr -> map[akr.id] = Company.get(Kredit.get(akr.kredit_id)?.client?:0)?.name; map }

    renderPdf(template:'profitact',model:hsRes,filename:"profit_${hsRes.agentagr.id}_${String.format('%tm_%<tY',new Date(hsRes.act.year-1900,hsRes.act.month-1,1))}.pdf")
  }

  def agclientpayments = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.type = requestService.getIntDef('type',0)
    if (hsRes.type)
      hsRes.cashpayments = Cash.findAllByAgentagr_idAndAgent_idAndType(hsRes.agentagr.id,0,2,[sort:'operationdate',order:'desc'])
    else
      hsRes.payments = Payrequest.findAllByPaytypeAndAgentagr_idAndAgent_idAndModstatusGreaterThan(2,hsRes.agentagr.id,0,-1,[sort:'paydate',order:'desc'])

    return hsRes
  }

  def agagentpayments = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.type = requestService.getIntDef('type',0)
    if (hsRes.type)
      hsRes.cashpayments = Cash.findAllByAgentagr_idAndAgent_idGreaterThan(hsRes.agentagr.id,0,[sort:'operationdate',order:'desc'])
    else
      hsRes.payments = Payrequest.findAllByPaytypeInListAndAgentagr_idAndAgent_idGreaterThanAndModstatusGreaterThan([1,6],hsRes.agentagr.id,0,-1,[sort:'paydate',order:'desc'])
    hsRes.agents = Agent.list().inject([:]){map, agent -> map[agent.id]=agent.name;map}
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)

    return hsRes
  }

  def agpayment = {
    checkAccess(7)
    checkSectionAccess(5)
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('agentagr_id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.agrtypes = Agreementtype.findAllByIdNotEqual(5)
    hsRes.agents = Agent.findAllByIdInList(Agentrate.findAllByAgentkredit_idInList(Agentkredit.findAllByAgentagr_id(hsRes.agentagr.id).collect{ it.id }?:[0]).collect{ it.agent_id }.unique()?:[0])

    return hsRes
  }

  def agpaymentbanklist = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)

    return [banks:Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(requestService.getIntDef('company_id',0),1,1).collect{Bank.get(it.bank_id)}]
  }

  def agpaymentagrlist = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)

    def lsAgrs
    switch(requestService.getIntDef('agreementtype_id',0)){
      case 1: lsAgrs = License.list([sort:'anumber',order:'asc']); break
      case 2: lsAgrs = Space.findAllByModstatus(1,[sort:'anumber',order:'asc']); break
      case 3: lsAgrs = Kredit.findAllByModstatus(1,[sort:'anumber',order:'asc']); break
      case 4: lsAgrs = Lizing.findAllByModstatus(1,[sort:'anumber',order:'asc']); break
      case 6: lsAgrs = Cession.findAllByModstatus(1,[sort:'anumber',order:'asc']); break
      case 7: lsAgrs = Trade.findAllByModstatus(1,[sort:'anumber',order:'asc']); break
    }

    return [agrs:lsAgrs]
  }

  def addagpayment = {
    checkAccess(7)
    checkSectionAccess(5)
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('agentagr_id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['paycat','fromcompany_id','tocompany_id','agreement_id','agreementtype_id','agent_id',
                                     'is_nds'],null,['tobank','destination','comment','tagcomment'],null,['summa'/*,'summands'*/])
    hsRes.inrequest.paydate = requestService.getDate('agpayment_paydate')?.format('dd.MM.yyyy')

    hsRes.mainclient = Client.get(Client.get(hsRes.agentagr.client_id)?.parent?:0)
    hsRes.is_mainagent = hsRes.mainclient?.id==Agent.get(hsRes.inrequest.agent_id?:0)?.client_id

    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<11
    if(!hsRes.inrequest.paydate)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.agent_id)
      hsRes.result.errorcode<<10
    if (!hsRes.is_mainagent){
      if(!hsRes.inrequest.paycat)
        hsRes.result.errorcode<<3
      if(!hsRes.inrequest.fromcompany_id)
        hsRes.result.errorcode<<4
      if(!hsRes.inrequest.tocompany_id)
        hsRes.result.errorcode<<5
      if(!hsRes.inrequest.tobank)
        hsRes.result.errorcode<<6
      if(hsRes.inrequest.paycat==4&&!hsRes.inrequest.comment)
        hsRes.result.errorcode<<12
    }

    if(!hsRes.result.errorcode){
      try {
        if (hsRes.is_mainagent) new Payrequest().csiSetPayrequest(paycat:4,paytype:6,client_id:hsRes.mainclient.id,agentagr_id:hsRes.agentagr.id,agent_id:hsRes.inrequest.agent_id,summa:hsRes.inrequest.summa,is_nds:hsRes.inrequest.is_nds,paydate:hsRes.inrequest.paydate,destination:hsRes.inrequest.destination).csiSetInitiator(hsRes.user.id).save(failOnError:true)
        else new Payrequest().csiSetPayrequest(hsRes.inrequest+[paytype:1,client_id:hsRes.agentagr.client_id,agentagr_id:hsRes.agentagr.id]).csiSetInitiator(hsRes.user.id).save(flush:true,failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addagpayment\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }

  def clientfixes = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.fixes = Payrequest.findAllByPaytypeAndAgentagr_idAndAgent_idAndModstatusGreaterThan(4,hsRes.agentagr.id,0,-1,[sort:'paydate',order:'desc'])
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)

    return hsRes
  }

  def clientfix = {
    checkAccess(7)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agentagr = Agentagr.get(requestService.getIntDef('agentagr_id',0))
    if (!hsRes.agentagr) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.clientfix = Payrequest.get(requestService.getIntDef('id',0))
    hsRes.iscanedit = recieveSectionPermission(AAGEDIT)

    return hsRes
  }

  def addclientfix = {
    checkAccess(7)
    checkSectionAccess(5)
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.agentagr = Agentagr.get(requestService.getIntDef('agentagr_id',0))
    hsRes.clientfix = Payrequest.get(lId)
    if (!hsRes.agentagr||(!hsRes.clientfix&&lId)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,['destination'],null,['summa'])
    hsRes.inrequest.paydate = requestService.getDate('clientfix_paydate')?.format('dd.MM.yyyy')

    if(!hsRes.inrequest.paydate)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<2
    else if(hsRes.inrequest.summa<=0)
      hsRes.result.errorcode<<3

    if(!hsRes.result.errorcode){
      try {
        hsRes.mainclient = Client.get(Client.get(hsRes.agentagr.client_id)?.parent?:0)
        if(!hsRes.clientfix) hsRes.clientfix = new Payrequest()
        hsRes.clientfix.csiSetPayrequest(hsRes.inrequest+[paycat:4,paytype:4,client_id:hsRes.mainclient?hsRes.mainclient.id:hsRes.agentagr.client_id,subclient_id:hsRes.mainclient?hsRes.agentagr.client_id:0,agentagr_id:hsRes.agentagr.id]).csiSetInitiator(hsRes.user.id).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addclientfix\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }

  def deleteclientfix = {
    checkAccess(7)
    if (!checkSectionAccess(5)) return
    if (!checkSectionPermission(AAGEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.payrequest = Payrequest.findByAgentagr_idAndIdAndPaytypeAndModstatusGreaterThan(requestService.getIntDef('id',0),requestService.getIntDef('clientfix_id',0),4,-1)
    if (hsRes.payrequest?.modstatus>0||hsRes.payrequest?.deal_id>0) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest?.delete(flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deleteclientfix\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Agent <<</////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Service >>>///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def servicefilter = {
    checkAccess(7)
    checkSectionAccess(8)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails){
      hsRes.inrequest = session.agrlastRequest
    }

    hsRes.iscanedit = recieveSectionPermission(ASEREDIT)
    hsRes.stypes = Servicetype.list()

    return hsRes
  }

  def services = {
    checkAccess(7)
    checkSectionAccess(8)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.agrlastRequest
      session.agrlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['atype','asort','sid'],null,['ecompany_name','zcompany_name'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.dateend = requestService.getDate('dateend')
      hsRes.inrequest.offset = requestService.getOffset()
      session.agrlastRequest = hsRes.inrequest
    }
    session.agrlastRequest.agrobject = 8

    hsRes.searchresult = new ServiceSearch().csiSelectServices(hsRes.inrequest.sid?:0,hsRes.inrequest.zcompany_name?:'',
                                                               hsRes.inrequest.ecompany_name?:'',hsRes.inrequest.atype?:0,
                                                               hsRes.inrequest.asort?:0,hsRes.inrequest.modstatus?:0,
                                                               hsRes.inrequest.dateend,session.user.group.visualgroup_id,
                                                               20,hsRes.inrequest.offset)
    hsRes.stypes = Servicetype.list().inject([:]){map, type -> map[type.id]=type.name;map}

    return hsRes
  }

  def service = {
    checkAccess(7)
    checkSectionAccess(8)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId=requestService.getIntDef('id',0)
    hsRes.service = Service.get(lId)
    if (!hsRes.service&&lId) {
      response.sendError(404)
      return
    }

    hsRes.zcompany = Company.get(hsRes.service?.zcompany_id?:0)
    hsRes.ecompany = Company.get(hsRes.service?.ecompany_id?:0)
    hsRes.zbanks = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(hsRes.service?.zcompany_id?:0,1,1).collect{Bank.get(it.bank_id)}
    hsRes.ebanks = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(hsRes.service?.ecompany_id?:0,1,1).collect{Bank.get(it.bank_id)}
    hsRes.stypes = Servicetype.list()
    hsRes.projects = Project.findAllByModstatus(1,[sort:'name'])
    hsRes.responsiblies = new UserpersSearch().csiFindByAccessrigth(ASEREDIT)
    hsRes.iscanedit = recieveSectionPermission(ASEREDIT)
    hsRes.isCanRestore = hsRes.service?.modstatus==0&&hsRes.service?.enddate>=new Date().clearTime()
    hsRes.isCanDelete = !Payrequest.findAllByAgreementtype_idAndAgreement_idAndModstatusGreaterThan(8,hsRes.service?.id?:0,-1)&&hsRes.iscanedit

    return hsRes
  }

  def servicebanklist={
    checkAccess(7)
    checkSectionAccess(8)
    requestService.init(this)

    return [type:requestService.getIntDef('type',0),banks:Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(Company.findByNameOrInn(requestService.getStr('companyname'),requestService.getStr('companyname'))?.id,1,1).collect{Bank.get(it.bank_id)}]
  }

  def updateservice = {
    checkAccess(7)
    if (!checkSectionAccess(8)) return
    if (!checkSectionPermission(ASEREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    def lId=requestService.getIntDef('id',0)
    hsRes.service = Service.get(lId)
    if (!hsRes.service&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['atype','asort','prolongcondition','prolongterm','paycondition','payterm',
                                     'paytermcondition','modstatus','is_nds','project_id'],['summa','responsible'],
                                    ['zcompany','ecompany','zbank_id','ebank_id','anumber','comment','description'])
    hsRes.inrequest.adate = requestService.getDate('adate')
    hsRes.inrequest.enddate = requestService.getDate('enddate')

    if(!hsRes.service){
      if(!hsRes.inrequest.zcompany)
        hsRes.result.errorcode<<1
      else if(!Company.findByNameOrInn(hsRes.inrequest.zcompany,hsRes.inrequest.zcompany))
        hsRes.result.errorcode<<2
      else if(Company.findAllByNameOrInn(hsRes.inrequest.zcompany,hsRes.inrequest.zcompany).size()>1)
        hsRes.result.errorcode<<3
      if(!hsRes.inrequest.ecompany)
        hsRes.result.errorcode<<4
      else if(!Company.findByNameOrInn(hsRes.inrequest.ecompany,hsRes.inrequest.ecompany))
        hsRes.result.errorcode<<5
      else if(Company.findAllByNameOrInn(hsRes.inrequest.ecompany,hsRes.inrequest.ecompany).size()>1)
        hsRes.result.errorcode<<6
      if(!hsRes.inrequest.summa)
        hsRes.result.errorcode<<12
      else if(hsRes.inrequest.summa<0)
        hsRes.result.errorcode<<13
      if(!hsRes.inrequest.enddate)
        hsRes.result.errorcode<<15
      if(hsRes.inrequest.enddate<hsRes.inrequest.adate)
        hsRes.result.errorcode<<16
    }
    if(!hsRes.inrequest.atype)
      hsRes.result.errorcode<<9
    if(!hsRes.inrequest.asort)
      hsRes.result.errorcode<<10
    if(!hsRes.inrequest.anumber)
      hsRes.result.errorcode<<11
    if(!hsRes.inrequest.adate)
      hsRes.result.errorcode<<14
    if(!hsRes.inrequest.payterm)
      hsRes.result.errorcode<<17
    if(hsRes.inrequest.prolongcondition==2&&hsRes.inrequest.prolongterm<=0)
      hsRes.result.errorcode<<18

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.service = new Service(zcompany_id:Company.findByNameOrInn(hsRes.inrequest.zcompany,hsRes.inrequest.zcompany)?.id,ecompany_id:Company.findByNameOrInn(hsRes.inrequest.ecompany,hsRes.inrequest.ecompany)?.id).setMainAgrData(hsRes.inrequest)
        hsRes.result.service = hsRes.service.setData(hsRes.inrequest).updateModstatus(hsRes.inrequest.modstatus?:0).csiSetAdmin(session.user.id).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Agreement/updateservice\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def servicedopagrs = {
    checkAccess(7)
    checkSectionAccess(8)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.service = Service.get(requestService.getIntDef('id',0))
    if (!hsRes.service) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.servicedopagrs = Servicedopagr.findAllByService_id(hsRes.service.id,[sort:'id',order:'desc'])
    hsRes.firstagrid = Servicedopagr.getMinId(hsRes.service.id)
    hsRes.iscanedit = recieveSectionPermission(ASEREDIT)

    return hsRes
  }

  def servicedopagr = {
    checkAccess(7)
    checkSectionAccess(8)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.service = Service.get(requestService.getIntDef('service_id',0))
    if (!hsRes.service) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,['id'])

    hsRes.servicedopagr = Servicedopagr.get(hsRes.inrequest.id)
    hsRes.iscanedit = recieveSectionPermission(ASEREDIT)

    return hsRes
  }

  def addservicedopagr = {
    checkAccess(7)
    if (!checkSectionAccess(8)) return
    if (!checkSectionPermission(ASEREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]
 
    hsRes+=requestService.getParams(['id'],['summa'],['nomer','comment'])
    hsRes.inrequest.dsdate = requestService.getDate('servicedopagr_dsdate')
    hsRes.inrequest.startdate = requestService.getDate('servicedopagr_startdate')
    hsRes.inrequest.enddate = requestService.getDate('servicedopagr_enddate')

    hsRes.service = Service.get(requestService.getLongDef('service_id',0))
    hsRes.servicedopagr = Servicedopagr.get(hsRes.inrequest.id)
    if (!hsRes.service||(!hsRes.servicedopagr&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.nomer)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.dsdate)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.startdate)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.enddate)
      hsRes.result.errorcode<<5

    if(!hsRes.result.errorcode){
      try {
        if (!hsRes.inrequest.id) hsRes.servicedopagr = new Servicedopagr(service_id:hsRes.service.id)
        hsRes.servicedopagr.setData(hsRes.inrequest).csiSetAdmin(session.user.id).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addservicedopagr\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deleteservicedopagr = {
    checkAccess(7)
    if (!checkSectionAccess(8)) return
    if (!checkSectionPermission(ASEREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.service = Service.get(requestService.getLongDef('service_id',0))
    if (!hsRes.service||Servicedopagr.getMinId(hsRes.service?.id)==requestService.getLongDef('id',0)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Servicedopagr.findByService_idAndId(hsRes.service.id,requestService.getLongDef('id',0))?.csiSetAdmin(session.user.id)?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deleteservicedopagr\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def servicecalculations = {
    checkAccess(7)
    checkSectionAccess(8)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.service = Service.get(requestService.getIntDef('id',0))
    if (!hsRes.service) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.servicecalculations = Servicecalculation.findAllByService_id(hsRes.service.id,[sort:'calcdate',order:'desc'])
    hsRes.iscanedit = recieveSectionPermission(ASEREDIT)

    return hsRes
  }

  def deleteservicecalculation = {
    checkAccess(7)
    if (!checkSectionAccess(8)) return
    if (!checkSectionPermission(ASEREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.service = Service.get(requestService.getLongDef('service_id',0))
    if (!hsRes.service) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Servicecalculation.findByService_idAndId(hsRes.service.id,requestService.getIntDef('id',0))?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deleteservicecalculation\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def servicecalculation = {
    checkAccess(7)
    checkSectionAccess(8)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.service = Service.get(requestService.getIntDef('service_id',0))
    if (!hsRes.service) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.servcalc = Servicecalculation.findByService_idAndId(hsRes.service.id?:0,requestService.getIntDef('id',0))
    hsRes.iscanedit = recieveSectionPermission(ASEREDIT)

    return hsRes
  }

  def updateservicecalculation = {
    checkAccess(7)
    if (!checkSectionAccess(8)) return
    if (!checkSectionPermission(ASEREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['id'],null,['schet'],null,['summa'])
    hsRes.inrequest.schetdate = requestService.getDate('servcalc_schetdate')
    hsRes.inrequest.maindate = requestService.getDate('servcalc_maindate')

    hsRes.service = Service.get(requestService.getIntDef('service_id',0))
    hsRes.servcalc = Servicecalculation.findByService_idAndId(hsRes.service?.id?:0,hsRes.inrequest.id?:0)
    if (!hsRes.service||(!hsRes.servcalc&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(hsRes.inrequest.summa&&hsRes.inrequest.summa<0)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.id){
      if(!hsRes.inrequest.maindate)
        hsRes.result.errorcode<<2
      else if(Servicecalculation.findAllByService_idAndMonthAndYear(hsRes.service.id,hsRes.inrequest.maindate.getMonth()+1,hsRes.inrequest.maindate.getYear()+1900))
        hsRes.result.errorcode<<3
    }

    if(!hsRes.result.errorcode){
      try {
        if (!hsRes.inrequest.id) hsRes.servcalc = new Servicecalculation(service_id:hsRes.service.id).setBaseData(month:hsRes.inrequest.maindate.getMonth()+1,year:hsRes.inrequest.maindate.getYear()+1900,calcdate:new Date())
        hsRes.servcalc.setData(hsRes.inrequest).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/updateservicecalculation\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def servicehistory = {
    checkAccess(7)
    checkSectionAccess(8)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.service = Service.get(requestService.getIntDef('id',0))
    if (!hsRes.service) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.history = new ServicehistSearch().csiFindServiceHistory(hsRes.service.id)

    return hsRes
  }

  def srpayrequests = {
    checkAccess(7)
    checkSectionAccess(8)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.service = Service.get(requestService.getIntDef('id',0))
    if (!hsRes.service) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.iscanedit = recieveSectionPermission(ASEREDIT)
    hsRes.payrequests = Payrequest.findAllByAgreementtype_idAndAgreement_idAndModstatusGreaterThan(8,hsRes.service.id,-1,[sort:'paydate',order:'desc'])

    return hsRes
  }

  def srpayrequest = {
    checkAccess(7)
    checkSectionAccess(8)
    if (!checkSectionPermission(ASEREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agr = Service.get(requestService.getIntDef('agr_id',0))
    if (!hsRes.agr) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    hsRes.bank = new BankaccountSearch().csiFindAccounts(hsRes.agr.zcompany_id,1,1)
    hsRes.project = Project.findAllByModstatus(1,[sort:'name'])
    hsRes.defproject_id = Project.findByIs_main(1)?.id?:0
    hsRes.expensetype = new ExpensetypeSearch().csiGetList()
    hsRes.tobanks = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(hsRes.agr.ecompany_id,1,1).collect{ Bank.get(it.bank_id) }
    hsRes.tobanks.sort{it.name}
    hsRes.iscantag = recieveSectionPermission(APTAG)

    return hsRes
  }

  def addsrpayrequest = {
    checkAccess(7)
    if (!checkSectionAccess(8)) return
    if (!checkSectionPermission(ASEREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['id','is_nds','frombank','project_id','expensetype_id','is_task'],null,['destination','tobank'],null,['summa'])
    hsRes.inrequest.payrequest_paydate = requestService.getDate('payrequest_paydate')

    hsRes.agr = Service.get(requestService.getIntDef('agr_id',0))
    hsRes.payrequest = Payrequest.get(hsRes.inrequest.id)
    if (!hsRes.agr||(!hsRes.payrequest&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<1
    else if(hsRes.inrequest.summa<=0)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.tobank)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.payrequest_paydate)
      hsRes.result.errorcode<<4
    if(recieveSectionPermission(APTAG)&&!hsRes.inrequest.expensetype_id)
      hsRes.result.errorcode<<5

    if(!hsRes.result.errorcode){
      try {
        if (!hsRes.inrequest.id) hsRes.payrequest = new Payrequest(paytype:1,paycat:1,agreementtype_id:8,agreement_id:hsRes.agr.id).csiSetServiceAgrData(hsRes.agr).csiSetInitiator(hsRes.user.id)
        hsRes.payrequest.setGeneralData(hsRes.inrequest).csiSetPayrequestTag(hsRes.inrequest,recieveSectionPermission(APTAG),hsRes.user.id).csiSetBankaccount_id(hsRes.inrequest.frombank).save(failOnError:true)
        if (hsRes.inrequest.is_task&&hsRes.payrequest.taskpay_id==0){
          def taskpay_id = new Taskpay(paygroup:hsRes.payrequest.paygroup,is_manual:1).csiSetTaskpay(term:String.format('%td.%<tm.%<tY',hsRes.payrequest.paydate),company_id:hsRes.payrequest.fromcompany_id,summa:hsRes.payrequest.summa).csiSetInitiator(hsRes.user.id).csiSetBankaccountId(hsRes.payrequest.bankaccount_id).save(flush:true,failOnError:true)?.id?:0
          hsRes.payrequest.csiSetTaskpay_id(taskpay_id).csiSetModstatus(1).save(flush:true,failOnError:true)
        }
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addsrpayrequest\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletesrpayrequest = {
    checkAccess(7)
    if (!checkSectionAccess(8)) return
    if (!checkSectionPermission(ASEREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.payrequest = Payrequest.findByAgreementtype_idAndAgreement_idAndId(8,requestService.getIntDef('id',0),requestService.getIntDef('payrequest_id',0))
    if (hsRes.payrequest?.modstatus!=0) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest.delete(flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletesrpayrequest\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Service <<<///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////SMR >>>///////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def smrfilter = {
    checkAccess(7)
    checkSectionAccess(9)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails){
      hsRes.inrequest = session.agrlastRequest
    }

    hsRes.iscanedit = recieveSectionPermission(ASMREDIT)
    hsRes.smrcats = Smrcat.list()

    return hsRes
  }

  def smrs = {
    checkAccess(7)
    checkSectionAccess(9)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.agrlastRequest
      session.agrlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['smrcat_id','smrsort'],null,['client_name','supplier_name'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.offset = requestService.getOffset()
      session.agrlastRequest = hsRes.inrequest
    }
    session.agrlastRequest.agrobject = 9

    hsRes.searchresult = new SmrSearch().csiSelectSmrs(hsRes.inrequest.client_name?:'',hsRes.inrequest.supplier_name?:'',
                                                       hsRes.inrequest.smrcat_id?:0,hsRes.inrequest.smrsort?:0,
                                                       hsRes.inrequest.modstatus?:0,session.user.group.visualgroup_id,
                                                       20,hsRes.inrequest.offset)
    hsRes.smrcats = Smrcat.list().inject([:]){map, cat -> map[cat.id]=cat.name;map}

    return hsRes
  }

  def smr = {
    checkAccess(7)
    checkSectionAccess(9)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId = requestService.getIntDef('id',0)
    hsRes.smr = Smr.get(lId)
    if (!hsRes.smr&&lId) {
      response.sendError(404)
      return
    }

    hsRes.clientcompany = Company.get(hsRes.smr?.client?:0)
    hsRes.suppliercompany = Company.get(hsRes.smr?.supplier?:0)
    hsRes.cbanks = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(hsRes.smr?.client?:0,1,1).collect{Bank.get(it.bank_id)}
    hsRes.sbanks = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(hsRes.smr?.supplier?:0,1,1).collect{Bank.get(it.bank_id)}
    hsRes.smrcats = Smrcat.list()
    hsRes.users = User.list()
    hsRes.iscanedit = recieveSectionPermission(ASMREDIT)
    hsRes.isCanDelete = !Payrequest.findAllByAgreementtype_idAndAgreement_idAndModstatusGreaterThan(9,hsRes.smr?.id?:0,-1)&&hsRes.iscanedit

    return hsRes
  }

  def smrbanklist={
    checkAccess(7)
    checkSectionAccess(9)
    requestService.init(this)

    return [type:requestService.getIntDef('type',0),banks:Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(Company.findByNameOrInn(requestService.getStr('companyname'),requestService.getStr('companyname'))?.id,1,1).collect{Bank.get(it.bank_id)}]
  }

  def updatesmr = {
    checkAccess(7)
    if (!checkSectionAccess(9)) return
    if (!checkSectionPermission(ASMREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    def lId=requestService.getIntDef('id',0)
    hsRes.smr = Smr.get(lId)
    if (!hsRes.smr&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['smrcat_id','paytype','modstatus','project_id'],['summa','avans','responsible'],
                                    ['clientcompany','suppliercompany','cbank_id','sbank_id','anumber','comment','description'],null,['avanspercent'])
    hsRes.inrequest.adate = requestService.getDate('adate')
    hsRes.inrequest.enddate = requestService.getDate('enddate')

    if(!hsRes.smr){
      if(!hsRes.inrequest.clientcompany)
        hsRes.result.errorcode<<1
      else if(!Company.findByNameOrInn(hsRes.inrequest.clientcompany,hsRes.inrequest.clientcompany))
        hsRes.result.errorcode<<2
      else if(Company.findAllByNameOrInn(hsRes.inrequest.clientcompany,hsRes.inrequest.clientcompany).size()>1)
        hsRes.result.errorcode<<3
      if(!hsRes.inrequest.suppliercompany)
        hsRes.result.errorcode<<4
      else if(!Company.findByNameOrInn(hsRes.inrequest.suppliercompany,hsRes.inrequest.suppliercompany))
        hsRes.result.errorcode<<5
      else if(Company.findAllByNameOrInn(hsRes.inrequest.suppliercompany,hsRes.inrequest.suppliercompany).size()>1)
        hsRes.result.errorcode<<6
    }
    if(!hsRes.inrequest.cbank_id)
      hsRes.result.errorcode<<7
    if(!hsRes.inrequest.sbank_id)
      hsRes.result.errorcode<<8
    if(!hsRes.inrequest.smrcat_id)
      hsRes.result.errorcode<<9
    if(!hsRes.inrequest.anumber)
      hsRes.result.errorcode<<11
    if(!hsRes.inrequest.responsible)
      hsRes.result.errorcode<<10
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<12
    else if(hsRes.inrequest.summa<0)
      hsRes.result.errorcode<<13
    if(!hsRes.inrequest.adate)
      hsRes.result.errorcode<<14
    if(!hsRes.inrequest.enddate)
      hsRes.result.errorcode<<15
    if(hsRes.inrequest.enddate<hsRes.inrequest.adate)
      hsRes.result.errorcode<<16
    if(hsRes.inrequest.paytype==2&&!hsRes.inrequest.avans&&!hsRes.inrequest.avanspercent)
      hsRes.result.errorcode<<17
    else if(hsRes.inrequest.paytype==2&&hsRes.inrequest.avans>hsRes.inrequest.summa)
      hsRes.result.errorcode<<18

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.smr = new Smr(client:Company.findByNameOrInn(hsRes.inrequest.clientcompany,hsRes.inrequest.clientcompany)?.id,supplier:Company.findByNameOrInn(hsRes.inrequest.suppliercompany,hsRes.inrequest.suppliercompany)?.id)
        hsRes.result.smr = hsRes.smr.setData(hsRes.inrequest).updateModstatus(hsRes.inrequest.modstatus).csiSetAdmin(session.user.id).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Agreement/updatesmr\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def smrhistory = {
    checkAccess(7)
    checkSectionAccess(9)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.smr = Smr.get(requestService.getIntDef('id',0))
    if (!hsRes.smr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.history = new SmrhistSearch().csiFindSmrHistory(hsRes.smr.id)
    hsRes.smrcats = Smrcat.list().inject([:]){map, cat -> map[cat.id]=cat.name;map}

    return hsRes
  }

  def smrpayrequests = {
    checkAccess(7)
    checkSectionAccess(9)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.smr = Smr.get(requestService.getIntDef('id',0))
    if (!hsRes.smr) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.iscanedit = recieveSectionPermission(ASMREDIT)
    hsRes.payrequests = Payrequest.findAllByAgreementtype_idAndAgreement_idAndModstatusGreaterThan(9,hsRes.smr.id,-1,[sort:'paydate',order:'desc'])

    return hsRes
  }

  def smrpayrequest = {
    checkAccess(7)
    checkSectionAccess(9)
    if (!checkSectionPermission(ASMREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.agr = Smr.get(requestService.getIntDef('agr_id',0))
    if (!hsRes.agr) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))

    return hsRes
  }

  def addsmrpayrequest = {
    checkAccess(7)
    if (!checkSectionAccess(9)) return
    if (!checkSectionPermission(ASMREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['id'],null,['destination'],null,['summa','summands'])
    hsRes.inrequest.payrequest_paydate = requestService.getDate('payrequest_paydate')

    hsRes.agr = Smr.get(requestService.getIntDef('agr_id',0))
    hsRes.payrequest = Payrequest.get(hsRes.inrequest.id)
    if (!hsRes.agr||(!hsRes.payrequest&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<1
    else if(hsRes.inrequest.summa<=0)
      hsRes.result.errorcode<<2
    if(hsRes.inrequest.summands&&hsRes.inrequest.summands<0)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.payrequest_paydate)
      hsRes.result.errorcode<<4

    if(!hsRes.result.errorcode){
      try {
        if (!hsRes.inrequest.id) hsRes.payrequest = new Payrequest(paytype:1,paycat:1,agreementtype_id:9,agreement_id:hsRes.agr.id).csiSetSmrAgrData(hsRes.agr).csiSetInitiator(hsRes.user.id)
        hsRes.payrequest.setGeneralData(hsRes.inrequest).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addsmrpayrequest\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletesmrpayrequest = {
    checkAccess(7)
    if (!checkSectionAccess(9)) return
    if (!checkSectionPermission(ASMREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.payrequest = Payrequest.findByAgreementtype_idAndAgreement_idAndId(9,requestService.getIntDef('id',0),requestService.getIntDef('payrequest_id',0))
    if (hsRes.payrequest?.modstatus!=0) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest?.delete(flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletesmrpayrequest\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////SMR <<<///////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Loan >>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def loanfilter = {
    checkAccess(7)
    checkSectionAccess(10)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails){
      hsRes.inrequest = session.agrlastRequest
    }

    hsRes.iscanedit = recieveSectionPermission(ALOANEDIT)

    return hsRes
  }

  def loans = {
    checkAccess(7)
    checkSectionAccess(10)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.agrlastRequest
      session.agrlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['loantype'],null,['client_name','lender_name'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.offset = requestService.getOffset()
      session.agrlastRequest = hsRes.inrequest
    }
    session.agrlastRequest.agrobject = 10

    hsRes.searchresult = new LoanSearch().csiSelectLoans(hsRes.inrequest.client_name?:'',hsRes.inrequest.lender_name?:'',
                                                         hsRes.inrequest.loantype?:0,hsRes.inrequest.modstatus?:0,
                                                         session.user.group.visualgroup_id,20,hsRes.inrequest.offset)
    hsRes.valutas = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code.toLowerCase();map}

    return hsRes
  }

  def loan = {
    checkAccess(7)
    checkSectionAccess(10)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId = requestService.getIntDef('id',0)
    hsRes.loan = Loan.get(lId)
    if (!hsRes.loan&&lId) {
      response.sendError(404)
      return
    }

    if (hsRes.loan){
      hsRes.lender = hsRes.loan.lender?Company.get(hsRes.loan.lender)?.name:Pers.get(hsRes.loan.lender_pers)?.shortname
      hsRes.client = hsRes.loan.client?Company.get(hsRes.loan.client)?.name:Pers.get(hsRes.loan.client_pers)?.shortname
    }
    hsRes.iscanedit = recieveSectionPermission(ALOANEDIT)
    hsRes.isCanDelete = !(Payrequest.findAllByAgreementtype_idAndAgreement_idAndModstatusGreaterThan(10,hsRes.loan?.id?:0,-1)||Loanline.findAllByLoan_idAndModstatusGreaterThanEquals(hsRes.loan?.id?:0,0))&&hsRes.iscanedit

    return hsRes
  }

  def updateloan = {
    checkAccess(7)
    if (!checkSectionAccess(10)) return
    if (!checkSectionPermission(ALOANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.loan = Loan.get(lId)
    if (!hsRes.loan&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['valuta_id','is_cbcalc','modstatus','payterm','repaymenttype_id','monthnumber',
                                     'clienttype','lendertype','loanclass'],['summa'],
                                    ['clientcompany','lendercompany','clientpers','lenderpers','anumber','comment'],null,['rate'])
    hsRes.inrequest.adate = requestService.getDate('adate')
    hsRes.inrequest.startdate = requestService.getDate('startdate')
    hsRes.inrequest.enddate = requestService.getDate('enddate')

    if(!hsRes.loan){
      if(!hsRes.inrequest.lendertype&&!hsRes.inrequest.lendercompany)
        hsRes.result.errorcode<<1
      else if(!hsRes.inrequest.lendertype&&!Company.findByNameOrInn(hsRes.inrequest.lendercompany,hsRes.inrequest.lendercompany))
        hsRes.result.errorcode<<2
      else if(!hsRes.inrequest.lendertype&&Company.findAllByNameOrInn(hsRes.inrequest.lendercompany,hsRes.inrequest.lendercompany).size()>1)
        hsRes.result.errorcode<<3
      if(!hsRes.inrequest.clienttype&&!hsRes.inrequest.clientcompany)
        hsRes.result.errorcode<<4
      else if(!hsRes.inrequest.clienttype&&!Company.findByNameOrInn(hsRes.inrequest.clientcompany,hsRes.inrequest.clientcompany))
        hsRes.result.errorcode<<5
      else if(!hsRes.inrequest.clienttype&&Company.findAllByNameOrInn(hsRes.inrequest.clientcompany,hsRes.inrequest.clientcompany).size()>1)
        hsRes.result.errorcode<<6
      if(hsRes.inrequest.lendertype&&!hsRes.inrequest.lenderpers)
        hsRes.result.errorcode<<7
      else if(hsRes.inrequest.lendertype&&!Compholder.findByPers_id(Pers.findByShortname(hsRes.inrequest.lenderpers)?.id?:0))
        hsRes.result.errorcode<<8
      if(hsRes.inrequest.clienttype&&!hsRes.inrequest.clientpers)
        hsRes.result.errorcode<<9
      else if(hsRes.inrequest.clienttype&&!Pers.findByShortname(hsRes.inrequest.clientpers))
        hsRes.result.errorcode<<10
      if(hsRes.inrequest.lendertype&&hsRes.inrequest.clienttype)
        hsRes.result.errorcode<<11
    }
    if(!hsRes.inrequest.anumber)
      hsRes.result.errorcode<<12
    if(!hsRes.inrequest.adate)
      hsRes.result.errorcode<<13
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<14
    if(!hsRes.inrequest.rate)
      hsRes.result.errorcode<<15
    if(!hsRes.inrequest.startdate)
      hsRes.result.errorcode<<16
    if(!hsRes.inrequest.enddate)
      hsRes.result.errorcode<<17
    else if(hsRes.inrequest.startdate&&Tools.computeMonthDiff(hsRes.inrequest.startdate,hsRes.inrequest.enddate)==0)
      hsRes.result.errorcode<<18
    if(!hsRes.inrequest.payterm)
      hsRes.result.errorcode<<19

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.loan = new Loan(client:hsRes.inrequest.clienttype?0:Company.findByNameOrInn(hsRes.inrequest.clientcompany,hsRes.inrequest.clientcompany)?.id,client_pers:!hsRes.inrequest.clienttype?0:Pers.findByShortname(hsRes.inrequest.clientpers)?.id,lender:hsRes.inrequest.lendertype?0:Company.findByNameOrInn(hsRes.inrequest.lendercompany,hsRes.inrequest.lendercompany)?.id,lender_pers:!hsRes.inrequest.lendertype?0:Pers.findByShortname(hsRes.inrequest.lenderpers)?.id)
        hsRes.result.loan = hsRes.loan.setData(hsRes.inrequest).csiSetAdmin(session.user.id).updateModstatus(hsRes.inrequest.modstatus).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Agreement/updateloan\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def loanline = {
    checkAccess(7)
    checkSectionAccess(10)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.loan = Loan.get(requestService.getIntDef('id',0))
    if (!hsRes.loan) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.planpayments = Loanline.findAllByLoan_idAndModstatusGreaterThanEquals(hsRes.loan.id,0,[sort:'paydate',order:'asc'])
    hsRes.iscanedit = recieveSectionPermission(ALOANEDIT)

    return hsRes
  }

  def loanplanpayment = {
    checkAccess(7)
    checkSectionAccess(10)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.loan = Loan.get(requestService.getIntDef('loan_id',0))
    if (!hsRes.loan) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.iscanedit = recieveSectionPermission(ALOANEDIT)

    return hsRes
  }

  def addloanplanpayment = {
    checkAccess(7)
    if (!checkSectionAccess(10)) return
    if (!checkSectionPermission(ALOANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]
 
    hsRes.loan = Loan.get(requestService.getLongDef('loan_id',0))
    if (!hsRes.loan) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['summa'],null,null,null,['summarub'])
    hsRes.inrequest.planpayment_paydate = requestService.getDate('planpayment_paydate')

    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.planpayment_paydate)
      hsRes.result.errorcode<<2
    if(!hsRes.loan.isRateable()&&!hsRes.inrequest.summarub)
      hsRes.result.errorcode<<3

    if(!hsRes.result.errorcode){
      try {
        new Loanline(loan_id:hsRes.loan.id).setData(hsRes.inrequest,hsRes.loan.getvRate(),hsRes.loan.isRateable()).csiSetModstatus(0).csiSetAdmin(session.user.id).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addloanplanpayment\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deleteloanplanpayment = {
    checkAccess(7)
    if (!checkSectionAccess(10)) return
    if (!checkSectionPermission(ALOANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes+=requestService.getParams(null,null,['id'])
    hsRes.loan = Loan.get(requestService.getLongDef('loan_id',0))
    if (!hsRes.loan||!hsRes.inrequest.id) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Loanline.get(hsRes.inrequest.id).csiSetModstatus(-1).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deleteloanplanpayment\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def loanpayments = {
    checkAccess(7)
    checkSectionAccess(10)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.loan = Loan.get(requestService.getIntDef('id',0))
    if (!hsRes.loan) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payments = new LoanpaymentSearch().csiFindKreditPayment(hsRes.loan.id)
    hsRes.loansum = hsRes.loan.loanclass==1?hsRes.loan.summa:Loanline.findAllByLoan_idAndModstatusGreaterThanEquals(hsRes.loan.id,0).sum{it.summa}?:0
    hsRes.iscanedit = recieveSectionPermission(ALOANEDIT)
    hsRes.totalbody = 0.0g

    return hsRes
  }

  def loanpayment = {
    checkAccess(7)
    checkSectionAccess(10)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.loan = Loan.get(requestService.getIntDef('loan_id',0))
    if (!hsRes.loan) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes+=requestService.getParams(null,null,['id'])
    hsRes.loanpayment = Loanpayment.get(hsRes.inrequest.id)
    hsRes.iscanedit = recieveSectionPermission(ALOANEDIT)

    return hsRes
  }

  def addloanpayment = {
    checkAccess(7)
    if (!checkSectionAccess(10)) return
    if (!checkSectionPermission(ALOANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getLongDef('loan_id',0)
    hsRes+=requestService.getParams(['id'],null,null,null,['summa','summaperc','summarub','summapercrub'])
    hsRes.inrequest.loanpayment_paydate = requestService.getDate('loanpayment_paydate')

    hsRes.loan = Loan.get(lId)
    hsRes.loanpayment = Loanpayment.get(hsRes.inrequest.id)
    if (!hsRes.loan||(!hsRes.loanpayment&&hsRes.inrequest.id)||hsRes.loanpayment?.modstatus>1) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.summa&&!hsRes.inrequest.summaperc)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.loanpayment_paydate)
      hsRes.result.errorcode<<2
    if(!hsRes.loan.isRateable()&&!hsRes.inrequest.summarub&&!hsRes.inrequest.summapercrub)
      hsRes.result.errorcode<<3

    if(!hsRes.result.errorcode){
      try {
        if (!hsRes.inrequest.id) hsRes.loanpayment = new Loanpayment(loan_id:hsRes.loan.id,is_auto:0)
        hsRes.loanpayment.setData(hsRes.inrequest,hsRes.loan.getvRate(),hsRes.loan.isRateable()).csiSetModstatus(0).csiSetAdmin(session.user.id).save(flush:true,failOnError:true)
        if (hsRes.loan.repaymenttype_id<3&&hsRes.loan.loanclass<3&&!Loanpayment.findByLoan_idAndModstatusGreaterThan(hsRes.loan.id,0)) {
          Loanpayment.findAllByLoan_idAndPaydateGreaterThanAndModstatus(hsRes.loan.id,hsRes.loanpayment.paydate,0).each{ it.delete(flush:true) }
          hsRes.loan.recomputepayments(hsRes.loanpayment).each{
            if(it.basesumma+it.perssumma!=0) new Loanpayment(loan_id:hsRes.loan.id).setComputedData(it).csiSetAdmin(session.user.id).save(failOnError:true)
          }
        }
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addloanpayment\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deleteloanpayment = {
    checkAccess(7)
    if (!checkSectionAccess(10)) return
    if (!checkSectionPermission(ALOANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes+=requestService.getParams(null,null,['id'])
    hsRes.loan = Loan.get(requestService.getLongDef('loan_id',0))
    if (!hsRes.loan||!hsRes.inrequest.id) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Loanpayment.get(hsRes.inrequest.id)?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deleteloanpayment\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def generateloanpayments = {
    checkAccess(7)
    if (!checkSectionAccess(10)) return
    if (!checkSectionPermission(ALOANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.loan = Loan.get(requestService.getLongDef('id',0))
    if (!hsRes.loan) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      if (hsRes.loan.repaymenttype_id<3&&hsRes.loan.loanclass<3&&!Loanpayment.findByLoan_idAndModstatusGreaterThan(hsRes.loan.id,0)) {
        Loanpayment.findAllByLoan_id(hsRes.loan.id).each{ it.delete(flush:true) }
        hsRes.loan.computepayments().each{
          if(it.basesumma+it.perssumma!=0) new Loanpayment(loan_id:hsRes.loan.id).setComputedData(it).csiSetAdmin(session.user.id).save(failOnError:true)
        }
      }
    } catch(Exception e) {
      log.debug("Error save data in Agreement/generateloanpayments\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def recalculateLoanRubSummas = {
    checkAccess(7)
    if (!checkSectionAccess(10)) return
    if (!checkSectionPermission(ALOANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.loan = Loan.get(requestService.getIntDef('id',0))
    if (!hsRes.loan) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.loan.recalculateRubSummas()
    } catch(Exception e) {
      log.debug("Error save data in Agreement/recalculateLoanRubSummas\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def uploadloanpayments = {
    checkAccess(7)
    if (!checkSectionAccess(10)) return
    if (!checkSectionPermission(ALOANEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.loan = Loan.get(requestService.getLongDef('id',0))
    if (!hsRes.loan) {
      response.sendError(404)
      return
    }

    hsRes.result = parseService.parseLoanpaymentsFile(request.getFile('file'))

    if(!hsRes.result.errorcode){
      try {
        if (hsRes.loan.repaymenttype_id==3&&!Loanpayment.findByLoan_idAndModstatusGreaterThan(hsRes.loan.id,0)) {
          Loanpayment.findAllByLoan_id(hsRes.loan.id).each{ it.delete(flush:true) }
          hsRes.result.preparedData.each{
            if(it.summa+it.summaperc!=0) new Loanpayment(loan_id:hsRes.loan.id).csiSetAdmin(session.user.id).setData(it,hsRes.loan.getvRate(),true).save(failOnError:true)
          }
        }
      } catch(Exception e) {
        log.debug("Error save data in Agreement/uploadloanpayments\n"+e.toString())
        hsRes.result.errorcode<<100
      }
    }

    return hsRes.result
  }

  def lnoutpayrequests = {
    checkAccess(7)
    checkSectionAccess(10)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.loan = Loan.get(requestService.getIntDef('id',0))
    if (!hsRes.loan) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payrequests = Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeAndModstatusGreaterThan(10,hsRes.loan.id,1,-1,[sort:'paydate',order:'desc'])

    return hsRes
  }

  def lninpayrequests = {
    checkAccess(7)
    checkSectionAccess(10)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.loan = Loan.get(requestService.getIntDef('id',0))
    if (!hsRes.loan) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payrequests = Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeAndModstatusGreaterThan(10,hsRes.loan.id,2,-1,[sort:'paydate',order:'desc'])

    return hsRes
  }

  def loanhistory = {
    checkAccess(7)
    checkSectionAccess(10)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.loan = Loan.get(requestService.getIntDef('id',0))
    if (!hsRes.loan) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.history = new LoanhistSearch().csiFindLoanHistory(hsRes.loan.id)

    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Loan <<<//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Bankdeposit >>>///////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def depositfilter = {
    checkAccess(7)
    checkSectionAccess(11)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails){
      hsRes.inrequest = session.agrlastRequest
    }

    hsRes.banks = Bankdeposit.getBanks()
    hsRes.iscanedit = recieveSectionPermission(ADEPEDIT)

    return hsRes
  }

  def deposits = {
    checkAccess(7)
    checkSectionAccess(11)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.agrlastRequest
      session.agrlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['did','bankcompany_id'])
      hsRes.inrequest.offset = requestService.getOffset()
      session.agrlastRequest = hsRes.inrequest
    }
    session.agrlastRequest.agrobject = 11

    hsRes.searchresult = new BankdepositSearch().csiSelectDeposits(hsRes.inrequest,20,hsRes.inrequest.offset)
    hsRes.valutas = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code.toLowerCase();map}
    hsRes.cursummas = hsRes.searchresult.records.inject([:]){map, deposit -> map[deposit.id]=agentKreditService.computeDepositCurSumma(deposit);map}

    return hsRes
  }

  def deposit = {
    checkAccess(7)
    checkSectionAccess(11)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId = requestService.getIntDef('id',0)
    hsRes.deposit = Bankdeposit.get(lId)
    if (!hsRes.deposit&&lId) {
      response.sendError(404)
      return
    }

    hsRes.bankcompanies = Company.findAllByIs_bank(1,[sort:'legalname',order:'asc'])
    hsRes.cursumma = agentKreditService.computeDepositCurSumma(hsRes.deposit)
    hsRes.iscanedit = recieveSectionPermission(ADEPEDIT)

    return hsRes
  }


  def updatedeposit = {
    checkAccess(7)
    if (!checkSectionAccess(11)) return
    if (!checkSectionPermission(ADEPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.deposit = Bankdeposit.get(lId)
    if (!hsRes.deposit&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['valuta_id','bank','modstatus','dtype','term'],null,['anumber','comment'],null,
                                    ['summa','rate','startsumma','startprocent'])
    hsRes.inrequest.adate = requestService.getDate('adate')
    hsRes.inrequest.enddate = requestService.getDate('enddate')
    hsRes.inrequest.startsaldodate = requestService.getDate('startsaldodate')

    if(!hsRes.deposit&&!hsRes.inrequest.bank)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.adate)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.rate)
      hsRes.result.errorcode<<4
    if(hsRes.inrequest.dtype==1&&!hsRes.inrequest.enddate)
      hsRes.result.errorcode<<5

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.deposit = new Bankdeposit(bank:hsRes.inrequest.bank)
        hsRes.result.deposit = hsRes.deposit.setData(hsRes.inrequest).csiSetAdmin(session.user.id).updateModstatus(hsRes.inrequest.modstatus).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Agreement/updatedeposit\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def depositbodypayments = {
    checkAccess(7)
    checkSectionAccess(11)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.deposit = Bankdeposit.get(requestService.getIntDef('id',0))
    if (!hsRes.deposit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payrequests = Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeInListAndModstatusGreaterThanAndIs_dop(11,hsRes.deposit.id,[1,2],-1,0,[sort:'paydate',order:'desc'])

    return hsRes
  }

  def depositpercpayments = {
    checkAccess(7)
    checkSectionAccess(11)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.deposit = Bankdeposit.get(requestService.getIntDef('id',0))
    if (!hsRes.deposit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payrequests = Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeAndModstatusGreaterThanAndIs_dop(11,hsRes.deposit.id,2,-1,1,[sort:'paydate',order:'desc'])

    return hsRes
  }

  def deposithistory = {
    checkAccess(7)
    checkSectionAccess(11)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.deposit = Bankdeposit.get(requestService.getIntDef('id',0))
    if (!hsRes.deposit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.history = new BankdeposithistSearch().csiFindDepositHistory(hsRes.deposit.id)

    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Bankdeposit <<<///////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Finlizing >>>/////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def finlizingfilter = {
    checkAccess(7)
    checkSectionAccess(12)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails){
      hsRes.inrequest = session.agrlastRequest
    }

    hsRes.iscanedit = recieveSectionPermission(AFLEDIT)

    return hsRes
  }

  def finlizings = {
    checkAccess(7)
    checkSectionAccess(12)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.agrlastRequest
      session.agrlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['flid'],null,['company_name'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.offset = requestService.getOffset()
      session.agrlastRequest = hsRes.inrequest
    }
    session.agrlastRequest.agrobject = 12

    hsRes.searchresult = new FinlizingSearch().csiSelectFLizings(hsRes.inrequest,session.user.group.visualgroup_id,20,hsRes.inrequest.offset)
    hsRes.summaries = hsRes.searchresult.records.inject([:]){map, flizing -> map[flizing.id]=agentKreditService.computeFinLizingBalance(flizing);map}

    return hsRes
  }

  def finlizing = {
    checkAccess(7)
    checkSectionAccess(12)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId = requestService.getIntDef('id',0)
    hsRes.flizing = Finlizing.get(lId)
    if (!hsRes.flizing&&lId) {
      response.sendError(404)
      return
    }

    hsRes.fldatel = Company.get(hsRes.flizing?.fldatel)?.name
    hsRes.flpoluchatel = Company.get(hsRes.flizing?.flpoluchatel)?.name
    hsRes.flbank = Company.get(hsRes.flizing?.flbank)?.name
    hsRes.summary = agentKreditService.computeFinLizingBalance(hsRes.flizing)
    hsRes.responsiblies = new UserpersSearch().csiFindByAccessrigth(AFLEDIT)
    hsRes.iscanedit = recieveSectionPermission(AFLEDIT)
    hsRes.isCanRestore = hsRes.flizing?.modstatus==0&&hsRes.flizing?.enddate>=new Date().clearTime()

    return hsRes
  }


  def updatefinlizing = {
    checkAccess(7)
    if (!checkSectionAccess(12)) return
    if (!checkSectionPermission(AFLEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.flizing = Finlizing.get(lId)
    if (!hsRes.flizing&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['modstatus'],['responsible'],['anumber','comment','fldatel','flpoluchatel','flbank','description'],null,['summa','rate'])
    hsRes.inrequest.adate = requestService.getDate('adate')
    hsRes.inrequest.enddate = requestService.getDate('enddate')

    if (!hsRes.flizing){
      if(!hsRes.inrequest.fldatel)
        hsRes.result.errorcode<<1
      else if(!Company.findByNameOrInn(hsRes.inrequest.fldatel,hsRes.inrequest.fldatel))
        hsRes.result.errorcode<<2
      else if(Company.findAllByNameOrInn(hsRes.inrequest.fldatel,hsRes.inrequest.fldatel).size()>1)
        hsRes.result.errorcode<<3
      if(!hsRes.inrequest.flpoluchatel)
        hsRes.result.errorcode<<4
      else if(!Company.findByNameOrInn(hsRes.inrequest.flpoluchatel,hsRes.inrequest.flpoluchatel))
        hsRes.result.errorcode<<5
      else if(Company.findAllByNameOrInn(hsRes.inrequest.flpoluchatel,hsRes.inrequest.flpoluchatel).size()>1)
        hsRes.result.errorcode<<6
      if(!hsRes.inrequest.flbank)
        hsRes.result.errorcode<<7
      else if(!Company.findByNameOrInn(hsRes.inrequest.flbank,hsRes.inrequest.flbank))
        hsRes.result.errorcode<<8
      else if(Company.findAllByNameOrInn(hsRes.inrequest.flbank,hsRes.inrequest.flbank).size()>1)
        hsRes.result.errorcode<<9
      if(!hsRes.inrequest.enddate)
        hsRes.result.errorcode<<11
      if(!hsRes.inrequest.summa)
        hsRes.result.errorcode<<12
      if(!hsRes.inrequest.rate)
        hsRes.result.errorcode<<13
    }
    if(!hsRes.inrequest.adate)
      hsRes.result.errorcode<<10
    if(!hsRes.inrequest.anumber)
      hsRes.result.errorcode<<14

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.flizing = new Finlizing().setBaseData(hsRes.inrequest)
        hsRes.result.flizing = hsRes.flizing.setData(hsRes.inrequest).csiSetAdmin(session.user.id).updateModstatus(hsRes.inrequest.modstatus).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Agreement/updatefinlizing\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def finlizingpayments = {
    checkAccess(7)
    checkSectionAccess(12)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.flizing = Finlizing.get(requestService.getIntDef('id',0))
    if (!hsRes.flizing) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payrequests = Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeInListAndModstatusGreaterThan(12,hsRes.flizing.id,[1,2],-1,[sort:'paydate',order:'desc'])

    return hsRes
  }

  def finlizingbalance = {
    checkAccess(7)
    checkSectionAccess(12)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.flizing = Finlizing.get(requestService.getIntDef('id',0))
    if (!hsRes.flizing) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.periods = Finlizperiod.findAllByFinlizing_id(hsRes.flizing.id,[sort:'fmonth',order:'asc'])
    hsRes.payrequests = hsRes.periods.inject([:]){map, period -> map[period.id]=Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeAndModstatusGreaterThanAndIs_dopAndPlatperiod(12,period.finlizing_id,1,-1,0,String.format('%tm.%<tY',period.fmonth)).sum{ it.summa }?:0.0g;map}
    hsRes.refunds = hsRes.periods.inject([:]){map, period -> map[period.id]=Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeInListAndModstatusGreaterThanAndIs_dopAndPlatperiod(12,period.finlizing_id,[1,2],-1,1,String.format('%tm.%<tY',period.fmonth)).sum{ it.paytype==1?it.summa:-it.summa }?:0.0g;map}
    hsRes.balance = 0.0g
    hsRes.percent = 0.0g
    hsRes.bodydebt = hsRes.flizing.summa
    hsRes.iscanedit = recieveSectionPermission(AFLEDIT)

    return hsRes
  }

  def finlizingperiod = {
    checkAccess(7)
    checkSectionAccess(12)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.flizing = Finlizing.get(requestService.getIntDef('flizing_id',0))
    if (!hsRes.flizing) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.flperiod = Finlizperiod.get(requestService.getIntDef('id',0))
    hsRes.iscanedit = recieveSectionPermission(AFLEDIT)

    return hsRes
  }

  def addfinlizingperiod = {
    checkAccess(7)
    if (!checkSectionAccess(12)) return
    if (!checkSectionPermission(AFLEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    def lId = requestService.getLongDef('id',0)
    hsRes.flizing = Finlizing.get(requestService.getIntDef('flizing_id',0))
    hsRes.flperiod = Finlizperiod.get(lId)
    if (!hsRes.flizing||(!hsRes.flperiod&&lId)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,null,null,['summa','compensation','procent','body','returnsumma'])
    hsRes.fmonth = requestService.getRaw('fmonth')

    if (!hsRes.flperiod){
      if(!hsRes.fmonth)
        hsRes.result.errorcode<<1
      else if(Finlizperiod.findByFmonthAndFinlizing_id(hsRes.fmonth,hsRes.flizing.id))
        hsRes.result.errorcode<<2
    }

    if(!hsRes.result.errorcode){
      try {
        if(!hsRes.flperiod) hsRes.flperiod = new Finlizperiod(finlizing_id:hsRes.flizing.id,fmonth:hsRes.fmonth)
        hsRes.flperiod.setData(hsRes.inrequest).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addfinlizingperiod\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletefinlizingperiod = {
    checkAccess(7)
    if (!checkSectionAccess(12)) return
    if (!checkSectionPermission(AFLEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    try {
      Finlizperiod.findByFinlizing_idAndId(requestService.getLongDef('flizing_id',0),requestService.getLongDef('id',0))?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletefinlizingperiod\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def finlizingdopagrs = {
    checkAccess(7)
    checkSectionAccess(12)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.flizing = Finlizing.get(requestService.getIntDef('id',0))
    if (!hsRes.flizing) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.finlizingdopagrs = Finlizingdopagr.findAllByFinlizing_id(hsRes.flizing.id,[sort:'id',order:'desc'])
    hsRes.firstagrid = Finlizingdopagr.getMinId(hsRes.flizing.id)
    hsRes.iscanedit = recieveSectionPermission(AFLEDIT)

    return hsRes
  }

  def finlizingdopagr = {
    checkAccess(7)
    checkSectionAccess(12)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.flizing = Finlizing.get(requestService.getIntDef('flizing_id',0))
    if (!hsRes.flizing) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.finlizingdopagr = Finlizingdopagr.get(requestService.getIntDef('id',0))
    hsRes.flpoluchatel = Company.get(hsRes.finlizingdopagr?.flpoluchatel?:0)?.name
    hsRes.iscanedit = recieveSectionPermission(AFLEDIT)

    return hsRes
  }

  def addfinlizingdopagr = {
    checkAccess(7)
    if (!checkSectionAccess(12)) return
    if (!checkSectionPermission(AFLEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]
 
    hsRes+=requestService.getParams(['id'],null,['nomer','comment','flpoluchatel'],null,['summa','rate'])
    hsRes.inrequest.dsdate = requestService.getDate('finlizingdopagr_dsdate')
    hsRes.inrequest.startdate = requestService.getDate('finlizingdopagr_startdate')
    hsRes.inrequest.enddate = requestService.getDate('finlizingdopagr_enddate')

    hsRes.flizing = Finlizing.get(requestService.getLongDef('flizing_id',0))
    hsRes.finlizingdopagr = Finlizingdopagr.get(hsRes.inrequest.id)
    if (!hsRes.flizing||(!hsRes.finlizingdopagr&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.nomer)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.dsdate)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.startdate)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.enddate)
      hsRes.result.errorcode<<5
    if(!hsRes.inrequest.rate)
      hsRes.result.errorcode<<6
    if(!hsRes.inrequest.flpoluchatel)
      hsRes.result.errorcode<<7
    else if(!Company.findByNameOrInn(hsRes.inrequest.flpoluchatel,hsRes.inrequest.flpoluchatel))
      hsRes.result.errorcode<<8
    else if(Company.findAllByNameOrInn(hsRes.inrequest.flpoluchatel,hsRes.inrequest.flpoluchatel).size()>1)
      hsRes.result.errorcode<<9

    if(!hsRes.result.errorcode){
      try {
        if (!hsRes.inrequest.id) hsRes.finlizingdopagr = new Finlizingdopagr(finlizing_id:hsRes.flizing.id)
        hsRes.finlizingdopagr.setData(hsRes.inrequest).csiSetAdmin(session.user.id).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addfinlizingdopagr\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletefinlizingdopagr = {
    checkAccess(7)
    if (!checkSectionAccess(12)) return
    if (!checkSectionPermission(AFLEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.flizing = Finlizing.get(requestService.getLongDef('flizing_id',0))
    if (!hsRes.flizing||Finlizingdopagr.getMinId(hsRes.flizing?.id)==requestService.getLongDef('id',0)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Finlizingdopagr.findByFinlizing_idAndId(hsRes.flizing.id,requestService.getLongDef('id',0))?.csiSetAdmin(session.user.id)?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deletefinlizingdopagr\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def finlizinghistory = {
    checkAccess(7)
    checkSectionAccess(12)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.flizing = Finlizing.get(requestService.getIntDef('id',0))
    if (!hsRes.flizing) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.history = new FinlizinghistSearch().csiFindFLizingHistory(hsRes.flizing.id)

    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Finlizing <<</////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Indeposit >>>/////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def indepositfilter = {
    checkAccess(7)
    checkSectionAccess(13)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails){
      hsRes.inrequest = session.agrlastRequest
    }

    hsRes.clients = Client.findAllByModstatus(1)
    hsRes.iscanedit = recieveSectionPermission(ADEPEDIT)

    return hsRes
  }

  def indeposits = {
    checkAccess(7)
    checkSectionAccess(13)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    if (session.agrlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.agrlastRequest
      session.agrlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['indid','client_id','aclass'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',0)
      hsRes.inrequest.offset = requestService.getOffset()
      session.agrlastRequest = hsRes.inrequest
    }
    session.agrlastRequest.agrobject = 13

    hsRes.searchresult = new IndepositSearch().csiSelectDeposits(hsRes.inrequest,20,hsRes.inrequest.offset)
    hsRes.valutas = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code.toLowerCase();map}
    hsRes.debts = hsRes.searchresult.records.inject([:]){map, deposit -> map[deposit.id]=agentKreditService.computeIndepositBody(deposit);map}

    return hsRes
  }

  def indeposit = {
    checkAccess(7)
    checkSectionAccess(13)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    def lId = requestService.getIntDef('id',0)
    hsRes.deposit = Indeposit.get(lId)
    if (!hsRes.deposit&&lId) {
      response.sendError(404)
      return
    }

    hsRes.clients = Client.findAllByModstatus(1)
    hsRes.client = Client.get(hsRes.deposit?.client_id?:0)
    hsRes.bodydebt = agentKreditService.computeIndepositBody(hsRes.deposit)
    hsRes.percentdebt = agentKreditService.computeIndepositPercent(hsRes.deposit)
    hsRes.iscanedit = recieveSectionPermission(ADEPEDIT)
    hsRes.isCanRestore = (hsRes.deposit?.enddate?:new Date().clearTime())>=new Date().clearTime()&&hsRes.deposit?.modstatus==0

    return hsRes
  }

  def updateindeposit = {
    checkAccess(7)
    if (!checkSectionAccess(13)) return
    if (!checkSectionPermission(ADEPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.deposit = Indeposit.get(lId)
    if (!hsRes.deposit&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['valuta_id','client_id','modstatus','atype','aclass'],null,['anumber','comment'],null,
                                    ['summa','rate','startsaldo','comrate'])
    hsRes.inrequest.adate = requestService.getDate('adate')
    hsRes.inrequest.enddate = requestService.getDate('enddate')

    if(!hsRes.deposit){
      if(!hsRes.inrequest.client_id)
        hsRes.result.errorcode<<1
      if(!hsRes.inrequest.adate)
        hsRes.result.errorcode<<2
      if(!hsRes.inrequest.summa)
        hsRes.result.errorcode<<3
      if(!hsRes.inrequest.rate)
        hsRes.result.errorcode<<4
      if(hsRes.inrequest.atype==1&&!hsRes.inrequest.enddate)
        hsRes.result.errorcode<<5
      if((hsRes.inrequest.comrate?:0)<0)
        hsRes.result.errorcode<<6
    }

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.deposit = new Indeposit().setBaseData(hsRes.inrequest)
        hsRes.result.deposit = hsRes.deposit.setData(hsRes.inrequest).csiSetAdmin(session.user.id).updateModstatus(hsRes.inrequest.modstatus).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Agreement/updateindeposit\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def indepositpayments = {
    checkAccess(7)
    checkSectionAccess(13)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.deposit = Indeposit.get(requestService.getIntDef('id',0))
    if (!hsRes.deposit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payrequests = Payrequest.findAllByAgreementtype_idAndAgreement_idAndPaytypeInListAndModstatusGreaterThan(13,hsRes.deposit.id,[1,2],-1,[sort:'paydate',order:'asc'])
    hsRes.payments = hsRes.payrequests.inject([:]){map, prequest -> map[prequest.id]=Payment.findByPayrequest_id(prequest.id)?.id;map}
    hsRes.bodydebt = hsRes.deposit.startsaldo

    return hsRes
  }

  def indepositcashpayments = {
    checkAccess(7)
    checkSectionAccess(13)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.deposit = Indeposit.get(requestService.getIntDef('id',0))
    if (!hsRes.deposit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.cashpayments = Cash.findAllByIndeposit_id(hsRes.deposit.id,[sort:'operationdate',order:'asc'])
    hsRes.bodydebt = hsRes.deposit.startsaldo

    return hsRes
  }

  def indepositprjoperations = {
    checkAccess(7)
    checkSectionAccess(13)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.deposit = Indeposit.get(requestService.getIntDef('id',0))
    if (!hsRes.deposit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.project_id = requestService.getIntDef('project_id',0)
    hsRes.operations = new IndepositprojectSearch().csiSelectOperations([indeposit_id:hsRes.deposit.id,project_id:hsRes.project_id],20,requestService.getOffset())
    hsRes.projects = new Project().csiSearchIndepositProjects(hsRes.deposit.id)
    hsRes.iscanedit = recieveSectionPermission(ADEPEDIT)

    return hsRes
  }

  def indepositprjoperation = {
    checkAccess(7)
    checkSectionAccess(13)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.deposit = Indeposit.get(requestService.getIntDef('deposit_id',0))
    if (!hsRes.deposit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.projects = Project.list()
    hsRes.iscanedit = recieveSectionPermission(ADEPEDIT)

    return hsRes
  }

  def addindepositprjoperation = {
    checkAccess(7)
    if (!checkSectionAccess(13)) return
    if (!checkSectionPermission(ADEPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]

    hsRes.deposit = Indeposit.get(requestService.getIntDef('deposit_id',0))
    if (!hsRes.deposit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['project_from','project_to','deposit_id'],null,null,null,['summa'])
    hsRes.inrequest.operationdate = requestService.getDate('indepositprjoperation_operationdate')

    if(!hsRes.inrequest.project_from)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.project_to)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<3
    else if(hsRes.inrequest.project_from&&Project.get(hsRes.inrequest.project_from)?.computeIndepositSaldo(hsRes.deposit.id)<hsRes.inrequest.summa)
      hsRes.result.errorcode<<5
    if(!hsRes.inrequest.operationdate)
      hsRes.result.errorcode<<4

    if(!hsRes.result.errorcode){
      try {
        paymentService.createInnerProjectOperation(hsRes.inrequest)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addindepositprjoperation\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deleteindepositprjoperation = {
    checkAccess(7)
    if (!checkSectionAccess(13)) return
    if (!checkSectionPermission(ADEPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.prjoperation = Indepositproject.findByIndeposit_idAndId(requestService.getIntDef('deposit_id',0),requestService.getLongDef('id',0))
    if (!hsRes.prjoperation||hsRes.prjoperation.is_transfer==0||hsRes.prjoperation.related_id==0) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.prjoperation.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deleteindepositprjoperation\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def indepositprojects = {
    checkAccess(7)
    checkSectionAccess(13)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.deposit = Indeposit.get(requestService.getIntDef('id',0))
    if (!hsRes.deposit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.projects = new Project().csiSearchIndepositProjects(hsRes.deposit.id)

    return hsRes
  }

  def indepositdopagrs = {
    checkAccess(7)
    checkSectionAccess(13)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.deposit = Indeposit.get(requestService.getIntDef('id',0))
    if (!hsRes.deposit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.indepositdopagrs = Indepositdopagr.findAllByIndeposit_id(hsRes.deposit.id,[sort:'id',order:'desc'])
    hsRes.firstagrid = Indepositdopagr.getMinId(hsRes.deposit.id)
    hsRes.iscanedit = recieveSectionPermission(ADEPEDIT)

    return hsRes
  }

  def indepositdopagr = {
    checkAccess(7)
    checkSectionAccess(13)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.deposit = Indeposit.get(requestService.getIntDef('deposit_id',0))
    if (!hsRes.deposit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.indepositdopagr = Indepositdopagr.get(requestService.getIntDef('id',0))
    hsRes.iscanedit = recieveSectionPermission(ADEPEDIT)

    return hsRes
  }

  def addindepositdopagr = {
    checkAccess(7)
    if (!checkSectionAccess(13)) return
    if (!checkSectionPermission(ADEPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7
    hsRes.result=[errorcode:[]]
 
    hsRes+=requestService.getParams(['id','atype'],null,['nomer','comment'],null,['summa','rate','comrate'])
    hsRes.inrequest.dsdate = requestService.getDate('indepositdopagr_dsdate')
    hsRes.inrequest.startdate = requestService.getDate('indepositdopagr_startdate')
    hsRes.inrequest.enddate = requestService.getDate('indepositdopagr_enddate')

    hsRes.deposit = Indeposit.get(requestService.getIntDef('deposit_id',0))
    hsRes.indepositdopagr = Indepositdopagr.get(hsRes.inrequest.id)
    if (!hsRes.deposit||(!hsRes.indepositdopagr&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.rate)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.dsdate)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.startdate)
      hsRes.result.errorcode<<4
    if(hsRes.inrequest.atype==1&&!hsRes.inrequest.enddate)
      hsRes.result.errorcode<<5
    if((hsRes.inrequest.comrate?:0)<0)
      hsRes.result.errorcode<<6

    if(!hsRes.result.errorcode){
      try {
        if (!hsRes.inrequest.id) hsRes.indepositdopagr = new Indepositdopagr(indeposit_id:hsRes.deposit.id)
        hsRes.indepositdopagr.setData(hsRes.inrequest).csiSetAdmin(session.user.id).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Agreement/addindepositdopagr\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deleteindepositdopagr = {
    checkAccess(7)
    if (!checkSectionAccess(13)) return
    if (!checkSectionPermission(ADEPEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.deposit = Indeposit.get(requestService.getIntDef('deposit_id',0))
    if (!hsRes.deposit||Indepositdopagr.getMinId(hsRes.deposit?.id)==requestService.getLongDef('id',0)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Indepositdopagr.findByIndeposit_idAndId(hsRes.deposit.id,requestService.getLongDef('id',0))?.csiSetAdmin(session.user.id)?.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Agreement/deleteindepositdopagr\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def indeposithistory = {
    checkAccess(7)
    checkSectionAccess(13)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 7

    hsRes.deposit = Indeposit.get(requestService.getIntDef('id',0))
    if (!hsRes.deposit) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.history = new IndeposithistSearch().csiFindDepositHistory(hsRes.deposit.id)

    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Indeposit <<</////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def showscan = {
    checkAccess(7)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)

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
}
