class Finlizperiod {
  static mapping = { version false }

  Integer id
  Integer finlizing_id
  Date fmonth
  Integer qdays = 0
  BigDecimal summa
  BigDecimal compensation
  BigDecimal procent
  BigDecimal body
  BigDecimal returnsumma

  def beforeInsert(){
    def flizing = Finlizing.get(finlizing_id)
    def cal = Calendar.getInstance()
    cal.setTime(fmonth)
    if (flizing.adate.getMonth()==cal.get(Calendar.MONTH)&&flizing.adate.getYear()+1900==cal.get(Calendar.YEAR)) qdays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)-flizing.adate.getDate()
    else if (flizing.enddate.getMonth()==cal.get(Calendar.MONTH)&&flizing.enddate.getYear()+1900==cal.get(Calendar.YEAR)) qdays = flizing.enddate.getDate()
    else qdays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
  }

  Finlizperiod setData(_request){
    summa = _request.summa?:0.0g
    compensation = _request.compensation?:0.0g
    procent = _request.procent?:0.0g
    body = _request.body?:0.0g
    returnsumma = _request.returnsumma?:0.0g
    this
  }

}