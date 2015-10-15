class Finlizinghist {
  static mapping = { version false }

  Integer id
  Integer finlizing_id
  String anumber
  Date adate
  Date enddate
  BigDecimal summa
  BigDecimal rate
  Integer modstatus
  String description
  String comment
  Long responsible
  Date inputdate = new Date()
  Long admin_id

  Finlizinghist setData(_prop){
    properties = _prop
    inputdate = new Date()
    this
  }

}