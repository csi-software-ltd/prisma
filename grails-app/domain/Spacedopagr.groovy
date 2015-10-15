class Spacedopagr {
  static mapping = { version false }

  Integer id
  Integer space_id
  String anumber
  Date adate
  Date startdate
  Date enddate
  Integer payterm
  Double ratemeter
  BigDecimal rate
  Integer is_addpayment
  BigDecimal ratedop
  Integer is_changeprice

  Spacedopagr setData(_request){
    anumber = _request.anumber
    adate = _request.adate
    startdate = _request.startdate
    enddate = _request.enddate
    payterm = _request.payterm
    ratemeter = _request.ratemeter?_request.ratemeter.toDouble():0d
    rate = _request.rate?:0.0g
    is_addpayment = _request.is_addpayment?:0
    ratedop = !is_addpayment?0.0g:_request.ratedop?:0.0g
    is_changeprice = _request.is_changeprice?:0
    this
  }
}