class Lizingdopagr {
  static mapping = { version false }

  Integer id
  Integer lizing_id
  String nomer
  Date dsdate
  Date startdate
  Date enddate
  BigDecimal summa
  String comment

  def transient admin_id = 0

  def afterInsert(){
    Lizingdopagr.withNewSession{
      if (id!=getMinId(lizing_id)){
        Lizing.get(lizing_id).csiSetAdmin(admin_id).fillFrom(this).save(failOnError:true,flush:true)
      }
    }
  }

  def beforeUpdate(){
    Lizingdopagr.withNewSession{
      if(id==getMaxId(lizing_id))
        Lizing.get(lizing_id).csiSetAdmin(admin_id).fillFrom(this).save(failOnError:true,flush:true)
    }
  }

  def afterDelete(){
    Lizingdopagr.withNewSession{
      Lizing.get(lizing_id).csiSetAdmin(admin_id).fillFrom(Lizingdopagr.findByLizing_idAndIdNotEqual(lizing_id,id,[sort:'id',order:'desc'])).save(failOnError:true,flush:true)
    }
  }

  Lizingdopagr setData(_request){
    nomer = _request.nomer
    dsdate = _request.dsdate
    startdate = _request.startdate
    enddate = _request.enddate
    summa = _request.summa
    comment = _request.comment?:''
    this
  }

  Lizingdopagr fillFrom(Lizing _lizing){
    nomer = _lizing.anumber
    dsdate = _lizing.adate
    startdate = _lizing.adate
    enddate = _lizing.enddate
    summa = _lizing.summa
    comment = 'Основной договор'
    this
  }

  Lizingdopagr csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  static Integer getMinId(_lizing_id){
    return Lizingdopagr.findByLizing_id(_lizing_id,[sort:'id',order:'asc'])?.id?:0
  }

  static Integer getMaxId(_lizing_id){
    return Lizingdopagr.findByLizing_id(_lizing_id,[sort:'id',order:'desc'])?.id?:0
  }
}