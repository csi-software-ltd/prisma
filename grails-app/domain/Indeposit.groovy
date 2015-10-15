class Indeposit {
  static mapping = { version false }
  static constraints = {
    enddate(nullable:true)
  }
  private enum Historyfields {
    ATYPE, MODSTATUS, ENDDATE, SUMMA, RATE, COMMENT, ANUMBER, STARTSALDO, COMRATE
  }

  Integer id
  Integer client_id
  Integer atype
  Integer aclass
  Integer modstatus
  Date inputdate = new Date()
  String anumber
  Date adate
  Date enddate
  BigDecimal summa
  BigDecimal rate
  BigDecimal comrate
  BigDecimal startsaldo
  Integer valuta_id
  String comment

  def transient admin_id

  def afterInsert(){
    new Indeposithist(indeposit_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
    new Indepositdopagr(indeposit_id:id).fillFrom(this).save(failOnError:true)
  }

  def beforeUpdate(){
    if(isHaveDirty()) new Indeposithist(indeposit_id:id,admin_id:admin_id).setData(properties).save(failOnError:true)
  }

  String toString(){
    "$anumber от ${String.format('%td.%<tm.%<tY',adate)}${!modstatus?' (архивный)':''}"
  }

  Boolean isHaveDirty (){ return Indeposit.Historyfields.values().toList().find{ isDirty(it.toString().toLowerCase())}?true:false }

  Indeposit setBaseData(_request){
    client_id = _request.client_id
    atype = _request.atype?:0
    aclass = _request.aclass
    anumber = _request.anumber?:''
    adate = _request.adate
    enddate = atype?_request.enddate:null
    summa = _request.summa
    rate = _request.rate
    comrate = _request.comrate?:0
    this
  }

  Indeposit setData(_request){
    startsaldo = _request.startsaldo?:0.0g
    valuta_id = _request.valuta_id?:857
    comment = _request.comment?:''
    this
  }

  Indeposit fillFrom(Indepositdopagr _dopagr){
    atype = _dopagr.atype
    enddate = _dopagr.enddate
    summa = _dopagr.summa
    rate = _dopagr.rate
    comrate = _dopagr.comrate?:0
    this
  }

  Indeposit csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  Indeposit updateModstatus(iStatus){
    modstatus = iStatus?:0
    this
  }

}