class Department {
  static mapping = {
    version false
  }

  Integer id
  String name
  String shortname = ''
  Integer depgroup_id = 0
  Integer project_id
  Long cashsaldo = 0
  Integer is_extra = 0
  Integer is_tehdir = 0
  Integer parent = 0
  Integer is_dep = 0
  Integer is_cashextstaff = 0

  def csiSetDepartment(hsInrequest){
    name = hsInrequest?.name?:''
    shortname = hsInrequest?.shortname?:''
    is_dep = hsInrequest?.is_dep?:0
    is_cashextstaff = hsInrequest?.is_cashextstaff?:0
    parent = hsInrequest?.parent?:0    
    project_id = hsInrequest?.project_id?:0    
    this
  }

  Department changeSaldo(lSaldo){
    cashsaldo += lSaldo
    this
  }

}
