class Cashevent {
  static mapping = {
    version false
  }

  Integer id
  Integer cashzakaz_id
  Integer summa
  Integer casheventtype_id
  Integer cashreport_id
  Date inputdate = new Date()
  String comment
  String comment_dep = ''
  Long admin_id

  Cashevent setData(_prop){
    properties = _prop
    inputdate = new Date()
    this
  }

}