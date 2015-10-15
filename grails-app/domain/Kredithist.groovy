class Kredithist {
  static mapping = {
    version false
  }

  Integer id
  Integer kredit_id
  Integer kredtype
  Integer is_real
  Integer is_tech
  Integer is_realtech
  BigDecimal summa
  Double rate
  Date enddate
  Integer kreditterm
  Integer is_agr
  String comment
  String dopagrcomment
  Long responsible
  Date inputdate = new Date()
  Long admin_id

  Kredithist setData(_prop){
    properties = _prop
    inputdate = new Date()
    this
  }

}