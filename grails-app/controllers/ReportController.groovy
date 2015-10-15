import grails.converters.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import pl.touk.excel.export.WebXlsxExporter
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

class ReportController {
  def requestService
  def salaryService
  def agentKreditService
  def docexportService

  final String REPALLSAL = 'is_rep_allsalary'

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
    if(iId<1||!session.user.group?."${Reportgroup.get(iId)?.checkfield}"){
      response.sendError(403)
      return false;
    }
    return true
  }

  Boolean checkSubSectionAccess(iId) {
    if(iId<1||!session.user.group?."${Report.get(iId)?.checkfield}"){
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
    checkAccess(12)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12
    if(!requestService.getIntDef('group_id',0)){
      redirect(controller:'report',action:Reportgroup.list().find{session.user.group."$it.checkfield"}?.action?:'error')
      return
    } else if (!checkSectionAccess(requestService.getIntDef('group_id',0))) return
    else session.reportgroup_id = requestService.getIntDef('group_id',0)

    hsRes.reportgroup = Reportgroup.get(session.reportgroup_id)
    hsRes.reports = Report.findAllByReportgroup_id(hsRes.reportgroup.id)

    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Mysalary >>>//////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def mysalaryfilter = {
    checkAccess(12)
    if(!checkSectionAccess(1)) return
    if(!checkSubSectionAccess(1)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.isallsal = recieveSectionPermission(REPALLSAL)
    hsRes.reportdates = hsRes.isallsal?Salaryreport.findAllBySalarytype_id(3,[order:'desc',sort:'repdate']).collect{[disvalue:String.format('%tY-%<tm',new Date(it.year-1900,it.month-1,1)),keyvalue:String.format('%td.%<tm.%<tY',new Date(it.year-1900,it.month-1,1))]}:Salary.findAllByPers_id(hsRes.user.pers_id,[order:'desc',sort:'inputdate']).collect{[disvalue:String.format('%tY-%<tm',new Date(it.year-1900,it.month-1,1)),keyvalue:String.format('%td.%<tm.%<tY',new Date(it.year-1900,it.month-1,1))]}
    hsRes.users = new UserpersSearch().csiFindPersuser()

    return hsRes
  }

  def mysalary = {
    checkAccess(12)
    if(!checkSectionAccess(1)) return
    if(!checkSubSectionAccess(1)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(null,['user_id'])
    hsRes.inrequest.repdate = requestService.getDate('repdate')

    hsRes.searchresult = new SalarySearch().csiFindSalaries(hsRes.inrequest.repdate,!recieveSectionPermission(REPALLSAL)?hsRes.user.pers_id:User.get(hsRes.inrequest.user_id)?.pers_id?:0,20,requestService.getOffset())

    return hsRes
  }

  def printmysalary = {
    checkAccess(12)
    if(!checkSectionAccess(1)) return
    if(!checkSubSectionAccess(1)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.salary = Salary.get(requestService.getLongDef('id',0))
    hsRes.pers = Pers.get(hsRes.salary.pers_id)
    if (!hsRes.salary||!hsRes.pers) {
      response.sendError(404)
      return
    }
    if (hsRes.salary.pers_id!=hsRes.user.pers_id&&!recieveSectionPermission(REPALLSAL)) {
      response.sendError(403)
      return
    }
    hsRes.salarycomps = Salarycomp.findAllByPers_idAndMonthAndYear(hsRes.pers.id,hsRes.salary.month,hsRes.salary.year)

    renderPdf(template:'mysalary',model:hsRes,filename:"salary_${String.format('%tm_%<tY',new Date(hsRes.salary.year-1900,hsRes.salary.month-1,1))}.pdf")
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Mysalary >>>//////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Dirsalary >>>/////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def dirsalaryfilter = {
    checkAccess(12)
    if(!checkSectionAccess(1)) return
    if(!checkSubSectionAccess(2)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def dirsalary = {
    checkAccess(12)
    if(!checkSectionAccess(1)) return
    if(!checkSubSectionAccess(2)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(null,['pers_id'])

    if (hsRes.inrequest.pers_id) {
      hsRes.directors = Pers.findAllByIdAndPerstype(hsRes.inrequest.pers_id,2)
      hsRes.directorscount = Pers.countByIdAndPerstype(hsRes.inrequest.pers_id,2)
    } else {
      hsRes.directors = Pers.findAllByPerstype(2,[sort:'shortname',order:'asc',max:requestService.getStr('viewtype')!='table'?-1:10,offset:requestService.getOffset()])
      hsRes.directorscount = Pers.countByPerstype(2)
    }
    def calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH,-1)
    calendar.set(Calendar.DATE,15)
    def estimateDate = calendar.getTime().clearTime()
    hsRes.dirdetails = hsRes.directors.inject([:]){ map, pers ->
      def jobstartdates = [:]
      def gdcomplist = Company.findAllByIdInList(Compers.findAllByPers_idAndPosition_idAndModstatus(pers.id,1,1).collect{ jobstartdates[it.company_id] = it.jobstart; it.company_id }.unique()?:[0])
      def gbcomplist = Company.findAllByIdInList(Compers.findAllByPers_idAndPosition_idAndModstatusAndCompany_idNotInList(pers.id,2,1,gdcomplist.collect{it.id}?:[0]).collect{it.company_id}.unique()?:[0])
      def agrcomplist = (gdcomplist.collect{it.id}?:[0]).collect{ companyId -> Kredit.findAllByClientAndEnddateGreaterThanAndStartdateLessThanAndAdateGreaterThanEquals(companyId,estimateDate,estimateDate,jobstartdates[companyId])+Cession.findAllByCessionaryAndEnddateGreaterThanAndAdateLessThanAndAdateGreaterThanEqualsAndIs_dirsalary(companyId,estimateDate,estimateDate,jobstartdates[companyId],1)+Lizing.findAllByArendatorAndEnddateGreaterThanAndAdateLessThanAndAdateGreaterThanEqualsAndIs_dirsalary(companyId,estimateDate,estimateDate,jobstartdates[companyId],1) }
      def agrcount = (agrcomplist.collect{ it.size()>0?true:false }-false).size()
      map[pers.id] = [gdcompanies:gdcomplist,jobstartdates:jobstartdates,gbcompanies:gbcomplist,agrcomplist:agrcomplist.flatten(),agrcount:agrcount]
      map
    }
    hsRes.bonusrates = [gdbonus:Tools.getIntVal(Dynconfig.findByName('pers.actsalary.bonus_gd')?.value,5000),gbbonus:Tools.getIntVal(Dynconfig.findByName('pers.actsalary.bonus_gb')?.value,5000),hbonus:Tools.getIntVal(Dynconfig.findByName('pers.actsalary.bonus_sh')?.value,5000),agrbonus:Tools.getIntVal(Dynconfig.findByName('pers.actsalary.bonus_agr')?.value,5000)]

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'dirsalary', model: hsRes, filename: "dirsalary.pdf")
      return
    }

    return hsRes
  }

  def dirsalaryXLS = {
    checkAccess(12)
    if(!checkSectionAccess(1)) return
    if(!checkSubSectionAccess(2)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(null,['pers_id'])

    if (hsRes.inrequest.pers_id) {
      hsRes.directors = Pers.findAllByIdAndPerstype(hsRes.inrequest.pers_id,2)
    } else {
      hsRes.directors = Pers.findAllByPerstype(2,[sort:'shortname',order:'asc',max:-1])
    }

    if (hsRes.directors.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def calendar = Calendar.getInstance()
      calendar.add(Calendar.MONTH,-1)
      calendar.set(Calendar.DATE,15)
      def estimateDate = calendar.getTime().clearTime()
      hsRes.dirdetails = hsRes.directors.inject([:]){ map, pers ->
        def jobstartdates = [:]
        def gdcomplist = Company.findAllByIdInList(Compers.findAllByPers_idAndPosition_idAndModstatus(pers.id,1,1).collect{ jobstartdates[it.company_id] = it.jobstart; it.company_id }.unique()?:[0])
        def gbcomplist = Company.findAllByIdInList(Compers.findAllByPers_idAndPosition_idAndModstatusAndCompany_idNotInList(pers.id,2,1,gdcomplist.collect{it.id}?:[0]).collect{it.company_id}.unique()?:[0])
        def agrcomplist = (gdcomplist.collect{it.id}?:[0]).collect{ companyId -> Kredit.findAllByClientAndEnddateGreaterThanAndStartdateLessThanAndAdateGreaterThanEquals(companyId,estimateDate,estimateDate,jobstartdates[companyId])+Cession.findAllByCessionaryAndEnddateGreaterThanAndAdateLessThanAndAdateGreaterThanEqualsAndIs_dirsalary(companyId,estimateDate,estimateDate,jobstartdates[companyId],1)+Lizing.findAllByArendatorAndEnddateGreaterThanAndAdateLessThanAndAdateGreaterThanEqualsAndIs_dirsalary(companyId,estimateDate,estimateDate,jobstartdates[companyId],1) }
        def agrcount = (agrcomplist.collect{ it.size()>0?true:false }-false).size()
        map[pers.id] = [gdcompanies:gdcomplist,jobstartdates:jobstartdates,gbcompanies:gbcomplist,agrcomplist:agrcomplist.flatten(),agrcount:agrcount]
        map
      }
      hsRes.bonusrates = [gdbonus:Tools.getIntVal(Dynconfig.findByName('pers.actsalary.bonus_gd')?.value,5000),gbbonus:Tools.getIntVal(Dynconfig.findByName('pers.actsalary.bonus_gb')?.value,5000),hbonus:Tools.getIntVal(Dynconfig.findByName('pers.actsalary.bonus_sh')?.value,5000),agrbonus:Tools.getIntVal(Dynconfig.findByName('pers.actsalary.bonus_agr')?.value,5000)]
      def rowCounter = 4
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(1, 2, "Зарплаты директоров")
        putCellValue(2, 2, String.format('%td.%<tm.%<tY %<tH:%<tM',new Date()))
        fillRow(['ФИО','Фикс. оплата','За ген. директора','За главбуха','За договора','Текущий оклад'],3,false,Tools.getXlsTableHeaderStyle(6))
        hsRes.directors.eachWithIndex{ record, index ->
          fillRow([record.shortname,
                   record.is_fixactsalary?'Да':'Нет',
                   Tools.toFixed(hsRes.bonusrates.gdbonus*hsRes.dirdetails[record.id].gdcompanies.size(),2),
                   Tools.toFixed(hsRes.dirdetails[record.id].gbcompanies.size()?hsRes.bonusrates.gbbonus:0,2),
                   Tools.toFixed(hsRes.bonusrates.agrbonus*hsRes.dirdetails[record.id].agrcount,2),
                   record.actsalary], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(6) : index == hsRes.directors.size()-1 ? Tools.getXlsTableLastLineStyle(6) : Tools.getXlsTableLineStyle(6))
        }
        save(response.outputStream)
      }
    }
    return
  }

  def dirsalarycompute = {
    checkAccess(12)
    if(!checkSectionAccess(1)) return
    if(!checkSubSectionAccess(2)) return

    salaryService.computeDirSalary()

    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Dirsalary >>>/////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Dayenquiry >>>////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def dayenquiryfilter = {
    checkAccess(12)
    if(!checkSectionAccess(2)) return
    if(!checkSubSectionAccess(3)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def dayenquiry = {
    checkAccess(12)
    if(!checkSectionAccess(2)) return
    if(!checkSubSectionAccess(3)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.reportdate = requestService.getDate('reportdate')?:new Date()
    hsRes.newenquiries = Enquiry.countByModstatusAndInputdateBetween(0,hsRes.reportdate,hsRes.reportdate+1)
    hsRes.confenquiries = Enquiry.countByModstatusAndStartdate(1,hsRes.reportdate)
    hsRes.expectedenquiries = Enquiry.countByModstatusAndTermdate(1,hsRes.reportdate)
    hsRes.receivedenquiries = Enquiry.countByModstatusAndEnddate(2,hsRes.reportdate)
    hsRes.denyenquiries = Enquiry.countByModstatusAndEnddate(-1,hsRes.reportdate)

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'dayenquiry', model: hsRes, filename: "dayenquiry_${String.format('%td_%<tm_%<tY',hsRes.reportdate)}.pdf")
      return
    }
    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Dayenquiry >>>////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Monthenquiry >>>//////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def monthenquiryfilter = {
    checkAccess(12)
    if(!checkSectionAccess(2)) return
    if(!checkSubSectionAccess(3)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def monthenquiry = {
    checkAccess(12)
    if(!checkSectionAccess(2)) return
    if(!checkSubSectionAccess(3)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.reportdate = requestService.getRaw('reportdate')
    def reportEnd = Calendar.getInstance()
    reportEnd.setTime(hsRes.reportdate)
    reportEnd.add(Calendar.MONTH,1)
    hsRes.newenquiries = Enquiry.countByInputdateBetween(hsRes.reportdate,reportEnd.getTime())
    hsRes.expectedenquiries = Enquiry.countByTermdateBetween(hsRes.reportdate,reportEnd.getTime()-1)
    hsRes.receivedenquiries = Enquiry.countByModstatusAndEnddateBetween(2,hsRes.reportdate,reportEnd.getTime()-1)
    hsRes.denyenquiries = Enquiry.countByModstatusAndEnddateBetween(-1,hsRes.reportdate,reportEnd.getTime()-1)
    hsRes.enqtypes = Enqtype.list().collect{ [name:it.name,count:Enquiry.countByModstatusAndEnddateBetweenAndEnqtype_id(2,hsRes.reportdate,reportEnd.getTime()-1,it.id)] }

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'monthenquiry', model: hsRes, filename: "monthenquiry_${String.format('%tm_%<tY',new Date())}.pdf")
      return
    }
    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Monthenquiry >>>//////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Saldo >>>/////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def saldofilter = {
    checkAccess(12)
    if(!checkSectionAccess(7)) return
    if(!checkSubSectionAccess(5)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def saldo = {
    checkAccess(12)
    if(!checkSectionAccess(7)) return
    if(!checkSubSectionAccess(5)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['valuta_id','sort'],null,['bankname','company'])
    hsRes.reportstart = requestService.getDate('reportstart')
    hsRes.reportend = requestService.getDate('reportend')

    hsRes.searchresult = new ActsaldohistorySearch().csiGetSaldoreport(hsRes.reportstart,hsRes.reportend,hsRes.inrequest.company?:'',hsRes.inrequest.bankname?:'',hsRes.inrequest.valuta_id?:857,hsRes.inrequest.sort?:0,requestService.getStr('viewtype')!='table'?-1:20,requestService.getOffset())
    hsRes.valutas = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code.toLowerCase();map}

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'saldo', model: hsRes, filename: "saldo.pdf")
      return
    }

    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Saldo <<</////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Banksaldo >>>/////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def banksaldofilter = {
    checkAccess(12)
    if(!checkSectionAccess(7)) return
    if(!checkSubSectionAccess(20)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def banksaldo = {
    checkAccess(12)
    if(!checkSectionAccess(7)) return
    if(!checkSubSectionAccess(20)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['valuta_id','sort'],null,['bankname','company'])

    hsRes.searchresult = new BankaccountSearch().csiFindAccountForBanksaldo(hsRes.inrequest.bankname?:'',hsRes.inrequest.company?:'',hsRes.inrequest.valuta_id?:857,hsRes.inrequest.sort?:0,requestService.getStr('viewtype')!='table'?-1:20,requestService.getOffset())
    hsRes.valutas = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code.toLowerCase();map}
    hsRes.saldos = hsRes.searchresult.records.inject([:]){ map, baccount ->
      def cursaldo = baccount.actsaldo -
                    (Taskpay.findAllByModdateGreaterThanEqualsAndBankaccount_idAndTaskpaystatusInList(baccount.actsaldodate?:new Date()-365,baccount.id,[2,4]).sum{it.summa}?:0) +
                    (Payrequest.findAllByPaydateGreaterThanEqualsAndTobankaccount_idAndPaytypeAndModstatusGreaterThanAndInstatusGreaterThan(baccount.actsaldodate?:new Date()-365,baccount.id,2,1,1).sum{it.summa}?:0) +
                    (Payrequest.findAllByPaydateGreaterThanEqualsAndTobankaccount_idAndPaytypeAndModstatusGreaterThanAndPaycatAndIs_dop(baccount.actsaldodate?:new Date()-365,baccount.id,3,2,4,1).sum{it.summa}?:0) -
                    (Payrequest.findAllByPaydateGreaterThanEqualsAndBankaccount_idAndPaytypeAndModstatusGreaterThanAndPaycatAndIs_dop(baccount.actsaldodate?:new Date()-365,baccount.id,3,2,4,1).sum{it.summa}?:0)
      def computedsaldo = cursaldo + (Payrequest.findAllByPaydateGreaterThanEqualsAndTobankaccount_idAndPaytypeAndModstatusGreaterThanAndInstatusGreaterThan(baccount.actsaldodate?:new Date()-365,baccount.id,2,1,0).sum{it.summa}?:0)
      map[baccount.id] = [cursaldo:cursaldo,computedsaldo:computedsaldo]
      map
    }

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'banksaldo', model: hsRes, filename: "banksaldo.pdf")
      return
    }

    return hsRes
  }

  def banksaldoXLS = {
    checkAccess(12)
    if(!checkSectionAccess(7)) return
    if(!checkSubSectionAccess(20)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['valuta_id','sort'],null,['bankname','company'])

    hsRes.report = new BankaccountSearch().csiFindAccountForBanksaldo(hsRes.inrequest.bankname?:'',hsRes.inrequest.company?:'',hsRes.inrequest.valuta_id?:857,hsRes.inrequest.sort?:0,requestService.getStr('viewtype')!='table'?-1:20,requestService.getOffset())

    if (hsRes.report.records.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      hsRes.valutas = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.code.toLowerCase();map}
      hsRes.saldos = hsRes.report.records.inject([:]){ map, baccount ->
        def cursaldo = baccount.actsaldo -
                      (Taskpay.findAllByModdateGreaterThanEqualsAndBankaccount_idAndTaskpaystatusInList(baccount.actsaldodate?:new Date()-365,baccount.id,[2,4]).sum{it.summa}?:0) +
                      (Payrequest.findAllByPaydateGreaterThanEqualsAndTobankaccount_idAndPaytypeAndModstatusGreaterThanAndInstatusGreaterThan(baccount.actsaldodate?:new Date()-365,baccount.id,2,1,1).sum{it.summa}?:0) +
                      (Payrequest.findAllByPaydateGreaterThanEqualsAndTobankaccount_idAndPaytypeAndModstatusGreaterThanAndPaycatAndIs_dop(baccount.actsaldodate?:new Date()-365,baccount.id,3,2,4,1).sum{it.summa}?:0) -
                      (Payrequest.findAllByPaydateGreaterThanEqualsAndBankaccount_idAndPaytypeAndModstatusGreaterThanAndPaycatAndIs_dop(baccount.actsaldodate?:new Date()-365,baccount.id,3,2,4,1).sum{it.summa}?:0)
        def computedsaldo = cursaldo + (Payrequest.findAllByPaydateGreaterThanEqualsAndTobankaccount_idAndPaytypeAndModstatusGreaterThanAndInstatusGreaterThan(baccount.actsaldodate?:new Date()-365,baccount.id,2,1,0).sum{it.summa}?:0)
        map[baccount.id] = [cursaldo:cursaldo,computedsaldo:computedsaldo]
        map
      }
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(1, 4, "Остатки по счетам")
        putCellValue(2, 4, String.format('%td.%<tm.%<tY %<tH:%<tM',new Date()))
        fillRow(['Банк','Компания','Тип счета','Факт. сальдо','Дата','Текущий остаток','Расчетный остаток','СС банка','Дата','СС компании','Подтв. сальдо','Дата'],3,false)
        hsRes.report.records.each{ record ->
          fillRow([record.bankname,
                   record.cname,
                   record.typeaccount_id==1?'расчетный':record.typeaccount_id==2?'корпоративный':record.typeaccount_id==3?'текущий':record.typeaccount_id==4?'транзитный':'накопительный',
                   Tools.toFixed(record.actsaldo,2),
                   record.actsaldodate?String.format('%td.%<tm.%<tY',record.actsaldodate):'нет',
                   Tools.toFixed(hsRes.saldos[record.id].cursaldo,2),
                   Tools.toFixed(hsRes.saldos[record.id].computedsaldo,2),
                   Tools.toFixed(record.banksaldo,2),
                   record.banksaldodate?String.format('%td.%<tm.%<tY',record.banksaldodate):'нет',
                   Tools.toFixed(record.actsaldo-record.banksaldo,2),
                   Tools.toFixed(record.saldo,2),
                   record.saldodate?String.format('%td.%<tm.%<tY',record.saldodate):'нет'], rowCounter++, false)
        }
        save(response.outputStream)
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Banksaldo <<</////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Agentagrprofit >>>////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def agentagrprofitfilter = {
    checkAccess(12)
    if(!checkSectionAccess(3)) return
    if(!checkSubSectionAccess(6)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def agentagrprofit = {
    checkAccess(12)
    if(!checkSectionAccess(3)) return
    if(!checkSubSectionAccess(6)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['report_year','type'])

    hsRes.report = Actagent.findAllByYear((hsRes.inrequest.report_year?:2014))
    hsRes.agrs = Agentagr.findAllByModstatus(1).inject([:]){ map, agentagr -> map[agentagr.id] = [bankname:Bank.get(agentagr.bank_id)?.name,client_name:Client.get(agentagr.client_id)?.name]; map }

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'agentagrprofit', model: hsRes, filename: "agentagrprofit.pdf")
      return
    }

    return hsRes
  }

  def agentagrprofitXLS = {
    checkAccess(12)
    if(!checkSectionAccess(3)) return
    if(!checkSubSectionAccess(6)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['report_year','type'])

    hsRes.report = Actagent.findAllByYear((hsRes.inrequest.report_year?:2014))
    hsRes.agrs = Agentagr.findAllByModstatus(1).inject([:]){ map, agentagr -> map[agentagr.id] = [bankname:Bank.get(agentagr.bank_id)?.name,client_name:Client.get(agentagr.client_id)?.name]; map }

    if (hsRes.report.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      if (!hsRes.inrequest.type) {
        new WebXlsxExporter().with {
          setResponseHeaders(response)
          def titles = ['Клиент','Банк']
          hsRes.report.groupBy{new Date(it.year-1900,it.month-1,1)}.each{ dates ->
            titles << String.format('%tB',dates.key)
          }
          titles << 'Итого'
          fillRow(titles,3,false)
          hsRes.report.groupBy{it.agentagr_id}.each{ record ->
            def row = [hsRes.agrs[record.key].client_name,hsRes.agrs[record.key].bankname]
            hsRes.report.groupBy{new Date(it.year-1900,it.month-1,1)}.each{ dates ->
              row << Tools.toFixed(record.value.find{ dates.key==new Date(it.year-1900,it.month-1,1)}?.profit?:0.0g,2)
            }
            row << Tools.toFixed(record.value.groupBy{new Date(it.year-1900,it.month-1,1)}.collect{ it.value[0] }.sum{ it.profit },2)
            fillRow(row, rowCounter++, false)
          }
          def totalrow = ['','ИТОГО']
          hsRes.report.groupBy{new Date(it.year-1900,it.month-1,1)}.each{ dates ->
            totalrow << Tools.toFixed(dates.value.groupBy{it.agentagr_id}.collect{ it.value[0] }.sum{ it.profit },2)
          }
          totalrow << Tools.toFixed(hsRes.report.groupBy{it.agentagr_id}.collect{ it.value }.sum{ it.groupBy{new Date(it.year-1900,it.month-1,1)}.collect{ it.value[0] }.sum{ it.profit }},2)
          fillRow(totalrow, rowCounter++, false)
          save(response.outputStream)
        }
      } else {
        new WebXlsxExporter().with {
          setResponseHeaders(response)
          fillRow(['Клиент','Банк','I','II','III','IV','Итого'],3,false)
          hsRes.report.groupBy{it.agentagr_id}.each{ record ->
            def row = [hsRes.agrs[record.key].client_name,hsRes.agrs[record.key].bankname]
            (1..4).each { season ->
              row << Tools.toFixed(record.value.findAll{ season==(it.month in 1..3? 1 : it.month in 4..6? 2 : it.month in 7..9? 3 : 4)}.groupBy{new Date(it.year-1900,it.month-1,1)}.collect{ it.value[0] }.sum{ it.profit }?:0.0g,2)
            }
            row << Tools.toFixed(record.value.groupBy{new Date(it.year-1900,it.month-1,1)}.collect{ it.value[0] }.sum{ it.profit },2)
            fillRow(row, rowCounter++, false)
          }
          def totalrow = ['','ИТОГО']
          (1..4).each { season ->
            totalrow << Tools.toFixed(hsRes.report.findAll{ season==(it.month in 1..3? 1 : it.month in 4..6? 2 : it.month in 7..9? 3 : 4) }.groupBy{it.agentagr_id}.collect{ it.value }.sum{ it.groupBy{new Date(it.year-1900,it.month-1,1)}.collect{ it.value[0] }.sum{ it.profit }},2)
          }
          totalrow << Tools.toFixed(hsRes.report.groupBy{it.agentagr_id}.collect{ it.value }.sum{ it.groupBy{new Date(it.year-1900,it.month-1,1)}.collect{ it.value[0] }.sum{ it.profit }},2)
          fillRow(totalrow, rowCounter++, false)
          save(response.outputStream)
        }
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Agentagrprofit <<<////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Agentagrprofitdetail >>>//////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def agentagrprofitdetailfilter = {
    checkAccess(12)
    if(!checkSectionAccess(3)) return
    if(!checkSubSectionAccess(7)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def agentagrprofitdetail = {
    checkAccess(12)
    if(!checkSectionAccess(3)) return
    if(!checkSubSectionAccess(7)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['type'])
    hsRes.reportstart = requestService.getRaw('reportstart')
    hsRes.reportend = requestService.getRaw('reportend')

    hsRes.report = new AgentratekreditplanReportSearch().csiSelectReportPeriods(hsRes.reportstart,hsRes.reportend)
    hsRes.agrs = Agentagr.findAllByModstatus(1).inject([:]){ map, agentagr -> map[agentagr.id] = [bankname:Bank.get(agentagr.bank_id)?.name,client_name:Client.get(agentagr.client_id)?.name]; map }

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'agentagrprofitdetail', model: hsRes, filename: "agentagrprofitdetail.pdf")
      return
    }

    return hsRes
  }

  def agentagrprofitdetailXLS = {
    checkAccess(12)
    if(!checkSectionAccess(3)) return
    if(!checkSubSectionAccess(7)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['type'])
    hsRes.reportstart = requestService.getRaw('reportstart')
    hsRes.reportend = requestService.getRaw('reportend')

    hsRes.report = new AgentratekreditplanReportSearch().csiSelectReportPeriods(hsRes.reportstart,hsRes.reportend)
    hsRes.agrs = Agentagr.findAllByModstatus(1).inject([:]){ map, agentagr -> map[agentagr.id] = [bankname:Bank.get(agentagr.bank_id)?.name,client_name:Client.get(agentagr.client_id)?.name]; map }

    if (hsRes.report.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      if (!hsRes.inrequest.type) {
        new WebXlsxExporter().with {
          setResponseHeaders(response)
          def titles = ['Клиент','Банк','Заемщик','Договор']
          hsRes.report.groupBy{new Date(it.year-1900,it.month-1,1)}.each{ dates ->
            titles << String.format('%tB',dates.key)
          }
          titles << 'Итого'
          fillRow(titles,3,false)
          hsRes.report.groupBy{it.agentkredit_id}.each{ record ->
            def row = [hsRes.agrs[record.value[0].agentagr_id].client_name,
                       hsRes.agrs[record.value[0].agentagr_id].bankname,
                       record.value[0].clientname,
                       record.value[0].anumber + ' от ' + String.format('%td.%<tm.%<tY',record.value[0].adate)]
            hsRes.report.groupBy{new Date(it.year-1900,it.month-1,1)}.each{ dates ->
              row << Tools.toFixed(record.value.find{ dates.key==new Date(it.year-1900,it.month-1,1)}?.recieveProfit()?:0.0g,2)
            }
            row << Tools.toFixed(record.value.sum{ it.recieveProfit() }?:0.0g,2)
            fillRow(row, rowCounter++, false)
          }
          def totalrow = ['','','','ИТОГО']
          hsRes.report.groupBy{new Date(it.year-1900,it.month-1,1)}.each{ dates ->
            totalrow << Tools.toFixed(dates.value.sum{ it.recieveProfit() }?:0.0g,2)
          }
          totalrow << Tools.toFixed(hsRes.report.sum{ it.recieveProfit() }?:0.0g,2)
          fillRow(totalrow, rowCounter++, false)
          save(response.outputStream)
        }
      } else {
        new WebXlsxExporter().with {
          setResponseHeaders(response)
          def titles = ['Клиент','Банк','Заемщик','Договор']
          hsRes.report.groupBy{new Date(it.year-1900,(it.month in 1..3? 1 : it.month in 4..6? 2 : it.month in 7..9? 3 : 4),1)}.each{ season ->
            titles << (season.key.getMonth()==1?'I':season.key.getMonth()==2?'II':season.key.getMonth()==3?'III':'IV')+'.'+(season.key.getYear()+1900)
          }
          titles << 'Итого'
          fillRow(titles,3,false)
          hsRes.report.groupBy{it.agentkredit_id}.each{ record ->
            def row = [hsRes.agrs[record.value[0].agentagr_id].client_name,
                       hsRes.agrs[record.value[0].agentagr_id].bankname,
                       record.value[0].clientname,
                       record.value[0].anumber + ' от ' + String.format('%td.%<tm.%<tY',record.value[0].adate)]
            hsRes.report.groupBy{new Date(it.year-1900,(it.month in 1..3? 1 : it.month in 4..6? 2 : it.month in 7..9? 3 : 4),1)}.each{ season ->
              row << Tools.toFixed(record.value.findAll{ season.key==new Date(it.year-1900,(it.month in 1..3? 1 : it.month in 4..6? 2 : it.month in 7..9? 3 : 4),1)}.sum{ it.recieveProfit() }?:0.0g,2)
            }
            row << Tools.toFixed(record.value.sum{ it.recieveProfit() }?:0.0g,2)
            fillRow(row, rowCounter++, false)
          }
          def totalrow = ['','','','ИТОГО']
          hsRes.report.groupBy{new Date(it.year-1900,(it.month in 1..3? 1 : it.month in 4..6? 2 : it.month in 7..9? 3 : 4),1)}.each{ season ->
            totalrow << Tools.toFixed(season.value.sum{ it.recieveProfit() },2)
          }
          totalrow << Tools.toFixed(hsRes.report.sum{ it.recieveProfit() }?:0.0g,2)
          fillRow(totalrow, rowCounter++, false)
          save(response.outputStream)
        }
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Agentagrprofitdetail <<<//////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Clientpayment >>>/////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def clientpaymentfilter = {
    checkAccess(12)
    if(!checkSectionAccess(4)) return
    if(!checkSubSectionAccess(8)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def clientpayment = {
    checkAccess(12)
    if(!checkSectionAccess(4)) return
    if(!checkSubSectionAccess(8)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['client_id'])
    hsRes.reportstart = requestService.getDate('reportstart')
    hsRes.reportend = requestService.getDate('reportend')

    hsRes.report = new PayrequestClientSearch().csiSelectPayments(hsRes.inrequest.client_id?:0,'',0,
                                                    hsRes.reportstart,hsRes.reportend,0,1,-100,0,0,0,0,0)

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'clientpayment', model: hsRes, filename: "clientpayments.pdf")
      return
    }

    return hsRes
  }

  def clientpaymentXLS = {
    checkAccess(12)
    if(!checkSectionAccess(4)) return
    if(!checkSubSectionAccess(8)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['client_id'])
    hsRes.reportstart = requestService.getDate('reportstart')
    hsRes.reportend = requestService.getDate('reportend')

    hsRes.report = new PayrequestClientSearch().csiSelectPayments(hsRes.inrequest.client_id?:0,'',0,
                                                    hsRes.reportstart,hsRes.reportend,0,1,-100,0,0,0,0,0)

    if (hsRes.report.records.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        fillRow(['Дата платежа','Клиент','Подклиент','Тип платежа','Плательщик','Получатель','Сумма платежа','Процент комиссии','Тип расчета процента','Процент клиента','Процент посредника','Сумма агентских','Сумма к возврату клиенту','Сумма к возврату посреднику','Сумма к учету списания','Остаток клиентского счета'],3,false)
        hsRes.report.records.each{ record ->
          fillRow([String.format('%td.%<tm.%<tY',record.paydate),
                   record.client_name,
                   record.subclient_name,
                   record.is_clientcommission?'Возврат комиссии':record.is_midcommission?'Возврат посреднику':record.paytype==1?'Исходящий':record.paytype==2?'Входящий':record.paytype==3?'Внутренний':record.paytype==4?'Списание':record.paytype==8?'Откуп':record.paytype==9?'Комиссия':record.paytype==7?'Абон. плата':payrequest.paytype==10?'связанный входящий':payrequest.paytype==11?'внешний':'Пополнение',
                   record.paytype==4?'Списание по агентскому договору':record.fromcompany_name?:record.fromcompany?:'нет',
                   record.paytype==4?'Списание по агентскому договору':record.tocompany_name?:record.tocompany?:'нет',
                   Tools.toFixed(record.summa,2),
                   record.compercent,
                   record.percenttype?'деление':'умножение',
                   record.subclient_id?Tools.toFixed(record.supcompercent,2):'нет',
                   record.subclient_id?Tools.toFixed(record.midpercent,2):'нет',
                   Tools.toFixed(record.comission,2),
                   record.subclient_id?Tools.toFixed(record.supcomission,2):'нет',
                   record.subclient_id?Tools.toFixed(record.midcomission,2):'нет',
                   Tools.toFixed(record.clientcommission,2),
                   Tools.toFixed(record.curclientsaldo+record.dinclientsaldo+record.computeClientdelta()?:0.0g,2)], rowCounter++, false)
        }
        save(response.outputStream)
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Clientpayment <<</////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Clientcomission >>>///////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def clientcomissionfilter = {
    checkAccess(12)
    if(!checkSectionAccess(4)) return
    if(!checkSubSectionAccess(9)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def clientcomission = {
    checkAccess(12)
    if(!checkSectionAccess(4)) return
    if(!checkSubSectionAccess(9)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.reportdate = requestService.getRaw('reportdate')

    hsRes.report = new ClientreportSearch().csiSelectClientReport(hsRes.reportdate)
    def requestlist = Payrequest.findAll{ modstatus >= 0 && agent_id == 0 && client_id > 0 && month(paydate) == (hsRes.reportdate.getMonth()+1) && year(paydate) == (hsRes.reportdate.getYear()+1900) }
    hsRes.statistic = [:]
    hsRes.statistic.income = requestlist.sum{ it.paytype in [2,5,8,9] ? it.summa : 0.0g }?:0.0g
    hsRes.statistic.incomecount = requestlist.findAll{ it.paytype in [2,5,8,9] }.size()
    hsRes.statistic.outlay = requestlist.sum{ it.paytype in [1,3,4,7] ? it.summa : 0.0g }?:0.0g
    hsRes.statistic.outlaycount = requestlist.findAll{ it.paytype in [1,3,4,7] }.size()
    hsRes.statistic.comission = requestlist.sum{ it.comission }?:0.0g
    hsRes.statistic.supcomission = requestlist.sum{ it.supcomission }?:0.0g
    hsRes.statistic.retcomission = requestlist.sum{ it.is_clientcommission?it.clientdelta:0.0g }?:0.0g
    hsRes.statistic.midcomission = requestlist.sum{ it.midcomission }?:0.0g
    hsRes.statistic.retmidcomission = requestlist.sum{ it.is_midcommission?it.clientdelta:0.0g }?:0.0g
    hsRes.statistic.repayment = requestlist.sum{ it.clientcommission }?:0.0g
    hsRes.statistic.curclientsaldo = Client.list().sum{ it.computeCurSaldo() }
    hsRes.statistic.startclientsaldo = hsRes.statistic.curclientsaldo + (Payrequest.findAll{ modstatus >= 0 && agent_id == 0 && client_id > 0 && ((month(paydate) < (hsRes.reportdate.getMonth()+1) && year(paydate) == (hsRes.reportdate.getYear()+1900)) || year(paydate) < (hsRes.reportdate.getYear()+1900)) }.sum{ it.computeClientdelta() }?:0.0g)
    hsRes.statistic.endclientsaldo = hsRes.statistic.startclientsaldo + (requestlist.sum{ it.computeClientdelta() }?:0.0g)

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'clientcomission', model: hsRes, filename: "clientcomission.pdf")
      return
    }

    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Clientcomission <<<///////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Client >>>////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def clientfilter = {
    checkAccess(12)
    if(!checkSectionAccess(4)) return
    if(!checkSubSectionAccess(10)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def client = {
    checkAccess(12)
    if(!checkSectionAccess(4)) return
    if(!checkSubSectionAccess(10)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['client_id'])
    hsRes.reportdate = requestService.getRaw('reportdate')

    hsRes.client = Client.get(hsRes.inrequest.client_id)
    def requestlist = Payrequest.findAll{ modstatus >= 0 && agent_id == 0 && ((client_id == (hsRes.client?.id?:-1) && subclient_id == 0) || subclient_id == (hsRes.client?.id?:-1)) && month(paydate) == (hsRes.reportdate.getMonth()+1) && year(paydate) == (hsRes.reportdate.getYear()+1900) }
    hsRes.statistic = [:]
    hsRes.statistic.income = requestlist.sum{ it.paytype in [2,5,8,9] ? it.summa : 0.0g }?:0.0g
    hsRes.statistic.incomecount = requestlist.findAll{ it.paytype in [2,5,8,9] }.size()
    hsRes.statistic.outlay = requestlist.sum{ it.paytype in [1,3,4,7] ? it.summa : 0.0g }?:0.0g
    hsRes.statistic.outlaycount = requestlist.findAll{ it.paytype in [1,3,4,7] }.size()
    hsRes.statistic.comission = requestlist.sum{ it.comission }?:0.0g
    hsRes.statistic.curclientsaldo = hsRes.client?.computeCurSaldo()?:0.0g
    hsRes.statistic.startclientsaldo = hsRes.statistic.curclientsaldo + (Payrequest.findAll{ modstatus >= 0 && agent_id == 0 && ((client_id == (hsRes.client?.id?:-1) && subclient_id == 0) || subclient_id == (hsRes.client?.id?:-1)) && ((month(paydate) < (hsRes.reportdate.getMonth()+1) && year(paydate) == (hsRes.reportdate.getYear()+1900)) || year(paydate) < (hsRes.reportdate.getYear()+1900)) }.sum{ it.computeClientdelta() }?:0.0g)
    hsRes.statistic.endclientsaldo = hsRes.statistic.startclientsaldo + (requestlist.sum{ it.computeClientdelta() }?:0.0g)

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'client', model: hsRes, filename: "client.pdf")
      return
    }

    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Client <<<////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Clientcur >>>/////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def clientcurfilter = {
    checkAccess(12)
    if(!checkSectionAccess(4)) return
    if(!checkSubSectionAccess(11)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def clientcur = {
    checkAccess(12)
    if(!checkSectionAccess(4)) return
    if(!checkSubSectionAccess(11)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['client_id'])
    hsRes.reportstart = requestService.getDate('reportstart')
    hsRes.reportend = requestService.getDate('reportend')
    if (!hsRes.reportend) hsRes.reportend = new Date()
    if (!hsRes.reportstart) hsRes.reportstart = hsRes.reportend

    hsRes.report = new PayrequestClientSearch().csiSelectPayments(hsRes.inrequest.client_id?:0,'',0,
                                                    hsRes.reportstart,hsRes.reportend,0,0,-100,0,0,0,0,0)
    hsRes.client = Client.get(hsRes.inrequest.client_id)
    hsRes.curclientsaldo = hsRes.client?.computeCurSaldo()?:0.0g
    hsRes.startclientsaldo = hsRes.curclientsaldo + (Payrequest.findAll{ modstatus >= 0 && agent_id == 0 && ((client_id == (hsRes.client?.id?:-1) && subclient_id == 0) || subclient_id == (hsRes.client?.id?:-1)) && paydate < hsRes.reportstart }.sum{ it.computeClientdelta() }?:0.0g)
    hsRes.endclientsaldo = hsRes.startclientsaldo + (hsRes.report.records.findAll{ (it.client_id == (hsRes.client?.id?:-1) && it.subclient_id == 0) || it.subclient_id == (hsRes.client?.id?:-1) }.sum{ it.computeClientdelta() }?:0.0g)
    hsRes.clients = Client.list().inject([:]){map, client -> map[client.id]=client.name;map}

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'clientcur', model: hsRes, filename: "clientcur.pdf")
      return
    }

    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Clientcur <<</////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Clientsup >>>/////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def clientsupagentfilter = {
    checkAccess(12)
    if(!checkSectionAccess(4)) return
    if(!checkSubSectionAccess(13)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def clientsupagent = {
    checkAccess(12)
    if(!checkSectionAccess(4)) return
    if(!checkSubSectionAccess(13)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.reportdate = requestService.getDate('reportdate')

    hsRes.client = Client.findByIs_super(1)
    hsRes.report = new Payrequest().csiSelectAgentpaymentsByDate(hsRes.client.id,hsRes.reportdate)
    hsRes.summary = [accrued:hsRes.report.sum{it.summa}?:0,paid:hsRes.report.sum{it.agentcommission}?:0]

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'clientsupagent', model: hsRes, filename: "clientsupagents.pdf")
      return
    }

    return hsRes
  }

  def clientsupagentXLS = {
    checkAccess(12)
    if(!checkSectionAccess(4)) return
    if(!checkSubSectionAccess(13)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.reportdate = requestService.getDate('reportdate')

    hsRes.client = Client.findByIs_super(1)
    hsRes.report = new Payrequest().csiSelectAgentpaymentsByDate(hsRes.client.id,hsRes.reportdate)
    hsRes.summary = [accrued:hsRes.report.sum{it.summa}?:0,paid:hsRes.report.sum{it.agentcommission}?:0]

    if (hsRes.report.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 7
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(1, 3, "Агентские начисления и выплаты по агенту ${hsRes.client?.name} с даты ${String.format('%td.%<tm.%<tY',hsRes.reportdate)}")
        fillRow(['Начислено на выплату','Выплачено','Не оплачено'],3,false)
        fillRow([Tools.toFixed(hsRes.summary.accrued,2),Tools.toFixed(hsRes.summary.paid,2),Tools.toFixed(hsRes.summary.accrued-hsRes.summary.paid,2)],4,false)
        fillRow(['дата начисления','месяц начисления','дата списания','сумма начисления','сумма списания'],6,false)
        hsRes.report.each{ record ->
          fillRow([String.format('%td.%<tm.%<tY',record.inputdate),
                   record.platperiod,
                   record.execdate?String.format('%td.%<tm.%<tY',record.execdate):'нет',
                   Tools.toFixed(record.summa,2),
                   Tools.toFixed(record.agentcommission,2)], rowCounter++, false)
        }
        save(response.outputStream)
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Clientsup <<</////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Cashdep >>>///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def cashdepfilter = {
    checkAccess(12)
    if(!checkSectionAccess(5)) return
    if(!checkSubSectionAccess(12)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def cashdep = {
    checkAccess(12)
    if(!checkSectionAccess(5)) return
    if(!checkSubSectionAccess(12)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.reporttype = requestService.getIntDef('type',0)
    hsRes.reportdate = requestService.getDate('reportdate')?:new Date()

    if(hsRes.reporttype==1){
      hsRes.userreport = new CashdepartmentSearch().csiFindUsersaldoByCashaccess([1,3],hsRes.reportdate)
    } else if(hsRes.reporttype==2){
      hsRes.loanreport = new UserpersSearch().csiFindByLoansaldo()
    } else if(hsRes.reporttype==3){
      hsRes.penaltyreport = new UserpersSearch().csiFindByPenalty()
    } else {
      hsRes.depreport = new CashdepartmentSearch().csiFindDepsaldoByDate(hsRes.reportdate)
      hsRes.departments = Department.list().inject([:]){map, dep -> map[dep.id]=dep.name;map}
    }

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'cashdep', model: hsRes, filename: "cashdep.pdf")
      return
    }

    return hsRes
  }

  def cashdepXLS = {
    checkAccess(12)
    if(!checkSectionAccess(5)) return
    if(!checkSubSectionAccess(12)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['valuta_id','sort'],null,['bankname','company'])

    hsRes.reporttype = requestService.getIntDef('type',0)
    hsRes.reportdate = requestService.getDate('reportdate')?:new Date()

    if(hsRes.reporttype==1){
      hsRes.report = new CashdepartmentSearch().csiFindUsersaldoByCashaccess([1,3],hsRes.reportdate)
    } else if(hsRes.reporttype==2){
      hsRes.report = new UserpersSearch().csiFindByLoansaldo()
    } else if(hsRes.reporttype==3){
      hsRes.report = new UserpersSearch().csiFindByPenalty()
    } else {
      hsRes.report = new CashdepartmentSearch().csiFindDepsaldoByDate(hsRes.reportdate)
      hsRes.departments = Department.list().inject([:]){map, dep -> map[dep.id]=dep.name;map}
    }

    if (hsRes.report.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        if (hsRes.reporttype==1){
          putCellValue(1, 1, "Остатки подотчетных средств подотчетного лица на ${String.format('%td.%<tm.%<tY',hsRes.reportdate)}")
          fillRow(['ФИО','Тип','Итого'],3,false)
          hsRes.report.each{ record ->
            fillRow([record.pers_name,
                     record.cashaccess==1?'подотчетное лицо':'кассир холдинга',
                     record.depusersaldo], rowCounter++, false)
          }
        } else if (hsRes.reporttype==2){
          putCellValue(1, 1, "Остатки по заемным средствам")
          fillRow(['ФИО','Итого'],3,false)
          hsRes.report.each{ record ->
            fillRow([record.pers_name,
                     record.loansaldo], rowCounter++, false)
          }
        } else if (hsRes.reporttype==3){
          putCellValue(1, 1, "Задолженность по штрафам")
          fillRow(['ФИО','Итого'],3,false)
          hsRes.report.each{ record ->
            fillRow([record.pers_name,
                     record.penalty], rowCounter++, false)
          }
        } else {
          putCellValue(1, 1, "Остатки подотчетных средств отдела на ${String.format('%td.%<tm.%<tY',hsRes.reportdate)}")
          fillRow(['Наименование отдела','Сальдо отдела','Сальдо сотрудников','Итого'],3,false)
          hsRes.report.each{ record ->
            fillRow([hsRes.departments[record.id],
                     record.saldo?:0,
                     record.depusersaldo?:0,
                     (record.saldo?:0)+(record.depusersaldo?:0)], rowCounter++, false)
          }
        }
        save(response.outputStream)
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Cashdep <<<///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Bankrequest >>>///////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def bankrequestfilter = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(14)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.companies = new BankaccountSearch().csiFindAccountsForBankrequest()
    hsRes.banks = new BankaccountSearch().csiFindAccounts(hsRes.companies[0]?.company_id,1,1,[is_request:true])

    return hsRes
  }

  def docbanklist = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    requestService.init(this)

    return [banks:new BankaccountSearch().csiFindAccounts(requestService.getIntDef('company_id',0),1,1,[is_request:requestService.getIntDef('type',0)==1?true:false,is_anketa:requestService.getIntDef('type',0)==2?true:false])]
  }

  def bankrequest = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(14)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

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
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Bankrequest <<<///////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Bankanketa >>>////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def bankanketafilter = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(15)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.companies = new BankaccountSearch().csiFindAccountsForBankanketa()
    hsRes.banks = new BankaccountSearch().csiFindAccounts(hsRes.companies[0]?.company_id,1,1,[is_anketa:true])

    return hsRes
  }

  def bankanketa = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(15)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

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
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Bankanketa <<<////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Dopcard >>>///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def dopcardfilter = {
    checkAccess(12)
    if(!checkSectionAccess(1)) return
    if(!checkSubSectionAccess(16)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def dopcard = {
    checkAccess(12)
    if(!checkSectionAccess(1)) return
    if(!checkSubSectionAccess(16)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.reports = []
    hsRes.total = [paid:0,returned:0,comission:0,delta:0]
    def cal = Calendar.getInstance()
    12.times {
      hsRes.reports << [platperiod:String.format('%tB %<tY',cal.getTime()),paid:Payrequest.findAllByModstatusGreaterThanAndPaytypeAndPaycatAndIs_dopAndPlatperiod(1,1,3,1,String.format('%tm.%<tY',cal.getTime())).sum{it.summa}?:0,returned:new CashSearch().csiSelectDCReturn(cal.getTime(),0,0).records.sum{it.summa}?:0,comission:Payrequest.findAllByModstatusGreaterThanAndPaytypeAndPlatperiod(1,9,String.format('%tm.%<tY',cal.getTime())).sum{it.summa}?:0]
      cal.add(Calendar.MONTH,-1)
    }
    hsRes.reports.each {
      hsRes.total.paid += it.paid
      hsRes.total.returned += it.returned
      hsRes.total.comission += it.comission
      hsRes.total.delta += it.paid - it.returned - it.comission
    }
    hsRes.dopcardsaldo = Holding.findByName('dopcardsaldo')?.cashsaldo

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'dopcard', model: hsRes, filename: "dopcard.pdf")
      return
    }

    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Dopcard <<<///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Kreditfolio >>>///////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def kreditfoliofilter = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(17)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.banks = Kredit.getBanks()
    hsRes.companies = Kredit.getCompanies()
    hsRes.users = Kredit.getResponsibles()

    return hsRes
  }

  def bankcompanylist = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(17)) return
    requestService.init(this)

    return [companies:Kredit.getCompanies(requestService.getStr('bank_id'))]
  }

  def kreditfolio = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(17)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['company_id','zalog_id','is_agr','is_nolicense','is_debt','is_active','activitystatus'],
                                    ['responsible'],['bank_id'])
    hsRes.kreditfoliodate = requestService.getDate('kreditfoliodate')?:new Date()
    hsRes.kreditdate_start = requestService.getDate('kreditdate_start')
    hsRes.kreditdate_end = requestService.getDate('kreditdate_end')

    hsRes.report = new KreditfolioSearch().csiSelectKreditfolio(hsRes.kreditfoliodate,hsRes.kreditdate_start,hsRes.kreditdate_end,
                                                 hsRes.inrequest.bank_id?:'',hsRes.inrequest.company_id?:0,hsRes.inrequest.responsible?:0,
                                                 hsRes.inrequest.is_agr?:0,hsRes.inrequest.zalog_id?:0,hsRes.inrequest.is_nolicense?:0,
                                                 hsRes.inrequest.is_debt?:0,hsRes.inrequest.is_active?:0,hsRes.inrequest.activitystatus?:0)

    if (hsRes.report.records.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(2, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      hsRes.kreditzalog = hsRes.report.records.inject([:]){ map, kredit -> map[kredit.id] = Kreditzalog.findAllByKredit_id(kredit.id); map }
      hsRes.kreditsaldo = hsRes.report.records.inject([:]){ map, kredit -> map[kredit.id] = kredit.computesaldo(hsRes.kreditfoliodate); map }
      hsRes.zalogtypes = Zalogtype.list().inject([:]){map, zalogtype -> map[zalogtype.id]=zalogtype.name;map}
      hsRes.actstatuses = Activitystatus.list().inject([:]){map, status -> map[status.id]=status.name;map}
      def rowCounter = 5
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(2, 4, "Общий кредитный портфель по состоянию на ${String.format('%td.%<tm.%<tY',hsRes.kreditfoliodate)}")
        fillRow(['№пп','Заемщик','Статус активности','Банк кредитор','Сумма кредита','Сумма кредита в долларах','Сумма кредита в евро','Текущая задолженность','Текущая задолженность в долларах','Текущая задолженность в евро','Дата договора','Номер договора','Наличие договора','Ставка','Тип кредита','Дата выдачи','Дата погашения','Вид обеспечения','Залоговая стоимость','Рыночная стоимость','Дата договора залога','Номер договора залога','Наличие договора залога','Примечание'],4,false)
        hsRes.report.records.eachWithIndex{ record, i ->
          fillRow([record.id.toString(),
                   record.company_name,
                   record.activitystatus_id in [1,2,5,8]?hsRes.actstatuses[record.activitystatus_id]:record.activitystatus_id==3?'реорганизация':record.activitystatus_id==6?'банкротство':'ликвидация',
                   record.bank_name+(!record.is_license?' (Отозвана лицензия)':''),
                   record.valuta_id==857?Tools.toFixed(record.ds_summa?:record.summa,2):0,
                   record.valuta_id==840?Tools.toFixed(record.ds_summa?:record.summa,2):0,
                   record.valuta_id==978?Tools.toFixed(record.ds_summa?:record.summa,2):0,
                   record.valuta_id==857?Tools.toFixed(hsRes.kreditsaldo[record.id],2):0,
                   record.valuta_id==840?Tools.toFixed(hsRes.kreditsaldo[record.id],2):0,
                   record.valuta_id==978?Tools.toFixed(hsRes.kreditsaldo[record.id],2):0,
                   String.format('%td.%<tm.%<tY',record.adate),
                   record.anumber,
                   record.is_agr?'Да':'Нет',
                   Tools.toFixed(record.ds_rate?:record.rate,2),
                   record.kredtype==1?'Кредит':record.kredtype==2?'Кредитная линия':record.kredtype==3?'Овердрафт':'Линия с лимитом задолженности',
                   String.format('%td.%<tm.%<tY',record.startdate),
                   String.format('%td.%<tm.%<tY',record.ds_enddate?:record.enddate),
                   hsRes.zalogtypes[hsRes.kreditzalog[record.id][0]?.zalogtype_id]?:'',
                   hsRes.kreditzalog[record.id][0]?.zalogcost?Tools.toFixed(hsRes.kreditzalog[record.id][0]?.zalogcost,2):'',
                   hsRes.kreditzalog[record.id][0]?.marketcost?Tools.toFixed(hsRes.kreditzalog[record.id][0]?.marketcost,2):'',
                   hsRes.kreditzalog[record.id][0]?.zalogstart?String.format('%td.%<tm.%<tY',hsRes.kreditzalog[record.id][0]?.zalogstart):'',
                   hsRes.kreditzalog[record.id][0]?.zalogagr?:'',
                   hsRes.kreditzalog[record.id][0]?.is_zalogagr?'Да':'Нет',
                   hsRes.kreditzalog[record.id][0]?.zalogprim?:''], rowCounter++, false)
          if(hsRes.kreditzalog[record.id].size()>1){
            hsRes.kreditzalog[record.id].eachWithIndex{ zalog, j ->
              if(j)
                fillRow(['','','','','','','','','','','','',
                         hsRes.zalogtypes[zalog.zalogtype_id],
                         zalog.zalogcost?Tools.toFixed(zalog.zalogcost,2):'',
                         zalog.marketcost?Tools.toFixed(zalog.marketcost,2):'',
                         zalog.zalogstart?String.format('%td.%<tm.%<tY',zalog.zalogstart):'',
                         zalog.zalogagr?:'',
                         zalog.is_zalogagr?'Да':'Нет',
                         zalog.zalogprim?:''], rowCounter++, false)
            }
          }
        }
        fillRow(['ИТОГО','','','',hsRes.report.records.sum{it.valuta_id==857?Tools.toFixed(it.ds_summa?:it.summa,2):0},hsRes.report.records.sum{it.valuta_id==840?Tools.toFixed(it.ds_summa?:it.summa,2):0},hsRes.report.records.sum{it.valuta_id==978?Tools.toFixed(it.ds_summa?:it.summa,2):0},hsRes.report.records.sum{it.valuta_id==857?Tools.toFixed(hsRes.kreditsaldo[it.id],2):0},hsRes.report.records.sum{it.valuta_id==840?Tools.toFixed(hsRes.kreditsaldo[it.id],2):0},hsRes.report.records.sum{it.valuta_id==978?Tools.toFixed(hsRes.kreditsaldo[it.id],2):0}],rowCounter++,false)
        save(response.outputStream)
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Kreditfolio <<<///////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Kreditcompany >>>/////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def kreditcompanyfilter = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(18)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.companies = Kredit.getCompanies()

    return hsRes
  }

  def kreditcompany = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(17)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['company_id'])
    hsRes.reportdate = requestService.getDate('reportdate')?:new Date()
    hsRes.stopdate = requestService.getDate('stopdate')

    hsRes.company = Company.get(hsRes.inrequest.company_id)
    hsRes.report = new KreditfolioSearch().csiSelectKreditCompany(hsRes.inrequest.company_id?:0,hsRes.reportdate,hsRes.stopdate)

    if (hsRes.report.records.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(2, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 6
      hsRes.valutas = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=valuta.shortname;map}
      hsRes.zalogtypes = Zalogtype.list().inject([:]){map, zalogtype -> map[zalogtype.id]=zalogtype.name;map}
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(2, 4, "СПРАВКА ПО КОМПАНИИ")
        putCellValue(3, 4, hsRes.company.name)
        putCellValue(4, 4, "По состоянию на ${String.format('%td.%<tm.%<tY',hsRes.reportdate)}")
        hsRes.report.records.each{ record ->
          putCellValue(rowCounter++, 1, "Кредитный договор ${record.anumber} от ${String.format('%td.%<tm.%<tY',record.adate)}")
          putCellValue(rowCounter++, 1, "Банк-кредитор ${record.bank_name+(!record.is_license?' (Отозвана лицензия)':'')}")
          putCellValue(rowCounter, 1, "Сумма кредита")
          putCellValue(rowCounter, 3, record.ds_summa?:record.summa)
          putCellValue(rowCounter, 4, "${hsRes.valutas[record.valuta_id]}")
          putCellValue(rowCounter, 5, "Сумма по факту")
          putCellValue(rowCounter, 7, Tools.toFixed(record.computesaldo(hsRes.reportdate),2))
          putCellValue(rowCounter++, 8, "${hsRes.valutas[record.valuta_id]}")
          putCellValue(rowCounter++, 1, "Дата окончания ${String.format('%td.%<tm.%<tY',record.ds_enddate?:record.enddate)}")
          putCellValue(rowCounter, 1, "Ставка")
          putCellValue(rowCounter, 3, Tools.toFixed(record.ds_rate?:record.rate,2))
          putCellValue(rowCounter++, 4, "% годовых")
          rowCounter++
          def dopagrs = Kreditdopagr.findAllByKredit_idAndStartdateLessThanEquals(record.id,hsRes.reportdate,[sort:'id',order:'asc'])
          if (!dopagrs) putCellValue(rowCounter++, 1, "Дополнительных соглашений нет")
          else {
            putCellValue(rowCounter++, 1, "Первоначальные условия по договору ${dopagrs[0].nomer} от ${String.format('%td.%<tm.%<tY',dopagrs[0].dsdate)}")
            fillRow(['',
                     String.format('%td.%<tm.%<tY',dopagrs[0].startdate),
                     String.format('%td.%<tm.%<tY',dopagrs[0].enddate),
                     dopagrs[0].summa,
                     Tools.toFixed(dopagrs[0].rate,2)], rowCounter++, false)
            if (dopagrs.size()>1){
              rowCounter++
              putCellValue(rowCounter++, 1, "Дополнительные соглашения")
              fillRow(['','номер соглашения','дата начала','дата окончания','сумма','ставка','признак пролонгации','признак залога'],++rowCounter,false, [null]+Tools.getXlsTableHeaderStyle(7))
              dopagrs.tail().eachWithIndex{ dopagr, index ->
                fillRow(['',dopagr.nomer,
                         String.format('%td.%<tm.%<tY',dopagr.startdate),
                         String.format('%td.%<tm.%<tY',dopagr.enddate),
                         dopagr.summa,
                         Tools.toFixed(dopagr.rate,2),
                         dopagr.is_prolong?'да':'нет',
                         record.zalogstatus==2?'да':'нет'], ++rowCounter, false, index == 0 ? [null]+Tools.getXlsTableFirstLineStyle(7) : index == dopagrs.tail().size()-1 ? [null]+Tools.getXlsTableLastLineStyle(7) : [null]+Tools.getXlsTableLineStyle(7))
              }
            }
          }
          rowCounter +=2
          def zalogagrs = Kreditzalog.findAllByKredit_idAndZalogstartLessThanEquals(record.id,hsRes.reportdate,[sort:'id',order:'desc'])
          if (!zalogagrs) putCellValue(rowCounter++, 1, "Договоров обеспечения нет")
          else {
            putCellValue(rowCounter++, 1, "Обеспечение")
            fillRow(['','номер договора','Дата заключения','Дата окончания','Вид','Рыночная стоимость, руб','Залоговая стоимость, руб'],++rowCounter,false, [null]+Tools.getXlsTableHeaderStyle(6))
          }
          zalogagrs.eachWithIndex{ zalogagr, index ->
            fillRow(['',zalogagr.zalogagr,
                     zalogagr.zalogstart?String.format('%td.%<tm.%<tY',zalogagr.zalogstart):'нет',
                     zalogagr.zalogend?String.format('%td.%<tm.%<tY',zalogagr.zalogend):'нет',
                     hsRes.zalogtypes[zalogagr.zalogtype_id],
                     Tools.toFixed(zalogagr.marketcost,2),
                     Tools.toFixed(zalogagr.zalogcost,2)], ++rowCounter, false, index == 0 ? [null]+Tools.getXlsTableFirstLineStyle(6) : index == zalogagrs.size()-1 ? [null]+Tools.getXlsTableLastLineStyle(6) : [null]+Tools.getXlsTableLineStyle(6))
          }
          rowCounter += 2
          if (!zalogagrs.find{it.strakhnumber!=''}) putCellValue(rowCounter++, 1, "Договоров страхования нет")
          else {
            putCellValue(rowCounter++, 1, "Страхование")
            fillRow(['','Номер договора залога','Номер договора страхования','Дата заключения','Дата страхования','Страховая сумма, руб'],++rowCounter,false, [null]+Tools.getXlsTableHeaderStyle(5))
          }
          zalogagrs.eachWithIndex{ zalogagr, index ->
            if(zalogagr.strakhnumber!='')
              fillRow(['',zalogagr.zalogagr,
                       zalogagr.strakhnumber,
                       zalogagr.strakhdate?String.format('%td.%<tm.%<tY',zalogagr.strakhdate):'нет',
                       zalogagr.strakhvalidity?String.format('%td.%<tm.%<tY',zalogagr.strakhvalidity):'нет',
                       zalogagr.strakhsumma], ++rowCounter, false, index == 0 ? [null]+Tools.getXlsTableFirstLineStyle(5) : index == zalogagrs.size()-1 ? [null]+Tools.getXlsTableLastLineStyle(5) : [null]+Tools.getXlsTableLineStyle(5))
          }
          rowCounter += 2
          def payments = new KreditpaymentSearch().csiFindKreditPayment(record.id)
          def paidsummas = [body:0,percaccrued:0,percpaid:0,bodydebt:0,percdebt:0]
          payments.each{ payment ->
            if(payment.paydate<hsRes.reportdate){
              paidsummas.percaccrued += payment.summapercrub
              if (payment.paidstatus==2) paidsummas.body += payment.summarub
              else paidsummas.bodydebt += payment.summarub
              if (payment.percpaidstatus==2) paidsummas.percpaid += payment.summapercrub
              else paidsummas.percdebt += payment.summapercrub
            }
          }
          putCellValue(rowCounter, 1, "Сумма погашенного кредита")
          putCellValue(rowCounter++, 5, paidsummas.body)
          putCellValue(rowCounter, 1, "Сумма процентов начисленных")
          putCellValue(rowCounter++, 5, paidsummas.percaccrued)
          putCellValue(rowCounter, 1, "Сумма процентов уплаченных")
          putCellValue(rowCounter++, 5, paidsummas.percpaid)
          putCellValue(rowCounter, 1, "Сумма просроченных платежей по кредиту")
          putCellValue(rowCounter++, 5, paidsummas.bodydebt)
          putCellValue(rowCounter, 1, "Сумма просроченных платежей по процентам")
          putCellValue(rowCounter++, 5, paidsummas.percdebt)
          rowCounter += 2
          if (!payments) putCellValue(rowCounter++, 1, "График платежей не предусмотрен")
          else {
            putCellValue(rowCounter++, 1, "График платежей, руб.")
            fillRow(['','Год','Дата','Сумма погашения по кредиту','Сумма по уплате процентов','Итого'],++rowCounter,false, [null]+Tools.getXlsTableHeaderStyle(5))
          }
          payments.eachWithIndex{ payment, index ->
            fillRow(['',(payment.paydate.getYear()+1900).toString(),
                     String.format('%td.%<tm.%<tY',payment.paydate),
                     Tools.toFixed(payment.summarub,2),
                     Tools.toFixed(payment.summapercrub,2),
                     Tools.toFixed(payment.summarub+payment.summapercrub,2)], ++rowCounter, false, index == 0 ? [null]+Tools.getXlsTableFirstLineStyle(5) : index == payments.size()-1 ? [null]+Tools.getXlsTableLastLineStyle(5) : [null]+Tools.getXlsTableLineStyle(5) )
          }
          rowCounter += 4
        }
        save(response.outputStream)
      }
    }
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Kreditcompany <<</////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Spacefolio >>>////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def spacefoliofilter = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(19)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.companies = Space.getCompanies()

    return hsRes
  }

  def spacefolio = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(19)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['company_id'])
    hsRes.spacefoliodate = requestService.getDate('spacefoliodate')?:new Date()
    hsRes.spacedate_start = requestService.getDate('spacedate_start')
    hsRes.spacedate_end = requestService.getDate('spacedate_end')

    hsRes.report = new SpacefolioSearch().csiSelectSpacefolio(hsRes.spacefoliodate,hsRes.spacedate_start,
                                                 hsRes.spacedate_end,hsRes.inrequest.company_id?:0)

    if (hsRes.report.records.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(2, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      hsRes.company = Company.get(hsRes.inrequest.company_id)
      hsRes.spacetypes = Spacetype.list().inject([:]){map, spacetype -> map[spacetype.id]=spacetype.name;map}
      def rowCounter = 6
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(2, 3, "Реестр договоров аренды, заключенных ${hsRes.company.name} по состоянию на ${String.format('%td.%<tm.%<tY',hsRes.spacefoliodate)}")
        putCellValue(3, 4, "${hsRes.spacedate_start?'с '+String.format('%td.%<tm.%<tY',hsRes.spacedate_start):''}${hsRes.spacedate_end?' по '+String.format('%td.%<tm.%<tY',hsRes.spacedate_end):''}")
        fillRow(['Арендодатель','Дата договора','Номер договора','Срок действия','Порядок пролонгации','Адрес','Площадь','Тип помещений','Сумма платежа','Сумма доп. платежей','Периодичность','Сумма изменена'],5,false)
        hsRes.report.records.each{ record ->
          fillRow([record.arendodatel_name,
                   String.format('%td.%<tm.%<tY',record.ds_adate?:record.adate),
                   record.ds_anumber?:record.anumber,
                   String.format('%td.%<tm.%<tY',record.ds_enddate?:record.enddate),
                   record.prolongcondition==2?'А':'Н',
                   record.shortaddress,
                   record.area,
                   hsRes.spacetypes[record.spacetype_id],
                   record.ds_rate?:record.rate,
                   record.ds_ratedop?:record.ratedop,
                   (record.ds_payterm?:record.payterm)+' ежемесячно',
                   record.is_changeprice?'Да':'Нет'], rowCounter++, false)
        }
        save(response.outputStream)
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Spacefolio <<<////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Subspace >>>//////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def subspacefilter = {
    checkAccess(12)
    if(!checkSectionAccess(8)) return
    if(!checkSubSectionAccess(21)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.companies = Space.getCompaniesExt()

    return hsRes
  }

  def subspace = {
    checkAccess(12)
    if(!checkSectionAccess(8)) return
    if(!checkSubSectionAccess(21)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['company_id'])

    def oSearch = new SpaceSearch()
    hsRes.report = oSearch.csiFindSpace(hsRes.inrequest.company_id?:0,1)

    if (hsRes.report.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(2, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      hsRes.subspaces = hsRes.report.inject([:]){ map, space -> map[space.id] = oSearch.csiFindSpace(0,-100,space.id); map }
      def rowCounter = 6
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(2, 3, "Список состояния договоров аренды на ${String.format('%td.%<tm.%<tY',new Date())}")
        fillRow(['Адрес аренды','Арендодатель','Арендатор','Номер договора','Дата договора','Срок действия','Кол-во помещений','Свободные помещения','Субарендатор','Номер договора','Дата договора','Срок действия','Метраж','Стоимость аренды','Условие продления','Статус разрешения','Статус работы'],5,false)
        hsRes.report.each{ record ->
          fillRow([record.shortaddress,
                   record.arendodatel_name,
                   record.arendator_name,
                   record.anumber,
                   String.format('%td.%<tm.%<tY',record.adate),
                   String.format('%td.%<tm.%<tY',record.enddate),
                   record.subspaceqty?:'Без права субаренды',
                   record.subspaceqty-hsRes.subspaces[record.id].size(),
                   hsRes.subspaces[record.id][0]?.arendator_name?:'',
                   hsRes.subspaces[record.id][0]?.anumber?:'',
                   hsRes.subspaces[record.id][0]?.adate?String.format('%td.%<tm.%<tY',hsRes.subspaces[record.id][0].adate):'',
                   hsRes.subspaces[record.id][0]?.enddate?String.format('%td.%<tm.%<tY',hsRes.subspaces[record.id][0].enddate):'',
                   hsRes.subspaces[record.id][0]?.area?Tools.toFixed(hsRes.subspaces[record.id][0].area,2):'',
                   hsRes.subspaces[record.id][0]?.rate?:'',
                   hsRes.subspaces[record.id][0]?.prolongcondition==0?'без пролонгации':hsRes.subspaces[record.id][0]?.prolongcondition==1?'по доп соглашению':hsRes.subspaces[record.id][0]?.prolongcondition==2?'автоматически':hsRes.subspaces[record.id][0]?.prolongcondition==3?'с уведомлением':'',
                   hsRes.subspaces[record.id][0]?.permitstatus==0?'Нет информации':hsRes.subspaces[record.id][0]?.permitstatus==1?'Разрешено':hsRes.subspaces[record.id][0]?.permitstatus==-1?'Отказано':'',
                   hsRes.subspaces[record.id][0]?.workstatus==0?'Нет информации':hsRes.subspaces[record.id][0]?.workstatus==1?'Принято к исполнению':''], rowCounter++, false)
          if(hsRes.subspaces[record.id].size()>1){
            hsRes.subspaces[record.id].tail().each{ subspace ->
              fillRow(['','','','','','','','',
                       subspace.arendator_name,
                       subspace.anumber,
                       String.format('%td.%<tm.%<tY',subspace.adate),
                       String.format('%td.%<tm.%<tY',subspace.enddate),
                       subspace.area?Tools.toFixed(subspace.area,2):'',
                       subspace.rate,
                       subspace.prolongcondition==0?'без пролонгации':subspace.prolongcondition==1?'по доп соглашению':subspace.prolongcondition==2?'автоматически':subspace.prolongcondition==3?'с уведомлением':'',
                       subspace.permitstatus==0?'Нет информации':subspace.permitstatus==0?'Разрешено':'Отказано',
                       subspace.workstatus==0?'Нет информации':'Принято к исполнению'], rowCounter++, false)
            }
          }
        }
        save(response.outputStream)
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Subspace <<<//////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Spacesummary >>>//////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def spacesummaryfilter = {
    checkAccess(12)
    if(!checkSectionAccess(8)) return
    if(!checkSubSectionAccess(26)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def spacesummary = {
    checkAccess(12)
    if(!checkSectionAccess(8)) return
    if(!checkSubSectionAccess(26)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['spacetype_id','arendatype_id','subrenting','is_adrsame','payterm_from','payterm_to'],null,['arendator_name','arendodatel_name','address'])
    hsRes.inrequest.enddate_from = requestService.getDate('enddate_from')
    hsRes.inrequest.enddate_to = requestService.getDate('enddate_to')

    hsRes.report = new SpaceSummarySearch().csiSelectSpaces(hsRes.inrequest,0,0)
    hsRes.checktypes = Bankchecktype.list().inject([:]){map, type -> map[type.id]=[name:type.name,shortname:type.shortname];map}
    hsRes.spacetypes = Spacetype.list().inject([:]){map, spacetype -> map[spacetype.id]=spacetype.name;map}

    def rowCounter = 16

    new WebXlsxExporter().with {
      setResponseHeaders(response)
      setColumnWidth(0,40)
      setColumnWidth(15,80*256)
      setColumnWidth(4,50*256)
      putCellValue(1, 2, "Договора аренды на ${String.format('%td.%<tm.%<tY',new Date())}")
      putCellValue(2, 0, "Настройка фильтра:")
      putCellValue(3, 0, "Арендодатель: ${hsRes.inrequest.arendodatel_name?:'нет'}")
      putCellValue(4, 0, "Арендатор: ${hsRes.inrequest.arendator_name?:'нет'}")
      putCellValue(5, 0, "Ключевое слово адреса: ${hsRes.inrequest.address?:'нет'}")
      putCellValue(6, 0, "Тип помещений: ${hsRes.spacetypes[hsRes.inrequest.spacetype_id]?:'нет'}")
      putCellValue(7, 0, "Тип аренды: ${hsRes.inrequest.arendatype_id==1?'внешняя':hsRes.inrequest.arendatype_id==2?'внутренняя':'все'}")
      putCellValue(8, 0, "Субаренда: ${hsRes.inrequest.subrenting==1?'Разрешена':hsRes.inrequest.subrenting==2?'С письменного разрешения':hsRes.inrequest.subrenting==3?'Без права субаренды':'все'}")
      putCellValue(9, 0, "Совпадение юридического и фактических адресов: ${hsRes.inrequest.is_adrsame?'Да':'Не обязательно'}")
      putCellValue(10, 0, "День оплаты с: ${hsRes.inrequest.payterm_from?:'не задан'}")
      putCellValue(11, 0, "День оплаты по: ${hsRes.inrequest.payterm_to?:'не задан'}")
      putCellValue(12, 0, "Окончание с: ${hsRes.inrequest.enddate_from?String.format('%td.%<tm.%<tY',hsRes.inrequest.enddate_from):'не задано'}")
      putCellValue(13, 0, "Окончание по: ${hsRes.inrequest.enddate_to?String.format('%td.%<tm.%<tY',hsRes.inrequest.enddate_to):'не задано'}")
      fillRow(['','Арендодатель','Арендатор','Тип аренды','Адрес аренды','Площадь помещений','Номер договора','Дата договора','Срок действия','Сумма платежей','Срок оплаты','Дата последней оплаты','Сумма последней оплаты','Ген. директор','Цена за метр','Проверка банков'],15,false)
      hsRes.report.records.groupBy{ it.sid }.collect{ it.value }.each{ record ->
        def oPayrequest = Payrequest.findByAgreementtype_idAndAgreement_idAndModstatusGreaterThan(2,record[0].sid,-1,[sort:'paydate',order:'desc'])
        fillRow(['',record[0].arendodatel_name,
                 record[0].arendator_name,
                 hsRes.spacetypes[record[0].spacetype_id],
                 record[0].fulladdress,
                 record[0].area,
                 record[0].anumber,
                 String.format('%td.%<tm.%<tY',record[0].adate),
                 String.format('%td.%<tm.%<tY',record[0].enddate),
                 record[0].rate+record[0].ratedop,
                 record[0].payterm.toString(),
                 oPayrequest?String.format('%td.%<tm.%<tY',oPayrequest.paydate):'нет',
                 oPayrequest?.summa?:0.0g,
                 record[0].arendator_gd,
                 record[0].ratemeter,
                 record.collect{ it.checkdate?String.format('%td.%<tm.%<tY',it.checkdate)+' - '+hsRes.checktypes[it.checktype_id].shortname+' - '+it.bank_name:'' }.join(' \n\r')], rowCounter++, false, { st -> def styles = []; 15.times{ styles << null }; styles << [wrap:true] }.call())
      }
      setColumnAutoWidth(1)
      setColumnAutoWidth(2)
      setColumnAutoWidth(6)
      save(response.outputStream)
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Spacesummary <<<//////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Bankdir >>>///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def bankdirfilter = {
    checkAccess(12)
    if(!checkSectionAccess(7)) return
    if(!checkSubSectionAccess(22)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def bankdir = {
    checkAccess(12)
    if(!checkSectionAccess(7)) return
    if(!checkSubSectionAccess(22)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['cgroup_id','typeaccount_id','activitystatus_id','is_noclosed','is_active'],null,['bankname','dirname'])

    hsRes.report = new BankdirSearch().csiFindBankdirs(hsRes.inrequest.cgroup_id?:0,hsRes.inrequest.bankname?:'',hsRes.inrequest.dirname?:'',hsRes.inrequest.typeaccount_id?:0,hsRes.inrequest.activitystatus_id?:0,hsRes.inrequest.is_noclosed?:0,hsRes.inrequest.is_active?:0)

    if (hsRes.report.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(2, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      hsRes.cgroups = Cgroup.list().inject([:]){map, group -> map[group.id]=group.name;map}
      hsRes.actstatus = Activitystatus.list().inject([:]){map, status -> map[status.id]=status.name;map}
      hsRes.outsources = Outsource.list().inject([:]){map, source -> map[source.id]=source.name;map}
      hsRes.compokveds = hsRes.report.collect{it.company_id}.unique().inject([:]){map, company_id -> map[company_id]=Compokved.findAllByCompany_idAndModstatusAndIs_main(company_id,1,0).collect{it.okved_id}.join(', ');map}
      def prevCID = 0
      def rowCounter = 4
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        fillRow(['Статус','Группа компаний','Компания','Банк','ФИО директора','Бухгалтер','Тип счета','Дата открытия счета','Дата закрытия счета','Статус БК','Дата активации БК','Срок действия БК','Комментарий к БК','ИНН','Основной оквэд','Дополнительные оквэд'],3,false)
        hsRes.report.each{ record ->
          if(prevCID!=record.company_id){
            prevCID = record.company_id
            fillRow([hsRes.actstatus[record.activitystatus_id]?:'',
                     hsRes.cgroups[record.cgroup_id]?:'',
                     record.cname,'',
                     record.gd,'','','','','','','','',record.inn,
                     record.okvedmain,hsRes.compokveds[record.company_id]], rowCounter++, false)
          }
          if (record.bank_id)
            fillRow([hsRes.actstatus[record.activitystatus_id]?:'',
                     hsRes.cgroups[record.cgroup_id]?:'',
                     record.cname,
                     record.bankname,
                     record.p_shortname?:'',
                     record.outsource_id?hsRes.outsources[record.outsource_id]:'Наша бухгалтерия',
                     record.closedate?'закрыт':!record.is_license?'отозванный':record.typeaccount_id==1?'расчетный':record.typeaccount_id==2?'корпоративный':record.typeaccount_id==3?'текущий':record.typeaccount_id==4?'транзитный':record.typeaccount_id==5?'накопительный':record.typeaccount_id==6?'планируемый':'отказ в открытии',
                     record.opendate?String.format('%td.%<tm.%<tY',record.opendate):'',
                     record.closedate?String.format('%td.%<tm.%<tY',record.closedate):'',
                     record.is_bkactproc==1?'активация':record.ibankstatus==1?'активен':record.ibankstatus==2?'просрочен':record.ibankstatus==-1?'заблокирован':'нет',
                     record.ibank_open?String.format('%td.%<tm.%<tY',record.ibank_open):'нет',
                     record.ibank_open?String.format('%td.%<tm.%<tY',record.ibank_open+record.ibankterm):'нет',
                     record.ibank_comment,
                     record.inn,
                     record.okvedmain,hsRes.compokveds[record.company_id]], rowCounter++, false)
        }
        save(response.outputStream)
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Bankdir <<<///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Useractivity >>>//////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def useractivityfilter = {
    checkAccess(12)
    if(!checkSectionAccess(9)) return
    if(!checkSubSectionAccess(23)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.departments = Department.list(sort:'name',order:'asc')
    hsRes.perslist = Pers.findAllByIdInList(User.findAllByModstatusAndUsergroup_idNotEqual(1,1).collect{it.pers_id},[sort:'shortname',order:'asc'])

    return hsRes
  }

  def perslist = {
    checkAccess(12)
    if(!checkSectionAccess(9)) return
    if(!checkSubSectionAccess(23)) return
    requestService.init(this)

    return [perslist:requestService.getIntDef('department_id',0)?Pers.findAllByIdInList(User.findAllByDepartment_idAndModstatusAndUsergroup_idNotEqual(requestService.getIntDef('department_id',0),1,1).collect{it.pers_id},[sort:'shortname',order:'asc']):Pers.findAllByIdInList(User.findAllByModstatusAndUsergroup_idNotEqual(1,1).collect{it.pers_id},[sort:'shortname',order:'asc'])]
  }

  def useractivity = {
    checkAccess(12)
    if(!checkSectionAccess(9)) return
    if(!checkSubSectionAccess(23)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['department_id','pers_id','is_active'])
    hsRes.inrequest.reportstart = requestService.getDate('reportstart')
    hsRes.inrequest.reportend = requestService.getDate('reportend')?:new Date()

    hsRes.searchresult = new UserActivitySearch().csiFindUserActivity(hsRes.inrequest,requestService.getStr('viewtype')!='table'?-1:20,requestService.getOffset())

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'useractivity', model: hsRes, filename: "useractivity.pdf")
      return
    }

    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Useractivity <<<//////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Cashsummary >>>///////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def cashsummaryfilter = {
    checkAccess(12)
    if(!checkSectionAccess(5)) return
    if(!checkSubSectionAccess(24)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def cashsummary = {
    checkAccess(12)
    if(!checkSectionAccess(5)) return
    if(!checkSubSectionAccess(24)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['department_id','reportdate_month','reportdate_year','is_needreports'])
    hsRes.reportdate = requestService.getRaw('reportdate')

    hsRes.report = new CashSearch().csiSelectCash(0,hsRes.inrequest.department_id?:0,-100,0,null,hsRes.reportdate,new Date(hsRes.inrequest.reportdate_year-1900,hsRes.inrequest.reportdate_month,0),0,0,0,'',0,0)
    if (hsRes.inrequest.is_needreports){
      hsRes.cashreports =  new CashreportSearch().csiSelectReportsSummary(hsRes.inrequest.department_id?:0,hsRes.reportdate,new Date(hsRes.inrequest.reportdate_year-1900,hsRes.inrequest.reportdate_month,0))
      hsRes.repexpensetypes = hsRes.cashreports.collect{it.expensetype_id}.unique().inject([:]){map, eId -> map[eId]=Expensetype.get(eId);map}
    }

    if (hsRes.report.records.size()==0&&(hsRes.cashreports?.size()?:0)==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(2, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      hsRes.cashclasses = Cashclass.list().inject([0:'']){map, cclass -> map[cclass.id]=cclass.name;map}
      hsRes.departments = Department.list().inject([0:'']){map, department -> map[department.id]=department.name;map}
      hsRes.projects = Project.list().inject([0:'']){map, project -> map[project.id]=project.name;map}
      hsRes.expensetypes = hsRes.report.records.collect{it.expensetype_id}.unique().inject([:]){map, eId -> map[eId]=Expensetype.get(eId);map}
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(2, 1, "Операции по кассе")
        fillRow(['Код','Дата операции','Комментарий','Тип','Сумма получения','Сумма выдачи','Сальдо','Статья расхода','Подраздел расхода','Раздел расхода','Класс','Отдел','Сотрудник','Проект'],3,false)
        hsRes.report.records.each{ record ->
          fillRow([record.id.toString(),
                   String.format('%td.%<tm.%<tY',record.operationdate),
                   record.comment,
                   record.type==1?'выдача':record.type==2?'получение':record.type==3?'возврат':record.type==4?'Финансирование':'Начисление',
                   record.type in [1,5]?0:record.summa,
                   record.type==1?record.summa:0,
                   record.saldo,
                   hsRes.expensetypes[record.expensetype_id].name,
                   hsRes.expensetypes[record.expensetype_id].podrazdel,
                   hsRes.expensetypes[record.expensetype_id].razdel,
                   hsRes.cashclasses[record.cashclass],
                   record.department_id?hsRes.departments[record.department_id]:'нет',
                   record.pers_fio?:record.pers_name?:'',
                   hsRes.projects[record.project_id]], rowCounter++, false)
        }
        if (hsRes.inrequest.is_needreports){
          rowCounter += 2
          putCellValue(rowCounter++, 1, "Отчеты")
          fillRow(['Код','Дата подтверждения','Комментарий','Тип','Сумма отчета','Сальдо','Статья отчета','Подраздел отчета','Раздел отчета','Класс','Отдел','Сотрудник','Проект'],rowCounter++,false)
          hsRes.cashreports.each{ record ->
            fillRow([record.id.toString(),
                     String.format('%td.%<tm.%<tY',record.repdate),
                     record.description,
                     'отчет',
                     record.summa,
                     0,
                     hsRes.repexpensetypes[record.expensetype_id].name,
                     hsRes.repexpensetypes[record.expensetype_id].podrazdel,
                     hsRes.repexpensetypes[record.expensetype_id].razdel,
                     'подотчет',
                     record.department_id?hsRes.departments[record.department_id]:'нет',
                     record.executor_name?:'',
                     hsRes.projects[record.project_id]], rowCounter++, false)
          }
        }
        save(response.outputStream)
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Cashsummary <<<///////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Change >>>////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def changefilter = {
    checkAccess(12)
    if(!checkSectionAccess(10)) return
    if(!checkSubSectionAccess(25)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def change = {
    checkAccess(12)
    if(!checkSectionAccess(10)) return
    if(!checkSubSectionAccess(25)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['reportdate_month','reportdate_year'])
    hsRes.reportdate = requestService.getRaw('reportdate')
    hsRes.reportend = new Date(hsRes.inrequest.reportdate_year-1900,hsRes.inrequest.reportdate_month,0)

    hsRes.report = Company.findAllByIs_holding(1)

    if (hsRes.report.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(2, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(2, 0, "Отчет по изменениям за ${String.format('%tB %<tY',hsRes.reportdate)}")
        fillRow(['Компания','Учетный номер','Регистрация/Перерегистрация','Смена названия','ИНН','Налоговая','КПП','ОКАТО','ОКТМО','Юр. адрес','Уставной капитал','Генеральный директор','Главный бухгалтер','Расчетные счета','Кредитные договора','Арендные договора','Лизинг','Займы'],3,false)
        hsRes.report.each{ record ->
          def gds = Compers.findByCompany_idAndPosition_idAndModstatusAndJobstartBetween(record.id,1,1,hsRes.reportdate,hsRes.reportend)
          def gbs = Compers.findByCompany_idAndPosition_idAndModstatusAndJobstartBetween(record.id,2,1,hsRes.reportdate,hsRes.reportend)
          def baccounts = (Bankaccount.findAllByCompany_idAndTypeaccount_idAndOpendateBetween(record.id,1,hsRes.reportdate,hsRes.reportend) +
                           Bankaccount.findAllByCompany_idAndTypeaccount_idAndClosedateBetween(record.id,1,hsRes.reportdate,hsRes.reportend) +
                           Bankaccount.findAllByCompany_idAndTypeaccount_idAndIbank_openBetween(record.id,1,hsRes.reportdate,hsRes.reportend)).unique()
          def kredits = Kredit.findAllByClientAndAdateBetween(record.id,hsRes.reportdate,hsRes.reportend)
          def spaces = Space.findAllByArendatorAndAdateBetween(record.id,hsRes.reportdate,hsRes.reportend)
          def lizings = Lizing.findAllByArendatorAndAdateBetween(record.id,hsRes.reportdate,hsRes.reportend)
          def loans = Loan.findAllByClientAndAdateBetween(record.id,hsRes.reportdate,hsRes.reportend)
          if (gds||gbs||baccounts||kredits||spaces||lizings||loans||(record.adrdate>=hsRes.reportdate&&record.adrdate<=hsRes.reportend)||(record.capitaldate>=hsRes.reportdate&&record.capitaldate<=hsRes.reportend)||(record.namedate>=hsRes.reportdate&&record.namedate<=hsRes.reportend))
            fillRow([record.name,
                     record.id.toString(),
                     record.reregdate?String.format('%td.%<tm.%<tY',record.reregdate):record.opendate?String.format('%td.%<tm.%<tY',record.opendate):'нет данных',
                     record.namedate>=hsRes.reportdate&&record.namedate<=hsRes.reportend?'Да':'Нет',
                     record.inn,
                     Taxinspection.get(record.taxinspection_id)?.toString()?:'',
                     record.adrdate>=hsRes.reportdate&&record.adrdate<=hsRes.reportend?record.kpp:'',
                     record.adrdate>=hsRes.reportdate&&record.adrdate<=hsRes.reportend?record.okato:'',
                     record.adrdate>=hsRes.reportdate&&record.adrdate<=hsRes.reportend?record.oktmo:'',
                     record.adrdate>=hsRes.reportdate&&record.adrdate<=hsRes.reportend?record.legaladr:'',
                     record.capitaldate>=hsRes.reportdate&&record.capitaldate<=hsRes.reportend?record.capital+' - '+String.format('%td.%<tm.%<tY',record.capitaldate):'',
                     gds?.toString()?:'',
                     gbs?.toString()?:'',
                     baccounts.collect{it.toString()}.join(' \n\r'),
                     kredits.collect{it.toFullString()}.join(' \n\r'),
                     spaces.collect{it.toFullString()}.join(' \n\r'),
                     lizings.collect{it.toFullString()}.join(' \n\r'),
                     loans.collect{it.toFullString()}.join(' \n\r')], rowCounter++, false)
        }
        save(response.outputStream)
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Change <<<////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Ibank >>>/////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def ibankfilter = {
    checkAccess(12)
    if(!checkSectionAccess(7)) return
    if(!checkSubSectionAccess(27)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def ibank = {
    checkAccess(12)
    if(!checkSectionAccess(7)) return
    if(!checkSubSectionAccess(27)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(null,null,['bankname'])

    hsRes.searchresult = new BankaccountSearch().csiFindExpiresAccounts(hsRes.inrequest.bankname?:'')

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'ibank', model: hsRes, filename: "ibank.pdf")
      return
    }

    return hsRes
  }

  def ibankXLS = {
    checkAccess(12)
    if(!checkSectionAccess(7)) return
    if(!checkSubSectionAccess(27)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(null,null,['bankname'])

    hsRes.report = new BankaccountSearch().csiFindExpiresAccounts(hsRes.inrequest.bankname?:'')

    if (hsRes.report.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(1, 4, "Окончание срока БК")
        putCellValue(2, 4, String.format('%td.%<tm.%<tY %<tH:%<tM',new Date()))
        fillRow(['Банк','Компания','Тип счета','Номер счета','Дата активации бк','Срок действия бк','Статус бк','Директор в компании','Директор по сведениям банка'],3,false)
        hsRes.report.each{ record ->
          fillRow([record.shortname,
                   record.cname,
                   record.typeaccount_id==1?'расчетный':record.typeaccount_id==2?'корпоративный':record.typeaccount_id==3?'текущий':record.typeaccount_id==4?'транзитный':'накопительный',
                   record.schet,
                   String.format('%td.%<tm.%<tY',record.ibank_open),
                   String.format('%td.%<tm.%<tY',record.ibank_close),
                   record.ibankstatus==1?'активен':record.ibankstatus==2?'просрочен':record.ibankstatus==-1?'заблокирован':'нет',
                   record.gd,
                   Pers.get(record.pers_id)?.shortname?:''], rowCounter++, false)
        }
        save(response.outputStream)
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Ibank <<</////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Kreditsummary >>>/////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def krsummaryfilter = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(28)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def krdebtcompute = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(28)) return

    Kredit.findAllByModstatus(1).each { kredit -> kredit.updateDebt(agentKreditService.computeKreditDebt(kredit)).save() }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def krsummary = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(28)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    def rates = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=new Valutarate().csiSearchCurrent(valuta.id)?.toBigDecimal()?:1.0g;map}
    hsRes.report = new KreditSummarySearch().csiSelectBankSummary().collect{
      def resultdebt = it.debtrub + it.debteur*rates[978] + it.debtusd*rates[840] + it.debtamd*rates[51]
      [bname:it.bank_name,debt:resultdebt]
    }
    hsRes.totaldebt = hsRes.report.sum{ it.debt }

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'krsummary', model: hsRes, filename: "krsummary.pdf")
      return
    }

    return hsRes
  }

  def krsummaryXLS = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(28)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    def rates = Valuta.list().inject([:]){map, valuta -> map[valuta.id]=new Valutarate().csiSearchCurrent(valuta.id)?.toBigDecimal()?:1.0g;map}
    hsRes.report = new KreditSummarySearch().csiSelectBankSummary().collect{
      def resultdebt = it.debtrub + it.debteur*rates[978] + it.debtusd*rates[840] + it.debtamd*rates[51]
      [bname:it.bank_name,debt:resultdebt]
    }
    hsRes.totaldebt = hsRes.report.sum{ it.debt }

    if (hsRes.report.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        setColumnWidth(0,4*256)
        setColumnWidth(1,60*256)
        setColumnWidth(2,25*256)
        setColumnWidth(3,20*256)
        putCellValue(1, 0, "Справка об общей кредитной задолженности по банкам")
        putCellValue(2, 0, "по состоянию на ${String.format('%td.%<tm.%<tY %<tH:%<tM',new Date())}")
        fillRow(['№','Наименование банка','Сумма задолженности по кредитам','Доля в общей задолженности, в %'],3,false,[[wrap:true],[wrap:true],[wrap:true],[wrap:true]])
        hsRes.report.eachWithIndex{ record, i ->
          fillRow([(i+1).toString(),
                   record.bname,
                   record.debt,
                   record.debt/hsRes.totaldebt*100], rowCounter++, false)
        }
        fillRow(['','Всего',hsRes.totaldebt,100],rowCounter++,false)
        save(response.outputStream)
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Kreditsummary <<</////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Dirsummary >>>////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def dirsummaryfilter = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(29)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def dirsummary = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(29)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(null,null,['cname'])
    hsRes.reportdate = requestService.getDate('reportdate')?:new Date()
    hsRes.company = Company.findByNameIlikeAndIs_holding('%'+(hsRes.inrequest.cname?:'')+'%',1)
    if (!hsRes.inrequest.cname)
      hsRes.allreport = new CompersSearch().csiFindGDsByDate(hsRes.reportdate)
    else hsRes.compreport = new CompersSearch().csiFindGDsByCompany(hsRes.company?.id?:0)

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'dirsummary', model: hsRes, filename: "dirsummary.pdf")
      return
    }

    return hsRes
  }

  def dirsummaryXLS = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(29)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(null,null,['cname'])
    hsRes.reportdate = requestService.getDate('reportdate')?:new Date()
    hsRes.company = hsRes.inrequest.cname?Company.findByNameIlikeAndIs_holding('%'+hsRes.inrequest.cname+'%',1):null
    if (!hsRes.inrequest.cname)
      hsRes.allreport = new CompersSearch().csiFindGDsByDate(hsRes.reportdate)
    else hsRes.compreport = new CompersSearch().csiFindGDsByCompany(hsRes.company?.id?:0)

    if (!hsRes.allreport?.size()&&!hsRes.compreport?.size()) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        if (hsRes.allreport?.size()>0){
          setColumnWidth(0,4*256)
          setColumnWidth(1,30*256)
          setColumnWidth(2,60*256)
          setColumnWidth(3,20*256)
          putCellValue(1, 0, "Справка по директорам на ${String.format('%td.%<tm.%<tY',hsRes.reportdate)}")
          fillRow(['№','Фио директора','Компания','Дата вступления в должность'],3,false,[[wrap:true],[wrap:true],[wrap:true],[wrap:true]])
        } else {
          setColumnWidth(0,25*256)
          setColumnWidth(1,50*256)
          setColumnWidth(2,30*256)
          setColumnWidth(3,20*256)
          setColumnWidth(4,20*256)
          putCellValue(1, 0, "Справка по директорам компании ${hsRes.company?.name}")
          fillRow(['Фио директора','Паспортные данные','Образование','Дата вступления в должность','Дата завершения деятельности'],3,false,[[wrap:true],[wrap:true],[wrap:true],[wrap:true],[wrap:true]])
        }
        hsRes.allreport.eachWithIndex{ record, i ->
          fillRow([(i+1).toString(),
                   record.shortname,
                   record.position_name,
                   String.format('%td.%<tm.%<tY',record.jobstart)], rowCounter++, false)
        }
        hsRes.compreport.each{ record ->
          fillRow([record.shortname,
                   record.collectPassData(),
                   record.education,
                   String.format('%td.%<tm.%<tY',record.jobstart),
                   record.jobend?String.format('%td.%<tm.%<tY',record.jobend):'нет'], rowCounter++, false)
        }
        save(response.outputStream)
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Dirsummary <<<////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Agrsummary >>>////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def agrsummaryfilter = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(30)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    return hsRes
  }

  def agrsummary = {
    checkAccess(12)
    if(!checkSectionAccess(6)) return
    if(!checkSubSectionAccess(30)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['reporttype'],null,['cname'])
    hsRes.company = hsRes.inrequest.cname?Company.findByNameIlikeAndIs_holding('%'+hsRes.inrequest.cname+'%',1):null
    hsRes.reportdate_start = requestService.getDate('reportdate_start')
    hsRes.reportdate_end = requestService.getDate('reportdate_end')?:new Date()

    hsRes.licenses = new LicenseSearch().csiSelectLicenseSummary(hsRes.reportdate_start,hsRes.reportdate_end,hsRes.inrequest.reporttype?0:hsRes.company?.id,hsRes.inrequest.reporttype?hsRes.company?.id:0)
    hsRes.industries = Industry.list().inject([:]){map, industry -> map[industry.id]=industry.name;map}
    hsRes.services = new ServiceSearch().csiSelectServiceSummary(hsRes.reportdate_start,hsRes.reportdate_end,hsRes.inrequest.reporttype?0:hsRes.company?.id,hsRes.inrequest.reporttype?hsRes.company?.id:0)
    hsRes.stypes = Servicetype.list().inject([:]){map, type -> map[type.id]=type.name;map}
    hsRes.smrs = new SmrSearch().csiSelectSmrSummary(hsRes.reportdate_start,hsRes.reportdate_end,hsRes.inrequest.reporttype?0:hsRes.company?.id,hsRes.inrequest.reporttype?hsRes.company?.id:0)
    hsRes.smrcats = Smrcat.list().inject([:]){map, cat -> map[cat.id]=cat.name;map}
    hsRes.trades = new TradeSearch().csiSelectTradeSummary(hsRes.reportdate_start,hsRes.reportdate_end,hsRes.inrequest.reporttype?0:hsRes.company?.id,hsRes.inrequest.reporttype?hsRes.company?.id:0)
    hsRes.tradecats = Tradecat.list().inject([:]){map, cat -> map[cat.id]=cat.name;map}
    hsRes.lizings = new LizingSearch().csiSelectLizingSummary(hsRes.reportdate_start,hsRes.reportdate_end,hsRes.inrequest.reporttype?0:hsRes.company?.id,hsRes.inrequest.reporttype?hsRes.company?.id:0)
    hsRes.cars = Car.list().inject([:]){map, car -> map[car.id]=car.name;map}
    hsRes.spaces = new SpaceSearch().csiSelectSpaceSummary(hsRes.reportdate_start,hsRes.reportdate_end,hsRes.inrequest.reporttype?0:hsRes.company?.id,hsRes.inrequest.reporttype?hsRes.company?.id:0)
    hsRes.spacetypes = Spacetype.list().inject([:]){map, spacetype -> map[spacetype.id]=spacetype.name;map}
    hsRes.kredits = new KreditSearch().csiSelectKreditSummary(hsRes.reportdate_start,hsRes.reportdate_end,hsRes.inrequest.reporttype?0:hsRes.company?.id,hsRes.inrequest.reporttype?hsRes.company?.id:0)

    if (hsRes.licenses.size()==0&&hsRes.services.size()==0&&hsRes.smrs.size()==0&&hsRes.trades.size()==0&&hsRes.lizings.size()==0&&hsRes.spaces.size()==0&&hsRes.kredits.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(2, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(2, 0, "Реестр договоров ${hsRes.inrequest.reporttype?'поставщика/исполнителя':'покупателя/заказчика'} ${hsRes.company?.name?:''} по состоянию на ${String.format('%td.%<tm.%<tY',hsRes.reportdate_end)}")
        fillRow([hsRes.inrequest.reporttype?'Наименование заказчика':'Наименование исполнителя','Дата договора','Номер договора','Дата окончания работ','Дата окончания договора','Сумма договора','Регулярный платеж','Предмет договора','Порядок расчетов по договору'],3,false)
        hsRes.licenses.each{ record ->
          fillRow([hsRes.inrequest.reporttype?record.company_name:record.sro_name,
                   String.format('%td.%<tm.%<tY',record.adate),
                   record.anumber,
                   'Нет',
                   String.format('%td.%<tm.%<tY',record.enddate),
                   'Нет',
                   record.regfee,
                   hsRes.industries[record.industry_id]?:'',
                   record.regfeeterm==1?'ежемесячно':'ежеквартально'], rowCounter++, false)
        }
        hsRes.services.each{ record ->
          fillRow([hsRes.inrequest.reporttype?record.zcompany_name:record.ecompany_name,
                   String.format('%td.%<tm.%<tY',record.adate),
                   record.anumber,
                   'Нет',
                   String.format('%td.%<tm.%<tY',record.enddate),
                   'Нет',
                   record.summa,
                   hsRes.stypes[record.atype]?:'',
                   record.paycondition==1?'ежемесячно':'ежеквартально'], rowCounter++, false)
        }
        hsRes.smrs.each{ record ->
          fillRow([hsRes.inrequest.reporttype?record.client_name:record.supplier_name,
                   String.format('%td.%<tm.%<tY',record.adate),
                   record.anumber,
                   String.format('%td.%<tm.%<tY',record.enddate),
                   String.format('%td.%<tm.%<tY',record.enddate),
                   record.summa,
                   'Нет',
                   hsRes.smrcats[record.smrcat_id]?:'',
                   record.paytype==1?'Единоверменно':'Авансовый платеж'], rowCounter++, false)
        }
        hsRes.trades.each{ record ->
          fillRow([hsRes.inrequest.reporttype?record.client_name:record.supplier_name,
                   String.format('%td.%<tm.%<tY',record.adate),
                   record.anumber,
                   'Нет',
                   record.enddate?String.format('%td.%<tm.%<tY',record.enddate):'нет',
                   'Нет',
                   record.summa,
                   hsRes.tradecats[record.tradecat_id]?:'',
                   record.paytype==1?'Единоверменно':'Регуляные платежи'], rowCounter++, false)
        }
        hsRes.lizings.each{ record ->
          fillRow([hsRes.inrequest.reporttype?record.arendator_name:record.arendodatel_name,
                   String.format('%td.%<tm.%<tY',record.adate),
                   record.anumber,
                   'Нет',
                   String.format('%td.%<tm.%<tY',record.enddate),
                   record.summa,
                   record.rate,
                   hsRes.cars[record.car_id]?:record.description,
                   'ежемесячно'], rowCounter++, false)
        }
        hsRes.spaces.each{ record ->
          fillRow([hsRes.inrequest.reporttype?record.arendator_name:record.arendodatel_name,
                   String.format('%td.%<tm.%<tY',record.adate),
                   record.anumber,
                   'Нет',
                   String.format('%td.%<tm.%<tY',record.enddate),
                   'Нет',
                   record.rate+record.ratedop,
                   hsRes.spacetypes[record.spacetype_id]?:'',
                   'ежемесячно'], rowCounter++, false)
        }
        hsRes.kredits.each{ record ->
          fillRow([hsRes.inrequest.reporttype?record.client_name:record.bank_name,
                   String.format('%td.%<tm.%<tY',record.adate),
                   record.anumber,
                   'Нет',
                   String.format('%td.%<tm.%<tY',record.enddate),
                   record.summa,
                   record.summa*record.rate/100/365*30,
                   record.kredtype==1?'Кредит':record.kredtype==2?'Кредитная линия':record.kredtype==3?'Овердрафт':'Линия с лимитом задолженности',
                   'ежемесячно'], rowCounter++, false)
        }
        save(response.outputStream)
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Agrsummary <<<////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Monthsalary >>>///////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def monthsalaryfilter = {
    checkAccess(12)
    if(!checkSectionAccess(1)) return
    if(!checkSubSectionAccess(31)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.repdates = Salaryreport.findAllBySalarytype_idInList([1,5],[order:'desc',sort:'repdate']).collect{[disvalue:String.format('%tY-%<tm',new Date(it.year-1900,it.month-1,1)),keyvalue:String.format('%td.%<tm.%<tY',new Date(it.year-1900,it.month-1,1))]}.unique()

    return hsRes
  }

  def monthsalary = {
    checkAccess(12)
    if(!checkSectionAccess(1)) return
    if(!checkSubSectionAccess(31)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['department_id'])
    hsRes.inrequest.repdate = requestService.getDate('repdate')

    hsRes.searchresult = new SalaryDepSearch().csiFindMonthSalary(hsRes.inrequest.repdate,hsRes.inrequest.department_id?:0,requestService.getStr('viewtype')!='table'?-1:20,requestService.getOffset())
    hsRes.departments = Department.list().inject([:]){map, department -> map[department.id]=department.name;map}

    if (requestService.getStr('viewtype')!='table') {
      renderPdf(template: 'monthsalary', model: hsRes, filename: "monthsalary.pdf")
      return
    }

    return hsRes
  }

  def monthsalaryXLS = {
    checkAccess(12)
    if(!checkSectionAccess(1)) return
    if(!checkSubSectionAccess(31)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['department_id'])
    hsRes.inrequest.repdate = requestService.getDate('repdate')

    hsRes.searchresult = new SalaryDepSearch().csiFindMonthSalary(hsRes.inrequest.repdate,hsRes.inrequest.department_id?:0,requestService.getStr('viewtype')!='table'?-1:20,requestService.getOffset())
    hsRes.departments = Department.list().inject([:]){map, department -> map[department.id]=department.name;map}

    if (hsRes.searchresult.records.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      hsRes.summas = [actsalary:0.0g,prepayment:0,offsalary:0.0g,prevfix:0.0g,bonus:0,shtraf:0,overloadsumma:0,holiday:0,reholiday:0,precashpayment:0,cash:0]
      hsRes.searchresult.records.each{ record ->
        hsRes.summas.actsalary += record.actsalary
        hsRes.summas.prepayment += record.prepayment
        hsRes.summas.offsalary += record.offsalary
        hsRes.summas.prevfix += record.prevfix
        hsRes.summas.bonus += record.bonus
        hsRes.summas.shtraf += record.shtraf
        hsRes.summas.overloadsumma += record.overloadsumma
        hsRes.summas.holiday += record.holiday
        hsRes.summas.precashpayment += record.precashpayment
        hsRes.summas.cash += record.cash
      }
      def rowCounter = 4
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(1, 2, "Отчет по зарплате за месяц.")
        putCellValue(2, 2, String.format('%tB %<tY',hsRes.inrequest.repdate))
        fillRow(['Департамент','Отдел','ФИО','Факт. оклад','Аванс','Официальный б/н','Б/н вне ведомости','Бонус','Штраф','Переработка','Отпускные','Отп. перерасчет','Нал до срока','Сумма к нал. выплате','Статусы оплаты аванса','Дата выплаты аванса','Статус официальной оплаты','Дата официальной оплаты','Статус наличной оплаты','Дата итоговой оплаты'],3,false,Tools.getXlsTableHeaderStyle(20))
        hsRes.searchresult.records.eachWithIndex{ record, index ->
          fillRow([record.parent?hsRes.departments[record.parent]:'',
                   record.d_name,
                   record.p_shortname,
                   record.actsalary,
                   record.prepayment,
                   record.offsalary,
                   record.prevfix,
                   record.bonus,
                   record.shtraf,
                   record.overloadsumma,
                   record.holiday,
                   record.reholiday,
                   record.precashpayment,
                   record.cash,
                   record.prepaystatus==2?'Да':record.prepaystatus==1?'Начислено':'Нет',
                   record.prepaydate?String.format('%td.%<tm.%<tY',record.prepaydate):'',
                   record.offstatus==2?'Да':record.offstatus==1?'Начислено':'Нет',
                   record.prepaydate?String.format('%td.%<tm.%<tY',record.prepaydate):'',
                   record.cashstatus==2?'Да':record.cashstatus==1?'Начислено':'Нет',
                   record.prepaydate?String.format('%td.%<tm.%<tY',record.prepaydate):''], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(20) : Tools.getXlsTableLineStyle(20))
        }
          fillRow(['','','',
                   hsRes.summas.actsalary,
                   hsRes.summas.prepayment,
                   hsRes.summas.offsalary,
                   hsRes.summas.prevfix,
                   hsRes.summas.bonus,
                   hsRes.summas.shtraf,
                   hsRes.summas.overloadsumma,
                   hsRes.summas.holiday,
                   hsRes.summas.reholiday,
                   hsRes.summas.precashpayment,
                   hsRes.summas.cash,'','','','','',''], rowCounter++, false, Tools.getXlsTableLastLineStyle(20))
        save(response.outputStream)
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Monthsalary >>>///////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Paytaskincomplete >>>/////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def paytaskincompletefilter = {
    checkAccess(12)
    if(!checkSectionAccess(9)) return
    if(!checkSubSectionAccess(32)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.companies = Company.findAllByIs_holdingAndModstatus(1,1,[sort:'name',order:'asc'])
    hsRes.banks = new BankaccountSearch().csiFindBanknames()

    return hsRes
  }

  def paytaskincomplete = {
    checkAccess(12)
    if(!checkSectionAccess(9)) return
    if(!checkSubSectionAccess(32)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['company_id'],null,['bank_id'])
    hsRes.inrequest.reportstart = requestService.getDate('reportstart')
    hsRes.inrequest.reportend = requestService.getDate('reportend')

    hsRes.searchresult = new PayrequestTaskSearch().csiSelectTaskpay(hsRes.inrequest,requestService.getStr('viewtype')!='table'?-1:20,requestService.getOffset())
    hsRes.saldos = hsRes.searchresult.records.groupBy{it.bankaccount_id}.inject([:]){map, node -> map[node.key]=node.value[0].computeCurSaldo();map}

    if (requestService.getStr('viewtype')!='table') {
      hsRes.exptypes = hsRes.searchresult.records.inject([:]){map, prequest -> map[prequest.id]=Expensetype.get(prequest.expensetype_id);map}
      hsRes.clients = Client.list().inject([0:'']){map, client -> map[client.id]=client.name;map}
      hsRes.projects = Project.list().inject([0:'']){map, project -> map[project.id]=project.name;map}
      renderPdf(template: 'paytaskincomplete', model: hsRes, filename: "paytaskincomplete.pdf")
      return
    }

    return hsRes
  }

  def paytaskincompleteXLS = {
    checkAccess(12)
    if(!checkSectionAccess(9)) return
    if(!checkSubSectionAccess(32)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['company_id'],null,['bank_id'])
    hsRes.inrequest.reportstart = requestService.getDate('reportstart')
    hsRes.inrequest.reportend = requestService.getDate('reportend')

    hsRes.searchresult = new PayrequestTaskSearch().csiSelectTaskpay(hsRes.inrequest,-1,requestService.getOffset())

    if (hsRes.searchresult.records.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      hsRes.saldos = hsRes.searchresult.records.groupBy{it.bankaccount_id}.inject([:]){map, node -> map[node.key]=node.value[0].computeCurSaldo();map}
      hsRes.exptypes = hsRes.searchresult.records.inject([:]){map, prequest -> map[prequest.id]=Expensetype.get(prequest.expensetype_id);map}
      hsRes.clients = Client.list().inject([0:'']){map, client -> map[client.id]=client.name;map}
      hsRes.projects = Project.list().inject([0:'']){map, project -> map[project.id]=project.name;map}
      def rowCounter = 4
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(1, 2, "Отчет по неисполненным заявкам на платежи.")
        putCellValue(2, 2, String.format('%td.%<tm.%<tY %<tH:%<tM',new Date()))
        fillRow(['Срок платежа','Дата акцепта','Компания','Банк','Тип платежа','Контрагент','Назначение','Сумма','Клиент','Проект','Раздел расхода','Подраздел расхода','Статья расхода','Текущий остаток на счете'],3,false,Tools.getXlsTableHeaderStyle(14))
        hsRes.searchresult.records.eachWithIndex{ record, index ->
          fillRow([String.format('%td.%<tm.%<tY',record.paydate),
                   record.acceptdate?String.format('%td.%<tm.%<tY',record.acceptdate):'нет',
                   record.fromcompany,
                   record.bank_name,
                   record.paytype==1?'исходящий':'внутренний',
                   record.tocompany,
                   record.destination.size()>50?record.destination[0..50]:record.destination,
                   record.summa,
                   hsRes.clients[record.client_id],
                   hsRes.projects[record.project_id],
                   hsRes.exptypes[record.id]?.razdel?:'',
                   hsRes.exptypes[record.id]?.podrazdel?:'',
                   hsRes.exptypes[record.id]?.name?:'',
                   hsRes.saldos[record.bankaccount_id]], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(14) : index == hsRes.searchresult.records.size()-1 ? Tools.getXlsTableLastLineStyle(14) : Tools.getXlsTableLineStyle(14))
        }
        save(response.outputStream)
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Paytaskincomplete <<</////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Paytaskhand >>>///////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def paytaskhandfilter = {
    checkAccess(12)
    if(!checkSectionAccess(9)) return
    if(!checkSubSectionAccess(33)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes.companies = Company.findAllByIs_holdingAndModstatus(1,1,[sort:'name',order:'asc'])
    hsRes.banks = new BankaccountSearch().csiFindBanknames()

    return hsRes
  }

  def paytaskhand = {
    checkAccess(12)
    if(!checkSectionAccess(9)) return
    if(!checkSubSectionAccess(33)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['company_id','modstatus'],null,['bank_id'])
    hsRes.inrequest.reportstart = requestService.getDate('reportstart')
    hsRes.inrequest.reportend = requestService.getDate('reportend')
    hsRes.inrequest.execdatestart = requestService.getDate('execdatestart')
    hsRes.inrequest.execdateend = requestService.getDate('execdateend')

    hsRes.searchresult = new PayrequestTaskSearch().csiSelectHandTaskpay(hsRes.inrequest,requestService.getStr('viewtype')!='table'?-1:20,requestService.getOffset())

    if (requestService.getStr('viewtype')!='table') {
      hsRes.exptypes = hsRes.searchresult.records.inject([:]){map, prequest -> map[prequest.id]=Expensetype.get(prequest.expensetype_id);map}
      hsRes.clients = Client.list().inject([0:'']){map, client -> map[client.id]=client.name;map}
      hsRes.projects = Project.list().inject([0:'']){map, project -> map[project.id]=project.name;map}
      renderPdf(template: 'paytaskhand', model: hsRes, filename: "paytaskhand.pdf")
      return
    }

    return hsRes
  }

  def paytaskhandXLS = {
    checkAccess(12)
    if(!checkSectionAccess(9)) return
    if(!checkSubSectionAccess(33)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 12

    hsRes+=requestService.getParams(['company_id','modstatus'],null,['bank_id'])
    hsRes.inrequest.reportstart = requestService.getDate('reportstart')
    hsRes.inrequest.reportend = requestService.getDate('reportend')
    hsRes.inrequest.execdatestart = requestService.getDate('execdatestart')
    hsRes.inrequest.execdateend = requestService.getDate('execdateend')

    hsRes.searchresult = new PayrequestTaskSearch().csiSelectHandTaskpay(hsRes.inrequest,-1,requestService.getOffset())

    if (hsRes.searchresult.records.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      hsRes.exptypes = hsRes.searchresult.records.inject([:]){map, prequest -> map[prequest.id]=Expensetype.get(prequest.expensetype_id);map}
      hsRes.clients = Client.list().inject([0:'']){map, client -> map[client.id]=client.name;map}
      hsRes.projects = Project.list().inject([0:'']){map, project -> map[project.id]=project.name;map}
      def rowCounter = 4
      new WebXlsxExporter().with {
        setResponseHeaders(response,'paytaskhand_'+String.format('%td-%<tm-%<tY_%<tH-%<tM',new Date()))
        putCellValue(1, 2, "Отчет по неисполненным заявкам на платежи.")
        putCellValue(2, 2, String.format('%td.%<tm.%<tY %<tH:%<tM',new Date()))
        fillRow(['Срок платежа','Дата акцепта','Дата исполнения','Компания','Банк','Тип платежа','Контрагент','Назначение','Сумма','Статус срочности','Статус исполнения','Клиент','Проект','Раздел расхода','Подраздел расхода','Статья расхода'],3,false,Tools.getXlsTableHeaderStyle(16))
        hsRes.searchresult.records.eachWithIndex{ record, index ->
          fillRow([String.format('%td.%<tm.%<tY',record.paydate),
                   String.format('%td.%<tm.%<tY',record.acceptdate),
                   record.execdate?String.format('%td.%<tm.%<tY',record.execdate):'нет',
                   record.fromcompany,
                   record.bank_name,
                   record.paytype==1?'исходящий':'внутренний',
                   record.tocompany,
                   record.destination.size()>50?record.destination[0..50]:record.destination,
                   record.summa,
                   record.is_urgent==1?'срочный':'обычный',
                   record.modstatus>=2?'исполнено':'не исполнено',
                   hsRes.clients[record.client_id],
                   hsRes.projects[record.project_id],
                   hsRes.exptypes[record.id]?.razdel?:'',
                   hsRes.exptypes[record.id]?.podrazdel?:'',
                   hsRes.exptypes[record.id]?.name?:''], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(16) : index == hsRes.searchresult.records.size()-1 ? Tools.getXlsTableLastLineStyle(16) : Tools.getXlsTableLineStyle(16))
        }
        save(response.outputStream)
      }
    }
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Paytaskhand <<<///////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
}