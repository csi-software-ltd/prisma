class Kreditline {
  static mapping = {
    version false
  }

  Integer id
  Integer kredit_id
  Date paydate
  BigDecimal summa
  BigDecimal summarub
  BigDecimal rate = 1g
  Integer modstatus
  Long admin_id

  Kreditline setData(_request,bdRate,_needRate){
  	paydate = _request.planpayment_paydate
  	summa = _request.summa
    summarub = _request.summarub?:summa
    if (_needRate) updateRate(bdRate)
  	this
  }

  Kreditline csiSetModstatus(iStatus){
		modstatus = iStatus==-1&&modstatus==1?1:iStatus?:0
		this
  }

  Kreditline fillfromPayrequest(Payrequest _request, bdRate){
    summa = summa?:(summarub/bdRate)
    rate = bdRate?:1g
    this
  }

  Kreditline csiSetAdmin(iUser){
  	admin_id = iUser?:0
  	this
  }

  Kreditline updateRate(bdRate){
    rate = bdRate?:1g
    summarub = summa*rate
    this
  }

}