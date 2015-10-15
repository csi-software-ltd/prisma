class Zalogtype {
  static mapping = { version false }

  Integer id
  String name

  Zalogtype setData(_request){
    name = _request?.name?:''
    this
  }

}