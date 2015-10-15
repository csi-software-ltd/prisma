class Taskevent {
  static mapping = { version false }

  Integer id
  Integer task_id
  String description
  Date inputdate
  Integer taskstatus
  Long executor
  Long remapper
  Long admin_id

  Taskevent setData(_prop){
    properties = _prop
    inputdate = new Date()
    this
  }
}