import grails.converters.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import pl.touk.excel.export.WebXlsxExporter

class CashController {
  def requestService
  def imageService

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

  private def checkAccess(iActionId){
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

  private Boolean checkAccessLevel(Integer iLevel) {
    checkAccessLevel([iLevel])
  }
  private Boolean checkAccessLevel(lsLevel) {
    if(!(session.user.cashaccess in lsLevel)){
      response.sendError(403)
      return false;
    }
    return true
  }

  private Boolean checkZakazAccess(_zakaz,_dep_id) {
    if (_zakaz&&((session.user.cashaccess in [1,6,7]&&_zakaz.initiator!=session.user.id)||(session.user.cashaccess==2&&_zakaz.department_id!=_dep_id&&_zakaz.initiator!=session.user.id))) {
      response.sendError(403)
      return false
    }
    return true
  }

  private Boolean checkReportAccess(_report,_dep_id) {
    if ((session.user.cashaccess in [0,1,4,5]&&_report.executor!=session.user.id)||(session.user.cashaccess==2&&_report.department_id!=_dep_id&&_report.executor!=session.user.id)) {
      response.sendError(403)
      return false
    }
    return true
  }

  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Main >>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def index = {
    checkAccess(8)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails&&session.cashlastRequest){
      session.cashlastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.cashlastRequest
    } else {
      hsRes+=requestService.getParams(['department_id','pers_id'])
      hsRes.inrequest.cashsection = requestService.getIntDef('cashsection',0)
    }

    return hsRes
  }

  def zakazfilter = {
    checkAccess(8)
    if(!checkAccessLevel([1,2,3,6,7])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    if (session.cashlastRequest?.fromDetails){
      hsRes.inrequest = session.cashlastRequest
    }

    hsRes.status = Cashstatus.list()

    return hsRes
  }

  def requestfilter = {
    checkAccess(8)
    if(!checkAccessLevel(3..5)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    if (session.cashlastRequest?.fromDetails){
      hsRes.inrequest = session.cashlastRequest
    }
    hsRes.status = Cashreqstatus.list()

    return hsRes
  }

  def reportfilter = {
    checkAccess(8)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    if (session.cashlastRequest?.fromDetails){
      hsRes.inrequest = session.cashlastRequest
    }
    hsRes.reportstatus = Cashrepstatus.list()
    hsRes.exprazdel = Expensetype1.list()
    hsRes.exppodrazdel = hsRes.inrequest?.exprazdel_id?Expensetype2.findAllByExpensetype1_id(hsRes.inrequest.exprazdel_id):Expensetype2.list()
    hsRes.expensetypes = !hsRes.inrequest?.exprazdel_id?Expensetype.list():!hsRes.inrequest?.exppodrazdel_id?Expensetype.findAllByExpensetype1_id(hsRes.inrequest.exprazdel_id):Expensetype.findAllByExpensetype1_idAndExpensetype2_id(hsRes.inrequest.exprazdel_id,hsRes.inrequest.exppodrazdel_id)

    return hsRes
  }

  def maincashfilter = {
    checkAccess(8)
    if(!checkAccessLevel([3,5,6,7])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    if (session.cashlastRequest?.fromDetails){
      hsRes.inrequest = session.cashlastRequest
    }
    hsRes.requests = new CashzakazSearch().csiFindApprovedNonCopletedZakaz(-1,0)
    hsRes.departments = Department.list().inject([0:'']){map, department -> map[department.id]=department.name;map}
    hsRes.cashclasses = hsRes.inrequest?.maincashtype>0?Cashclass."findAllByIs_type${hsRes.inrequest.maincashtype}"(1,[sort:'name',order:'asc']):Cashclass.list(sort:'name',order:'asc')
    hsRes.exprazdel = Expensetype1.list()
    hsRes.exppodrazdel = hsRes.inrequest?.exprazdel_id?Expensetype2.findAllByExpensetype1_id(hsRes.inrequest.exprazdel_id):Expensetype2.list()
    hsRes.expensetypes = !hsRes.inrequest?.exprazdel_id?Expensetype.list():!hsRes.inrequest?.exppodrazdel_id?Expensetype.findAllByExpensetype1_id(hsRes.inrequest.exprazdel_id):Expensetype.findAllByExpensetype1_idAndExpensetype2_id(hsRes.inrequest.exprazdel_id,hsRes.inrequest.exppodrazdel_id)

    return hsRes
  }

  def depcashfilter = {
    checkAccess(8)
    if(!checkAccessLevel([2,3,5,6])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    if (session.cashlastRequest?.fromDetails){
      hsRes.inrequest = session.cashlastRequest
    } else {
      hsRes+=requestService.getParams(['department_id','pers_id'])
    }

    hsRes.departments = session.user.cashaccess==2?Department.findAllByIdOrParent(hsRes.user.department_id,hsRes.user.department_id,[sort:'name',order:'asc'])+(hsRes.user.is_tehdirleader?[Department.findByIs_tehdir(1)]:[]):Department.list(sort:'name',order:'asc')
    hsRes.perslist = Pers.findAllByIdInList(User.findAllByCashaccessGreaterThanAndDepartment_idAndModstatus(session.user.cashaccess==2?-1:0,session.user.cashaccess==2?hsRes.user.department_id:hsRes.inrequest?.department_id?:0,1).collect{it.pers_id}.unique())

    return hsRes
  }

  def myoperationfilter = {
    checkAccess(8)
    if(!checkAccessLevel([0,1,4,6])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    if (session.cashlastRequest?.fromDetails){
      hsRes.inrequest = session.cashlastRequest
    }

    return hsRes
  }

  def myoperations = {
    checkAccess(8)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    hsRes.opdatestart = requestService.getDate('opdatestart')
    hsRes.opdateend = requestService.getDate('opdateend')
    hsRes.mycashrecords = new CashdepartmentSearch().csiSelectCash(0,hsRes.user.department_id,session.user.id,-100,-100,null,hsRes.opdatestart,hsRes.opdateend,20,requestService.getOffset())
    hsRes.department = Department.get(hsRes.user.department_id)

    return hsRes
  }

  def perslist = {
    checkAccess(8)
    if(!checkAccessLevel([2,3,5,6])) return
    requestService.init(this)

    return [perslist:Department.get(requestService.getIntDef('department_id',0))?.is_tehdir?Pers.findAllByIdInListAndPerstype(User.findAllByDepartment_idAndModstatus(0,1).collect{it.pers_id}.unique(),2):Pers.findAllByIdInList(User.findAllByCashaccessGreaterThanAndDepartment_idAndModstatus(requestService.getIntDef('department_id',0)?-1:0,requestService.getIntDef('department_id',0),1).collect{it.pers_id}.unique())]
  }

  def list = {
    checkAccess(8)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    if (session.cashlastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.cashlastRequest
      session.cashlastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['zakaz_id','department_id','modstatus','archivestatus','is_request','is_reports',
                                       'is_maincash','maincashtype','maincashclass','depcashtype','depcashclass',
                                       'is_depcash','reqstatus','repstatus','expensetype_id','project_id','exprazdel_id',
                                       'exppodrazdel_id','mcid'],['pers_id'],['username','expensetype_name','executor_name','comment'])
      hsRes.inrequest.todate = requestService.getDate('todate')
      hsRes.inrequest.opdatestart = requestService.getDate('opdatestart')
      hsRes.inrequest.opdateend = requestService.getDate('opdateend')
      hsRes.inrequest.reqdate = requestService.getDate('reqdate')
      hsRes.inrequest.repdate = requestService.getDate('repdate')
      hsRes.inrequest.offset = requestService.getOffset()
      session.cashlastRequest = hsRes.inrequest
    }
    if (hsRes.inrequest.is_request) {
      if(!checkAccessLevel(3..5)) return
      session.cashlastRequest.cashsection = 2
      hsRes.requests = new CashrequestSearch().csiSelectRequest(0,hsRes.inrequest.reqstatus?:0,hsRes.inrequest.reqdate,20,hsRes.inrequest.offset)
      hsRes.cashstatus = Cashreqstatus.list().inject([:]){map, status -> map[status.id]=status.name;map}

      render(view: "requests", model: hsRes)
      return
    } else if(hsRes.inrequest.is_reports){
      session.cashlastRequest.cashsection = 3
      hsRes.reports = new CashreportSearch().csiSelectReports(0,session.user.cashaccess in [0,1,4]?session.user.id:0l,hsRes.inrequest.executor_name?:'',session.user.cashaccess==2?hsRes.user.department_id:0,hsRes.inrequest.repstatus?:0,hsRes.inrequest.repdate,hsRes.inrequest.exprazdel_id?:0,hsRes.inrequest.exppodrazdel_id?:0,hsRes.inrequest.expensetype_id?:0,hsRes.inrequest.project_id?:0,20,hsRes.inrequest.offset)
      hsRes.expensetypes = hsRes.reports.records.collect{it.expensetype_id}.unique().inject([:]){map, eId -> map[eId]=Expensetype.get(eId);map}
      hsRes.cashstatus = Cashrepstatus.list().inject([:]){map, status -> map[status.id]=status.name;map}
      hsRes.departments = Department.list().inject([0:'']){map, department -> map[department.id]=department.name;map}

      render(view: "reports", model: hsRes)
      return
    } else if(hsRes.inrequest.is_maincash){
      session.cashlastRequest.cashsection = 4
      hsRes.maincashrecords = new CashSearch().csiSelectCash(hsRes.inrequest.mcid?:0,hsRes.inrequest.department_id?:0,hsRes.inrequest.maincashtype,hsRes.inrequest.maincashclass?:0,hsRes.inrequest.todate,hsRes.inrequest.opdatestart,hsRes.inrequest.opdateend,hsRes.inrequest.exprazdel_id?:0,hsRes.inrequest.exppodrazdel_id?:0,hsRes.inrequest.expensetype_id?:0,hsRes.inrequest.comment?:'',10,hsRes.inrequest.offset)
      hsRes.cashclasses = Cashclass.list().inject([0:'']){map, cclass -> map[cclass.id]=cclass.name;map}
      hsRes.departments = Department.list().inject([0:'']){map, department -> map[department.id]=department.name;map}
      hsRes.agents = Agent.list().inject([0:'']){map, agent -> map[agent.id]=agent.name;map}
      hsRes.expensetypes = hsRes.maincashrecords.records.collect{it.expensetype_id}.unique().inject([:]){map, eId -> map[eId]=Expensetype.get(eId);map}

      render(view: "maincash", model: hsRes)
      return
    } else if(hsRes.inrequest.is_depcash){
      session.cashlastRequest.cashsection = 5
      hsRes.depcashrecords = new CashdepartmentSearch().csiSelectCash(0,session.user.cashaccess==2&&!hsRes.user.is_tehdirleader?hsRes.user.department_id:hsRes.inrequest.department_id?:0,User.findByPers_id(hsRes.inrequest.pers_id?:-1)?.id?:0,hsRes.inrequest.depcashtype,hsRes.inrequest.depcashclass?:0,hsRes.inrequest.todate,hsRes.inrequest.opdatestart,hsRes.inrequest.opdateend,20,hsRes.inrequest.offset)
      hsRes.departments = Department.list().inject([0:'']){map, department -> map[department.id]=department.name;map}
      hsRes.expensetypes = hsRes.depcashrecords.records.collect{it.expensetype_id}.unique().inject([:]){map, eId -> map[eId]=Expensetype.get(eId);map}

      render(view: "depcash", model: hsRes)
      return
    } else session.cashlastRequest.cashsection = 1

    hsRes.requests = new CashzakazSearch().csiSelectZakaz(0,session.user.cashaccess in [1,6,7]?session.user.id:0l,
                                                          session.user.cashaccess in [1,6,7]?0:hsRes.inrequest.department_id?:0,
                                                          hsRes.inrequest.username?:'',hsRes.inrequest.modstatus?:0,
                                                          hsRes.inrequest.archivestatus?:0,0,hsRes.inrequest.todate,20,hsRes.inrequest.offset)
    hsRes.cashstatus = Cashstatus.list().inject([:]){map, status -> map[status.id]=status.name;map}
    hsRes.departments = Department.list().inject([0:'']){map, department -> map[department.id]=department.name;map}

    return hsRes
  }

  def depcashXLS = {
    checkAccess(8)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    hsRes+=requestService.getParams(['department_id','depcashtype','depcashclass'],['pers_id'])
    hsRes.inrequest.todate = requestService.getDate('todate')
    hsRes.inrequest.opdatestart = requestService.getDate('opdatestart')
    hsRes.inrequest.opdateend = requestService.getDate('opdateend')

    hsRes.depcashrecords = new CashdepartmentSearch().csiSelectCash(0,session.user.cashaccess==2&&!hsRes.user.is_tehdirleader?hsRes.user.department_id:hsRes.inrequest.department_id?:0,User.findByPers_id(hsRes.inrequest.pers_id?:-1)?.id?:0,hsRes.inrequest.depcashtype,hsRes.inrequest.depcashclass?:0,hsRes.inrequest.todate,hsRes.inrequest.opdatestart,hsRes.inrequest.opdateend,0,0)
    hsRes.departments = Department.list().inject([0:'']){map, department -> map[department.id]=department.name;map}
    hsRes.expensetypes = hsRes.depcashrecords.records.collect{it.expensetype_id}.unique().inject([:]){map, eId -> map[eId]=Expensetype.get(eId);map}

    if (hsRes.depcashrecords.records.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(1, 4, "Операции по кассе")
        putCellValue(2, 4, String.format('%td.%<tm.%<tY %<tH:%<tM',new Date()))
        fillRow(['Дата операции','Сумма операции','Тип операции','Класс операции','Отдел','Сотрудник','Комментарий','Статья расходов','Остаток средств'],3,false,Tools.getXlsTableHeaderStyle(9))
        hsRes.depcashrecords.records.eachWithIndex{ record, index ->
          fillRow([String.format('%td.%<tm.%<tY',record.operationdate),
                   record.summa,
                   record.type==1?'выдача':record.type==2?'получение':record.type==3?'возврат':record.type==4?'возврат в гл. кассу':record.type==5?'начисление':'отчет',
                   record.cashclass==1?'зарплата':record.cashclass==2?'подотчет':record.cashclass==3?'заем':record.cashclass==4?'расход':record.cashclass==5?'штраф':'прочее',
                   hsRes.departments[record.department_id],
                   record.pers_fio?:record.pers_name,
                   record.comment,
                   hsRes.expensetypes[record.expensetype_id]?.toString()?:0,
                   record.saldo], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(9) : index == hsRes.depcashrecords.records.size()-1 ? Tools.getXlsTableLastLineStyle(9) : Tools.getXlsTableLineStyle(9))
        }
        save(response.outputStream)
      }
    }
    return
  }

  def myoperationsXLS = {
    checkAccess(8)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    hsRes.opdatestart = requestService.getDate('opdatestart')
    hsRes.opdateend = requestService.getDate('opdateend')
    hsRes.mycashrecords = new CashdepartmentSearch().csiSelectCash(0,hsRes.user.department_id,session.user.id,-100,-100,null,hsRes.opdatestart,hsRes.opdateend,0,0)
    hsRes.department = Department.get(hsRes.user.department_id)
    hsRes.expensetypes = hsRes.mycashrecords.records.collect{it.expensetype_id}.unique().inject([:]){map, eId -> map[eId]=Expensetype.get(eId);map}

    if (hsRes.mycashrecords.records.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(1, 4, "Операции по кассе")
        putCellValue(2, 4, String.format('%td.%<tm.%<tY %<tH:%<tM',new Date()))
        fillRow(['Дата операции','Сумма операции','Тип операции','Класс операции','Отдел','Сотрудник','Комментарий','Статья расходов','Остаток средств'],3,false,Tools.getXlsTableHeaderStyle(9))
        hsRes.mycashrecords.records.eachWithIndex{ record, index ->
          fillRow([String.format('%td.%<tm.%<tY',record.operationdate),
                   record.summa,
                   record.type==1?'выдача':record.type==2?'получение':record.type==3?'возврат':record.type==4?'возврат в гл. кассу':record.type==5?'начисление':'отчет',
                   record.cashclass==1?'зарплата':record.cashclass==2?'подотчет':record.cashclass==3?'заем':record.cashclass==4?'расход':record.cashclass==5?'штраф':'прочее',
                   hsRes.department?.name?:'',
                   record.pers_fio?:record.pers_name,
                   record.comment,
                   hsRes.expensetypes[record.expensetype_id]?.toString()?:'',
                   record.saldo], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(9) : index == hsRes.mycashrecords.records.size()-1 ? Tools.getXlsTableLastLineStyle(9) : Tools.getXlsTableLineStyle(9))
        }
        save(response.outputStream)
      }
    }
    return
  }

  def detail = {
    checkAccess(8)
    if(!checkAccessLevel([1,2,3,6,7])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    def lId=requestService.getIntDef('id',0)
    hsRes.zakaz = Cashzakaz.get(lId)
    if (!hsRes.zakaz&&lId) {
      response.sendError(404)
      return
    }
    if(!checkZakazAccess(hsRes.zakaz,hsRes.user.department_id)) return

    hsRes.cashstatus = Cashstatus.list().inject([:]){map, status -> map[status.id]=status.name;map}
    hsRes.initiator = User.get(hsRes.zakaz?.initiator)?.name
    hsRes.departments = Department.list()
    hsRes.department = Department.get(hsRes.zakaz?.department_id)?.name
    hsRes.executors = User.findAllByCashaccessInListAndModstatusAndUsergroup_idGreaterThan([1,3,5,6],1,1)

    return hsRes
  }

  def update = {
    checkAccess(8)
    if(!checkAccessLevel([1,2,3,6,7])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getLongDef('id',0)
    hsRes+=requestService.getParams(['modstatus','valuta_id','basetype','department_id'],['executor','summa'],['purpose','comment'])
    hsRes.inrequest.todate = requestService.getDate('todate')

    hsRes.zakaz = Cashzakaz.get(lId)
    if (!hsRes.zakaz&&lId) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    if(!checkZakazAccess(hsRes.zakaz,hsRes.user.department_id)) return

    if((hsRes.zakaz?.modstatus<3||hsRes.zakaz?.modstatus==5)&&!hsRes.inrequest.summa)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.purpose)
      hsRes.result.errorcode<<2
    if(hsRes.inrequest.basetype==1&&!hsRes.inrequest.department_id)
      hsRes.result.errorcode<<3
    if(hsRes.inrequest.basetype==2&&!hsRes.inrequest.executor)
      hsRes.result.errorcode<<4
    if(hsRes.inrequest.modstatus==5&&!hsRes.inrequest.comment)
      hsRes.result.errorcode<<5
    if(!hsRes.inrequest.todate)
      hsRes.result.errorcode<<6

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.zakaz = new Cashzakaz(department_id:(hsRes.inrequest.basetype==1&&session.user.cashaccess==3?hsRes.inrequest.department_id:hsRes.inrequest.basetype==2&&session.user.cashaccess==3?0:session.user.cashaccess in [1,3,5,6,7]?0:hsRes.user.department_id),initiator:(hsRes.inrequest.basetype==2&&session.user.cashaccess==3?hsRes.inrequest.executor:session.user.id))
        hsRes.result.zakaz = hsRes.zakaz.setData(hsRes.inrequest).csiSetAdmin(session.user.id).csiSetModstatus(hsRes.inrequest.modstatus,session.user.cashaccess).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Cash/update\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def events = {
    checkAccess(8)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    def lId=requestService.getIntDef('id',0)
    hsRes.zakaz = Cashzakaz.get(lId)
    if (!hsRes.zakaz) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.events = Cashevent.findAllByCashzakaz_id(hsRes.zakaz.id,[sort:'inputdate',order:'desc'])
    hsRes.eventtypes = Casheventtype.list().inject([:]){map, eventtype -> map[eventtype.id]=eventtype.name;map}
    hsRes.admins = User.findAllByIdInList(hsRes.events.collect{it.admin_id}?:[0]).inject([:]){map, user -> map[user.id]=user.name;map}

    return hsRes
  }

  def deletecashrequest = {
    checkAccess(8)
    if(!checkAccessLevel(3..5)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    hsRes.cashrequest = Cashrequest.get(requestService.getIntDef('id',0))
    if (!(hsRes.cashrequest?.modstatus in [1,2,6])) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.cashrequest.csiSetModstatus(7,session.user.cashaccess).delete(failOnError:true,flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Cash/deletecashrequest\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def completezakaz = {
    checkAccess(8)
    if(!checkAccessLevel(3)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8
    hsRes.result=[errorcode:[]]

    hsRes.zakaz = Cashzakaz.get(requestService.getIntDef('id',0))
    if (!(hsRes.zakaz?.modstatus in [1,3])) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.zakaz.csiSetAdmin(session.user.id).csiSetModstatus(4,session.user.cashaccess).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Cash/completezakaz\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def allocatezakaz = {
    checkAccess(8)
    if(!checkAccessLevel(3)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8
    hsRes.result=[errorcode:[]]
 
    def lId=requestService.getIntDef('id',0)

    hsRes.zakaz = Cashzakaz.get(lId)
    if (!hsRes.zakaz?.is_managerapprove||hsRes.zakaz?.modstatus!=2) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.zakaz.csiSetAdmin(session.user.id).csiSetModstatus(3,session.user.cashaccess).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Cash/allocatezakaz\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def repeatzakaz = {
    checkAccess(8)
    if(!checkAccessLevel(3)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8
    hsRes.result=[errorcode:[]]
 
    def lId=requestService.getIntDef('id',0)

    hsRes.zakaz = Cashzakaz.get(lId)
    if (!hsRes.zakaz?.is_managerapprove||hsRes.zakaz?.modstatus==4) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.zakaz.csiSetAdmin(session.user.id).csiSetModstatus(1,session.user.cashaccess).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Cash/repeatzakaz\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def addcashrequest = {
    checkAccess(8)
    if(!checkAccessLevel(3)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    return hsRes
  }

  def incertcashrequest = {
    checkAccess(8)
    if(!checkAccessLevel(3)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8
    hsRes.result=[errorcode:[]]
 
    hsRes.reqdate = requestService.getDate('reqdate')
    hsRes.cashzakazlist = Cashzakaz.findAllByTodateAndModstatusAndCashrequest_id(hsRes.reqdate,1,0)

    if(!hsRes.reqdate)
      hsRes.result.errorcode<<1
    else if(!hsRes.cashzakazlist)
      hsRes.result.errorcode<<2

    if(!hsRes.result.errorcode){
      try {
        hsRes.result.cashrequest = new Cashrequest(reqdate:hsRes.reqdate,initiator:session.user.id).csiSetSumma(hsRes.cashzakazlist.sum{it.getSummaRub()}).save(failOnError:true)?.id?:0
        hsRes.cashzakazlist.each{ it.csiSetCashrequestId(hsRes.result.cashrequest).save(failOnError:true) }
      } catch(Exception e) {
        log.debug("Error save data in Cash/incertcashrequest\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def cashrequest = {
    checkAccess(8)
    if(!checkAccessLevel(3..5)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    hsRes.cashrequest = Cashrequest.get(requestService.getIntDef('id',0))
    if (!hsRes.cashrequest) {
      response.sendError(404)
      return
    }

    hsRes.cashstatus = Cashreqstatus.list().inject([:]){map, status -> map[status.id]=status.name;map}
    hsRes.initiator = User.get(hsRes.cashrequest?.initiator)?.name
    hsRes.payrequest = Payrequest.findByCashrequest_id(hsRes.cashrequest.id)

    return hsRes
  }

  def updatecashrequest = {
    checkAccess(8)
    if(!checkAccessLevel(3..5)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getLongDef('id',0)
    hsRes+=requestService.getParams(['summa','modstatus'],null,['comment'],null,['margin'])

    hsRes.cashrequest = Cashrequest.get(lId)
    if (!hsRes.cashrequest) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<1
    if(hsRes.inrequest.modstatus==5&&!hsRes.inrequest.margin)
      hsRes.result.errorcode<<2

    if(!hsRes.result.errorcode){
      try {
        hsRes.cashrequest.setData(hsRes.inrequest,session.user.cashaccess).csiSetAdmin(session.user.id).csiSetModstatus(hsRes.inrequest.modstatus,session.user.cashaccess).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Cash/updatecashrequest\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def cashrequestzakaz = {
    checkAccess(8)
    if(!checkAccessLevel(3..5)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    hsRes.cashrequest = Cashrequest.get(requestService.getIntDef('id',0))
    if (!hsRes.cashrequest) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.cashzakazlist = new CashzakazSearch().csiSelectZakaz(0,0l,0,'',-100,0,hsRes.cashrequest.id,null,100,0)
    hsRes.departments = Department.list().inject([0:'']){map, department -> map[department.id]=department.name;map}

    return hsRes
  }

  def removefromrequest = {
    checkAccess(8)
    if(!checkAccessLevel(3)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8
    hsRes.result=[errorcode:[]]
 
    hsRes+=requestService.getParams(['cashzakaz_id'])

    hsRes.cashrequest = Cashrequest.get(requestService.getIntDef('id',0))
    hsRes.zakaz = Cashzakaz.get(hsRes.inrequest.cashzakaz_id)
    if (!hsRes.cashrequest||!hsRes.zakaz||hsRes.cashrequest?.modstatus>3) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.zakaz.csiSetCashrequestId(0).save(failOnError:true)
      if (hsRes.cashrequest.modstatus<=3) hsRes.cashrequest.csiSetSumma(Cashzakaz.findAllByCashrequest_idAndIdNotEqual(hsRes.cashrequest.id,hsRes.zakaz.id).sum{it.getSummaRub()}).csiSetAdmin(session.user.id).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Cash/removefromrequest\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def cashrequestnewzakaz = {
    checkAccess(8)
    if(!checkAccessLevel(3)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    hsRes.cashrequest = Cashrequest.get(requestService.getIntDef('id',0))
    if (!hsRes.cashrequest) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.cashzakazlist = new CashzakazSearch().csiSelectZakaz(0,0l,0,'',1,0,-1,null,100,0)
    hsRes.departments = Department.list().inject([0:'']){map, department -> map[department.id]=department.name;map}

    return hsRes
  }

  def addtorequest = {
    checkAccess(8)
    if(!checkAccessLevel(3)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8
    hsRes.result=[errorcode:[]]
 
    hsRes+=requestService.getParams(['cashzakaz_id'])

    hsRes.cashrequest = Cashrequest.get(requestService.getIntDef('id',0))
    hsRes.zakaz = Cashzakaz.get(hsRes.inrequest.cashzakaz_id)
    if (!hsRes.cashrequest||!hsRes.zakaz||hsRes.cashrequest?.modstatus>3) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.zakaz.csiSetCashrequestId(hsRes.cashrequest.id).save(failOnError:true)
      if (hsRes.cashrequest.modstatus<=3) hsRes.cashrequest.csiSetSumma((Cashzakaz.findAllByCashrequest_idAndIdNotEqual(hsRes.cashrequest.id,hsRes.zakaz.id).sum{it.getSummaRub()}?:0)+hsRes.zakaz.getSummaRub()).csiSetAdmin(session.user.id).save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Cash/addtorequest\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def cashrequesthistory = {
    checkAccess(8)
    if(!checkAccessLevel(3..5)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    def lId=requestService.getIntDef('id',0)
    hsRes.cashrequest = Cashrequest.get(lId)
    if (!hsRes.cashrequest) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.history = new CashrequesthistSearch().csiFindHistory(hsRes.cashrequest.id)
    hsRes.cashstatus = Cashreqstatus.list().inject([:]){map, status -> map[status.id]=status.name;map}

    return hsRes
  }

  def addcashreport = {
    checkAccess(8)
    if(!checkAccessLevel([0,1,2,3,4,6])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    hsRes.expensetypes = new ExpensetypeSearch().csiGetList('',session.user.id)
    hsRes.project = Project.findAllByModstatus(1,[sort:'name'])
    hsRes.defproject_id = Project.findByIs_main(1)?.id?:0
    hsRes.cars = Car.list(sort:'name')
    hsRes.expcar_ids = Expensetype.findAllByIs_car(1).collect{it.id}?:[]
    hsRes.departments = Department.list()
    hsRes.perslist = session.user.cashaccess==3?User.findAllByModstatusAndCashaccessInListAndUsergroup_idNotEqual(1,[1,3,6],1):Department.get(hsRes.user.department_id)?.is_cashextstaff?User.findAllByModstatusAndCashaccessAndUsergroup_idNotEqual(1,0,1):User.findAllByDepartment_idAndModstatus(hsRes.user.department_id,1)

    return hsRes
  }

  def incertcashreport = {
    checkAccess(8)
    if(!checkAccessLevel([0,1,2,3,4,6])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8
    hsRes.result=[errorcode:[]]
 
    hsRes+=requestService.getParams(['expensetype_id','type','project_id','department_id','car_id'],['summa','executor'],['description'])
    hsRes.inrequest.repdate = requestService.getDate('repdate')
    def hsData

    if(!hsRes.inrequest.repdate)
      hsRes.result.errorcode<<2
    if(session.user.cashaccess in [1,3,6]&&!hsRes.inrequest.expensetype_id)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.description)
      hsRes.result.errorcode<<6
    if(!hsRes.result.errorcode){
      imageService.init(this)
      hsData = imageService.rawUpload('file',true)
      if(hsData.error==2)
        hsRes.result.errorcode<<5
      else if(hsData.error)
        hsRes.result.errorcode<<1
    }

    if(!hsRes.result.errorcode){
      try {
        hsRes.result.cashreport = new Cashreport(file_id:imageService.rawUpload('file').fileid,executor:hsRes.inrequest.executor?:session.user.id,initiator:session.user.id,type:hsRes.inrequest.type?:0).csiSetDepartment(hsRes.inrequest,hsRes.user).setData(hsRes.inrequest,session.user.cashaccess).csiSetModstatus(session.user.cashaccess in [1,6]?1:session.user.cashaccess==3?2:0,session.user.cashaccess).save(failOnError:true)?.id?:0
      } catch(org.springframework.dao.TransientDataAccessResourceException err) {
        log.debug("Error save data in Cash/incertcashreport\n"+err.toString())
        hsRes.result.errorcode << 101
        Cashreport.withSession { session -> session.clear() }
      } catch(Exception e) {
        log.debug("Error save data in Cash/incertcashreport\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    return hsRes.result
  }

  def cashreport = {
    checkAccess(8)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    def lId=requestService.getIntDef('id',0)
    hsRes.cashreport = Cashreport.get(lId)
    if (!hsRes.cashreport||(session.user.cashaccess in [0,1]&&hsRes.cashreport.executor!=session.user.id)) {
      response.sendError(404)
      return
    }
    if(!checkReportAccess(hsRes.cashreport,hsRes.user.department_id)) return

    hsRes.cashstatus = Cashrepstatus.get(hsRes.cashreport.modstatus)
    hsRes.department = Department.get(hsRes.cashreport.department_id)
    hsRes.executor = User.get(hsRes.cashreport.executor)
    hsRes.initiator = User.get(hsRes.cashreport.initiator)
    hsRes.expensetypes = new ExpensetypeSearch().csiGetList('',hsRes.initiator?.id?:0,-1,hsRes.cashreport.expensetype_id)
    hsRes.project = Project.findAllByModstatus(1,[sort:'name'])
    hsRes.cars = Car.list(sort:'name')
    hsRes.expcar_ids = Expensetype.findAllByIs_car(1).collect{it.id}?:[]
    hsRes.is_canchange = hsRes.cashreport.executor==session.user.id&&session.user.cashaccess in [0,1,2,3,4,6]&&hsRes.cashreport.modstatus<1
    hsRes.is_canchangeexpensetype = (session.user.cashaccess in [1,2]&&hsRes.cashreport.modstatus<2)||(session.user.cashaccess in [3,6])
    hsRes.is_canchangescan = (hsRes.cashreport.executor==session.user.id||session.user.cashaccess in [2,3])&&hsRes.cashreport.modstatus<1
    hsRes.is_candelete = (hsRes.cashreport.executor==session.user.id||hsRes.cashreport.initiator==session.user.id||session.user.cashaccess in [2,3])&&hsRes.cashreport.modstatus<0

    return hsRes
  }

  def showscan = {
    checkAccess(8)
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

  def updatecashreport = {
    checkAccess(8)
    if(!checkAccessLevel([0,1,2,3,6])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getLongDef('id',0)
    hsRes.cashreport = Cashreport.get(lId)
    if (!hsRes.cashreport) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    if(!checkReportAccess(hsRes.cashreport,hsRes.user.department_id)) return

    hsRes+=requestService.getParams(['modstatus','expensetype_id','project_id','car_id'],['summa'],['description'])
    hsRes.inrequest.repdate = requestService.getDate('repdate')
    if (hsRes.inrequest.modstatus<2) hsRes.inrequest.comment = requestService.getStr('comment')
    if (hsRes.inrequest.modstatus<1) hsRes.inrequest.comment_dep = requestService.getStr('comment_dep')
    def hsData

    hsRes.is_canchange = hsRes.cashreport.executor==session.user.id&&session.user.cashaccess in [0,1,2,3,4,6]&&hsRes.cashreport.modstatus<1
    hsRes.is_canchangeexpensetype = (session.user.cashaccess in [1,2]&&hsRes.cashreport.modstatus<2)||(session.user.cashaccess in [3,6])
    hsRes.is_canchangescan = (hsRes.cashreport.executor==session.user.id||session.user.cashaccess in [2,3])&&hsRes.cashreport.modstatus<1

    imageService.init(this)

    if(hsRes.is_canchange&&!hsRes.inrequest.summa)
      hsRes.result.errorcode<<2
    if(hsRes.inrequest.modstatus==-2&&!hsRes.inrequest.comment)
      hsRes.result.errorcode<<3
    if(hsRes.inrequest.modstatus==-1&&!hsRes.inrequest.comment_dep)
      hsRes.result.errorcode<<4
    if(hsRes.is_canchange&&!hsRes.inrequest.repdate)
      hsRes.result.errorcode<<5
    if(hsRes.is_canchangeexpensetype&&!hsRes.inrequest.expensetype_id)
      hsRes.result.errorcode<<6
    if(hsRes.is_canchange&&!hsRes.inrequest.description)
      hsRes.result.errorcode<<7
    if(hsRes.is_canchangescan&&!hsRes.result.errorcode){
      hsData = imageService.rawUpload('file',true)
      if(hsData.error in [1,3])
        hsRes.result.errorcode<<1
    }

    if(!hsRes.result.errorcode){
      try {
        hsRes.cashreport.setData(hsRes.inrequest,session.user.cashaccess).csiSetFileId(imageService.rawUpload('file').fileid).csiSetAdmin(session.user.id).csiSetModstatus(hsRes.inrequest.modstatus?:session.user.cashaccess in [1,6]?1:0,session.user.cashaccess).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Cash/updatecashreport\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    return hsRes.result
  }

  def deletereport = {
    checkAccess(8)
    if(!checkAccessLevel([0,1,2,3,4,6])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    hsRes.cashreport = Cashreport.get(requestService.getIntDef('id',0))
    if (hsRes.cashreport?.modstatus>=0||(hsRes.cashreport.executor!=session.user.id&&hsRes.cashreport.initiator!=session.user.id&&!(session.user.cashaccess in [2,3]))) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.cashreport.delete(failOnError:true,flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Cash/deletereport\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def cashreporthistory = {
    checkAccess(8)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    def lId=requestService.getIntDef('id',0)
    hsRes.cashreport = Cashreport.get(lId)
    if (!hsRes.cashreport) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    if(!checkReportAccess(hsRes.cashreport,hsRes.user.department_id)) return

    hsRes.events = Cashevent.findAllByCashreport_id(hsRes.cashreport.id,[sort:'inputdate',order:'desc'])
    hsRes.eventtypes = Casheventtype.list().inject([:]){map, eventtype -> map[eventtype.id]=eventtype.name;map}

    return hsRes
  }

  def maincashrecord = {
    checkAccess(8)
    if(!checkAccessLevel([3,5,6,7])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    def lId=requestService.getIntDef('id',0)
    hsRes.cashrecord = Cash.get(lId)
    if (!hsRes.cashrecord&&lId) {
      response.sendError(404)
      return
    }

    hsRes.cashclasses = Cashclass."findAllByIs_type${hsRes.cashrecord?.type?:1}"(1,[sort:'name',order:'asc'])
    hsRes.nontaggedclasses = Cashclass.findAllByIs_defaultexpense(1).collect{ [id:it.id,exp_id:Tools.getIntVal(Dynconfig.findByName(it.confkey)?.value,0)] } as JSON
    hsRes.departments = Department.findAllByIs_extra(0,[sort:'name',order:'asc'])
    hsRes.executors = hsRes.cashrecord?.department_id?User.findAllByDepartment_idAndModstatusAndUsergroup_idNotEqual(hsRes.cashrecord.department_id,1,1,[sort:'name',order:'asc']):User.findAllByCashaccessInListAndModstatusAndUsergroup_idNotEqual([1,3,5,6,7],1,1,[sort:'name',order:'asc'])
    hsRes.loanusers = User.findAllByModstatusAndIs_loanAndUsergroup_idNotEqual(1,1,1)
    hsRes.parkingusers = User.findAllByModstatusAndIs_parkingAndUsergroup_idNotEqual(1,1,1)
    hsRes.agentagrs = Agentagr.findAllByModstatus(1)
    hsRes.agents = new AgentrateSearch().csiSelectAgents(hsRes.cashrecord?.agentagr_id?:0)
    hsRes.indeposits = Indeposit.findAllByModstatusInListAndAclass(0..1,2)
    hsRes.project = Project.findAllByModstatus(1,[sort:'name'])
    hsRes.cars = Car.list(sort:'name')
    hsRes.defproject_id = Project.findByIs_main(1)?.id?:0
    hsRes.expensetype = new ExpensetypeSearch().csiGetList('',0,-1,hsRes.cashrecord?.expensetype_id?:0)
    hsRes.expcar_ids = Expensetype.findAllByIs_car(1).collect{it.id}?:[]
    hsRes.admin = User.get(hsRes.cashrecord?.admin_id?:0)?.name
    hsRes.tagadmin = User.get(hsRes.cashrecord?.tagadmin_id?:0)?.name
    hsRes.iscanedit = session.user.cashaccess in [3,6]
    hsRes.iscanadd = session.user.cashaccess==3

    return hsRes
  }

  def maincashagentlist={
    checkAccess(8)
    if(!checkAccessLevel([3,5,6,7])) return
    requestService.init(this)

    return [agents:new AgentrateSearch().csiSelectAgents(requestService.getIntDef('agentagr',0))]
  }

  def maincashperslist={
    checkAccess(8)
    if(!checkAccessLevel([3,5,6,7])) return
    requestService.init(this)

    return [executors:requestService.getIntDef('department_id',0)?User.findAllByDepartment_idAndModstatusAndUsergroup_idNotEqual(requestService.getIntDef('department_id',0),1,1,[sort:'name',order:'asc']):User.findAllByCashaccessInListAndModstatusAndUsergroup_idNotEqual([1,3,5,6,7],1,1,[sort:'name',order:'asc'])]
  }

  def maincashclasslist={
    checkAccess(8)
    if(!checkAccessLevel([3,5,6,7])) return
    requestService.init(this)

    return [cashclasses:(requestService.getIntDef('type',1)!=-100?Cashclass."findAllByIs_type${requestService.getIntDef('type',1)}"(1,[sort:'name',order:'asc']):Cashclass.list(sort:'name',order:'asc'))]
  }

  def exppodrazdellist={
    checkAccess(8)
    requestService.init(this)

    return [exppodrazdel:requestService.getIntDef('razdel',0)?Expensetype2.findAllByExpensetype1_id(requestService.getIntDef('razdel',0)):Expensetype2.list()]
  }

  def exptypelist={
    checkAccess(8)
    requestService.init(this)

    return [expensetypes:!requestService.getIntDef('razdel',0)?Expensetype.list():!requestService.getIntDef('podrazdel',0)?Expensetype.findAllByExpensetype1_id(requestService.getIntDef('razdel',0)):Expensetype.findAllByExpensetype1_idAndExpensetype2_id(requestService.getIntDef('razdel',0),requestService.getIntDef('podrazdel',0))]
  }

  def updatemaincashrecord = {
    checkAccess(8)
    if(!checkAccessLevel([3,6])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8
    hsRes.result=[errorcode:[]]
 
    def lId = requestService.getLongDef('id',0)
    hsRes.cashrecord = Cash.get(lId)
    if (!hsRes.cashrecord&&lId) {
      response.sendError(404)
      return
    }

    hsRes+=requestService.getParams(['valuta_id','maincashtype','maincashclass','department_id','agentagr_id','agent_id',
                                     'project_id','expensetype_id','car_id','indeposit_id'],['summa','pers_id','loaner_id',
                                     'parkinger_id'],['comment'])
    hsRes.inrequest.operationdate = requestService.getDate('operationdate')
    hsRes.inrequest.platperiod = requestService.getRaw('platperiod')

    def hsData

    imageService.init(this)

    if (!hsRes.cashrecord||hsRes.cashrecord?.id==Cash.getLastId()){
      if(!hsRes.inrequest.summa)
        hsRes.result.errorcode<<2
      if(!hsRes.inrequest.maincashclass)
        hsRes.result.errorcode<<3
      else if(hsRes.inrequest.maincashtype!=2&&!(hsRes.inrequest.maincashclass in [1,3,7,8,9,12,16,17,18])&&!hsRes.inrequest.department_id&&!hsRes.inrequest.pers_id)
        hsRes.result.errorcode<<4
      else if(hsRes.inrequest.maincashclass==16&&!hsRes.inrequest.pers_id)
        hsRes.result.errorcode<<4
      else if(hsRes.inrequest.maincashtype!=2&&hsRes.inrequest.maincashclass==3&&(!hsRes.inrequest.agentagr_id||!hsRes.inrequest.agent_id))
        hsRes.result.errorcode<<7
      else if(hsRes.inrequest.maincashtype==2&&hsRes.inrequest.maincashclass==3&&!hsRes.inrequest.agentagr_id)
        hsRes.result.errorcode<<8
      else if(hsRes.inrequest.maincashclass==4&&!hsRes.inrequest.department_id)
        hsRes.result.errorcode<<10
      else if(hsRes.inrequest.maincashclass==7&&hsRes.inrequest.project_id==Project.findByIs_main(1).id&&!hsRes.inrequest.loaner_id)
        hsRes.result.errorcode<<11
      else if(hsRes.inrequest.maincashclass==12&&!hsRes.inrequest.parkinger_id)
        hsRes.result.errorcode<<12
      else if(hsRes.inrequest.maincashclass in [18,19]&&!hsRes.inrequest.indeposit_id)
        hsRes.result.errorcode<<14
      if(!hsRes.inrequest.operationdate)
        hsRes.result.errorcode<<5
    }
    if(!hsRes.inrequest.comment)
      hsRes.result.errorcode<<13
    if(!hsRes.inrequest.expensetype_id)
      hsRes.result.errorcode<<9
    if(!hsRes.result.errorcode){
      hsData = imageService.rawUpload('file',true)
      if(!(hsRes.cashrecord?.receipt>0)&&hsRes.inrequest.maincashtype==1&&hsRes.inrequest.maincashclass!=1&&hsData.error==2)
        hsRes.result.errorcode<<6
      else if(hsData.error in [1,3])
        hsRes.result.errorcode<<1
    }

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.cashrecord = new Cash()
        hsRes.cashrecord.csiSetReceipt(imageService.rawUpload('file').fileid).csiSetAdmin(session.user.id).csiSetTagAdmin(session.user.id).setData(hsRes.inrequest).save(failOnError:true)
      } catch(org.springframework.dao.TransientDataAccessResourceException err) {
        log.debug("Error save data in Cash/updatemaincashrecord\n"+err.toString())
        hsRes.result.errorcode << 101
        Cash.withSession { session -> session.clear() }
      } catch(Exception e) {
        log.debug("Error save data in Cash/updatemaincashrecord\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    return hsRes.result
  }

  def depcashrecord = {
    checkAccess(8)
    if(!checkAccessLevel([2,3,5,6])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8

    def lId=requestService.getIntDef('id',0)
    hsRes.cashrecord = Cashdepartment.get(lId)
    if (!hsRes.cashrecord&&lId) {
      response.sendError(404)
      return
    }

    hsRes.departments = session.user.cashaccess==2?Department.findAllByIdOrParent(hsRes.user.department_id,hsRes.user.department_id,[sort:'name',order:'asc'])+(hsRes.user.is_tehdirleader?[Department.findByIs_tehdir(1)]:[]):Department.list(sort:'name',order:'asc')
    hsRes.perslist = (User.findAllByCashaccessGreaterThanAndDepartment_idInListAndModstatus(session.user.cashaccess==2?-1:hsRes.cashrecord?.department_id?-1:0,Department.findAllByIdOrParent(session.user.cashaccess==2?hsRes.user.department_id:hsRes.cashrecord?.department_id?:0,session.user.cashaccess==2?hsRes.user.department_id:hsRes.cashrecord?.department_id?:-1).collect{it.id}?:[],1)+(Department.get(hsRes.user.department_id)?.is_cashextstaff?User.findAllByModstatusAndCashaccessAndUsergroup_idNotEqualAndDepartment_idNotEqual(1,0,1,session.user.cashaccess==2?hsRes.user.department_id:hsRes.cashrecord?.department_id?:0):[])).sort{it.name}
    hsRes.iscanedit = !hsRes.cashrecord&&session.user.cashaccess==2

    return hsRes
  }

  def userperslist = {
    checkAccess(8)
    if(!checkAccessLevel([2])) return
    requestService.init(this)

    def oDepartment = Department.get(requestService.getIntDef('department_id',0))

    return [perslist:oDepartment?.is_tehdir?User.findAllByDepartment_idAndModstatusAndPers_idInList(0,1,Pers.findAllByPerstype(2).collect{it.id},[sort:'name',order:'asc']):(User.findAllByDepartment_idInListAndModstatus(Department.findAllByIdOrParent(oDepartment?.id?:0,oDepartment?.id?:-1).collect{it.id}?:[],1)+(oDepartment?.is_cashextstaff?User.findAllByModstatusAndCashaccessAndUsergroup_idNotEqualAndDepartment_idNotEqual(1,0,1,oDepartment?.id):[])).sort{it.name}]
  }

  def updatedepcashrecord = {
    checkAccess(8)
    if(!checkAccessLevel(2)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 8
    hsRes.result=[errorcode:[]]
 
    hsRes+=requestService.getParams(['valuta_id','depcashtype','depcashclass','department_id'],['summa','pers_id'],['comment'])
    hsRes.inrequest.operationdate = requestService.getDate('operationdate')
    hsRes.inrequest.platperiod = requestService.getRaw('platperiod')

    def hsData

    imageService.init(this)

    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.depcashclass)
      hsRes.result.errorcode<<3
    else if(hsRes.inrequest.depcashclass==1&&!hsRes.user.is_leader)
      hsRes.result.errorcode<<8
    if(!hsRes.inrequest.pers_id)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.operationdate)
      hsRes.result.errorcode<<5
    if(!hsRes.inrequest.comment)
      hsRes.result.errorcode<<6
    if(!hsRes.result.errorcode){
      hsData = imageService.rawUpload('file',true)
      if(hsRes.inrequest.depcashtype==1&&hsRes.inrequest.depcashclass==1&&hsData.error==2)
        hsRes.result.errorcode<<7
      if(hsData.error in [1,3])
        hsRes.result.errorcode<<1
    }

    if(!hsRes.result.errorcode){
      try {
        new Cashdepartment(department_id:hsRes.inrequest.department_id,pers_id:hsRes.inrequest.pers_id,is_dep:1).csiSetReceipt(imageService.rawUpload('file').fileid).csiSetAdmin(session.user.id).setData(hsRes.inrequest).save(failOnError:true)?.id?:0
        if(hsRes.inrequest.depcashclass==1&&hsRes.inrequest.depcashtype==3) User.findByIdAndModstatus(hsRes.inrequest.pers_id,1,[sort:'cassadebt',order:'desc'])?.updateCassadebt(-hsRes.inrequest.summa)?.save(flush:true)
      } catch(Exception e) {
        log.debug("Error save data in Cash/updatedepcashrecord\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    return hsRes.result
  }

}