import grails.converters.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import pl.touk.excel.export.WebXlsxExporter

class TaskController {
  def requestService

  final String TMY = 'is_taskmy'
  final String TALL = 'is_taskall'
  final String TENQ = 'is_enquiry'
  final String TENQEDIT = 'is_enquiryedit'
  final String TTPAY = 'is_taskpay'
  final String TTPAYALL = 'is_taskpayall'
  final String TTPAYNEW = 'is_payplantask'
  final String TPPEXEC = 'is_payplanexec'
  final String TPACCEPT = 'is_payaccept'
  final String TCLACCEPT = 'is_clientpaynew'
  final String TPVIEW1 = 'is_viewbudgpayplantask'
  final String TPVIEW2 = 'is_viewkredpayplantask'
  final String TPVIEW3 = 'is_viewrentpayplantask'
  final String TPVIEW4 = 'is_viewgnrlpayplantask'
  final String TSPACE = 'is_arenda'
  final String TSPPERM = 'is_prolongpermit'
  final String TSPWORK = 'is_prolongwork'

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

  private Boolean checkTaskAccess(Task _task) {
    if (!_task && !recieveSectionPermission(TMY)) return false
    if (recieveSectionPermission(TALL)) return true
    if (_task.initiator!=session.user.id&&_task.remapper!=session.user.id&&_task.executor!=session.user.id&&_task.department_id!=session.user.department_id) return false
    return true
  }

  private def collectPaygroup() {
    def paygroups = [0]
    if (recieveSectionPermission([TPACCEPT,TTPAYNEW,TCLACCEPT])) return [1,2,3,4]
    if (recieveSectionPermission(TPVIEW1)) paygroups << 1
    if (recieveSectionPermission(TPVIEW2)) paygroups << 2
    if (recieveSectionPermission(TPVIEW3)) paygroups << 3
    if (recieveSectionPermission(TPVIEW4)) paygroups << 4
    paygroups
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Main >>>//////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  def index = {
    checkAccess(9)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    def fromDetails = requestService.getIntDef('fromDetails',0)

    if (fromDetails&&session.tasklastRequest){
      session.tasklastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.tasklastRequest
    } else {
      hsRes.inrequest=[:]
      hsRes.inrequest.taskobject = requestService.getIntDef('taskobject',0)
      hsRes.inrequest.is_accept = requestService.getIntDef('is_accept',-100)
      if (!hsRes.inrequest.is_accept) session.tasklastRequest = hsRes.inrequest + [fromDetails:1]
    }
    hsRes.isenquiry = recieveSectionPermission(TENQ)
    hsRes.isspprolong = recieveSectionPermission(TSPACE)
    hsRes.ismy = recieveSectionPermission(TMY)
    hsRes.isall = recieveSectionPermission(TALL)

    return hsRes
  }

  def taskfilter = {
    checkAccess(9)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    if (session.tasklastRequest?.fromDetails){
      hsRes.inrequest = session.tasklastRequest
    }

    return hsRes
  }

  def taskmyfilter = {
    checkAccess(9)
    if (!checkSectionPermission(TMY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    if (session.tasklastRequest?.fromDetails){
      hsRes.inrequest = session.tasklastRequest
    }

    return hsRes
  }

  def taskallfilter = {
    checkAccess(9)
    if (!checkSectionPermission(TALL)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    if (session.tasklastRequest?.fromDetails){
      hsRes.inrequest = session.tasklastRequest
    }

    hsRes.executor = User.findAllByModstatusAndDepartment_id(1,hsRes.inrequest?.department_id?:-1,[sort:'name',order:'asc'])

    return hsRes
  }

  def tasklist = {
    checkAccess(9)
    requestService.init(this)
    def hsRes = [:]
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    if (session.tasklastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.tasklastRequest
      session.tasklastRequest.fromDetails = 0
    } else {
      hsRes.inrequest = [:]
      hsRes.inrequest.offset = requestService.getOffset()
      session.tasklastRequest = hsRes.inrequest
    }
    session.tasklastRequest.taskobject = 0

    hsRes.searchresult = new TaskSearch().csiSelectTask(-1,hsRes.user.id,-1,0,0,20,hsRes.inrequest.offset)
    hsRes.departments = Department.list().inject([:]){map, department -> map[department.id]=department.name;map}
    hsRes.taskstatuses = Taskstatus.list().inject([:]){map, status -> map[status.id]=status.name;map}
    hsRes.tasktypes = Tasktype.list().inject([:]){map, type -> map[type.id]=type.name;map}

    return hsRes
  }

  def taskmylist = {
    checkAccess(9)
    if (!checkSectionPermission(TMY)) return
    requestService.init(this)
    def hsRes = [:]
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    if (session.tasklastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.tasklastRequest
      session.tasklastRequest.fromDetails = 0
    } else {
      hsRes.inrequest = [:]
      hsRes.inrequest.offset = requestService.getOffset()
      session.tasklastRequest = hsRes.inrequest
    }
    session.tasklastRequest.taskobject = 1

    hsRes.searchresult = new TaskSearch().csiSelectTask(hsRes.user.id,-1,-1,0,0,20,hsRes.inrequest.offset)
    hsRes.departments = Department.list().inject([:]){map, department -> map[department.id]=department.name;map}
    hsRes.taskstatuses = Taskstatus.list().inject([:]){map, status -> map[status.id]=status.name;map}
    hsRes.tasktypes = Tasktype.list().inject([:]){map, type -> map[type.id]=type.name;map}

    return hsRes
  }

  def taskalllist = {
    checkAccess(9)
    if (!checkSectionPermission(TALL)) return
    requestService.init(this)
    def hsRes = [:]
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    if (session.tasklastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.tasklastRequest
      session.tasklastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(null,['executor'])
      hsRes.inrequest.department_id = requestService.getIntDef('department_id',-1)
      hsRes.inrequest.taskstatus = requestService.getIntDef('taskstatus',0)
      hsRes.inrequest.offset = requestService.getOffset()
      session.tasklastRequest = hsRes.inrequest
    }
    session.tasklastRequest.taskobject = 2

    hsRes.searchresult = new TaskSearch().csiSelectTask(-1,-1,hsRes.inrequest.taskstatus,hsRes.inrequest.department_id,hsRes.inrequest.executor?:0,20,hsRes.inrequest.offset)
    hsRes.departments = Department.list().inject([:]){map, department -> map[department.id]=department.name;map}
    hsRes.taskstatuses = Taskstatus.list().inject([:]){map, status -> map[status.id]=status.name;map}
    hsRes.tasktypes = Tasktype.list().inject([:]){map, type -> map[type.id]=type.name;map}

    return hsRes
  }

  def taskdetail = {
    checkAccess(9)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    def lId=requestService.getIntDef('id',0)
    hsRes.task = Task.get(lId)
    if (!hsRes.task&&lId) {
      response.sendError(404)
      return
    }
    if (!checkTaskAccess(hsRes.task)) {
      response.sendError(403)
      return
    }
    hsRes+=requestService.getParams(['payment_id'])

    hsRes.department=Department.list([sort:'name',order:'asc'])
    hsRes.tasktype=Tasktype.list([sort:'name',order:'asc'])
    hsRes.taskstatus=Taskstatus.list()

    hsRes.executor = User.findAllByModstatusAndDepartment_id(1,hsRes.task?.department_id?:-1,[sort:'name',order:'asc'])
    if(!hsRes.task && hsRes.inrequest.payment_id){
      def oPayment = Payment.get(hsRes.inrequest.payment_id)
      hsRes.default_tasktype = 9
      hsRes.plink = oPayment?.id?:0
      hsRes.description = oPayment?.collectInfodata()?:''
    }
    hsRes.is_initiator = (hsRes.task?.remapper?:hsRes.task?.initiator)==hsRes.user.id
    hsRes.is_executor = hsRes.task?.executor==hsRes.user.id||(hsRes.task?.department_id==hsRes.user.department_id&&hsRes.task?.executor==0)

    return hsRes
  }

  def executor = {
    checkAccess(9)
    requestService.init(this)
    def hsRes =[:]
    def iDepartment=requestService.getIntDef('department_id',-1)
    def bAll=requestService.getIntDef('all',0)
    if(iDepartment)
      hsRes.executor=User.findAll("FROM User WHERE department_id=:department_id AND modstatus=1 AND id!=:id ORDER BY name ASC",[department_id:iDepartment,id:(bAll?-1L:session.user.id)])

    return hsRes
  }

  def saveTaskDetail = {
    checkAccess(9)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.result=[errorcode:[]]

    def lId = requestService.getIntDef('id',0)
    hsRes.task = Task.get(lId)
    if (!hsRes.task&&lId) {
      response.sendError(404)
      return
    }
    if (!checkTaskAccess(hsRes.task)) {
      response.sendError(403)
      return
    }

    hsRes+=requestService.getParams(['tasktype_id','department_id','taskstatus','link','is_remap'],['executor'],['description','term'])
    if (hsRes.inrequest.is_remap) hsRes.inrequest.taskstatus = 3

    if (!hsRes.task){
      if(!hsRes.inrequest.tasktype_id)
        hsRes.result.errorcode<<1
      if(!hsRes.inrequest.term)
        hsRes.result.errorcode<<2
      else if(!hsRes.inrequest.term.matches('\\d{2}\\.\\d{2}\\.\\d{4}'))
        hsRes.result.errorcode<<3
      if(!hsRes.inrequest.department_id)
        hsRes.result.errorcode<<4
    } else {
      if (hsRes.inrequest.is_remap&&!hsRes.inrequest.executor)
        hsRes.result.errorcode<<5
      else if (hsRes.inrequest.is_remap&&hsRes.inrequest.executor==hsRes.task.executor)
        hsRes.result.errorcode<<6
      if (hsRes.inrequest.is_remap&&((hsRes.task.remapper?:hsRes.task.initiator)!=hsRes.user.id)&&(hsRes.inrequest.description?:'')==hsRes.task.description)
        hsRes.result.errorcode<<7
      else if (hsRes.inrequest.taskstatus==5&&hsRes.inrequest.description==hsRes.task.description)
        hsRes.result.errorcode<<7
    }

    if(!hsRes.result.errorcode){
      try {
        if(!hsRes.task) hsRes.task = new Task(initiator:hsRes.user.id).setBaseData(hsRes.inrequest)
        hsRes.result.task_id = hsRes.task.csiSetAdmin(session.user.id).setData(hsRes.inrequest).csiSetTaskstatus(hsRes.inrequest).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Task/saveTaskDetail\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }

  def taskeventlist = {
    checkAccess(9)
    requestService.init(this)
    def hsRes=[taskevent:[]]
    hsRes.user = session.user

    hsRes.taskevent=Taskevent.findAll("FROM Taskevent WHERE task_id=:task_id ORDER BY id desc",[task_id:requestService.getIntDef('id',0)])

    return hsRes
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////taskpay >>>//////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  def taskpayfilter = {
    checkAccess(9)
    if (!checkSectionPermission(TTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    if (session.tasklastRequest?.fromDetails){
      hsRes.inrequest = session.tasklastRequest
    }

    hsRes.taskpaystatus = Taskpaystatus.list()
    hsRes.isall = recieveSectionPermission(TTPAYALL)
    hsRes.iscanaccept = recieveSectionPermission([TCLACCEPT,TPACCEPT])

    return hsRes
  }

  def taskpaylist = {
    checkAccess(9)
    if (!checkSectionPermission(TTPAY)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    if (session.tasklastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.tasklastRequest
      session.tasklastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['paygroup','tid'],null,['company','executor'])
      hsRes.inrequest.taskpaystatus = requestService.getIntDef('taskpaystatus',0)
      hsRes.inrequest.is_accept = requestService.getIntDef('is_accept',0)
      hsRes.inrequest.termdate = requestService.getDate('termdate')
      hsRes.inrequest.offset = requestService.getOffset()
      session.tasklastRequest = hsRes.inrequest
    }
    session.tasklastRequest.taskobject = 3

    hsRes.searchresult = new TaskpaySearch().csiSelectTaskpay(hsRes.user.id,collectPaygroup(),hsRes.inrequest.tid?:0,
                                                              hsRes.inrequest.executor?:'',hsRes.inrequest.taskpaystatus?:0,
                                                              hsRes.inrequest.company?:'',hsRes.inrequest.termdate,
                                                              !recieveSectionPermission([TCLACCEPT,TPACCEPT])?1:hsRes.inrequest.is_accept?:0,
                                                              hsRes.inrequest.paygroup?:0,20,hsRes.inrequest.offset?:0)
    hsRes.taskpaystatuses = Taskpaystatus.list().inject([:]){map, status -> map[status.id]=status.name;map}
    hsRes.iscandelete = recieveSectionPermission(TTPAYNEW)

    return hsRes
  }
  ///////////////////////////////////////////////////////////////////////////////////////////
  def taskpaydetail = {
    checkAccess(9)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    hsRes.taskpay = Taskpay.get(requestService.getIntDef('id',0))
    if (!hsRes.taskpay) {
      response.sendError(404)
      return
    }

    hsRes.bankaccount = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(hsRes.taskpay.company_id,1,1,[sort:'schet',order:'asc'])
    hsRes.curbankaccount = Bankaccount.get(hsRes.taskpay.bankaccount_id)

    if(hsRes.curbankaccount){
      hsRes.bank = Bank.get(hsRes.curbankaccount.bank_id)
      hsRes.cursaldo = hsRes.curbankaccount.actsaldo - (Taskpay.findAllByModdateGreaterThanEqualsAndBankaccount_idAndTaskpaystatusInList(hsRes.curbankaccount.actsaldodate?:new Date()-365,hsRes.curbankaccount.id,[2,4]).sum{it.summa}?:0) + (Payrequest.findAllByPaydateGreaterThanEqualsAndTobankaccount_idAndPaytypeAndModstatusGreaterThan(hsRes.curbankaccount.actsaldodate?:new Date()-365,hsRes.curbankaccount.id,2,2).sum{it.summa}?:0) + (Payrequest.findAllByPaydateGreaterThanEqualsAndTobankaccount_idAndPaytypeAndModstatusGreaterThanAndPaycatAndIs_dop(hsRes.curbankaccount.actsaldodate?:new Date()-365,hsRes.curbankaccount.id,3,2,4,1).sum{it.summa}?:0) - (Payrequest.findAllByPaydateGreaterThanEqualsAndBankaccount_idAndPaytypeAndModstatusGreaterThanAndPaycatAndIs_dop(hsRes.curbankaccount.actsaldodate?:new Date()-365,hsRes.curbankaccount.id,3,2,4,1).sum{it.summa}?:0) - (Taskpay.findAllByBankaccount_idAndTaskpaystatusInListAndIs_accept(hsRes.curbankaccount.id,[0,1,3,5],1).sum{it.summa}?:0)
      hsRes.accsaldo = Taskpay.findAllByBankaccount_idAndTaskpaystatusInListAndIdNotEqual(hsRes.curbankaccount.id,[-1,0,1,3,5],hsRes.taskpay.id).sum{it.summa}?:0.0g
      hsRes.compsaldo = Taskpay.findAllByCompany_idAndBankaccount_idAndTaskpaystatusInListAndIdNotEqual(hsRes.taskpay.company_id,0,[-1,0,1,3,5],hsRes.taskpay.id).sum{it.summa}?:0.0g
      hsRes.totalsaldo = hsRes.cursaldo - (!hsRes.taskpay.is_accept?hsRes.taskpay.summa:0)
    }

    hsRes.taskpaystatus = Taskpaystatus.list()
    hsRes.executor = new UserpersSearch().csiFindByAccessrigth(TPPEXEC)
    hsRes.nds = Tools.getIntVal(ConfigurationHolder.config.payment.nds,18)
    hsRes.iscancreate = recieveSectionPermission(TTPAYNEW)
    hsRes.iscanaccept = recieveSectionPermission(hsRes.taskpay.is_client||hsRes.taskpay.is_internal?TCLACCEPT:TPACCEPT)&&!hsRes.taskpay.assertCompany()
    hsRes.iscanexec = recieveSectionPermission(TPPEXEC)
    hsRes.iscandelete = recieveSectionPermission(TTPAYNEW)&&!Payrequest.findAllByTaskpay_id(hsRes.taskpay.id)
    if(session.tasklastRequest?.taskobject != 3) session.tasklastRequest = [taskobject:3]

    return hsRes
  }

  def savetaskpaydetail = {
    checkAccess(9)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9
    hsRes.result=[errorcode:[]]

    hsRes.taskpay = Taskpay.get(requestService.getIntDef('id',0))
    if (!hsRes.taskpay) {
      response.sendError(404)
      return
    }
    if(!recieveSectionPermission(TTPAYNEW)&&!recieveSectionPermission(TPACCEPT)&&!recieveSectionPermission(TPPEXEC)&&!recieveSectionPermission(TCLACCEPT)){
      response.sendError(403)
      return
    }

    hsRes+=requestService.getParams(['bankaccount_id','taskpaystatus'],['executor'],['company'],['term'])
    if (recieveSectionPermission(TTPAYNEW)) hsRes.inrequest.description = requestService.getStr('description')
    if (recieveSectionPermission(hsRes.taskpay.is_client||hsRes.taskpay.is_internal?TCLACCEPT:TPACCEPT)) {
      hsRes.inrequest.is_accept = requestService.getIntDef('is_accept',0)
      hsRes.inrequest.is_urgent = requestService.getIntDef('is_urgent',0)
      hsRes.inrequest.plan = requestService.getStr('plan')
    } else {
      hsRes.inrequest.is_accept = hsRes.taskpay.is_accept
      hsRes.inrequest.is_urgent = hsRes.taskpay.is_urgent
    }
    if (recieveSectionPermission(TPPEXEC)) {
      hsRes.inrequest.payway = requestService.getIntDef('payway',0)
      hsRes.inrequest.comment = requestService.getStr('comment')
    } else {
      hsRes.inrequest.payway = hsRes.taskpay.payway
    }
    if (!hsRes.inrequest.taskpaystatus&&hsRes.inrequest.is_accept==1&&hsRes.taskpay.is_accept==0&&hsRes.inrequest.executor>0) hsRes.inrequest.taskpaystatus = 1

    if(!hsRes.taskpay.is_accept&&hsRes.taskpay.taskpaystatus!=-1){
      if(!hsRes.inrequest.term)
        hsRes.result.errorcode<<1
      if (hsRes.taskpay.is_t) {
        if(!hsRes.inrequest.company)
          hsRes.result.errorcode<<5
        else if(!Company.findByNameOrInn(hsRes.inrequest.company,hsRes.inrequest.company))
          hsRes.result.errorcode<<6
        else if(Company.findAllByNameOrInn(hsRes.inrequest.company,hsRes.inrequest.company).size()>1)
          hsRes.result.errorcode<<7
      }
      if(!hsRes.taskpay.assertCompany() && !hsRes.inrequest.bankaccount_id)
        hsRes.result.errorcode<<3
    } else if (hsRes.taskpay.is_accept && hsRes.taskpay.taskpaystatus==0 && hsRes.inrequest.is_accept && hsRes.inrequest.taskpaystatus==1 && !hsRes.inrequest.executor) {
      hsRes.result.errorcode<<2
    }

    if(!hsRes.result.errorcode){
      try {
        hsRes.taskpay.updateTaskpay(hsRes.inrequest).acceptTask(hsRes.inrequest.is_accept?hsRes.user.id:0l).csiSetTaskpaystatus(hsRes.inrequest.taskpaystatus?:0).csiSetInitiator(hsRes.user.id).csiSetUrgent(hsRes.inrequest.is_urgent?:0).save(failOnError:true)
        if (!hsRes.taskpay.is_accept && hsRes.taskpay.is_t) Payrequest.findAllByTaskpay_idAndPaytypeInList(hsRes.taskpay.id,[1,3,8]).each{ prequest -> prequest.updatecompany(hsRes.taskpay.company_id).save(failOnError:true,flush:true) }
        else Payrequest.findAllByTaskpay_idAndPaytypeInList(hsRes.taskpay.id,[1,3,8]).each{ prequest -> prequest.csiSetBankaccount_id(hsRes.taskpay.bankaccount_id).csiSetPayway(hsRes.inrequest.payway).save(flush:true,failOnError:true) }
      } catch(Exception e) {
        log.debug("Error save data in Task/savetaskpaydetail\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }

  def deletetaskpay = {
    checkAccess(9)
    if (!checkSectionPermission(TTPAYNEW)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    hsRes.taskpay = Taskpay.get(requestService.getIntDef('id',0))
    if (!hsRes.taskpay||(Payrequest.findAllByTaskpay_id(hsRes.taskpay?.id)&&hsRes.taskpay?.is_manual==0)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(session.tasklastRequest?.taskobject != 3) session.tasklastRequest = [taskobject:3]
    try {
      if (hsRes.taskpay.is_manual==1) Payrequest.findAllByTaskpay_id(hsRes.taskpay.id).each{ it.delete(flush:true) }
      hsRes.taskpay.delete(flush:true)
    } catch(Exception e) {
      log.debug("Error save data in Task/deletetaskpay\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def bankaccount = {
    checkAccess(9)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    hsRes.curbankaccount = Bankaccount.findByIdAndModstatus(requestService.getIntDef('id',0),1)
    hsRes.taskpay = Taskpay.get(requestService.getIntDef('taskpay_id',0))
    if (!hsRes.curbankaccount||!hsRes.taskpay) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.bank = Bank.get(hsRes.curbankaccount.bank_id)
    hsRes.cursaldo = hsRes.curbankaccount.actsaldo - (Taskpay.findAllByModdateGreaterThanEqualsAndBankaccount_idAndTaskpaystatusInList(hsRes.curbankaccount.actsaldodate?:new Date()-365,hsRes.curbankaccount.id,[2,4]).sum{it.summa}?:0) + (Payrequest.findAllByPaydateGreaterThanEqualsAndTobankaccount_idAndPaytypeAndModstatusGreaterThan(hsRes.curbankaccount.actsaldodate?:new Date()-365,hsRes.curbankaccount.id,2,2).sum{it.summa}?:0) + (Payrequest.findAllByPaydateGreaterThanEqualsAndTobankaccount_idAndPaytypeAndModstatusGreaterThanAndPaycatAndIs_dop(hsRes.curbankaccount.actsaldodate?:new Date()-365,hsRes.curbankaccount.id,3,2,4,1).sum{it.summa}?:0) - (Payrequest.findAllByPaydateGreaterThanEqualsAndBankaccount_idAndPaytypeAndModstatusGreaterThanAndPaycatAndIs_dop(hsRes.curbankaccount.actsaldodate?:new Date()-365,hsRes.curbankaccount.id,3,2,4,1).sum{it.summa}?:0) - (Taskpay.findAllByBankaccount_idAndTaskpaystatusInListAndIs_accept(hsRes.curbankaccount.id,[0,1,3,5],1).sum{it.summa}?:0)
    hsRes.accsaldo = Taskpay.findAllByBankaccount_idAndTaskpaystatusInListAndIdNotEqual(hsRes.curbankaccount.id,[-1,0,1,3,5],hsRes.taskpay.id).sum{it.summa}?:0.0g
    hsRes.compsaldo = Taskpay.findAllByCompany_idAndBankaccount_idAndTaskpaystatusInListAndIdNotEqual(hsRes.taskpay.company_id,0,[-1,0,1,3,5],hsRes.taskpay.id).sum{it.summa}?:0.0g
    hsRes.totalsaldo = hsRes.cursaldo - hsRes.taskpay.summa

    return hsRes
  }

  def payrequesttasklist = {
    checkAccess(9)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    hsRes.taskpay = Taskpay.get(requestService.getIntDef('id',0))
    if (!hsRes.taskpay) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payrequests = Payrequest.findAllByTaskpay_idAndPaytypeInList(hsRes.taskpay.id,[1,3,8],[sort:'paydate',order:'desc'])
    hsRes.taxes = Tax.list().inject([:]){map, tax -> map[tax.id]=tax.shortname;map}
    hsRes.iscanedit = recieveSectionPermission(TPPEXEC)
    hsRes.iscandelete = recieveSectionPermission(TTPAYNEW)

    return hsRes
  }

  def payrequest = {
    checkAccess(9)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    hsRes.taskpay = Taskpay.get(requestService.getIntDef('taskpay_id',0))
    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (!hsRes.taskpay||!hsRes.payrequest) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.tobanks = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(hsRes.payrequest.tocompany_id?:0,1,1).collect{ Bank.get(it.bank_id) }
    hsRes.frombank = Bank.get(Bankaccount.get(hsRes.payrequest?.bankaccount_id?:0)?.bank_id?:'')
    hsRes.iscanedit = recieveSectionPermission([TCLACCEPT,TPACCEPT])&&hsRes.payrequest.modstatus==1

    return hsRes
  }

  def payrequestbanklist = {
    checkAccess(9)
    requestService.init(this)

    return [banks:Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(requestService.getIntDef('company_id',0),1,1).collect{Bank.get(it.bank_id)}]
  }

  def updatetaskpayrequest = {
    checkAccess(9)
    if (!checkSectionPermission([TCLACCEPT,TPACCEPT])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9
    hsRes.result=[errorcode:[]]
 
    hsRes.taskpay = Taskpay.get(requestService.getIntDef('taskpay_id',0))
    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (!hsRes.taskpay||!hsRes.payrequest||hsRes.payrequest.modstatus!=1) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['tocompany_id'],null,['tobank','destination'],null,['summa'])

    if(!hsRes.inrequest.tocompany_id)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.tobank)
      hsRes.result.errorcode<<2
    else if(!Bankaccount.findByBank_idAndCompany_idAndModstatusAndTypeaccount_id(hsRes.inrequest.tobank,hsRes.inrequest.tocompany_id,1,1))
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.destination)
      hsRes.result.errorcode<<5

    if(!hsRes.result.errorcode){
      try {
        hsRes.payrequest.updateTocompany(hsRes.inrequest).csiSetDestination(hsRes.inrequest.destination).csiSetSumma(hsRes.inrequest.summa).save(failOnError:true,flush:true)
        hsRes.taskpay.recomputeSummas().save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Task/updatetaskpayrequest\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }

  def setPayrequestModstatus = {
    checkAccess(9)
    if (!checkSectionPermission(TPPEXEC)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    hsRes.taskpay = Taskpay.get(requestService.getIntDef('taskpay_id',0))
    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (!hsRes.taskpay||!hsRes.payrequest||hsRes.taskpay?.executor!=hsRes.user.id) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    def iStatus = requestService.getIntDef('status',0)

    try {
      if (iStatus>0) hsRes.payrequest.csiSetBankaccount_id(iStatus==2?hsRes.taskpay.bankaccount_id:0).csiSetModstatus(iStatus).save(flush:true,failOnError:true)
      hsRes.taskpay.computeStatus().save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Task/setPayrequestModstatus\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false,needReload:hsRes.taskpay.isDirty('taskpaystatus')?1:0]}
    return
  }

  def removefromtask = {
    checkAccess(9)
    if (!checkSectionPermission(TTPAYNEW)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    hsRes.taskpay = Taskpay.get(requestService.getIntDef('taskpay_id',0))
    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (!hsRes.taskpay||!hsRes.payrequest||!(hsRes.taskpay.taskpaystatus in [0,1,3,5])) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.payrequest.csiSetBankaccount_id(0).csiSetExecdate(null).csiSetModstatus(0).csiSetTaskpay_id(0).save(flush:true,failOnError:true)
      hsRes.taskpay.recomputeSummas().computeStatus().save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Task/removefromtask\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def splitpayrequest = {
    checkAccess(9)
    if (!checkSectionPermission(TTPAYNEW)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9
    hsRes.result=[errorcode:[]]

    hsRes.taskpay = Taskpay.get(requestService.getIntDef('taskpay_id',0))
    hsRes.payrequest = Payrequest.get(requestService.getIntDef('id',0))
    if (!hsRes.taskpay||!hsRes.payrequest||hsRes.taskpay.is_accept!=0||hsRes.payrequest.taskpay_id!=hsRes.taskpay.id) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(null,null,null,null,['summa'])

    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<1
    else if(hsRes.inrequest.summa<0)
      hsRes.result.errorcode<<2
    else if(hsRes.inrequest.summa>=hsRes.payrequest.summa)
      hsRes.result.errorcode<<2

    if(!hsRes.result.errorcode){
      try {
        hsRes.payrequest.cloneRequest().correctSummas(hsRes.inrequest.summa).save(failOnError:true)
        hsRes.payrequest.correctSummas(hsRes.payrequest.summa-hsRes.inrequest.summa).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Task/splitpayrequest\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }

  def taskaddprequests = {
    checkAccess(9)
    if (!checkSectionPermission(TTPAYNEW)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    hsRes.taskpay = Taskpay.get(requestService.getIntDef('id',0))
    if (!hsRes.taskpay) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.payrequests = Payrequest.findAllByTaskpay_idAndPaytypeInListAndFromcompany_idAndPaygroupAndModstatusInList(0,[1,3,8],hsRes.taskpay.company_id,hsRes.taskpay.paygroup,[0,1],[sort:'paydate',order:'desc'])
    hsRes.taxes = Tax.list().inject([:]){map, tax -> map[tax.id]=tax.shortname;map}

    return hsRes
  }

  def extendtask = {
    checkAccess(9)
    if (!checkSectionPermission(TTPAYNEW)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    hsRes.taskpay = Taskpay.get(requestService.getIntDef('id',0))
    def payrequestIds = requestService.getIntIds('payrequestids')
    if (hsRes.taskpay?.taskpaystatus!=0||!payrequestIds) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      Payrequest.findAllByIdInListAndTaskpay_idAndPaytypeInListAndFromcompany_idAndModstatusInList(payrequestIds,0,[1,3,8],hsRes.taskpay.company_id,[0,1]).each{ prequest ->
        prequest.csiSetTaskpay_id(hsRes.taskpay.id).csiSetModstatus(1).save(flush:true)
      }
      hsRes.taskpay.recomputeSummas().save(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Task/extendtask\n"+e.toString());
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def movetonewtask = {
    checkAccess(9)
    if (!checkSectionPermission(TTPAYNEW)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    hsRes.taskpay = Taskpay.get(requestService.getIntDef('id',0))
    def payrequestIds = requestService.getIntIds('payrequestids')
    if (hsRes.taskpay?.taskpaystatus!=0||!payrequestIds) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      payrequestIds.collect{Payrequest.get(it).fromcompany_id}.unique().each{ company_id ->
        (1..4).each{ paygroup ->
          def lsPayreq = Payrequest.findAllByFromcompany_idAndModstatusAndPaytypeInListAndPaygroupAndIdInList(company_id,0,[1,3],paygroup,payrequestIds)
          if(lsPayreq){
            def oTaskpay = new Taskpay(paygroup:paygroup).csiSetTaskpay([term:String.format('%td.%<tm.%<tY',new Date()),company_id:company_id,summa:lsPayreq.sum{it.summa}]).csiSetInitiator(hsRes.user.id).save(flush:true,failOnError:true)
            lsPayreq.each{ it.csiSetTaskpay_id(oTaskpay.id).csiSetModstatus(1).save(flush:true,failOnError:true) }
          }
        }
      }
    } catch(Exception e) {
      log.debug("Error save data in Task/movetonewtask\n"+e.toString());
      render(contentType:"application/json"){[error:true]}
      return
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def paytransfers = {
    checkAccess(9)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    hsRes.taskpay = Taskpay.get(requestService.getIntDef('id',0))
    if (!hsRes.taskpay) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.paytransfers = Payrequest.findAllByTaskpay_idAndPaytypeAndIs_dopAndModstatusGreaterThan(hsRes.taskpay.id,3,1,-1,[sort:'paydate',order:'desc'])
    hsRes.iscanadd = recieveSectionPermission(TPPEXEC)
    hsRes.iscandelete = recieveSectionPermission(TPPEXEC)

    return hsRes
  }

  def paytransfer = {
    checkAccess(9)
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    def lId = requestService.getIntDef('id',0)
    hsRes.taskpay = Taskpay.get(requestService.getIntDef('taskpay_id',0))
    hsRes.paytransfer = Payrequest.get(lId)
    if (!hsRes.taskpay||(!hsRes.paytransfer&&lId)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes.tocompany = Company.get(hsRes.taskpay.company_id)
    hsRes.frombanks = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(hsRes.paytransfer?.fromcompany_id?:0,1,1).collect{Bank.get(it.bank_id)}
    hsRes.frombankbik = Bankaccount.get(hsRes.paytransfer?.bankaccount_id?:0)?.bank_id
    hsRes.tobanks = Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(hsRes.paytransfer?.tocompany_id?:0,1,1).collect{Bank.get(it.bank_id)}
    hsRes.tobankbik = Bankaccount.get(hsRes.paytransfer?.tobankaccount_id?:0)?.bank_id
    hsRes.basecomment = message(code:'payrequest.transfer.basecomment', args:[hsRes.taskpay.id])

    return hsRes
  }

  def paytransferbanklist = {
    checkAccess(9)
    if (!checkSectionPermission(TPPEXEC)) return
    requestService.init(this)

    return [banks:Bankaccount.findAllByCompany_idAndModstatusAndTypeaccount_id(requestService.getIntDef('company_id',0),1,1).collect{Bank.get(it.bank_id)},field_id:requestService.getStr('fieldId')]
  }

  def addpaytransfer = {
    checkAccess(9)
    if (!checkSectionPermission(TPPEXEC)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9
    hsRes.result=[errorcode:[]]

    hsRes.taskpay = Taskpay.get(requestService.getIntDef('taskpay_id',0))
    if (!hsRes.taskpay) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['fromcompany_id','is_nds','tocompany_id'],null,['frombank','tobank','destination'],null,['summa'])
    hsRes.inrequest.paydate = requestService.getDate('paytransfer_paydate')?.format('dd.MM.yyyy')

    if(!hsRes.inrequest.paydate)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.fromcompany_id)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.frombank)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.summa)
      hsRes.result.errorcode<<4
    if(!hsRes.inrequest.tocompany_id)
      hsRes.result.errorcode<<5
    if(!hsRes.inrequest.tobank)
      hsRes.result.errorcode<<6
    hsRes.basecomment = message(code:'payrequest.transfer.basecomment', args:[hsRes.taskpay.id])

    if(!hsRes.result.errorcode){
      try {
        new Payrequest(is_dop:1).csiSetPaytransfer([paycat:4,paytype:3,summa:hsRes.inrequest.summa,is_nds:hsRes.inrequest.is_nds,paydate:hsRes.inrequest.paydate,destination:hsRes.inrequest.destination,fromcompany_id:hsRes.inrequest.fromcompany_id,frombank:hsRes.inrequest.frombank,tocompany_id:hsRes.inrequest.tocompany_id,tobank:hsRes.inrequest.tobank],hsRes.taskpay,hsRes.basecomment).csiSetInitiator(hsRes.user.id).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Task/addpaytransfer\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }

  def deletepaytransfer = {
    checkAccess(9)
    if (!checkSectionPermission(TPPEXEC)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    hsRes.taskpay = Taskpay.get(requestService.getIntDef('taskpay_id',0))
    hsRes.paytransfer = Payrequest.get(requestService.getIntDef('id',0))
    if (hsRes.taskpay?.taskpaystatus!=1||hsRes.paytransfer?.modstatus!=1) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.paytransfer.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Task/deletepaytransfer\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  def report = {
    checkAccess(9)  
    requestService.init(this)
    def hsRes=[:]  
    hsRes.user = session.user   
    
    def lId=requestService.getIntDef('id',0)      

    hsRes.taskpay = Taskpay.get(lId)
    if (!hsRes.taskpay) {
      response.sendError(404)
      return
    } 
    
    hsRes.company=Company.get(hsRes.taskpay?.company_id?:0)
    
    if(hsRes.company){            
      if(hsRes.taskpay?.bankaccount_id){        
        hsRes.bankaccount=Bankaccount.findByIdAndModstatus(hsRes.taskpay?.bankaccount_id,1)      
      
        if(hsRes.bankaccount)
          hsRes.bank=Bank.get(hsRes.bankaccount.bank_id)               
      }    
    } 
    
    hsRes.taskpaystatus=Taskpaystatus.get(hsRes.taskpay.taskpaystatus)

    hsRes.payrequest = Payrequest.findAllByTaskpay_idAndPaytype(lId,1)       
    
    renderPdf(template:'report',model:hsRes,filename:'taskpay_'+hsRes.taskpay.id+'_card.pdf')
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////taskpay <<<///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Enquiry >>>///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def enquiryfilter = {
    checkAccess(9)
    if (!checkSectionPermission(TENQ)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    if (session.tasklastRequest?.fromDetails){
      hsRes.inrequest = session.tasklastRequest
    }
    hsRes.iscanadd = recieveSectionPermission(TENQEDIT)
    hsRes.banks = Enquiry.getBanks()
    hsRes.taxinspections = Enquiry.getTaxinspections()

    return hsRes
  }

  def enquiries = {
    checkAccess(9)
    if (!checkSectionPermission(TENQ)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    if (session.tasklastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.tasklastRequest
      session.tasklastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['whereto','modstatus','is_table'],null,['company_name','bank_id','taxinspection_id'])
      hsRes.inrequest.inputdate_start = requestService.getDate('inputdate_start')
      hsRes.inrequest.inputdate_end = requestService.getDate('inputdate_end')
      hsRes.inrequest.termdate = requestService.getDate('termdate')
      hsRes.inrequest.ondate = requestService.getDate('ondate')
      hsRes.inrequest.offset = requestService.getOffset()
      session.tasklastRequest = hsRes.inrequest
    }
    session.tasklastRequest.taskobject = 4

    hsRes.searchresult = new EnquirySearch().csiSelectEnqueries(hsRes.inrequest.company_name?:'',hsRes.inrequest.bank_id?:'',
                                                                hsRes.inrequest.taxinspection_id?:'',hsRes.inrequest.whereto?:0,
                                                                hsRes.inrequest.modstatus?:0,hsRes.inrequest.inputdate_start,
                                                                hsRes.inrequest.inputdate_end,hsRes.inrequest.termdate,
                                                                hsRes.inrequest.ondate,hsRes.inrequest.is_table?20:-1,hsRes.inrequest.offset)
    hsRes.enqtypes = Enqtype.list().inject([:]){map, type -> map[type.id]=type.name;map}

    if (hsRes.inrequest.is_table) return hsRes

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
        fillRow(['Дата запроса','Компания','Куда','Тип справки','На дату','Дата получения','Статус'],3,false)
        hsRes.searchresult.records.each{ record ->
          fillRow([String.format('%td.%<tm.%<tY',record.startdate?:record.inputdate),
                   record.company_name,
                   record.bank_name?record.bank_name:record.inspection_district?record.inspection_district+' '+record.inspection_name:record.inspection_name,
                   hsRes.enqtypes[record.enqtype_id],
                   String.format('%td.%<tm.%<tY',record.ondate),
                   String.format('%td.%<tm.%<tY',record.enddate?:record.termdate),
                   !record.modstatus?'Новая':record.modstatus==1?'Заявка принята':record.modstatus==2?'Справка выдана':record.modstatus==3?'Требуется перезапрос':'Отказ'
                  ], rowCounter++, false)
        }
        save(response.outputStream)
      }
    }
  }

  def addenquiry = {
    checkAccess(9)
    if (!checkSectionPermission(TENQEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    hsRes.taxinspections = Company.findAllByModstatusAndIs_holding(1,1).collect{it.taxinspection_id}.unique().collect{Taxinspection.get(it)}

    return hsRes
  }

  def enquirybanklist={
    checkAccess(9)
    requestService.init(this)

    def sCompany = requestService.getStr('company')
    def sBank = requestService.getStr('bank')
    def lsAccounts = sCompany?Bankaccount.findAllByCompany_idAndModstatus(Company.findByNameOrInn(sCompany,sCompany)?.id,1):Bankaccount.findAllByModstatus(1)
    return [banks:lsAccounts.collect{it.bank_id}.unique().collect{Bank.get(it)}.sort{it.name},selectedBank:sBank,valutas:lsAccounts.findAll{it.bank_id==sBank}.collect{it.valuta_id}.unique()]
  }

  def incertenquiry = {
    checkAccess(9)
    if (!checkSectionPermission(TENQEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9
    hsRes.result=[errorcode:[]]

    hsRes+=requestService.getParams(['enqtype_id','accounttype','valuta_id'],null,['company','endetails','bank_id','taxinspection_id'])
    hsRes.inrequest.ondate = requestService.getDate('ondate')

    if(hsRes.inrequest.company&&!Company.findByNameOrInn(hsRes.inrequest.company,hsRes.inrequest.company))
      hsRes.result.errorcode<<2
    else if(hsRes.inrequest.company&&Company.findAllByNameOrInn(hsRes.inrequest.company,hsRes.inrequest.company).size()>1)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.enqtype_id)
      hsRes.result.errorcode<<4
    else if(!Enqtype.get(hsRes.inrequest.enqtype_id))
      hsRes.result.errorcode<<5
    else if(Enqtype.get(hsRes.inrequest.enqtype_id).type==2){
      if (!hsRes.inrequest.company&&!hsRes.inrequest.bank_id)
        hsRes.result.errorcode<<6
      if (hsRes.inrequest.company&&hsRes.inrequest.bank_id&&!Bankaccount.findByCompany_idAndModstatus(Company.findByNameOrInn(hsRes.inrequest.company,hsRes.inrequest.company)?.id?:0,1))
        hsRes.result.errorcode<<7
      if(hsRes.inrequest.accounttype==2&&!hsRes.inrequest.valuta_id)
        hsRes.result.errorcode<<8
    } else if(Enqtype.get(hsRes.inrequest.enqtype_id).type==1){
      if(!hsRes.inrequest.company&&!hsRes.inrequest.taxinspection_id)
        hsRes.result.errorcode<<1
    }
    if(!hsRes.inrequest.ondate)
      hsRes.result.errorcode<<9

    if(!hsRes.result.errorcode){
      try {
        if (Enqtype.get(hsRes.inrequest.enqtype_id).type==2&&!hsRes.inrequest.bank_id)
          Bankaccount.findAllByCompany_idAndModstatus(Company.findByNameOrInn(hsRes.inrequest.company,hsRes.inrequest.company)?.id,1).collect{it.bank_id}.unique().each{
            new Enquiry(company_id:Company.findByNameOrInn(hsRes.inrequest.company,hsRes.inrequest.company)?.id,enqtype_id:hsRes.inrequest.enqtype_id,whereto:2).setData(hsRes.inrequest+[bank_id:it]).csiSetAdmin(session.user.id).save(failOnError:true)?.id?:0
          }
        else if (Enqtype.get(hsRes.inrequest.enqtype_id).type==2&&!hsRes.inrequest.company&&hsRes.inrequest.bank_id)
          Bankaccount.findAllByBank_idAndModstatus(hsRes.inrequest.bank_id,1).collect{it.company_id}.unique().each{
            new Enquiry(company_id:it,enqtype_id:hsRes.inrequest.enqtype_id,whereto:2).setData(hsRes.inrequest).csiSetAdmin(session.user.id).save(failOnError:true)?.id?:0
          }
        else if (Enqtype.get(hsRes.inrequest.enqtype_id).type==1&&!hsRes.inrequest.company&&hsRes.inrequest.taxinspection_id)
          Company.findAllByTaxinspection_idAndModstatusAndIs_holding(hsRes.inrequest.taxinspection_id,1,1).each{
            new Enquiry(company_id:it.id,enqtype_id:hsRes.inrequest.enqtype_id,whereto:1).setData(hsRes.inrequest).csiSetAdmin(session.user.id).save(failOnError:true)?.id?:0
          }
        else
          hsRes.result.enquiry = new Enquiry(company_id:Company.findByNameOrInn(hsRes.inrequest.company,hsRes.inrequest.company)?.id,enqtype_id:hsRes.inrequest.enqtype_id,whereto:Enqtype.get(hsRes.inrequest.enqtype_id).type).setData(hsRes.inrequest).csiSetAdmin(session.user.id).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Task/updatespace\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def enquiry = {
    checkAccess(9)
    if (!checkSectionPermission(TENQ)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    hsRes.enquiry = Enquiry.get(requestService.getIntDef('id',0))
    if (!hsRes.enquiry) {
      response.sendError(404)
      return
    }

    hsRes.company = Company.get(hsRes.enquiry.company_id)
    hsRes.initiator = User.get(hsRes.enquiry.admin_id)?.name
    hsRes.bank = Bank.get(hsRes.enquiry.bank_id)
    hsRes.taxinspection = Taxinspection.get(hsRes.enquiry.taxinspection_id)
    hsRes.valutas = Bankaccount.findAllByCompany_idAndModstatus(hsRes.enquiry.company_id,1).findAll{it.bank_id==hsRes.enquiry.bank_id}.collect{it.valuta_id}.unique()
    hsRes.iscanedit = recieveSectionPermission(TENQEDIT)

    return hsRes
  }

  def updateenquiry = {
    checkAccess(9)
    if (!checkSectionPermission(TENQEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9
    hsRes.result=[errorcode:[]]

    hsRes.enquiry = Enquiry.get(requestService.getIntDef('id',0))
    if (!hsRes.enquiry) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['accounttype','valuta_id','modstatus'],null,['endetails','comment'])
    hsRes.inrequest.termdate = requestService.getDate('termdate')
    hsRes.inrequest.enddate = requestService.getDate('enddate')
    hsRes.inrequest.ondate = requestService.getDate('ondate')

    if(hsRes.inrequest.accounttype==2&&!hsRes.inrequest.valuta_id)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.termdate)
      hsRes.result.errorcode<<2
    if(hsRes.inrequest.modstatus==-1&&!hsRes.inrequest.comment)
      hsRes.result.errorcode<<3
    if(!hsRes.inrequest.ondate)
      hsRes.result.errorcode<<4

    if(!hsRes.result.errorcode){
      try {
        hsRes.enquiry.updateData(hsRes.inrequest).csiSetAdmin(session.user.id).csiSetModstatus(hsRes.inrequest.modstatus).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Task/updateenquiry\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }

  def deleteenquiry = {
    checkAccess(9)
    if (!checkSectionPermission(TENQEDIT)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    hsRes.enquiry = Enquiry.get(requestService.getIntDef('id',0))
    if (hsRes.enquiry.modstatus!=0) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    try {
      hsRes.enquiry.delete(failOnError:true)
    } catch(Exception e) {
      log.debug("Error save data in Task/deleteenquiry\n"+e.toString())
    }

    render(contentType:"application/json"){[error:false]}
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Enquiry <<<///////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Spaceprolong >>>//////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////

  def spaceprolongfilter = {
    checkAccess(9)
    if (!checkSectionPermission(TSPACE)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    if (session.tasklastRequest?.fromDetails){
      hsRes.inrequest = session.tasklastRequest
    }

    return hsRes
  }

  def spaceprolongs = {
    checkAccess(9)
    if (!checkSectionPermission(TSPACE)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    if (session.tasklastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.tasklastRequest
      session.tasklastRequest.fromDetails = 0
    } else {
      hsRes.inrequest = [:]
      hsRes.inrequest.permitstatus = requestService.getIntDef('permitstatus',0)
      hsRes.inrequest.offset = requestService.getOffset()
      session.tasklastRequest = hsRes.inrequest
    }
    session.tasklastRequest.taskobject = 5

    hsRes.searchresult = new SpaceSearch().csiSelectSpaceProlongs(hsRes.inrequest,20,hsRes.inrequest.offset)
    hsRes.spacetypes = Spacetype.list().inject([:]){map, spacetype -> map[spacetype.id]=spacetype.name;map}
    hsRes.today = new Date()

    return hsRes
  }

  def spaceprolong = {
    checkAccess(9)
    if (!checkSectionPermission(TSPACE)) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary()
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9

    hsRes.spaceprolong = Space.get(requestService.getIntDef('id',0))
    if (!hsRes.spaceprolong) {
      response.sendError(404)
      return
    }

    hsRes.spacetypes = Spacetype.list()
    hsRes.arendator = Company.get(hsRes.spaceprolong.arendator)
    hsRes.arendodatel = Company.get(hsRes.spaceprolong.arendodatel)
    hsRes.workuser = Pers.get(User.get(hsRes.spaceprolong.workuser)?.pers_id?:0)
    hsRes.iscanedit = recieveSectionPermission([TSPPERM,TSPWORK])
    hsRes.iscanpermit = recieveSectionPermission(TSPPERM)
    hsRes.iscanwork = recieveSectionPermission(TSPWORK)

    return hsRes
  }

  def updatespaceprolong = {
    checkAccess(9)
    if (!checkSectionPermission([TSPPERM,TSPWORK])) return
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(true)
    hsRes.user = User.get(session.user.id)
    hsRes.action_id = 9
    hsRes.result=[errorcode:[]]

    hsRes.spaceprolong = Space.get(requestService.getIntDef('id',0))
    if (!hsRes.spaceprolong) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    hsRes+=requestService.getParams(['permitstatus','workstatus'],null,['prolongcomment'])
    if (!recieveSectionPermission(TSPPERM)) hsRes.inrequest.permitstatus = hsRes.spaceprolong.permitstatus
    if (!recieveSectionPermission(TSPWORK)) hsRes.inrequest.workstatus = hsRes.spaceprolong.workstatus

    if(!hsRes.result.errorcode){
      try {
        hsRes.spaceprolong.updateprolongData(hsRes.inrequest).csiSetPermitstatus(hsRes.inrequest.permitstatus?:0).csiSetWorkstatus(hsRes.inrequest.workstatus?:0,session.user.id).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Task/updatespaceprolong\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  //////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Spaceprolong <<<//////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////
}