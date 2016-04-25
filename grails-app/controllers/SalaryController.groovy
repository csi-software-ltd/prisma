import grails.converters.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import pl.touk.excel.export.WebXlsxExporter

class SalaryController {
  def requestService
  def imageService
  def parseService
  def salaryService

  final String SALAVEDIT = 'is_salaryedit'
  final String SALBUHEDIT = 'is_salarybuhedit'
  final String SALOFFEDIT = 'is_salaryoffedit'
  final String SALALLDEP = 'is_salaryalldep'
  final String SALAPPROVE = 'is_salaryapprove'
  final String SALTAXEDIT = 'is_paynalogedit'
  final String SALNALEDIT = 'is_salarynaledit'

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
    if(!session.user.group?."${Salarytype.get(iId)?.checkfield}"){
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

  private Boolean checkReportAccess(_report,_dep_id) {
    if (!recieveSectionPermission(SALALLDEP)&&!(_report.department_id in Department.findAllByIdOrParent(_dep_id,Department.get(_dep_id)?.id?:-1).collect{ it.id })) {
      response.sendError(403)
      return false
    }
    return true
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Main >>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def index = {
    checkAccess(11)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails&&session.sallastRequest){
      session.sallastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.sallastRequest
    } else {
      hsRes.inrequest=[:]
      hsRes.inrequest.salsection = requestService.getIntDef('salsection',0)
    }

    hsRes.saltypes = Salarytype.findAllByShortnameNotEqual('')

    return hsRes
  }

  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Avans >>>/////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def avansfilter = {
    checkAccess(11)
    checkSectionAccess(1)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    if (session.sallastRequest?.fromDetails){
      hsRes.inrequest = session.sallastRequest
    }
    hsRes.repdates = Salaryreport.findAllBySalarytype_id(1,[order:'desc',sort:'repdate']).collect{[disvalue:String.format('%tY-%<tm',new Date(it.year-1900,it.month-1,1)),keyvalue:String.format('%td.%<tm.%<tY',new Date(it.year-1900,it.month-1,1))]}.unique()
    hsRes.iscanincert = recieveSectionPermission(SALAVEDIT)
    hsRes.isalldep = recieveSectionPermission(SALALLDEP)

    return hsRes
  }

  def avanses = {
    checkAccess(11)
    checkSectionAccess(1)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    if (session.sallastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.sallastRequest
      session.sallastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['modstatus','department_id'])
      hsRes.inrequest.repdate = requestService.getDate('repdate')
      hsRes.inrequest.offset = requestService.getOffset()
      session.sallastRequest = hsRes.inrequest
    }
    session.sallastRequest.salsection = 1

    hsRes.searchresult = new SalaryreportSearch().csiSelectAvanses(hsRes.inrequest.modstatus?:0,!recieveSectionPermission(SALALLDEP)?-100:hsRes.inrequest.department_id?:0,!recieveSectionPermission(SALALLDEP)?Department.findAllByIdOrParent(hsRes.user.department_id,Department.get(hsRes.user.department_id)?.id?:-1).collect{ it.id }:null,hsRes.inrequest.repdate,20,hsRes.inrequest.offset)
    hsRes.departments = Department.list().inject([:]){map, department -> map[department.id]=department.name;map}
    hsRes.iscandelete = recieveSectionPermission(SALAVEDIT)

    return hsRes
  }

  def addavans = {
    checkAccess(11)
    checkSectionAccess(1)
    if(!checkSectionPermission(SALAVEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    return hsRes
  }

  def incertavans = {
    checkAccess(11)
    if(!checkSectionAccess(1)) return
    if(!checkSectionPermission(SALAVEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11
    hsRes.result=[errorcode:[]]
 
    hsRes+=requestService.getParams(['repdate_month','repdate_year'])

    if(!hsRes.inrequest.repdate_month||!hsRes.inrequest.repdate_year)
      hsRes.result.errorcode<<1
    else if(!(hsRes.inrequest.repdate_month in 1..12)||hsRes.inrequest.repdate_year<2014)
      hsRes.result.errorcode<<1
    else if (Salaryreport.findByMonthAndYearAndDepartment_idAndSalarytype_id(hsRes.inrequest.repdate_month,hsRes.inrequest.repdate_year,hsRes.user.department_id,1))
      hsRes.result.errorcode<<2

    if(!hsRes.result.errorcode){
      try {
          def bdSumma = User.findAllByModstatusAndDepartment_idInListAndPers_idGreaterThan(1,Department.findAllByIdOrParent(hsRes.user.department_id,hsRes.user.department_id).collect{ it.id },0).collect{ it.pers_id }.unique().sum{ Salary.findOrCreateByMonthAndYearAndPers_idAndDepartment_id(hsRes.inrequest.repdate_month,hsRes.inrequest.repdate_year,it,hsRes.user.department_id).csiComputePrepaymentSumma(salaryService.computeEmployeeMonthSalary(it,hsRes.inrequest.repdate_month,hsRes.inrequest.repdate_year)).save(failOnError:true).prepayment }?.toBigDecimal()?:0g
          hsRes.result.avans = new Salaryreport(month:hsRes.inrequest.repdate_month,year:hsRes.inrequest.repdate_year,salarytype_id:1,department_id:hsRes.user.department_id).setData(null).csiSetSumma(bdSumma).csiSetModstatus(0).csiSetAdmin(session.user.id).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Salary/incertavans\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deleteavansreport = {
    checkAccess(11)
    if (!checkSectionAccess(1)) return
    if (!checkSectionPermission(SALAVEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    hsRes.avans = Salaryreport.findByIdAndSalarytype_id(requestService.getIntDef('id',0),1)
    if (hsRes.avans?.modstatus!=0) {
      response.sendError(404)
      render(contentType:"application/json"){[error:true]}
      return
    }
    if(!checkReportAccess(hsRes.avans,hsRes.user.department_id)) return

    try {
      Salary.findAllByMonthAndYearAndDepartment_id(hsRes.avans.month,hsRes.avans.year,hsRes.avans.department_id).each{ it.delete(flush:true) }
      hsRes.avans.delete(flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Salary/deleteavansreport\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def avans = {
    checkAccess(11)
    if(!checkSectionAccess(1)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    def lId=requestService.getIntDef('id',0)
    hsRes.avans = Salaryreport.get(lId)
    if (!hsRes.avans) {
      response.sendError(404)
      return
    }
    if(!checkReportAccess(hsRes.avans,hsRes.user.department_id)) return
    hsRes.iscanclose = (hsRes.avans.file&&hsRes.avans.modstatus==1&&!Salary.findAllByMonthAndYearAndDepartment_id(hsRes.avans.month,hsRes.avans.year,hsRes.avans.department_id).find{ it.prepaystatus==1 })
    hsRes.iscanedit = recieveSectionPermission(SALAVEDIT)

    return hsRes
  }

  def recievesumma={
    requestService.init(this)
    def lId = requestService.getIntDef('id',0)
    if(lId){
      render g.number(value:Salaryreport.get(lId)?.summa?:0)
      return
    }
    render ''
  }

  def updateavans = {
    checkAccess(11)
    if(!checkSectionAccess(1)) return
    if(!checkSectionPermission(SALAVEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getLongDef('id',0)
    hsRes.avans = Salaryreport.get(lId)
    if (!hsRes.avans) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    if(!checkReportAccess(hsRes.avans,hsRes.user.department_id)) return

    hsRes.repdate = requestService.getDate('repdate')
    hsRes.modstatus = requestService.getIntDef('modstatus',0)

    if(!hsRes.repdate)
      hsRes.result.errorcode<<1
    if(hsRes.modstatus==2&&!(hsRes.avans.file&&hsRes.avans.modstatus==1&&!Salary.findAllByMonthAndYearAndDepartment_id(hsRes.avans.month,hsRes.avans.year,hsRes.avans.department_id).find{ it.prepaystatus==1 }))
      hsRes.result.errorcode<<2

    if(!hsRes.result.errorcode){
      try {
        hsRes.avans.setData(repdate:hsRes.repdate).csiSetModstatus(hsRes.modstatus).csiSetConfirm(hsRes.modstatus==1?0:hsRes.avans.is_confirm).save(failOnError:true)
        if(hsRes.modstatus==1) {
          Cashzakaz.findOrCreateWhere(department_id:hsRes.avans.department_id,salaryreport_id:hsRes.avans.id).setData(summa:hsRes.avans.summa.toLong(),todate:hsRes.avans.repdate,purpose:"Оплата авансовой ведомости за ${String.format('%tB %<tY',new Date(hsRes.avans.year-1900,hsRes.avans.month-1,1))}").csiSetAdmin(session.user.id).csiSetModstatus(1,0).save(failOnError:true)
          Salary.findAllByMonthAndYearAndDepartment_id(hsRes.avans.month,hsRes.avans.year,hsRes.avans.department_id).each{
            it.csiSetPrepaystatus(1).save(flush:true)
          }
        }
      } catch(Exception e) {
        log.debug("Error save data in Salary/updateavans\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def avansreport = {
    checkAccess(11)
    if(!checkSectionAccess(1)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    def lId=requestService.getIntDef('id',0)
    hsRes.avans = Salaryreport.get(lId)
    if (!hsRes.avans) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    if(!checkReportAccess(hsRes.avans,hsRes.user.department_id)) return

    hsRes.prepayments = new SalarySearch().csiFindPrepayments(hsRes.avans.month,hsRes.avans.year,hsRes.avans.department_id)
    hsRes.iscanedit = recieveSectionPermission(SALAVEDIT)

    return hsRes
  }

  def prepayment = {
    checkAccess(11)
    if(!checkSectionAccess(1)) return
    if(!checkSectionPermission(SALAVEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    def lId=requestService.getIntDef('avans_id',0)
    hsRes.avans = Salaryreport.get(lId)
    hsRes.prepayment = Salary.get(requestService.getIntDef('id',0))
    if (!hsRes.avans||!hsRes.prepayment) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    if(!checkReportAccess(hsRes.avans,hsRes.user.department_id)) return

    hsRes.pers = Pers.get(hsRes.prepayment.pers_id)

    return hsRes
  }

  def prepaymentupdate = {
    checkAccess(11)
    if(!checkSectionAccess(1)) return
    if(!checkSectionPermission(SALAVEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11
    hsRes.result=[errorcode:[]]
 
    def lId=requestService.getIntDef('avans_id',0)
    hsRes.avans = Salaryreport.get(lId)
    hsRes.prepayment = Salary.get(requestService.getIntDef('id',0))
    if (hsRes.avans?.modstatus!=0||!hsRes.prepayment) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    if(!checkReportAccess(hsRes.avans,hsRes.user.department_id)) return

    def lSumma = requestService.getIntDef('prepayment',0)

    if(lSumma<0)
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        hsRes.prepayment.csiSetPrepaymentSumma(lSumma).save(failOnError:true)
        hsRes.avans.csiSetSumma(((Salary.findAllByMonthAndYearAndDepartment_idAndIdNotEqual(hsRes.avans.month,hsRes.avans.year,hsRes.avans.department_id,hsRes.prepayment.id).sum{ it.prepayment }?:0)+lSumma).toBigDecimal()).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Salary/prepaymentupdate\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def payprepayment = {
    checkAccess(11)
    if(!checkSectionAccess(1)) return
    if(!checkSectionPermission(SALAVEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getIntDef('id',0)
    def iType = requestService.getIntDef('type',0)
    hsRes.avans = Salaryreport.get(lId)
    hsRes.prepayment = Salary.get(requestService.getIntDef('sal_id',0))
    if (hsRes.avans?.modstatus!=1||!hsRes.prepayment||!iType) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    if(!checkReportAccess(hsRes.avans,hsRes.user.department_id)) return

    try {
      if(Department.get(hsRes.avans.department_id)?.is_extra)
        new Cash().csiSetReceipt(null).csiSetAdmin(session.user.id).setData(maincashtype:1,department_id:hsRes.avans.department_id,pers_id:User.findByPers_idAndDepartment_idAndModstatus(hsRes.prepayment.pers_id,hsRes.avans.department_id,1)?.id,maincashclass:4,operationdate:new Date(),platperiod:new Date(hsRes.avans.year-1900,hsRes.avans.month,1),comment:(iType==1?'аванс':'отмена аванса'),summa:(iType==1?hsRes.prepayment.prepayment:-hsRes.prepayment.prepayment)).save(failOnError:true)
      else
        new Cashdepartment(department_id:hsRes.avans.department_id,pers_id:User.findByPers_idAndModstatus(hsRes.prepayment.pers_id,1)?.id,is_dep:1).csiSetReceipt(null).csiSetAdmin(session.user.id).setData(depcashtype:1,depcashclass:1,operationdate:new Date(),platperiod:new Date(hsRes.avans.year-1900,hsRes.avans.month,1),comment:(iType==1?'аванс':'отмена аванса'),summa:(iType==1?hsRes.prepayment.prepayment:-hsRes.prepayment.prepayment)).save(failOnError:true)
      hsRes.prepayment.csiSetPrepaystatus(iType==1?2:1).csiSetPrepaydate(iType==1?new Date():null).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Salary/payprepayment\n"+e.toString())
      hsRes.result.errorcode << 100
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Avans <<</////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Buhreport >>>/////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def buhfilter = {
    checkAccess(11)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    if (session.sallastRequest?.fromDetails){
      hsRes.inrequest = session.sallastRequest
    }
    hsRes.iscanincert = recieveSectionPermission(SALBUHEDIT)
    hsRes.iscanpay = recieveSectionPermission(SALOFFEDIT)
    hsRes.buhreports = Salaryreport.findAllBySalarytype_id(2,[order:'desc',sort:'repdate']).collect{[disvalue:String.format('%tY-%<tm',new Date(it.year-1900,it.month-1,1)),keyvalue:String.format('%td.%<tm.%<tY',new Date(it.year-1900,it.month-1,1))]}
    hsRes.curreport = session.sallastRequest?.fromDetails?Salaryreport.findByMonthAndYearAndSalarytype_id(hsRes.inrequest.repdate.getMonth()+1,hsRes.inrequest.repdate.getYear()+1900,2):Salaryreport.findBySalarytype_id(2,[order:'desc',sort:'repdate'])
    hsRes.islinked = !Salarycomp.findAll{ month == hsRes.curreport?.month && year == hsRes.curreport?.year && (compstatus == 0 || perstatus == 0) }

    return hsRes
  }

  def getaddbuttons = {
    checkAccess(11)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    hsRes.date = requestService.getDate('date')
    hsRes.curreport = Salaryreport.findByMonthAndYearAndSalarytype_id(hsRes.date.getMonth()+1,hsRes.date.getYear()+1900,2)
    hsRes.islinked = !Salarycomp.findAll{ month == hsRes.curreport?.month && year == hsRes.curreport?.year && (compstatus == 0 || perstatus == 0) }
    hsRes.iscanincert = recieveSectionPermission(SALBUHEDIT)
    hsRes.iscanpay = recieveSectionPermission(SALOFFEDIT)

    return hsRes
  }

  def buhreports = {
    checkAccess(11)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    if (session.sallastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.sallastRequest
      session.sallastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['compstatus','perstatus','is_tax','perstype'],null,['company_name','pers_name'])
      hsRes.inrequest.repdate = requestService.getDate('repdate')?:new Date()
      hsRes.inrequest.offset = requestService.getOffset()
      session.sallastRequest = [:]
      session.sallastRequest = hsRes.inrequest
    }
    session.sallastRequest.salsection = 2

    hsRes.salreport = Salaryreport.findByMonthAndYearAndSalarytype_id(hsRes.inrequest.repdate.getMonth()+1,hsRes.inrequest.repdate.getYear()+1900,2)
    if(hsRes.inrequest.pers_name||(hsRes.inrequest.perstype?:0)>-100||(hsRes.inrequest.perstatus?:0)>-100){
      hsRes.salarypers = new Salarycomp().csiSelectPers(hsRes.inrequest.repdate,hsRes.inrequest.pers_name?:'',
                                                        hsRes.inrequest.perstype?:0,hsRes.inrequest.perstatus?:0,
                                                        null,-1,0)
      hsRes.companies = new Salarycomp().csiSelectCompanies(hsRes.inrequest.repdate,hsRes.inrequest.company_name?:'',
                                                            hsRes.inrequest.compstatus?:0,hsRes.salarypers.records.companyinn?:['1'],10,hsRes.inrequest.offset)
    } else {
      hsRes.companies = new Salarycomp().csiSelectCompanies(hsRes.inrequest.repdate,hsRes.inrequest.company_name?:'',
                                                            hsRes.inrequest.compstatus?:0,null,10,hsRes.inrequest.offset)
      hsRes.salarypers = new Salarycomp().csiSelectPers(hsRes.inrequest.repdate,hsRes.inrequest.pers_name?:'',
                                                        hsRes.inrequest.perstype?:0,hsRes.inrequest.perstatus?:0,
                                                        hsRes.companies.records.companyinn,-1,0)
    }
    hsRes.warningstatus = hsRes.salreport?.modstatus!=0?0:Compers.countByModstatus(1)!=Salarycomp.countByMonthAndYearAndPerstatus(hsRes.salreport.month,hsRes.salreport.year,1)?1:0
    hsRes.iscanedit = recieveSectionPermission(SALBUHEDIT)

    return hsRes
  }

  def buhreportXLS = {
    checkAccess(11)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    hsRes+=requestService.getParams(['compstatus','perstatus','is_tax','perstype'],null,['company_name','pers_name'])
    hsRes.inrequest.repdate = requestService.getDate('repdate')?:new Date()

    hsRes.salreport = Salaryreport.findByMonthAndYearAndSalarytype_id(hsRes.inrequest.repdate.getMonth()+1,hsRes.inrequest.repdate.getYear()+1900,2)
    if(hsRes.inrequest.pers_name||(hsRes.inrequest.perstype?:0)>-100||(hsRes.inrequest.perstatus?:0)>-100){
      hsRes.salarypers = new Salarycomp().csiSelectPers(hsRes.inrequest.repdate,hsRes.inrequest.pers_name?:'',
                                                        hsRes.inrequest.perstype?:0,hsRes.inrequest.perstatus?:0,
                                                        null,-1,0)
      hsRes.companies = new Salarycomp().csiSelectCompanies(hsRes.inrequest.repdate,hsRes.inrequest.company_name?:'',
                                                            hsRes.inrequest.compstatus?:0,hsRes.salarypers.records.companyinn?:['1'],-1,0)
    } else {
      hsRes.companies = new Salarycomp().csiSelectCompanies(hsRes.inrequest.repdate,hsRes.inrequest.company_name?:'',
                                                            hsRes.inrequest.compstatus?:0,null,-1,0)
      hsRes.salarypers = new Salarycomp().csiSelectPers(hsRes.inrequest.repdate,hsRes.inrequest.pers_name?:'',
                                                        hsRes.inrequest.perstype?:0,hsRes.inrequest.perstatus?:0,
                                                        hsRes.companies.records.companyinn,-1,0)
    }

    def rowCounter = 2
    new WebXlsxExporter().with {
      setResponseHeaders(response)
      putCellValue(0, 2, "${String.format('%tB %<tY',new Date(hsRes.salreport.year-1900,hsRes.salreport.month-1,1))}")
      fillRow(['Компания','ИНН','Район','ФИО','СНИЛС','Комментарии','Начислено','ЗП к выплате','Задолженность по зарплате','НДФЛ (13%)','ВНиМ (2,9%)','НС и ПЗ','ФФОМС (5,1%)','ОПС','ИТОГО Налоги к оплате'],1,false)
      hsRes.companies.records.each{ company ->
        hsRes.salarypers.records.each{ pers ->
          if(company.companyinn==pers.companyinn&&!hsRes.inrequest.is_tax){
            //pers
            fillRow([pers.companyname,
                     pers.companyinn,
                     pers.region,
                     pers.fio,
                     pers.snils,
                     pers.position,
                     pers.fullsalary,
                     pers.netsalary,
                     pers.debtsalary,'','','','',''], rowCounter++, false)
          }
        }
        //company
        fillRow([company.companyname,
                 company.companyinn,
                 "НАЛОГИ",
                 "НАЛОГИ",'',
                 "НАЛОГИ",'','','',
                 company.ndfl,
                 company.fss_tempinvalid,
                 company.fss_accident,
                 company.ffoms,
                 company.pf,
                 company.ndfl+company.fss_tempinvalid+company.fss_accident+company.ffoms+company.pf], rowCounter++, false)
      }
      save(response.outputStream)
    }
    return
  }

  def nonfindemployee = {
    checkAccess(11)
    checkSectionAccess(2)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    hsRes.salreport = Salaryreport.get(requestService.getIntDef('id',0))
    if (!hsRes.salreport) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.compers = new CompersSearch().csiFindNonLinkedCompers(hsRes.salreport.month,hsRes.salreport.year)
    hsRes.positions = Position.list().inject([:]){map, position -> map[position.id]=position.name;map}
    return hsRes
  }

  def addbuhreport = {
    checkAccess(11)
    checkSectionAccess(2)
    if (!checkSectionPermission(SALBUHEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    return hsRes
  }

  def incertbuhreport = {
    checkAccess(11)
    if (!checkSectionAccess(2)) return
    if (!checkSectionPermission(SALBUHEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    hsRes.result = parseService.parseBuhReportFile(request.getFile('file'))

    if(!hsRes.result.errorcode){
      try {
        hsRes.result.preparedData.each{
          new Salarycomp(is_pers:(it.find{it.value=="НАЛОГИ"}?0:1)).setData(it,hsRes.result.reportDate).save(failOnError:true)
        }
        new Salaryreport(month:hsRes.result.reportDate.getMonth()+1,year:hsRes.result.reportDate.getYear()+1900,salarytype_id:2,department_id:0).setData(null).csiSetSumma(0g).csiSetModstatus(0).csiSetAdmin(session.user.id).save(failOnError:true)
      } catch(org.springframework.dao.DataIntegrityViolationException err) {
        log.debug("Error save data in Salary/incertbuhreport\n"+err.toString())
        Salarycomp.withSession { session -> session.clear() }
        Salarycomp.findAllByMonthAndYear(hsRes.result.reportDate.getMonth()+1,hsRes.result.reportDate.getYear()+1900).each{ it.delete() }
        hsRes.result.errorcode<<100
      } catch(Exception e) {
        log.debug("Error save data in Salary/incertbuhreport\n"+e.toString())
        hsRes.result.errorcode<<100
      }
    }

    return hsRes.result
  }

  def buhsalarydetail = {
    checkAccess(11)
    if(!checkSectionAccess(2)) return
    if (!checkSectionPermission(SALBUHEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    hsRes.scomp = Salarycomp.get(requestService.getIntDef('id',0))
    if (!hsRes.scomp) {
      response.sendError(404)
      return
    }
    hsRes.iscanedit = recieveSectionPermission(SALBUHEDIT)&&((hsRes.scomp.is_pers&&hsRes.scomp.compstatus<2)||(!hsRes.scomp.is_pers&&hsRes.scomp.perstatus<2))

    return hsRes
  }

  def updatebuhsalarycomp = {
    checkAccess(11)
    if(!checkSectionAccess(5)) return
    if(!checkSectionPermission(SALBUHEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11
    hsRes.result=[errorcode:[]]
 
    hsRes.scomp = Salarycomp.get(requestService.getIntDef('id',0))
    if (!hsRes.scomp||(hsRes.scomp?.is_pers==1&&hsRes.scomp?.compstatus==2)||(hsRes.scomp?.is_pers==0&&hsRes.scomp?.perstatus==2)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,null,null,['fullsalary','netsalary','debtsalary','ndfl','debtndfl','fss_tempinvalid',
                                    'debtfss_tempinvalid','fss_accident','debtfss_accident','ffoms','debtffoms','pf','debtpf'])

    if(hsRes.inrequest.fullsalary&&hsRes.inrequest.fullsalary<0)
      hsRes.result.errorcode<<1
    if(hsRes.scomp.is_pers){
      if(hsRes.inrequest.debtsalary&&hsRes.inrequest.debtsalary<0)
        hsRes.result.errorcode<<2
      if(hsRes.inrequest.netsalary&&hsRes.inrequest.netsalary<0)
        hsRes.result.errorcode<<13
    } else {
      if(hsRes.inrequest.ndfl&&hsRes.inrequest.ndfl<0)
        hsRes.result.errorcode<<3
      if(hsRes.inrequest.debtndfl&&hsRes.inrequest.debtndfl<0)
        hsRes.result.errorcode<<4
      if(hsRes.inrequest.fss_tempinvalid&&hsRes.inrequest.fss_tempinvalid<0)
        hsRes.result.errorcode<<5
      if(hsRes.inrequest.debtfss_tempinvalid&&hsRes.inrequest.debtfss_tempinvalid<0)
        hsRes.result.errorcode<<6
      if(hsRes.inrequest.fss_accident&&hsRes.inrequest.fss_accident<0)
        hsRes.result.errorcode<<7
      if(hsRes.inrequest.debtfss_accident&&hsRes.inrequest.debtfss_accident<0)
        hsRes.result.errorcode<<8
      if(hsRes.inrequest.ffoms&&hsRes.inrequest.ffoms<0)
        hsRes.result.errorcode<<9
      if(hsRes.inrequest.debtffoms&&hsRes.inrequest.debtffoms<0)
        hsRes.result.errorcode<<10
      if(hsRes.inrequest.pf&&hsRes.inrequest.pf<0)
        hsRes.result.errorcode<<11
      if(hsRes.inrequest.debtpf&&hsRes.inrequest.debtpf<0)
        hsRes.result.errorcode<<12
    }

    if(!hsRes.result.errorcode){
      try {
        hsRes.scomp.updateCompBuhData(hsRes.inrequest).updatePersBuhData(hsRes.inrequest).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Salary/updatebuhsalarycomp\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }

  def computebuhreport = {
    checkAccess(11)
    if (!checkSectionAccess(2)) return
    if (!checkSectionPermission(SALBUHEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    def cal = Calendar.getInstance()
    cal.add(Calendar.MONTH,-1)
    if (!Salaryreport.findByMonthAndYearAndSalarytype_id(cal.getTime().getMonth()+1,cal.getTime().getYear()+1900,2)){
      try {
        salaryService.createNewBuhreport(hsRes.user.id,cal.getTime())
      } catch(Exception e) {
        log.debug("Error save data in Salary/computebuhreport\n"+e.toString())
      }
    } else {
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
  }

  def updatebuhinn = {
    checkAccess(11)
    if (!checkSectionAccess(2)) return
    if (!checkSectionPermission(SALBUHEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    def lId = requestService.getLongDef('salcomp_id',0)
    hsRes+=requestService.getParams(null,null,['salinn'])

    hsRes.salarycomp = Salarycomp.get(lId)
    if (!hsRes.salarycomp||!(hsRes.inrequest.salinn).matches('(\\d{10})|(\\d{12})')) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.salarycomp.csiUpdateInn(hsRes.inrequest.salinn).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Salary/updatebuhinn\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def updatebuhsnils = {
    checkAccess(11)
    if (!checkSectionAccess(2)) return
    if (!checkSectionPermission(SALBUHEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    def lId = requestService.getLongDef('salcomp_id',0)
    hsRes+=requestService.getParams(null,null,['salsnils'])

    hsRes.salarycomp = Salarycomp.get(lId)
    if (!hsRes.salarycomp||!(hsRes.inrequest.salsnils).matches('\\d{3}\\-\\d{3}\\-\\d{3}\\s\\d{2}')) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.perstatus = hsRes.salarycomp.csiUpdateSnils(hsRes.inrequest.salsnils).save(failOnError:true).perstatus
    } catch(Exception e) {
      log.debug("Error save data in Salary/updatebuhsnils\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false,perstatus:hsRes.perstatus]}
    return
  }

  def deletebuhreport = {
    checkAccess(11)
    if (!checkSectionAccess(2)) return
    if (!checkSectionPermission(SALBUHEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    def lId = requestService.getLongDef('id',0)

    hsRes.salaryreport = Salaryreport.findByIdAndSalarytype_id(lId,2)
    if (hsRes.salaryreport?.modstatus!=0) {
      response.sendError(404)
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Salarycomp.findAllByMonthAndYear(hsRes.salaryreport.month,hsRes.salaryreport.year).each{ it.delete(flush:true) }
      hsRes.salaryreport.delete(flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Salary/deletebuhreport\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def linkbuhreport = {
    checkAccess(11)
    if (!checkSectionAccess(2)) return
    if (!checkSectionPermission(SALBUHEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    def lId = requestService.getLongDef('id',0)

    hsRes.salaryreport = Salaryreport.findByIdAndSalarytype_id(lId,2)
    if (hsRes.salaryreport?.modstatus!=0) {
      response.sendError(404)
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Salarycomp.findAllByMonthAndYearAndIs_pers(hsRes.salaryreport.month,hsRes.salaryreport.year,0).each{ it.csiUpdateInn(it.companyinn).save(flush:true) }
    } catch(Exception e) {
      log.debug("Error save data in Salary/linkbuhreport\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def accruebuhreport = {
    checkAccess(11)
    if (!checkSectionAccess(2)) return
    if (!checkSectionPermission(SALBUHEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    def lId = requestService.getLongDef('id',0)

    hsRes.salaryreport = Salaryreport.findByIdAndSalarytype_id(lId,2)
    if (hsRes.salaryreport?.modstatus!=0||Salarycomp.find{ month == hsRes.salaryreport?.month && year == hsRes.salaryreport?.year && (compstatus == 0 || perstatus == 0) }) {
      response.sendError(404)
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      def bdSumma = Salarycomp.findAllByMonthAndYear(hsRes.salaryreport.month,hsRes.salaryreport.year).each{ it.accrue().save(flush:true) }.sum{ it.computesum() }
      hsRes.salaryreport.csiSetModstatus(1).csiSetSumma(bdSumma).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Salary/accruebuhreport\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def disaccruebuhreport = {
    checkAccess(11)
    if (!checkSectionAccess(2)) return
    if (!checkSectionPermission(SALBUHEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    def lId = requestService.getLongDef('id',0)

    hsRes.salaryreport = Salaryreport.findByIdAndSalarytype_id(lId,2)
    if (hsRes.salaryreport?.modstatus!=1) {
      response.sendError(404)
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Salarycomp.findAllByMonthAndYear(hsRes.salaryreport.month,hsRes.salaryreport.year).each{ it.disaccrue().save(flush:true) }
      hsRes.salaryreport.csiSetZeroModstatus().csiSetSumma(0g).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Salary/disaccruebuhreport\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def payofficial = {
    checkAccess(11)
    if (!checkSectionAccess(2)) return
    if (!checkSectionPermission(SALOFFEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    def lId = requestService.getLongDef('id',0)

    hsRes.salaryreport = Salaryreport.findByIdAndSalarytype_id(lId,2)
    if (hsRes.salaryreport?.modstatus!=1) {
      response.sendError(404)
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      if(hsRes.salaryreport.csiSetModstatus(salaryService.computeCardsSummaForReport(hsRes.salaryreport)).save(failOnError:true)?.modstatus==2){
        new Salaryreport(month:hsRes.salaryreport.month,year:hsRes.salaryreport.year,salarytype_id:3,department_id:0).setData(null).csiSetSumma(Salarycomp.findAllByMonthAndYear(hsRes.salaryreport.month,hsRes.salaryreport.year).sum{ it.cardmain }?:0g).csiSetModstatus(0).csiSetAdmin(session.user.id).save(failOnError:true)
      }
    } catch(Exception e) {
      log.debug("Error save data in Salary/payofficial\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Buhreport <<</////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Offreport >>>/////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def offilter = {
    checkAccess(11)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    if (session.sallastRequest?.fromDetails){
      hsRes.inrequest = session.sallastRequest
    }

    hsRes.iscanedit = recieveSectionPermission(SALOFFEDIT)
    hsRes.offreports = Salaryreport.findAllBySalarytype_id(3,[order:'desc',sort:'repdate']).collect{[disvalue:String.format('%tY-%<tm',new Date(it.year-1900,it.month-1,1)),keyvalue:String.format('%td.%<tm.%<tY',new Date(it.year-1900,it.month-1,1))]}
    hsRes.curreport = Salaryreport.findBySalarytype_id(3,[order:'desc',sort:'repdate'])

    return hsRes
  }

  def getaddoffbuttons = {
    checkAccess(11)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    hsRes.date = requestService.getDate('date')
    hsRes.curreport = Salaryreport.findByMonthAndYearAndSalarytype_id(hsRes.date.getMonth()+1,hsRes.date.getYear()+1900,3)
    hsRes.iscanedit = recieveSectionPermission(SALOFFEDIT)

    return hsRes
  }

  def offreports = {
    checkAccess(11)
    checkSectionAccess(3)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11
    hsRes.iscanedit = recieveSectionPermission(SALOFFEDIT)

    session.sallastRequest = [:]
    session.sallastRequest.salsection = 3
    hsRes+=requestService.getParams(['perstype'],null,['company_name','pers_name'])
    hsRes.inrequest.repdate = requestService.getDate('repdate')?:new Date()

    hsRes.salreport = Salaryreport.findByMonthAndYearAndSalarytype_id(hsRes.inrequest.repdate.getMonth()+1,hsRes.inrequest.repdate.getYear()+1900,3)
    hsRes.searchresult = new SalarycompSearch().csiSelectOffreports(hsRes.inrequest.repdate,hsRes.inrequest.perstype?:0,hsRes.inrequest.company_name?:'',hsRes.inrequest.pers_name?:'',20,requestService.getOffset())

    return hsRes
  }

  def updateoffcardmain = {
    checkAccess(11)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(SALOFFEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    def lId = requestService.getLongDef('salcomp_id',0)
    hsRes+=requestService.getParams(null,null,null,null,['salmain'])

    hsRes.salarycomp = Salarycomp.get(lId)
    if (!hsRes.salarycomp||hsRes.salarycomp.paidmainstatus!=0||hsRes.salarycomp.paidaddstatus!=0||hsRes.salarycomp.perstype!=2||(hsRes.salarycomp.cardmain+hsRes.salarycomp.cardadd)<=0||hsRes.salarycomp.netsalary<hsRes.inrequest.salmain||(hsRes.inrequest.salmain?:0g)<0g) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.salarycomp.csiUpdateCardmain(hsRes.inrequest.salmain?:0g).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Salary/updateoffcardmain\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def recomputeofficial = {
    checkAccess(11)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(SALOFFEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    hsRes.salaryreport = Salaryreport.findByIdAndSalarytype_id(requestService.getLongDef('id',0),3)
    if (hsRes.salaryreport?.modstatus!=0) {
      response.sendError(404)
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      salaryService.computeCardsSummaForReport(hsRes.salaryreport)
      hsRes.salaryreport.csiSetSumma(Salarycomp.findAllByMonthAndYear(hsRes.salaryreport.month,hsRes.salaryreport.year).sum{ it.cardmain }?:0g).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Salary/recomputeofficial\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def createpayrequests = {
    checkAccess(11)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(SALOFFEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    def lId = requestService.getLongDef('id',0)

    hsRes.salaryreport = Salaryreport.findByIdAndSalarytype_id(lId,3)
    if (hsRes.salaryreport?.modstatus!=0) {
      response.sendError(404)
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      if(hsRes.salaryreport.csiSetModstatus(salaryService.createPayrequests(hsRes.salaryreport)).save(failOnError:true)?.modstatus==1){
        def lsSalary = salaryService.computeSalary(hsRes.salaryreport)
        lsSalary.collect{it.department_id}.unique().each{ dep ->
          new Salaryreport(month:hsRes.salaryreport.month,year:hsRes.salaryreport.year,salarytype_id:5,department_id:dep).setData(null).csiSetSumma(lsSalary.sum{it.department_id!=dep?0:it.cash>0?it.cash:0}?.toBigDecimal()?:0g).csiSetModstatus(0).csiSetAdmin(session.user.id).save(failOnError:true)
        }
      }
    } catch(Exception e) {
      log.debug("Error save data in Salary/createpayrequests\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def closebuhreport = {
    checkAccess(11)
    if (!checkSectionAccess(3)) return
    if (!checkSectionPermission(SALOFFEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    def lId = requestService.getLongDef('id',0)

    hsRes.salaryreport = Salaryreport.findByIdAndSalarytype_id(lId,3)
    if (hsRes.salaryreport?.modstatus!=1) {
      response.sendError(404)
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.salaryreport.csiSetModstatus(2).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Salary/closebuhreport\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Offreport <<</////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Cash >>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def cashfilter = {
    checkAccess(11)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    if (session.sallastRequest?.fromDetails){
      hsRes.inrequest = session.sallastRequest
    }
    hsRes.isalldep = recieveSectionPermission(SALALLDEP)

    return hsRes
  }

  def cashreports = {
    checkAccess(11)
    checkSectionAccess(5)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    if (session.sallastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.sallastRequest
      session.sallastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['modstatus','department_id'])
      hsRes.inrequest.offset = requestService.getOffset()
      session.sallastRequest = hsRes.inrequest
    }
    session.sallastRequest.salsection = 5

    hsRes.searchresult = new SalaryreportSearch().csiSelectCashreports(hsRes.inrequest.modstatus?:0,!recieveSectionPermission(SALALLDEP)?hsRes.user.department_id:hsRes.inrequest.department_id?:0,20,hsRes.inrequest.offset)
    hsRes.departments = Department.list().inject([0:'Директора']){map, department -> map[department.id]=department.name;map}

    return hsRes
  }

  def cashreport = {
    checkAccess(11)
    if(!checkSectionAccess(5)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    def lId=requestService.getIntDef('id',0)
    hsRes.cashreport = Salaryreport.get(lId)
    if (!hsRes.cashreport) {
      response.sendError(404)
      return
    }
    if(!checkReportAccess(hsRes.cashreport,hsRes.user.department_id)) return
    hsRes.iscanclose = recieveSectionPermission(SALNALEDIT)&&(hsRes.cashreport.file&&hsRes.cashreport.modstatus==1&&!Salary.findAllByMonthAndYearAndDepartment_id(hsRes.cashreport.month,hsRes.cashreport.year,hsRes.cashreport.department_id).find{ it.cashstatus==1 })
    hsRes.iscanedit = recieveSectionPermission(SALNALEDIT)
    hsRes.iscanapprove = recieveSectionPermission(SALAPPROVE)
    hsRes.monthworkdays = Tools.computeMonthWorkDays(hsRes.cashreport.month,hsRes.cashreport.year)?:22

    return hsRes
  }

  def updatecashreport = {
    checkAccess(11)
    if(!checkSectionAccess(5)) return
    if(!checkSectionPermission([SALNALEDIT,SALAPPROVE])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getLongDef('id',0)
    hsRes.cashreport = Salaryreport.get(lId)
    if (!hsRes.cashreport) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    if(!checkReportAccess(hsRes.cashreport,hsRes.user.department_id)) return

    hsRes.repdate = requestService.getDate('repdate')
    hsRes.modstatus = requestService.getIntDef('modstatus',0)
    hsRes.is_confirm = requestService.getIntDef('is_confirm',0)
    hsRes.comment = requestService.getStr('comment')
    hsRes.commentdep = requestService.getStr('commentdep')

    if(!hsRes.repdate)
      hsRes.result.errorcode<<1
    if(hsRes.modstatus==2&&!(hsRes.cashreport.file&&hsRes.cashreport.modstatus==1&&!Salary.findAllByMonthAndYearAndDepartment_id(hsRes.cashreport.month,hsRes.cashreport.year,hsRes.cashreport.department_id).find{ it.cashstatus==1 }))
      hsRes.result.errorcode<<2
    if(hsRes.modstatus==1&&hsRes.cashreport.is_confirm!=1&&!recieveSectionPermission(SALNALEDIT))
      hsRes.result.errorcode<<3
    if(hsRes.is_confirm==1&&hsRes.cashreport.is_confirm!=1&&!recieveSectionPermission(SALAPPROVE))
      hsRes.result.errorcode<<4
    if(hsRes.is_confirm==-2&&hsRes.cashreport.is_confirm!=-2&&!recieveSectionPermission(SALAPPROVE))
      hsRes.result.errorcode<<5
    if(hsRes.is_confirm==-1&&hsRes.cashreport.is_confirm==-2&&hsRes.cashreport.modstatus==1&&!hsRes.commentdep)
      hsRes.result.errorcode<<6

    if(!hsRes.result.errorcode){
      try {
        hsRes.cashreport.setData(repdate:hsRes.repdate,comment:hsRes.comment,commentdep:hsRes.commentdep).csiSetModstatus(hsRes.cashreport.modstatus==2&&hsRes.is_confirm==-2?1:hsRes.modstatus).csiSetConfirm(hsRes.is_confirm).save(failOnError:true)
        if(hsRes.modstatus==1) {
          if (hsRes.cashreport.summa>0) new Cashzakaz(department_id:hsRes.cashreport.department_id,initiator:session.user.id).setData(summa:hsRes.cashreport.summa.toLong(),purpose:"Оплата зарплатной наличной ведомости ${!hsRes.cashreport.department_id?'директоров ':''}за ${String.format('%tB %<tY',new Date(hsRes.cashreport.year-1900,hsRes.cashreport.month-1,1))}").csiSetAdmin(session.user.id).csiSetModstatus(0,0).save(failOnError:true)
          Salary.findAllByMonthAndYearAndDepartment_id(hsRes.cashreport.month,hsRes.cashreport.year,hsRes.cashreport.department_id).each{
            if(it.cash<0) User.findByPers_idAndModstatus(it.pers_id,1,[sort:'cassadebt',order:'desc'])?.updateCassadebt(-it.cash)?.save(flush:true)
            it.csiSetCashstatus(it.cash>0?1:2).save(flush:true)
          }
        }
      } catch(Exception e) {
        log.debug("Error save data in Salary/updatecashreport\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def cashsalary = {
    checkAccess(11)
    if(!checkSectionAccess(5)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    def lId=requestService.getIntDef('id',0)
    hsRes.cashreport = Salaryreport.get(lId)
    if (!hsRes.cashreport) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    if(!checkReportAccess(hsRes.cashreport,hsRes.user.department_id)) return

    hsRes.cashpayments = new SalarySearch().csiFindPrepayments(hsRes.cashreport.month,hsRes.cashreport.year,hsRes.cashreport.department_id)
    hsRes.persusers = hsRes.cashpayments.inject([:]){map, salary -> map[salary.id]=User.findByPers_idAndModstatus(salary.pers_id,1,[sort:'cassadebt',order:'desc']);map}
    hsRes.iscanedit = recieveSectionPermission(SALNALEDIT)

    return hsRes
  }

  def cashpayment = {
    checkAccess(11)
    if(!checkSectionAccess(5)) return
    if(!checkSectionPermission(SALNALEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    def lId=requestService.getIntDef('creport_id',0)
    hsRes.cashreport = Salaryreport.get(lId)
    hsRes.cashpayment = Salary.get(requestService.getIntDef('id',0))
    if (!hsRes.cashreport||!hsRes.cashpayment) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    if(!checkReportAccess(hsRes.cashreport,hsRes.user.department_id)) return

    hsRes.pers = Pers.get(hsRes.cashpayment.pers_id)

    return hsRes
  }

  def cashpaymentupdate = {
    checkAccess(11)
    if(!checkSectionAccess(5)) return
    if(!checkSectionPermission(SALNALEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11
    hsRes.result=[errorcode:[]]
 
    def lId=requestService.getIntDef('creport_id',0)
    hsRes.cashreport = Salaryreport.get(lId)
    hsRes.cashpayment = Salary.get(requestService.getIntDef('id',0))
    if (!hsRes.cashreport||hsRes.cashreport.modstatus>1||!hsRes.cashpayment||hsRes.cashpayment.cashstatus>1) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    if(!checkReportAccess(hsRes.cashreport,hsRes.user.department_id)) return

    hsRes+=requestService.getParams(null,['bonus','shtraf','overloadsumma','holiday','precashpayment'],null,null,['overloadhour','prevfix'])
    hsRes.inrequest.actsalary = requestService.getIntDef('actsalary',0)

    if(hsRes.inrequest.actsalary<0)
      hsRes.result.errorcode<<1
    if((hsRes.inrequest.bonus?:0)<0)
      hsRes.result.errorcode<<2
    if((hsRes.inrequest.shtraf?:0)<0)
      hsRes.result.errorcode<<3
    if((hsRes.inrequest.overloadhour?:0)<0)
      hsRes.result.errorcode<<4
    if((hsRes.inrequest.overloadsumma?:0)<0)
      hsRes.result.errorcode<<5
    if((hsRes.inrequest.holiday?:0)<0)
      hsRes.result.errorcode<<6
    if((hsRes.inrequest.precashpayment?:0)<0)
      hsRes.result.errorcode<<7
    if((hsRes.inrequest.prevfix?:0)<0)
      hsRes.result.errorcode<<8

    if(!hsRes.result.errorcode){
      try {
        def lSumma = hsRes.cashpayment.updateCashData(hsRes.inrequest).save(failOnError:true)?.cash
        hsRes.cashreport.csiSetSumma(((Salary.findAllByMonthAndYearAndDepartment_idAndIdNotEqual(hsRes.cashreport.month,hsRes.cashreport.year,hsRes.cashreport.department_id,hsRes.cashpayment.id).sum{ it.cash>0?it.cash:0 }?:0)+(lSumma>0?lSumma:0)).toBigDecimal()).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Salary/cashpaymentupdate\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def paycashpayment = {
    checkAccess(11)
    if(!checkSectionAccess(5)) return
    if(!checkSectionPermission(SALNALEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getIntDef('id',0)
    def iType = requestService.getIntDef('type',0)
    hsRes.cashreport = Salaryreport.get(lId)
    hsRes.cashpayment = Salary.get(requestService.getIntDef('sal_id',0))
    if (hsRes.cashreport?.modstatus!=1||!hsRes.cashpayment||!iType) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    if(!checkReportAccess(hsRes.cashreport,hsRes.user.department_id)) return

    try {
      if(Department.get(hsRes.cashreport.department_id)?.is_extra)
        new Cash().csiSetReceipt(null).csiSetAdmin(session.user.id).setData(maincashtype:1,department_id:hsRes.cashreport.department_id,pers_id:User.findByPers_idAndDepartment_idAndModstatus(hsRes.cashpayment.pers_id,hsRes.cashreport.department_id,1)?.id,maincashclass:4,operationdate:new Date(),platperiod:new Date(hsRes.cashreport.year-1900,hsRes.cashreport.month,1),comment:(iType==1?'зарплата':'отмена зарплаты'),summa:(iType==1?hsRes.cashpayment.cash:-hsRes.cashpayment.cash)).save(failOnError:true)
      else
        new Cashdepartment(department_id:hsRes.cashreport.department_id?:Department.findByIs_tehdir(1).id,pers_id:User.findByPers_idAndModstatus(hsRes.cashpayment.pers_id,1)?.id,is_dep:1).csiSetReceipt(null).csiSetAdmin(session.user.id).setData(depcashtype:1,depcashclass:1,operationdate:new Date(),platperiod:new Date(hsRes.cashreport.year-1900,hsRes.cashreport.month,1),comment:(iType==1?'зарплата':'отмена зарплаты'),summa:(iType==1?hsRes.cashpayment.cash:-hsRes.cashpayment.cash)).save(failOnError:true)
      hsRes.cashpayment.csiSetCashstatus(iType==1?2:1).csiSetCashdate(iType==1?new Date():null).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Salary/paycashpayment\n"+e.toString())
      hsRes.result.errorcode << 100
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Cash <<<//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Taxreport >>>/////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def taxfilter = {
    checkAccess(11)
    checkSectionAccess(9)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    if (session.sallastRequest?.fromDetails){
      hsRes.inrequest = session.sallastRequest
    }
    hsRes.taxes = Tax.findAllByIdGreaterThanEquals(6)
    hsRes.iscanincert = recieveSectionPermission(SALTAXEDIT)

    return hsRes
  }

  def taxreports = {
    checkAccess(11)
    checkSectionAccess(9)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    if (session.sallastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.sallastRequest
      session.sallastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['paystatus','tax_id'],null,['company_name'])
      hsRes.inrequest.offset = requestService.getOffset()
      session.sallastRequest = hsRes.inrequest
    }
    session.sallastRequest.salsection = 9

    hsRes.searchresult = new TaxpaymentSearch().csiSelectTaxes(hsRes.inrequest.company_name?:'',hsRes.inrequest.paystatus?:0,
                                                               hsRes.inrequest.tax_id?:0,20,hsRes.inrequest.offset)
    hsRes.taxes = Tax.list().inject([:]){map, tax -> map[tax.id]=tax.shortname;map}
    hsRes.iscanedit = recieveSectionPermission(SALTAXEDIT)

    return hsRes
  }

  def linktaxreport = {
    checkAccess(11)
    if (!checkSectionAccess(9)) return
    if (!checkSectionPermission(SALTAXEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    try {
      Taxpayment.findAllByPaystatus(-1).each{ it.csiLinkReports().computeStatus().save(flush:true) }
    } catch(Exception e) {
      log.debug("Error save data in Salary/linktaxreport\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def deletetaxreport = {
    checkAccess(11)
    if (!checkSectionAccess(9)) return
    if (!checkSectionPermission(SALTAXEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    hsRes.taxpayment = Taxpayment.get(requestService.getIntDef('id',0))
    if (!hsRes.taxpayment||hsRes.taxpayment.paystatus>0) {
      response.sendError(404)
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.taxpayment.delete(flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Salary/deletetaxreport\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def paytaxreport = {
    checkAccess(11)
    if (!checkSectionAccess(9)) return
    if (!checkSectionPermission(SALTAXEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    hsRes.taxpayment = Taxpayment.get(requestService.getIntDef('id',0))
    if (hsRes.taxpayment?.paystatus!=0) {
      response.sendError(404)
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      def prequest = new Payrequest().filldata(hsRes.taxpayment.csiSetPaystatus(1).save(failOnError:true)).csiSetInitiator(hsRes.user.id).save(failOnError:true)
      def taskpay_id = new Taskpay(paygroup:prequest.paygroup).csiSetTaskpay(term:String.format('%td.%<tm.%<tY',prequest.paydate),company_id:prequest.fromcompany_id,summa:prequest.summa).csiSetInitiator(hsRes.user.id).csiSetTaskpaystatus(0).save(flush:true,failOnError:true)?.id?:0
      prequest.csiSetTaskpay_id(taskpay_id).csiSetModstatus(1).save(flush:true,failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Salary/paytaxreport\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def payalltaxreport = {
    checkAccess(11)
    if (!checkSectionAccess(9)) return
    if (!checkSectionPermission(SALTAXEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    try {
      Taxpayment.findAllByPaystatus(0).each{ taxpayment ->
        def prequest = new Payrequest().filldata(taxpayment.csiSetPaystatus(1).save(failOnError:true)).csiSetInitiator(hsRes.user.id).save(failOnError:true)
        def taskpay_id = new Taskpay(paygroup:prequest.paygroup).csiSetTaskpay(term:String.format('%td.%<tm.%<tY',prequest.paydate),company_id:prequest.fromcompany_id,summa:prequest.summa).csiSetInitiator(hsRes.user.id).csiSetTaskpaystatus(0).save(flush:true,failOnError:true)?.id?:0
        prequest.csiSetTaskpay_id(taskpay_id).csiSetModstatus(1).save(flush:true,failOnError:true)
      }
    } catch(Exception e) {
      log.debug("Error save data in Salary/payalltaxreport\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def addtaxreport = {
    checkAccess(11)
    checkSectionAccess(9)
    if (!checkSectionPermission(SALTAXEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    return hsRes
  }

  def incerttaxreport = {
    checkAccess(11)
    if (!checkSectionAccess(9)) return
    if (!checkSectionPermission(SALTAXEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    hsRes.result = parseService.parseTaxReportFile(request.getFile('file'))

    if(!hsRes.result.errorcode){
      try {
        hsRes.result.preparedData.each{
          if (it.taxsumma>0&&!Taxpayment.findAllByInnAndTax_idAndMonthAndYear(it.companyinn,hsRes.result.tax.id,hsRes.result.reportDate.getMonth()+1,hsRes.result.reportDate.getYear()+1900)) new Taxpayment(tax_id:hsRes.result.tax.id,month:hsRes.result.reportDate.getMonth()+1,year:hsRes.result.reportDate.getYear()+1900,taxyear:hsRes.result.reportDate.getYear()+1900,kvartal:0).setData(it).computeStatus().save(failOnError:true)
        }
      } catch(Exception e) {
        log.debug("Error save data in Salary/incerttaxreport\n"+e.toString())
        hsRes.result.errorcode<<100
      }
    }

    return hsRes.result
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Taxreport <<</////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Personal >>>//////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def personal = {
    checkAccess(11)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11
    if(!hsRes.user.is_leader) {
      response.sendError(403)
      return
    }

    session.sallastRequest = [:]
    session.sallastRequest.salsection = 10
    hsRes.personal = new UserpersSearch().csiFindByDepartment(hsRes.user.department_id)

    return hsRes
  }

  def employee = {
    checkAccess(11)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11
    if(!hsRes.user.is_leader) {
      response.sendError(403)
      return
    }

    hsRes.pers_user = Pers.get(requestService.getLongDef('id',0))
    hsRes.user_dep = Department.get(User.findByPers_idAndModstatus(hsRes.pers_user?.id?:0,1)?.department_id?:-1)
    if (!hsRes.pers_user||(hsRes.user_dep?.id!=hsRes.user.department_id&&hsRes.user_dep?.parent!=hsRes.user.department_id)) {
      response.sendError(404)
      return
    }
    return hsRes
  }

  def psalarylist = {
    checkAccess(11)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11
    if(!hsRes.user.is_leader) {
      response.sendError(403)
      return
    }

    hsRes.pers_user = Pers.get(requestService.getLongDef('id',0))
    hsRes.user_dep = Department.get(User.findByPers_idAndModstatus(hsRes.pers_user?.id?:0,1)?.department_id?:-1)
    if (!hsRes.pers_user||(hsRes.user_dep?.id!=hsRes.user.department_id&&hsRes.user_dep?.parent!=hsRes.user.department_id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.psalary = new Psalary().csiFindPsalary(hsRes.pers_user.id)

    return hsRes
  }

  def psalary = {
    checkAccess(11)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11
    if(!hsRes.user.is_leader) {
      response.sendError(403)
      return
    }

    hsRes.pers_user = Pers.get(requestService.getLongDef('pers_id',0))
    hsRes.user_dep = Department.get(User.findByPers_idAndModstatus(hsRes.pers_user?.id?:0,1)?.department_id?:-1)
    if (!hsRes.pers_user||(hsRes.user_dep?.id!=hsRes.user.department_id&&hsRes.user_dep?.parent!=hsRes.user.department_id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.psalary = Psalary.get(requestService.getLongDef('id',0))

    return hsRes
  }

  def updatePsalary = {
    checkAccess(11)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11
    hsRes.result=[errorcode:[]]
    if(!hsRes.user.is_leader) {
      response.sendError(403)
      return
    }

    hsRes.pers_user = Pers.get(requestService.getLongDef('pers_id',0))
    hsRes.user_dep = Department.get(User.findByPers_idAndModstatus(hsRes.pers_user?.id?:0,1)?.department_id?:-1)
    if (!hsRes.pers_user||(hsRes.user_dep?.id!=hsRes.user.department_id&&hsRes.user_dep?.parent!=hsRes.user.department_id)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    def lId = requestService.getLongDef('id',0)
    hsRes.psalary = Psalary.get(lId)
    if (!hsRes.psalary&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,['actsalary','pers_id'],['comment'],['pdate'])

    if(!hsRes.inrequest.pdate)
      hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        if(!hsRes.psalary) hsRes.psalary = new Psalary()
        hsRes.psalary.csiSetPsalary(hsRes.inrequest,hsRes.user.id).save(flush:true,failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Salary/updatePsalary\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Personal <<<//////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def addsalreportscan = {
    checkAccess(11)
    if(!checkSectionAccess(1)) return
    if(!checkSectionPermission([SALNALEDIT,SALAVEDIT])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getLongDef('id',0)
    hsRes.salreport = Salaryreport.get(lId)
    if (!hsRes.salreport) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    if(!checkReportAccess(hsRes.salreport,hsRes.user.department_id)) return

    imageService.init(this)
    def hsData = imageService.rawUpload('file',true)
    if(hsData.error in [1,3])
        hsRes.result.errorcode<<1

    if(!hsRes.result.errorcode){
      try {
        hsRes.salreport.csiSetFileId(imageService.rawUpload('file').fileid).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Salary/addsalreportscan\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    return hsRes.result
  }

  def showscan = {
    checkAccess(11)
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

  def printreport = {
    checkAccess(11)
    if(!checkSectionAccess(1)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 11

    hsRes.salaryreport = Salaryreport.get(requestService.getIntDef('id',0))
    if (!hsRes.salaryreport) {
      response.sendError(404)
      return
    }
    if (hsRes.salaryreport.department_id!=hsRes.user.department_id&&!recieveSectionPermission(SALALLDEP)) {
      response.sendError(403)
      return
    }
    hsRes.salaries = new SalarySearch().csiFindPrepayments(hsRes.salaryreport.month,hsRes.salaryreport.year,hsRes.salaryreport.department_id)
    hsRes.department = Department.get(hsRes.salaryreport.department_id)

    renderPdf(template:'salaryreport',model:hsRes,filename:"salaryreport_${String.format('%tm_%<tY',new Date(hsRes.salaryreport.year-1900,hsRes.salaryreport.month-1,1))}.pdf")
  }

}
