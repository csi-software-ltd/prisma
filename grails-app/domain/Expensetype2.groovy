class Expensetype2 {
  static mapping = { version false }

  Integer id
  Integer expensetype1_id
  String name

  def beforeUpdate(){
    if (isDirty('name')) Expensetype.withNewSession{ Expensetype.findAllByExpensetype2_id(id).each{ it.csisetPodrazdel(name).save(flush:true) } }
  }

  Expensetype2 setData(_request){
    name = _request.name
    expensetype1_id = _request.expensetype1_id
    this
  }
}