class Spacecalculation {
  static mapping = { version false }
  static constraints = {
    schetdate(nullable:true)
  }

  Integer id
  Integer space_id
  Integer month
  Integer year
  Date calcdate
  String schet = ''
  Date schetdate
  BigDecimal summa
  Integer is_dop

  Spacecalculation setBaseData(_request){
    month = _request.month
    year = _request.year
    calcdate = _request.calcdate
    this
  }

  Spacecalculation setData(_request){
    schet = _request.schet?:''
    schetdate = _request.schetdate
    summa = _request.summa?:0.0g
    this
  }
}