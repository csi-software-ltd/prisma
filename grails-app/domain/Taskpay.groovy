class Taskpay {
  static mapping = { version false }
  static constraints = {
    moddate(nullable:true)
    acceptdate(nullable:true)
  }

  Integer id
  Date inputdate = new Date()
  Integer taskpaystatus = 0
  Integer paygroup = 0
  Date term
  Date moddate
  Long initiator
  Long executor = 0l
  Integer bankaccount_id = 0
  Integer company_id = 0
  BigDecimal summa
  String description = ''
  String plan = ''
  String comment = ''
  Integer is_accept = 0
  Date acceptdate
  Long acceptoperator = 0
  Integer is_t = 0
  Integer is_client = 0
  Integer is_internal = 0
  Integer is_urgent = 0
  Integer payway = 0
  Integer is_manual = 0
/////////////////////////////////////////////////////////////////
  def beforeInsert(){
    is_t = assertCompany()?1:0
  }

  Taskpay csiSetTaskpay(hsInrequest){
    Date tempterm = Tools.getDate(hsInrequest?.term?:'')
    term = tempterm>new Date().clearTime()?tempterm:new Date()
    company_id = hsInrequest?.company_id?:0
    summa = hsInrequest?.summa?:0g
    this
  }

  Taskpay updatePlanData(_request){
    executor = _request.executor?:0L
    plan = _request.plan?:''
    this
  }

  Taskpay updateTaskpay(_request){
    if (!is_accept&&taskpaystatus!=-1){
      term = Tools.getDate(_request.term?:'')
      if (is_t) company_id = Company.findByNameOrInn(_request.company,_request.company)?.id
      if (assertCompany()) bankaccount_id = 0
      else bankaccount_id = Bankaccount.findByCompany_idAndIdAndModstatusAndTypeaccount_id(company_id,_request.bankaccount_id?:0,1,1)?.id?:0
    }
    if (taskpaystatus==0&&_request.taskpaystatus==1) {
      executor = _request.executor?:0L
    } else if (taskpaystatus==1&&!_request.taskpaystatus) {
      executor = 0L
    }

    description = _request.description?:description
    plan = _request.plan?:plan
    comment = _request.comment?:comment
    payway = _request.payway?:0
    this
  }

  Taskpay csiSetInitiator(lAdmin){
    if (taskpaystatus==0&&!is_accept) initiator = lAdmin
    this
  }

  Taskpay csiSetClient(iStatus){
    is_client = iStatus?:0
    this
  }

  Taskpay csiSetInternal(iStatus){
    is_internal = iStatus?:0
    this
  }

  Taskpay csiSetUrgent(iStatus){
    is_urgent = iStatus?:0
    this
  }

  Taskpay csiSetBankaccountId(iAccount){
    if (!is_accept) bankaccount_id = iAccount?:0
    this
  }

  Taskpay acceptTask(lAdmin){
    if (!is_accept && !assertCompany() && bankaccount_id && lAdmin) {
      is_accept = 1
      acceptoperator = lAdmin
      acceptdate = new Date()
    } else if (is_accept && !lAdmin){
      is_accept = 0
      acceptoperator = 0l
      acceptdate = null
      executor = 0L
    }
    this
  }

  Taskpay csiSetTaskpaystatus(iStatus){
    if (taskpaystatus==0&&iStatus==1&&executor>0) moddate = new Date()
    if (taskpaystatus!=-1&&iStatus==-1) acceptTask(0)
    taskpaystatus = !is_accept?0:iStatus==1&&executor==0?0:iStatus
    this
  }

  Taskpay computeStatus(){
    taskpaystatus = !executor?0:!Payrequest.findAllByTaskpay_idAndPaytypeInListAndModstatusGreaterThan(id,[1,3,8],1)?1:!Payrequest.findAllByTaskpay_idAndPaytypeInListAndModstatus(id,[1,3,8],1)?2:3
    if(isDirty('taskpaystatus')&&getPersistentValue('taskpaystatus')!=3&&taskpaystatus==2) moddate = new Date()
    this
  }

  Taskpay recomputeSummas(){
    summa = (Payrequest.findAllByTaskpay_id(id).sum{it.summa}?:0.0g) - (Payrequest.findAllByTaskpay_idAndPaytypeAndPaycatAndIs_dop(id,3,4,1).sum{it.summa}?:0.0g)
    this
  }

  Boolean assertCompany(){
    Company.get(company_id)?.inn==(Dynconfig.findByName('company.service.inn')?.value?:'000000000000')
  }
}