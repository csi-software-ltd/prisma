class Bankdeposit {
  static mapping = { version false }
  static constraints = {
    enddate(nullable:true)
    startsaldodate(nullable:true)
  }
  private enum Historyfields {
    DTYPE, MODSTATUS, ENDDATE, SUMMA, RATE, TERM, COMMENT
  }

  Integer id
  Integer bank
  Integer dtype
  Integer modstatus
  Date inputdate = new Date()
  String anumber
  Date adate
  Date enddate
  BigDecimal summa
  BigDecimal rate
  Integer valuta_id
  Integer term
  BigDecimal startsumma
  BigDecimal startprocent
  Date startsaldodate
  String comment

  def transient admin_id

  def afterInsert(){
    new Bankdeposithist(bankdeposit_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  def beforeUpdate(){
    if(isHaveDirty()) new Bankdeposithist(bankdeposit_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  String toString(){
    "$anumber от ${String.format('%td.%<tm.%<tY',adate)}${!modstatus?' (архивный)':''}"
  }

  static def getBanks(){
    Bankdeposit.list().collect{it.bank}.unique().collect{ Company.get(it) }.sort{ it.name }
  }

  Boolean isHaveDirty (){ return Bankdeposit.Historyfields.values().toList().find{ isDirty(it.toString().toLowerCase())}?true:false }

  Bankdeposit setData(_request){
    dtype = _request.dtype?:0
    anumber = _request.anumber?:''
    adate = _request.adate
    enddate = dtype?_request.enddate:null
    summa = _request.summa
    rate = _request.rate
    valuta_id = _request.valuta_id?:857
    term = _request.term?:1
    startsumma = _request.startsumma?:0.0g
    startprocent = _request.startprocent?:0.0g
    startsaldodate = _request.startsaldodate
    comment = _request.comment?:''
    this
  }

  Bankdeposit csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  Bankdeposit updateModstatus(iStatus){
    modstatus = ((enddate?:new Date())<new Date()-1)?0:iStatus?:0
    this
  }

}