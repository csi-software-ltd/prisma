class Indepositdopagr {
  static mapping = { version false }
  static constraints = {
    enddate(nullable:true)
  }

  Integer id
  Integer indeposit_id
  Integer atype
  String nomer
  Date dsdate
  Date startdate
  Date enddate
  BigDecimal summa
  BigDecimal rate
  BigDecimal comrate
  String comment

  def transient admin_id = 0

  def afterInsert(){
    Indepositdopagr.withNewSession{
      if (id!=getMinId(indeposit_id)){
        Indeposit.get(indeposit_id).csiSetAdmin(admin_id).fillFrom(this).save(failOnError:true,flush:true)
      }
    }
  }

  def beforeUpdate(){
    Indepositdopagr.withNewSession{
      if(id==getMaxId(indeposit_id))
        Indeposit.get(indeposit_id).csiSetAdmin(admin_id).fillFrom(this).save(failOnError:true,flush:true)
    }
  }

  def afterDelete(){
    Indepositdopagr.withNewSession{
      Indeposit.get(indeposit_id).csiSetAdmin(admin_id).fillFrom(Indepositdopagr.findByIndeposit_idAndIdNotEqual(indeposit_id,id,[sort:'id',order:'desc'])).save(failOnError:true,flush:true)
    }
  }

  Indepositdopagr setData(_request){
    atype = _request.atype?:0
    nomer = _request.nomer?:''
    dsdate = _request.dsdate
    startdate = _request.startdate
    enddate = atype?_request.enddate:null
    summa = _request.summa
    rate = _request.rate
    comrate = _request.comrate?:0
    comment = _request.comment?:''
    this
  }

  Indepositdopagr fillFrom(Indeposit _deposit){
    atype = _deposit.atype
    nomer = _deposit.anumber
    dsdate = _deposit.adate
    startdate = _deposit.adate
    enddate = _deposit.enddate
    summa = _deposit.summa
    rate = _deposit.rate
    comrate = _deposit.comrate?:0
    comment = 'Основной договор'
    this
  }

  Indepositdopagr csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  static Integer getMinId(_indeposit_id){
    return Indepositdopagr.findByIndeposit_id(_indeposit_id,[sort:'id',order:'asc'])?.id?:0
  }

  static Integer getMaxId(_indeposit_id){
    return Indepositdopagr.findByIndeposit_id(_indeposit_id,[sort:'id',order:'desc'])?.id?:0
  }
}