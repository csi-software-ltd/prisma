class Licplanpayment {
  static mapping = {
    version false
  }

  Integer id
  Integer license_id
  Date paydate
  Integer summa
  Integer modstatus
  Long admin_id

  Licplanpayment setData(_request){
  	paydate = _request.planpayment_paydate
  	summa = _request.summa
  	this
  }

  Licplanpayment csiSetModstatus(iStatus){
		modstatus = iStatus==-1&&modstatus>0?modstatus:iStatus?:0
		this
  }

  Licplanpayment csiSetAdmin(iUser){
  	admin_id = iUser?:0
  	this
  }

  Licplanpayment generatePayrequest(){
    if (modstatus == 0) modstatus = new Payrequest(paytype:1,paycat:1,agreementtype_id:1,agreement_id:license_id).csiSetLicenseAgrData(License.get(license_id)).setGeneralData(payrequest_paydate:paydate,summa:summa.toBigDecimal(),summands:summa.toBigDecimal()*Tools.getIntVal(Dynconfig.findByName('payrequest.nds.value')?.value,18)/100,destination:'Оплата лицензии по договору').save(failOnError:true)?1:0
    this
  }

}