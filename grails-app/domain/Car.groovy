class Car {
  static mapping = { version false }

  Integer id
  String name
  Integer modstatus = 1

  Car csiSetData(_request){
    name = _request.cname
    this
  }

  Car csiSetModstatus(iStatus){
    modstatus = iStatus?:0
    this
  }
}