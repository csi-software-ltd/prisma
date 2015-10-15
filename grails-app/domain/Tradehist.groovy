class Tradehist {
  static mapping = {
    version false
  }
  static constraints = {
    enddate(nullable:true)
  }

  Integer id
  Integer trade_id
  Integer tradecat_id
  String anumber
  Date adate
  Date enddate
  String comment
  Integer summa
  Integer paytype
  Long responsible
  Date inputdate
  Long admin_id

  Tradehist setData(_prop){
    properties = _prop
    inputdate = new Date()
    this
  }

}