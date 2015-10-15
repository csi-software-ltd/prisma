class Finlizing {
  def agentKreditService
  static mapping = { version false }
  private enum Historyfields {
    ANUMBER, ADATE, ENDDATE, SUMMA, RATE, MODSTATUS, COMMENT, DESCRIPTION, RESPONSIBLE
  }

  Integer id
  Integer fldatel
  Integer flpoluchatel
  Integer flbank
  Date inputdate = new Date()
  String anumber
  Date adate
  Date enddate
  BigDecimal summa
  BigDecimal rate
  Integer modstatus
  String description
  String comment
  Long responsible

  def transient admin_id = 0

  def afterInsert(){
    new Finlizinghist(finlizing_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
    new Finlizingdopagr(finlizing_id:id).fillFrom(this).save(failOnError:true)
  }

  def beforeUpdate(){
    if(isHaveDirty()) new Finlizinghist(finlizing_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  String toString(){
    "$anumber от ${String.format('%td.%<tm.%<tY',adate)}${!modstatus?' (архивный)':''}"
  }

  Boolean isHaveDirty (){ return Finlizing.Historyfields.values().toList().find{ isDirty(it.toString().toLowerCase())}?true:false }

  Finlizing setBaseData(_request){
    fldatel = Company.findByNameOrInn(_request.fldatel,_request.fldatel)?.id
    flpoluchatel = Company.findByNameOrInn(_request.flpoluchatel,_request.flpoluchatel)?.id
    flbank = Company.findByNameOrInn(_request.flbank,_request.flbank)?.id
    enddate = _request.enddate
    summa = _request.summa
    rate = _request.rate
    this
  }

  Finlizing setData(_request){
    anumber = _request.anumber
    adate = _request.adate
    description = _request.description?:''
    comment = _request.comment?:''
    responsible = _request.responsible?:0l
    this
  }

  Finlizing fillFrom(Finlizingdopagr _dopagr){
    flpoluchatel = _dopagr.flpoluchatel
    enddate = _dopagr.enddate
    summa = _dopagr.summa
    rate = _dopagr.rate
    if ((agentKreditService.computeFinLizingBalance(this).bodydebt*100).toInteger()==0&&enddate<new Date().clearTime()) updateModstatus(0)
    else updateModstatus(1)
    this
  }

  Finlizing csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  Finlizing updateModstatus(iStatus){
    modstatus = iStatus?:0
    this
  }

}