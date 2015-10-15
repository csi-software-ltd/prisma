class Compers {
  static mapping = {
    version false
  }
  static constraints = {
    jobend(nullable:true)
    gd_valid(nullable:true)
  }

  Integer id
  Long pers_id
  Integer company_id
  Integer position_id
  Integer composition_id
  Date jobstart
  Date jobend
  Date gd_valid
  Integer modstatus
  Integer salary = 0
  String industrywork
  String prevwork
  String comment
  Integer tagproject = 0
  Integer tagclient = 0
  Integer tagexpensemain = 0
  Integer tagexpenseadd = 0
  String tagcomment = ''

  def transient adm_id

  def afterInsert(){
    new Compershist(admin_id:adm_id).setData(properties).save(failOnError:true)
    if(position_id==1&&modstatus==1) Company.get(company_id).csiSetGd(Pers.get(pers_id)?.fullname?:'').save(failOnError:true)
  }

  def beforeUpdate(){
    if(this.isDirty()) new Compershist(admin_id:adm_id).setData(properties).save(failOnError:true)
    if(isDirty('modstatus')&&position_id==1) {
      Company.withNewSession {
        Company.get(company_id).csiSetGd((modstatus==0?'':(Pers.get(pers_id)?.fullname?:''))).save(failOnError:true,flush:true)
      }
    }
  }

  String toString(){
    "${Pers.get(pers_id)?.shortname} - ${String.format('%td.%<tm.%<tY',jobstart)}"
  }

  Compers setData(_request,_accesslevel=0){
    composition_id = _request.composition_id
    position_id = Composition.get(composition_id)?.position_id
    jobstart = _request.employee_jobstart
    jobend = _request.employee_jobend?:null
    gd_valid = jobend?:_request.employee_gd_valid?:position_id==1?_request.employee_jobstart+365*(_request.gd_valid_years?:0):null
    salary = _accesslevel<2?salary:_request.salary?:0
    comment = _request.comment?:''
    industrywork = position_id!=1?'':_request.industrywork?:''
    prevwork = position_id!=1?'':_request.prevwork?:''
    this
  }

  Compers updateModstatus(){
    modstatus = jobend?0:1
    this
  }

  Compers csiSetAdmin(iAdmin){
    adm_id = iAdmin?:0
    this
  }

  Compers csiSetTagData(_request){
    tagproject = _request.tagproject?:tagproject
    tagclient = _request.tagclient?:tagclient
    tagexpensemain = position_id==3?0:_request.tagexpensemain?:Tools.getIntVal(Dynconfig.findByName('compers.tags.default.expensemain.value')?.value,0)
    tagexpenseadd = position_id==4?0:_request.tagexpenseadd?:Tools.getIntVal(Dynconfig.findByName('compers.tags.default.expenseadd.value')?.value,0)
    tagcomment = _request.tagcomment?:tagcomment
    this
  }

}
