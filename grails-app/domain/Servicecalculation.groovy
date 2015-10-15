class Servicecalculation {
  static mapping = { version false }
  static constraints = {
    schetdate(nullable:true)
  }

  Integer id
  Integer service_id
  Integer month
  Integer year
  Date calcdate
  String schet = ''
  Date schetdate
  BigDecimal summa

  Servicecalculation setBaseData(_request){
    month = _request.month
    year = _request.year
    calcdate = _request.calcdate
    this
  }

  Servicecalculation setData(_request){
    schet = _request.schet?:''
    schetdate = _request.schetdate
    summa = _request.summa?:0.0g
    this
  }
}