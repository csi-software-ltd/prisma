class Enquiry {
  static mapping = { version false }
  static constraints = {
    startdate(nullable:true)
    enddate(nullable:true)
  }

  Integer id
  Integer company_id
  String nomer
  Integer whereto
  String bank_id
  String taxinspection_id
  Date inputdate = new Date()
  Date startdate
  Integer term
  Date termdate
  Date enddate
  Date ondate
  Integer modstatus = 0
  Integer enqtype_id
  Integer accounttype
  Integer valuta_id
  String endetails
  String comment
  Integer admin_id

  static def getBanks(){
    Enquiry.list().collect{it.bank_id}.unique().collect{Bank.get(it)}-null
  }

  static def getTaxinspections(){
    Enquiry.list().collect{it.taxinspection_id}.unique().collect{Taxinspection.get(it)}-null
  }

  Enquiry setData(_request){
    nomer = _request.nomer?:''
    bank_id = whereto==1?'':_request.bank_id
    taxinspection_id = whereto==2?'':Company.get(company_id)?.taxinspection_id
    term = Bank.get(bank_id)?.is_local!=0?Enqtype.get(enqtype_id)?.term:Enqtype.get(enqtype_id)?.longterm
    termdate = Tools.getNextWorkedDate(new Date(),term)
    ondate = _request.ondate
    accounttype = _request.accounttype?:0
    valuta_id = accounttype!=2?857:_request.valuta_id?:857
    endetails = _request.endetails?:''
    comment = _request.comment?:''
    this
  }

  Enquiry updateData(_request){
    nomer = _request.nomer?:''
    accounttype = _request.accounttype?:0
    valuta_id = accounttype!=2?857:_request.valuta_id?:857
    termdate = _request.termdate?:termdate
    enddate = _request.enddate?:enddate
    ondate = _request.ondate?:ondate
    endetails = _request.endetails?:''
    comment = _request.comment?:''
    this
  }

  Enquiry csiSetModstatus(iStatus){
    modstatus = iStatus?:0
    if (modstatus==1) startdate = startdate?:new Date()
    else if (modstatus==2||modstatus==-1) enddate = enddate?:new Date()
    this
  }

  Enquiry csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

}