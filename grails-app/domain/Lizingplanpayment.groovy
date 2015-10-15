class Lizingplanpayment {
  static mapping = {
    version false
  }

  Integer id
  Integer lizing_id
  Date paydate
  BigDecimal summa
  Integer modstatus = 0
  Integer is_insurance = 0
  Long admin_id

  Lizingplanpayment setData(_request){
  	paydate = modstatus?paydate:_request.planpayment_paydate
  	summa = modstatus?summa:_request.summa
    is_insurance = modstatus?is_insurance:_request.is_insurance?:0
  	this
  }

  Lizingplanpayment csiSetModstatus(iStatus){
		modstatus = iStatus==-1&&modstatus>0?modstatus:iStatus?:0
		this
  }

  Lizingplanpayment csiSetAdmin(iUser){
  	admin_id = iUser?:0
  	this
  }

  Lizingplanpayment generatePayrequest(){
    if (modstatus == 0) modstatus = new Payrequest(paytype:1,paycat:1,agreementtype_id:4,agreement_id:lizing_id).csiSetLizingAgrData(Lizing.get(lizing_id)).setGeneralData(payrequest_paydate:paydate,summa:summa,summands:summa*Tools.getIntVal(Dynconfig.findByName('payrequest.nds.value')?.value,18)/100,destination:'Оплата лизинга по договору').save(failOnError:true)?1:0
    this
  }
}