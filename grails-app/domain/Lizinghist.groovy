class Lizinghist {
  static mapping = {
    version false
  }

  Integer id
  Integer lizing_id
  String anumber
  Date adate
  Date enddate
  String comment
  BigDecimal summa
  BigDecimal initialfee
  Integer rate
  Long responsible
  Date inputdate
  Long admin_id

  Lizinghist setData(_prop){
    properties = _prop
    inputdate = new Date()
    this
  }

}