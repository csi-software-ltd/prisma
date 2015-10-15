class Tasktype {
  static mapping = { version false }

  Integer id
  String name

	Tasktype setData(_request){
		name = _request.tname
		this
	}
}