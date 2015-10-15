class Bankcheck {
  static mapping = { version false }

  Integer id
  Integer agr_id
  Integer agrtype_id
  String bank_id
  Date checkdate
  Integer checktype_id
  String contactinfo = ''
  String comment = ''

  static def getSpaceBanks(){
    Bankcheck.findAllByAgrtype_id(2).collect{it.bank_id}.unique().collect{ Bank.get(it) }.sort{ it.name }
  }

  Bankcheck setData(_request){
		bank_id = Bank.findByNameOrId(_request.bank,_request.bank)?.id
		checkdate = _request.checkdate
		checktype_id = _request.checktype_id
		contactinfo = _request.contactinfo?:''
		comment = _request.comment?:''
    this
  }
}