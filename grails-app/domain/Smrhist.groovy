class Smrhist {
  static mapping = { version false }

  Integer id
  Integer smr_id
  Date inputdate
  Date enddate
  Integer smrcat_id
  Long summa
  Long responsible
  String comment
  Long admin_id

  Smrhist setData(_prop){
    properties = _prop
    inputdate = new Date()
    this
  }

}