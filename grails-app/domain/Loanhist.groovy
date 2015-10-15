class Loanhist {
  static mapping = { version false }

  Integer id
  Integer loan_id
  Integer loanclass
  Long summa
  Double rate
  Date enddate
  String comment
  Date inputdate = new Date()
  Long admin_id

  Loanhist setData(_prop){
    properties = _prop
    inputdate = new Date()
    this
  }

}