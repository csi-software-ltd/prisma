class Servicehist {
  static mapping = { version false }

  Integer id
  Integer service_id
  Date inputdate
  Date enddate
  Long summa
  String zbank_id
  String ebank_id
  String description
  String comment
  Long responsible
  Long admin_id

  Servicehist setData(_prop){
    properties = _prop
    inputdate = new Date()
    this
  }

}