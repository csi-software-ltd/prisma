class Compershist {
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
  Date inputdate = new Date()
  Integer salary
  Long admin_id
  String comment

  Compershist setData(_prop){
    properties = _prop
    this
  }

}