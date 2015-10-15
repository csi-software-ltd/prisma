class Loanpayment {
  static mapping = { version false }
  static constraints = {
    paiddate(nullable:true)
    percpaiddate(nullable:true)
  }

  Integer id
  Integer loan_id
  Date paydate
  BigDecimal summa
  BigDecimal summaperc
  BigDecimal summarub
  BigDecimal summapercrub
  BigDecimal rate = 1g
  BigDecimal paid = 0g
  Date paiddate
  Integer paidstatus = 0
  BigDecimal percpaid = 0g
  Date percpaiddate
  Integer percpaidstatus = 0
  Integer modstatus = 0
  Integer paidmonth = 0
  Long admin_id
  Integer is_auto = 1

  Loanpayment setData(_request,bdRate,_needRate){
    paydate = _request.loanpayment_paydate
    summa = _request.summa?:0g
    summaperc = _request.summaperc?:0g
    summarub = _request.summarub?:summa
    summapercrub = _request.summapercrub?:summaperc
    if (_needRate) updateRate(bdRate)
    this
  }

  Loanpayment setComputedData(_request){
    paydate = _request.paydate
    summa = _request.basesumma
    summaperc = _request.perssumma
    rate = _request.rate
    summarub = summa*rate
    summapercrub = summaperc*rate
    paidmonth = _request.paidmonths
    this
  }

  Loanpayment fillfromPayrequest(Payrequest _request, bdRate){
    summa = summa?:summarub/bdRate
    summaperc = summaperc?:0
    rate = bdRate?:1g
    summapercrub = summapercrub?:0
    paidmonth = paidmonth?:0
    is_auto = 0
    this
  }

  Loanpayment computeModstatus(){
    if ((!summapercrub||percpaidstatus==2)&&(!summarub||paidstatus==2)) csiSetModstatus(2)
    else if (percpaidstatus==2||paidstatus==2) csiSetModstatus(1)
    else csiSetModstatus(0)
  }

  Loanpayment csiSetModstatus(iStatus){
    modstatus = iStatus?:0
    this
  }

  Loanpayment csiSetAdmin(iUser){
    admin_id = iUser?:0
    this
  }

  Loanpayment updateRate(bdRate){
    rate = bdRate?:1g
    summarub = paidstatus?summarub:summa*rate
    summapercrub = percpaidstatus?summapercrub:summaperc*rate
    this
  }

  Loanpayment updatepaiddata(bdSumma,iStatus,_isPerc,dDate){
    if(_isPerc){
      percpaid += bdSumma
      percpaidstatus = iStatus
      percpaiddate = dDate
    } else {
      paid += bdSumma
      paidstatus = iStatus
      paiddate = dDate
    }
    this
  }
}