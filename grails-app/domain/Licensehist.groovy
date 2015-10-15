class Licensehist {
  static mapping = {
    version false
  }
  static constraints = {
    enddate(nullable:true)
  }

  Integer id
  Integer license_id
  Date inputdate
  String anumber
  Date adate
  Date enddate
  Integer paytype
  Integer entryfee
  Integer alimit
  Integer regfee
  Integer modstatus
  Long admin_id

  Licensehist setData(_prop){
    properties = _prop
    inputdate = new Date()
    this
  }

}