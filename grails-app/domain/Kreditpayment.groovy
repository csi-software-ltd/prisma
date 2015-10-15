class Kreditpayment {
  static mapping = {
    version false
  }
  static constraints = {
    paiddate(nullable:true)
    percpaiddate(nullable:true)
  }

  Integer id
  Integer kredit_id
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
  Integer is_generate = 0

  Kreditpayment setData(_request,bdRate,_needRate){
    paydate = _request.kreditpayment_paydate
    summa = _request.summa?:0g
    summaperc = _request.summaperc?:0g
    summarub = _request.summarub?:summa
    summapercrub = _request.summapercrub?:summaperc
    if (_needRate) updateRate(bdRate)
    this
  }

  Kreditpayment setComputedData(_request){
    paydate = _request.paydate
    summa = _request.basesumma
    summaperc = _request.perssumma
    rate = _request.rate
    summarub = summa*rate
    summapercrub = summaperc*rate
    paidmonth = _request.paidmonths
    this
  }

  Kreditpayment fillfromPayrequest(Payrequest _request, bdRate, bdSumma){
    summarub = summarub?:bdSumma?:0
    summapercrub = summapercrub?:0
    summa = summarub/bdRate
    summaperc = summapercrub/bdRate
    rate = bdRate?:1g
    is_auto = id?is_auto:0
    is_generate = id?is_generate:1
    this
  }

  Kreditpayment fillfromPayrequestDop(Payrequest _request, bdRate, bdSumma){
    summarub = summarub?:0
    summapercrub = summapercrub?:bdSumma?:0
    summa = summarub/bdRate
    summaperc = summapercrub/bdRate
    rate = bdRate?:1g
    is_auto = id?is_auto:0
    is_generate = id?is_generate:1
    this
  }

  Kreditpayment computeModstatus(){
    if ((!summapercrub||percpaidstatus==2)&&(!summarub||paidstatus==2)) csiSetModstatus(2)
    else if (percpaidstatus==2||paidstatus==2) csiSetModstatus(1)
    else csiSetModstatus(0)
  }

  Kreditpayment csiSetModstatus(iStatus){
    modstatus = iStatus?:0
    this
  }

  Kreditpayment csiSetAdmin(iUser){
    admin_id = iUser?:0
    this
  }

  Kreditpayment updateRate(bdRate){
    rate = bdRate?:1g
    summarub = paidstatus?summarub:summa*rate
    summapercrub = percpaidstatus?summapercrub:summaperc*rate
    this
  }

  Kreditpayment updatepaiddata(bdSumma,iStatus,_isPerc,dDate){
    if(_isPerc){
      percpaid += bdSumma
      percpaidstatus = percpaid>0?iStatus:0
      percpaiddate = dDate
    } else {
      paid += bdSumma
      paidstatus = paid?iStatus:0
      paiddate = dDate
    }
    this
  }

  Kreditpayment generateBodyPayrequest(lUserId){
    if (paidstatus == 0) paidstatus = new Payrequest(paytype:1,paycat:1,agreementtype_id:3,agreement_id:kredit_id,agrpayment_id:id).csiSetKreditAgrData(Kredit.get(kredit_id)).setGeneralData(payrequest_paydate:paydate,summa:summarub,summands:0,destination:'Оплата тела кредита по договору').csiSetInitiator(lUserId).save(failOnError:true)?1:0
    this
  }

  Kreditpayment generatePercPayrequest(lUserId){
    if (percpaidstatus == 0) percpaidstatus = new Payrequest(paytype:1,paycat:1,agreementtype_id:3,agreement_id:kredit_id,agrpayment_id:id).csiSetKreditAgrData(Kredit.get(kredit_id)).setGeneralData(payrequest_paydate:paydate,summa:summapercrub,summands:0,destination:'Оплата процентов по договору').csiSetDop().csiSetInitiator(lUserId).save(failOnError:true)?1:0
    this
  }

}