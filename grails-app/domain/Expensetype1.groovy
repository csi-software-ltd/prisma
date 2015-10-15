class Expensetype1 {
  static mapping = { version false }

  Integer id
  String name

  def beforeUpdate(){
    if (isDirty('name')) Expensetype.withNewSession{ Expensetype.findAllByExpensetype1_id(id).each{ it.csisetRazdel(name).save(flush:true) } }
  }

  Expensetype1 setData(_request){
    name = _request.name
    this
  }
}