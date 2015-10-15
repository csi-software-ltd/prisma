class Loanline {
  static mapping = { version false }

  Integer id
  Integer loan_id
  Date paydate
  Long summa
  BigDecimal summarub
  BigDecimal rate = 1g
  Integer modstatus
  Long admin_id

  def afterInsert(){
    Loan.get(loan_id)?.increaseBodydebt(summa)?.save(failOnError:true)
  }

  Loanline setData(_request,bdRate,_needRate){
  	paydate = _request.planpayment_paydate
  	summa = _request.summa
    summarub = _request.summarub?:summa.toBigDecimal()
    if (_needRate) updateRate(bdRate)
  	this
  }

  Loanline csiSetModstatus(iStatus){
		modstatus = iStatus==-1&&modstatus==1?1:iStatus?:0
    if(isDirty('modstatus')&&modstatus==-1) Loan.get(loan_id)?.decreaseBodydebt(summa)?.save(failOnError:true)
		this
  }

  Loanline fillfromPayrequest(Payrequest _request, bdRate){
    summa = summa?:Math.round(summarub/bdRate)
    rate = bdRate?:1g
    this
  }

  Loanline csiSetAdmin(iUser){
  	admin_id = iUser?:0
  	this
  }

  Loanline updateRate(bdRate){
    rate = bdRate?:1g
    summarub = summa*rate
    this
  }

}