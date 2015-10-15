class Kreditdopagr {
  static mapping = { version false }

  Integer id
  Integer kredit_id
  String nomer
  Date dsdate
  Date startdate
  Date enddate
  BigDecimal summa
  BigDecimal rate
  Integer is_prolong
  String comment

  def transient admin_id = 0

  def afterInsert(){
    Kreditdopagr.withNewSession{
      if (id!=getMinId(kredit_id))
        Kredit.get(kredit_id).fillFrom(this).csiSetAdmin(admin_id).save(failOnError:true,flush:true)
    }
  }

  def beforeUpdate(){
    Kreditdopagr.withNewSession{
      if(id==getMaxId(kredit_id))
        Kredit.get(kredit_id).fillFrom(this).csiSetAdmin(admin_id).save(failOnError:true,flush:true)
    }
  }

  def afterDelete(){
    Kreditdopagr.withNewSession{
      Kredit.get(kredit_id).fillFrom(Kreditdopagr.findByKredit_idAndIdNotEqual(kredit_id,id,[sort:'id',order:'desc'])).csiSetAdmin(admin_id).save(failOnError:true,flush:true)
    }
  }

  Kreditdopagr setData(_request){
    nomer = _request.nomer
    dsdate = _request.dsdate
    startdate = _request.startdate
    enddate = _request.enddate
    summa = _request.summa
    rate = _request.rate?.toDouble()
    is_prolong = _request.is_prolong?:0
    comment = _request.comment?:''
    this
  }

  Kreditdopagr fillFrom(Kredit _kredit){
    nomer = _kredit.anumber
    dsdate = _kredit.adate
    startdate = _kredit.startdate
    enddate = _kredit.enddate
    summa = _kredit.summa
    rate = _kredit.rate
    is_prolong = 0
    comment = 'Основной договор'
    this
  }

  Kreditdopagr csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  static Integer getMinId(_kredit_id){
    return Kreditdopagr.findByKredit_id(_kredit_id,[sort:'id',order:'asc'])?.id?:0
  }

  static Integer getMaxId(_kredit_id){
    return Kreditdopagr.findByKredit_id(_kredit_id,[sort:'id',order:'desc'])?.id?:0
  }
}