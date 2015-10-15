class Indeposithist {
  static mapping = { version false }
  static constraints = {
    enddate(nullable:true)
  }

  Integer id
  Integer indeposit_id
  Integer atype
  Integer modstatus
  String anumber
  Date enddate
  BigDecimal summa
  BigDecimal rate
  BigDecimal comrate
  BigDecimal startsaldo
  String comment
  Date inputdate = new Date()
  Long admin_id

  Indeposithist setData(_prop){
    properties = _prop
    inputdate = new Date()
    this
  }

}