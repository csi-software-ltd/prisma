class Cashrequesthist {
  static mapping = {
    version false
  }

  Integer id
  Integer cashrequest_id
  Date reqdate
  Integer modstatus
  Integer summa
  Float margin
  String comment
  Long admin_id
  Date inputdate

  Cashrequesthist setData(_prop){
    properties = _prop
    inputdate = new Date()
    this
  }

}