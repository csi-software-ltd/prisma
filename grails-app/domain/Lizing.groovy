class Lizing {
  def agentKreditService
  static mapping = { version false }
  static constraints = {
    debtdate(nullable:true)
    startsaldodate(nullable:true)
  }
  private enum Historyfields {
    ANUMBER, ADATE, ENDDATE, COMMENT, SUMMA, INITIALFEE, RATE, RESPONSIBLE
  }

  Integer id
  Integer mainagr_id
  Integer arendator
  Integer arendodatel
  Integer owner = 0
  Integer creditor = 0
  String cbank_id = ''
  Integer lizsort
  String anumber
  Date adate
  Date inputdate = new Date()
  Date enddate
  String description
  Integer modstatus
  Integer cessionstatus = 0
  String comment
  BigDecimal summa
  BigDecimal initialfee
  BigDecimal startsaldo
  Date startsaldodate
  BigDecimal restfee = 0
  Integer rate = 0
  Integer debt = 0
  Date debtdate
  Integer is_dirsalary = 1
  Integer project_id = 0
  Integer car_id = 0
  Long responsible

  def transient admin_id

  def beforeInsert(){
    restfee = startsaldodate?startsaldo:summa
  }

  def afterInsert(){
    new Lizinghist(lizing_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
    new Lizingdopagr(lizing_id:id).fillFrom(this).save(failOnError:true)
  }

  def beforeUpdate(){
    if (modstatus==0) restfee = 0
    else updateDebt(agentKreditService.computeLizingDebt(this))
    if(isHaveDirty()) new Lizinghist(lizing_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  String toString(){
    "$anumber от ${String.format('%td.%<tm.%<tY',adate)}${!modstatus?' (архивный)':''}"
  }

  String toFullString(){
    "${Company.get(arendodatel)?.name} - $anumber от ${String.format('%td.%<tm.%<tY',adate)} - ${Tools.toFixed(summa,2)}"
  }

  static def getResponsibles(){
    Lizing.list().collect{it.responsible}.unique().inject([:]){map, id -> map[id]=User.get(id)?.name;map}
  }

  Lizing setBaseData(_request){
    enddate = _request.enddate
    summa = _request.summa
    this
  }

  Lizing setData(_request){
    mainagr_id = (!_request.lizsort)?_request.mainagr_id:0
    arendator = Company.findByNameOrInn(_request.arendator,_request.arendator)?.id
    arendodatel = Company.findByNameOrInn(_request.arendodatel,_request.arendodatel)?.id
    lizsort = _request.lizsort?:0
    anumber = _request.anumber
    adate = _request.adate
    description = _request.description?:''
    comment = _request.comment?:''
    initialfee = _request.initialfee?:0.0g
    startsaldo = _request.startsaldo?:0.0g
    startsaldodate = _request.startsaldodate
    project_id = _request.project_id?:0
    car_id = _request.car_id?:0
    responsible = _request.responsible?:0l
    this
  }

  Lizing fillFrom(Lizingdopagr _dopagr){
    enddate = _dopagr.enddate
    summa = _dopagr.summa
    this
  }

  Lizing updateModstatus(iStatus){
    if(iStatus==-1&&!(Payrequest.findAllByAgreementtype_idAndAgreement_idAndModstatusGreaterThan(4,id,-1)||Lizingplanpayment.findAllByLizing_idAndModstatusGreaterThanEquals(id,0))) modstatus = -1
    else if (iStatus!=-1) modstatus = !iStatus?0:(enddate<new Date().clearTime())?0:1
    this
  }

  Lizing csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  Lizing csiSetDirSalary(iStatus,iAccess){
    is_dirsalary = !iAccess?is_dirsalary:iStatus?:0
    this
  }

  Lizing updateDebt(_summa){
    restfee = _summa?:0.0g
    this
  }

  Lizing csiSetCessionstatus(iStatus){
    cessionstatus = iStatus?:0
    this
  }

  Lizing csiSetCreditor(iCreditor){
    creditor = iCreditor?:0
    this
  }

  Lizing csiSetCbank(sBankId){
    cbank_id = sBankId?:''
    this
  }

  Boolean isHaveDirty (){ return Lizing.Historyfields.values().toList().find{ isDirty(it.toString().toLowerCase())}?true:false }

}