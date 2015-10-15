class Spacehist {
  static mapping = {
    version false
  }

  Integer id
  Integer space_id
  Integer mainagr_id
  Integer spacetype_id
  Date enddate
  Integer modstatus
  String comment
  Double area
  Integer payterm
  Double ratemeter
  Integer is_nosubrenting
  Integer is_nopayment
  Integer is_addpayment
  Integer project_id
  BigDecimal rate
  Long admin_id
  Long responsible
  Date inputdate

  Spacehist setData(_prop){
    properties = _prop
    inputdate = new Date()
    this
  }

}