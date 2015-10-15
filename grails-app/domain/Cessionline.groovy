class Cessionline {
  static mapping = {
    version false
  }

  Integer id
  Integer cession_id
  Date paydate
  BigDecimal summa
  Integer modstatus
  Long admin_id

  Cessionline setData(_request){
  	paydate = _request.planpayment_paydate
  	summa = _request.summa
  	this
  }

  Cessionline csiSetModstatus(iStatus){
		modstatus = iStatus==-1&&modstatus==1?1:iStatus?:0
		this
  }

  Cessionline csiSetAdmin(iUser){
  	admin_id = iUser?:0
  	this
  }

}