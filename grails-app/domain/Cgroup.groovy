class Cgroup {
  static mapping = { version false }

  Integer id
  String name

  Cgroup setData(_request){
    name = _request?.name?:''
    this
  }

}