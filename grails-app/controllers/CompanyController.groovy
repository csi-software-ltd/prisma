import grails.converters.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import pl.touk.excel.export.WebXlsxExporter
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

class CompanyController {
  def requestService
  def docexportService
  def agentKreditService

  final String CINCERT = 'is_companyinsert'
  final String CEDIT = 'is_companyedit'
  final String CCARD = 'is_companycard'
  final String CACCOUNT = 'is_companyaccount'
  final String CACCEDIT = 'is_companyaccountedit'
  final String CSTAFF = 'is_companystaff'
  final String CSTEDIT = 'is_companystaffedit'
  final String RCARD = 'is_companyrequisit'
  final String CARENDA = 'is_arenda'
  final String CAGR = 'is_agr'
  final String CKREDIT = 'is_kredit'
  final String CKRREAL = 'is_realkredit'
  final String CLIZING = 'is_lizing'
  final String CAGENT = 'is_agent'
  final String CCESSION = 'is_cession'
  final String CTRADE = 'is_trade'
  final String CLICENSE = 'is_license'
  final String CPAYMENT = 'is_payment'
  final String CPAYPLAN = 'is_payplan'
  final String CPRJ = 'is_project'
  final String CPAYTAG = 'is_paytag'
  final String CLOAN = 'is_loan'
  final String CCGROUP = 'is_cgroup'

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

  private Boolean checkSectionAccess(String sField) {
    checkSectionAccess([sField])
  }

  private Boolean checkSectionAccess(lsField) {
    if(!lsField.find{ session.user.group?."$it" }) {
      response.sendError(403)
      return false;
    }
    return true
  }

  private Boolean recieveSectionAccess(String sField) {
    recieveSectionAccess([sField])
  }

  private Boolean recieveSectionAccess(lsField) {
    lsField.find{ session.user.group?."$it" } as Boolean
  }

  private def getPermissionList() {
    def permissions = [:]
    permissions.iscanedit = recieveSectionAccess([CINCERT,CEDIT])
    permissions.ishistory = recieveSectionAccess([CINCERT,CEDIT])
    permissions.iscard = recieveSectionAccess(CCARD)
    permissions.isreqcard = recieveSectionAccess(RCARD)
    permissions.isaccount = recieveSectionAccess(CACCOUNT)
    permissions.isstaff = recieveSectionAccess(CSTAFF)
    permissions.isarenda = recieveSectionAccess(CARENDA)
    permissions.isagr = recieveSectionAccess([CKREDIT,CLIZING,CCESSION,CTRADE,CLOAN])
    permissions.islicense = recieveSectionAccess(CLICENSE)
    permissions.ispayment = recieveSectionAccess(CPAYMENT)
    permissions.ispayplan = recieveSectionAccess(CPAYPLAN)
    permissions.isproject = recieveSectionAccess(CPRJ)
    permissions.istag = recieveSectionAccess(CPAYTAG)
    permissions.iscgroup = recieveSectionAccess(CCGROUP)
    permissions
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Main >>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def index = {
    checkAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails&&session.companylastRequest){
      session.companylastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.companylastRequest
    } else {
      hsRes.inrequest=[:]
      hsRes.inrequest.is_holding = requestService.getIntDef('is_holding',1)
    }

    hsRes.colors = Color.list()
    hsRes.projects = Project.list()
    hsRes.taxoptions = Taxoption.list()
    hsRes.responsiblies = User.findAllByDepartment_idAndModstatus(10,1)
    hsRes.iscanincert = recieveSectionAccess(CINCERT)

    return hsRes
  }

  def list = {
    checkAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    if (session.companylastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.companylastRequest
      session.companylastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['is_holding','project_id','taxoption_id','bankaccount','is_license','www','colorfill'],
                                      ['responsible'],['cname','okved','bankname','gd','district','color'])
      hsRes.inrequest.offset = requestService.getOffset()
      session.companylastRequest = [:]
      session.companylastRequest = hsRes.inrequest
    }

    hsRes.requests = new CompanySearch().csiSelectCompanies(0,hsRes.inrequest.cname?:'',hsRes.inrequest.bankname?:'',
                                                            hsRes.inrequest.okved?:'',hsRes.inrequest.project_id?:0,
                                                            hsRes.inrequest.taxoption_id?:0,hsRes.inrequest.bankaccount?:0,
                                                            hsRes.inrequest.is_holding?:0,hsRes.inrequest.gd?:'',
                                                            hsRes.inrequest.district?:'',hsRes.inrequest.responsible?:0l,
                                                            hsRes.inrequest.is_license?:0,hsRes.inrequest.www?:0,
                                                            hsRes.inrequest.color?:'',hsRes.inrequest.colorfill?:0,
                                                            session.user.group.visualgroup_id,20,hsRes.inrequest.offset)
    hsRes.accounts = hsRes.requests.records.inject([:]){map, company -> map[company.id]=new BankaccountSearch().csiFindAccounts(company.id,1,1);map}
    hsRes.colors = Color.list()

    return hsRes
  }

  def setcolor = {
    checkAccess(5)
    if (!checkSectionAccess([CEDIT,CINCERT])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    hsRes.company = Company.get(requestService.getLongDef('id',0))
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.company.csiSetColor(requestService.getStr('color')).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Company/setcolor\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def setcolorfill = {
    checkAccess(5)
    if (!checkSectionAccess([CEDIT,CINCERT])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    hsRes.company = Company.get(requestService.getLongDef('id',0))
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.company.csiSetColorFill(requestService.getIntDef('colorfill',0)).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Company/setcolorfill\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def bikvalidate={
    requestService.init(this)
    def sBik = requestService.getStr('bik')
    if(sBik){
      def oBank = Bank.findByIdIlike(sBik)
      render (oBank?oBank.name+';'+oBank.coraccount:"Банк не найден;Банк не найден")
      return
    }
    render ';'
  }

  def banknamevalidate={
    requestService.init(this)
    def sBank = requestService.getStr('bankname')
    if(sBank){
      def oBank = Bank.findByNameIlike(sBank)
      render (oBank?oBank.id+';'+oBank.coraccount:"Банк не найден;Банк не найден")
      return
    }
    render ';'
  }  

  def detail = {
    checkAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    def lId=requestService.getIntDef('id',0)
    hsRes.company = Company.get(lId)
    if (!lId&&!checkSectionAccess(CINCERT)) return
    if (!hsRes.company&&lId) {
      response.sendError(404)
      return
    }
    hsRes+=requestService.getParams(['is_holding'],null,['inn','kpp','name'])
    hsRes.saldo = Bankaccount.findAllByCompany_idAndModstatus(hsRes.company?.id,1)?.sum{it.saldo}?:0
    hsRes.permissions = getPermissionList()
    
    hsRes.compokved = new CompokvedSearch().csiFindCompokved(lId,-100)
    hsRes.account = new BankaccountSearch().csiFindAccounts(lId,1)
    hsRes.responsiblies = User.findAllByDepartment_idAndModstatus(10,1)
    hsRes.outsources = Outsource.findAllByModstatus(1)
    hsRes.projects = Project.findAllByIdInList(Cproject.findAllByCompany_id(hsRes.company?.id?:0).collect{it.project_id}?:[])
    hsRes.clients = Client.findAllByModstatus(1)
    hsRes.is_visual = session.user.group.visualgroup_id==0
    hsRes.is_holding = (hsRes.company?hsRes.company.is_holding:hsRes.inrequest.is_holding==-1?0:session.companylastRequest?.is_holding?:0)&&(hsRes.company?(hsRes.is_visual||session.user.group.visualgroup_id==hsRes.company.visualgroup_id):true)

    return hsRes
  }

  def update = {
    checkAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getLongDef('id',0)
    if (!lId&&!checkSectionAccess(CINCERT)) return
    else if (lId&&!checkSectionAccess([CEDIT,CINCERT])) return
    hsRes.company = Company.get(lId)
    if (!hsRes.company&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['is_holding','is_subarenda','is_sublizing','taxoption_id','is_bank','capitalsecure',
                                     'is_dirchange','activitystatus_id','tagproject','tagclient','tagexpense','capitalpaid',
                                     'outsource_id','outsourceprice','is_req','is_pic','is_ldoc'],
                                     ['capital','responsible1','responsible2','buycost','salecost'],
                                     ['cname','legalname','inn','ogrn','okato','okpo','regauthority','city','legaladr',
                                     'postadr','emailpassword','tel','smstel','email','taxinspection_id','comment','kpp',
                                     'smstel2','oktmo','www','okogu','pfrfreg','fssreg','tagcomment'])
    hsRes.inrequest.opendate = requestService.getDate('opendate')
    hsRes.inrequest.namedate = requestService.getDate('namedate')
    hsRes.inrequest.adrdate = requestService.getDate('adrdate')
    hsRes.inrequest.capitaldate = requestService.getDate('capitaldate')
    hsRes.inrequest.reregdate = requestService.getDate('reregdate')
    hsRes.inrequest.reqdate = requestService.getDate('reqdate')
    hsRes.inrequest.picdate = requestService.getDate('picdate')
    hsRes.inrequest.ldocdate = requestService.getDate('ldocdate')
    hsRes.inrequest.cgroup_id = recieveSectionAccess(CCGROUP)?requestService.getIntDef('cgroup_id',0):hsRes.company?.cgroup_id?:0
    hsRes.inrequest.visualgroup_id = session.user.group.visualgroup_id==0?requestService.getIntDef('visualgroup_id',0):hsRes.company?.visualgroup_id?:hsRes.inrequest.is_holding?session.user.group.visualgroup_id:1

    if(!hsRes.inrequest.cname)
      hsRes.result.errorcode<<1
    else if(Company.findByNameAndIdNotEqual(hsRes.inrequest.cname,hsRes.company?.id?:0))
      hsRes.result.errorcode<<12
    if(!hsRes.inrequest.inn)
      hsRes.result.errorcode<<2
    else if(!hsRes.inrequest.inn.matches('(\\d{10})|(\\d{12})'))
      hsRes.result.errorcode<<5
    else if(Company.findByInnAndIdNotEqual(hsRes.inrequest.inn,hsRes.company?.id?:0))
      hsRes.result.errorcode<<9
    if(hsRes.inrequest.is_holding&&!hsRes.inrequest.ogrn)
      hsRes.result.errorcode<<10
    else if(hsRes.inrequest.ogrn&&!hsRes.inrequest.ogrn.matches('\\d*'))
      hsRes.result.errorcode<<3
    else if(hsRes.inrequest.ogrn&&Company.findByOgrnAndIdNotEqual(hsRes.inrequest.ogrn,hsRes.company?.id?:0))
      hsRes.result.errorcode<<11
    if(hsRes.inrequest.email&&!Tools.checkEmailString(hsRes.inrequest.email))
      hsRes.result.errorcode<<4
    if(hsRes.inrequest.is_holding&&!hsRes.inrequest.kpp)
      hsRes.result.errorcode<<6
    else if(hsRes.inrequest.kpp&&!hsRes.inrequest.kpp.matches('\\d{9}'))
      hsRes.result.errorcode<<7
    if(!hsRes.inrequest.opendate)
      hsRes.result.errorcode<<8

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.company = new Company()
        hsRes.result.company = hsRes.company.setData(hsRes.inrequest).computeModstatus().csiSetAdmin(session.user.id).csiSetTagData(hsRes.inrequest).csiSetOutsource(session.user.confaccess!=2?hsRes.company:hsRes.inrequest).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Company/update\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def projects = {
    checkAccess(5)
    if (!checkSectionAccess(CPRJ)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    def lId=requestService.getIntDef('id',0)
    hsRes.company = Company.get(lId)
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.projects = Project.findAllByIdInList(Cproject.findAllByCompany_id(hsRes.company.id).collect{it.project_id}?:[])
    hsRes.iscanaddproject = Project.findAllByIdNotInList(Cproject.findAllByCompany_id(hsRes.company.id).collect{it.project_id}?:[0])?true:false

    return hsRes
  }

  def project = {
    checkAccess(5)
    if (!checkSectionAccess(CPRJ)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    def lId=requestService.getIntDef('id',0)
    hsRes.company = Company.get(lId)
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.projects = Project.findAllByIdNotInList(Cproject.findAllByCompany_id(hsRes.company.id).collect{it.project_id}?:[0])

    return hsRes
  }

  def addtoproject = {
    checkAccess(5)
    if (!checkSectionAccess(CPRJ)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getLongDef('company_id',0)
    hsRes+=requestService.getParams(['project_id'])

    hsRes.company = Company.get(lId)
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.project_id||Cproject.findByCompany_idAndProject_id(hsRes.company.id,hsRes.inrequest.project_id))
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        hsRes.company.addtoproject(hsRes.inrequest.project_id).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Company/addtoproject\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def removefromproject = {
    checkAccess(5)
    if (!checkSectionAccess(CPRJ)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    def lId = requestService.getLongDef('company_id',0)
    hsRes+=requestService.getParams(['id'])

    hsRes.company = Company.get(lId)
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.company.removefromproject(hsRes.inrequest.id).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Company/removefromproject\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def personal = {
    checkAccess(5)
    if (!checkSectionAccess(CSTAFF)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    def lId=requestService.getIntDef('id',0)
    hsRes.company = Company.get(lId)
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.modstatus = requestService.getIntDef('modstatus',0)
    hsRes.positions = Position.list().inject([:]){map, position -> map[position.id]=position.name;map}
    hsRes.iscanedit = recieveSectionAccess(CSTEDIT)
    if(hsRes.modstatus==2){
      hsRes.history = new CompersSearch().csiFindCompersHistory(hsRes.company.id)
      render(view: "personal_history", model: hsRes)
      return
    } else if(hsRes.modstatus==3) {
      hsRes.founders = new CompholderSearch().csiFindCompholdersByCompanyIdAndModstatus(hsRes.company.id,-100)
      render(view: "founders", model: hsRes)
      return
    } else if(hsRes.modstatus==4) {
      hsRes.vacancies = new CompvacancySearch().csiFindCompvacancyByCompanyId(hsRes.company.id)
      render(view: "vacancies", model: hsRes)
      return
    } else
      hsRes.personal = new CompersSearch().csiFindCompers(hsRes.company.id,'',hsRes.modstatus)

    return hsRes
  }

  def employee = {
    checkAccess(5)
    if (!checkSectionAccess(CSTAFF)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    def lId=requestService.getIntDef('company_id',0)
    hsRes.company = Company.get(lId)
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.employee = Compers.get(requestService.getIntDef('id',0))
    hsRes.pers = Pers.get(hsRes.employee?.pers_id?:0)
    hsRes.compositions = Composition.list()
    hsRes.projects = Project.findAllByIdInList(Cproject.findAllByCompany_id(hsRes.company.id?:0).collect{it.project_id}?:[])
    hsRes.expensetype = new ExpensetypeSearch().csiGetFullList()
    hsRes.iscanedit = recieveSectionAccess(CSTEDIT)
    hsRes.iscantag = recieveSectionAccess(CPAYTAG)

    return hsRes
  }

  def founder = {
    checkAccess(5)
    if (!checkSectionAccess(CSTAFF)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    def lId=requestService.getIntDef('company_id',0)
    hsRes.company = Company.get(lId)
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.founder = Compholder.get(requestService.getIntDef('id',0))
    hsRes.pers = Pers.get(hsRes.founder?.pers_id?:0)
    hsRes.holdcompany = Company.get(hsRes.founder?.holdcompany_id?:0)
    hsRes.iscanedit = recieveSectionAccess(CSTEDIT)

    return hsRes
  }

  def vacancy = {
    checkAccess(5)
    if (!checkSectionAccess(CSTAFF)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    hsRes.company = Company.get(requestService.getIntDef('company_id',0))
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.vacancy = Compvacancy.findByCompany_idAndId(hsRes.company.id,requestService.getIntDef('id',0))
    hsRes.compositions = Composition.list()
    hsRes.iscanedit = recieveSectionAccess(CSTEDIT)

    return hsRes
  }

  def addemployee = {
    checkAccess(5)
    if (!checkSectionAccess(CSTEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['salary','id','composition_id','gd_valid_years','tagproject','tagclient','tagexpensemain',
                                     'tagexpenseadd'],null,['comment','pers_name','tagcomment','industrywork','prevwork'])
    hsRes.inrequest.employee_jobstart = requestService.getDate('employee_jobstart')
    hsRes.inrequest.employee_jobend = requestService.getIntDef('modstatus',0)?requestService.getDate('employee_jobend'):null
    hsRes.inrequest.employee_gd_valid = requestService.getDate('employee_gd_valid')

    hsRes.company = Company.get(requestService.getLongDef('company_id',0))
    hsRes.employee = Compers.get(hsRes.inrequest.id)
    if (!hsRes.company||(!hsRes.employee&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.composition = Composition.get(hsRes.inrequest.composition_id)
    if(!hsRes.employee&&!hsRes.inrequest.pers_name)
      hsRes.result.errorcode<<1
    else if(!hsRes.employee&&!Pers.findByShortname(hsRes.inrequest.pers_name))
      hsRes.result.errorcode<<9
    else if(!hsRes.employee&&Pers.findAllByShortname(hsRes.inrequest.pers_name).size()>1)
      hsRes.result.errorcode<<10
    else if(Compers.findByCompany_idAndPers_idAndPosition_idAndModstatusAndIdNotEqual(hsRes.company.id,hsRes.employee?.pers_id?:Pers.findByShortname(hsRes.inrequest.pers_name)?.id?:0,hsRes.composition?.position_id?:0,1,hsRes.employee?.id?:0)?.id?:0)
      hsRes.result.errorcode<<6
    if(!hsRes.composition?.position_id)
      hsRes.result.errorcode<<2
    else if(Position.get(hsRes.composition?.position_id).type!=(hsRes.employee?Pers.get(hsRes.employee.pers_id).perstype:Pers.findByShortname(hsRes.inrequest.pers_name)?.perstype))
      hsRes.result.errorcode<<8
    if(!hsRes.inrequest.employee_jobstart)
      hsRes.result.errorcode<<3
    if(hsRes.composition?.position_id==1&&!hsRes.inrequest.employee_gd_valid&&!hsRes.inrequest.gd_valid_years)
      hsRes.result.errorcode<<4
    if(hsRes.inrequest.employee_jobend&&!hsRes.inrequest.comment)
      hsRes.result.errorcode<<5
    if(hsRes.composition?.position_id in 1..2&&!hsRes.inrequest.employee_jobend&&Compers.findByCompany_idAndPosition_idAndModstatusAndIdNotEqual(hsRes.company.id,hsRes.composition?.position_id?:0,1,hsRes.employee?.id))
      hsRes.result.errorcode<<7

    if(!hsRes.result.errorcode){
      try {
        if(!hsRes.employee) hsRes.employee = new Compers(pers_id:Pers.findByShortname(hsRes.inrequest.pers_name)?.id,company_id:hsRes.company.id)
        hsRes.employee.setData(hsRes.inrequest,session.user.confaccess).updateModstatus().csiSetAdmin(session.user.id).csiSetTagData(hsRes.inrequest).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Company/addemployee\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deleteemployee = {
    checkAccess(5)
    if (!checkSectionAccess(CSTEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    hsRes.employee = Compers.findByCompany_idAndId(requestService.getIntDef('company_id',0),requestService.getIntDef('id',0))
    if (hsRes.employee?.modstatus!=0) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.employee.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Company/deleteemployee\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def addfounder = {
    checkAccess(5)
    if (!checkSectionAccess(CSTEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getLongDef('company_id',0)
    hsRes+=requestService.getParams(['id','share','summa'],null,['comment','pers_name','company_name'])
    hsRes.inrequest.founder_startdate = requestService.getDate('founder_startdate')
    hsRes.inrequest.founder_enddate = requestService.getDate('founder_enddate')

    hsRes.company = Company.get(lId)
    hsRes.founder = Compholder.get(hsRes.inrequest.id)
    if (!hsRes.company||(!hsRes.founder&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.founder&&!hsRes.inrequest.pers_name&&!hsRes.inrequest.company_name)
      hsRes.result.errorcode<<1
    else if(!hsRes.founder&&hsRes.inrequest.pers_name&&hsRes.inrequest.company_name)
      hsRes.result.errorcode<<8
    else if (!hsRes.founder&&hsRes.inrequest.pers_name) {
      if(!Pers.findByShortname(hsRes.inrequest.pers_name))
        hsRes.result.errorcode<<2
      else if(!hsRes.founder&&Pers.findAllByShortname(hsRes.inrequest.pers_name).size()>1)
        hsRes.result.errorcode<<3
    } else if (!hsRes.founder&&hsRes.inrequest.company_name) {
      if(!Company.findByNameOrInn(hsRes.inrequest.company_name,hsRes.inrequest.company_name))
        hsRes.result.errorcode<<9
      else if(Company.findAllByNameOrInn(hsRes.inrequest.company_name,hsRes.inrequest.company_name).size()>1)
        hsRes.result.errorcode<<10
    }
    if(!hsRes.inrequest.founder_startdate)
      hsRes.result.errorcode<<4
    else if (hsRes.inrequest.founder_enddate&&hsRes.inrequest.founder_enddate<=hsRes.inrequest.founder_startdate)
      hsRes.result.errorcode<<5
    if(!hsRes.inrequest.share)
      hsRes.result.errorcode<<6
    else if(!hsRes.inrequest.founder_enddate&&(Compholder.findAllByCompany_idAndModstatusAndPers_idNotInList(hsRes.company.id,1,[0l,Pers.findByShortname(hsRes.inrequest.pers_name?:'')?.id?:hsRes.founder?.pers_id?:0l]).sum{it.share}?:0)+(Compholder.findAllByCompany_idAndModstatusAndHoldcompany_idNotInList(hsRes.company.id,1,[0,Company.findByNameOrInn(hsRes.inrequest.company_name?:'',hsRes.inrequest.company_name?:'')?.id?:hsRes.founder?.holdcompany_id?:0]).sum{it.share}?:0)+hsRes.inrequest.share>100)
      hsRes.result.errorcode<<7

    if(!hsRes.result.errorcode){
      try {
        if(!hsRes.founder) hsRes.founder = new Compholder(pers_id:Pers.findByShortname(hsRes.inrequest.pers_name?:'')?.id?:0,company_id:hsRes.company.id,holdcompany_id:Company.findByNameOrInn(hsRes.inrequest.company_name?:'',hsRes.inrequest.company_name?:'')?.id?:0)
        hsRes.founder.setData(hsRes.inrequest).csiSetAdmin(session.user.id).updateModstatus().save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Company/addfounder\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletefounder = {
    checkAccess(5)
    if (!checkSectionAccess(CSTEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    hsRes.founder = Compholder.findByCompany_idAndId(requestService.getIntDef('company_id',0),requestService.getIntDef('id',0))
    if (hsRes.founder?.modstatus!=0) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.founder.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Company/deletefounder\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def addvacancy = {
    checkAccess(5)
    if (!checkSectionAccess(CSTEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5
    hsRes.result=[errorcode:[]]
 
    hsRes+=requestService.getParams(['id','salary','numbers','composition_id'])

    hsRes.company = Company.get(requestService.getLongDef('company_id',0))
    hsRes.vacancy = Compvacancy.get(hsRes.inrequest.id)
    if (!hsRes.company||(!hsRes.vacancy&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.composition_id)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.numbers)
      hsRes.result.errorcode<<2
    else if(hsRes.inrequest.numbers<0)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.salary)
      hsRes.result.errorcode<<4
    else if(hsRes.inrequest.salary<0)
      hsRes.result.errorcode<<5

    if(!hsRes.result.errorcode){
      try {
        if(!hsRes.vacancy) hsRes.vacancy = new Compvacancy(company_id:hsRes.company.id)
        hsRes.vacancy.setData(hsRes.inrequest).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Company/addvacancy\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deletecompvacancy = {
    checkAccess(5)
    if (!checkSectionAccess(CSTEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    try {
      Compvacancy.findByCompany_idAndId(requestService.getIntDef('company_id',0),requestService.getIntDef('id',0)).delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Company/deletecompvacancy\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def stafflist = {
    checkAccess(5)
    if (!checkSectionAccess(CSTAFF)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    hsRes.company = Company.get(requestService.getIntDef('company_id',0))
    if (!hsRes.company) {
      response.sendError(404)
      return
    }

    hsRes.stafflist = (new CompersSearch().csiFindCompers(hsRes.company.id,'',1)+new CompvacancySearch().csiFindCompvacancyByCompanyId(hsRes.company.id)).groupBy{it.composition_id}.collect{ it.value }
    hsRes.gd = new CompersSearch().csiFindCompersByPosition(hsRes.company.id,1,1)
    hsRes.gb = new CompersSearch().csiFindCompersByPosition(hsRes.company.id,2,1)
    def reportsize = hsRes.stafflist.size()
    def staffnumber = hsRes.stafflist.sum{ it.sum{ it.class==CompersSearch.class?1:it.numbers } }
    def staffsalary = hsRes.stafflist.sum{ it.sum{ it.class==CompersSearch.class?1:it.numbers }*it[0].salary }

    if (reportsize==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def templatepath = (ConfigurationHolder.config.xlstemplate.stafflist.path)?ConfigurationHolder.config.xlstemplate.stafflist.path.trim():"d:/project/Prisma/web-app/xlstemplates/templatestafflist.xlsx"
      new WebXlsxExporter(templatepath).with {
        setResponseHeaders(response)
        putCellValue(5, 1, hsRes.company.legalname)
        putCellValue(9, 3, 1)
        putCellValue(9, 4, String.format('%td.%<tm.%<tY г.',new Date()))
        putCellValue(12, 2, String.format('%tB %<tY г.',new Date()))
        putCellValue(12, 23, "штат в количестве $staffnumber единиц")
        (18..<(reportsize+18)).eachWithIndex{ rowNumber, idx ->
          fillRow(['','','',hsRes.stafflist[idx][0].position_name,hsRes.stafflist[idx].sum{ it.class==CompersSearch.class?1:it.numbers },hsRes.stafflist[idx][0].salary,'','','','','','','','','','','','','','','','','',hsRes.stafflist[idx].sum{ it.class==CompersSearch.class?1:it.numbers }*hsRes.stafflist[idx][0].salary], rowNumber, rowNumber!=(reportsize+17))
        }
        putCellValue((reportsize+18), 4, staffnumber)
        putCellValue((reportsize+18), 23, staffsalary)
        putCellValue((reportsize+18)+1, 4, staffnumber)
        putCellValue((reportsize+18)+1, 23, staffsalary)
        putCellValue((reportsize+18)+3, 22, hsRes.gd[0]?.shortname?:'')
        putCellValue((reportsize+18)+5, 22, hsRes.gb[0]?.shortname?:'')
        save(response.outputStream)
      }
    }
    return
  }

  def accounts = {
    checkAccess(5)
    if (!checkSectionAccess(CACCOUNT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    def lId=requestService.getIntDef('id',0)
    hsRes.company = Company.get(lId)
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.modstatus = requestService.getIntDef('modstatus',0)
    hsRes.accounts = new BankaccountSearch().csiFindAccountsByCompany(hsRes.company.id,hsRes.modstatus)
    hsRes.valutas = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code.toLowerCase();map}
    hsRes.iscanedit = recieveSectionAccess(CACCEDIT)

    return hsRes
  }

  def account = {
    checkAccess(5)
    if (!checkSectionAccess(CACCOUNT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    def lId=requestService.getIntDef('company_id',0)
    hsRes.company = Company.get(lId)
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.account = Bankaccount.get(requestService.getIntDef('id',0))
    hsRes.iscanedit = recieveSectionAccess(CACCEDIT)
    hsRes.bankclients = Bankclient.list()
    hsRes.directors = new CompersSearch().csiFindCompersByPosition(hsRes.company.id,1)
    hsRes.curdirector_id = Compers.findByCompany_idAndPosition_idAndModstatus(hsRes.company.id,1,1)?.pers_id
    hsRes.bank = Bank.get(hsRes.account?.bank_id?:'')

    return hsRes
  }

  def addaccount = {
    checkAccess(5)
    if (!checkSectionAccess(CACCEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getLongDef('company_id',0)
    hsRes+=requestService.getParams(['id','valuta_id','ibankblock','bankclient_id','is_duplicate','is_smsinfo',
                                     'typeaccount_id','is_bkactproc','ibankterm'],['pers_id'],['bank_id','schet',
                                     'anomer','ibank_comment','coraccount','bankname','smstel','dopoffice'])
    hsRes.inrequest.account_adate = requestService.getDate('account_adate')
    hsRes.inrequest.account_opendate = requestService.getDate('account_opendate')
    hsRes.inrequest.account_closedate = requestService.getDate('account_closedate')
    hsRes.inrequest.account_ibank_open = requestService.getDate('account_ibank_open')
    hsRes.inrequest.directordate = requestService.getDate('directordate')

    hsRes.company = Company.get(lId)
    hsRes.account = Bankaccount.get(hsRes.inrequest.id)
    if (!hsRes.company||(!hsRes.account&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.bank_id)
      hsRes.result.errorcode<<1
    else if(!Bank.findByIdIlike(hsRes.inrequest.bank_id))
      hsRes.result.errorcode<<2
    else if(Bank.findByIdIlike(hsRes.inrequest.bank_id)?.name!=hsRes.inrequest.bankname)
      hsRes.result.errorcode<<2
    if (!(hsRes.inrequest.typeaccount_id in [6,7])){
      if(!hsRes.inrequest.schet)
        hsRes.result.errorcode<<3
      else if(!Bank.findByIdIlike(hsRes.inrequest.bank_id)?.is_foreign&&!hsRes.inrequest.schet.replace('.','').matches('\\d{20}'))
        hsRes.result.errorcode<<4
      else if(!Bank.findByIdIlike(hsRes.inrequest.bank_id)?.is_foreign&&hsRes.inrequest.valuta_id==857&&!hsRes.inrequest.schet.replace('.','').matches('\\d{5}810\\d{12}'))
        hsRes.result.errorcode<<4
    }
    if(hsRes.inrequest.account_opendate&&hsRes.inrequest.account_closedate&&hsRes.inrequest.account_closedate<=hsRes.inrequest.account_opendate)
      hsRes.result.errorcode<<5
    if(hsRes.inrequest.typeaccount_id in [2,5]&&Bankaccount.findByCompany_idAndModstatusAndBank_idAndIdNotEqualAndTypeaccount_id(hsRes.company.id,1,hsRes.inrequest.bank_id,hsRes.account?.id?:0,hsRes.inrequest.typeaccount_id))
      hsRes.result.errorcode<<7
    if(hsRes.inrequest.typeaccount_id in [3,4]&&Bankaccount.findByCompany_idAndModstatusAndBank_idAndIdNotEqualAndTypeaccount_idAndValuta_id(hsRes.company.id,1,hsRes.inrequest.bank_id,hsRes.account?.id?:0,hsRes.inrequest.typeaccount_id,hsRes.inrequest.valuta_id))
      hsRes.result.errorcode<<8
    if(hsRes.inrequest.typeaccount_id in [1,2,5]&&hsRes.inrequest.valuta_id!=857)
      hsRes.result.errorcode<<9


    if(!hsRes.result.errorcode){
      try {
        if(!hsRes.account) hsRes.account = new Bankaccount(company_id:hsRes.company.id)
        hsRes.account.setData(hsRes.inrequest).csiSetIbankblock(hsRes.inrequest.ibankblock).updateModstatus().updateIbankstatus().save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Company/addaccount\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deleteaccount = {
    checkAccess(5)
    if (!checkSectionAccess(CACCEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    hsRes.account = Bankaccount.findByCompany_idAndId(requestService.getIntDef('company_id',0),requestService.getIntDef('id',0))
    if (hsRes.account?.modstatus!=0) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.account.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Company/deleteaccount\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def okveds = {
    checkAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    def lId=requestService.getIntDef('id',0)
    hsRes.company = Company.get(lId)
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.modstatus = requestService.getIntDef('modstatus',0)
    hsRes.compokved = new CompokvedSearch().csiFindCompokved(hsRes.company.id,hsRes.modstatus)

    return hsRes
  }

  def okved = {
    checkAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    def iCompanyId = requestService.getIntDef('company_id',0)
    hsRes.company = Company.get(iCompanyId)
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    def iId = requestService.getIntDef('id',0)
    hsRes.compokved = Compokved.get(iId)
    hsRes.okveds = Okved.findAllByIdNotInList(Compokved.findAllByCompany_id(hsRes.company.id).collect{it.okved_id}?:['0'])

    return hsRes
  }

  def addtookved = {
    checkAccess(5)
    if (!checkSectionAccess([CEDIT,CINCERT])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5
    hsRes.result=[errorcode:[]]
 
    hsRes+=requestService.getParams(['id','modstatus'],['company_id'],['okved_name','comments'])
    hsRes.inrequest.moddate = requestService.getDate('moddate')
    hsRes.inrequest.okved_id=hsRes.inrequest.okved_name.split(' - ')[0]    

    hsRes.company = Company.get(hsRes.inrequest.company_id)
    hsRes.compokved = Compokved.get(hsRes.inrequest.id)
    if (!hsRes.company||(!hsRes.compokved&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.compokved && !hsRes.inrequest.okved_name)
      hsRes.result.errorcode<<1
    else if(!hsRes.compokved && (!Okved.get(hsRes.inrequest.okved_name.split(' - ')[0])||Compokved.findByCompany_idAndOkved_id(hsRes.company.id,Okved.get(hsRes.inrequest.okved_name.split(' - ')[0]).id)))
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.moddate)
      hsRes.result.errorcode<<3

    if(!hsRes.result.errorcode){
      try {
        if(!hsRes.compokved)
          hsRes.company.addtookved(hsRes.inrequest.okved_name.split(' - ')[0],hsRes.inrequest.moddate,hsRes.inrequest.comments)
        else
          hsRes.compokved.csiSetData(hsRes.inrequest).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Company/addtookved\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def removefromokved = {
    checkAccess(5)
    if (!checkSectionAccess([CEDIT,CINCERT])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    def lId = requestService.getLongDef('company_id',0)
    hsRes+=requestService.getParams(null,null,['id'])

    hsRes.company = Company.get(lId)
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.company.removefromokved(hsRes.inrequest.id).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Company/removefromokved\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def setmainokved = {
    checkAccess(5)
    if (!checkSectionAccess([CEDIT,CINCERT])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    def lId = requestService.getLongDef('company_id',0)
    hsRes+=requestService.getParams(null,null,['id'])

    hsRes.company = Company.get(lId)
    hsRes.compokved = Compokved.findByCompany_idAndOkved_id(lId,hsRes.inrequest.id?:'')
    if (!hsRes.company||!hsRes.compokved) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.company.csiSetMainOkved(hsRes.inrequest.id).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Company/setmainokved\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def setarhiveokved = {
    checkAccess(5)
    if (!checkSectionAccess([CEDIT,CINCERT])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5
    hsRes.result=[errorcode:[]]

    def lId = requestService.getLongDef('company_id',0)
    hsRes+=requestService.getParams(['modstatus'],null,['id','comments'])
    hsRes.inrequest.moddate = requestService.getDate('moddate')

    hsRes.company = Company.get(lId)
    hsRes.compokved = Compokved.findByCompany_idAndOkved_id(lId,hsRes.inrequest.id?:'')
    if (!hsRes.company||!hsRes.compokved) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    if(!hsRes.inrequest.comment)
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        hsRes.compokved.csiSetData(hsRes.inrequest).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Company/setarhiveokved\n"+e.toString())
      }
    }
    render(contentType:"application/json"){[error:false]}
    return
  }

  def spaces = {
    checkAccess(5)
    if (!checkSectionAccess(CARENDA)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    def lId=requestService.getIntDef('id',0)
    hsRes.company = Company.get(lId)
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.asort = requestService.getIntDef('asort',0)
    if(hsRes.asort==-100){
      hsRes.history = new SpacehistSearch().csiFindCompanySpaceHistory(hsRes.company.id)
      render(view: "space_history", model: hsRes)
      return
    } else
      hsRes.spaces = new SpaceSearch().csiFindSpace(hsRes.company.id,hsRes.asort)

    hsRes.spacetypes = Spacetype.list().inject([:]){map, spacetype -> map[spacetype.id]=spacetype.name;map}
    hsRes.arendatypes = Arendatype.list().inject([:]){map, arendatype -> map[arendatype.id]=arendatype.name;map}

    return hsRes
  }

  def kredits = {
    checkAccess(5)
    if (!checkSectionAccess(CKREDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    def lId=requestService.getIntDef('id',0)
    hsRes.company = Company.get(lId)
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.modstatus = requestService.getIntDef('modstatus',1)    
    hsRes.kredits = new KreditSearch().csiFindCompanyKredits(hsRes.company.id,hsRes.modstatus,recieveSectionAccess(CKRREAL))
    hsRes.valutas = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code.toLowerCase();map}

    return hsRes
  }

  def lizings = {
    checkAccess(5)
    if (!checkSectionAccess(CLIZING)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    hsRes.company = Company.get(requestService.getIntDef('id',0))
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.modstatus = requestService.getIntDef('modstatus',1)  
    hsRes.lizings = new LizingSearch().csiSelectCompanyLizings(hsRes.company.id,hsRes.modstatus)
    hsRes.restfees = hsRes.lizings.inject([:]){map, lizing -> map[lizing.id]=agentKreditService.computeLizingDebt(lizing);map}

    return hsRes
  }

  def trades = {
    checkAccess(5)
    if (!checkSectionAccess(CTRADE)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    hsRes.company = Company.get(requestService.getIntDef('id',0))
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.modstatus = requestService.getIntDef('modstatus',1)
    hsRes.trades = new TradeSearch().csiSelectCompanyTrades(hsRes.company.id)

    return hsRes
  }

  def cessions = {
    checkAccess(5)
    if (!checkSectionAccess(CCESSION)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    hsRes.company = Company.get(requestService.getIntDef('id',0))
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.modstatus = requestService.getIntDef('modstatus',1)
    hsRes.cessions = new CessionSearch().csiSelectCompanyCessions(hsRes.company.id)
    hsRes.valutacodes = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code;map}

    return hsRes
  }

  def loans = {
    checkAccess(5)
    if (!checkSectionAccess(CLOAN)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    hsRes.company = Company.get(requestService.getIntDef('id',0))
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.modstatus = requestService.getIntDef('modstatus',1) 
    hsRes.loans = new LoanSearch().csiFindCompanyLoans(hsRes.company.id,hsRes.modstatus)
    hsRes.valutas = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code.toLowerCase();map}

    return hsRes
  }

  def payrequests = {
    checkAccess(5)
    if (!checkSectionAccess(CPAYPLAN)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    hsRes.company = Company.get(requestService.getIntDef('id',0))
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payrequests = Payrequest.findAll(sort:'id',order:'desc',max:Tools.getIntVal(Dynconfig.findByName('company.payrequests.results.max')?.value,20)){ modstatus >= 0 && (fromcompany_id == hsRes.company.id || tocompany_id == hsRes.company.id) }
    hsRes.taxes = Tax.list().inject([:]){map, tax -> map[tax.id]=tax.name;map}

    return hsRes
  }

  def licenses = {
    checkAccess(5)
    if (!checkSectionAccess(CLICENSE)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    def lId=requestService.getIntDef('id',0)
    hsRes.company = Company.get(lId)
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.licenses = new LicenseSearch().csiFindCompanyLicenses(hsRes.company.id)
    hsRes.industries = Industry.list().inject([:]){map, industry -> map[industry.id]=industry.name;map}

    return hsRes
  }

  def complicenses = {
    checkAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    hsRes.company = Company.get(requestService.getIntDef('id',0))
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.complicenses = Complicense.findAllByCompany_id(hsRes.company.id,[sort:'modstatus',order:'desc'])
    hsRes.iscanedit = recieveSectionAccess(CEDIT)

    return hsRes
  }

  def complicense = {
    checkAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    hsRes.company = Company.get(requestService.getIntDef('company_id',0))
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.complicense = Complicense.findByCompany_idAndId(hsRes.company.id,requestService.getIntDef('id',0))
    hsRes.iscanedit = recieveSectionAccess(CEDIT)

    return hsRes
  }

  def addcomplicense = {
    checkAccess(5)
    if (!checkSectionAccess(CEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5
    hsRes.result=[errorcode:[]]
 
    hsRes+=requestService.getParams(['id','modstatus'],null,['comment','nomer','formnumber','authority','name'])
    hsRes.inrequest.ldate = requestService.getDate('complicense_ldate')
    hsRes.inrequest.validity = requestService.getDate('complicense_validity')

    hsRes.company = Company.get(requestService.getLongDef('company_id',0))
    hsRes.complicense = Complicense.get(hsRes.inrequest.id)
    if (!hsRes.company||(!hsRes.complicense&&hsRes.inrequest.id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.name)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.ldate)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.validity)
      hsRes.result.errorcode<<3
    else if (hsRes.inrequest.validity<=hsRes.inrequest.ldate)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.nomer)
      hsRes.result.errorcode<<5
    if(!hsRes.inrequest.formnumber)
      hsRes.result.errorcode<<6
    if(!hsRes.inrequest.authority)
      hsRes.result.errorcode<<7

    if(!hsRes.result.errorcode){
      try {
        if(!hsRes.complicense) hsRes.complicense = new Complicense(company_id:hsRes.company.id)
        hsRes.complicense.setData(hsRes.inrequest).csiSetModstatus(hsRes.inrequest.modstatus).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Company/addcomplicense\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def history = {
    checkAccess(5)
    if (!checkSectionAccess([CEDIT,CINCERT])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    def lId=requestService.getIntDef('id',0)
    hsRes.company = Company.get(lId)
    if (!hsRes.company) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.history = new CompanyhistSearch().csiFindHistory(hsRes.company.id)
    hsRes.taxoptions = Taxoption.list().inject([:]){map, option -> map[option.id]=option.name;map}
    hsRes.statuses = Activitystatus.list().inject([:]){map, status -> map[status.id]=status.name;map}

    return hsRes
  }
  
  def report = {
    checkAccess(5)
    if (!checkSectionAccess(CCARD)) return
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  
    hsRes.user = session.user   
    
    def lId=requestService.getIntDef('id',0)
    
    hsRes.company=Company.get(lId)

    if(lId && !hsRes.company){
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.companyhist=Companyhist.findAllByCompany_id(hsRes.company?.id).collect{it.legaladr}.unique()    
    hsRes.companyhist.remove(hsRes.company?.legaladr)    

    hsRes.compokved_active = new CompokvedSearch().csiFindCompokved(hsRes.company.id,-100,1)
    hsRes.compokved_not_active = new CompokvedSearch().csiFindCompokved(hsRes.company.id,-100,0)
    //hsRes.positions = Position.list().inject([:]){map, position -> map[position.id]=position.name;map}
    hsRes.personal = new CompersSearch().csiFindCompersAll(hsRes.company.id,1)
    if(hsRes.personal.size()){
      hsRes.general=hsRes.personal[0]
      if(hsRes.general.position_id!=1)
        hsRes.general=null
    }        
    for(oPers in hsRes.personal){
      if(oPers.position_id==2)
        hsRes.gb=oPers                    
    }     
    hsRes.owners = new CompholderSearch().csiFindCompholdersByCompanyIdAndModstatus(hsRes.company.id,1)
    
    hsRes.general_old = new CompersSearch().csiFindCompersByPosition(hsRes.company.id,1,0)
    hsRes.gb_old = new CompersSearch().csiFindCompersByPosition(hsRes.company.id,2,0)
    hsRes.owners_old = new CompholderSearch().csiFindCompholdersByCompanyIdAndModstatus(hsRes.company.id,0)
    if (!hsRes.gb) hsRes.gboutsources = new ServiceSearch().csiFindByZcompanyIdAndModstatusAndType(hsRes.company.id,1,1)

    hsRes.accounts = new BankaccountSearch().csiFindAccounts(hsRes.company.id)
    def iOffice=Spacetype.findWhere(name:'Офис')?.id?:0
    if(iOffice)
      hsRes.office=Space.findAllByArendatorAndSpacetype_idAndModstatus(hsRes.company?.id?:0,iOffice,1)
    def iWh=Spacetype.findWhere(name:'Склад')?.id?:0
    if(iWh)
      hsRes.wh=Space.findAllByArendatorAndSpacetype_idAndModstatus(hsRes.company?.id?:0,iWh,1) 

    def lsLicense=new License().csiFindLicenceIndustry(lId)      
    hsRes.industry=[]
    if(lsLicense){
      for(license in lsLicense)
        hsRes.industry<<Industry.get(license.industry_id?:0)
      
      hsRes.industry.sort{it.name}      
    }
    hsRes.complicenses = Complicense.findAllByCompany_id(hsRes.company.id,[sort:'modstatus',order:'desc'])

    renderPdf(template:'report',model:hsRes,filename:'company_'+hsRes.company.id+'_card.pdf')
  }
  
  def requisit = {
    checkAccess(5)
    if (!checkSectionAccess(RCARD)) return
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  
    hsRes.user = session.user   
    
    def lId=requestService.getIntDef('id',0)
    def sOkved=requestService.getStr('okved')
    def iSchet_id=requestService.getIntDef('schet_id',0)
    
    hsRes.company=Company.get(lId)

    if(lId && !hsRes.company){
      render(contentType:"application/json"){[error:true]}
      return
    }                  
    
    hsRes.compokved = new CompokvedSearch().csiFindCompokvedByOkvedId(hsRes.company.id,sOkved)
   
    hsRes.personal = new CompersSearch().csiFindCompersAll(hsRes.company.id,1)
    if(hsRes.personal.size()){
      hsRes.general=hsRes.personal[0]
      if(hsRes.general.position_id!=1)
        hsRes.general=null
    }            
 
    hsRes.account = Bankaccount.get(iSchet_id)
    hsRes.bank=Bank.get(hsRes.account?.bank_id?:'')    
    
    def lsLicense=new License().csiFindLicenceIndustry(lId)      
    hsRes.industry=[]
    if(lsLicense){
      for(license in lsLicense)
        hsRes.industry<<Industry.get(license.industry_id?:0)
      
      hsRes.industry.sort{it.name}      
    }    

    renderPdf(template:'requisit',model:hsRes,filename:'company_requisit_'+hsRes.company.id+'_card.pdf')
  }

  def bankrequest = {
    checkAccess(5)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    hsRes.company = Company.get(requestService.getIntDef('id',0))
    if(!hsRes.company){
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.account = Bankaccount.findByCompany_idAndModstatusAndTypeaccount_idAndId(hsRes.company.id,1,1,requestService.getIntDef('acc_id',0))
    hsRes.bank = Bank.get(hsRes.account?.bank_id?:'')
    hsRes.gd = Pers.get(Compers.findByCompany_idAndPosition_idAndModstatus(hsRes.company.id,1,1)?.pers_id?:0)
    hsRes.gb = Pers.get(Compers.findByCompany_idAndPosition_idAndModstatus(hsRes.company.id,2,1)?.pers_id?:0)

    String inputfilepath = (ConfigurationHolder.config.doctemplate.bankrequest.path)?ConfigurationHolder.config.doctemplate.bankrequest.path.trim():"d:/project/Prisma/web-app/doctemplates/bankrequest.docx"
    WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(inputfilepath))
    MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart()
    def mappings = [legalname:hsRes.company.legalname,legaladr:hsRes.company.legaladr,tel:hsRes.company.tel,gdname:hsRes.gd?.shortname?:'',gbname:hsRes.gb?.shortname?:'',rs:hsRes.account?.schet?:'',bank:hsRes.bank?.name?:'']
    documentPart.variableReplace(mappings)

    response.setHeader("Content-disposition", "attachment; filename=bankrequest.docx")
    response.contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    (new SaveToZipFile(wordMLPackage)).save(response.outputStream)
    response.flushBuffer()
  }

  def bankanketa = {
    checkAccess(5)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 5

    hsRes.company = Company.get(requestService.getIntDef('id',0))
    if(!hsRes.company){
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.account = Bankaccount.findByCompany_idAndModstatusAndTypeaccount_idAndId(hsRes.company.id,1,1,requestService.getIntDef('acc_id',0))
    hsRes.bank = Bank.get(hsRes.account?.bank_id?:'')
    hsRes.gd = Pers.get(Compers.findByCompany_idAndPosition_idAndModstatus(hsRes.company.id,1,1)?.pers_id?:0)
    hsRes.gb = Pers.get(Compers.findByCompany_idAndPosition_idAndModstatus(hsRes.company.id,2,1)?.pers_id?:0)
    hsRes.complicenses = Complicense.findAllByCompany_idAndModstatus(hsRes.company.id,1,[sort:'ldate',order:'desc'])
    hsRes.compfounders = new CompholderSearch().csiFindCompholdersByCompanyIdAndModstatus(hsRes.company.id,1)
    def mappings = [cname:hsRes.company.name,legalname:hsRes.company.legalname,legaladr:hsRes.company.legaladr,tel:hsRes.company.tel,opendate:(hsRes.company.opendate?String.format('%td.%<tm.%<tY',hsRes.company.opendate):''),reregdate:(hsRes.company.reregdate?String.format('%td.%<tm.%<tY',hsRes.company.reregdate):''),regauthority:hsRes.company.regauthority?:'',www:hsRes.company.www,ogrn:hsRes.company.ogrn,inn:hsRes.company.inn,okpo:hsRes.company.okpo,gdposition:'Генеральный директор',gdname:hsRes.gd?.shortname?:'',gdpass:hsRes.gd?.passport?:'',gdaddress:hsRes.gd?.propiska?:'',gbposition:'Главный бухгалтер',gbname:hsRes.gb?.shortname?:'',gbpass:hsRes.gb?.passport?:'',gbaddress:hsRes.gb?.propiska?:'',rs:hsRes.account?.schet?:'',bank:hsRes.bank?.name?:'',rsday:hsRes.account?.opendate?.getDate()?.toString()?:'',rsmonth:hsRes.account?.opendate?String.format('%tB',hsRes.account.opendate):'',rsyear:hsRes.account?.opendate?(hsRes.account.opendate.getYear()+1900).toString():'']

    String inputfilepath = (ConfigurationHolder.config.doctemplate.bankanketa.path)?ConfigurationHolder.config.doctemplate.bankanketa.path.trim():"d:/project/Prisma/web-app/doctemplates/bankanketa.docx"
    WordprocessingMLPackage wordMLPackage = Docx4J.load(new File(inputfilepath))
    ByteArrayInputStream xmlStream = new ByteArrayInputStream(docexportService.getBankanketaXML(complicenses:hsRes.complicenses,compfounders:hsRes.compfounders).getBytes("UTF-8"))
    Docx4J.bind(wordMLPackage, xmlStream, Docx4J.FLAG_BIND_INSERT_XML | Docx4J.FLAG_BIND_BIND_XML)
    MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart()
    documentPart.variableReplace(mappings)

    response.setHeader("Content-disposition", "attachment; filename=bankanketa.docx")
    response.contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    (new SaveToZipFile(wordMLPackage)).save(response.outputStream)
    response.flushBuffer()
  }

  def reportall = {
    checkAccess(5)
    if (!checkSectionAccess(CCARD)) return
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)  
    hsRes.user = session.user   

    hsRes.companylist = Company.findAllByIs_holding(1).collect{
      def result = [:]
      result.company = it

      result.companyhist=Companyhist.findAllByCompany_id(result.company?.id).collect{it.legaladr}.unique()
      result.companyhist.remove(result.company?.legaladr)

      result.compokved_active = new CompokvedSearch().csiFindCompokved(result.company.id,-100,1)
      result.compokved_not_active = new CompokvedSearch().csiFindCompokved(result.company.id,-100,0)
      //result.positions = Position.list().inject([:]){map, position -> map[position.id]=position.name;map}
      result.personal = new CompersSearch().csiFindCompersAll(result.company.id,1)
      if(result.personal.size()){
        result.general=result.personal[0]
        if(result.general.position_id!=1)
          result.general=null
      }
      for(oPers in result.personal){
        if(oPers.position_id==2)
          result.gb=oPers
      }
      result.owners = new CompholderSearch().csiFindCompholdersByCompanyIdAndModstatus(result.company.id,1)

      result.general_old = new CompersSearch().csiFindCompersByPosition(result.company.id,1,0)
      result.gb_old = new CompersSearch().csiFindCompersByPosition(result.company.id,2,0)
      result.owners_old = new CompholderSearch().csiFindCompholdersByCompanyIdAndModstatus(result.company.id,0)
      if (!result.gb) result.gboutsources = new ServiceSearch().csiFindByZcompanyIdAndModstatusAndType(result.company.id,1,1)

      result.accounts = new BankaccountSearch().csiFindAccounts(result.company.id)
      def iOffice=Spacetype.findWhere(name:'Офис')?.id?:0
      if(iOffice)
        result.office=Space.findAllByArendatorAndSpacetype_idAndModstatus(result.company?.id?:0,iOffice,1)
      def iWh=Spacetype.findWhere(name:'Склад')?.id?:0
      if(iWh)
        result.wh=Space.findAllByArendatorAndSpacetype_idAndModstatus(result.company?.id?:0,iWh,1)

      def lsLicense=new License().csiFindLicenceIndustry(result.company.id)
      result.industry=[]
      if(lsLicense){
        for(license in lsLicense)
          result.industry<<Industry.get(license.industry_id?:0)
        result.industry.sort{it.name}
      }
      result.complicenses = Complicense.findAllByCompany_id(result.company.id,[sort:'modstatus',order:'desc'])
      result
    }

    renderPdf(template:'report_all',model:hsRes,filename:'companies_card.pdf')
  }
}