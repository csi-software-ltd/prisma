class Finlizingdopagr {
  static mapping = { version false }

  Integer id
  Integer finlizing_id
  Integer flpoluchatel
  String nomer
  Date dsdate
  Date startdate
  Date enddate
  BigDecimal summa
  BigDecimal rate
  String comment

  def transient admin_id = 0

  def afterInsert(){
    Finlizingdopagr.withNewSession{
      if (id!=getMinId(finlizing_id)){
        Finlizing.get(finlizing_id).csiSetAdmin(admin_id).fillFrom(this).save(failOnError:true,flush:true)
      }
    }
  }

  def beforeUpdate(){
    Finlizingdopagr.withNewSession{
      if(id==getMaxId(finlizing_id))
        Finlizing.get(finlizing_id).csiSetAdmin(admin_id).fillFrom(this).save(failOnError:true,flush:true)
    }
  }

  def afterDelete(){
    Finlizingdopagr.withNewSession{
      Finlizing.get(finlizing_id).csiSetAdmin(admin_id).fillFrom(Finlizingdopagr.findByFinlizing_idAndIdNotEqual(finlizing_id,id,[sort:'id',order:'desc'])).save(failOnError:true,flush:true)
    }
  }

  Finlizingdopagr setData(_request){
    flpoluchatel = Company.findByNameOrInn(_request.flpoluchatel,_request.flpoluchatel)?.id
    nomer = _request.nomer
    dsdate = _request.dsdate
    startdate = _request.startdate
    enddate = _request.enddate
    summa = _request.summa
    rate = _request.rate
    comment = _request.comment?:''
    this
  }

  Finlizingdopagr fillFrom(Finlizing _flizing){
    flpoluchatel = _flizing.flpoluchatel
    nomer = _flizing.anumber
    dsdate = _flizing.adate
    startdate = _flizing.adate
    enddate = _flizing.enddate
    summa = _flizing.summa
    rate = _flizing.rate
    comment = 'Основной договор'
    this
  }

  Finlizingdopagr csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  static Integer getMinId(_finlizing_id){
    return Finlizingdopagr.findByFinlizing_id(_finlizing_id,[sort:'id',order:'asc'])?.id?:0
  }

  static Integer getMaxId(_finlizing_id){
    return Finlizingdopagr.findByFinlizing_id(_finlizing_id,[sort:'id',order:'desc'])?.id?:0
  }
}