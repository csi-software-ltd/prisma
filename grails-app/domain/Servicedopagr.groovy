class Servicedopagr {
  static mapping = { version false }

  Integer id
  Integer service_id
  String nomer
  Date dsdate
  Date startdate
  Date enddate
  Long summa
  String comment

  def transient admin_id = 0

  def afterInsert(){
    Servicedopagr.withNewSession{
      if (id!=getMinId(service_id))
        Service.get(service_id).fillFrom(this).csiSetAdmin(admin_id).save(failOnError:true,flush:true)
    }
  }

  def beforeUpdate(){
    Servicedopagr.withNewSession{
      if(id==getMaxId(service_id))
        Service.get(service_id).fillFrom(this).csiSetAdmin(admin_id).save(failOnError:true,flush:true)
    }
  }

  def afterDelete(){
    Servicedopagr.withNewSession{
      Service.get(service_id).fillFrom(Servicedopagr.findByService_idAndIdNotEqual(service_id,id,[sort:'id',order:'desc'])).csiSetAdmin(admin_id).save(failOnError:true,flush:true)
    }
  }

  Servicedopagr setData(_request){
    nomer = _request.nomer
    dsdate = _request.dsdate
    startdate = _request.startdate
    enddate = _request.enddate
    summa = _request.summa
    comment = _request.comment?:''
    this
  }

  Servicedopagr fillFrom(Service _service){
    nomer = _service.anumber
    dsdate = _service.adate
    startdate = _service.adate
    enddate = _service.enddate
    summa = _service.summa
    comment = 'Основной договор'
    this
  }

  Servicedopagr csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  static Integer getMinId(_service_id){
    return Servicedopagr.findByService_id(_service_id,[sort:'id',order:'asc'])?.id?:0
  }

  static Integer getMaxId(_service_id){
    return Servicedopagr.findByService_id(_service_id,[sort:'id',order:'desc'])?.id?:0
  }
}