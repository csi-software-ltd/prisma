class Visualgroup {
  static mapping = { version false }

  Integer id
  String name


  Visualgroup setData(_request){
    name = _request?.name?:''
    this
  }
}