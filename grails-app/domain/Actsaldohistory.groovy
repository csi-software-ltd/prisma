class Actsaldohistory {
  static mapping = { version false }
  static constraints = {
    actsaldodate(nullable:true)
    saldodate(nullable:true)
    banksaldodate(nullable:true)
  }

  Integer id
  Integer bankaccount_id
  Date inputdate
  Integer actsaldo
  Date actsaldodate
  BigDecimal saldo
  Date saldodate
  Long banksaldo
  Date banksaldodate
////////////////////////////////////

	Actsaldohistory csiSetData(hsInrequest){

    bankaccount_id=hsInrequest?.id?:0
    inputdate=new Date()
    actsaldo=hsInrequest?.actsaldo?:0l
    actsaldodate=hsInrequest?.actsaldodate
    saldo=hsInrequest?.saldo?:0.0g
    saldodate=hsInrequest?.saldodate
    banksaldo=hsInrequest?.banksaldo?:0l
    banksaldodate=hsInrequest?.banksaldodate

		this
	}
}