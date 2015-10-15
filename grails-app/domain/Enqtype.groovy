class Enqtype {
  static mapping = { version false }

  Integer id
  String name
  Integer term
  Integer longterm
  Integer type

  Enqtype setData(_request){
		name = _request.tname
    term = _request.term
		type = _request.type
    longterm = type==1?0:_request.longterm
    this
  }
}