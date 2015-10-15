class Complicense {
  static mapping = { version false }

  Integer id
  Integer company_id
  String name
  String nomer
  Date ldate
  Date validity
  String formnumber
  String authority
  String comment = ''
  Integer modstatus = 1

  Complicense csiSetModstatus(iStatus){
    modstatus = iStatus?:0
    this
  }

  Complicense setData(_request){
    name = _request.name
    nomer = _request.nomer
    ldate = _request.ldate
    validity = _request.validity
    formnumber = _request.formnumber
    authority = _request.authority
    comment = _request.comment?:''
    this
  }

}