class Bankdeposithist {
  static mapping = { version false }
  static constraints = {
    enddate(nullable:true)
  }

  Integer id
  Integer bankdeposit_id
  Integer dtype
  Integer modstatus
  Date enddate
  BigDecimal summa
  BigDecimal rate
  Integer term
  String comment
  Date inputdate = new Date()
  Long admin_id

  Bankdeposithist setData(_prop){
    properties = _prop
    inputdate = new Date()
    this
  }

}