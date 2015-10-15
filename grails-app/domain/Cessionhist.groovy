class Cessionhist {
  static mapping = {
    version false
  }
  static constraints = {
  }

  Integer id
  Integer cession_id
  Date enddate
  BigDecimal summa
  String dopagrcomment
  Long responsible
  Date inputdate = new Date()
  Long admin_id

  Cessionhist setData(_prop){
    properties = _prop
    inputdate = new Date()
    this
  }

}