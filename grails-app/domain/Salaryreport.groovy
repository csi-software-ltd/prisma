class Salaryreport {
  static mapping = { version false }

  Integer id
  Integer month
  Integer year
  Date repdate
  BigDecimal summa = 0g
  Integer modstatus = 0
  Integer salarytype_id
  Integer department_id
  Date inputdate = new Date()
  Long file = 0
  Integer is_confirm = 0
  String comment = ''
  String commentdep = ''
  Long admin_id

  Salaryreport csiSetAdmin(iAdmin){
    admin_id = iAdmin?:0
    this
  }

  Salaryreport csiSetSumma(bdSumma){
    summa = bdSumma?:0g
    this
  }

  Salaryreport csiSetModstatus(iStatus){
    modstatus = iStatus?:modstatus
    this
  }

  Salaryreport csiSetZeroModstatus(){
    modstatus = 0
    this
  }

  Salaryreport csiSetConfirm(iStatus){
    is_confirm = iStatus?:0
    this
  }

  Salaryreport setData(_request){
    repdate = _request?.repdate?:new Date(year-1900,month,Tools.getIntVal(Dynconfig.findByName('salary.prepayment.paydate')?.value,1))
    comment = _request?.comment?:comment
    commentdep = _request?.commentdep?:commentdep
    this
  }

  Salaryreport csiSetFileId(iFileId){
    file = iFileId?:file
    this
  }

}